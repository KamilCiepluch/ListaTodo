package com.example.listapplication_final;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class Menu extends Activity {
    private MyDatabaseHelper database;
    private EditText editText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        SharedPreferences sharedPreferences = getSharedPreferences("List", Context.MODE_PRIVATE);

        // hide tasks
        SwitchCompat hideFinishedTasks = findViewById(R.id.hide_finished);
        hideFinishedTasks.setChecked(sharedPreferences.getBoolean("hideFinishedTasks",false));
        hideFinishedTasks.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
        int pos = sharedPreferences.getInt("NotificationTimePos", 0);
        notificationsSpinner.setSelection(pos);
        notificationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences("List", Context.MODE_PRIVATE);
                String currentOption =  sharedPreferences.getString("NotificationTimeString", "0min");
                if(!currentOption.equals(notificationTimeTags[position]))
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("NotificationTimePos", position);
                    editor.putString("NotificationTimeString", notificationTimeTags[position]);
                    editor.apply();


                    List<DataModel> list = database.getTasksListWithActiveNotifications();
                    NotificationHelper notificationHelper = new NotificationHelper(Menu.this);
                    for(DataModel dataModel:list)
                    {
                        String offset = notificationTimeTags[position];
                        long timeMilli = TimeCalculator.calculateTimeDifference(dataModel.getExecutionTime(),"0min");
                        if(timeMilli>0)
                        {
                            timeMilli = TimeCalculator.calculateTimeDifference(dataModel.getExecutionTime(),offset);
                            notificationHelper.cancelNotification(dataModel.getPrimaryKey());
                            notificationHelper.scheduleNotification(dataModel.getTitle(),
                                    dataModel.getDescription(),timeMilli, dataModel.getPrimaryKey());
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        editText = findViewById(R.id.enter_tag);
        RecyclerView tagsRecyclerView = findViewById(R.id.recyclerView);

        database = new MyDatabaseHelper(this);
        setUpRecyclerView(tagsRecyclerView);



        Button addTagButton = findViewById(R.id.addTag);
        addTagButton.setOnClickListener(v -> {
            String tag = editText.getText().toString().trim().toUpperCase();
            if(tag.length()>0)
            {
                database.addTag(tag,true);
                setUpRecyclerView(tagsRecyclerView);
            }

        });


        Button deleteTagButton = findViewById(R.id.deleteTag);
        deleteTagButton.setOnClickListener(v -> {
            String tag = editText.getText().toString().trim().toUpperCase();
            if(database.getCountByTasksWithTag(tag)==0)
            {
                database.deleteTag(tag);
                setUpRecyclerView(tagsRecyclerView);
            }
            else {
                Toast.makeText(Menu.this, "You can't delete tag that is used by task", Toast.LENGTH_SHORT).show();
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
        adapter.setOnItemClickListener(position -> editText.setText(itemList.get(position).getName()));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
