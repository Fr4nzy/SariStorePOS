package com.projectfkklp.saristorepos.repositories;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AuthenticationRepository {
    public static FirebaseUser getCurrentAuthentication() {
        return Objects.requireNonNull(
                FirebaseAuth
                    .getInstance()
                    .getCurrentUser()
        );
    }
    public static String getCurrentAuthenticationUid() {
        return getCurrentAuthentication().getUid();
    }
}
