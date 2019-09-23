{
    "indexingMode": "none",
    "automatic": false,
    "includedPaths": [],
    "excludedPaths": []
}

export INSTANCE_URL=https://duyhaibenchcosmosdev.documents.azure.com:443
export DATABASE=benchmarkdb
export COLLECTION=Insert1kb5kRPS
export DURATION_IN_MINUTES=10
export STATS_INTERVAL_IN_SECS=10
export DISTINCT_DOCS_COUNT=10000
export INJECTOR_COUNT=1
export CONCURRENCY=27
export MASTERKEY=

java -Xms20G -Xmx20G -Xmn15G -XX:+CMSParallelRemarkEnabled -XX:SurvivorRatio=8 -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:+UseTLAB -XX:+CMSParallelInitialMarkEnabled -XX:+CMSEdenChunksRecordAlways  -XX:+UseCondCardMark -jar  benchmarkRunner-1.jar


java -XX:+UseG1GC -XX:G1RSetUpdatingPauseTimePercent=5 -XX:MaxGCPauseMillis=1000 -XX:ParallelGCThreads=16 -XX:ConcGCThreads=16 -XX:InitiatingHeapOccupancyPercent=70 -Xms100G -Xmx100G -jar  benchmarkRunner-1.jar

java -Xms5G -Xmx5G -Xmn2G -XX:+UseParallelOldGC -XX:ParallelGCThreads=16 -XX:MaxGCPauseMillis=10 -XX:SurvivorRatio=8 -XX:+UseTLAB -jar  benchmarkRunner-1.jar