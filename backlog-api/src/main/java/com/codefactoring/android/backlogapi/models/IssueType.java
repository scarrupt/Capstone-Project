package com.codefactoring.android.backlogapi.models;

import java.util.Objects;

public class IssueType {

    private long id;
    private long projectId;
    private String name;
    private String color;
    private long displayOrder;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(long displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueType issueType = (IssueType) o;
        return Objects.equals(id, issueType.id) &&
                Objects.equals(projectId, issueType.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectId);
    }

    @Override
    public String toString() {
        return "IssueType{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", displayOrder=" + displayOrder +
                '}';
    }
}