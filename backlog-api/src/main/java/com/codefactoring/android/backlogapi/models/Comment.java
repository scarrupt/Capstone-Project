package com.codefactoring.android.backlogapi.models;

import java.util.Arrays;
import java.util.Objects;

public class Comment {

    private long id;
    private String content;
    private ChangeLog[] changeLog;
    private User createdUser;
    private String created;
    private String updated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ChangeLog[] getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(ChangeLog[] changeLog) {
        this.changeLog = changeLog;
    }

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", changeLog='" + Arrays.toString(changeLog) + '\'' +
                ", createdUser=" + createdUser +
                ", created='" + created + '\'' +
                ", updated='" + updated + '\'' +
                '}';
    }
}