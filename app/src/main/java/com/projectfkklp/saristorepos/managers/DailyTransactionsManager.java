package com.projectfkklp.saristorepos.managers;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.projectfkklp.saristorepos.models.DailyTransactions;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.repositories.DailyTransactionsRepository;
import com.projectfkklp.saristorepos.repositories.SessionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class DailyTransactionsManager {
    public static CollectionReference getCollectionReference(Context context) {
        return  StoreManager.getCollectionReference()
            .document(SessionRepository.getCurrentStore(context).getId())
            .collection("daily_transactions");
    }

    public static Task<Task<Void>> addTransaction(Context context, Transaction transaction){
        String date = LocalDate.now().toString();
        DocumentReference dailyTransactionsDocument = DailyTransactionsRepository
            .getDocument(context, date);

        return dailyTransactionsDocument
            .get()
            .continueWith(task->{
                DailyTransactions todayDailyTransactions = task.getResult().toObject(DailyTransactions.class);

                if (todayDailyTransactions==null){
                    // For first transaction of the day
                    todayDailyTransactions = new DailyTransactions(date);
                }

                todayDailyTransactions.getTransactions().add(transaction);

                return dailyTransactionsDocument.set(todayDailyTransactions);
            })
            .addOnFailureListener(task->{
                task.printStackTrace();
            })
        ;
    }
}
