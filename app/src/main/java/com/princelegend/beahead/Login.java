package com.princelegend.beahead;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {

    // defining vars
    EditText email, password;
    Button loginButton;
    TextView toRegView, forgotPass;
    TextView titleTv,msgTv;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    // popup vars
    Button sendBtn,backBtn;
    EditText reset;
    AlertDialog.Builder passwordResetDialogBuilder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.logemail);
        password = (EditText) findViewById(R.id.logpassword);
        loginButton = (Button) findViewById(R.id.login);
        toRegView = (TextView) findViewById(R.id.newheretxt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        forgotPass = (TextView) findViewById(R.id.forgotPassword);

        fAuth = FirebaseAuth.getInstance();

        // if user is already logged in
        if(fAuth.getCurrentUser() != null && fAuth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userMail = email.getText().toString().trim();
                String userPass = password.getText().toString().trim();

                if(TextUtils.isEmpty(userMail)) {
                    email.setError("Email is required!");
                    return;
                }
                if(TextUtils.isEmpty(userPass)) {
                    password.setError("Password is required!");
                    return;
                }

                // progress bar visibility on
                progressBar.setVisibility(View.VISIBLE);

                // authenticate the user
                fAuth.signInWithEmailAndPassword(userMail,userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            if(fAuth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(Login.this,"Please verify your email first.",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            Toast.makeText(Login.this, "An error occurred! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        toRegView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordResetDialogBuilder = new AlertDialog.Builder(Login.this);
                final View resetPassPopupView = getLayoutInflater().inflate(R.layout.reset_password, null);
                passwordResetDialogBuilder.setView(resetPassPopupView);
                dialog = passwordResetDialogBuilder.create();
                dialog.show();

                //popup vars
                sendBtn = (Button) resetPassPopupView.findViewById(R.id.sendBtn);
                backBtn = (Button) resetPassPopupView.findViewById(R.id.backBtn);
                reset = (EditText) resetPassPopupView.findViewById(R.id.resetMail);
                titleTv = (TextView) resetPassPopupView.findViewById(R.id.resetPass);
                msgTv = (TextView) resetPassPopupView.findViewById(R.id.resetMsg);
                titleTv.setText("Reset your password");
                msgTv.setText("Enter your mail to receive reset link");
                reset.setHint("Enter mail");
                reset.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                sendBtn.setText("Send");

                sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String enteredMail = reset.getText().toString().trim();
                        if(TextUtils.isEmpty(enteredMail)) {
                            reset.setError("Email is required!");
                            return;
                        }
                        fAuth.sendPasswordResetEmail(enteredMail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this,"Resent link sent to your mail",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this,"Error! Reset link is not sent! " + e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }
}