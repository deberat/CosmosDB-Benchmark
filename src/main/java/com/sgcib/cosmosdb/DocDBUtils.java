package com.sgcib.cosmosdb;

import com.microsoft.azure.cosmosdb.*;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;

public class DocDBUtils {

    private DocDBUtils() {
    }

    public static Database getDatabase(AsyncDocumentClient client, String databaseId) {
        FeedResponse<Database> feedResponsePages = client
                .queryDatabases(new SqlQuerySpec("SELECT * FROM root r WHERE r.id=@id",
                        new SqlParameterCollection(new SqlParameter("@id", databaseId))), null)
                .toBlocking().single();

        if (feedResponsePages.getResults().isEmpty()) {
            throw new RuntimeException("cannot find datatbase " + databaseId);
        }
        return feedResponsePages.getResults().get(0);
    }

    public static DocumentCollection getCollection(AsyncDocumentClient client, String databaseLink,
                                            String collectionId) {
        FeedResponse<DocumentCollection> feedResponsePages = client
                .queryCollections(databaseLink,
                        new SqlQuerySpec("SELECT * FROM root r WHERE r.id=@id",
                                new SqlParameterCollection(new SqlParameter("@id", collectionId))),
                        null)
                .toBlocking().single();

        if (feedResponsePages.getResults().isEmpty()) {
            throw new RuntimeException("cannot find collection " + collectionId);
        }
        return feedResponsePages.getResults().get(0);
    }
}
