package com.alexey_klimchuk.gdgapp.activities.registration;

import android.app.Activity;

/**
 * Created by Alexey on 11.09.2016.
 */

public interface RegistrationRelations {

    interface View {

        void showProgressDialog();

        void hideProgressDialog();

        void showMessage(int message);

        void showMessage(String message);

        Activity getActivity();
    }

    interface Presenter {

        void registerUser(String email, String password);
    }
}
