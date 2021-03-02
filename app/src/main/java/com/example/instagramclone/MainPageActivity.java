package com.example.instagramclone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Fragments.HomeFragment;
import com.example.instagramclone.Fragments.NotificationFragment;
import com.example.instagramclone.Fragments.ProfileFragment;
import com.example.instagramclone.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainPageActivity extends AppCompatActivity {

    BottomNavigationView navigationView;

    Fragment fragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        navigationView = findViewById(R.id.bottom_navigation);

        navigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        Bundle intent = getIntent().getExtras();

        if (intent != null)
        {
            String sended = intent.getString("publisher");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS" , MODE_PRIVATE).edit();
            editor.putString("profileid",sended);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.all_fragment, new ProfileFragment()).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.all_fragment, new HomeFragment()).commit();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.nav_home:
                            //Ana ekran cercevesi
                            fragment = new HomeFragment();

                            break;

                        case R.id.nav_search:
                            //Arama cercevesi
                            fragment = new SearchFragment();

                            break;

                        case R.id.nav_add:
                            //Gonderi ekleme ekranina
                            fragment = null;
                            startActivity(new Intent(MainPageActivity.this , PostActivity.class));

                            break;

                        case R.id.nav_fav:
                            //Bildirim cercevesi
                            fragment = new NotificationFragment();

                            break;

                        case R.id.nav_profile:

                            SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();

                            //Profil cercevesi
                            fragment = new ProfileFragment();

                            break;
                    }
                    if (fragment != null )
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.all_fragment,fragment).commit();
                    }
                    return true;
                }
            };
}