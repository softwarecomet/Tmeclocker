package com.adolfo.timeclock.models;

import android.graphics.Bitmap;

public class EmployeeModel {

    private String employeeID;
    private String employName;
    private String nationalID;
    private Bitmap picture;

    public EmployeeModel(String employeeID, String employName, String nationalID, Bitmap picture) {
        this.employeeID = employeeID;
        this.employName = employName;
        this.nationalID = nationalID;
        this.picture = picture;
    }

    public String getEmployeeID() { return employeeID; }

    public void setEmployeeID(String employeeID) { this.employeeID = employeeID; }

    public String getEmployName() { return employName; }

    public void setEmployName(String employName) { this.employName = employName; }

    public String getNationalID() { return nationalID; }

    public void setNationalID(String nationalID) { this.nationalID = nationalID; }

    public Bitmap getPicture() { return picture; }

    public void setPicture(Bitmap picture) { this.picture = picture; }
}
