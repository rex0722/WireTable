package com.study.application.speech;

import java.io.Serializable;

public class SpeechMessage implements Serializable {

    private String classification, messageContent;

    public SpeechMessage(String inputClassification, String inputContent){
        classification = inputClassification;
        messageContent = inputContent;
    }

    public String getClassification(){
        return classification;
    }

    public String getMessageContent(){
        return messageContent;
    }

}
