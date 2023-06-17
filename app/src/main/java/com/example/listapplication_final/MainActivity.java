package com.example.listapplication_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton actionButton;
    private MyDatabaseHelper database;
    private static final int REQUEST_SELECT_IMAGE = 1;
    private static final int REQUEST_CODE_PERMISSION = 123;

    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
        int grantResult = ContextCompat.checkSelfPermission(this, permissions[0]);

        if (grantResult != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION);
        }


        searchView= findViewById(R.id.searchView);



        database = new MyDatabaseHelper(this);

     /*
        database.deleteAllData();
    //    database.getTagsID();


        database.addTag("tag1");
        database.addTag("tag2");
        database.addTag("tag3");

        /*
        database.addData(new DataModel("Element 1","XD",
                "12:15", "15:45",true,true,1,null));
*/

        /*
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
*/



        List<DataModel> itemList = database.getTaskList();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(itemList,this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        actionButton = findViewById(R.id.fab);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddTaskActivity.class);
                startActivity(intent);
            }
        });
       // database.deleteAllData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<DataModel> itemList = database.getTaskList();

        // Tworzenie komparatora opartego na dacie i czasie wykonania
        Comparator<DataModel> executionTimeComparator = new Comparator<DataModel>() {
            @Override
            public int compare(DataModel item1, DataModel item2) {
                return item1.getExecutionTime().compareTo(item2.getExecutionTime());
            }
        };

        // Sortowanie listy używając komparatora
        Collections.sort(itemList, executionTimeComparator);


        RecyclerViewAdapter adapter = new RecyclerViewAdapter(itemList,this);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Obsługa kliknięcia na element listy
                // Pobierz kliknięty element na podstawie pozycji
                DataModel clickedItem = itemList.get(position);

                // Wykonaj odpowiednie akcje na podstawie klikniętego elementu
                Toast.makeText(MainActivity.this, "Kliknięto element ", Toast.LENGTH_SHORT).show();
                Log.wtf("Click", "Kliknieto element: " + position);
                Intent intent = new Intent(MainActivity.this,EditTask.class);
                intent.putExtra("TaskID", clickedItem.getPrimaryKey());
                startActivity(intent);

            }
        });
        recyclerView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Tutaj możesz obsłużyć wysłanie formularza wyszukiwania (np. uruchomić wyszukiwanie)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Tutaj możesz reagować na zmiany tekstu w polu wyszukiwania (np. filtrując dane)
                List<DataModel> itemList = database.getTaskList()
                        .stream()
                        .filter(dataModel -> dataModel.getTitle().startsWith(newText))
                        .collect(Collectors.toList());

                RecyclerViewAdapter adapter = new RecyclerViewAdapter(itemList,MainActivity.this);
                adapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        // Obsługa kliknięcia na element listy
                        // Pobierz kliknięty element na podstawie pozycji
                        DataModel clickedItem = itemList.get(position);

                        // Wykonaj odpowiednie akcje na podstawie klikniętego elementu
                        Toast.makeText(MainActivity.this, "Kliknięto element ", Toast.LENGTH_SHORT).show();
                        Log.wtf("Click", "Kliknieto element: " + position);
                        Intent intent = new Intent(MainActivity.this,EditTask.class);
                        intent.putExtra("TaskID", clickedItem.getPrimaryKey());
                        startActivity(intent);

                    }
                });
                recyclerView.setAdapter(adapter);
                return false;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] bArray = bos.toByteArray();
                database.updateDataImage(1,bArray);
                //  imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}