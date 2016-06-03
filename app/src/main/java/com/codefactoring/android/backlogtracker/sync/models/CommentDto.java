package com.codefactoring.android.backlogtracker.sync.models;

import com.google.common.base.Objects;

public class CommentDto {

    private long id;
    private long issueId;
    private String content;
    private long createdUserId;
    private String created;
    private String updated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIssueId() {
        return issueId;
    }

    public void setIssueId(long issueId) {
        this.issueId = issueId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(long createdUserId) {
        this.createdUserId = createdUserId;
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
        CommentDto that = (CommentDto) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CommentDto{" +
                "id=" + id +
                ", issueId=" + issueId +
                ", content='" + content + '\'' +
                ", createdUserId=" + createdUserId +
                ", created='" + created + '\'' +
                ", updated='" + updated + '\'' +
                '}';
    }
}
