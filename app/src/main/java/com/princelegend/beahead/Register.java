package com.princelegend.beahead;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    // layout variables
    EditText fullName, email, password, phone;
    Button regButton;
    ProgressBar progressBar;
    TextView alreadyRegView;
    // authentication var
    FirebaseAuth fAuth;
    // Firestore var;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // providing each var with id
        fullName = (EditText) findViewById(R.id.regfullname);
        email = (EditText) findViewById(R.id.regEmail);
        password = (EditText) findViewById(R.id.regPassword);
        phone = (EditText) findViewById(R.id.phoneno);
        regButton = (Button) findViewById(R.id.register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        alreadyRegView = (TextView) findViewById(R.id.toLogin);

        // initializing firebase auth var
        fAuth = FirebaseAuth.getInstance();
        // initializing firestore var
        fStore = FirebaseFirestore.getInstance();

        // button on click listener
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userMail = email.getText().toString().trim();
                String userPass = password.getText().toString().trim();
                String userName = fullName.getText().toString().trim();
                String userPhone = phone.getText().toString().trim();

                if(TextUtils.isEmpty(userName)) {
                    fullName.setError("Name is required!");
                    return;
                }
                if(TextUtils.isEmpty(userMail)) {
                    email.setError("Email is required!");
                    return;
                }
                if(TextUtils.isEmpty(userPass)) {
                    password.setError("Password is required!");
                    return;
                }
                if(userPass.length() < 8) {
                    password.setError("Password must contain at least 8 characters!");
                    return;
                }

                // progress bar visibility on
                progressBar.setVisibility(View.VISIBLE);

                // Creating given user
                fAuth.createUserWithEmailAndPassword(userMail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String uid = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(uid);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fullName",userName);
                            user.put("Email",userMail);
                            user.put("phoneNo",userPhone);
                            documentReference.set(user);

                            // verification after register
                            fAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "User Created! Please check your email for verification.", Toast.LENGTH_SHORT).show();
                                    fullName.setText("");
                                    email.setText("");
                                    password.setText("");
                                    phone.setText("");
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this, "An error occurred! "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });

                        } else {
                            Toast.makeText(Register.this, "An error occurred!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        // text view on click listener
        alreadyRegView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

    }
}