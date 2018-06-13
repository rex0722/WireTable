package com.study.application.fireBase;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.study.application.R;

import java.util.ArrayList;

public class ListViewDataAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private ArrayList<DisplayData> dataArrayList;

    public ListViewDataAdapter(Context c, ArrayList<DisplayData> arrayList){
        inflater = LayoutInflater.from(c);
        dataArrayList = arrayList;
    }

    @Override
    public int getCount() {
        return dataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.listview_setting, null);

        TextView txtIndex = convertView.findViewById(R.id.txtIndex);
        TextView txtClassification = convertView.findViewById(R.id.txtClassification);
        TextView txtDate = convertView.findViewById(R.id.txtDate);
        TextView txtUser = convertView.findViewById(R.id.txtUser);
        TextView txtAvailable = convertView.findViewById(R.id.txtAvailable);

        txtIndex.setText(dataArrayList.get(position).getIndex());
        txtClassification.setText(dataArrayList.get(position).getClassification());
        txtDate.setText(dataArrayList.get(position).getDate());
        txtUser.setText(dataArrayList.get(position).getUser());

        if (dataArrayList.get(position).getAvailable())
            txtAvailable.setText(R.string.status_return);
        else
            txtAvailable.setText(R.string.status_borrow);

        return convertView;
    }
}
