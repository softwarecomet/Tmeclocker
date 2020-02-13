package com.adolfo.timeclock.apiServices;

import android.annotation.SuppressLint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class APIServices {

    public static final String RESPONSE_UNWANTED = "UNWANTED";
    public static final String LOGIN = "/login_master";
    public static final String GETEMPLOYEES = "/get_employees";
    public static final String GETPROJECTS = "/get_projects";
    public static final String SENDTIMESTAMP = "/send_timestamp";

    private static final String LINE_FEED = "\r\n";
    private static final String twoHyphens = "--";

    public static String POSTWithURL(String url, String emailStr, String passwordStr) throws IOException {
        trustEveryone();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        String boundary = "===" + System.currentTimeMillis() + "===";

        con.setRequestMethod("POST");
        con.addRequestProperty("Cache-Control", "no-cache");
        con.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        con.addRequestProperty("Accept", "application/json");
        con.setDoInput(true);
        con.setDoOutput(true);

        OutputStream outputStream = con.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        //first parameter - email
        writer.write(twoHyphens + boundary + LINE_FEED);
        writer.write("Content-Disposition: form-data; name=\"email\"" + LINE_FEED + LINE_FEED
                + emailStr + LINE_FEED);

        //second parameter - passwordStr
        writer.write(twoHyphens + boundary + LINE_FEED);
        writer.write("Content-Disposition: form-data; name=\"password\"" + LINE_FEED + LINE_FEED
                + passwordStr + LINE_FEED);
        writer.close();
        outputStream.close();

        //Retrieving Data
        BufferedReader bufferResponse;
        if (con.getResponseCode() / 100 == 2) {
            bufferResponse = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } else {
            bufferResponse = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        }

        String line;
        StringBuilder newResponse = new StringBuilder();
        while ((line = bufferResponse.readLine()) != null) {
            newResponse.append(line);
        }

        bufferResponse.close();
        return newResponse.toString();
    }

    private static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {

                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {

                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
