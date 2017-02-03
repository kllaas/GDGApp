package com.alexey_klimchuk.gdgapp.activities.settings_dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.alexey_klimchuk.gdgapp.R;
import com.rey.material.widget.Button;
import com.rey.material.widget.ProgressView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alexey on 13.11.2016.
 */

public class SettingsDialogFragment extends DialogFragment implements SettignsRelations.View {

    @BindView(R.id.progress_bar)
    ProgressView progressView;
    @BindView(R.id.buttonLoadTo)
    Button btnLoadTo;
    @BindView(R.id.buttonLoadFrom)
    Button btnLoadFrom;

    private SettignsRelations.Presenter mPresenter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_settings, null);

        ButterKnife.bind(this, v);
        mPresenter = new SettingsPresenter(this);

        builder.setView(v).setTitle(getResources().getString(R.string.synchronizing)).
                setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SettingsDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }

    @OnClick(R.id.buttonLoadFrom)
    public void onLoadFromClick() {
        mPresenter.loadFromServer();
    }

    @OnClick(R.id.buttonLoadTo)
    public void onLoadToClick() {
        mPresenter.loadToServer();
    }

    @Override
    public void showProgressDialog() {
        progressView.setVisibility(View.VISIBLE);
        disableButtons();
    }

    @Override
    public void hideProgressDialog() {
        progressView.setVisibility(View.VISIBLE);
        enableButtons();
    }

    @Override
    public void onLoadingEnd() {
        getDialog().cancel();
    }

    private void disableButtons() {
        btnLoadFrom.setEnabled(false);
        btnLoadTo.setEnabled(false);
        getDialog().setCancelable(false);
    }

    private void enableButtons() {
        btnLoadFrom.setEnabled(true);
        btnLoadTo.setEnabled(true);
        getDialog().setCancelable(true);
    }

}