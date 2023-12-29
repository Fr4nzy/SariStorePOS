package com.projectfkklp.saristorepos.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfkklp.saristorepos.interfaces.OnUserRetrieve;
import com.projectfkklp.saristorepos.models.User;

public class UserRepository {
    public static CollectionReference getCollectionReference() {
        return FirebaseFirestore.getInstance()
                .collection("users");
    }

    public static void getCurrentUser(OnUserRetrieve callback){
        getCollectionReference()
            .document(AuthenticationRepository.getCurrentUserUid())
            .get()
            .addOnCompleteListener(task -> {
                DocumentSnapshot document = task.getResult();
                callback.onUserRetrieved(document.toObject(User.class));
            });
    }
}

