package com.codefactoring.android.backlogtracker.sync.models;

import com.google.common.base.Objects;

public class ProjectDto {

    private long id;

    private String projectKey;

    private String name;

    private BacklogImage image;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BacklogImage getImage() {
        return image;
    }

    public void setImage(BacklogImage image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectDto that = (ProjectDto) o;
        return Objects.equal(projectKey, that.projectKey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(projectKey);
    }

    @Override
    public String toString() {
        return "ProjectDto{" +
                "id=" + id +
                ", projectKey='" + projectKey + '\'' +
                ", name='" + name + '\'' +
                ", image=" + image +
                '}';
    }
}
