package com.projectfkklp.saristorepos.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfkklp.saristorepos.enums.SignInMethod;
import com.projectfkklp.saristorepos.interfaces.OnUserRetrieve;
import com.projectfkklp.saristorepos.models.User;

import java.util.List;

public class UserRepository {
    public static CollectionReference getCollectionReference() {
        return FirebaseFirestore.getInstance()
            .collection("users");
    }

    public static  void getSignedInUser(SignInMethod signInMethod, OnUserRetrieve callback){
        String userUid = AuthenticationRepository.getCurrentUserUid();

        getCollectionReference()
            .limit(1)
            .whereEqualTo(signInMethod.uidField, userUid)
            .get().addOnSuccessListener(task->{
                List<User> users= task.toObjects(User.class);
                User user = !users.isEmpty() ? users.get(0) : null;
                callback.onUserRetrieved(user);
            });
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

