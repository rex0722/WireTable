package com.study.application.fireBase;


public class Record {

    private boolean available;
    private long dateTime;
    private String user, date, classification;

    public Record(boolean available, long dateTime, String user, String date, String classification){
        this.available = available;
        this.dateTime = dateTime;
        this.user = user;
        this.date = date;
        this.classification = classification;
    }

    public boolean getAvailable(){
        return available;
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
