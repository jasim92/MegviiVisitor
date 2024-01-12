package com.example.megviivisitor;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.http2.Header;

public class NetworkUtil{

    public static final String BASE_URL = "http://192.168.1.51/";
    public static final String LOGIN_URL = BASE_URL + "auth/login";
    public static final String ADD_VISITOR_URL = BASE_URL + "subject/file";

    public static List<String> CookieList;
    public  static String headerValue;
    public static int Rcode;

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static JSONArray getLoginArrayResponse(String url) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        AdminLogin adminLogin = new AdminLogin();
        try {
            Request.Builder builder = new Request.Builder();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody Rbody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("username",adminLogin.email)
                    .addFormDataPart("password",adminLogin.password)
                    .build();
            builder.url(url)
                    .method("POST", Rbody)
                    .addHeader("User-Agent", "Koala Admin")
                    .addHeader("Content-Type", "application/json");
            Request request = builder.build();

            try {
                Response response = okHttpClient.newCall(request).execute();

                // these lines will get cookie from response of header and we will take in header value variable and use it for subsequent calls
                CookieList = response.headers().values("Set-Cookie");
                Headers allHeaders = response.headers();
                 headerValue = allHeaders.get("Set-Cookie");
                Log.e("headers : ", headerValue);

                String body = response.body().string();
                JSONArray bodyJson = new JSONArray(body);

                return bodyJson;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } finally {
//            okHttpClient.disconnect();
        }
    }
    public static JSONArray getVisitorArrayResponse(String url) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        VisitorActivity visitorActivity = new VisitorActivity();
        Log.e("filepath : ", visitorActivity.imageFilePath);

        try {
            Request.Builder builder = new Request.Builder();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody Rbody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("subject_type","1")
                    .addFormDataPart("name",visitorActivity.visitor_name)
                    .addFormDataPart("start_time", String.valueOf(visitorActivity.ts1))
                    .addFormDataPart("end_time", String.valueOf(visitorActivity.ts2))
                    .addFormDataPart("photo",visitorActivity.imageFilePath,
                            RequestBody.create(mediaType.parse("application/octet-stream"),
                                    new File(visitorActivity.imageFilePath)))
                    .build();
            builder.url(url)
                    .method("POST", Rbody)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cookie", headerValue);
            Request request = builder.build();

            try {
                Response response = okHttpClient.newCall(request).execute();

                CookieList = response.headers().values("Set-Cookie");


                String body = response.body().string();
                JSONArray bodyJson = new JSONArray(body);

                return bodyJson;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } finally {
//            okHttpClient.disconnect();
        }
    }
    public static JSONObject getPhotoCheckResponse(String url) throws IOException
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        VisitorActivity visitorActivity = new VisitorActivity();
        Log.e("filepath : ", VisitorActivity.imageFilePath);

        try {
            Request.Builder builder = new Request.Builder();
            MediaType mediaType = MediaType.parse("multipart/form-data");
            RequestBody Rbody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("photo",visitorActivity.imageFilePath,
                            RequestBody.create(mediaType.parse("application/octet-stream"),
                                    new File(visitorActivity.imageFilePath)))
                    .build();
            builder.url(url)
                    .method("POST", Rbody)
                    .addHeader("Content-Type", "multipart/form-data")
                    .addHeader("Cookie", headerValue);
            Request request = builder.build();

            try {
                Response response = okHttpClient.newCall(request).execute();
                String body = response.body().string();
                JSONObject bodyJson = new JSONObject(body);
                Rcode = bodyJson.getInt("code");
                Log.e("photo : ", String.valueOf(Rcode));

                return bodyJson;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } finally {
//            okHttpClient.disconnect();
        }
    }

}
