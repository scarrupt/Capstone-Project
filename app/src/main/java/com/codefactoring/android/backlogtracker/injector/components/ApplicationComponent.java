package com.codefactoring.android.backlogtracker.injector.components;


import com.codefactoring.android.backlogtracker.injector.modules.ApplicationModule;
import com.codefactoring.android.backlogtracker.injector.modules.BacklogModule;
import com.codefactoring.android.backlogtracker.view.account.AccountActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, BacklogModule.class})
public interface ApplicationComponent {
    void inject(AccountActivity accountActivity);
}
