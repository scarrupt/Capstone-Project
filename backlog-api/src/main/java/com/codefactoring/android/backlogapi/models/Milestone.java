package com.codefactoring.android.backlogapi.models;

import com.google.common.base.Objects;

import java.util.Date;

public class Milestone {

    private long id;
    private long projectId;
    private String name;
    private String description;
    private Date startDate;
    private Date releaseDueDate;
    private boolean archived;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getReleaseDueDate() {
        return releaseDueDate;
    }

    public void setReleaseDueDate(Date releaseDueDate) {
        this.releaseDueDate = releaseDueDate;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
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
        Milestone milestone = (Milestone) o;
        return Objects.equal(id, milestone.id) &&
                Objects.equal(projectId, milestone.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, projectId);
    }

    @Override
    public String toString() {
        return "Milestone{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", releaseDueDate=" + releaseDueDate +
                ", archived=" + archived +
                ", displayOrder=" + displayOrder +
                '}';
    }
}