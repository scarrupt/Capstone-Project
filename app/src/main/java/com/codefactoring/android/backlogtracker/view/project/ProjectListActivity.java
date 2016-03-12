package com.codefactoring.android.backlogtracker.view.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codefactoring.android.backlogtracker.R;

public class ProjectListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        setTitle(R.string.title_activity_project_list);
    }
}
