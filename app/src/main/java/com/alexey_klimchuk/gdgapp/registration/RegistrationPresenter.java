package com.alexey_klimchuk.gdgapp.registration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.login.LoginActivity;
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
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void registerUser(String email, String password) {
        if (!validateForm(email, password)) {
            return;
        }

        mRegistrationView.showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(mRegistrationView.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.toString());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        mRegistrationView.hideProgressDialog();

                        if (!task.isSuccessful()) {
                            mRegistrationView.showMessage(task.getException().getMessage());
                        } else {
                            Intent intent = new Intent(mRegistrationView.getActivity(), LoginActivity.class);
                            mRegistrationView.getActivity().startActivity(intent);
                        }
                    }
                });
    }

    private boolean validateForm(String email, String password) {
        if (email.length() > 0 && password.length() > 0) {
            return true;
        }
        mRegistrationView.showMessage(R.string.fields_not_walid);
        return false;
    }
}
