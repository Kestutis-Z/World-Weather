package com.haringeymobile.ukweather.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.haringeymobile.ukweather.R;

/**
 * A task that shows a circular progress bar and the "loading" message while
 * executing.
 */
public abstract class AsyncTaskWithProgressBar<Params, Progress, Result>
        extends AsyncTask<Params, Progress, Result> {

    private Context context;
    private ProgressDialog progressDialog;

    public AsyncTaskWithProgressBar<Params, Progress, Result> setContext(
            Context context) {
        this.context = context;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (context != null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(
                    R.string.loading_message));
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface arg0) {
                    progressDialog.dismiss();
                }
            });

            progressDialog.show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
