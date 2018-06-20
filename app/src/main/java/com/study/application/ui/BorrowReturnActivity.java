package com.study.application.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.study.application.R;
import com.study.application.fireBase.DisplayData;
import com.study.application.fireBase.Reader;
import com.study.application.fireBase.Writer;
import com.study.application.scanner.ScanQrCodeActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BorrowReturnActivity extends AppCompatActivity {

    private final String TAG = "BorrowReturnActivity";

    private String status, conditionSearchValue, dialogSuccessMessage;

    private TextInputLayout itemInputLayout;
    private EditText dateEdt;
    private EditText nameEdt;
    private EditText itemEdt;
    Button submitBtn;

    private ArrayList<DisplayData> conditionDataArrayList;
    private StatusBroadcast statusBroadcast = new StatusBroadcast();
    private Reader reader = new Reader();
    private Writer writer = new Writer();
    public static final int REQUEST_CODE = 50;

    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

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
        setListeners();

        setEditText();
        registerReceiver(statusBroadcast, new IntentFilter("DelverConditionData"));
    }

    private void initView() {
        itemInputLayout = findViewById(R.id.itemInputLayout);
        dateEdt = findViewById(R.id.dateEdt);
        nameEdt = findViewById(R.id.nameEdt);
        itemEdt = findViewById(R.id.itemEdt);
        submitBtn = findViewById(R.id.submitBtn);
    }

    private void setListeners() {
        itemEdt.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent();

            bundle.putString("TARGET", "ITEM");
            intent.putExtras(bundle);
            intent.setClass(this, ScanQrCodeActivity.class);

            startActivityForResult(intent, REQUEST_CODE);
        });

        submitBtn.setOnClickListener(v -> {
            if (itemEdt.getText().toString().equals("")) {
                new AlertDialog.Builder(BorrowReturnActivity.this).
                        setTitle(R.string.dialog_title_error).
                        setMessage(R.string.dialog_message_no_data).
                        setPositiveButton(R.string.dialog_button_check, (dialog, which) -> {
                        }).show();
            } else {
                writer.writeToDatabase(itemEdt.getText().toString(),
                        dateEdt.getText().toString(),
                        nameEdt.getText().toString(),
                        status,
                        date.getTime());
                new AlertDialog.Builder(BorrowReturnActivity.this).
                        setTitle(R.string.dialog_title_inform).
                        setMessage(dialogSuccessMessage).
                        setPositiveButton(R.string.dialog_button_check, (dialog, which) -> {
                        }).show();

                dateEdt.setText(dateFormat.format(date.getTime()));
                itemEdt.setText("");
            }
        });
    }

    private void setEditText() {
        // date
//        dateEdt.setText(String.valueOf(System.currentTimeMillis()));
        dateEdt.setText(dateFormat.format(date.getTime()));

        // name
        nameEdt.setText(WelcomeActivity.userName);
//        nameEdt.setText("Rex");
        // item
        Bundle bundle = getIntent().getExtras();
        String s = bundle.getString("FUNCTION_STRING");

        if (s.equals(getString(R.string.borrow_item))) {
            status = getString(R.string.status_borrow);
            conditionSearchValue = getString(R.string.status_return);
            dialogSuccessMessage = getString(R.string.dialog_message_borrow_success);
        } else {
            status = getString(R.string.status_return);
            conditionSearchValue = getString(R.string.status_borrow);
            dialogSuccessMessage = getString(R.string.dialog_message_return_success);
        }


        itemInputLayout.setHint(s);
        statusDataCheck();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean canBorrow_or_Return = false;
        String message = "";

        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                String s = data.getExtras().getString("ITEM");

                for (int i = 0; i < conditionDataArrayList.size(); i++) {

                    if (conditionDataArrayList.get(i).getIndex().contains(s)) {
                        if (status.equals(getString(R.string.status_return))) {
                            if (conditionDataArrayList.get(i).getUser().contains(nameEdt.getText().toString())){
                                canBorrow_or_Return = true;
                                break;
                            }else {
                                message = getString(R.string.dialog_message_return_user_error);
                                break;
                            }
                        }else {
                            canBorrow_or_Return = true;
                            break;
                        }
                    } else{
                        canBorrow_or_Return = false;
                        message = status;
                    }

                }


                if (canBorrow_or_Return)
                    itemEdt.setText(s);
                else {
                    itemEdt.setText("");
                    new AlertDialog.Builder(BorrowReturnActivity.this).
                            setTitle(R.string.dialog_title_error).
                            setMessage(s + message).setPositiveButton(R.string.dialog_button_check, (dialog, which) -> {
                    }).show();
                }
            }
        }
    }

    private void statusDataCheck() {
        reader.conditionSearch("狀態", conditionSearchValue);
    }

    private class StatusBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "DelverConditionData":
                        conditionDataArrayList = (ArrayList<DisplayData>) intent.getSerializableExtra("conditionData");
                        for (int i = 0; i < conditionDataArrayList.size(); i++)
                            Log.i("TAG", "Item:" + conditionDataArrayList.get(i).getIndex());
                        break;
                }
            }
        }
    }

}
