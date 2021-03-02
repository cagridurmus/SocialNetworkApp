package com.example.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class OptionsActivity extends AppCompatActivity {

    TextView txt_exit , txt_options;
    ImageView exit , options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        txt_exit = findViewById(R.id.txt_exit_optionsActivity);
        txt_options = findViewById(R.id.txt_settings_optionsActivity);

        exit = findViewById(R.id.exit_optionsActivity);
        options = findViewById(R.id.settings_optionsActivity);

        Toolbar toolbar = findViewById(R.id.toolbar_optionsActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Secenekler");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(OptionsActivity.this , StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(OptionsActivity.this , StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        txt_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this ,  OptionsProfileActivity.class));
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this ,  OptionsProfileActivity.class));
            }
        });
    }
}