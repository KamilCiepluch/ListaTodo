package com.example.listapplication_final;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.listapplication_final.NotificationHelper;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Odczytaj dane wejściowe
        Data inputData = getInputData();
        String title = inputData.getString("title");
        String message = inputData.getString("message");
        long notificationId = inputData.getLong("notificationId", 0);

        // Wyświetl powiadomienie
        showNotification(title, message, notificationId);

        return Result.success();
    }

    private void showNotification(String title, String message, long notificationId) {
        Context context = getApplicationContext();
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder builder = notificationHelper.getNotificationBuilder(title, message,notificationId);
        notificationHelper.notify(builder, (int)notificationId);
    }
}