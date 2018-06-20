package com.study.application.fireBase;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.study.application.ui.MainActivity;
import com.study.application.ui.SearchActivity;

import java.util.ArrayList;

public class Reader {

    private ArrayList<DisplayData> dataArrayList = new ArrayList<>();
    private ArrayList<DisplayData> conditionDataArrayList = new ArrayList<>();
    private ArrayList<String> itemArrayList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("Record");

    public void allDataSearch(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataArrayList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    dataArrayList.add(new DisplayData(
                            ds.child("status").getValue().toString(),
                            ds.getKey(),
                            ds.child("user").getValue().toString(),
                            ds.child("date").getValue().toString(),
                            ds.child("classification").getValue().toString()));
                    Log.v("TAG", "Value:" + ds.getValue());
                }

                Intent intent = new Intent("DelverData");
                intent.putExtra("data", dataArrayList);
                SearchActivity.searchContext.sendBroadcast(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    public void spinnerElementSearch(String selectItem){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String keyWord = keyWordJudge(selectItem);
                itemArrayList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (!itemArrayList.contains(ds.child(keyWord).getValue().toString()))
                        itemArrayList.add(ds.child(keyWord).getValue().toString());
                }

                Intent intent = new Intent("SpinnerItemElement");
                intent.putExtra("SpinnerItemElementArray", itemArrayList.toArray(new String [itemArrayList.size()]));
                SearchActivity.searchContext.sendBroadcast(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void conditionSearch(String word, String value){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                conditionDataArrayList.clear();
                String keyWord = keyWordJudge(word);

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.child(keyWord).getValue().toString().equals(value)){
                        conditionDataArrayList.add(new DisplayData(
                                ds.child("status").getValue().toString(),
                                ds.getKey(),
                                ds.child("user").getValue().toString(),
                                ds.child("date").getValue().toString(),
                                ds.child("classification").getValue().toString()));
                    }

                }

                Intent intent = new Intent("DelverConditionData");
                intent.putExtra("conditionData", conditionDataArrayList);
                MainActivity.mContext.sendBroadcast(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private String keyWordJudge(String word){

        String keyWord = "";

        switch (word){
            case "品項":
                keyWord = "classification";
                break;
            case "使用者":
                keyWord = "user";
                break;
            case "狀態":
                keyWord = "status";
                break;
        }

        return keyWord;
    }


}
