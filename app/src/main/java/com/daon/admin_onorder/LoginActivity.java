package com.daon.admin_onorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.sam4s.io.OnConnectListener;
import com.sam4s.io.ethernet.SocketInfo;
import com.sam4s.printer.Sam4sBuilder;
import com.sam4s.printer.Sam4sFinder;
import com.sam4s.printer.Sam4sPrint;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    ScheduledExecutorService scheduler;
    ScheduledFuture<?> future;
    Handler handler = new Handler();
    String[] deviceList = null;
    Sam4sFinder ef = new Sam4sFinder();
    final static int DISCOVERY_INTERVAL = 500;
    EditText edit_id;
    EditText edit_pass;
    AdminApplication app;
    RelativeLayout loginBtn;
    String fcm_id;
    String TAG = "daon_test";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Sam4sPrint printer = new Sam4sPrint();
    Sam4sPrint printer2 = new Sam4sPrint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        editor.putString("storename", "돈내코순두부한림점");
        editor.putString("storecode", "dncsh");
        editor.commit();
        app = new AdminApplication();
        edit_id = findViewById(R.id.edit_id);
        edit_pass = findViewById(R.id.edit_pw);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        fcm_id = token;
                        // Log and toast
                        String msg = token;
                        Log.d(TAG, msg);
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        try {
            try {
                printer.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.20.191", 9100);
                printer.resetPrinter();
                printer2.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.20.193", 9100);
                printer2.resetPrinter();
//                printer.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.20.33", 9100);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("daon", "print error = "+e.getMessage());
            }

            if (!printer.IsConnected(Sam4sPrint.DEVTYPE_ETHERNET)){
                try {

                    Log.d("daon", "print error = "+printer.getPrinterStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    Log.d("daon", "aaa = "+printer.getPrinterStatus());
                    app.setPrinter(printer);
                    app.setPrinter2(printer2);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loginBtn = findViewById(R.id.loginactivity_btn_login);
//        loginBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//
//
//            }
//        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    setFcm(fcm_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setFcm(String fcm_id) throws Exception {
        Log.d("daon_test", "printer1 = "+printer.getPrinterStatus());
        Log.d("daon_test", "printer2 = "+printer2.getPrinterStatus());
        if (printer.getPrinterStatus() != null) {
            String str_id = "dncsh";
            String str_pass = "1234";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://15.164.232.164:5000/")
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

            interfaceApi.setFcm(str_id, str_pass, fcm_id).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        if (String.valueOf(response.body().get("StatusCode")).equals("200")) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("daon", "fail = " + t.getMessage());
                }
            });
        }else{
            setPrinter();
        }
    }
    public void setPrinter(){
        app = new AdminApplication();
        try {
            try {
                printer.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.0.38", 9100);
                printer.resetPrinter();
                printer2.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "192.168.0.37", 9100);
                printer2.resetPrinter();
                Log.d("daon_test", "printer1 = "+printer.getPrinterStatus());
                Log.d("daon_test", "printer2 = "+printer2.getPrinterStatus());

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("daon", "print error = "+e.getMessage());
            }

            if (!printer.IsConnected(Sam4sPrint.DEVTYPE_ETHERNET)){
                try {
                    Log.d("daon", "print error = "+printer.getPrinterStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                app.setPrinter(printer);
                app.setPrinter2(printer2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}