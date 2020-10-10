package com.example.notes;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import model.Task;

public class TasksRecyclerAdapter extends FirestoreRecyclerAdapter<Task, TasksRecyclerAdapter.TaskViewHolder> {

    TaskListener taskListener;

    public TasksRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Task> options, TaskListener taskListener) {
        super(options);
        this.taskListener = taskListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull TasksRecyclerAdapter.TaskViewHolder holder, int position, @NonNull Task task) {
        final CharSequence txt_date = DateFormat.format("EEEE, MMM d, yyyy h:mm:ss a", task.getCreated().toDate());

        holder.title.setText(task.getTitle());
        holder.dateCreated.setText(txt_date.toString());
        holder.checkBox.setChecked(task.isCompleted());
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasks_layout, parent, false);
        return new TaskViewHolder(view);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, dateCreated;
        CheckBox checkBox;
        CardView mCardView;
        View view;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txt_title_task);
            dateCreated = itemView.findViewById(R.id.txt_date_created_tasks);
            checkBox = itemView.findViewById(R.id.checkbox);
            mCardView = itemView.findViewById(R.id.task_card);
            view = itemView;

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    Task task = getItem(getAdapterPosition());
                    if (task.isCompleted() != isChecked) {
                        taskListener.handleCheckChanged(isChecked, snapshot);
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Task task = getItem(getAdapterPosition());
                    if (!task.isCompleted()) {
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                        taskListener.handleEditTask(snapshot);
                    }
                }
            });
        }

        public void deleteItem() {
            taskListener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }

    interface TaskListener {
        public void handleCheckChanged(boolean isChecked, DocumentSnapshot snapshot);

        public void handleEditTask(DocumentSnapshot snapshot);

        public void handleDeleteItem(DocumentSnapshot snapshot);
    }
}
