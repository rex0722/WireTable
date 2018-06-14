package com.study.application.fireBase;


import java.io.Serializable;

public class DisplayData implements Serializable {


     private String index, user, date, classification, status;

    public DisplayData(String status, String index, String user, String date, String classification){
        this.status = status;
        this.index = index;
        this.user = user;
        this.date = date;
        this.classification = classification;
    }

    public String getStatus(){
        return status;
    }

    public String getIndex(){
        return index;
    }

    public String getUser(){
        return user;
    }

    public String getDate(){
        return date;
    }

    public  String getClassification(){
        return classification;
    }


}
