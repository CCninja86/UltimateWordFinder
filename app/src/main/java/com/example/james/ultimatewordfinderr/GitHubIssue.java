package com.example.james.ultimatewordfinderr;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GitHubIssue {

    private String title;
    private String description;
    private int milestone;
    private String[] labels;
    private String[] assignees;
    private String priority;

    public GitHubIssue(String title, String description, String priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public GitHubIssue() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMilestone() {
        return milestone;
    }

    public void setMilestone(int milestone) {
        this.milestone = milestone;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    public String[] getAssignees() {
        return assignees;
    }

    public void setAssignees(String[] assignees) {
        this.assignees = assignees;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String toJson() {
        Gson gson = new Gson();

        return gson.toJson(this, new TypeToken<GitHubIssue>() {
        }.getType());
    }
}
