package com.example.utmpapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {


    private UserRegisterTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mFirstNameView;
    private EditText mUserIDView;
    private EditText mConfirmPasswordView;
    private EditText mSetThresholdView;
    private View mProgressView;
    private View mLoginFormView;

    public static final String CREDENTIALS = "Credentials" ;
    public static final String EMAIL_PREF = "emailKey";
    public static final String PASSWORD_PREF = "passwordKey";
    public static final String NAME_PREF = "nameKey";
    public static final String USERID_PREF = "useridKey";
    public static final String THRESHOLD_PREF = "thresholdKey";
    public static final String DEVICES_PREF = "deviceKey";
    public static final String LOGIN_PREF = "loginKey";
    public static final int defaultDevices = 0;
    public static final boolean defaultLogin = false;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupActionBar();
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.register_email);

        mPasswordView = (EditText) findViewById(R.id.register_password);
        mFirstNameView = (EditText) findViewById(R.id.register_firstName);
        mUserIDView = (EditText)findViewById(R.id.register_userID);
        mConfirmPasswordView = (EditText) findViewById(R.id.register_confirmPassword);
        mSetThresholdView = (EditText) findViewById(R.id.register_setThreshold);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_register_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    /*private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }*/

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mFirstNameView.setError(null);
        mConfirmPasswordView.setError(null);
        mUserIDView.setError(null);
        mSetThresholdView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String firstName = mFirstNameView.getText().toString();
        String userID = mUserIDView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();
        String setThreshold = mSetThresholdView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password))
        {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;

        }else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid first name.
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }
        // Check for a valid user ID.
        if (TextUtils.isEmpty(userID)) {
            mUserIDView.setError(getString(R.string.error_field_required));
            focusView = mUserIDView;
            cancel = true;
        }else if (!isUserIDValid(userID)) {
            mUserIDView.setError(getString(R.string.error_invalid_userID));
            focusView = mUserIDView;
            cancel = true;
        }
        // Validation for User Threshold
        if (TextUtils.isEmpty(setThreshold)){
            mSetThresholdView.setError(getString(R.string.error_field_required));
            focusView = mSetThresholdView;
            cancel = true;
        } else if (!isThresholdValid(setThreshold)){
            mSetThresholdView.setError(getString(R.string.error_invalid_threshold));
            focusView = mSetThresholdView;
            cancel = true;
        }
        // Check for a valid confirm password, if the user entered one.
        if (TextUtils.isEmpty(confirmPassword))
        {
            mConfirmPasswordView.setError(getString(R.string.error_field_required));
            focusView = mConfirmPasswordView;
            cancel = true;

        }else if (!password.equals(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_invalid_cpassword));
            focusView = mConfirmPasswordView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            mAuthTask = new UserRegisterTask();
            mAuthTask.execute(email, password, firstName, userID, setThreshold);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isUserIDValid(String userID) {
        return userID.length() > 0 && userID.length() <= 10;
    }
    private boolean isThresholdValid(String threshold){
        return Integer.parseInt(threshold) >= 3;
    }
    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<String, Void, Boolean> {

/*        private final String mEmail;
        private final String mPassword;
        private final String mFirstName;
        private final String mUserID;*/

/*
        UserRegisterTask(String email, String password, String firstName, String userID) {
            mEmail = email;
            mPassword = password;
            mFirstName = firstName;
            mUserID = userID;
        }*/

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                return writeCredentials(params[0],params[1],params[2],params[3],params[4]);
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);

            if (success) {
                //finish();
                Toast.makeText(getApplicationContext(),"Hurray!!! You have been registered",Toast.LENGTH_LONG).show();
                Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(loginIntent);
            } else {
                mPasswordView.setError("Something went wrong during registration");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }

        protected boolean writeCredentials(String email, String password, String name, String userid, String threshold) {

            sharedpreferences = getSharedPreferences(CREDENTIALS,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(EMAIL_PREF, email);
            editor.putString(PASSWORD_PREF, password);
            editor.putString(NAME_PREF, name);
            editor.putString(USERID_PREF, userid);
            editor.putInt(THRESHOLD_PREF, Integer.parseInt(threshold));
            editor.putInt(DEVICES_PREF, defaultDevices);
            editor.putBoolean(LOGIN_PREF,defaultLogin);
            return editor.commit();
        }
    }
}

