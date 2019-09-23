package com.sgcib.cosmosdb;

import com.microsoft.azure.cosmosdb.ConsistencyLevel;
import lombok.Data;
import picocli.CommandLine;

import java.util.Optional;

import static java.lang.System.getenv;

@CommandLine.Command(name = "benchmark",
        sortOptions = false,
        headerHeading = "@|bold,underline Usage|@:%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description|@:%n%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n",
        header = "Execute benchmark on CosmosDB",
        description = "Execute benchmark on CosmosDB"
)
@Data
public class BenchmarkParameters {

    private static final String INSTANCE_URL = "INSTANCE_URL";
    private static final String MASTERKEY = "MASTERKEY";
    private static final String DATABASE = "DATABASE";
    private static final String COLLECTION = "COLLECTION";
    private static final String DOC_SIZE_IN_KB = "DOC_SIZE_IN_KB";
    private static final String CONSISTENCY = "CONSISTENCY";
    private static final String CONCURRENCY = "CONCURRENCY";
    private static final String DURATION_IN_MINUTES = "DURATION_IN_MINUTES";
    private static final String STATS_INTERVAL_IN_SECS = "STATS_INTERVAL_IN_SECS";
    private static final String DISTINCT_DOCS_COUNT = "DISTINCT_DOCS_COUNT";
    private static final String INJECTOR_COUNT = "INJECTOR_COUNT";

    private static final String DEFAULT_DOC_SIZE_IN_KB = "1";
    private static final String DEFAULT_CONSISTENCY_LEVEL = "Session";
    private static final String DEFAULT_CONCURRENCY = "10";
    private static final String DEFAULT_DURATION_IN_MINUTES = "5";
    private static final String DEFAULT_STATS_INTERVAL_IN_SECS = "5";
    private static final String DEFAULT_DISTINCT_DOCS_COUNT = "1000000";
    private static final String DEFAULT_INJECTOR_COUNT = "1";

    @CommandLine.Option(names = {"--url"}, description = "instance URL. " +
            "Ex: https://mycosmosdb.documents.azure.com:443. " +
            "We also read this value from env variable " + INSTANCE_URL)
    private String instanceUrl= getenv(INSTANCE_URL);

    @CommandLine.Option(names = {"--masterKey"}, description = "instance URL. " +
            "Ex: sdf7sdfhze834Ugfjsdgf=." +
            "We also read this value from env variable " + MASTERKEY)
    private String masterKey= getenv(MASTERKEY);


    @CommandLine.Option(names = {"--database"}, description = "database name. " +
            "Ex: my_cosmos_db." +
            "We also read this value from env variable " + DATABASE)
    private String database= getenv(DATABASE);

    @CommandLine.Option(names = {"--collection"}, description = "collection name. " +
            "Ex: my_collection." +
            "We also read this value from env variable " + COLLECTION)
    private String collection= getenv(COLLECTION);

    @CommandLine.Option(names = {"--docSizeInKb"}, description = "document size in kb. " +
            "Ex: 10 for a 10kb document." +
            "Default = " + DEFAULT_DOC_SIZE_IN_KB + "." +
            "We also read this value from env variable " + DOC_SIZE_IN_KB)
    private String docSizeInKb= Optional.ofNullable(getenv(DOC_SIZE_IN_KB)).orElse(DEFAULT_DOC_SIZE_IN_KB);

    @CommandLine.Option(names = {"--consistency"}, description = "consistency level. " +
            "Ex: Eventual." +
            "Default " + DEFAULT_CONSISTENCY_LEVEL + "." +
            "We also read this value from env variable " + CONSISTENCY)
    private String consistency= Optional.ofNullable(getenv(CONSISTENCY)).orElse(DEFAULT_CONSISTENCY_LEVEL);

    @CommandLine.Option(names = {"--concurrency"}, description = "concurrency level. " +
            "Ex: 50." +
            "Default = " + DEFAULT_CONCURRENCY + "." +
            "We also read this value from env variable " + CONCURRENCY)
    private String concurrency= Optional.ofNullable(getenv(CONCURRENCY)).orElse(DEFAULT_CONCURRENCY);

    @CommandLine.Option(names = {"--durationInMinutes"}, description = "test duration in minutes. " +
            "Ex: 5." +
            "Default = " + DEFAULT_DURATION_IN_MINUTES + "." +
            "We also read this value from env variable " + DURATION_IN_MINUTES)
    private String durationInMinutes= Optional.ofNullable(getenv(DURATION_IN_MINUTES)).orElse(DEFAULT_DURATION_IN_MINUTES);

    @CommandLine.Option(names = {"--statsIntervalInSecs"}, description = "print statistics every x seconds. " +
            "Ex: 5." +
            "Default = " + DEFAULT_STATS_INTERVAL_IN_SECS + "." +
            "We also read this value from env variable " + STATS_INTERVAL_IN_SECS)
    private String statsIntervalInSecs= Optional.ofNullable(getenv(STATS_INTERVAL_IN_SECS)).orElse(DEFAULT_STATS_INTERVAL_IN_SECS);

    @CommandLine.Option(names = {"--distinctDocsCount"}, description = "distinct documents to pre-generate. " +
            "Ex: 1000000." +
            "Default = " + DEFAULT_DISTINCT_DOCS_COUNT + "." +
            "We also read this value from env variable " + DISTINCT_DOCS_COUNT)
    private String distinctDocsCount= Optional.ofNullable(getenv(DISTINCT_DOCS_COUNT)).orElse(DEFAULT_DISTINCT_DOCS_COUNT);

    @CommandLine.Option(names = {"--injectorCount"}, description = "number of injector/for loops for injection. " +
            "Ex: 1." +
            "Default = " + DEFAULT_INJECTOR_COUNT + "." +
            "We also read this value from env variable " + INJECTOR_COUNT)
    private String injectorCount= Optional.ofNullable(getenv(INJECTOR_COUNT)).orElse(DEFAULT_INJECTOR_COUNT);


    public ConsistencyLevel consistencyLevel() {
        return ConsistencyLevel.valueOf(consistency);
    }

    public int concurrencyLevel() {
        return Integer.parseInt(concurrency);
    }

    public int runtimeInMinutes() {
        return Integer.parseInt(durationInMinutes);
    }

    public int docSizeInKb() {
        return Integer.parseInt(docSizeInKb);
    }

    public int statsIntervalInSecs() {
        return Integer.parseInt(statsIntervalInSecs);
    }

    public int distinctDocsCount() {
        return Integer.parseInt(distinctDocsCount);
    }

    public int injectorCount() {
        return Integer.parseInt(injectorCount);
    }

    @Override
    public String toString() {
        return "\n\nBenchmarkParameters{" +
                "\n\tinstanceUrl='" + instanceUrl + '\'' +
                "\n\t, masterKey='" + masterKey + '\'' +
                "\n\t, database='" + database + '\'' +
                "\n\t, collection='" + collection + '\'' +
                "\n\t, docSizeInKb='" + docSizeInKb + '\'' +
                "\n\t, consistency='" + consistency + '\'' +
                "\n\t, concurrency='" + concurrency + '\'' +
                "\n\t, durationInMinutes='" + durationInMinutes + '\'' +
                "\n\t, statsIntervalInSecs='" + statsIntervalInSecs + '\'' +
                "\n\t, distinctDocsCount='" + distinctDocsCount + '\'' +
                "\n\t, injectorCount='" + injectorCount + '\'' +
                "\n\t}\n";
    }
}
