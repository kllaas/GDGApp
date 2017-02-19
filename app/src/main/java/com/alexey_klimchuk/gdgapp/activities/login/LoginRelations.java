package com.alexey_klimchuk.gdgapp.activities.login;

import android.app.Activity;

/**
 * Created by Alexey on 11.09.2016.
 */

public interface LoginRelations {

    interface View {

        void setLoadingIndicator(boolean active);

        Activity getActivity();

        String getEmail();

        String getPassword();
    }

    interface Presenter {

        void tryToLogin();

        void login();
    }
}
