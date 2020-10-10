package com.example.notes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

public class EditNotes extends AppCompatActivity {
    Intent data;
    TextView dateCreated;
    EditText editTitle, editContent;
    Toolbar toolbar;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        data = getIntent();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        dateCreated = findViewById(R.id.txt_date_created_edit_notes);
        editTitle = findViewById(R.id.ed_title_edit_notes);
        editContent = findViewById(R.id.ed_content_edit_notes);

        String txt_date = data.getStringExtra("created");
        String txt_title = data.getStringExtra("title");
        String txt_content = data.getStringExtra("content");

        dateCreated.setText(txt_date);
        editTitle.setText(txt_title);
        editContent.setText(txt_content);

        FloatingActionButton fab = findViewById(R.id.fab_done_edit_notes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txt_title = editTitle.getText().toString().trim();
                String txt_content = editContent.getText().toString().trim();

                if (TextUtils.isEmpty(txt_content)) {
                    Toast.makeText(EditNotes.this, "Please enter note details!!!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(txt_title)) {
                    editTitle.setText("Untitled");
                    changeNote();
                } else {
                    changeNote();
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
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeNote() {
        progressDialog.setMessage("Please wait a few moments...");
        progressDialog.show();

        String txt_title = editTitle.getText().toString().trim();
        String txt_content = editContent.getText().toString().trim();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference = firebaseFirestore
                .collection("Notes").document(data.getStringExtra("noteID"));

        Map<String, Object> note = new HashMap<>();
        note.put("title", txt_title);
        note.put("content", txt_content);
        note.put("created", new Timestamp(new Date()));

        documentReference.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(EditNotes.this, "Updated successfully!!!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(EditNotes.this, HomePageActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(EditNotes.this, "Failed to update note, try again!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
