package com.example.listapplication_final;

public class TagModel {

    public TagModel(String name, boolean active)
    {
        this.name = name;
        this.active = active;
    }
    private final String name;
    private boolean active;

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
