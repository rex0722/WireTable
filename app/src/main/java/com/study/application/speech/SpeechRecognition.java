package com.study.application.speech;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.study.application.fireBase.SpeechDataReader;
import com.study.application.ui.MainActivity;

import java.util.ArrayList;


public class SpeechRecognition implements RecognitionListener {

    private Handler handler = new Handler();
    private String classification = "";
    private boolean isCorrect = false;


    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.v("TAG","onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.v("TAG","onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
//        Log.v("TAG","onRmsChanged : " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.v("TAG","");
    }

    @Override
    public void onEndOfSpeech() {
        Log.v("TAG","onBufferReceived");

        handler.postDelayed(()-> {
                MainActivity.speech.startListening(MainActivity.recognizerIntent);
        },1000);

    }

    @Override
    public void onError(int error) {
        Log.v("TAG","onError");
    }

    @Override
    public void onResults(Bundle results) {
        Log.v("TAG","onResults");

        ArrayList<String> match = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (match != null && !StatusDefinition.CURRENT_STATUS.equals(StatusDefinition.QR_CODE_SCAN)){
            if (messageHandle(match.get(0))){
                Intent intent = new Intent(StatusDefinition.CURRENT_STATUS);
                intent.putExtra(StatusDefinition.CURRENT_STATUS, classification);
                MainActivity.mContext.sendBroadcast(intent);
            }
        }

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.v("TAG","onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.v("TAG","onEvent");
    }

    private boolean messageHandle(String voiceInput){

        for(int i = 0; i < SpeechDataReader.messagesArrayList.size(); i++){
            if (voiceInput.contains(SpeechDataReader.messagesArrayList.get(i).getMessageContent())){
                classification = SpeechDataReader.messagesArrayList.get(i).getClassification();
                isCorrect = true;
                break;
            }else
                isCorrect = false;
        }
        return isCorrect;
    }
}
