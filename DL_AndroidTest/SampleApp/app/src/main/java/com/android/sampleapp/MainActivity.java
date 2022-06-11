package com.android.sampleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<Model> modelList = new ArrayList<>();
    RecyclerView mRecyclerView;
    ListAdapter adapter = new ListAdapter(this, modelList);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.data_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        getData();
    }

    public void getData() {
        InputStream inputStream;
        modelList.clear();
        String path = "script.txt";
        String line = "";
        try {
            inputStream = getResources().getAssets().open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            while ((line = reader.readLine()) != null) {
                Model model = new Model();
                model.setText(line);
                modelList.add(model);
            }
        } catch (IOException e1) {
            Log.e("MainActivity", "Error" + line, e1);
            e1.printStackTrace();
        }
        adapter = new ListAdapter(this, modelList);
        mRecyclerView.setAdapter(adapter);
//        setTimerOfScrolling(4000);
        disableManualScrolling();
    }

    public void setTimerOfScrolling(int time) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() < (adapter.getItemCount() - 1)) {
                    linearLayoutManager.smoothScrollToPosition(mRecyclerView, new RecyclerView.State(), linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1);
                } else if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == (adapter.getItemCount() - 1)) {
                    linearLayoutManager.smoothScrollToPosition(mRecyclerView, new RecyclerView.State(), 0);
                }
            }
        }, 0, time);
    }

    public void disableManualScrolling() {
        // Use it
        RecyclerView.OnItemTouchListener disable = new ListAdapter(this, modelList);
        mRecyclerView.addOnItemTouchListener(disable);        // disables scolling
        mRecyclerView.removeOnItemTouchListener(disable);
    }
}