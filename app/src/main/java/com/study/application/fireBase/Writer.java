package com.study.application.fireBase;




import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class Writer{

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("Record");

    public void writeToDatabase(String index, String date, String name, String status, long dateTime){
        reference.child(index).child("date").setValue(date);
        reference.child(index).child("user").setValue(name);
        reference.child(index).child("dateTime").setValue(dateTime);
        reference.child(index).child("status").setValue(status);
    }

}
