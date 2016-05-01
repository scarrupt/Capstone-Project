package com.codefactoring.android.backlogtracker.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class RefreshInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        final Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
