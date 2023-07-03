package com.example.listapplication_final;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
        int pos = sharedPreferences.getInt("NotificationTime", 0);
        notificationsSpinner.setSelection(pos);
        notificationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences("List", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("NotificationTime", position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        EditText editText = findViewById(R.id.enter_tag);


        RecyclerView tagsRecyclerView = findViewById(R.id.recyclerView);

        MyDatabaseHelper database = new MyDatabaseHelper(this);
        List<TagModel> itemList = database.getTagsNamesAsTagsArray();
        MenuRecyclerViewAdapter adapter = new MenuRecyclerViewAdapter(itemList);
        adapter.setOnItemClickListener(position -> {
            // Obsługa kliknięcia na element listy
            // Pobierz kliknięty element na podstawie pozycji
            /*
            DataModel clickedItem = itemList.get(position);

            // Wykonaj odpowiednie akcje na podstawie klikniętego elementu
            Toast.makeText(MainActivity.this, "Kliknięto element ", Toast.LENGTH_SHORT).show();
            Log.wtf("Click", "Kliknieto element: " + position);
            Intent intent = new Intent(MainActivity.this,EditTask.class);
            intent.putExtra("TaskID", clickedItem.getPrimaryKey());
            startActivity(intent);
            */
        });
        tagsRecyclerView.setAdapter(adapter);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this));






        //Todo
        Button addTagButton = findViewById(R.id.addTag);
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //todo
        Button deleteTagButton = findViewById(R.id.deleteTag);
        deleteTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });







    }
}
