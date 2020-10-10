package com.example.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView txtSignUp, forgotPassword;
    EditText edEmail, edPassword;
    ImageView imgGoogle, imgFacebook, imgTwitter;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    int counter = 4;
    GoogleSignInClient mGoogleSignInClient;
    public int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupVariables();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
        }

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = edEmail.getText().toString().trim();
                String txt_password = edPassword.getText().toString().trim();
                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(LoginActivity.this, "Empty Credentials!!!", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(txt_email, txt_password);
                }
            }

            private void loginUser(String email, String password) {
                progressDialog.setMessage("Please wait a few moments...");
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            checkEmailVerification();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed!!!", Toast.LENGTH_SHORT).show();
                            counter--;
                            if (counter == 0) {
                                btnLogin.setEnabled(false);
                                Toast.makeText(LoginActivity.this, "Too many unsuccessful attempts!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        Closes app on back pressed
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void setupVariables() {
        btnLogin = findViewById(R.id.btn_login_login_activity);
        forgotPassword = findViewById(R.id.txt_forgot_password_login_activity);
        txtSignUp = findViewById(R.id.txt_sign_up_login_activity);
        edEmail = findViewById(R.id.ed_email_login_activity);
        edPassword = findViewById(R.id.ed_password_login_activity);
//        imgGoogle = findViewById(R.id.img_google_login_activity);
//        imgFacebook = findViewById(R.id.img_facebook_login_activity);
//        imgTwitter = findViewById(R.id.img_twitter_login_activity);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailFlag = firebaseUser.isEmailVerified();
        if (emailFlag) {
            Toast.makeText(LoginActivity.this, "Login Successful!!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Please verify your Email!!!", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Closes app on pressing back
        if (item.getItemId() == android.R.id.home) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
        return super.onOptionsItemSelected(item);
    }
}
