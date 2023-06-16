package com.example.listapplication_final;

public class Attachment {
    private int dataId;
    private String path;

    public Attachment(int dataId, String path) {
        this.dataId = dataId;
        this.path = path;
    }

    public String getPath() {
        return path;
    }
    public int getDataId() {
        return dataId;
    }
}
