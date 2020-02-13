package com.adolfo.timeclock.models;

public class ProjectModel {

    private String projectID;
    private String projectName;

    public ProjectModel(String projectID, String projectName) {
        this.projectID = projectID;
        this.projectName = projectName;
    }

    public String getProjectID() { return projectID; }

    public void setProjectID(String projectID) { this.projectID = projectID; }

    public String getProjectName() { return projectName; }

    public void setProjectName(String projectName) { this.projectName = projectName; }
}
