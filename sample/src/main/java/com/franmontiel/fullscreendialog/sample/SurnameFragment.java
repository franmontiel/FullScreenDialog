package com.franmontiel.fullscreendialog.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.franmontiel.fullscreendialog.DialogConfirmationButtonController;
import com.franmontiel.fullscreendialog.OnDialogActionListener;

/**
 * Created by fj on 25/02/17.
 */

public class SurnameFragment extends Fragment
        implements OnDialogActionListener, DialogConfirmationButtonController {

    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String RESULT_FULL_NAME = "RESULT_FULL_NAME";

    private DialogConfirmationButtonStatusController confirmationStatusController;
    private EditText surname;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.surname_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView name = (TextView) getView().findViewById(R.id.name);
        name.setText(getString(R.string.hi_name, getArguments().getString(EXTRA_NAME)));

        surname = (EditText) getView().findViewById(R.id.surnameField);

        confirmationStatusController.setEnabled(!surname.getText().toString().isEmpty());
        surname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmationStatusController.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void setDialogConfirmationButtonStatusController(DialogConfirmationButtonStatusController statusController) {
        this.confirmationStatusController = statusController;
    }

    @Override
    public void onConfirmDialog(DialogConfirmActionCallback callback) {
        Bundle result = new Bundle();
        result.putString(RESULT_FULL_NAME, getArguments().getString(EXTRA_NAME) + " " + surname.getText().toString());
        callback.onActionConfirmed(result);
    }

    @Override
    public void onDiscardDialog(DialogDiscardActionCallback callback) {
        callback.onActionConfirmed();
    }
}