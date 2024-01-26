package com.projectfkklp.saristorepos.repositories;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.projectfkklp.saristorepos.managers.StoreManager;
import com.projectfkklp.saristorepos.models.DailyTransactions;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.utils.DateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DailyTransactionsRepository {
    public static final int PAGINATION_LIMIT = 30;
    public static CollectionReference getCollectionReference(Context context) {
        return  StoreManager.getCollectionReference()
            .document(SessionRepository.getCurrentStore(context).getId())
            .collection("daily_transactions");
    }

    public static DocumentReference getDocument(Context context, String date){
        return getCollectionReference(context).document(date);
    }

    public static Task<DocumentSnapshot> getDailyTransactions(Context context, LocalDate date){
        return getCollectionReference(context).document(date.toString()).get();
    }

    public static Task<QuerySnapshot> getAll(Context context){
        return getCollectionReference(context).get();
    }

    public static Task<QuerySnapshot> getDailyTransactions(Context context, int page){
        LocalDate currentDate = LocalDate.now();
        String lowerDate = currentDate.plusDays(1 - (long) PAGINATION_LIMIT * (page+1) ).toString();
        String upperDate = currentDate.plusDays(-((long) PAGINATION_LIMIT *page) ).toString();

        return getCollectionReference(context)
            .orderBy("date", Query.Direction.DESCENDING)  // Order by date in descending order
            .whereGreaterThanOrEqualTo("date",lowerDate)
            .whereLessThanOrEqualTo("date", upperDate)
            .get()
        ;
    }

    public static Task<QuerySnapshot> getFirstDailyTransactions(Context context){
        return getCollectionReference(context)
            .orderBy("date", Query.Direction.ASCENDING)
            .limit(1)
            .get();
    }

    public static List<DailyTransactions> getDummyDailyTransactions(double[] sales){
        List<DailyTransactions> dummyDailyTransactions = new ArrayList<>();

        // Prepare products
        List<Product> dummyProducts = ProductRepository.getDummyProducts();
        HashMap<Integer, Product> hashedDummyProducts = new HashMap<>();
        for (Product dummyProduct:dummyProducts){
            hashedDummyProducts.put((int) dummyProduct.getUnitPrice(), dummyProduct);
        }
        Integer[] unitPrices = hashedDummyProducts.keySet().stream()
            .sorted((a, b) -> Integer.compare(b, a)) // Sorting in descending order
            .toArray(Integer[]::new);

        // Load dummyDailyTransactions
        Date date = DateUtils.addDays(new Date(), -sales.length);
        for (double sale: sales) {
            Transaction transaction = new Transaction(
                DateUtils.toLocalDateTime(date).toString(),
                new ArrayList<>()
            );

            double balance = sale;
            for (int unitPrice: unitPrices) {
                int quantity = (int) (balance / unitPrice);

                if (quantity==0) {
                    continue;
                }

                Product product = hashedDummyProducts.get(unitPrice);
                assert product != null;
                transaction.getItems().add(new TransactionItem(
                    product.getId(),
                    quantity,
                    product.getUnitPrice()
                ));

                balance -= unitPrice * quantity;
            }

            dummyDailyTransactions.add(new DailyTransactions(
                DateUtils.toLocalDate(date).toString(),
                Collections.singletonList(transaction)
            ));

            // Increment date
            date = DateUtils.addDays(date, 1);
        }

        return dummyDailyTransactions;
    }

}
