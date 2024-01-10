package com.projectfkklp.saristorepos.repositories;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectfkklp.saristorepos.utils.RepositoryUtils;

import java.util.List;

public class StoreRepository {
    public static CollectionReference getCollectionReference() {
        return FirebaseFirestore.getInstance()
                .collection("stores");
    }

    public static Task<List<Object>> searchStores(String searchText, List<String> associatedStoreIds){
        CollectionReference collectionRef = getCollectionReference();

        Task<QuerySnapshot> nameTask = collectionRef
            .where(RepositoryUtils.match("name", searchText))
            .get();
        Task<QuerySnapshot> addressTask = collectionRef
            .where(RepositoryUtils.match("address", searchText))
            .get();
        Task<QuerySnapshot> idTask = collectionRef
            .where(RepositoryUtils.match("id", searchText))
            .get();

        Task<QuerySnapshot> associatedStoresTask;
        if (!associatedStoreIds.isEmpty()) {
            associatedStoresTask = collectionRef
                .where(Filter.inArray("id", associatedStoreIds))
                .get();
        } else {
            // Handle the case when associatedStoreIds is empty
            associatedStoresTask = Tasks.forResult(null);
        }

        return Tasks.whenAllSuccess(nameTask, addressTask, idTask, associatedStoresTask);
    }

    public static Task<QuerySnapshot> getStoresByIds(List<String> storeIds){
        return storeIds.isEmpty()
            ? Tasks.forResult(null)
            : getCollectionReference()
                .whereIn("id", storeIds)
                .get();
    }

}


