package com.study.application.speech;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.study.application.ui.MainActivity;

import java.util.Locale;

public class SpeechSynthesis {
    public static TextToSpeech textToSpeech;

    public static void createTTS(){
        if (textToSpeech == null){

            textToSpeech = new TextToSpeech(MainActivity.mContext, (status) ->{

                    if (status == TextToSpeech.SUCCESS){
                        int language = textToSpeech.setLanguage(Locale.TRADITIONAL_CHINESE);

                        if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED)
                            Toast.makeText(MainActivity.mContext, "語音不支持", Toast.LENGTH_SHORT).show();
                    }else
                        Log.d("TAG","語音合成尚未建立");
            });
        }
    }
}
