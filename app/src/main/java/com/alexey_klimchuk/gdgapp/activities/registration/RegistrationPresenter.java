package com.alexey_klimchuk.gdgapp.activities.registration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.activities.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Alexey on 11.09.2016.
 */

public class RegistrationPresenter implements RegistrationRelations.Presenter {

    private static final String TAG = "mLoginPresenter";
    private final RegistrationRelations.View mRegistrationView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    public RegistrationPresenter(RegistrationRelations.View registrationView) {
        mRegistrationView = registrationView;
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void registerUser(String email, String password) {
        if (!isValid(email, password)) {
            return;
        }

        mRegistrationView.showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(mRegistrationView.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleRegistrationResult(task);
                    }
                });
    }

    private void handleRegistrationResult(@NonNull Task<AuthResult> task) {
        mRegistrationView.hideProgressDialog();

        if (!task.isSuccessful()) {
            mRegistrationView.showMessage(task.getException().getMessage());
        } else {
            Intent intent = new Intent(mRegistrationView.getActivity(), LoginActivity.class);
            mRegistrationView.getActivity().startActivity(intent);
        }
    }

    private boolean isValid(String email, String password) {
        if (email.length() > 0 && password.length() > 0) {
            return true;
        }
        mRegistrationView.showMessage(R.string.fields_not_walid);
        return false;
    }
}
