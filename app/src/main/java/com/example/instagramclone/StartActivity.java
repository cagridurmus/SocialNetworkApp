package com.example.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    Button btn_homeLogin , btn_homeSignup;

    FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();

        user = FirebaseAuth.getInstance().getCurrentUser();

        //Eger veri tabaninda kullanici varsa direk anasayfaya gonder
        if (user != null)
        {
            Intent intent = new Intent(StartActivity.this , MainPageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_homeLogin = findViewById(R.id.btn_login);
        btn_homeSignup = findViewById(R.id.btn_signup);

        btn_homeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this , LoginActivity.class));

            }
        });

        btn_homeSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this , SignupActivity.class));

            }
        });
    }
}