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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeEmailFragment extends Fragment {

    EditText edOldEmail, edPassword, edNewEmail;
    Button change;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);

        progressDialog = new ProgressDialog(getActivity());
        edNewEmail = view.findViewById(R.id.ed_new_email_change_email);
        edOldEmail = view.findViewById(R.id.ed_old_email_change_email);
        edPassword = view.findViewById(R.id.ed_password_change_email);
        change = view.findViewById(R.id.btn_change_email);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = edNewEmail.getText().toString().trim();

                if (TextUtils.isEmpty(txt_email)) {
                    Toast.makeText(getActivity(), "Empty Field!!!", Toast.LENGTH_SHORT).show();
                } else {
                    updateEmail();
                }
            }
        });

        return view;
    }

    public void updateEmail() {
        progressDialog.setMessage("Please wait a few moments...");
        progressDialog.show();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String txt_old_email = edOldEmail.getText().toString().trim();
        String txt_password = edPassword.getText().toString().trim();

        AuthCredential authCredential = EmailAuthProvider.getCredential(txt_old_email, txt_password);

        firebaseUser.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final String txt_new_email = edNewEmail.getText().toString().trim();
                firebaseUser.updateEmail(txt_new_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Email has been successfully updated, Verification Mail sent!!!", Toast.LENGTH_SHORT).show();
                                    // Update email
                                    final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getCurrentUser().getUid());
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                                            String txt_username = userProfile.getUsername();

                                            UserProfile userProfile1 = new UserProfile(txt_new_email, txt_username);
                                            databaseReference.setValue(userProfile1);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Failed to update Email!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed to update Email!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
