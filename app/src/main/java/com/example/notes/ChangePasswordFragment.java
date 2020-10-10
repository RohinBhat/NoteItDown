package com.example.notes;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    EditText edEmail, edOldPassword, edNewPassword, edConfirmNewPassword;
    Button change;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        edEmail = view.findViewById(R.id.ed_email_change_password);
        edOldPassword = view.findViewById(R.id.ed_old_password_change_password);
        edNewPassword = view.findViewById(R.id.ed_password_change_password);
        edConfirmNewPassword = view.findViewById(R.id.ed_confirm_password_change_password);
        change = view.findViewById(R.id.btn_change_password);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_new_password = edNewPassword.getText().toString().trim();
                String txt_confirm_new_password = edConfirmNewPassword.getText().toString().trim();

                if (TextUtils.isEmpty(txt_new_password) || TextUtils.isEmpty(txt_confirm_new_password)) {
                    Toast.makeText(getActivity(), "Empty Fields!!!", Toast.LENGTH_SHORT).show();
                } else if (!txt_new_password.equals(txt_confirm_new_password)) {
                    Toast.makeText(getActivity(), "Passwords do not match!!!", Toast.LENGTH_SHORT).show();
                } else if (txt_new_password.length() < 6) {
                    Toast.makeText(getActivity(), "Password should be longer than 6 characters!!!", Toast.LENGTH_SHORT).show();
                } else {
                    updatePassword();
                }
            }
        });

        return view;
    }

    public void updatePassword() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait a few momments...");
        progressDialog.show();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String txt_email = edEmail.getText().toString().trim();
        String txt_old_password = edOldPassword.getText().toString().trim();
        final String txt_new_password = edNewPassword.getText().toString().trim();

        AuthCredential authCredential = EmailAuthProvider.getCredential(txt_email, txt_old_password);

        firebaseUser.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseUser.updatePassword(txt_new_password).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Password has been successfully updated!!!", Toast.LENGTH_SHORT).show();
                            //Restart fragment
                        } else {
                            Toast.makeText(getActivity(), "Failed to update Password!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed to update Password!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
