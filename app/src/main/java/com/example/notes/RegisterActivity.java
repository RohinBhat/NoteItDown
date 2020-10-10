package com.example.notes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    TextView txtSignIn;
    EditText edUsername, edEmail, edPassword, edConfirmPassword;
    ImageView imgGoogle, imgFacebook, imgTwitter, profilePic;
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    private static int PICK_IMAGE = 123;
    Uri imagePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupVariables();
        addTextWatchers();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Select Default Profile Picture?");
                builder.setMessage("Do you wish to have the default profile picture or do you wish to upload a picture?");
                builder.setPositiveButton("Upload Image", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*"); // application/*  audio/*
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE);
                    }
                });
                builder.setNegativeButton("Default Picture", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imagePath = Uri.parse("android.resource://com.example.notes/drawable/default_profile_pic");
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                            profilePic.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNeutralButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = edEmail.getText().toString().trim();
                String txt_username = edUsername.getText().toString().trim();
                String txt_password = edPassword.getText().toString().trim();
                String txt_confirm_password = edConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_confirm_password)) {
                    Toast.makeText(RegisterActivity.this, "Empty Credentials!!!", Toast.LENGTH_SHORT).show();
                } else if (imagePath == null) {
                    Toast.makeText(RegisterActivity.this, "Please select a Profile Picture", Toast.LENGTH_SHORT).show();
                } else if (!txt_password.equals(txt_confirm_password)) {
                    Toast.makeText(RegisterActivity.this, "Passwords don't match!!!", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Passwords should be longer than 6 characters!!!", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(txt_email, txt_password);
                }
            }

            private void registerUser(String email, String password) {
                progressDialog.setMessage("Please wait a few moments...");
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            sendVerificationEmail();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            private void sendVerificationEmail() {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendUserData();
                                firebaseAuth.signOut();
                                Toast.makeText(RegisterActivity.this, "Registration Successful, Verification Mail sent!!!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Unable to send Verification Mail!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            private void sendUserData() {
                String txt_email = edEmail.getText().toString().trim();
                String txt_user = edUsername.getText().toString().trim();

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
                StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic"); // User Id/Images/profile_pic.jpg i.e. Profile Pic is the name of the uploaded picture
                UploadTask uploadTask = imageReference.putFile(imagePath);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Profile Picture Upload Failed!!!" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(RegisterActivity.this, "Profile Picture Upload Successful!!!", Toast.LENGTH_SHORT).show();
                    }
                });

                databaseReference.setValue(new UserProfile(txt_email, txt_user));
            }
        });

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }


    public void setupVariables() {
        profilePic = findViewById(R.id.img_profile_pic_register_activity);
        btnRegister = findViewById(R.id.btn_register_register_activity);
        txtSignIn = findViewById(R.id.txt_sign_in_register_activity);
        edUsername = findViewById(R.id.ed_username_register_activity);
        edEmail = findViewById(R.id.ed_email_register_activity);
        edPassword = findViewById(R.id.ed_password_register_activity);
        edConfirmPassword = findViewById(R.id.ed_confirm_password_register_activity);
//        imgGoogle = findViewById(R.id.img_google_register_activity);
//        imgFacebook = findViewById(R.id.img_facebook_register_activity);
//        imgTwitter = findViewById(R.id.img_twitter_register_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();

        progressDialog = new ProgressDialog(this);
    }

    public void addTextWatchers() {
        edEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (edEmail.getText().toString().trim().length() <= 0) {
                    edEmail.setError("This field cannot be empty!!!");
                } else {
                    edEmail.setError(null);
                }

            }
        });

        edUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (edUsername.getText().toString().trim().length() <= 0) {
                    edUsername.setError("This field cannot be empty!!!");
                } else {
                    edUsername.setError(null);
                }

            }
        });

        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (edPassword.getText().toString().trim().length() <= 0) {
                    edPassword.setError("This field cannot be empty!!!");
                } else if (edPassword.getText().toString().trim().length() < 6) {
                    edPassword.setError("Password should be longer than 6 characters!!!");
                } else {
                    edPassword.setError(null);
                }

            }
        });

        edConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (edConfirmPassword.getText().toString().trim().length() <= 0) {
                    edConfirmPassword.setError("This field cannot be empty!!!");
                } else if (edConfirmPassword.getText().toString().length() < 6) {
                    edConfirmPassword.setError("Password should be longer than 6 characters!!!");
                } else if (!edConfirmPassword.getText().toString().trim().equals(edPassword.getText().toString().trim())) {
                    edConfirmPassword.setError("Passwords do not match!!!");
                    edPassword.setError("Passwords do not match!!!");
                } else if (edConfirmPassword.getText().toString().trim().equals(edPassword.getText().toString().trim())) {
                    edPassword.setError(null);
                } else {
                    edConfirmPassword.setError(null);
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            overridePendingTransition(0, 0); //Removes animation while switching activities
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
