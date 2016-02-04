package com.coruscate.centrecourt.UserInterface.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.TypedfacedButton;
import com.coruscate.centrecourt.CustomControls.TypefacedEditText;
import com.coruscate.centrecourt.CustomControls.TypefacedRadioButton;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Fragments.Profile.MyWishListFragment;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONObject;
import org.json.JSONStringer;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsLetterActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.etEmail)
    TypefacedEditText etEmail;
    @InjectView(R.id.radioButtonGroup)
    RadioGroup radioButtonGroup;
    @InjectView(R.id.btnSubmit)
    TypedfacedButton btnSubmit;
    @InjectView(R.id.radiobuttonSubscribe)
    TypefacedRadioButton radiobuttonSubscribe;
    @InjectView(R.id.radiobuttonUnSubscribe)
    TypefacedRadioButton radiobuttonUnSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_letter);
        ButterKnife.inject(this);
        setUpToolbar();
        btnSubmit.setOnClickListener(this);
        if (AppConstant.isAndroid5()) {
            btnSubmit.setBackground(getDrawable(R.drawable.ripple_accent_round_corner));
        }
        String strUserInfo = UserDataPreferences.getUserInfo(NewsLetterActivity.this);
        if (strUserInfo.length() > 0) {
            try {
                JSONObject jsonObject = new JSONObject(strUserInfo);
                etEmail.setText(jsonObject.has("email") ? (jsonObject.isNull("email") ? "" : jsonObject.getString("email")) : "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("News Letter", this));
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
            if (AppConstant.isNetworkAvailable(NewsLetterActivity.this)) {
                String email = etEmail.getText().toString();
                if (email.length() > 0) {
                    if (AppConstant.isValidEmailAddress(email)) {
                        new SubUnsubNewsLetterTask(v).execute();
                    } else {
                        AppConstant.displayErroMessage(v, "Please enter valid email id", NewsLetterActivity.this);
                    }
                } else {
                    AppConstant.displayErroMessage(v, "Please enter email id", NewsLetterActivity.this);
                }
            } else {
                AppConstant.showNetworkError(NewsLetterActivity.this);
            }
        }
    }

    public class SubUnsubNewsLetterTask extends AsyncTask<Void, Void, Void> {

        JSONObject jObj;
        boolean flag = false;
        private int responseCode;
        private String message;
        private View v;
        CustomProgressDialog dialog;

        public SubUnsubNewsLetterTask(View v) {
            this.v = v;
            dialog = CustomProgressDialog.createProgressBar(NewsLetterActivity.this);
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
                JSONParser jsonParser = new JSONParser(NewsLetterActivity.this);
                JSONStringer jsonData = new JSONStringer().object()
                        .key("email").value(etEmail.getText().toString()).endObject();
                String[] data;
                if (radiobuttonSubscribe.isChecked()) {
                    data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_news_letter_subscribe, jsonData.toString());
                } else {
                    data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_news_letter_un_subscribe, jsonData.toString());

                }
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
                        AppConstant.showToastShort(NewsLetterActivity.this, message);
                    } else {
                        AppConstant.displayErroMessage(v, message, NewsLetterActivity.this);
                    }

                } else {
                    AppConstant.showNetworkError(NewsLetterActivity.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}