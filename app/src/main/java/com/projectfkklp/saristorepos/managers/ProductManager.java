package com.projectfkklp.saristorepos.managers;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models._Product;
import com.projectfkklp.saristorepos.repositories.AuthenticationRepository;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.utils.ModelUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.util.List;
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

    public static Task<Object> save(Context context, Product product) {
        return StoreRepository
            .getStoreById(SessionRepository.getCurrentStore(context).getId())
            .continueWith(task->{
                Store store = task.getResult().toObject(Store.class);
                assert store != null;
                List<Product> products = store.getProducts();

                // New Product
                if (StringUtils.isNullOrEmpty(product.getId())){
                    product.setId(ModelUtils.createUUID());
                    products.add(product);
                }
                // For existing product, update product
                else {
                    for (int i=0;i<products.size();i++){
                        Product existingProduct = products.get(i);
                        if (existingProduct.getId().equals(product.getId())){
                            products.set(i, product);
                            break;
                        }
                    }
                }

                return StoreManager.save(store);
            });
    }

}
