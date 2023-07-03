package com.example.listapplication_final;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Menu extends Activity {
    private MyDatabaseHelper database;
    private EditText editText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);


        // hide tasks
        SwitchCompat hideFinishedTasks = findViewById(R.id.hide_finished);
        hideFinishedTasks.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences sharedPreferences = getSharedPreferences("List", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("hideFinishedTasks", isChecked);
            editor.apply();
        });

        Spinner notificationsSpinner = findViewById(R.id.categorySpinner);
        String [] notificationTimeTags =  {"5min", "10min", "15min", "30min", "1h", "2h", "3h", "6h", "12h", "24h", "48h"};
        ArrayAdapter<String> notificationAdapter = new ArrayAdapter<>(com.example.listapplication_final.Menu.this,
                android.R.layout.simple_spinner_item, notificationTimeTags);
        notificationAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        notificationsSpinner.setAdapter(notificationAdapter);
        SharedPreferences sharedPreferences = getSharedPreferences("List", Context.MODE_PRIVATE);
        int pos = sharedPreferences.getInt("NotificationTimePos", 0);

        notificationsSpinner.setSelection(pos);
        notificationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences("List", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("NotificationTimePos", position);
                editor.putString("NotificationTimeString", notificationTimeTags[position]);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        editText = findViewById(R.id.enter_tag);


        RecyclerView tagsRecyclerView = findViewById(R.id.recyclerView);

        database = new MyDatabaseHelper(this);
        setUpRecyclerView(tagsRecyclerView);






        //Todo
        Button addTagButton = findViewById(R.id.addTag);
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = editText.getText().toString().trim().toUpperCase();
                database.addTag(tag,true);
                setUpRecyclerView(tagsRecyclerView);
            }
        });


        //todo
        Button deleteTagButton = findViewById(R.id.deleteTag);
        deleteTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = editText.getText().toString().trim().toUpperCase();
                database.deleteTag(tag);
                setUpRecyclerView(tagsRecyclerView);
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    private void setUpRecyclerView(RecyclerView recyclerView)
    {
        List<TagModel> itemList = database.getTagsNamesAsTagsArray();
        MenuRecyclerViewAdapter adapter = new MenuRecyclerViewAdapter(itemList,this);
        adapter.setOnItemClickListener(position -> {
            editText.setText(itemList.get(position).getName());
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
