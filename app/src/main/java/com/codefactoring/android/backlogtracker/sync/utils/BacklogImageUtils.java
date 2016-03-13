package com.codefactoring.android.backlogtracker.sync.utils;

import android.util.Log;

import java.io.FileOutputStream;

public final class BacklogImageUtils {

    private static final String LOG_TAG = BacklogImageUtils.class.getSimpleName();

    private BacklogImageUtils() {
    }

    public static void saveImage(String imagePath, byte[] data) {

        try {
            final FileOutputStream fos = new FileOutputStream(imagePath);
            fos.write(data);
            fos.close();
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Problem saving image", ex);
        }
    }
}