package com.alexey_klimchuk.gdgapp.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alexey_klimchuk.gdgapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * RegistrationActivity activity.
 */
public class RegistrationActivity extends AppCompatActivity implements RegistrationRelations.View {

    private static final String TAG = "mRegistration";
    @BindView(R.id.edit_text_name_registration)
    EditText name;
    @BindView(R.id.edit_text_password_registration)
    EditText password;
    @BindView(R.id.edit_text_password_confirm)
    EditText passwordConfirm;

    private RegistrationRelations.Presenter mPresenter;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        mPresenter = new RegistrationPresenter(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.fab)
    public void onClick(View v) {
        if (password.getText().toString().equals(passwordConfirm.getText().toString())) {
            mPresenter.registerUser(name.getText().toString(), password.getText().toString());
        } else {
            showMessage(R.string.message_pass_not_conf);
        }
    }

    @Override
    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getString(R.string.message_loading));
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        mProgressDialog.cancel();
    }

    @Override
    public void showMessage(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
