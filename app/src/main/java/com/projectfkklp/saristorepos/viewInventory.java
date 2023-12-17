package com.projectfkklp.saristorepos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton; // Use the correct FloatingActionButton class
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class viewInventory extends AppCompatActivity{
    RecyclerView recyclerView;
    List<DataClass> dataList;
    CollectionReference productsCollection;
    ListenerRegistration eventListener;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);

        FloatingActionButton fab = findViewById(R.id.fab); // Use the correct FloatingActionButton class
        recyclerView = findViewById(R.id.recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        dataList = new ArrayList<>();

        MyAdapterInventory adapter = new MyAdapterInventory(this, dataList);
        recyclerView.setAdapter(adapter);

        productsCollection = FirebaseFirestore.getInstance().collection("products");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();

        eventListener = productsCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Handle the error
                dialog.dismiss();
                return;
            }

            dataList.clear();
            for (DocumentSnapshot document : Objects.requireNonNull(value).getDocuments()) {
                DataClass dataClass = document.toObject(DataClass.class);
                if (dataClass != null) {
                    dataClass.setKey(document.getId());
                    dataList.add(dataClass);
                }
            }
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(viewInventory.this, Upload.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null) {
            eventListener.remove();
        }
    }
}
