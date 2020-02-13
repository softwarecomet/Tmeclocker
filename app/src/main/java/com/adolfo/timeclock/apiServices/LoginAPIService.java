package com.adolfo.timeclock.apiServices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.adolfo.timeclock.utils.AppConstants;
import com.adolfo.timeclock.utils.LoadingDialog;

public class LoginAPIService extends AsyncTask<Void, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private String TAG = getClass().getSimpleName();
    private String emailStr, passwordStr;
    private boolean flagProgress;
    private LoadingDialog loadingDialog;
    private OnResultReceived mListner;

    public interface OnResultReceived {
        void onResult(String result);
    }

    public LoginAPIService(Context mContext, String emailStr, String passwordStr, OnResultReceived mListner, boolean flagProgress) {
        this.mContext = mContext;
        this.emailStr = emailStr;
        this.passwordStr = passwordStr;
        this.mListner = mListner;
        this.flagProgress = flagProgress;
    }

    public void show() {
        if (flagProgress) {
            loadingDialog = new LoadingDialog(mContext, false);
        }
    }

    private void hide() {
        if (flagProgress) {
            loadingDialog.hide();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        show();
    }

    @Override
    protected String doInBackground(Void... unsued) {
        String response;
        try {
            String url = AppConstants.BaseURL + APIServices.LOGIN;
            Log.e("URL", url);
            response = APIServices.POSTWithURL(url, emailStr, passwordStr);
            Log.e(TAG, " Response: " + response);
        } catch (Exception e) {
            Log.e(TAG, "Error:... " + e.getMessage());
            response = APIServices.RESPONSE_UNWANTED;
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        hide();
        if (mListner != null) mListner.onResult(result);
    }
}
