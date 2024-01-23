package com.projectfkklp.saristorepos.activities.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.inventory._InventoryProductListPage;
import com.projectfkklp.saristorepos.activities.pos._PosPage;
import com.projectfkklp.saristorepos.activities.transaction.transaction_daily_summary._TransactionDailySummaryPage;
import com.projectfkklp.saristorepos.activities.user_login.UserLoginPage;
import com.projectfkklp.saristorepos.activities.analytics.AnalyticsDailySalesPage;
import com.projectfkklp.saristorepos.activities.analytics.AnalyticsForecastEntryPage;
import com.projectfkklp.saristorepos.utils.CacheUtils;

public class _DashboardPage extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._dashboard_page);

        CardView viewInventory = findViewById(R.id.viewInventory);
        CardView viewPOS = findViewById(R.id.viewPOS);
        CardView salesReport = findViewById(R.id.salesReport);
        CardView logout = findViewById(R.id.logout);
        CardView barchart = findViewById(R.id.barchart);
        CardView breakdown = findViewById(R.id.breakdown);

        viewInventory.setOnClickListener(this);
        viewPOS.setOnClickListener(this);
        salesReport.setOnClickListener(this);
        logout.setOnClickListener(this);
        barchart.setOnClickListener(this);
        breakdown.setOnClickListener(this);

        //DummyDataManager.uploadDummyData(this);
        //Arima.evaluate(this);
    }

    @Override
    public void onClick(View view) {
        Intent i;

        if (view.getId() == R.id.viewInventory) {
            i = new Intent(this, _InventoryProductListPage.class);
        } else if (view.getId() == R.id.viewPOS) {
            i = new Intent(this, _PosPage.class);
        } else if (view.getId() == R.id.salesReport) {
            i = new Intent(this, AnalyticsForecastEntryPage.class);
        } else if (view.getId() == R.id.barchart) {
            i = new Intent(this, AnalyticsDailySalesPage.class);
        } else if (view.getId() == R.id.logout) {
            showLogoutConfirmationDialog();
            return;
        } else {
            i = new Intent(this, _TransactionDailySummaryPage.class);
        }
        startActivity(i);
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logout();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void clearUserSession() {
        SharedPreferences preferences = getSharedPreferences("user_session_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    void logout() {
        FirebaseAuth.getInstance().signOut();
        clearUserSession();

        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {
            final Intent intent = new Intent(_DashboardPage.this, UserLoginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });

        CacheUtils.dump(this);
    }
}
