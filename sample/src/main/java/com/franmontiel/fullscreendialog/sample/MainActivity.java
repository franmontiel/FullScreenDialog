package com.franmontiel.fullscreendialog.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.franmontiel.fullscreendialog.FullScreenDialogFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.openDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullScreenDialogFragment build = new MyDialog.Builder(MainActivity.this)
                        .title("Titulo")
                        .positiveButton("Aceptar")
                        .content(R.layout.my_dialog)
                        .fullScreen(false).build();
                build.show(getSupportFragmentManager(), "dialog");
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}
