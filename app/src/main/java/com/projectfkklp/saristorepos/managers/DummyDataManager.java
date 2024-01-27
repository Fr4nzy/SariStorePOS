package com.projectfkklp.saristorepos.managers;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.projectfkklp.saristorepos.models._Product;
import com.projectfkklp.saristorepos.models._Transaction;
import com.projectfkklp.saristorepos.models.User;
import com.projectfkklp.saristorepos.repositories.DatasetRepository;
import com.projectfkklp.saristorepos.repositories.ProductRepository;
import com.projectfkklp.saristorepos.utils.DateUtils;
import com.projectfkklp.saristorepos.utils.ModelUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DummyDataManager {
    public static void uploadDummyData(Context context){
        // Retrieve dummy sales data
        double[] sales = DatasetRepository.getTrainingDataset(context); // Starting from Jan 1, 2019

        UserManager.deleteUser()
            .continueWithTask(task -> Tasks.forResult(uploadDummyDailySales(sales)))
            .continueWithTask(task->Tasks.forResult(uploadDummyProducts()))
            .continueWithTask(task -> Tasks.forResult(uploadDummyTransactions(sales)))
            .addOnCompleteListener(task->{
                Toast.makeText(context, "Uploading Dummy Data is completed!", Toast.LENGTH_LONG).show();
            })
        ;
    }

    public static ArrayList<Task<Void>> uploadDummyTransactions(double[] sales) {
        ArrayList<Task<Void>> tasks = new ArrayList<>();

        // Set start date
        Date date = DateUtils.addDays(new Date(), -sales.length);
        HashMap<Integer, _Product> dummyProductsHashmap = ProductRepository.getDummyProductHashMap();
        Integer[] keys = dummyProductsHashmap.keySet().stream()
                .sorted((a, b) -> Integer.compare(b, a)) // Sorting in descending order
                .toArray(Integer[]::new);

        for (double sale: sales) {
            _Transaction transaction = new _Transaction(
                    ModelUtils.createUUID(),
                    DateUtils.formatTimestamp(date),
                    sale
            );

            double balance = sale;
            for (int key: keys) {
                int quantity = (int) (balance / key);

                if (quantity==0) {
                    continue;
                }

                _Product product = dummyProductsHashmap.get(key);
                transaction.getItems().add(new _Product(
                        product.getKey(),
                        product.getPrice(),
                        product.getProduct(),
                        quantity
                ));

                balance -= key * quantity;
            }

            Task<Void> task = _TransactionManager.saveTransaction(transaction);
            tasks.add(task);

            // Increment date
            date = DateUtils.addDays(date, 1);
        }

        return tasks;
    }

    public static Task<Void> uploadDummyDailySales(double[] sales) {
        return UserManager.saveUser(
            new User(
                Arrays.stream(sales).boxed().collect(Collectors.toList()),
                DateUtils.addDays(new Date(), -1)
            )
        );
    }

    public static ArrayList<CompletableFuture<Void>> uploadDummyProducts() {
        ArrayList<CompletableFuture<Void>> tasks = new ArrayList<>();

        HashMap<Integer, _Product> dummyProductsHashmap = ProductRepository.getDummyProductHashMap();
        for (_Product product : dummyProductsHashmap.values()) {
            CompletableFuture<Void> task = ProductManager.saveProduct(product);
            tasks.add(task);
        }

        return tasks;
    }

}
