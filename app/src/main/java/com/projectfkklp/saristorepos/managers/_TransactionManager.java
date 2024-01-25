package com.projectfkklp.saristorepos.managers;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.projectfkklp.saristorepos.interfaces.OnTransactionAdded;
import com.projectfkklp.saristorepos.models._Transaction;
import com.projectfkklp.saristorepos.repositories.AuthenticationRepository;

import java.time.LocalDate;

public class _TransactionManager extends  BaseManager{

    public static CollectionReference _getCollectionReference() {
        return  FirebaseFirestore.getInstance()
            .collection("users")
            .document(AuthenticationRepository.getCurrentAuthenticationUid())
            .collection("transactions");
    }

    public static CollectionReference getCollectionReference(Context context, LocalDate date) {
        return  DailyTransactionsManager.getCollectionReference(context)
            .document(date.toString())
            .collection("transactions");
    }

    public static void addTransaction(_Transaction transaction, OnTransactionAdded callback){
        DocumentReference document = _getCollectionReference().document(transaction.getId());

        document.set(transaction).addOnSuccessListener(task->{
            callback.onTransactionAdded(transaction);
        });

        // For every transaction, don't forget to update daily sales that we use for forecasting
        UserManager.updateUserDailySales(transaction.getAmount());
    }

    public static Task<Void> saveTransaction(_Transaction transaction){
        DocumentReference document = _getCollectionReference().document(transaction.getId());
        return document.set(transaction);
    }

}
