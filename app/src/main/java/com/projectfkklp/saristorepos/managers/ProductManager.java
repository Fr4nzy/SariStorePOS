package com.projectfkklp.saristorepos.managers;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfkklp.saristorepos.models._Product;
import com.projectfkklp.saristorepos.repositories.AuthenticationRepository;

import java.util.concurrent.CompletableFuture;

public class ProductManager {
    public static CollectionReference getCollectionReference() {
        return  FirebaseFirestore.getInstance()
                .collection("users")
                .document(AuthenticationRepository.getCurrentAuthenticationUid())
                .collection("products");
    }

    public static CompletableFuture<Void> saveProduct(_Product product) {
        DocumentReference document = getCollectionReference().document(product.getKey());
        CompletableFuture<Void> future = new CompletableFuture<>();

        document.set(product)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("_Product saved successfully");
                    future.complete(null);
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error saving product: " + e.getMessage());
                    future.completeExceptionally(e);
                });

        return future;
    }

}
