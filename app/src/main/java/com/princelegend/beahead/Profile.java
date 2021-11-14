package com.princelegend.beahead;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    TextView profName,profMail,profPhone;
    ImageView editProfName,editProfPhone;
    Button changePass;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser firebaseUser;
    // to authenticate current user
    AuthCredential authCredential;

    // popup vars for pencil edit
    TextView titleTv,msgTv;
    Button sendBtn;
    EditText editableField;
    AlertDialog.Builder updateFieldDialogBuilder;

    // popup vars
    Button updateBtn,backButton;
    TextInputEditText currentPassEt,newPassEt,confirmPassEt;
    TextInputLayout currentPassLayout,newPassLayout,confirmPassLayout;
    AlertDialog.Builder updatePassDialogBuilder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // instances of both firebase vars
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        // getting current user
        firebaseUser = fAuth.getCurrentUser();

        // finding by ids
        profName = (TextView) findViewById(R.id.profileName);
        profMail = (TextView) findViewById(R.id.profileMail);
        profPhone = (TextView) findViewById(R.id.profilePhone);
        editProfName = (ImageView) findViewById(R.id.editProfileName);
        editProfPhone = (ImageView) findViewById(R.id.editProfilePhone);
        changePass = (Button) findViewById(R.id.changePassBtn);

        // String for uid
        String uid = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(uid);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                profPhone.setText(value.getString("phoneNo"));
                profMail.setText(value.getString("Email"));
                profName.setText(value.getString("fullName"));
            }
        });

        // onclick listener for edit profile name
        editProfName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFieldDialogBuilder = new AlertDialog.Builder(Profile.this);
                final View updateNamePopupView = getLayoutInflater().inflate(R.layout.reset_password, null);
                updateFieldDialogBuilder.setView(updateNamePopupView);
                dialog = updateFieldDialogBuilder.create();
                dialog.show();

                // finding viewids
                sendBtn = (Button) updateNamePopupView.findViewById(R.id.sendBtn);
                backButton = (Button) updateNamePopupView.findViewById(R.id.backBtn);
                editableField = (EditText) updateNamePopupView.findViewById(R.id.resetMail);
                titleTv = (TextView) updateNamePopupView.findViewById(R.id.resetPass);
                msgTv = (TextView) updateNamePopupView.findViewById(R.id.resetMsg);
                titleTv.setText("Update your name");
                msgTv.setText("Enter name to be updated");
                sendBtn.setText("Update");
                editableField.setHint("Enter name");
                editableField.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

                sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String editable = editableField.getText().toString().trim();
                        if(TextUtils.isEmpty(editable)) {
                            editableField.setError("Name is required!");
                            return;
                        }
                        profName.setText(editable);
                        Map<String,Object> user = new HashMap<>();
                        user.put("fullName",editable);
                        documentReference.update(user);
                        dialog.dismiss();
                    }
                });

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        // onclick listener for edit profile phone
        editProfPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFieldDialogBuilder = new AlertDialog.Builder(Profile.this);
                final View updatePhonePopupView = getLayoutInflater().inflate(R.layout.reset_password, null);
                updateFieldDialogBuilder.setView(updatePhonePopupView);
                dialog = updateFieldDialogBuilder.create();
                dialog.show();

                // finding viewids
                sendBtn = (Button) updatePhonePopupView.findViewById(R.id.sendBtn);
                backButton = (Button) updatePhonePopupView.findViewById(R.id.backBtn);
                editableField = (EditText) updatePhonePopupView.findViewById(R.id.resetMail);
                titleTv = (TextView) updatePhonePopupView.findViewById(R.id.resetPass);
                msgTv = (TextView) updatePhonePopupView.findViewById(R.id.resetMsg);
                titleTv.setText("Update your phone number");
                msgTv.setText("Enter phone number to be updated");
                sendBtn.setText("Update");
                editableField.setHint("Enter phone number");
                editableField.setInputType(InputType.TYPE_CLASS_PHONE);

                sendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String editable = editableField.getText().toString().trim();
                        if(TextUtils.isEmpty(editable)) {
                            editableField.setError("Name is required!");
                            return;
                        }
                        profPhone.setText(editable);
                        Map<String,Object> user = new HashMap<>();
                        user.put("phoneNo",editable);
                        documentReference.update(user);
                        dialog.dismiss();
                    }
                });

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        // Change pass set Click listener
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassDialogBuilder = new AlertDialog.Builder(Profile.this);
                final View updatePassPopupView = getLayoutInflater().inflate(R.layout.update_password, null);
                updatePassDialogBuilder.setView(updatePassPopupView);
                dialog = updatePassDialogBuilder.create();
                dialog.show();

                //popup vars
                updateBtn = (Button) updatePassPopupView.findViewById(R.id.updateBtn);
                backButton = (Button) updatePassPopupView.findViewById(R.id.backBtnUpdate);
                currentPassEt = (TextInputEditText) updatePassPopupView.findViewById(R.id.currentPass);
                newPassEt = (TextInputEditText) updatePassPopupView.findViewById(R.id.newPass);
                confirmPassEt = (TextInputEditText) updatePassPopupView.findViewById(R.id.confirmPass);
                currentPassLayout = (TextInputLayout) updatePassPopupView.findViewById(R.id.currentPassLayout);
                newPassLayout = (TextInputLayout) updatePassPopupView.findViewById(R.id.newPassLayout);
                confirmPassLayout = (TextInputLayout) updatePassPopupView.findViewById(R.id.confirmPassLayout);

                currentPassEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        currentPassLayout.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                newPassEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        newPassLayout.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                confirmPassEt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        confirmPassLayout.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                updateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String enteredNewPass = newPassEt.getText().toString().trim();
                        String enteredCurrentPass = currentPassEt.getText().toString().trim();
                        String enteredConfirmPass = confirmPassEt.getText().toString().trim();
                        if(TextUtils.isEmpty(enteredCurrentPass)) {
                            currentPassLayout.setError("Current password is required!");
                            return;
                        }
                        if(TextUtils.isEmpty(enteredNewPass)) {
                            newPassLayout.setError("Password is required!");
                            return;
                        }
                        if(enteredNewPass.length() < 8) {
                            newPassLayout.setError("Password must contain at least 8 characters!");
                            return;
                        }
                        if(TextUtils.isEmpty(enteredConfirmPass)) {
                            confirmPassLayout.setError("Confirm your new password!");
                            return;
                        }
                        if(!enteredConfirmPass.equals(enteredNewPass)) {
                            Toast.makeText(Profile.this,"Confirm password doesn't match new password!",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),enteredCurrentPass);
                        firebaseUser.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                firebaseUser.updatePassword(enteredNewPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Profile.this,"Password successfully updated",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Profile.this,"An error occurred! " + e.getMessage(),Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this,"An error occurred! " + e.getMessage(),Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                        dialog.dismiss();
                    }
                });

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }
}