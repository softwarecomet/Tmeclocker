package com.adolfo.timeclock.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adolfo.timeclock.R;
import com.adolfo.timeclock.apiServices.APIServices;
import com.adolfo.timeclock.apiServices.CheckAPIService;
import com.adolfo.timeclock.utils.AppConstants;
import com.adolfo.timeclock.utils.CoreApp;
import com.adolfo.timeclock.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigurationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ConfigurationActivity.class.getSimpleName();

    private EditText edt_servidor, edt_empresa, edt_proyecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        edt_servidor = findViewById(R.id.edt_servidor);
        edt_empresa = findViewById(R.id.edt_empresa);
        edt_proyecto = findViewById(R.id.edt_proyecto);
        TextView tv_save = findViewById(R.id.tv_save);
        ImageView img_logout = findViewById(R.id.img_logout);

        tv_save.setOnClickListener(this);
        img_logout.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        edt_servidor.setText("");
        edt_empresa.setText("");
        edt_proyecto.setText("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save :
                if (edt_servidor.getText().toString().equals("")) {
                    Toast.makeText(this, "Por favor ingrese el servidor", Toast.LENGTH_LONG).show();
                    return;
                }

                if (edt_empresa.getText().toString().equals("")) {
                    Toast.makeText(this, "Por favor ingrese el nombre de la empresa", Toast.LENGTH_LONG).show();
                    return;
                }

                if (edt_proyecto.getText().toString().equals("")) {
                    Toast.makeText(this, "Por favor ingrese el nombre del proyecto", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d(TAG, "Save the configuration data");

                String serverUrl = AppConstants.httpURL + edt_servidor.getText().toString() + AppConstants.APIURL + APIServices.LOGIN;
                if (CoreApp.isNetworkConnection(this)) {
                    new CheckAPIService(this, serverUrl, "Test", "Test", new CheckAPIService.OnResultReceived() {
                        @Override
                        public void onResult(String result) {
                            if (!result.equals(APIServices.RESPONSE_UNWANTED)) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    AppConstants.BaseURL = AppConstants.httpURL + edt_servidor.getText().toString() + AppConstants.APIURL;
                                    if (object.getString("success").equals("true")) {
                                        Preferences.setValue(ConfigurationActivity.this, Preferences.SERVER_URL, AppConstants.BaseURL);
                                        finish();
                                    } else {
                                        Preferences.setValue(ConfigurationActivity.this, Preferences.SERVER_URL, AppConstants.BaseURL);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ConfigurationActivity.this, "Por favor introduzca el servidor correcto", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(ConfigurationActivity.this, "Por favor introduzca el servidor correcto", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, true).execute();
                } else {
                    Toast.makeText(this, "No internet connection available", Toast.LENGTH_LONG).show();
                }

                finish();
                break;
            case R.id.img_logout:
                Log.d(TAG, "Log out");
                Preferences.setValue(this, Preferences.isLogin, false);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }
}
