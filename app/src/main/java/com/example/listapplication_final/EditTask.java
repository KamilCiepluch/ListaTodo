package com.example.listapplication_final;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class EditTask extends Activity {

    private static final int REQUEST_CODE_PERMISSION = 123;
    private static int PICK_FILE_REQUEST_CODE = 0;
    private static  int REQUEST_SELECT_IMAGE = 0;
    private ListView listView;
    private final ArrayList<String> items = new ArrayList<>();
    private byte[] bArray;
    String selectedOption;
    private MyDatabaseHelper database;


    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_details);




        Long taskID = getIntent().getLongExtra("TaskID",-1);
        database = new MyDatabaseHelper(this);
        context = this;
        DataModel data = database.getTask(taskID);

        ImageView imageView = findViewById(R.id.imageView);
        bArray = data.getImage();
        if(bArray!=null) {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bArray, 0, bArray.length));
        }

        EditText title = findViewById(R.id.title);
        title.setText(data.getTitle());


        EditText description = findViewById(R.id.description);
        description.setText(data.getDescription());

        TextView creationTime = findViewById(R.id.creation_time);
        creationTime.setText(data.getCreationTime());

        TextView executionTime = findViewById(R.id.execution_time);
        executionTime.setText(data.getExecutionTime());
        executionTime.setOnClickListener(v -> {
            // Get the current year, month, and day from the Calendar
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create a DatePickerDialog and set the current date as the initial selection
            DatePickerDialog datePickerDialog = new DatePickerDialog(com.example.listapplication_final.EditTask.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {

                        String dayStr = dayOfMonth < 10 ? "0"+dayOfMonth : "" +dayOfMonth;
                        String monthStr = (monthOfYear + 1)<10 ? "0" +(monthOfYear+1) : ""+(monthOfYear+1);

                        String selectedDate = dayStr + "/" + monthStr + "/" + year1;

                        // Pobierz bieżącą godzinę i minutę z kalendarza
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        // Utwórz TimePickerDialog i ustaw bieżącą godzinę jako początkową wartość wyboru
                        TimePickerDialog timePickerDialog = new TimePickerDialog(com.example.listapplication_final.EditTask.this,
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


        SwitchCompat status = findViewById(R.id.status);
        status.setChecked(data.getFinished());

        SwitchCompat notifications = findViewById(R.id.notifications);
        notifications.setChecked(data.getNotifications());



        Spinner tagsSpinner = findViewById(R.id.categorySpinner);
        ArrayList<String> tagsNamesArray = database.getTagsNames();
        String tagNameTmp = database.getTagName(data.getCategoryId());

        String [] tags = tagsNamesArray.toArray(new String[0]);
        ArrayAdapter<String> tagsAdapter = new ArrayAdapter<>(com.example.listapplication_final.EditTask.this,
                android.R.layout.simple_spinner_item, tags);
        tagsAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        tagsSpinner.setAdapter(tagsAdapter);
        tagsSpinner.setSelection(tagsNamesArray.indexOf(tagNameTmp));
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



        items.addAll( database.getAttachmentsList(data.getPrimaryKey()));


        listView = findViewById(R.id.attachments);
      //
        //  MyListAdapter adapter = new MyListAdapter(this,20,items);

        AttachmentsListAdapter adapter = new AttachmentsListAdapter(this,R.layout.item_layout,items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String filePath = items.get(position);

                Uri fileUri = Uri.parse(filePath);

                Log.wtf("URI", filePath);
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditTask.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                }

                if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileUri, "application/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Log.wtf("Attachment", "RIP");
                        //Toast.makeText(this, "Brak aplikacji obsługującej pliki PDF.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });



        Button addImageButton = findViewById(R.id.addImageButton);
        addImageButton.setOnClickListener(v -> selectImage());

        Button addFileButton = findViewById(R.id.addFileButton);
        addFileButton.setOnClickListener(v -> openFileChooser());







        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            MyDatabaseHelper database = new MyDatabaseHelper(com.example.listapplication_final.EditTask.this);
            int tagID = database.getTagID(selectedOption);
            DataModel dataModel = new DataModel(taskID,title.getText().toString(), description.getText().toString(),
                    creationTime.getText().toString(),
                    executionTime.getText().toString(),status.isChecked(),notifications.isChecked(),tagID,bArray);

            database.updateData(dataModel);
            database.updateAttachments(items, taskID);
            database.close();
        });
    }


    public void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        PICK_FILE_REQUEST_CODE =1;
        REQUEST_SELECT_IMAGE = 0;
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        PICK_FILE_REQUEST_CODE =0;
        REQUEST_SELECT_IMAGE = 1;
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
                // database.updateDataImage(1,bArray);
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}