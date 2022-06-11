package com.example.childfinarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;


public class ParentActivity extends AppCompatActivity {


    //Obtain references for parent and child databases
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://child-fin-ar-app-83bb7-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference signUpInfoDB = database.getReference("signUpInfo");
    private final DatabaseReference signUpInfoUsers = signUpInfoDB.child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        //app title bar is concealed
        getSupportActionBar().hide();

        Button loggingOutButton = findViewById(R.id.logOutButton);
        Button enterChildMode = findViewById(R.id.enterChildMode);
        TextView pName = (TextView) findViewById(R.id.parentName);
        TextView pEmail = (TextView) findViewById(R.id.parentEmail);
        TextView cName = (TextView) findViewById(R.id.childName);

        loggingOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); //logs user out
                startActivity(new Intent(ParentActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });

        enterChildMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ParentActivity.this, ChildModeActivity.class));
                finishAffinity();
            }
        });
        //obtain authentication session userID and use it as a child reference for the database
        String signedInUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference currentUser = signUpInfoDB.child("Users").child(signedInUserUID);

        ValueEventListener obtainProfileDataEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String uName = (String) dataSnapshot.child("parentName").getValue();
                String cNameDB = (String) dataSnapshot.child("childName").getValue();
                String emailDB = (String) dataSnapshot.child("parentEmail").getValue();

                pName.setText("Parent's Name: " + uName);
                cName.setText("Child's Name: " + cNameDB);
                pEmail.setText("Email: " + emailDB);

            }

            @Override
            public void onCancelled(DatabaseError failureToRetrieveInfo) {
                //If data has not been retrieved, the cause of error is added to the failure message
                Toast.makeText(ParentActivity.this, "Failure to display data" + failureToRetrieveInfo, Toast.LENGTH_SHORT).show();
            }
        };
        currentUser.addListenerForSingleValueEvent(obtainProfileDataEventListener);
    }
}



