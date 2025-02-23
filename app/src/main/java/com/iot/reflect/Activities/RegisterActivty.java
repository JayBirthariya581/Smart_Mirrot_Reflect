package com.iot.reflect.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.iot.reflect.R;

public class RegisterActivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
    }
}