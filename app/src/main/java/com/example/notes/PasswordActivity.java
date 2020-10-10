package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {

    EditText edEmail;
    Button reset;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        edEmail = findViewById(R.id.ed_email_password_activity);
        reset = findViewById(R.id.btn_reset_password_password_activity);
        firebaseAuth = FirebaseAuth.getInstance();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = edEmail.getText().toString().trim();

                if (TextUtils.isEmpty(txt_email)) {
                    Toast.makeText(PasswordActivity.this, "Please enter your email!!!", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(txt_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordActivity.this, "Password Reset Email sent successfully!!!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PasswordActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(PasswordActivity.this, "Unable to send Password Reset Email!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
}
