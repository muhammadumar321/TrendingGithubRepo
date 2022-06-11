package com.example.childfinarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button loginBtn, signUpBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //app title bar is concealed
        getSupportActionBar().hide();

        loginEmail = findViewById(R.id.regUserName);
        loginPassword = findViewById(R.id.regPassword);
        loginBtn = findViewById(R.id.loginButton);
        signUpBtn = findViewById(R.id.signUpButton1);

        mAuth=FirebaseAuth.getInstance();

        //directs user to the sign up page
        signUpBtn.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class ));
            }});

       //checks fields when user logs in
        loginBtn.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v){

                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    loginEmail.setError("There is no email");
                    loginEmail.requestFocus();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    loginEmail.setError("Enter a valid email address.");
                    loginEmail.requestFocus();
                    return;
                }
                else if (password.isEmpty()) {
                    loginPassword.setError("There is no password.");
                    loginPassword.requestFocus();
                    return;
                }
                else if (password.length() < 6) {
                    loginPassword.setError("The minimum password length is six characters.");
                    loginPassword.requestFocus();
                    return;
                } else {
                    Toast.makeText(LoginActivity.this,"An unknown error has occurred.",Toast.LENGTH_SHORT).show();
                }

                //logs user in via stored credentials in Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                }
                else
                {
                    Toast.makeText(LoginActivity.this,
                            "Please check your login details.",
                            Toast.LENGTH_SHORT).show();
                }

            }});
        }});

        }

    //decided to use AuthStateListener instead of using the getCurrentUser() method
    //Ensures that user is logged in and if user is, user is brought to the home page immediately
    FirebaseAuth.AuthStateListener userAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth mAuth) {
            FirebaseUser signedInUser = mAuth.getCurrentUser();
            if (signedInUser != null) {
                Intent loggedInState = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(loggedInState);
                finish();
            }
        }
    };

    //when app is killed and re-started, user is still logged in.
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(userAuthListener);
    }


}


