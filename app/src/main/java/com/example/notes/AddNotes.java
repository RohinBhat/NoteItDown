package com.example.notes;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNotes extends AppCompatActivity {

    private static final String TAG = "Add Notes";
    FirebaseFirestore firebaseFirestore;
    EditText edContent, edTitle;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        edContent = findViewById(R.id.ed_content_add_notes);
        edTitle = findViewById(R.id.ed_title_add_notes);
        progressDialog = new ProgressDialog(this);

        FloatingActionButton fab = findViewById(R.id.fab_done_add_notes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_title = edTitle.getText().toString().trim();
                String txt_content = edContent.getText().toString().trim();

                if (TextUtils.isEmpty(txt_content)) {
                    Toast.makeText(AddNotes.this, "Please enter note details!!!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(txt_title)) {
                    edTitle.setText("Untitled");
                    addNote();
                } else {
                    addNote();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_add_notes_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_close) {
            Toast.makeText(this, "Not Saved", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNote() {
        progressDialog.setMessage("Please wait a few moments...");
        progressDialog.show();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String txt_title = edTitle.getText().toString().trim();
        String txt_content = edContent.getText().toString().trim();
        Timestamp timestamp = new Timestamp(new Date());
        String txt_userId = firebaseUser.getUid();

        DocumentReference documentReference = firebaseFirestore
                .collection("Notes").document();
        Log.d(TAG, "addNote: Successful");

        Map<String, Object> note = new HashMap<>();
        note.put("title", txt_title);
        note.put("content", txt_content);
        note.put("created", timestamp);
        note.put("userId", txt_userId);

        documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(AddNotes.this, "Added successfully!!!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddNotes.this, "Failed to add note, try again!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
