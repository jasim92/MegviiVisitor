package com.example.megviivisitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.riversun.okhttp3.OkHttp3CookieHelper;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdminLogin extends AppCompatActivity{
    EditText emailTxt, passwordTxt, serverTxt;
    Button login;
    public static String email,password;
    public static String serverip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        emailTxt = findViewById(R.id.login_email);
        passwordTxt = findViewById(R.id.login_password);
        serverTxt = findViewById(R.id.server);

        login = findViewById(R.id.admin_panel_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = emailTxt.getText().toString();
                password = passwordTxt.getText().toString();
                serverip = serverTxt.getText().toString();
                String url = "http://" + serverip + "/auth/login";
                new LoginResponse(AdminLogin.this).execute(url);
            }
        });
    }

    public class LoginResponse extends AsyncTask<String,Void, JSONArray>
    {
        private ProgressDialog progressDialog;
        public LoginResponse(AdminLogin adminLogin)
        {
            progressDialog = new ProgressDialog(adminLogin);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Signing In");
            progressDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... urls) {
            String url = urls[0];
            JSONArray result = null;
            try {
                result = NetworkUtil.getLoginArrayResponse(url);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            Log.e("Login",": logged in successfully");
            Toast.makeText(getApplicationContext(),"Logged in successfully",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminLogin.this,VisitorActivity.class);
            startActivity(intent);
            if (progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
        }
    }

}
