package com.projectfkklp.saristorepos.activities.super_admin;

import android.os.AsyncTask;

import com.projectfkklp.saristorepos.utils.ProgressUtils;
import android.content.Context;

public class GenerateDummyDataTask extends AsyncTask<Void, Integer, Void> {
    private final Context context;

    public GenerateDummyDataTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        ProgressUtils.showDeterminateDialog(context, 100, "Generating dummy data...");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (int i = 0; i < 100; i++) {
            // Simulating some work being done
            // Update progress using publishProgress
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {

            }
            publishProgress(i);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        // Update the progress of the ProgressDialog
        ProgressUtils.setDeterminateProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // Dismiss the ProgressDialog when the task is complete
        ProgressUtils.dismissDeterminateDialog();
    }
}
