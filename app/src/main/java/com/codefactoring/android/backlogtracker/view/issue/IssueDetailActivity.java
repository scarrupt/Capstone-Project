package com.codefactoring.android.backlogtracker.view.issue;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codefactoring.android.backlogtracker.BacklogTrackerApplication;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.view.settings.SettingsActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;

public class IssueDetailActivity extends AppCompatActivity {

    @Inject
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_detail);
        initializeDependencyInjector();
        mTracker.setScreenName(IssueDetailActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initializeDependencyInjector() {
        ((BacklogTrackerApplication) getApplication())
                .getApplicationComponent()
                .inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
