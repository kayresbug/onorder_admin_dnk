package com.daon.admin_onorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.daon.admin_onorder.model.OrderModel;
import com.daon.admin_onorder.model.PrintOrderModel;
import com.daon.admin_onorder.model.ServiceModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.sam4s.printer.Sam4sBuilder;
import com.sam4s.printer.Sam4sPrint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ServiceModel> serviceModels = new ArrayList<>();
    ServiceAdapter adapter;
    private OrderDialog orderDialog;
    Context context;
    ImageView bottom_home;
    ImageView bottom_service;
    ImageView bottom_order;
    ImageView bottom_payment;
    String time;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        pref = getSharedPreferences("pref", MODE_PRIVATE);

        bottom_home = findViewById(R.id.service_bottom_menu1);
        bottom_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ServiceActivity.this, MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });
        bottom_order = findViewById(R.id.service_bottom_menu3);
        bottom_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceActivity.this, OrderActivity.class);
                startActivity(intent);
                finish();
            }
        });
        bottom_service = findViewById(R.id.service_bottom_menu2);
        bottom_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ServiceActivity.this, ServiceActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
        bottom_payment = findViewById(R.id.service_bottom_menu4);
        bottom_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ServiceActivity.this, "준비중 입니다.", Toast.LENGTH_SHORT).show();

            }
        });

        context = this;
        recyclerView = findViewById(R.id.serviceactivity_recycler_body);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

//

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.164.232.164:5000/")
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

        interfaceApi.getService(pref.getString("storecode", "")).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d("daon", "aaabbb = "+response.isSuccessful());

                if (response.isSuccessful()) {

                    JsonArray jsonArray = response.body();
                    ArrayList<ServiceModel> temp1 = new ArrayList<>();
                    ArrayList<ServiceModel> temp2 = new ArrayList<>();

                    for (int i = 0; i < jsonArray.size(); i++){

                        String obj = String.valueOf(jsonArray.get(i));
                        obj = obj.replace("[","");
                        obj = obj.replace("]","");
                        obj = obj.replace("\\","");
                        obj = obj.substring(1, obj.length()-1);

                        JSONObject strObj = null;
                        try {
                            strObj = new JSONObject(obj);
                            long dateval‍ue  = 1265110426299L;


                            Date date1 = new Date(Long.parseLong((String) strObj.get("inputtime")));

                            SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd \nhh:mm", Locale.getDefault());

                            String date22 =  datef.format(date1) ;
                            Log.d("daon", "time = "+date22);
                            ServiceModel serviceModel = new ServiceModel(strObj.get("num").toString(), strObj.get("tableno").toString(), strObj.get("body").toString(), strObj.get("status").toString(),date22);
                            if (serviceModel.getStatus().equals("접수대기")){
                                temp1.add(serviceModel);
                            }else if (serviceModel.getStatus().equals("접수완료")){
                                temp2.add(serviceModel);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    serviceModels.addAll(temp1);
                    serviceModels.addAll(temp2);
                    adapter = new ServiceAdapter(context, serviceModels);
                    recyclerView.setAdapter(adapter);
                }else{

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.d("daon", "fail = "+t.getMessage());

            }
        });
        BackThread thread = new BackThread();  // 작업스레드 생성
        thread.setDaemon(true);  // 메인스레드와 종료 동기화
        thread.start();
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",  Locale.getDefault());
//        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());
//
//        time = format2.format(calendar.getTime());
//        String time2 = format2.format(calendar.getTime());
//
//        FirebaseDatabase.getInstance().getReference().child("order").child(pref.getString("storename", "")).child(time).addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot item : snapshot.getChildren()) {
//                    PrintOrderModel printOrderModel = item.getValue(PrintOrderModel.class);
//                    if (printOrderModel.getPrintStatus().equals("x")) {
//                        print(printOrderModel);
//                        printOrderModel.setPrintStatus("o");
//                        FirebaseDatabase.getInstance().getReference().child("order").child(pref.getString("storename","")).child(time).child(item.getKey()).setValue(printOrderModel);
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        FirebaseDatabase.getInstance().getReference().child("service").child(pref.getString("storename", "")).child(time).addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot item : snapshot.getChildren()) {
//                    PrintOrderModel printOrderModel = item.getValue(PrintOrderModel.class);
//                    if (printOrderModel.getPrintStatus().equals("x")) {
//                        print(printOrderModel);
//                        printOrderModel.setPrintStatus("o");
//                        FirebaseDatabase.getInstance().getReference().child("service").child(pref.getString("storename","")).child(time).child(item.getKey()).setValue(printOrderModel);
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(ServiceActivity.this, MainActivity.class);
//        startActivity(intent);
        finish();
    }


//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }

    class BackThread extends Thread{  // Thread 를 상속받은 작업스레드 생성
        @Override
        public void run() {
            while (true) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd",  Locale.getDefault());

                time = format2.format(calendar.getTime());
                Log.d("daon_test", "time = "+time);
                try {
                    Thread.sleep(60000);   // 1000ms, 즉 1초 단위로 작업스레드 실행
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}