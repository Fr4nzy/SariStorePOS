package com.projectfkklp.saristorepos;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistory extends AppCompatActivity {

    private TransactionAdapter adapter;
    private List<TransactionDataClass> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        RecyclerView recyclerView = findViewById(R.id.transactionHistoryRV);
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Retrieve transaction data from Firestore in descending order by date
        retrieveTransactionData();
    }

    private void retrieveTransactionData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("transactions")
                .orderBy("date", Query.Direction.DESCENDING)  // Order by date in descending order
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Assuming you have a Transaction class, modify accordingly
                            TransactionDataClass transaction = document.toObject(TransactionDataClass.class);
                            if (transaction != null) {
                                transactionList.add(transaction);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle the error
                        Log.e("FirestoreListener", "Error fetching initial data", task.getException());
                    }
                });
    }
}
