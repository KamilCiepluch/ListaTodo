package com.example.listapplication_final;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class EditTask extends Activity {
    private static final int PICK_FILE_REQUEST_CODE = 0;
    private static final int REQUEST_SELECT_IMAGE = 1;
    private ListView listView;
    private final ArrayList<String> items = new ArrayList<>();
    private byte[] bArray;
    String selectedOption;
    private MyDatabaseHelper database;


    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_task);

        long taskID = getIntent().getLongExtra("TaskID",-1);
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
        AttachmentsListAdapter adapter = new AttachmentsListAdapter(this,R.layout.item_layout,items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String fileUriString = items.get(position);
                // Konwertowanie stringa na obiekt Uri
                Uri fileUri = Uri.parse(fileUriString);

                ContentResolver contentResolver = getContentResolver();

                contentResolver.takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                contentResolver.takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                Intent openIntent = new Intent(Intent.ACTION_VIEW);
                openIntent.setDataAndType(fileUri, contentResolver.getType(fileUri));
                openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                if (openIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(openIntent);
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

            showConfirmationDialog(dataModel,tagID);
        });

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper database = new MyDatabaseHelper(com.example.listapplication_final.EditTask.this);
                showConfirmationDialogDeleteButton(taskID);

            }
        });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        EditText title =findViewById(R.id.title);
        EditText description = findViewById(R.id.description);
        //  ImageView imageView = findViewById(R.id.imageView);
        TextView creationTime = findViewById(R.id.creation_time);
        TextView executionTime = findViewById(R.id.execution_time);
         Spinner tag = findViewById(R.id.categorySpinner);
        SwitchCompat isFinished = findViewById(R.id.status);
        SwitchCompat notifications = findViewById(R.id.notifications);

        outState.putString("title",title.getText().toString());
        outState.putString("description",description.getText().toString());
        outState.putByteArray("image", bArray);
        outState.putString("creationTime",creationTime.getText().toString());
        outState.putString("executionTime",executionTime.getText().toString());

        outState.putInt("tagPos", tag.getSelectedItemPosition());
        outState.putBoolean("isFinished", isFinished.isChecked());
        outState.putBoolean("notifications", notifications.isChecked());
        outState.putStringArrayList("list",items);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


        EditText title =findViewById(R.id.title);
        EditText description = findViewById(R.id.description);
        ImageView imageView = findViewById(R.id.imageView);
        TextView creationTime = findViewById(R.id.creation_time);
        TextView executionTime = findViewById(R.id.execution_time);
        Spinner tag = findViewById(R.id.categorySpinner);
        SwitchCompat isFinished = findViewById(R.id.status);
        SwitchCompat notifications = findViewById(R.id.notifications);
        listView = findViewById(R.id.attachments);

        title.setText(savedInstanceState.getString("title"));
        description.setText(savedInstanceState.getString("description"));
        bArray = savedInstanceState.getByteArray("image");
        if(bArray!=null)
        {
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bArray, 0, bArray.length));
        }
        creationTime.setText(savedInstanceState.getString("creationTime"));
        executionTime.setText(savedInstanceState.getString("executionTime"));
        tag.setSelection(savedInstanceState.getInt("tagPos"));
        isFinished.setChecked(savedInstanceState.getBoolean("isFinished"));
        notifications.setChecked(savedInstanceState.getBoolean("notifications"));

        items.clear();
        items.addAll(savedInstanceState.getStringArrayList("list"));
        AttachmentsListAdapter adapter = new AttachmentsListAdapter(this,R.layout.item_layout,items);
        listView.setAdapter(adapter);

    }


    private void showConfirmationDialogDeleteButton(long taskID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Do you want to delete task?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        database.deleteTask(taskID);
                        database.close();
                        onBackPressed();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Obsługa akcji po kliknięciu "Nie"
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showConfirmationDialog(DataModel dataModel, int tagID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation")
                .setMessage("Do you want to save changes?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        database.updateData(dataModel);
                        database.updateAttachments(items, tagID);
                        database.close();

                        if(dataModel.getNotifications())
                        {
                            SharedPreferences sharedPreferences = getSharedPreferences("List", Context.MODE_PRIVATE);
                            String offset = sharedPreferences.getString("NotificationTimeString", "0min");
                            NotificationHelper notificationHelper = new NotificationHelper(EditTask.this);
                            notificationHelper.cancelNotification(dataModel.getPrimaryKey());
                            long timeMili = TimeCalculator.calculateTimeDifference(dataModel.getExecutionTime(),offset);
                            notificationHelper.scheduleNotification(dataModel.getTitle(), dataModel.getDescription(),timeMili,(int) dataModel.getPrimaryKey());
                        }
                        //onBackPressed();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Obsługa akcji po kliknięciu "Nie"
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    public void selectImage() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }
}
