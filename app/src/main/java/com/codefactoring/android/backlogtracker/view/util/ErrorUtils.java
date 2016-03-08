package com.codefactoring.android.backlogtracker.view.util;

import android.content.Context;

import com.codefactoring.android.backlogapi.BacklogApiErrorConstants;
import com.codefactoring.android.backlogapi.BacklogApiException;
import com.codefactoring.android.backlogtracker.R;

public final class ErrorUtils {

    private ErrorUtils() {
    }

    public static String getErrorMessage(Context context, Throwable throwable) {
        if (throwable instanceof BacklogApiException) {
            final BacklogApiException exception = (BacklogApiException) throwable;
            switch (exception.getErrorCode()) {
                case BacklogApiErrorConstants.INTERNAL_ERROR:
                    return context.getString(R.string.error_server_internal);
                case BacklogApiErrorConstants.LICENCE_ERROR:
                    return context.getString(R.string.error_licence_operation);
                case BacklogApiErrorConstants.LICENCE_EXPIRED_ERROR:
                    return context.getString(R.string.error_licence_expired);
                case BacklogApiErrorConstants.ACCESS_DENIED_ERROR:
                    return context.getString(R.string.error_access_denied);
                case BacklogApiErrorConstants.UNAUTHORIZED_OPERATION_ERROR:
                    return context.getString(R.string.error_unauthorized_operation);
                case BacklogApiErrorConstants.NO_RESOURCE_ERROR:
                    return context.getString(R.string.error_no_resource);
                case BacklogApiErrorConstants.INVALID_REQUEST_ERROR:
                    return context.getString(R.string.error_invalid_request);
                case BacklogApiErrorConstants.SPACE_OVER_CAPACITY_ERROR:
                    return context.getString(R.string.error_space_over_capacity);
                case BacklogApiErrorConstants.RESOURCE_OVERFLOW_ERROR:
                    return context.getString(R.string.error_resource_overflow);
                case BacklogApiErrorConstants.TOO_LARGE_FILE_ERROR:
                    return context.getString(R.string.error_too_large_file);
                case BacklogApiErrorConstants.AUTHENTICATION_ERROR:
                    return context.getString(R.string.error_authentication);
                default:
                    return context.getString(R.string.error_default);
            }
        } else {
            return context.getString(R.string.error_default);
        }
    }
}