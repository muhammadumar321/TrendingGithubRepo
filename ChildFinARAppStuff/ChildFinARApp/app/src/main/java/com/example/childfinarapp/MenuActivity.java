package com.example.childfinarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class MenuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //app title bar is concealed
        getSupportActionBar().hide();

        Button parentBtn = findViewById(R.id.homeBtn1);
        Button choreListBtn = findViewById(R.id.homeBtn2);

        parentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(new Intent(MenuActivity.this, ParentActivity.class ));
            }});

        choreListBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { startActivity(new Intent(MenuActivity.this, ChorelistActivity.class ));
                }});

        }


    FirebaseAuth.AuthStateListener userAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
            FirebaseUser signedInUser = mAuth.getCurrentUser();
            if (signedInUser != null) {
                Intent loggedInState = new Intent(MenuActivity.this, LoginActivity.class);
                startActivity(loggedInState);
                finish();
            }
        }
    };

}



