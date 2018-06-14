package com.study.application.fireBase;


public class Record {

    private long dateTime;
    private String user, date, classification, status;

    public Record(String status, long dateTime, String user, String date, String classification){
        this.status = status;
        this.dateTime = dateTime;
        this.user = user;
        this.date = date;
        this.classification = classification;
    }

    public String getStatus(){
        return status;
    }

    public long getDateTime(){
        return dateTime;
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
