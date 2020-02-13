package com.adolfo.timeclock.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.adolfo.timeclock.R;
import com.adolfo.timeclock.models.CompanyModel;
import com.adolfo.timeclock.utils.AppConstants;
import com.adolfo.timeclock.utils.Preferences;

public class SplashActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if (Preferences.getValue_Boolean(SplashActivity.this, Preferences.isLogin, false)) {
                    AppConstants.BaseURL = Preferences.getValue_String(SplashActivity.this, Preferences.SERVER_URL);
                    AppConstants.token = Preferences.getValue_String(SplashActivity.this, Preferences.APIToken);
                    AppConstants.userID = Preferences.getValue_String(SplashActivity.this, Preferences.UserID);
                    AppConstants.currentUserName = Preferences.getValue_String(SplashActivity.this, Preferences.UserName);
                    AppConstants.companyArr.clear();
                    String[] companyNameArr = Preferences.loadArray(SplashActivity.this, Preferences.CompanyNameArr);
                    String[] companyIDArr = Preferences.loadArray(SplashActivity.this, Preferences.CompanyIDArr);
                    for (int i = 0; i < companyIDArr.length; i++) {
                        String companyName = companyNameArr[i];
                        String companyID = companyIDArr[i];
                        AppConstants.companyArr.add(new CompanyModel(companyName, companyID));
                    }

                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }
        }, 3000);
    }
}
