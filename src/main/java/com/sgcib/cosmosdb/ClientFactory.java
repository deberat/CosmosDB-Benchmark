package com.sgcib.cosmosdb;


import com.microsoft.azure.cosmosdb.ConnectionMode;
import com.microsoft.azure.cosmosdb.ConnectionPolicy;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;

public class ClientFactory {

    public static AsyncDocumentClient asyncClient(BenchmarkParameters parameters) {
        ConnectionPolicy policy = new ConnectionPolicy();
        policy.setConnectionMode(ConnectionMode.Direct);
        policy.setMaxPoolSize(5000);

        AsyncDocumentClient asyncClient = new AsyncDocumentClient.Builder()
                .withServiceEndpoint(parameters.getInstanceUrl())
                .withMasterKeyOrResourceToken(parameters.getMasterKey())
                .withConnectionPolicy(policy)
                .withConsistencyLevel(parameters.consistencyLevel())
                .build();

        return asyncClient;
    }
}
