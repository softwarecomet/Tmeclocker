package com.adolfo.timeclock.utils;

import com.adolfo.timeclock.models.CompanyModel;
import com.adolfo.timeclock.models.EmployeeModel;
import com.adolfo.timeclock.models.ProjectModel;

import java.util.ArrayList;
import java.util.List;

public class AppConstants {

    public static String httpURL = "http://";
    public static String APIURL = "/clientes/flexio/API_Flexio";
    public static String BaseURL = ""; // http://192.237.163.118/clientes/flexio/API_Flexio
    public static String token = "";
    public static String currentUserName = "";
    public static String userID = "";
    public static List<CompanyModel> companyArr = new ArrayList<>();

    public static List<EmployeeModel> employees = new ArrayList<>();
    public static List<ProjectModel> projects = new ArrayList<>();
}
