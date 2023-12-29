package com.projectfkklp.saristorepos.managers;

import com.google.firebase.firestore.CollectionReference;

import kotlin.NotImplementedError;

public class BaseManager {
    public static CollectionReference getCollectionReference(){
        throw new NotImplementedError();
    }
}