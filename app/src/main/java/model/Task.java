package model;

import com.google.firebase.Timestamp;

public class Task {
    private String title;
    private boolean completed;
    private Timestamp created;
    private String userId;

    public Task() {
    }

    public Task(String title, boolean completed, Timestamp created, String userId) {
        this.title = title;
        this.completed = completed;
        this.created = created;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", completed=" + completed +
                ", created=" + created +
                '}';
    }
}
