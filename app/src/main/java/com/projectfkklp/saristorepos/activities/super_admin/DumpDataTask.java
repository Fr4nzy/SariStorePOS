package com.projectfkklp.saristorepos.activities.super_admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.projectfkklp.saristorepos.managers.StoreManager;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.repositories.DailyTransactionsRepository;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.utils.DateUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DumpDataTask extends AsyncTask<Void, Integer, Void> {
    private final Context context;
    private ProgressDialog progressDialog;
    private int max = 1;
    private boolean isCompleted = false;
    private Store store;

    public DumpDataTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Dumping Data");
        progressDialog.setMessage("Calculating data...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        setProgressMax()
            .continueWithTask(task-> resetStoreData())
            .addOnCompleteListener(task-> clearDailyTransactionsFirebaseCollection())
            .addOnFailureListener(e -> showErrorDialog(e.getMessage()))
        ;

        while (!isCompleted){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {

            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        // Update the progress of the ProgressDialog
        progressDialog.setProgress(progressDialog.getProgress()+1);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // Dismiss the ProgressDialog when the task is complete
        progressDialog.dismiss();
        showAlertDialog();
    }

    // Dummy Data Generation Steps
    private Task<Object> setProgressMax(){
        return DailyTransactionsRepository.getAll(context)
            .continueWith(task->{
               max += task.getResult().size();
               progressDialog.setMax(max);
               progressDialog.setIndeterminate(false);

               return task;
            });
    }

    private Task<Task<Void>> resetStoreData(){
        progressDialog.setMessage("Resetting store products, and other data...");
        return StoreRepository.getStoreById(SessionRepository.getCurrentStore(context).getId())
            .continueWith(task->{
                store = task.getResult().toObject(Store.class);

                assert store != null;
                dumpProducts();
                resetDailySalesAndSold();

                publishProgress();
                return StoreManager.save(store);
            })
        ;
    }

    private void dumpProducts(){
        store.getProducts().clear();
    }

    private void resetDailySalesAndSold(){
        store.getDailySold().clear();
        store.getDailySales().clear();

        // NOTE: Last transaction is from yesterday (Check dummyDailyTransactions)
        store.setDailySalesUpdatedAt(DateUtils.addDays(new Date(), -1));
    }

    private void clearDailyTransactionsFirebaseCollection(){
        progressDialog.setMessage("Clearing Daily Transactions...");
        DailyTransactionsRepository.getAll(context)
            .addOnFailureListener(e -> showErrorDialog(e.getMessage()))
            .addOnCompleteListener(task->{
                if (!task.isSuccessful()) {
                    showErrorDialog(Objects.requireNonNull(task.getException()).getMessage());

                    return;
                }

                List<Task<Void>> deleteTasks = new ArrayList<>();
                for (QueryDocumentSnapshot document: task.getResult()){
                    Task<Void> deleteTask = document.getReference().delete();
                    deleteTask.addOnCompleteListener(dt->{
                        if (dt.isSuccessful()){
                            publishProgress();
                        }
                        else {
                            showErrorDialog(Objects.requireNonNull(dt.getException()).getMessage());
                        }
                    });

                    deleteTasks.add(deleteTask);
                }

                Tasks.whenAllComplete(deleteTasks).addOnCompleteListener(completeTask->{
                    isCompleted=true;
                });
            })
        ;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set the title and message for the dialog
        builder.setTitle("Done")
            .setMessage("Dumping Data is now done.\nYou may now close the dialog.");

        // Set the positive button and its action
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Perform the action when the "OK" button is clicked
                // (You can leave this empty if you just want to close the dialog)
                dialogInterface.dismiss(); // Close the dialog
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showErrorDialog(String message){
        progressDialog.dismiss();
        ToastUtils.show(context, message);
    }

}
