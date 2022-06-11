package com.example.childfinarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.HashMap;
import java.util.Map;




public class SignUpActivity extends AppCompatActivity {

    private Button nextBtn;
    private EditText parentName, nameOfChild, userEmail, userPassword;
    private FirebaseAuth mAuth;
    public static final String TAG = "SignUpActivity";

    // instantiation of SignUpInformation to get and set profile information
    private SignUpInformation signUpInfo = new SignUpInformation();

    //Writes the sign up information to the database
    //Obtain references for parent and child nodes of the database
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://child-fin-ar-app-83bb7-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private final DatabaseReference signUpInfoDB = database.getReference("signUpInfo");
    private final DatabaseReference signUpInfoUsers = signUpInfoDB.child("Users");
    private String emailAuthDBUserID="";
    private FirebaseFirestore firebaseDB = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //variables of input fields
        parentName=findViewById(R.id.userName);
        nameOfChild=findViewById(R.id.childName);
        userEmail=findViewById(R.id.userEmail);
        userPassword=findViewById (R.id.userPassword);

        //the NEXT button variable
        nextBtn=findViewById(R.id.signUpButton2);

        //Instantiates FirebaseAuth class as an object and returns it
        mAuth=FirebaseAuth.getInstance();

        //app title bar is concealed
        getSupportActionBar().hide();

        //Tasks that are executed to check fields when the 'next' button is clicked.
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = parentName.getText().toString().trim();
                String childsName= nameOfChild.getText().toString().trim();
                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                if(userName.isEmpty() && childsName.isEmpty())
                {
                    parentName.setError("The name fields must be filled.");
                    parentName.requestFocus();
                    return;

                }

                if (email.isEmpty() && email != null) {
                    userEmail.setError("Email is empty.");
                    userEmail.requestFocus();
                    return;

                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    userEmail.setError("Enter a valid email address.");
                    userEmail.requestFocus();
                    return;

                }

                if (password.isEmpty() && password != null) {
                    userPassword.setError("Enter a password.");
                    userPassword.requestFocus();
                    return;

                }

                if (password.length()<6) {
                    userPassword.setError("Length of the password should be more than 6 characters.");
                    userPassword.requestFocus();
                    return;

                }
                //else {
                 //   Toast.makeText(SignUpActivity.this,"An unknown error has occurred.",Toast.LENGTH_SHORT).show();
               // }

                //creates an authentication account in firebase authentication
                // and adds sign up information to firebase realtime database upon completion
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            String emailAuthDBUserID = mAuth.getCurrentUser().getUid();
                            addSignUpInfoToDB(emailAuthDBUserID,userName,childsName,email);
                            Toast.makeText(SignUpActivity.this,"You are successfully registered", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        }
                        else
                        {
                            Toast.makeText(SignUpActivity.this,"You are not registered! Please try again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });}
//method to push generatedEmailAuthUID, userName, childName and email to firebase
    private void addSignUpInfoToDB(String emailAuthDBUserID, String userName, String childName, String email) {

        signUpInfo.setParentName(userName);
        signUpInfo.setChildName(childName);
        signUpInfo.setParentEmail(email);
        signUpInfo.setAuthDBUserID(emailAuthDBUserID);

        //set emailAuthUserID (firebase authentication Uid) as the ID for each user
        signUpInfoUsers.child(emailAuthDBUserID).setValue(signUpInfo);

        //creates creditScore in firestore with each user's authentication id as the key
        DocumentReference fsDB = firebaseDB.collection("users").document(emailAuthDBUserID);
        Map<String, Object> creditScoreMap = new HashMap<>();
        creditScoreMap.put("creditNumber", 0);
        fsDB.set(creditScoreMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Current field, Credit Score, has been successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "An error has occurred:", e);
                    }
                });
    }
            // Subsequent line for debugging purposes
                public void onCancelled(@NonNull DatabaseError failureToAddInfo) {
                    //If sign up data has not been added, the cause of error is added to the failure message
                    Toast.makeText(SignUpActivity.this, "Failure to add data " + failureToAddInfo, Toast.LENGTH_SHORT).show();


        }
}



