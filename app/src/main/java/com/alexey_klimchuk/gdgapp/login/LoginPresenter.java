package com.alexey_klimchuk.gdgapp.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.notes.NotesActivity;
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
    private final LoginRelations.View view;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public LoginPresenter(LoginRelations.View loginView) {
        view = loginView;

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent intent = new Intent(view.getActivity(), NotesActivity.class);
                    view.getActivity().startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void login(String email, String password) {
        if (!isValid(email, password)) {
            return;
        }
        view.showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(view.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleCompleteLogging(task);
                    }
                });
    }

    private void handleCompleteLogging(@NonNull Task<AuthResult> task) {
        if (!task.isSuccessful()) {
            Log.w(TAG, "signInWithEmail:failed", task.getException());
            view.showMessage(R.string.auth_failed);
        }
        view.hideProgressDialog();
    }

    private boolean isValid(String email, String password) {
        if (email.length() > 0 && password.length() > 0) {
            return true;
        }
        view.showMessage(R.string.fields_not_walid);
        return false;
    }
}
