package com.codefactoring.android.backlogtracker.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.codefactoring.android.backlogtracker.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observable;


public class RegistrationIntentService extends IntentService {

    public static final String LOG_TAG = RegistrationIntentService.class.getSimpleName();

    private static final String BASE_URL = "http://localhost:8080/_ah/api/";

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID instanceId = InstanceID.getInstance(this);
        final String senderId = getString(R.string.gcm_defaultSenderId);
        try {
            final String token = instanceId.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            final Registration registration = new Registration();
            registration.setRegId(token);

            sendRegistrationToServer(token);

        } catch (Throwable ioEx) {
            Log.e(LOG_TAG, "Could not register device", ioEx);
        }
    }

    private void sendRegistrationToServer(String token) throws IOException {
        final Registration registration = new Registration();
        registration.setRegId(token);
        final Observable<ResponseBody> register = new UpdateApiClient().getRegisterOperations().register(registration);
        register.toBlocking().first();
    }
}