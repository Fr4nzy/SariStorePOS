package com.projectfkklp.saristorepos.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;

public class AuthenticationUtils {
    private static final Intent phoneSignInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(Collections.singletonList(
                    new AuthUI.IdpConfig.PhoneBuilder().build()
            ))
            .build();
    private static final Intent gmailSignInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(Collections.singletonList(
                    new AuthUI.IdpConfig.GoogleBuilder().build()
            ))
            .build();

    private static ActivityResultLauncher<Intent> createSignInLauncher(
        ComponentActivity activity,
        ActivityResultCallback<ActivityResult> callback
    ){
        return activity.registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                callback.onActivityResult(result);
                if (result.getResultCode() == Activity.RESULT_OK) {
                    signOutFirebase(activity, x -> {});
                }
            }
        );
    }

    public static void authenticateViaPhone(ComponentActivity activity, ActivityResultCallback<ActivityResult> callback){
        createSignInLauncher(activity, callback).launch(phoneSignInIntent);
    }

    public static void authenticateViaGmail(ComponentActivity activity, ActivityResultCallback<ActivityResult> callback){
        createSignInLauncher(activity, callback).launch(gmailSignInIntent);
    }

    public static void signOutFirebase(Context context, OnCompleteListener<Void> onCompleteListener){
        FirebaseAuth.getInstance().signOut();
        AuthUI.getInstance().signOut(context).addOnCompleteListener(onCompleteListener);
    }

}
