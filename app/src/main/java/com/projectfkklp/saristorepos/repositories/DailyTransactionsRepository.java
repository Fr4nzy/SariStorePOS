package com.projectfkklp.saristorepos.repositories;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.projectfkklp.saristorepos.managers.StoreManager;

import java.time.LocalDate;
import java.util.Date;

public class DailyTransactionsRepository {
    public static CollectionReference getCollectionReference(Context context) {
        return  StoreManager.getCollectionReference()
            .document(SessionRepository.getCurrentStore(context).getId())
            .collection("daily_transactions");
    }

    public static DocumentReference getDocument(Context context, String date){
        return getCollectionReference(context).document(date);
    }

    public static Task<DocumentSnapshot> getDailyTransactions(Context context, LocalDate date){
        return getCollectionReference(context).document(date.toString()).get();
    }
}
