package com.study.application.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.study.application.R;
import com.study.application.scanner.ScanQrCodeActivity;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    public static Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mContext = this;
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent();

            bundle.putString("TARGET", "USER");
            intent.putExtras(bundle);
            intent.setClass(this, ScanQrCodeActivity.class);

            startActivity(intent);

        });
    }
}
