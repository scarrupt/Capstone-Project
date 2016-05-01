package com.codefactoring.android.backlogtracker.injector.components;


import com.codefactoring.android.backlogtracker.gcm.GcmMessageHandler;
import com.codefactoring.android.backlogtracker.gcm.RegistrationIntentService;
import com.codefactoring.android.backlogtracker.injector.modules.ApplicationModule;
import com.codefactoring.android.backlogtracker.injector.modules.BacklogModule;
import com.codefactoring.android.backlogtracker.sync.BacklogSyncService;
import com.codefactoring.android.backlogtracker.view.account.AccountActivity;
import com.codefactoring.android.backlogtracker.view.issue.IssueDetailActivity;
import com.codefactoring.android.backlogtracker.view.issue.IssuesMainActivity;
import com.codefactoring.android.backlogtracker.view.project.ProjectListActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, BacklogModule.class})
public interface ApplicationComponent {
    void inject(AccountActivity accountActivity);
    void inject(ProjectListActivity projectListActivity);
    void inject(IssuesMainActivity issuesMainActivity);
    void inject(IssueDetailActivity issueDetailActivity);
    void inject(BacklogSyncService service);
    void inject(RegistrationIntentService service);
    void inject(GcmMessageHandler handler);

}
