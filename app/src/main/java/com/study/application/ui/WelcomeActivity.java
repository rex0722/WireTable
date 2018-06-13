package com.study.application.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.study.application.R;

public class WelcomeActivity extends AppCompatActivity {

    private final String TAG = "WelcomeActivity";

    public static String userName;
    private TextView welcomeTxt;
    private ImageView userImg;
    private boolean isKnownUser = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initView();
        setWelcomeTxt();
        setUserImg();
        setCounter();
    }

    private void initView() {
        welcomeTxt = findViewById(R.id.welcomeTxt);
        userImg = findViewById(R.id.userImg);
    }

    private void setCounter() {
        Handler countHandler = new Handler();

        Runnable runnable = () -> {
            if (isKnownUser) {
                Intent intent = new Intent();
                intent.setClass(this, FunctionActivity.class);
                startActivity(intent);
            }
            WelcomeActivity.this.finish();

        };
        countHandler.postDelayed(runnable, 3000);
    }

    private void setWelcomeTxt() {
        Bundle bundle = getIntent().getExtras();
        String s;

        userName = bundle.getString("USER");
        if (!IconConstants.IC_PERSON_MAP.containsKey(userName) || userName.equals("NoOne")) {
            isKnownUser = false;
            s = userName + "???";
        } else {
            isKnownUser = true;
            s = "Welcome " + userName;
        }
        welcomeTxt.setText(s);
    }

    private void setUserImg() {
        Bitmap bitmap;

        if (!IconConstants.IC_PERSON_MAP.containsKey(userName) || userName.equals("NoOne")) {
            isKnownUser = false;
            bitmap = BitmapFactory.decodeResource(
                    this.getResources(),
                    IconConstants.IC_PERSON_MAP.get("NoOne")
            );
        } else {
            isKnownUser = true;
            bitmap = BitmapFactory.decodeResource(
                    this.getResources(),
                    IconConstants.IC_PERSON_MAP.get(userName)
            );
        }
        userImg.setImageBitmap(bitmap);
    }
}
