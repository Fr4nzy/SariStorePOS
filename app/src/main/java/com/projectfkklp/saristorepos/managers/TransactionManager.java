package com.projectfkklp.saristorepos.managers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.projectfkklp.saristorepos.interfaces.OnTransactionAdded;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.repositories.AuthenticationRepository;

public class TransactionManager extends  BaseManager{

    public static CollectionReference getCollectionReference() {
        return  FirebaseFirestore.getInstance()
            .collection("users")
            .document(AuthenticationRepository.getCurrentUserUid())
            .collection("transactions");
    }

    public static void addTransaction(Transaction transaction, OnTransactionAdded callback){
        DocumentReference document = getCollectionReference().document(transaction.getId());

        document.set(transaction).addOnSuccessListener(task->{
            callback.onTransactionAdded(transaction);
        });

        // For every transaction, don't forget to update daily sales that we use for forecasting
        UserManager.updateUserDailySales(transaction.getAmount());
    }

    public static Task<Void> saveTransaction(Transaction transaction){
        DocumentReference document = getCollectionReference().document(transaction.getId());
        return document.set(transaction);
    }

}
