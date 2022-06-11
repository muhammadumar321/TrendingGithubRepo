package com.example.childfinarapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.os.Bundle;


public class LaunchScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        //app title bar is concealed
        getSupportActionBar().hide();

        Handler splashScreenhandler = new Handler();
        splashScreenhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LaunchScreenActivity.this , LoginActivity.class));
                finish();
            }
        } , 4000);
    }
}


