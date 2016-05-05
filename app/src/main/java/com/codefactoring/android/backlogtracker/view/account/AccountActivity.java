package com.codefactoring.android.backlogtracker.view.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.codefactoring.android.backlogapi.BacklogApiClient;
import com.codefactoring.android.backlogapi.models.User;
import com.codefactoring.android.backlogtracker.BacklogTrackerApplication;
import com.codefactoring.android.backlogtracker.Config;
import com.codefactoring.android.backlogtracker.R;
import com.codefactoring.android.backlogtracker.view.project.ProjectListActivity;
import com.codefactoring.android.backlogtracker.view.util.ErrorUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AccountActivity extends AccountAuthenticatorActivity {

    @Bind(R.id.text_space_key)
    EditText mSpaceKeyView;

    @Bind(R.id.text_api_key)
    EditText mApiKeyView;

    @Bind(R.id.progress_login)
    ProgressBar mLoadingIndicator;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    AccountManager mAccountManager;

    @Inject
    BacklogApiClient mBacklogApiClient;

    @Inject
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initButterKnife();
        initializeDependencyInjector();
        initializeAnalytics();

        setSupportActionBar(mToolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(R.string.app_name);
        }
    }

    private void initializeAnalytics() {
        mTracker.setScreenName(AccountActivity.class.getSimpleName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initButterKnife() {
        ButterKnife.bind(this);
    }

    private void initializeDependencyInjector() {
        ((BacklogTrackerApplication) getApplication())
                .getApplicationComponent()
                .inject(this);
    }

    @OnClick(R.id.button_next)
    public void next() {
        final String spaceKey = mSpaceKeyView.getText().toString();
        final String apiKey = mApiKeyView.getText().toString();

        storeAccountInfo(spaceKey, apiKey);
    }

    private void storeAccountInfo(String spaceKey, String apiKey) {
        resetErrors();

        boolean hasError = false;

        if (isEmpty(spaceKey)) {
            showSpaceKeyRequiredFieldError();
            hasError = true;

        } else if (!isSpaceKeyLengthValid(spaceKey)) {
            showSpaceKeyFieldLengthError();
            hasError = true;
        }

        if (isEmpty(apiKey)) {
            showApiKeyRequiredFieldError();
            hasError = true;
        }

        if (!hasError) {
            mBacklogApiClient
                    .connectWith(spaceKey, apiKey)
                    .getUserOperations()
                    .getOwnUser()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new AuthenticationSubscriber(spaceKey, apiKey));
            showLoadingIndicator();
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private boolean isSpaceKeyLengthValid(String spaceKey) {
        return spaceKey.length() >= 3 && spaceKey.length() <= 10;
    }

    private void resetErrors() {
        resetApiKeyFieldError();
        resetSpaceKeyFieldError();
    }

    public void showSpaceKeyRequiredFieldError() {
        mSpaceKeyView.setError(getString(R.string.error_field_required));
        mSpaceKeyView.requestFocus();
    }

    public void showSpaceKeyFieldLengthError() {
        mSpaceKeyView.setError(getString(R.string.error_space_key_length));
        mSpaceKeyView.requestFocus();
    }

    public void showApiKeyRequiredFieldError() {
        mApiKeyView.setError(getString(R.string.error_field_required));
        mApiKeyView.requestFocus();
    }

    public void resetApiKeyFieldError() {
        mApiKeyView.setError(null);
    }

    public void resetSpaceKeyFieldError() {
        mSpaceKeyView.setError(null);
    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, AccountActivity.class);
    }

    private class AuthenticationSubscriber extends rx.Subscriber<User> {

        private final String mSpaceKey;
        private final String mApiKey;

        public AuthenticationSubscriber(String spaceKey, String apiKey) {
            mSpaceKey = spaceKey;
            mApiKey = apiKey;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable throwable) {
            hideLoadingIndicator();
            final AlertDialog alertDialog = new AlertDialog
                    .Builder(AccountActivity.this)
                    .setTitle(getString(R.string.error_dialog_title))
                    .setMessage(ErrorUtils.getErrorMessage(getApplicationContext(), throwable))
                    .setNeutralButton(getString(R.string.error_dialog_button_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    }).create();

            alertDialog.show();
        }

        @Override
        public void onNext(User user) {
            hideLoadingIndicator();
            final Intent intent = new Intent();
            final String accountType = getString(R.string.account_type);
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, user.getUserId());
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
            intent.putExtra(AccountManager.KEY_AUTHTOKEN, mApiKey);

            final Account account = new Account(mSpaceKey, accountType);

            final Bundle userData = new Bundle();
            userData.putString(Config.KEY_SPACE_KEY, mSpaceKey);
            userData.putString(Config.KEY_API_KEY, mApiKey);

            mAccountManager.addAccountExplicitly(account, null, userData);

            // set the auth token we got (Not setting the auth token will cause
            // another call to the server to authenticate the user)
            mAccountManager.setAuthToken(account, getString(R.string.auth_token_type), mApiKey);

            // Our base class can do what Android requires with the
            // KEY_ACCOUNT_AUTHENTICATOR_RESPONSE extra that onCreate has
            // already grabbed
            setAccountAuthenticatorResult(intent.getExtras());
            // Tell the account manager settings page that all went well
            setResult(RESULT_OK, intent);

            ContentResolver.setSyncAutomatically(account, getApplication().getString(R.string.content_authority), true);

            startActivity(ProjectListActivity.makeIntent(getApplicationContext()));
            finish();
        }
    }

    public void showLoadingIndicator() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    public void hideLoadingIndicator() {
        mLoadingIndicator.setVisibility(View.GONE);
    }
}