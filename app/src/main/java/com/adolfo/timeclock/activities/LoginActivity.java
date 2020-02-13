package com.adolfo.timeclock.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adolfo.timeclock.R;
import com.adolfo.timeclock.apiServices.APIServices;
import com.adolfo.timeclock.apiServices.LoginAPIService;
import com.adolfo.timeclock.models.CompanyModel;
import com.adolfo.timeclock.utils.AppConstants;
import com.adolfo.timeclock.utils.CoreApp;
import com.adolfo.timeclock.utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText edt_user, edt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        edt_user = findViewById(R.id.edt_user);
        edt_password = findViewById(R.id.edt_password);
        TextView tv_login = findViewById(R.id.tv_login);
        ImageView img_config = findViewById(R.id.img_config);

        edt_user.setText("rrosas@pensanomica.com");
        edt_password.setText("12345");
        tv_login.setOnClickListener(this);
        img_config.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login :
                if (edt_user.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter the Username", Toast.LENGTH_LONG).show();
                    return;
                }
                if (edt_password.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter the Password", Toast.LENGTH_LONG).show();
                    return;
                }

                if (Preferences.getValue_String(this, Preferences.SERVER_URL).equals("")) {
                    Toast.makeText(this, "No se ha configurado un servidor.\\n Configurar un servidor antes de iniciar.", Toast.LENGTH_LONG).show();
                    return;
                }

                AppConstants.BaseURL = Preferences.getValue_String(this, Preferences.SERVER_URL);

                if (CoreApp.isNetworkConnection(this)) {
                    new LoginAPIService(this, edt_user.getText().toString(), edt_password.getText().toString(), new LoginAPIService.OnResultReceived() {
                        @Override
                        public void onResult(String result) {
                            if (!result.equals(APIServices.RESPONSE_UNWANTED)) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    if (object.getString("success").equals("true")) {
                                        Log.d(TAG, "Log In");
                                        String dataStr = object.getString("data");
                                        JSONObject dataObject = new JSONObject(dataStr);
                                        AppConstants.token = dataObject.getString("api_token");
                                        JSONObject userObject = dataObject.getJSONObject("user");
                                        AppConstants.userID = userObject.getString("id");
                                        AppConstants.currentUserName = userObject.getString("nombre") + " " + userObject.getString("apellido");
                                        JSONArray jsonArray = dataObject.getJSONArray("empresas");
                                        AppConstants.companyArr.clear();
                                        if (jsonArray.length() > 0) {
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject companyObject = jsonArray.getJSONObject(i);
                                                AppConstants.companyArr.add(new CompanyModel(companyObject.getString("empresa_id"), companyObject.getString("nombre_empresa")));
                                            }
                                        }

                                        String[] companyNameArr = new String[AppConstants.companyArr.size()];
                                        String[] companyIDArr = new String[AppConstants.companyArr.size()];
                                        for (int i = 0; i < AppConstants.companyArr.size(); i++) {
                                            CompanyModel companyModel = AppConstants.companyArr.get(i);
                                            companyNameArr[i] = companyModel.getCompanyName();
                                            companyIDArr[i] = companyModel.getCompanyID();
                                        }

                                        Preferences.setValue(LoginActivity.this, Preferences.isLogin, true);
                                        Preferences.setValue(LoginActivity.this, Preferences.APIToken, AppConstants.token);
                                        Preferences.setValue(LoginActivity.this, Preferences.UserID, AppConstants.userID);
                                        Preferences.setValue(LoginActivity.this, Preferences.UserName, AppConstants.currentUserName);
                                        Preferences.saveArray(LoginActivity.this, companyNameArr, Preferences.CompanyNameArr);
                                        Preferences.saveArray(LoginActivity.this, companyIDArr, Preferences.CompanyIDArr);


                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "No server connection available. Please try again later.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, true).execute();
                } else {
                    Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.img_config:
                Log.d(TAG, "Configuration");
                startActivity(new Intent(this, ConfigurationActivity.class));
                break;
        }
    }
}
