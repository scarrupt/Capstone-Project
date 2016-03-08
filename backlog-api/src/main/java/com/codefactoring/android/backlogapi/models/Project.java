package com.codefactoring.android.backlogapi.models;

import java.util.Objects;

public class Project {

    private long id;
    private String projectKey;
    private String name;
    private boolean chartEnabled;
    private boolean subtaskingEnabled;
    private boolean projectLeaderCanEditProjectLeader;
    private String textFormattingRule;
    private boolean archived;

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

    public boolean isChartEnabled() {
        return chartEnabled;
    }

    public void setChartEnabled(boolean chartEnabled) {
        this.chartEnabled = chartEnabled;
    }

    public boolean isSubtaskingEnabled() {
        return subtaskingEnabled;
    }

    public void setSubtaskingEnabled(boolean subtaskingEnabled) {
        this.subtaskingEnabled = subtaskingEnabled;
    }

    public boolean isProjectLeaderCanEditProjectLeader() {
        return projectLeaderCanEditProjectLeader;
    }

    public void setProjectLeaderCanEditProjectLeader(boolean projectLeaderCanEditProjectLeader) {
        this.projectLeaderCanEditProjectLeader = projectLeaderCanEditProjectLeader;
    }

    public String getTextFormattingRule() {
        return textFormattingRule;
    }

    public void setTextFormattingRule(String textFormattingRule) {
        this.textFormattingRule = textFormattingRule;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(projectKey, project.projectKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectKey);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", projectKey='" + projectKey + '\'' +
                ", name='" + name + '\'' +
                ", chartEnabled=" + chartEnabled +
                ", subtaskingEnabled=" + subtaskingEnabled +
                ", projectLeaderCanEditProjectLeader=" + projectLeaderCanEditProjectLeader +
                ", textFormattingRule='" + textFormattingRule + '\'' +
                ", archived=" + archived +
                '}';
    }
}