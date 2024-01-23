package com.projectfkklp.saristorepos.managers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfkklp.saristorepos.classes.ValidationStatus;
import com.projectfkklp.saristorepos.interfaces.OnStoreSave;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.validators.StoreValidator;

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
}
