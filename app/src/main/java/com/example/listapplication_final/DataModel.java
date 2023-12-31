package com.example.listapplication_final;

public class DataModel {

    private  long primaryKey;
    private final String title;
    private final String description;
    private final String creation_time;
    private final String execution_time;
    private final boolean isFinished;
    private final boolean notifications;
    private final int categoryId;
    private final byte[] image;
    public DataModel(long primaryKey, String title, String description,
                     String creation_time, String execution_time,
                     boolean status, boolean notifications, int categoryId, byte[] image) {
        this.primaryKey = primaryKey;
        this.title = title;
        this.description = description;
        this.creation_time = creation_time;
        this.execution_time = execution_time;
        this.isFinished = status;
        this.notifications = notifications;
        this.categoryId = categoryId;
        this.image = image;
    }

    public DataModel( String title, String description,
                     String creation_time, String execution_time,
                     boolean status, boolean notifications, int categoryId, byte[] image) {
        this.title = title;
        this.description = description;
        this.creation_time = creation_time;
        this.execution_time = execution_time;
        this.isFinished = status;
        this.notifications = notifications;
        this.categoryId = categoryId;
        this.image = image;
    }


    public long getPrimaryKey() {
        return primaryKey;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getCreationTime() {
        return creation_time;
    }

    public String getExecutionTime() {
        return execution_time;
    }


    public boolean getFinished() {
        return isFinished;
    }

    public boolean getNotifications() {
        return notifications;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public byte[] getImage() {
        return image;
    }
}