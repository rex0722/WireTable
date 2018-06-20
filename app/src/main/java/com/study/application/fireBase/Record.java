package com.study.application.fireBase;


class Record {

    private final long dateTime;
    private final String user;
    private final String date;
    private final String classification;
    private final String status;

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
