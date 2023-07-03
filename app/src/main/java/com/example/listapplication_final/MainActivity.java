package com.example.listapplication_final;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
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
        List<DataModel> itemList = database.getTaskList();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(itemList,this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton actionButton = findViewById(R.id.fab);
        actionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),AddTaskActivity.class);
            startActivity(intent);
        });

        FloatingActionButton settingButton = findViewById(R.id.settings);
        settingButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),Menu.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<DataModel> list = database.getTaskList();

        List<Integer> activeTagsIDs = database.getActiveTagsIDsArray();


        List<DataModel> itemList = list.stream()
                .filter(dataModel -> activeTagsIDs.contains(dataModel.getCategoryId()))
                .collect(Collectors.toList());


        // Tworzenie komparatora opartego na dacie i czasie wykonania
        Comparator<DataModel> executionTimeComparator = Comparator.comparing(DataModel::getExecutionTime);


        // Sortowanie listy używając komparatora
        itemList.sort(executionTimeComparator);




        RecyclerViewAdapter adapter = new RecyclerViewAdapter(itemList,this);
        adapter.setOnItemClickListener(position -> {
            // Obsługa kliknięcia na element listy
            // Pobierz kliknięty element na podstawie pozycji
            DataModel clickedItem = itemList.get(position);

            // Wykonaj odpowiednie akcje na podstawie klikniętego elementu
            Toast.makeText(MainActivity.this, "Kliknięto element ", Toast.LENGTH_SHORT).show();
            Log.wtf("Click", "Kliknieto element: " + position);
            Intent intent = new Intent(MainActivity.this,EditTask.class);
            intent.putExtra("TaskID", clickedItem.getPrimaryKey());
            startActivity(intent);

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
                adapter.setOnItemClickListener(position -> {
                    // Obsługa kliknięcia na element listy
                    // Pobierz kliknięty element na podstawie pozycji
                    DataModel clickedItem = itemList.get(position);

                    // Wykonaj odpowiednie akcje na podstawie klikniętego elementu
                    Intent intent = new Intent(MainActivity.this,EditTask.class);
                    intent.putExtra("TaskID", clickedItem.getPrimaryKey());
                    startActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }
}