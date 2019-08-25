package com.franmontiel.fullscreendialog.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.franmontiel.fullscreendialog.FullScreenDialogFragment;

public class MainActivity extends AppCompatActivity
        implements FullScreenDialogFragment.OnConfirmListener,
        FullScreenDialogFragment.OnDiscardListener,
        FullScreenDialogFragment.OnDiscardFromExtraActionListener {

    private TextView fullName;
    private FullScreenDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fullName = (TextView) findViewById(R.id.fullName);
        final EditText name = (EditText) findViewById(R.id.nameField);
        Button openDialog = (Button) findViewById(R.id.openDialog);

        final String dialogTag = "dialog";
        if (savedInstanceState != null) {
            dialogFragment =
                    (FullScreenDialogFragment) getSupportFragmentManager().findFragmentByTag(dialogTag);
            if (dialogFragment != null) {
                dialogFragment.setOnConfirmListener(this);
                dialogFragment.setOnDiscardListener(this);
                dialogFragment.setOnDiscardFromExtraActionListener(this);
            }
        }

        openDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bundle args = new Bundle();
                args.putString(SurnameFragment.EXTRA_NAME, name.getText().toString());
                name.setText("");

                dialogFragment = new FullScreenDialogFragment.Builder(MainActivity.this)
                        .setTitle(R.string.insert_surname)
                        .setConfirmButton(R.string.dialog_positive_button)
                        .setOnConfirmListener(MainActivity.this)
                        .setOnDiscardListener(MainActivity.this)
                        .setContent(SurnameFragment.class, args)
//                        .setExtraActions(R.menu.extra_items)
//                        .setOnDiscardFromActionListener(MainActivity.this)
                        .build();

                dialogFragment.show(getSupportFragmentManager(), dialogTag);
            }
        });
    }

    @Override
    public void onConfirm(@Nullable Bundle result) {
        fullName.setText(result.getString(SurnameFragment.RESULT_FULL_NAME));
    }

    @Override
    public void onDiscard() {
        Toast.makeText(MainActivity.this, R.string.dialog_discarded, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDiscardFromExtraAction(int actionId, @Nullable Bundle result) {
        Toast.makeText(MainActivity.this, String.valueOf(actionId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (dialogFragment != null && dialogFragment.isAdded()) {
            dialogFragment.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }


}
