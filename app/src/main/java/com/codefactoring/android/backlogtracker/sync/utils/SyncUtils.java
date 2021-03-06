package com.codefactoring.android.backlogtracker.sync.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.codefactoring.android.backlogapi.BacklogApiConstants.DATE_FORMAT_PATTERN;

public final class SyncUtils {

    private SyncUtils() {
    }

    public static String formatDate(Date date) {
        if (date != null) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN,
                    Locale.getDefault());
            return simpleDateFormat.format(date);
        }

        return null;
    }

    /*
     * From Google iosched 2015
     */
    public static String computeWeakHash(String string) {
        return String.format(Locale.US, "%08x%08x", string.hashCode(), string.length());
    }
}


