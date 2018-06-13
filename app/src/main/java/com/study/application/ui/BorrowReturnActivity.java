package com.study.application.ui;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.study.application.R;
import com.study.application.scanner.ScanQrCodeActivity;

public class BorrowReturnActivity extends AppCompatActivity {

    private final String TAG = "BorrowReturnActivity";

    private TextInputLayout itemInputLayout;
    private EditText dateEdt;
    private EditText nameEdt;
    private EditText itemEdt;
    public static final int REQUEST_CODE = 50;

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
        setContentView(R.layout.activity_borrow_return);

        initView();
        setEditText();
    }

    private void initView() {
        itemInputLayout = findViewById(R.id.itemInputLayout);
        dateEdt = findViewById(R.id.dateEdt);
        nameEdt = findViewById(R.id.nameEdt);
        itemEdt = findViewById(R.id.itemEdt);

        itemEdt.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent();

            bundle.putString("TARGET", "ITEM");
            intent.putExtras(bundle);
            intent.setClass(this, ScanQrCodeActivity.class);

            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    private void setEditText() {
        // date
        dateEdt.setText(String.valueOf(System.currentTimeMillis()));

        // name
        nameEdt.setText(WelcomeActivity.userName);

        // item
        Bundle bundle = getIntent().getExtras();
        String s = bundle.getString("FUNCTION_STRING");
        itemInputLayout.setHint(s);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                String s = data.getExtras().getString("ITEM");
                itemEdt.setText(s);
            }
        }
    }
}
