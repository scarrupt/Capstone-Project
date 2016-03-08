package com.codefactoring.android.backlogtracker;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;

public class BacklogTrackerTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context) throws InstantiationException,
                IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestApplication.class.getCanonicalName(), context);
    }
}
