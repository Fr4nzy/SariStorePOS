package com.projectfkklp.saristorepos.managers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.interfaces.OnStoreSave;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.utils.ModelUtils;
import com.projectfkklp.saristorepos.validators.StoreValidator;

import java.util.ArrayList;
import java.util.List;

public class StoreManager {

    public static CollectionReference getCollectionReference() {
        return  FirebaseFirestore.getInstance().collection("stores");
    }

    public static void saveStore(Store store, OnStoreSave onStoreSave){
        ValidationStatus validationStatus = StoreValidator.validate(store);
        Task<Void> task = null;

        if (validationStatus.isValid()) {
            task = getCollectionReference().document(store.getId()).set(store);
        }

        onStoreSave.onStoreSave(store, validationStatus, task);
    }

    public static void saveStoreWithDummyProducts(Store store){
        List<Product> products = store.getProducts();
        products.clear();
        for (int i=0;i<100;i++){
            products.add(new Product(
                ModelUtils.createUUID(),
                (i < 50? "Test Product ": "Sample Product ")+i,
                i%20,
                (i+1)*10,
                "",
                ""
            ));
        }
        getCollectionReference().document(store.getId()).set(store);
    }

    public static Task<Void> save(Store store){
        return getCollectionReference().document(store.getId()).set(store);
    }
}
