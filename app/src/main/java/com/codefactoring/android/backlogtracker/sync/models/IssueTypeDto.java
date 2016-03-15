package com.codefactoring.android.backlogtracker.sync.models;

import java.util.Objects;

public class IssueTypeDto {

    private long id;
    private long projectId;
    private String name;
    private String color;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueTypeDto that = (IssueTypeDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(projectId, that.projectId);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectId);
    }
}