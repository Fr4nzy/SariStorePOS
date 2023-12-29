package com.projectfkklp.saristorepos.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AuthenticationRepository {
    public static FirebaseUser getCurrentUser() {
        return Objects.requireNonNull(
            FirebaseAuth
                .getInstance()
                .getCurrentUser()
        );
    }
    public static String getCurrentUserUid() {
        return getCurrentUser().getUid();
    }
}
