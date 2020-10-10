package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ViewNotes extends AppCompatActivity {
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView content = findViewById(R.id.txt_content_edit_notes);
        TextView title = findViewById(R.id.txt_title_edit_notes);
        TextView date = findViewById(R.id.txt_date_created_view_notes);
        content.setMovementMethod(new ScrollingMovementMethod());

        data = getIntent();

        content.setText(data.getStringExtra("content"));
        title.setText(data.getStringExtra("title"));
        date.setText(data.getStringExtra("created"));

        FloatingActionButton fab = findViewById(R.id.fab_edit_notes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewNotes.this, EditNotes.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("noteID", data.getStringExtra("noteID"));
                intent.putExtra("created", data.getStringExtra("created"));
                intent.putExtra("userId", data.getStringExtra("userId"));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
