package com.study.application.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.study.application.R;

public class FunctionActivity extends AppCompatActivity {

    private final String TAG = "FunctionActivity";

    private boolean doubleBackToExitPressedOnce = false;

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
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        initView();
    }

    private void initView() {
        Button borrowBtn = findViewById(R.id.borrowBtn);
        Button returnBtn = findViewById(R.id.returnBtn);
        Button searchBtn = findViewById(R.id.searchBtn);

        Button.OnClickListener listener = view -> {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            switch (view.getId()) {
                case R.id.borrowBtn:
                    bundle.putString("FUNCTION_STRING", getResources().getString(R.string.borrow_item));
                    intent.putExtras(bundle);
                    intent.setClass(this, BorrowReturnActivity.class);
                    startActivity(intent);
                    break;
                case R.id.returnBtn:
                    bundle.putString("FUNCTION_STRING", getResources().getString(R.string.return_item));
                    intent.putExtras(bundle);
                    intent.setClass(this, BorrowReturnActivity.class);
                    startActivity(intent);
                    break;
                case R.id.searchBtn:
                    intent.setClass(this, SearchActivity.class);
                    startActivity(intent);
                    break;
            }
        };

        borrowBtn.setOnClickListener(listener);
        returnBtn.setOnClickListener(listener);
        searchBtn.setOnClickListener(listener);
    }
}
