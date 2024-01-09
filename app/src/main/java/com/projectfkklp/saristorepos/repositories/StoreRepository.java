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

        Task<QuerySnapshot> nameQuery = collectionRef
                .where(RepositoryUtils.match("name", searchText))
                .get();
        Task<QuerySnapshot> addressQuery = collectionRef
                .where(RepositoryUtils.match("address", searchText))
                .get();
        Task<QuerySnapshot> idQuery = collectionRef
                .where(RepositoryUtils.match("id", searchText))
                .get();
        Task<QuerySnapshot> associatedStores = collectionRef
                .where(Filter.inArray("id", associatedStoreIds))
                .get();

        return Tasks.whenAllSuccess(nameQuery, addressQuery, idQuery, associatedStores);
    }

}


