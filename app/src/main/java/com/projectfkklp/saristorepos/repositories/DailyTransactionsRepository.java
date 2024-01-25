package com.projectfkklp.saristorepos.repositories;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectfkklp.saristorepos.managers.StoreManager;

import java.time.LocalDate;

public class DailyTransactionsRepository {
    public static final int PAGINATION_LIMIT = 30;
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

    public static Task<QuerySnapshot> getDailyTransactions(Context context, int page){
        LocalDate currentDate = LocalDate.now();
        String lowerDate = currentDate.plusDays(1 - (long) PAGINATION_LIMIT * (page+1) ).toString();
        String upperDate = currentDate.plusDays(-((long) PAGINATION_LIMIT *page) ).toString();

        return getCollectionReference(context)
            .orderBy("date", Query.Direction.DESCENDING)  // Order by date in descending order
            .whereGreaterThanOrEqualTo("date",lowerDate)
            .whereLessThanOrEqualTo("date", upperDate)
            .get()
        ;
    }

    public static Task<QuerySnapshot> getFirstDailyTransactions(Context context){
        return getCollectionReference(context)
            .orderBy("date", Query.Direction.ASCENDING)
            .limit(1)
            .get();
    }
}
