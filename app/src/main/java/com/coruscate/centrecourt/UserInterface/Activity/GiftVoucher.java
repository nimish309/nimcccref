package com.coruscate.centrecourt.UserInterface.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.TypedfacedButton;
import com.coruscate.centrecourt.CustomControls.TypefacedEditText;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.AvenuesParams;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.ServiceUtility;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by cis on 7/28/2015.
 */
public class GiftVoucher extends AppCompatActivity implements
        android.content.DialogInterface.OnClickListener {
    public static final String TAG = "Gift Voucher ";
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.etYourName)
    TypefacedEditText etYourName;
    @InjectView(R.id.etYourEmail)
    TypefacedEditText etYourEmail;
    @InjectView(R.id.etYourContact)
    TypefacedEditText etYourContact;
    @InjectView(R.id.etReceipentName)
    TypefacedEditText etReceipentName;
    @InjectView(R.id.etReceipentEmail)
    TypefacedEditText etReceipentEmail;
    @InjectView(R.id.etReceipentContact)
    TypefacedEditText etReceipentContact;
    @InjectView(R.id.etMessage)
    TypefacedEditText etMessage;
    @InjectView(R.id.etAmount)
    TypefacedEditText etAmount;
    @InjectView(R.id.etTheme)
    TypefacedEditText etTheme;
    @InjectView(R.id.chkTerms)
    CheckBox chkTerms;
    @InjectView(R.id.btnContinue)
    TypedfacedButton btnContinue;
    private AlertDialog alert;
    int selectedPositionWeight = -1;
    String[] listItems;
    int selectedThemePosition = -1;

    // Gift voucher Theme
//    define('GIFT_CERTY_THEME_BIRTHDAY','1');
//    define('GIFT_CERTY_THEME_CHRISMAS','2');
//    define('GIFT_CERTY_THEME_GENERAL','3');


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gift_voucher_frag);
        ButterKnife.inject(this);

        setUpToolbar();

        try {
            JSONObject jobj = new JSONObject(UserDataPreferences.getUserInfo(GiftVoucher.this));
            etYourName.setText(JSONData.getString(jobj, "first_name") + " " + JSONData.getString(jobj, "last_name"));
            etYourContact.setText(JSONData.getString(jobj, "mobile"));
            etYourEmail.setText(JSONData.getString(jobj, "email"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etYourName.getText().length() == 0 || etYourEmail.getText().length() == 0 || etYourContact.getText().length() == 0 ||
                        etReceipentName.getText().length() == 0 || etReceipentEmail.getText().length() == 0 || etReceipentContact.getText().length() == 0
                        || etAmount.getText().length() == 0) {
                    AppConstant.displayErroMessage(etYourName, "Fill necessary fields", GiftVoucher.this);
                } else if (!chkTerms.isChecked()) {
                    AppConstant.displayErroMessage(etYourName, "Please accept the terms.", GiftVoucher.this);
                } else if (!AppConstant.isValidEmailAddress(etYourEmail.getText().toString())) {
                    AppConstant.displayErroMessage(etYourName, "Please enter valid Email Id.", GiftVoucher.this);
                } else if (!AppConstant.isValidEmailAddress(etReceipentEmail.getText().toString())) {
                    AppConstant.displayErroMessage(etYourName, "Please enter valid Receipent's Email Id.", GiftVoucher.this);
                } else {
                    new AddGiftVoucherTask().execute(etYourName.getText().toString(), etYourEmail.getText().toString(),
                            etYourContact.getText().toString(), etReceipentName.getText().toString(),
                            etReceipentEmail.getText().toString(), etReceipentContact.getText().toString()
                            , etMessage.getText().toString(), etAmount.getText().toString());
                }
            }
        });

        etTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTheme();
            }
        });
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

    public void selectTheme() {
        listItems = getResources().getStringArray(R.array.gift_theme);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setSingleChoiceItems(listItems, (selectedThemePosition - 1), this);
        alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        alert.dismiss();

        selectedThemePosition = which + 1;
        etTheme.setText(listItems[which]);
    }


    @Override
    public void onBackPressed() {
        closeActivity();
    }

    private void closeActivity() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("Gift Voucher", this));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    public class AddGiftVoucherTask extends AsyncTask<String, Void, String[]> {

        CustomProgressDialog dialog;

        public AddGiftVoucherTask() {
            dialog = CustomProgressDialog.createProgressBar(GiftVoucher.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        protected String[] doInBackground(String... params) {
            try {
                JSONParser jsonParser = new JSONParser(GiftVoucher.this);
                JSONStringer jsonData = new JSONStringer().object()
                        .key("added_by").value(UserDataPreferences.getUserId(GiftVoucher.this))
                        .key("from_name").value(params[0])
                        .key("from_email").value(params[1])
                        .key("from_mobile").value(params[2])
                        .key("to_name").value(params[3])
                        .key("to_email").value(params[4])
                        .key("to_mobile").value(params[5])
                        .key("message").value(params[6])
                        .key("amount").value(params[7])
                        .key("is_active").value(true);
                if (selectedThemePosition != -1)
                    jsonData.key("theme").value(selectedThemePosition);

                jsonData.endObject();

                return jsonParser.sendPostReq(Constants.api_v1 + Constants.api_add_gift_voucher, jsonData.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if (dialog.isShowing())
                dialog.dismiss();
            if (null != strings) {
                if (Integer.parseInt(strings[0]) == 200) {
                    try {
                        JSONObject jObj = new JSONObject(strings[1]);
                        if (jObj.getBoolean("flag")) {
                            JSONObject dataObj = jObj.getJSONObject("data");
                            callPaymentGateway(dataObj.getString("code"), dataObj.getString("from_name"),
                                    dataObj.getString("from_email"), dataObj.getString("from_mobile"));
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    AppConstant.showNetworkError(GiftVoucher.this);
                }
            }
        }

    }

    private void callPaymentGateway(String orderId, String fromName, String fromEmail, String fromContactNo) throws JSONException {

        String bill_address = "The Centre Court Near Ambikaniketan Bus- Stand, Parle Point, Athwalines";
        String bill_country = "India";
        String bill_city = "Surat";
        String bill_state = "Gujarat";
        String bill_pincode = "395005";

        Intent intent = new Intent(GiftVoucher.this, WebViewActivity.class);
        intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(getString(R.string.access)).toString().trim());
        intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderId).toString().trim());
        intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(getString(R.string.merchant)).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_NAME, ServiceUtility.chkNull(fromName).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_ADDRESS, ServiceUtility.chkNull(bill_address).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_COUNTRY, ServiceUtility.chkNull(bill_country).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_STATE, ServiceUtility.chkNull(bill_state).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_CITY, ServiceUtility.chkNull(bill_city).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_ZIP, ServiceUtility.chkNull(bill_pincode).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_TEL, ServiceUtility.chkNull(fromContactNo).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_EMAIL, ServiceUtility.chkNull(fromEmail).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_NAME, ServiceUtility.chkNull(fromName).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_ADDRESS, ServiceUtility.chkNull(bill_address).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_COUNTRY, ServiceUtility.chkNull(bill_country).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_STATE, ServiceUtility.chkNull(bill_state).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_CITY, ServiceUtility.chkNull(bill_city).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_ZIP, ServiceUtility.chkNull(bill_pincode).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_TEL, ServiceUtility.chkNull(fromContactNo).toString().trim());
        intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull("INR").toString().trim());
        intent.putExtra(AvenuesParams.MERCHANT_PARAM1, orderId);
        intent.putExtra(AvenuesParams.MERCHANT_PARAM2, "true");
        intent.putExtra(AvenuesParams.MERCHANT_PARAM3, "mobapp");
        intent.putExtra(AvenuesParams.IS_GIFT_VOUCHER, true);
        intent.putExtra(AvenuesParams.AMOUNT, "1");
        intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(Constants.api_v1 + Constants.api_get_rsa).toString().trim());
        startActivity(intent);
    }
}
