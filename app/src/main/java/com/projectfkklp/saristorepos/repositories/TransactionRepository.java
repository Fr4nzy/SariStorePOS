package com.projectfkklp.saristorepos.repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectfkklp.saristorepos.interfaces.OnTransactionsRetrieve;
import com.projectfkklp.saristorepos.interfaces.OnTransactionRetrieve;
import com.projectfkklp.saristorepos.models._Transaction;
import com.projectfkklp.saristorepos.utils.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionRepository {
    public static CollectionReference getCollectionReference() {
        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(AuthenticationRepository.getCurrentAuthenticationUid())
                .collection("transactions");
    }

    public static void retrieveAllTransactions(
        OnTransactionsRetrieve callback,
        Context context
    ) {
        retrieveAllTransactions(callback, context,0);
    }
    public static void retrieveAllTransactions(
        OnTransactionsRetrieve callback,
        Context context,
        int page
    ) {
        List<_Transaction> transactionList = new ArrayList<>();

        CollectionReference transactionCollection = getCollectionReference();

        String lowerDate = DateUtils.formatTimestamp(DateUtils.addDays(new Date(), 1-30*(page+1)));
        String uppderDate = DateUtils.formatTimestamp(DateUtils.addDays(new Date(), -(30*page)));

        transactionCollection
                .orderBy("date", Query.Direction.DESCENDING)  // Order by date in descending order
                .whereGreaterThanOrEqualTo("date",lowerDate)
                .whereLessThanOrEqualTo("date", uppderDate)
                .get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        // Else, proceed in retrieving from Firebase
                        for (DocumentSnapshot document : querySnapshot) {
                            _Transaction transaction = document.toObject(_Transaction.class);
                            if (transaction != null) {
                                transactionCollection.document(document.getId())
                                        .collection("items")
                                        .get();

                                transactionList.add(transaction);
                            }
                        }

                    } else {
                        // Handle the error
                        Log.e("FirestoreListener", "Error fetching initial data", task.getException());
                    }
                    return null;
                })
                .addOnCompleteListener(task -> {
                    try {
                        callback.onTransactionsRetrieved(transactionList);
                    } catch (Exception e) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void getFirstTransaction(OnTransactionRetrieve callback) {
        getCollectionReference()
        .orderBy("date", Query.Direction.ASCENDING)
        .limit(1)
        .get().addOnSuccessListener(task->{
            List<_Transaction> transactions= task.toObjects(_Transaction.class);
                    try {
                        callback.onTransactionRetrieved(transactions.get(0));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}

