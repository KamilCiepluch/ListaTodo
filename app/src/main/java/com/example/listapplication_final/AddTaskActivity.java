package com.example.listapplication_final;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class AddTaskActivity extends Activity {


    private static final int PICK_FILE_REQUEST_CODE = 0;
    private static final int REQUEST_SELECT_IMAGE = 1;
    private ListView listView;
    private final ArrayList<String> items = new ArrayList<>();
    private byte[] bArray;
    String selectedOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_details);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.ic_launcher_background);



        // creation time
        TextView creationTime = findViewById(R.id.creation_time);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String currentTime = timeFormat.format(calendar.getTime());
        creationTime.setText(currentDate +" " + currentTime);

        //execution time
        TextView executionTime = findViewById(R.id.execution_time);
        executionTime.setText(currentDate +" " + currentTime);
        executionTime.setOnClickListener(v -> {
            // Get the current year, month, and day from the Calendar
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a DatePickerDialog and set the current date as the initial selection
            DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {

                        String dayStr = dayOfMonth < 10 ? "0"+dayOfMonth : "" +dayOfMonth;
                        String monthStr = (monthOfYear + 1)<10 ? "0" +(monthOfYear+1) : ""+(monthOfYear+1);

                        String selectedDate = dayStr + "/" + monthStr + "/" + year1;

                        // Pobierz bieżącą godzinę i minutę z kalendarza
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        // Utwórz TimePickerDialog i ustaw bieżącą godzinę jako początkową wartość wyboru
                        TimePickerDialog timePickerDialog = new TimePickerDialog(AddTaskActivity.this,
                                (view1, hourOfDay, minute1) -> {
                                    // Tutaj możesz wykonywać operacje z wybraną godziną'
                                    String hourStr = hourOfDay < 10 ? "0"+hourOfDay : "" +hourOfDay;
                                    String minuteStr = minute1 < 10 ? "0"+minute1 : "" +minute1;



                                    // Utwórz łańcuch reprezentujący wybraną godzinę
                                    String selectedTime = selectedDate + " "+ hourStr + ":" + minuteStr;
                                    executionTime.setText(selectedTime);
                                }, hour, minute, true);
                        // Pokaż TimePickerDialog
                        timePickerDialog.show();

                    }, year, month, day);
            // Show the DatePickerDialog
            datePickerDialog.show();
        });


        // open database
        MyDatabaseHelper database = new MyDatabaseHelper(AddTaskActivity.this);
        String [] tags =database.getTagsNames().toArray(new String[0]);


        //tags spinner
        Spinner tagsSpinner = findViewById(R.id.categorySpinner);
        ArrayAdapter<String> tagsAdapter = new ArrayAdapter<>(AddTaskActivity.this,
                android.R.layout.simple_spinner_item, tags);
        tagsAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        tagsSpinner.setAdapter(tagsAdapter);
        tagsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 selectedOption = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Obsługa braku wybranej opcji
                selectedOption = null;
            }
        });


        // attachments
        listView = findViewById(R.id.attachments);
        AttachmentsListAdapter adapter = new AttachmentsListAdapter(this,R.layout.item_layout,items);
        listView.setAdapter(adapter);


        //Add image
        Button addImageButton = findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(v -> selectImage());

        //Add File
        Button addFileButton = findViewById(R.id.addFileButton);
        addFileButton.setOnClickListener(v -> openFileChooser());


        //Save
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            EditText title = findViewById(R.id.title);
            EditText description = findViewById(R.id.description);
            SwitchCompat status = findViewById(R.id.status);
            SwitchCompat notifications = findViewById(R.id.notifications);
            int tagID = database.getTagID(selectedOption);
            DataModel dataModel = new DataModel(title.getText().toString(), description.getText().toString(),
                    creationTime.getText().toString(),
                    executionTime.getText().toString(),status.isChecked(),notifications.isChecked(),tagID,bArray);

            long id = database.addData(dataModel);
            database.addAttachments(items, id );
            database.close();
        });
    }


    public void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    public void selectImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                AttachmentsListAdapter adapter = new AttachmentsListAdapter(this,R.layout.item_layout,items);
                items.add(uri.toString());
                listView.setAdapter(adapter);
            }
        }
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bArray = bos.toByteArray();
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
