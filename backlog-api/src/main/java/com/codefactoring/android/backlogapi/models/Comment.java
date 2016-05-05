package com.codefactoring.android.backlogapi.models;

import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.Date;

public class Comment {

    private long id;
    private String content;
    private ChangeLog[] changeLog;
    private User createdUser;
    private Date created;
    private Date updated;

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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equal(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
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