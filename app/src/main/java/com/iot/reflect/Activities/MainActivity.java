package com.iot.reflect.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.iot.reflect.Fragments.ProfileFragment;
import com.iot.reflect.Fragments.HomeFragment;
import com.iot.reflect.R;
import com.iot.reflect.SessionManager;
import com.iot.reflect.ViewModels.PresenceViewModel;

public class MainActivity extends AppCompatActivity {

    SessionManager sessionManager;
    BottomNavigationView bottomNavigationView;
    Fragment selectorFragment;
    PresenceViewModel presenceViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(MainActivity.this);
        presenceViewModel = new ViewModelProvider(MainActivity.this).get(PresenceViewModel.class);

        presenceViewModel.setUid(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
        presenceViewModel.setPresence("Online");

        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.status2));


        Fragment f1 = new HomeFragment();
        Fragment f2 = new ProfileFragment();


        FragmentManager fm = getSupportFragmentManager();


        fm.beginTransaction().add(R.id.fragmentContainerView, f2, "2").hide(f2).commit();
        fm.beginTransaction().add(R.id.fragmentContainerView, f1, "1").commit();
        selectorFragment = f1;

        bottomNavigationView = findViewById(R.id.bnv);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()) {

                    case R.id.homeFragment:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f1).commit();
                        selectorFragment = f1;
                        break;


                    case R.id.profileFragment:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f2).commit();
                        selectorFragment = f2;
                        break;


                }


                return true;
            }
        });


    }


}