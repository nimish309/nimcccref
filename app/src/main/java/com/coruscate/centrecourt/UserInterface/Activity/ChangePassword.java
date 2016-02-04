package com.coruscate.centrecourt.UserInterface.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.TypedfacedButton;
import com.coruscate.centrecourt.CustomControls.TypefacedEditText;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONObject;
import org.json.JSONStringer;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.etOldPassWord)
    TypefacedEditText etOldPassWord;
    @InjectView(R.id.etNewPassword)
    TypefacedEditText etNewPassword;
    @InjectView(R.id.etConfirmPassword)
    TypefacedEditText etConfirmPassword;
    @InjectView(R.id.btnSubmit)
    TypedfacedButton btnSubmit;
    private String oldpassword, newPassword, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.inject(this);
        setUpToolbar();
        btnSubmit.setOnClickListener(this);
        if (AppConstant.isAndroid5()) {
            btnSubmit.setBackground(getDrawable(R.drawable.ripple_accent_round_corner));
        }
        etConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        try {
                            changePassWord(v);
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


    }

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("Change Password", this));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeActivity() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem itemSearch = menu.findItem(R.id.itemSearch);
        final MenuItem itemCart = menu.findItem(R.id.itemMyCart);
        itemSearch.setVisible(false);
        itemCart.setVisible(false);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmit) {
            changePassWord(v);
        }
    }

    private void changePassWord(View v) {
        if (AppConstant.isNetworkAvailable(ChangePassword.this)) {
            oldpassword = etOldPassWord.getText().toString();
            newPassword = etNewPassword.getText().toString();
            confirmPassword = etConfirmPassword.getText().toString();
            if (oldpassword.length() > 0) {
                if (newPassword.length() > 0) {
                    if (confirmPassword.length() > 0) {
                        if (newPassword.equals(confirmPassword)) {
                            new ChangePasswordTask(v).execute();
                        } else {
                            AppConstant.displayErroMessage(v, "Confirm password not match", ChangePassword.this);
                        }
                    } else {
                        AppConstant.displayErroMessage(v, "Please enter confirm password", ChangePassword.this);
                    }
                } else {
                    AppConstant.displayErroMessage(v, "Please enter new password", ChangePassword.this);
                }
            } else {
                AppConstant.displayErroMessage(v, "Please enter ol password", ChangePassword.this);
            }
        } else {
            AppConstant.showNetworkError(ChangePassword.this);
        }
    }

    public class ChangePasswordTask extends AsyncTask<Void, Void, Void> {

        JSONObject jObj;
        boolean flag = false;
        private int responseCode;
        private String message;
        private View v;
        CustomProgressDialog dialog;

        public ChangePasswordTask(View v) {
            this.v = v;
            dialog = CustomProgressDialog.createProgressBar(ChangePassword.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONParser jsonParser = new JSONParser(ChangePassword.this);
                JSONStringer jsonData = new JSONStringer().object()
                        .key("old_password").value(oldpassword)
                        .key("password").value(newPassword).endObject();
                String[] data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_change_password, jsonData.toString());

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
                        setResult(12);
                        finish();
                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                        AppConstant.showToastShort(ChangePassword.this, message);
                    } else {
                        AppConstant.displayErroMessage(v, message, ChangePassword.this);
                    }

                } else {
                    AppConstant.showNetworkError(ChangePassword.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
