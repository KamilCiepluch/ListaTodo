package com.example.listapplication_final;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class NotificationHelper {
    private static final String CHANNEL_ID = "your_channel_id";
    private static final String CHANNEL_NAME = "Your Channel Name";
    private static final String CHANNEL_DESCRIPTION = "Your Channel Description";


    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public NotificationCompat.Builder getNotificationBuilder(String title, String message, long taskID) {

        Intent intent = new Intent(context, EditTask.class);
        intent.putExtra("TaskID",taskID);
        Log.wtf("TaskID = ", " " + taskID);
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.attachment_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getActivity(context, (int)taskID, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true);
    }

    public void notify(NotificationCompat.Builder builder, long notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify( (int) notificationId, builder.build());
    }

    public void scheduleNotification(String title, String message, long delayMillis, long notificationId) {
        Data inputData = new Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .putLong("delayMillis",delayMillis)
                .putLong("notificationId", notificationId)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context).enqueue(workRequest);
    }
    public void cancelNotification(long notificationId) {
        WorkManager.getInstance(context).cancelAllWorkByTag(String.valueOf(notificationId));
    }

    public void showNotification(String title, String message, long notificationId) {
        NotificationCompat.Builder builder = getNotificationBuilder(title, message,notificationId);
        notify(builder, (int)notificationId);
    }


}

