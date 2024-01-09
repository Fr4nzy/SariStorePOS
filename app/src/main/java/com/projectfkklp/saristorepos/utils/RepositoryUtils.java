package com.projectfkklp.saristorepos.utils;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RepositoryUtils {

    public static Filter match(String filter, String searchText) {
        return Filter.and(
            Filter.greaterThanOrEqualTo(filter, searchText),
            Filter.lessThanOrEqualTo(filter, searchText+'\uf8ff')
        );
    }

    public static List<DocumentSnapshot> mergeResults(List<Object> results) {
        List<DocumentSnapshot> mergedResults = new ArrayList<>();
        Set<String> uniqueDocumentIds = new HashSet<>();

        // Merge the results from each query
        for (Object result : results) {
            if (result instanceof QuerySnapshot) {
                QuerySnapshot querySnapshot = (QuerySnapshot) result;

                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    // Check if the document ID is unique
                    String documentId = document.getId();
                    if (!uniqueDocumentIds.contains(documentId)) {
                        mergedResults.add(document);
                        uniqueDocumentIds.add(documentId);
                    }
                }
            }
        }

        return mergedResults;
    }

}
