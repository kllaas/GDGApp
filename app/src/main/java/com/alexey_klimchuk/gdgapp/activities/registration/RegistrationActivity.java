package com.alexey_klimchuk.gdgapp.activities.registration;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.alexey_klimchuk.gdgapp.BaseActivity;
import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * RegistrationActivity activity.
 */
public class RegistrationActivity extends BaseActivity implements RegistrationRelations.View {

    @BindView(R.id.edit_text_name_registration)
    EditText name;

    @BindView(R.id.edit_text_password_registration)
    EditText password;

    @BindView(R.id.edit_text_password_confirm)
    EditText passwordConfirm;

    private RegistrationRelations.Presenter mPresenter;


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
            ToastUtils.showMessage(R.string.message_pass_not_conf, getActivity());
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
