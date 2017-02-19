package com.alexey_klimchuk.gdgapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Alexey on 31.10.2016.
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    public void setLoadingIndicator(boolean active) {
        if (active) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(getString(R.string.message_loading));
            mProgressDialog.show();
        } else {
            mProgressDialog.cancel();
        }
    }

}
