package com.codefactoring.android.backlogtracker.view.util;

import android.content.Context;
import android.text.format.DateUtils;

import com.codefactoring.android.backlogapi.BacklogApiConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static String convertUTCDateTimeToLocalDateTime(Context context, String dateTimeUTC) {
        try {
            final Date date = new SimpleDateFormat(BacklogApiConstants.DATE_FORMAT_PATTERN,
                    Locale.getDefault()).parse(dateTimeUTC);
            return DateUtils
                    .formatDateTime(context, date.getTime(), DateUtils.FORMAT_SHOW_YEAR |
                            DateUtils.FORMAT_SHOW_DATE |
                            DateUtils.FORMAT_SHOW_TIME);
        } catch (ParseException e) {
            return null;
        }
    }
}
