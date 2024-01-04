package com.projectfkklp.saristorepos.managers;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.projectfkklp.saristorepos.utils.AuthenticationUtils;

public class AuthenticationManager {
    public static void logout(Context context, OnCompleteListener<Void> onCompleteListener) {
        SessionManager.reset(context);
        AuthenticationUtils.signOutFirebase(context, onCompleteListener);
    }
}
