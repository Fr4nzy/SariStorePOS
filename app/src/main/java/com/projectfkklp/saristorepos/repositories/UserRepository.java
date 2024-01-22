package com.projectfkklp.saristorepos.repositories;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectfkklp.saristorepos.enums.AuthenticationProvider;
import com.projectfkklp.saristorepos.interfaces.OnUserRetrieve;
import com.projectfkklp.saristorepos.models.User;

import java.util.List;

public class UserRepository {
    public static CollectionReference getCollectionReference() {
        return FirebaseFirestore.getInstance()
            .collection("users");
    }

    public static void getUserByAuthentication(AuthenticationProvider authenticationProvider,String authenticationUid, OnUserRetrieve callback){
        getCollectionReference()
            .limit(1)
            .whereEqualTo(authenticationProvider.key, authenticationUid)
            .get().addOnSuccessListener(task->{
                List<User> users= task.toObjects(User.class);
                User user = !users.isEmpty() ? users.get(0) : null;
                callback.onUserRetrieved(user);
            });
    }

    public static void getUserByCurrentAuthentication(OnUserRetrieve callback){
        getCollectionReference()
            .document(AuthenticationRepository.getCurrentAuthenticationUid())
            .get()
            .addOnCompleteListener(task -> {
                DocumentSnapshot document = task.getResult();
                callback.onUserRetrieved(document.toObject(User.class));
            });
    }

    public static Task<QuerySnapshot> getUsersByIds(List<String> userIds){
        return userIds.isEmpty()
            ? Tasks.forResult(null)
            : getCollectionReference()
            .whereIn("id", userIds)
            .get();
    }
}

