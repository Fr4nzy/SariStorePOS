package com.projectfkklp.saristorepos.managers;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfkklp.saristorepos.enums.UserRole;
import com.projectfkklp.saristorepos.enums.UserStatus;
import com.projectfkklp.saristorepos.interfaces.OnSetFirebaseDocument;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.UserStoreRelation;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.utils.ModelUtils;
import com.projectfkklp.saristorepos.validators.UserStoreRelationValidator;

public class UserStoreRelationManager {
    public static CollectionReference getCollectionReference() {
        return FirebaseFirestore.getInstance()
            .collection("user_store_relations");
    }

    public static void request(Context context, Store store, UserRole userRole, OnSetFirebaseDocument onSetFirebaseDocument){
        String documentId = ModelUtils.createUUID();
        UserStoreRelation userStoreRelation = new UserStoreRelation(
            SessionRepository.getCurrentUser(context).getId(),
            store.getId(),
            userRole,
            UserStatus.REQUESTED
        );

        UserStoreRelationValidator.validate(context, userStoreRelation, validationStatus -> {
            if (validationStatus.isValid()) {
                getCollectionReference()
                    .document(documentId)
                    .set(userStoreRelation)
                    .addOnSuccessListener(onSetFirebaseDocument::onSuccess)
                    .addOnFailureListener(onSetFirebaseDocument::onFailed)
                    .addOnCompleteListener(onSetFirebaseDocument::onComplete);
            }
            else {
                onSetFirebaseDocument.onInvalid(validationStatus);
            }
        });
    }
}
