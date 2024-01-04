package com.projectfkklp.saristorepos.managers;

import android.content.Context;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.projectfkklp.saristorepos.utils.AuthenticationUtils;

public class AuthenticationManager {
    public static void logout(Context context, OnCompleteListener<Void> onCompleteListener) {
        SessionManager.reset(context);
        AuthenticationUtils.signOutFirebase(context, onCompleteListener);
    }
}
