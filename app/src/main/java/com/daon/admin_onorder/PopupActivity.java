package com.daon.admin_onorder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class PopupActivity extends AppCompatActivity {
    TextView title;
    TextView body;
    String str_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_popup);
        title = findViewById(R.id.popup_title);
        body = findViewById(R.id.pupop_body);
        Intent intent = getIntent();
        String str_title = intent.getStringExtra("title");
        String str_body = intent.getStringExtra("body");
        str_type = intent.getStringExtra("type");
        title.setText(str_title);
        body.setText(str_body);

    }
    public void mOnClose(View v){
        if (str_type.equals("service")){
            Intent intent_ = new Intent(this, ServiceActivity.class);
            startActivity(intent_);
            finish();
        }else{
            Intent intent_ = new Intent(this, OrderActivity.class);
            startActivity(intent_);
            finish();
        }

    }
    public void mOffClose(View v){
        finish();

    }
}