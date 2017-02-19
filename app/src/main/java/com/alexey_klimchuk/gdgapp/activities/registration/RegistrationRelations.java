package com.alexey_klimchuk.gdgapp.activities.registration;

import android.app.Activity;

/**
 * Created by Alexey on 11.09.2016.
 */

public interface RegistrationRelations {

    interface View {

        Activity getActivity();

        void setLoadingIndicator(boolean active);
    }

    interface Presenter {

        void registerUser(String email, String password);
    }
}
