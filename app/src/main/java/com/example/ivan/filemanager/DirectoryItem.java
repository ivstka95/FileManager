package com.example.ivan.filemanager;

/**
 * Created by Иван on 12.01.2017.
 */

public class DirectoryItem {
    private String path;
    private String name;

    public DirectoryItem(String path, String name) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {

        return path;
    }
}
