package com.alexey_klimchuk.gdgapp.activities.settings_dialog;

import android.app.Activity;

/**
 * Created by Alexey on 11.09.2016.
 */

public interface SettignsRelations {

    interface View {

        void showProgressDialog();

        void hideProgressDialog();

        Activity getActivity();

        void onLoadingEnd();
    }

    interface Presenter {

        void loadToServer();

        void loadFromServer();

    }
}
