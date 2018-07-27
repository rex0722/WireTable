package com.study.application.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.study.application.R;
import com.study.application.speech.SpeechRecognition;

public class FunctionActivity extends AppCompatActivity {

    private final String TAG = "FunctionActivity";
    private boolean doubleBackToExitPressedOnce = false;
    Intent intent = new Intent();
    Bundle bundle = new Bundle();

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_function);

        initView();
    }

    private void initView() {
        Button borrowBtn = findViewById(R.id.borrowedBtn);
        Button returnBtn = findViewById(R.id.returnedBtn);
        Button searchBtn = findViewById(R.id.searchedBtn);

        Button.OnClickListener listener = view -> {
//            Intent intent = new Intent();
//            Bundle bundle = new Bundle();

            switch (view.getId()) {
                case R.id.borrowedBtn:
                    borrowSet();
                    break;
                case R.id.returnedBtn:
                    returnSet();
                    break;
                case R.id.searchedBtn:
                    searchSet();
                    break;
            }
        };

        borrowBtn.setOnClickListener(listener);
        returnBtn.setOnClickListener(listener);
        searchBtn.setOnClickListener(listener);
    }

    private void borrowSet(){
        bundle.putString("FUNCTION_STRING", getResources().getString(R.string.borrow_item));
        intent.putExtras(bundle);
        intent.setClass(this, BorrowReturnActivity.class);
        startActivity(intent);
    }

    private void returnSet(){
        bundle.putString("FUNCTION_STRING", getResources().getString(R.string.return_item));
        intent.putExtras(bundle);
        intent.setClass(this, BorrowReturnActivity.class);
        startActivity(intent);
    }

    private void searchSet(){
        intent.setClass(this, SearchActivity.class);
        startActivity(intent);
    }
}
