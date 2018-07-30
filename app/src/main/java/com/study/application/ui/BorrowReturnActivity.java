package com.study.application.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.study.application.R;
import com.study.application.fireBase.DisplayData;
import com.study.application.fireBase.Reader;
import com.study.application.fireBase.Writer;
import com.study.application.scanner.ScanQrCodeActivity;
import com.study.application.speech.Classification;
import com.study.application.speech.SpeechSynthesis;
import com.study.application.speech.StatusDefinition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BorrowReturnActivity extends AppCompatActivity {

    private final String TAG = "BorrowReturnActivity";

    private String status, conditionSearchValue, dialogSuccessMessage;

    private TextInputLayout itemInputLayout;
    private EditText dateEdt;
    private EditText nameEdt;
    private EditText itemEdt;
    private Button submitBtn;

    private ArrayList<DisplayData> conditionDataArrayList;
    private final StatusBroadcast statusBroadcast = new StatusBroadcast();
    private final Reader reader = new Reader();
    private final Writer writer = new Writer();
    public static final int REQUEST_CODE = 50;

    private final Date date = new Date();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.TAIWAN);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_borrow_return);

        StatusDefinition.CURRENT_STATUS = StatusDefinition.BORROW_RETURN_SEARCH;
        initView();
        setListeners();
        setEditText();
        broadcastRegister();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusDefinition.CURRENT_STATUS = StatusDefinition.BORROW_RETURN_SEARCH;
        Log.i("TAG", "MainActivity---onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("TAG", "BorrowReturnActivity---onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("TAG", "BorrowReturnActivity---onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("TAG", "BorrowReturnActivity---onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("TAG", "BorrowReturnActivity---onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TAG", "BorrowReturnActivity---onDestroy");
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
            scanQrCodeActivityStartUp();
        });

        submitBtn.setOnClickListener(v -> {
                submitFunction();
        });
    }

    private void scanQrCodeActivityStartUp(){
        Bundle bundle = new Bundle();
        Intent intent = new Intent();

        bundle.putString("TARGET", "ITEM");
        intent.putExtras(bundle);
        intent.setClass(this, ScanQrCodeActivity.class);

        startActivityForResult(intent, REQUEST_CODE);
    }

    private void submitFunction(){
        if (itemEdt.getText().toString().equals("")) {
            Toast.makeText(BorrowReturnActivity.this, getString(R.string.dialog_message_no_data), Toast.LENGTH_LONG).show();
            SpeechSynthesis.textToSpeech.speak(getString(R.string.dialog_message_no_data),TextToSpeech.QUEUE_FLUSH, null );
        } else {
            writer.writeToDatabase(itemEdt.getText().toString(),
                    dateEdt.getText().toString(),
                    nameEdt.getText().toString(),
                    status,
                    date.getTime());

            Toast.makeText(BorrowReturnActivity.this, dialogSuccessMessage, Toast.LENGTH_LONG).show();

            SpeechSynthesis.textToSpeech.speak(dialogSuccessMessage, TextToSpeech.QUEUE_FLUSH, null);
            dateEdt.setText(dateFormat.format(date.getTime()));
            itemEdt.setText("");
        }
    }

    private void setEditText() {
        // date
        dateEdt.setText(dateFormat.format(date.getTime()));

        // name
        nameEdt.setText(WelcomeActivity.userName);

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
                            if (conditionDataArrayList.get(i).getUser().contains(nameEdt.getText().toString())) {
                                canBorrow_or_Return = true;
                                break;
                            } else {
                                message = getString(R.string.dialog_message_return_user_error);
                                break;
                            }
                        } else {
                            canBorrow_or_Return = true;
                            break;
                        }
                    } else {
                        canBorrow_or_Return = false;
                        message = status;
                    }

                }


                if (canBorrow_or_Return)
                    itemEdt.setText(s);
                else {
                    itemEdt.setText("");
                    Toast.makeText(BorrowReturnActivity.this, s + message, Toast.LENGTH_LONG).show();
                    SpeechSynthesis.textToSpeech.speak(s + message, TextToSpeech.QUEUE_FLUSH, null );
                }
            }
        }
    }

    private void statusDataCheck() {
        reader.conditionSearch("狀態", conditionSearchValue);
    }

    private void voiceToBorrowOrSubmit(String inputClassification){

        switch (inputClassification){
            case Classification.SCAN:
                scanQrCodeActivityStartUp();
                break;
            case Classification.SUBMIT:
                submitFunction();
                break;
            case Classification.GETBACK:
                finish();
                break;
        }

    }

    private void broadcastRegister(){
        registerReceiver(statusBroadcast, new IntentFilter("DelverConditionData"));
        registerReceiver(statusBroadcast, new IntentFilter(StatusDefinition.BORROW_RETURN_SEARCH));
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
                    case StatusDefinition.BORROW_RETURN_SEARCH:
                        voiceToBorrowOrSubmit(intent.getStringExtra(StatusDefinition.BORROW_RETURN_SEARCH));
                        break;
                }
            }
        }
    }

}
