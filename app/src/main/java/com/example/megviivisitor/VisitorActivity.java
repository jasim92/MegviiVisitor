package com.example.megviivisitor;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

public class VisitorActivity extends AppCompatActivity {
    Button b1, b2, browse, add, cam;
    ImageView imageView;
    Uri filePath;
    Bitmap bitmap;
    EditText name;
    public static String imageFilePath;

    public static long ts1, ts2;

    public static String visitor_name;
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        b1 = findViewById(R.id.start_time);
        b2 = findViewById(R.id.end_time);
        browse = findViewById(R.id.browse);
//        cam = findViewById(R.id.camera);
        add = findViewById(R.id.add_visitor);
        imageView = findViewById(R.id.imageView);
        name = findViewById(R.id.name);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                visitor_name = name.getText().toString();
                String start_time = String.valueOf(ts1);
                String end_time = String.valueOf(ts2);
                Log.e("inside: ", start_time);
                Log.e("inside: ", end_time);
                String url = "http://" + AdminLogin.serverip + "/subject/file";
                imageFilePath = getRealPathFromURI(filePath);
                new VisitorResponse(VisitorActivity.this).execute(url);
            }
        });

//        cam.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Dexter.withActivity(VisitorActivity.this)
//                        .withPermission(Manifest.permission.CAMERA)
//                        .withListener(new PermissionListener() {
//                            @Override
//                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//
//                            }
//
//                            @Override
//                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//
//                            }
//
//                            @Override
//                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//                                permissionToken.continuePermissionRequest();
//
//                            }
//                        }).check();
//            }
//        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(VisitorActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Please Select Image "), 1); //IT WILL CREATE A CHOOSER
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();

                            }
                        }).check();
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(VisitorActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myYear = year;
                        myMonth = month + 1;
                        myday = dayOfMonth;
                        Calendar c = Calendar.getInstance();
                        hour = c.get(Calendar.HOUR);
                        minute = c.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(VisitorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                myHour = hourOfDay;
                                myMinute = minute;
                                StringBuilder s1 = new StringBuilder().append(myday).append("/")
                                        .append(myMonth).append("/").append(myYear).append(" ").append(myHour).append(":").append(myMinute);
                                b1.setText(s1);
                                try {
                                    ts1 = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").parse(String.valueOf(s1)).getTime() / 1000;
                                    Log.e("ts1 : ", String.valueOf(ts1));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, hour, minute, DateFormat.is24HourFormat(getApplicationContext()));
                        timePickerDialog.show();

                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(VisitorActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myYear = year;
                        myMonth = month + 1;
                        myday = dayOfMonth;
                        Calendar c = Calendar.getInstance();
                        hour = c.get(Calendar.HOUR);
                        minute = c.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(VisitorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                myHour = hourOfDay;
                                myMinute = minute;
                                StringBuilder s2 = new StringBuilder().append(myday).append("/")
                                        .append(myMonth).append("/").append(myYear).append(" ").append(myHour).append(":").append(myMinute);
                                b2.setText(s2);
                                try {
                                    ts2 = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").parse(String.valueOf(s2)).getTime() / 1000;
                                    Log.e("ts2 : ", String.valueOf(ts2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, hour, minute, DateFormat.is24HourFormat(getApplicationContext()));
                        timePickerDialog.show();
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath); //GIVING FILEPATH TO INPUT STREAM
                bitmap = BitmapFactory.decodeStream(inputStream); //DECODING INPUT STREAM INTO BITMAP
                imageView.setImageBitmap(bitmap); //SETTING BITMAP IMAGE AS A IMAGE VIEW
                imageFilePath = getRealPathFromURI(filePath);
                String url = "http://" + AdminLogin.serverip + "/subject/photo/check";
                new PhotoChecker(VisitorActivity.this).execute(url);
            } catch (Exception e) {

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        Log.e("Path", result);
        return result;
    }


    class VisitorResponse extends AsyncTask<String, Void, JSONArray> {
        ProgressDialog progressDialog;

        public VisitorResponse(VisitorActivity visitorActivity) {
            progressDialog = new ProgressDialog(visitorActivity);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Adding Visitor");
            progressDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... urls) {
            String url = urls[0];
            JSONArray result = null;
            try {
                result = NetworkUtil.getVisitorArrayResponse(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            Log.e("Login", ": added");
            Toast.makeText(getApplicationContext(), "added", Toast.LENGTH_SHORT).show();
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }

    }

    class PhotoChecker extends AsyncTask<String, Void, JSONObject> {
        ProgressDialog progressDialog;

        public PhotoChecker(VisitorActivity visitorActivity) {
            progressDialog = new ProgressDialog(visitorActivity);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Checking Photo Quality");
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            String url = urls[0];
            JSONObject result = null;
            try {
                result = NetworkUtil.getPhotoCheckResponse(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (NetworkUtil.Rcode==0)
            {
                Log.e("Photo", ": checked");
                Toast.makeText(getApplicationContext(), "Photo quality is  good", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Photo quality is not good", Toast.LENGTH_LONG).show();
            }

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    }
