package com.alexey_klimchuk.gdgapp.login;

import android.app.Activity;

/**
 * Created by Alexey on 11.09.2016.
 */

public interface LoginRelations {

    interface View {

        void showProgressDialog();

        void hideProgressDialog();

        void showMessage(int message);

        Activity getActivity();
    }

    interface Presenter {

        void login(String email, String password);
    }
}
