package com.coruscate.centrecourt.UserInterface.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.TypedfacedButton;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener, OnPermissionCallback {
    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "MainActivity";
    private final String SINGLE_PERMISSION = Manifest.permission.GET_ACCOUNTS;
    Button btnLogin;
    ProgressDialog dialog;
    CallbackManager callbackManager;
    android.app.AlertDialog.Builder alert;
    private PermissionHelper permissionHelper;
    private AlertDialog builder;
    private ProgressDialog LogInActivityDialog;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private LinearLayout layoutCantConnect;
    private ConnectionResult mConnectionResult;

    private EditText etForgotPass;
    private EditText etEmail, etPassword;
    private TypedfacedButton btnTryAgain;
    private TextView txtBottomSignup, txtfrgtPassword;
    //    private SignInButton btnSignIn;
    private Button btnSignIn;
    private int loginType;// 1: simple , 2 facebook, 3 gmail
    private String loginId, loginName, loginEmail, loginImageUrl;

    // Twitter
//    private static Twitter twitter;
//    private static RequestToken requestToken;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LogInActivityDialog = new ProgressDialog(LoginActivity.this);
        LogInActivityDialog.setCancelable(false);
        layoutCantConnect = (LinearLayout) findViewById(R.id.layoutCantConnect);
        LogInActivityDialog.setMessage("Please wait...");
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnTryAgain = (TypedfacedButton) findViewById(R.id.btnTryAgain);
        if (AppConstant.isAndroid6()) {
            permissionHelper = PermissionHelper.getInstance(this);
        }
        btnTryAgain.setOnClickListener(this);
//        btnSignIn = (SignInButton) findViewById(R.id.gmail_login_button);
        btnSignIn = (Button) findViewById(R.id.gmail_login_button);
        txtBottomSignup = (TextView) findViewById(R.id.txtBottomSignup);
        txtfrgtPassword = (TextView) findViewById(R.id.txtfrgtPassword);
        txtBottomSignup.setOnClickListener(this);
        txtfrgtPassword.setOnClickListener(this);
        // Button click listeners
        btnSignIn.setOnClickListener(this);

        if (AppConstant.isAndroid5()) {
            btnLogin.setBackground(getDrawable(R.drawable.ripple_accent_round_corner));
        }
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        try {
                            logIn(1, v);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        Button btnFacebookLogin = (Button) findViewById(R.id.btnFacebookLogin);
        btnFacebookLogin.setOnClickListener(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                loginId = Profile.getCurrentProfile().getId();
                                loginName = Profile.getCurrentProfile().getName();
                                loginEmail = object.optString("email");
                                loginImageUrl = Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString();
                                if (AppConstant.isNetworkAvailable(LoginActivity.this)) {
                                    loginType = 2;
                                    new LoginTask(loginId, loginName, loginEmail, loginImageUrl, 2).execute();
                                } else {
                                    AppConstant.showNetworkError(LoginActivity.this);
                                }

                              /*  Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("name", Profile.getCurrentProfile().getName());
                                intent.putExtra("image", Profile.getCurrentProfile().getId());
                                startActivity(intent);
                                overridePendingTransition(R.anim.start_activity, R.anim.close_activity);
                                finish();*/


//                                Log.v("Email", "Resp : " + email);
                                Log.d("Facebook", Profile.getCurrentProfile().getName() + "\n" + Profile.getCurrentProfile().getId()
                                        + "\n" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));


                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
//        Toast.makeText(this, "onConnection Failed", Toast.LENGTH_SHORT).show();
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (null != dialog) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        // Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();

        // Update the UI after signin
        updateUI(true);

    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                // Log.d("getAboutMe", currentPerson.getAboutMe());
                // Log.d("getBirthday", currentPerson.getBirthday());
                // Log.d("getBraggingRights",
                // currentPerson.getBraggingRights());
                // Log.d("getCurrentLocation",
                // currentPerson.getCurrentLocation());
                // Log.d("getDisplayName", currentPerson.getDisplayName());
                // Log.d("getId", currentPerson.getId());
                // Log.d("getLanguage", currentPerson.getLanguage());
                // Log.d("getNickname", currentPerson.getNickname());
                // Log.d("getTagline", currentPerson.getTagline());
                // Log.d("getUrl", currentPerson.getUrl());
                // Log.d("getAgeRange", "" + currentPerson.getAgeRange());
                // Log.d("getCircledByCount",
                // "" + currentPerson.getCircledByCount());
                // Log.d("getCover", "" + currentPerson.getCover());
                // Log.d("getUrls", "" + currentPerson.getUrls());
                // Log.d("getPlacesLived", "" + currentPerson.getPlacesLived());
                // Log.d("getOrganizations", "" +
                // currentPerson.getOrganizations());
                // Log.d("getName", "" + currentPerson.getName());
                // Log.d("getGender", "" + currentPerson.getGender());
//                Toast.makeText(LoginActivity.this, currentPerson.getDisplayName(), Toast.LENGTH_LONG).show();
                loginName = currentPerson.getDisplayName();
                loginId = currentPerson.getId();
                loginImageUrl = currentPerson.getImage().getUrl();


                loginEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

                if (AppConstant.isNetworkAvailable(LoginActivity.this)) {
                    loginType = 3;
                    new LoginTask(loginId, loginName, loginEmail, loginImageUrl, 3).execute();
                } else {
                    AppConstant.showNetworkError(LoginActivity.this);
                }

/*
                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
              /* personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;*/
                // email = "";
//                if (email.equals("")) {
//                    AppConstant
//                            .singleButtonAlertDialog(
//                                    LoginActivity.this,
//                                    "Error",
//                                    "Unable to find Email Address from Google Plus.So please register with us  to use this application.");
//
//                } else {
//                    new LoginTask(LoginActivity.this, true, false, personName,
//                            email, id, personPhotoUrl, dob,
//                            shouldShowMainActivity, "").execute();
//                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != dialog) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        updateUI(false);
    }

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     */
    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            // btnSignIn.setVisibility(View.GONE);
            signOutFromGplus();
        } else {
            btnSignIn.setVisibility(View.VISIBLE);

        }
    }

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (null != mConnectionResult) {
            if (mConnectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    mConnectionResult
                            .startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    /**
     * Sign-out from google
     */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            updateUI(false);
        }
    }

    /**
     * Revoking access from google
     */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();
                            updateUI(false);
                        }

                    });
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.gmail_login_button:
                // mGoogleApiClient.connect();
//                if (AppConstant.isNetworkAvailable(LoginActivity.this)) {
                if (AppConstant.isAndroid6()) {
                    permissionHelper
                            .setForceAccepting(false) // default is false. its here so you know that it exists.
                            .request(SINGLE_PERMISSION);
                } else {
                    signInGoogle();
                }
//                } else {
//                    Toast.makeText(LoginActivity.this,
//                            "No Internet Connection Found.", Toast.LENGTH_LONG)
//                            .show();
//                }
                break;
            case R.id.btnFacebookLogin:
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends", "email"));
                break;
            case R.id.btnLogin:
                logIn(1, v);
                break;

            case R.id.btnTryAgain:
                layoutCantConnect.setVisibility(View.GONE);
                if (AppConstant.isNetworkAvailable(LoginActivity.this)) {
                    if (loginType == 1) {
                        logIn(1, v);
                    } else if (loginType == 2) {
                        new LoginTask(loginId, loginName, loginEmail, loginImageUrl, 2).execute();
                    } else if (loginType == 3) {
                        new LoginTask(loginId, loginName, loginEmail, loginImageUrl, 3).execute();
                    }
                } else {
                    AppConstant.showNetworkError(LoginActivity.this);
                }
                break;

            case R.id.txtBottomSignup:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;

            case R.id.txtfrgtPassword:
                showForgotPasswordDialog(v);
                break;
            default:
                break;
        }
    }

    private void showForgotPasswordDialog(final View v) {
        alert = new android.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.GeneralTheme));
        alert.setTitle("Enter email id code received on email account. ");
        alert.setCancelable(false);
        // Set an EditText view to get user input
        etForgotPass = new EditText(this);
        etForgotPass.setHint("Email id");
        etForgotPass.setSingleLine(true);
        etForgotPass.setMaxEms(50);
        etForgotPass.setBackground(getResources().getDrawable(R.drawable.edittext_selecter));
        int maxLength = 30;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        etForgotPass.setFilters(fArray);
        etForgotPass.setEnabled(true);
        etForgotPass.requestFocus();
        etForgotPass.setInputType(InputType.TYPE_CLASS_TEXT);
        try {
            // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(etForgotPass, R.drawable.cursor);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(10, 10, 10, 10);
        layout.addView(etForgotPass);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 20, 0);
        etForgotPass.setLayoutParams(params);

        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (etForgotPass.getText().toString().length() > 0) {
                    if (AppConstant.isNetworkAvailable(LoginActivity.this)) {
                        if (AppConstant.isValidEmailAddress(etForgotPass.getText().toString())) {
                            new forgotPasswordTask(v).execute();
                        } else {
                            AppConstant.displayErroMessage(v, "Please enter valid email id", LoginActivity.this);
                        }
                    } else {
                        AppConstant.showNetworkError(LoginActivity.this);
                    }
                } else {
                    AppConstant.displayErroMessage(v, "Please enter email id", LoginActivity.this);
                }


            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
        alert.show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void logIn(int type, View v) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (email.length() > 0) {
            if (AppConstant.isValidEmailAddress(email)) {
                if (!password.equals("")) {
                    if (AppConstant.isNetworkAvailable(this)) {
                        loginType = 1;
                        new LoginTask(type, email, password).execute();
                    } else {
                        AppConstant.showNetworkError(this);
                    }
                } else {
                    AppConstant.displayErroMessage(v, "Please enter password.", LoginActivity.this);
                }
            } else {
                AppConstant.displayErroMessage(v, "Please enter valid email id.", LoginActivity.this);
            }
        } else {

            AppConstant.displayErroMessage(v, "Please enter email id.", LoginActivity.this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);

    }

    @Override
    public void onPermissionGranted(String[] permissionName) {
        signInGoogle();
    }

    private void signInGoogle() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
        signInWithGplus();
    }

    @Override
    public void onPermissionDeclined(String[] permissionName) {

    }

    @Override
    public void onPermissionPreGranted(String permissionsName) {
        signInGoogle();
    }

    @Override
    public void onPermissionNeedExplanation(String permissionName) {
        getAlertDialog(permissionName).show();
    }

    public AlertDialog getAlertDialog(final String permission) {
        if (builder == null) {
            builder = new AlertDialog.Builder(this)
                    .setTitle("Grant Contact Permission").create();
        }
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionHelper.requestAfterExplanation(permission);
            }
        });
        builder.setMessage("Contact permission is needed to access user's contacts and profile.");
        return builder;
    }

    @Override
    public void onPermissionReallyDeclined(String permissionName) {

    }

    @Override
    public void onNoPermissionNeeded() {

    }

    public class forgotPasswordTask extends AsyncTask<Void, Void, Void> {
        JSONObject jObj;
        boolean flag = false;
        CustomProgressDialog dialog;
        private int responseCode;
        private String message;
        private View v;

        public forgotPasswordTask(View v) {
            this.v = v;
            dialog = CustomProgressDialog.createProgressBar(LoginActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONParser jsonParser = new JSONParser(LoginActivity.this);
                JSONStringer jsonData = new JSONStringer().object()
                        .key("email").value(etForgotPass.getText().toString()).endObject();
                String[] data;
                data = jsonParser.sendPostReq(Constants.api_ip + Constants.api_forgot_password, jsonData.toString());
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = JSONData.getBoolean(jObj, "flag");
                    message = JSONData.getString(jObj, "message");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (dialog.isShowing())
                    dialog.dismiss();
                if (responseCode == 200) {
                    if (flag) {
                        AppConstant.showToastShort(LoginActivity.this, message);
                    } else {
                        AppConstant.displayErroMessage(v, message, LoginActivity.this);
                    }

                } else {
                    AppConstant.showNetworkError(LoginActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class LoginTask extends AsyncTask<Void, Void, Void> {
        boolean showProgressBar = true, flag = false;
        ProgressDialog dialog;
        String id;
        private Context context;
        private int responseCode;
        private JSONObject jsonObjects;
        private String message;
        private int type;//0 simple...1 facebook..2 google
        private JSONStringer jsonStringer;
        private String user_id, firstName, email, imageUrl, pass;
        private String data[];

        public LoginTask(int type, String email, String password) {
            this.type = type;
            this.email = email;
            this.pass = password;
        }

        public LoginTask(String user_id, String firstName, String email, String imageUrl, int type) {
            this.imageUrl = imageUrl;
            this.email = email;
            this.firstName = firstName;
            this.user_id = user_id;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (dialog == null) {
                dialog = new ProgressDialog(LoginActivity.this);
            }
            dialog.setIndeterminate(true);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jParser = new JSONParser(context);

            try {
                if (type == 1) {
                    jsonStringer = new JSONStringer().object().
                            key("email").value(email).key("password").value(pass)
                            .endObject();
                    data = jParser.sendPostReqLogin(Constants.api_v1 + Constants.api_login, jsonStringer.toString());
                } else if (type == 2) {
                    jsonStringer = new JSONStringer().object().
                            key("facebook_id").value(user_id).key("first_name").value(firstName).
                            key("email").value(email).key("image").value(imageUrl)
                            .endObject();
                    data = jParser.sendPostReqLogin(Constants.api_v1 + Constants.api_facebook_login, jsonStringer.toString());
                } else if (type == 3) {
                    jsonStringer = new JSONStringer().object().
                            key("google_id").value(user_id).key("first_name").value(firstName).
                            key("email").value(email).key("image").value(imageUrl)
                            .endObject();
                    data = jParser.sendPostReqLogin(Constants.api_v1 + Constants.api_google_login, jsonStringer.toString());
                }
                responseCode = Integer.parseInt(data[0]);
                if (responseCode == 200) {
                    jsonObjects = new JSONObject(data[1]);
                    message = JSONData.getString(jsonObjects, "message");
                    flag = JSONData.getBoolean(jsonObjects, "flag");
                    if (flag) {
                        JSONObject dataObj = jsonObjects.getJSONObject("data");
                        UserDataPreferences.saveToken(LoginActivity.this, dataObj.getString("token"));
                        UserDataPreferences.saveUserInfo(LoginActivity.this, dataObj.toString());
                        Calendar calendar = Calendar.getInstance();
                        UserDataPreferences.saveLoginDate(LoginActivity.this, calendar.getTimeInMillis());

                        final int cartCount = Integer.parseInt(dataObj.has("cart_count") ? (dataObj.isNull("cart_count") ? "0" : dataObj.getString("cart_count")) : "0");
                        if (cartCount > 0) {
                            UserDataPreferences.saveCartCount(LoginActivity.this, cartCount);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (responseCode == 200) {
                    if (flag) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.start_activity, R.anim.close_activity);
                        finish();
                    } else {
                        AppConstant.showSingleButtonAlertDialog(LoginActivity.this, "Error", "Either username or password is incorrect.");
                       /* Intent registerActivity = new Intent(
                                LoginActivity.this,
                                RegisterActivity.class);
                        startActivity(registerActivity);
                        overridePendingTransition(R.anim.start_activity, R.anim.close_activity);
                        finish();*/
                    }
                } else {
                    AppConstant.unableConnectServer(LoginActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppConstant.unableConnectServer(LoginActivity.this);
            }
        }
    }

}

//    private void showForgotPasDialog() {
//        // TODO Auto-generated method stub
//        final Dialog showForgotPassDialog = new Dialog(LogInActivity.this);
//        showForgotPassDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        showForgotPassDialog.getWindow().setBackgroundDrawable(
//                new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        showForgotPassDialog.setContentView(R.layout.forgot_pass_dialog);
//        showForgotPassDialog.show();
//
//        final EditText txtForgotEmail = (EditText) showForgotPassDialog
//                .findViewById(R.id.etForgotEmail);
//        txtForgotEmail.setTextColor(getResources().getColor(R.color.black));
//        txtForgotEmail.setHintTextColor(getResources().getColor(R.color.black));
//        Button btnForgotPassSubmit = (Button) showForgotPassDialog
//                .findViewById(R.id.btnForgotPassSubmit);
//
//        btnForgotPassSubmit.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                String forgotEmail = txtForgotEmail.getText().toString();
//
//                if (forgotEmail.length() > 0) {
//
//                    new ForgotPasswordTask(LogInActivity.this, forgotEmail)
//                            .execute();
//
//                } else {
//                    Toast.makeText(LogInActivity.this, "Email is necessary.",
//                            Toast.LENGTH_SHORT).show();
//                }
//                showForgotPassDialog.dismiss();
//            }
//
//        });
//        showForgotPassDialog.show();
//    }
