package com.projectfkklp.saristorepos.managers;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.projectfkklp.saristorepos.models.DailyTransactions;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.repositories.DailyTransactionsRepository;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DailyTransactionsManager {
    public static CollectionReference getCollectionReference(Context context) {
        return  StoreManager.getCollectionReference()
            .document(SessionRepository.getCurrentStore(context).getId())
            .collection("daily_transactions");
    }

    public static Task<Task<Void>> addTransaction(Context context, Transaction transaction){
        String date = LocalDate.now().toString();
        DocumentReference dailyTransactionsDocument = DailyTransactionsRepository
            .getDocument(context, date);

        return StoreRepository
            .getStoreById(SessionRepository.getCurrentStore(context).getId())
            // Update Store Details: daily sales & products stocks
            .continueWith(task->{
                Store store = task.getResult().toObject(Store.class);
                assert store != null;

                // Update Daily Sales
                updateDailySales(store, transaction.calculateTotalQuantity(), transaction.calculateTotalSales());

                // Update Product Stocks
                for (TransactionItem transactionItem:transaction.getItems()){
                    Optional<Product> optionalProduct = store
                        .getProducts()
                        .stream()
                        .filter(p->p.getId().equals(transactionItem.getProductId()))
                        .findFirst();

                    if (!optionalProduct.isPresent()){
                        throw new Exception("Product Not Found");
                    }

                    Product product = optionalProduct.get();

                    product.setStocks(product.getStocks()-transactionItem.getQuantity());
                }

                return StoreManager.save(store);
            })
            // Add transaction to todayDailyTransactions
            .continueWithTask(task-> dailyTransactionsDocument.get())
            .continueWith(task->{
                DailyTransactions todayDailyTransactions = task.getResult().toObject(DailyTransactions.class);

                if (todayDailyTransactions==null){
                    // For first transaction of the day
                    todayDailyTransactions = new DailyTransactions(date);
                }

                todayDailyTransactions.getTransactions().add(transaction);

                return dailyTransactionsDocument.set(todayDailyTransactions);
            })
        ;
    }

    private static void updateDailySales(Store store, int newTransactionTotalQuantity, double newTransactionAmount){
        List<Integer> dailySold = store.getDailySold();
        List<Double> dailySales = store.getDailySales();
        Date lastUpdatedAt = store.getDailySalesUpdatedAt();
        Date currentDate = new Date();

        store.setDailySalesUpdatedAt(currentDate);

        //If no daily sales, adds a new transaction amount
        if (dailySales.size()==0) {
            dailySold.add(newTransactionTotalQuantity);
            dailySales.add(newTransactionAmount);
            return;
        }

        // If there is existing entry for today
        if(DateUtils.isSameDay(lastUpdatedAt, currentDate)){
            int lastItemIndex = dailySales.size()-1;

            Integer updatedDailySold = dailySold.get(lastItemIndex) + newTransactionTotalQuantity;
            dailySold.set(dailySold.size()-1, updatedDailySold);

            Double updatedDailySale = dailySales.get(lastItemIndex) + newTransactionAmount;
            dailySales.set(dailySales.size()-1, updatedDailySale);
            return;
        }

        // updatedAt and currentDate is not equal
        {
            // get gaps for difference of two or more, and fill it with zeros
            long gaps = DateUtils.calculateDaysDifference(lastUpdatedAt, currentDate) - 1;
            for (int i=0; i<gaps;i++){
                dailySold.add(0);
                dailySales.add(0.);
            }

            // Append the newTransactionAmount at the end of List
            dailySold.add(newTransactionTotalQuantity);
            dailySales.add(newTransactionAmount);
        }
    }
}
