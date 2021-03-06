package com.study.application.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.study.application.R;
import com.study.application.fireBase.DisplayData;
import com.study.application.fireBase.ListViewDataAdapter;
import com.study.application.fireBase.Reader;
import com.study.application.speech.Classification;
import com.study.application.speech.StatusDefinition;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private final String TAG = "SearchActivity";
    public static Context searchContext;

    private ListView lvData;
    private Spinner spnType;
    private Spinner spnItem;
    private Button btnSearch;
    private boolean isDataReady = false;

    private final DataBroadcast dataBroadcast = new DataBroadcast();
    private final Reader reader = new Reader();
    private ArrayAdapter<CharSequence> spnTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);
        searchContext = this;
        findViews();

        broadcastRegister();
        setListeners();
        setSpinnerTypeElements();
        spnType.setAdapter(spnTypeAdapter);
        StatusDefinition.CURRENT_STATUS = StatusDefinition.BORROW_RETURN_SEARCH;

//        reader.allDataSearch();
    }

    @Override
    protected void onResume() {

        super.onResume();
        StatusDefinition.CURRENT_STATUS = StatusDefinition.BORROW_RETURN_SEARCH;
    }

    private void findViews() {
        spnType = findViewById(R.id.spnType);
        spnItem = findViewById(R.id.spnItem);
        btnSearch = findViewById(R.id.btnSearch);
        lvData = findViewById(R.id.lvData);
    }

    private void broadcastRegister() {
        registerReceiver(dataBroadcast, new IntentFilter("DelverData"));
        registerReceiver(dataBroadcast, new IntentFilter("SpinnerItemElement"));
        registerReceiver(dataBroadcast, new IntentFilter("DelverConditionData"));
        registerReceiver(dataBroadcast, new IntentFilter(StatusDefinition.BORROW_RETURN_SEARCH));
    }

    private void setListeners() {
        btnSearch.setOnClickListener(v -> {
            if (isDataReady) {
                reader.conditionSearch(spnType.getSelectedItem().toString(), spnItem.getSelectedItem().toString());
            }
        });

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reader.spinnerElementSearch(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSpinnerTypeElements() {
        spnTypeAdapter = ArrayAdapter.createFromResource(searchContext, R.array.search_type, R.layout.spinner_setting);
    }

    private void setSpinnerItemElements(String[] spinnerItemElements) {
        ArrayAdapter<String> spnItemAdapter;

        spnItemAdapter = new ArrayAdapter<>(searchContext, R.layout.spinner_setting, spinnerItemElements);
        spnItem.setAdapter(spnItemAdapter);
    }

    private void voiceToSearchActivity(String inputClassification){
        switch (inputClassification){
            case Classification.GETBACK:
                finish();
                break;
        }
    }

    private class DataBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ListViewDataAdapter adapter;
            ArrayList<DisplayData> dataArrayList;
            ArrayList<DisplayData> conditionDataArrayList;

            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "DelverData":
                        dataArrayList = (ArrayList<DisplayData>) intent.getSerializableExtra("data");
                        adapter = new ListViewDataAdapter(searchContext, dataArrayList);
                        lvData.setAdapter(adapter);
                        break;
                    case "SpinnerItemElement":
                        setSpinnerItemElements(intent.getStringArrayExtra("SpinnerItemElementArray"));
                        isDataReady = true;
                        break;
                    case "DelverConditionData":
                        conditionDataArrayList = (ArrayList<DisplayData>) intent.getSerializableExtra("conditionData");
                        adapter = new ListViewDataAdapter(searchContext, conditionDataArrayList);
                        lvData.setAdapter(adapter);
                        break;
                    case StatusDefinition.BORROW_RETURN_SEARCH:
                        voiceToSearchActivity(intent.getStringExtra(StatusDefinition.BORROW_RETURN_SEARCH));
                        break;
                }
            }
        }
    }


}
