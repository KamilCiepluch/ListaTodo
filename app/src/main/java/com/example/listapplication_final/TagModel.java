package com.example.listapplication_final;

public class TagModel {

    private final int tagID;
    private final String name;
    private boolean active;

    public TagModel(int tagID,String name, boolean active)
    {
        this.tagID = tagID;
        this.name = name;
        this.active = active;
    }


    public int getTagID() {
        return tagID;
    }
    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
