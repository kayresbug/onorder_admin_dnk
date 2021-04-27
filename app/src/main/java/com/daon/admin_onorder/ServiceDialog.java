package com.daon.admin_onorder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceDialog extends Dialog implements View.OnClickListener {

    private Button mPositiveButton;
    private Button mNegativeButton;
    private TextView service_body;
    private ServiceDialogListener _listener ;


    String body;
    String num;
    public ServiceDialog(@NonNull Context context, String body, String num) {
        super(context);
        this.body = body;
        this.num = num;
    }

    public interface ServiceDialogListener {
        public void onDismissListener(String s);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.service_dialog);

        service_body = findViewById(R.id.service_dialog_body);
        service_body.setText(body);

        mPositiveButton = findViewById(R.id.service_pbutton);
        mNegativeButton = findViewById(R.id.service_nbutton);
        mPositiveButton.setOnClickListener(this);
        mNegativeButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.service_pbutton:
                Log.d("daon", "pbutton");
                Toast.makeText(getContext(), "확인", Toast.LENGTH_SHORT).show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://15.164.232.164:5000/")
                        .addConverterFactory(new NullOnEmptyConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

                interfaceApi.updateService("krgg0002", num).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            Log.d("daon", String.valueOf(response.body().get("StatusCode")));
                            if (String.valueOf(response.body().get("StatusCode")).equals("\"200\"")){
                                _listener.onDismissListener("ok_dismiss");
                            }
                        }else{

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d("daon", "fail = "+t.getMessage());
                    }
                });
                break;

            case R.id.service_nbutton:
                Toast.makeText(getContext(), "취소", Toast.LENGTH_SHORT).show();
                _listener.onDismissListener("dismiss");
                break;
        }

    }

    public void setOnDismissListener(ServiceDialogListener listener ) {
        _listener = listener ;
    }


}
