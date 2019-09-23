package com.sgcib.cosmosdb;

import ch.qos.logback.classic.Level;
import com.codahale.metrics.*;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.common.util.concurrent.MoreExecutors;
import com.microsoft.azure.cosmosdb.*;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import it.unimi.dsi.util.SplitMix64RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import ch.qos.logback.classic.Logger;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BenchmarkRunner {
    private final int DISTINCT_PARTITIONS_COUNT = 100000000;
    private final int DISTINCT_ROWS_PER_PARTITION = 1000000;

    private final MetricRegistry metricsRegistry = new MetricRegistry();
    private final Meter successMeter = metricsRegistry.meter("#Successful Operations");
    private final Meter failureMeter = metricsRegistry.meter("#Failed Operations");
    private final Timer latency = metricsRegistry.timer("Latency");
    private final ScheduledReporter reporter = ConsoleReporter.forRegistry(metricsRegistry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build();

    private final AtomicDouble requestCharge = new AtomicDouble(0D);
    private final SplitMix64RandomGenerator randomGenerator = new SplitMix64RandomGenerator();
    private final List<String> payload;

    private final AsyncDocumentClient client;
    private final Scheduler scheduler;
    private final BenchmarkParameters parameters;
    private final String collectionLink;
    private final String partitionKeyPath;
    private final Semaphore concurrencyControlSemaphore;

    public BenchmarkRunner(BenchmarkParameters parameters) {
        this.parameters = parameters;
        this.client = ClientFactory.asyncClient(parameters);
        Database database = DocDBUtils.getDatabase(client, parameters.getDatabase());
        DocumentCollection collection = DocDBUtils.getCollection(client, database.getSelfLink(), parameters.getCollection());
        this.collectionLink = String.format("dbs/%s/colls/%s", database.getId(), collection.getId());
        this.partitionKeyPath = collection.getPartitionKey().getPaths().iterator().next().split("/")[1];
        this.scheduler = Schedulers.computation();
        this.concurrencyControlSemaphore = new Semaphore(parameters.concurrencyLevel());
        this.payload = new ArrayList<>(parameters.distinctDocsCount());
    }

    public static void main(String ... args) throws Exception {

        Logger nettyLogger = (Logger) LoggerFactory.getLogger("io.netty");
        nettyLogger.setLevel(Level.OFF);

        BenchmarkParameters parameters = new BenchmarkParameters();
        CommandLine commandLine = new CommandLine(parameters);

        List<CommandLine> parsed = new ArrayList<>();

        try {
            parsed = commandLine.parse(args);
        } catch (CommandLine.ParameterException e) {
            log.error("Wrong arguments : {}", e.getValue());
            CommandLine.usage(parameters, System.out);
            System.exit(-1);
        }

        parameters = parsed.get(0).getCommand();

        log.info("Parameters: {}", parameters.toString());

        BenchmarkRunner benchmarkRunner = new BenchmarkRunner(parameters);
        benchmarkRunner.preGeneratePayload();
        benchmarkRunner.execute();
        System.exit(0);
    }

    public void preGeneratePayload() {
        log.info(" ----------------- Start generating {} payloads -----------------", parameters.distinctDocsCount());
        for (int i = 0; i < parameters.distinctDocsCount(); i++) {
            payload.add(RandomStringUtils.randomAlphanumeric(parameters.docSizeInKb() * 1000));
        }
    }


    public void execute() throws Exception {
        log.info("Start benchmark during {} minute(s)", parameters.getDurationInMinutes());
        long startTime = System.currentTimeMillis();
        final Histogram histogram = new ConcurrentHistogram(TimeUnit.MINUTES.toNanos(3), 2);
        reporter.start(parameters.statsIntervalInSecs(), TimeUnit.SECONDS);


        try {
            long now;
            long RUNTIME_IN_MILLIS = parameters.runtimeInMinutes() * 60 * 1000;

            do {
                Document document = generateDocument(partitionKeyPath);

                this.concurrencyControlSemaphore.acquire();
                final long startInNanos = System.nanoTime();

                RequestOptions options = new RequestOptions();
                AccessCondition accessCondition = new AccessCondition();
                accessCondition.setType(AccessConditionType.IfMatch);
                accessCondition.setCondition("version eq");
                options.setAccessCondition(accessCondition);
                Observable<ResourceResponse<Document>> obs = client.upsertDocument(
                        collectionLink,
                        document,
                        null,
                        false);

                LatencySubscriber subs = new LatencySubscriber(latency.time(),
                        concurrencyControlSemaphore,
                        histogram,
                        startInNanos);

                obs.subscribeOn(scheduler)
                        .subscribe(subs);
                now = System.currentTimeMillis();
            } while (now - startTime <= RUNTIME_IN_MILLIS);


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        log.info("\n\n Computing statistics ... \n\n");
        Thread.sleep(5000);

        long count = successMeter.getCount() + failureMeter.getCount();
        long now = System.currentTimeMillis();
        log.info("----------------- End test ----------------------");
        long durationInSecs = (now - startTime) / 1000;
        long actualRUsPerSec = new Double(requestCharge.get()/durationInSecs).longValue();
        log.info("\tDuration in seconds    = " + durationInSecs);
        log.info("\tNb of requests         = " + count);
        log.info("\tNb of requests/sec avg = " + (count/(parameters.runtimeInMinutes() * 60)));
        log.info("\tNb of OK               = " + successMeter.getCount());
        log.info("\tNb of KO               = " + failureMeter.getCount());
        log.info("\tActual consumed RUs    = " + new Double(requestCharge.get()).longValue());
        log.info("\tActual RUs/s           = " + actualRUsPerSec);

        Histogram newHistogram = histogram.copy();

        log.info("----------- General statistics --------------");
        log.info("\tMin latency in ms     = " + (newHistogram.getMinNonZeroValue()/1000000));
        log.info("\tMax latency in ms     = " + (newHistogram.getMaxValue()/1000000));
        log.info("\tAverage latency in ms = " + (newHistogram.getMean()/1000000));
        log.info("\tStd deviation in ms   = " + (newHistogram.getStdDeviation()/1000000));
        log.info("--------------- Percentile ------------------");
        log.info("\n");
        newHistogram.outputPercentileDistribution(System.out, 1000000.0);

        reporter.close();
        client.close();
    }

    private Document generateDocument(String partitionKeyPath) {
        String partition = randomGenerator.nextInt(DISTINCT_PARTITIONS_COUNT) + "";
        String row = randomGenerator.nextInt(DISTINCT_ROWS_PER_PARTITION) + "";
        int payloadIndex = randomGenerator.nextInt(parameters.distinctDocsCount());
        String payloadString = payload.get(payloadIndex);
        Document doc = new Document();
        doc.set(partitionKeyPath, partition);
        doc.setId(row);
        doc.set("value", payloadString);
        return doc;

    }

    private class LatencySubscriber extends Subscriber<ResourceResponse<Document>> {

        private final Timer.Context context;
        private final Semaphore semaphore;
        private final Histogram histogram;
        private final long startTimeInNanos;

        private LatencySubscriber(Timer.Context context,
                                  Semaphore semaphore,
                                  Histogram histogram,
                                  long startTimeInNanos) {
            this.context = context;
            this.semaphore = semaphore;
            this.histogram = histogram;
            this.startTimeInNanos = startTimeInNanos;
        }

        @Override
        public void onCompleted() {
            context.stop();
            successMeter.mark();
            long endInNanos = System.nanoTime();
            histogram.recordValue(endInNanos-startTimeInNanos);
            semaphore.release();
        }

        @Override
        public void onError(Throwable e) {
            context.stop();
            failureMeter.mark();
            semaphore.release();
        }

        @Override
        public void onNext(ResourceResponse<Document> documentResourceResponse) {
            requestCharge.getAndAdd(documentResourceResponse.getRequestCharge());
        }
    }
}
