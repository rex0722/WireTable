package com.study.application.fireBase;


public class DisplayData {

    private boolean available;
    private String index, user, date, classification;

    public DisplayData(boolean available, String index, String user, String date, String classification){
        this.available = available;
        this.index = index;
        this.user = user;
        this.date = date;
        this.classification = classification;
    }

    public boolean getAvailable(){
        return available;
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
