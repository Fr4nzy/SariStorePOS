package com.projectfkklp.saristorepos.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class UserStoreRelationRepository {
    public static CollectionReference getCollectionReference() {
        return FirebaseFirestore.getInstance()
                .collection("user_store_relations");
    }

    public static Task<QuerySnapshot> getRelationsByUserId(String userId) {
        return getCollectionReference()
            .whereEqualTo("userId", userId)
            .get();
    }

    public static Task<QuerySnapshot> getRelationsByStoreId(String storeId) {
        return getCollectionReference()
            .whereEqualTo("storeId", storeId)
            .get();
    }

}
