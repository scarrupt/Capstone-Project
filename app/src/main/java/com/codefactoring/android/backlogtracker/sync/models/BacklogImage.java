package com.codefactoring.android.backlogtracker.sync.models;

public class BacklogImage {
    private final String filename;
    private final byte[] data;

    public BacklogImage(String filename, byte[] data) {
        this.filename = filename;
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }
}