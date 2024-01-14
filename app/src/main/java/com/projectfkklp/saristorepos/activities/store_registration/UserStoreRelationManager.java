package com.projectfkklp.saristorepos.activities.store_registration;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projectfkklp.saristorepos.models.UserStoreRelation;

public class UserStoreRelationManager {

    public static void save(UserStoreRelation userStoreRelation) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user_store_relations").child(userStoreRelation.getUserId()).setValue(userStoreRelation);
    }
}
