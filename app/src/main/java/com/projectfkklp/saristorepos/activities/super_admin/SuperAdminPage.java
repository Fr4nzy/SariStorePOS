package com.projectfkklp.saristorepos.activities.super_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.managers.SessionManager;
import com.projectfkklp.saristorepos.managers.UserManager;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.UserRepository;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.concurrent.atomic.AtomicReference;

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
            .setMessage("This operation will overwrite the products, transactions, and other data under store in this session.\n\nAre you sure you want to proceed this operation?")
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

    public void showRevokeSuperAdminAccessConfirmation(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
            .setTitle("Revoke Super Admin Access?")
            .setMessage("This will remove your access to super admin privilege, \ndo you want to proceed?")
            .setPositiveButton("Yes", (dialog, which) -> {
                revokeSuperAdminAccess();
            })
            .setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            })
            .show();
    }

    private void revokeSuperAdminAccess(){
        AtomicReference<User> atomicUser = new AtomicReference<>();
        ProgressUtils.showDialog(this, "Revoking Access...");
        UserRepository.getUserById(SessionRepository.getCurrentUser(this).getId())
            .continueWith(task->{
                User user = task.getResult().toObject(User.class);
                atomicUser.set(user);

                assert user != null;
                user.setIsSuperAdmin(false);
                return UserManager.saveUser(user);
            })
            .addOnSuccessListener(task->{
                User user = atomicUser.get();

                SessionManager.setUser(this, user);
                ToastUtils.show(this, "Super Admin Privilege Revoked!");

                finish();
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()))
            .addOnCompleteListener(task-> ProgressUtils.dismissDialog());
    }

    public void evaluateArima(View view){
        startActivity(new Intent(this, ArimaEvaluationPage.class));
    }
}