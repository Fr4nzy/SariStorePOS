package com.projectfkklp.saristorepos.activities.super_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.projectfkklp.saristorepos.R;

public class SuperAdminPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_admin_page);
    }

    public void navigateBack(View view){
        finish();
    }

    public void showCreateDummyDataConfirmation(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
            .setTitle("Generate Dummy Data?")
            .setMessage("This operation will delete the products, transactions, and other data under store in this session.\n\nAre you sure you want to proceed this operation?")
            .setPositiveButton("Yes", (dialog, which) -> {
                generateDummyData();
            })
            .setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            })
            .show();
    }

    private void generateDummyData(){
        new GenerateDummyDataTask(this).execute();
    }
}