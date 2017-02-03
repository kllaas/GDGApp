package com.alexey_klimchuk.gdgapp.activities.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.alexey_klimchuk.gdgapp.Constants;
import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.activities.notes.NotesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Alexey on 11.09.2016.
 */

public class LoginPresenter implements LoginRelations.Presenter {

    private static final String TAG = "mLoginPresenter";

    private final LoginRelations.View mView;

    private FirebaseAuth mAuth;

    public LoginPresenter(LoginRelations.View loginView) {
        mView = loginView;

        mAuth = FirebaseAuth.getInstance();

        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent intent = new Intent(mView.getActivity(), NotesActivity.class);
                    mView.getActivity().startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void tryToLogin() {
        if (ContextCompat.checkSelfPermission(mView.getActivity(),
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mView.getActivity(),
                    new String[]{Manifest.permission.INTERNET},
                    Constants.PERMISSIONS_REQUEST_INTERNET);

            return;
        }

        login();
    }

    @Override
    public void login() {
        String email = mView.getEmail();
        String password = mView.getPassword();

        if (!isValid(email, password)) {
            return;
        }

        mView.showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mView.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleCompleteLogging(task);
                    }
                });
    }

    private void handleCompleteLogging(@NonNull Task<AuthResult> task) {
        if (!task.isSuccessful()) {
            Log.w(TAG, "signInWithEmail:failed", task.getException());
            mView.showMessage(R.string.auth_failed);
        }

        mView.hideProgressDialog();
    }

    private boolean isValid(String email, String password) {
        if (email.length() > 0 && password.length() > 0) {
            return true;
        }

        mView.showMessage(R.string.fields_not_walid);
        return false;
    }
}
