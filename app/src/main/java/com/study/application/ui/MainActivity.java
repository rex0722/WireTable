package com.study.application.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.study.application.R;
import com.study.application.fireBase.SpeechDataReader;
import com.study.application.scanner.ScanQrCodeActivity;
import com.study.application.speech.SpeechMessage;
import com.study.application.speech.SpeechRecognition;
import com.study.application.speech.SpeechSynthesis;
import com.study.application.speech.StatusDefinition;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private ArrayList<SpeechMessage> messagesArrayList = new ArrayList<>();
    public static Context mContext;
    private MainBroadcast mainBroadcast = new MainBroadcast();
    private SpeechDataReader speechDataReader = new SpeechDataReader();
    public static SpeechRecognizer speech = null;
    public static Intent recognizerIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initView();
        broadcastRegister();
        SpeechSynthesis.createTTS();

        speechDataReader.loginDataLoad();

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        SpeechRecognition speechRecognition = new SpeechRecognition();
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(speechRecognition);

        ActivityCompat.requestPermissions
                (MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG", "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("TAG", "onRestart");
        StatusDefinition.CURRENT_STATUS = StatusDefinition.LOGIN;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void initView() {
        mContext = this;
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(v -> {
            scanQrCodeActivityStartUp();
        });
    }

    private void scanQrCodeActivityStartUp(){
        Bundle bundle = new Bundle();
        Intent intent = new Intent();

        bundle.putString("TARGET", "USER");
        intent.putExtras(bundle);
        intent.setClass(this, ScanQrCodeActivity.class);

        startActivity(intent);
    }

    public void voiceToLogin(String inputClassification){

        Log.v("TAG", "voiceToLogin_inputClassification:" + inputClassification + ", size:" + SpeechDataReader.messagesArrayList.size());

        if (inputClassification.equals(StatusDefinition.LOGIN)){
            Log.v("TAG","QR code Scan START!!");
                StatusDefinition.CURRENT_STATUS = StatusDefinition.QR_CODE_SCAN;
                scanQrCodeActivityStartUp();
        }
    }

    private void broadcastRegister(){
        registerReceiver(mainBroadcast, new IntentFilter("LoginData"));
        registerReceiver(mainBroadcast, new IntentFilter(StatusDefinition.LOGIN));
    }


    private class MainBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null){
                switch (intent.getAction()){
                    case StatusDefinition.LOGIN:
                        voiceToLogin(intent.getStringExtra(StatusDefinition.LOGIN));
                        break;
                }
            }
        }
    }
}
