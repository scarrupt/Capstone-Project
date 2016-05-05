package com.codefactoring.android.backlogapi.models;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Issue {

    private long id;
    private long projectId;
    private String issueKey;
    private long keyId;
    private IssueType issueType;
    private String summary;
    private String description;
    private Priority priority;
    private Status status;
    private User assignee;
    private List<Milestone> milestone = new ArrayList<>();
    private User createdUser;
    private Date created;
    private User updatedUser;
    private Date updated;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public List<Milestone> getMilestone() {
        return milestone;
    }

    public void setMilestone(List<Milestone> milestone) {
        this.milestone = milestone;
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

    public User getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(User updatedUser) {
        this.updatedUser = updatedUser;
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
        Issue issue = (Issue) o;
        return Objects.equal(keyId, issue.keyId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(keyId);
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", issueKey='" + issueKey + '\'' +
                ", keyId=" + keyId +
                ", issueType=" + issueType +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", assignee=" + assignee +
                ", milestone=" + milestone +
                ", createdUser=" + createdUser +
                ", created=" + created +
                ", updatedUser=" + updatedUser +
                ", updated=" + updated +
                '}';
    }
}