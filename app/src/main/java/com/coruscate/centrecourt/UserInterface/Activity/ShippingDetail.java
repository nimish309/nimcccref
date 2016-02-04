package com.coruscate.centrecourt.UserInterface.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.coruscate.centrecourt.CustomControls.TypedfacedButton;
import com.coruscate.centrecourt.CustomControls.TypefacedCheckBox;
import com.coruscate.centrecourt.CustomControls.TypefacedEditText;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONObject;
import org.json.JSONStringer;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ShippingDetail extends AppCompatActivity implements View.OnClickListener {
    @InjectView(R.id.checkboxSameAsAbove)
    TypefacedCheckBox checkboxSameAsAbove;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.btnSubmit)
    TypedfacedButton btnSubmit;
    @InjectView(R.id.edtxtFirstName)
    TypefacedEditText edtxtFirstName;
    @InjectView(R.id.edtxtLastName)
    TypefacedEditText edtxtLastName;
    @InjectView(R.id.edtxtAddress1)
    TypefacedEditText edtxtAddress1;
    @InjectView(R.id.edtxtAddress2)
    TypefacedEditText edtxtAddress2;
    @InjectView(R.id.edtxtCity)
    TypefacedEditText edtxtCity;
    @InjectView(R.id.edtxtState)
    TypefacedEditText edtxtState;
    @InjectView(R.id.edtxtCountry)
    TypefacedEditText edtxtCountry;
    @InjectView(R.id.edtxtPinCode)
    TypefacedEditText edtxtPinCode;
    @InjectView(R.id.edtxtBillFirstName)
    TypefacedEditText edtxtBillFirstName;
    @InjectView(R.id.edtxtBillLastName)
    TypefacedEditText edtxtBillLastName;
    @InjectView(R.id.edtxtBillAddress1)
    TypefacedEditText edtxtBillAddress1;
    @InjectView(R.id.edtxtBillAddress2)
    TypefacedEditText edtxtBillAddress2;
    @InjectView(R.id.edtxtBillCity)
    TypefacedEditText edtxtBillCity;
    @InjectView(R.id.edtxtBillState)
    TypefacedEditText edtxtBillState;
    @InjectView(R.id.edtxtBillCountry)
    TypefacedEditText edtxtBillCountry;
    @InjectView(R.id.edtxtBillPinCode)
    TypefacedEditText edtxtBillPinCode;
    @InjectView(R.id.edtxtCouponId)
    TypefacedEditText edtxtCouponId;
    @InjectView(R.id.edtxtStoreName)
    TypefacedEditText edtxtStoreName;
    @InjectView(R.id.edtxtStoreUrl)
    TypefacedEditText edtxtStoreUrl;
    JSONObject jobjShipping = new JSONObject();
    JSONObject jobjBilling = new JSONObject();
    private String products;
    private Snackbar snackbar;
    private String couponId, officeName, officeURL;
    private int deliveryMethod, paymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_detail);
        ButterKnife.inject(this);
        checkboxSameAsAbove.setText(AppConstant.spanFont("Same as above", ShippingDetail.this));
       /* try {
            AppConstant.setToolBarColor(ShippingDetail.this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        setUpToolbar();
        products = getIntent().getStringExtra("products");
        setShippingDetail();
        checkboxSameAsAbove.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
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

    private void closeActivity() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
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

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("Shipping Detail", this));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void setShippingDetail() {
        JSONObject jsonObject;
        if (!UserDataPreferences.getUserShippingDetail(ShippingDetail.this).toString().equals("") || UserDataPreferences.getUserShippingDetail(ShippingDetail.this).toString() != null) {
            if (!UserDataPreferences.getUserBillingDetail(ShippingDetail.this).toString().equals("") || UserDataPreferences.getUserBillingDetail(ShippingDetail.this).toString() != null) {
                try {
                    jsonObject = new JSONObject(UserDataPreferences.getUserBillingDetail(ShippingDetail.this));
                    edtxtFirstName.setText(jsonObject.has("first_name") ? (jsonObject.isNull("first_name") ? "" : jsonObject.getString("first_name")) : "");
                    edtxtLastName.setText(jsonObject.has("last_name") ? (jsonObject.isNull("last_name") ? "" : jsonObject.getString("last_name")) : "");
                    edtxtAddress1.setText(jsonObject.has("address_1") ? (jsonObject.isNull("address_1") ? "" : jsonObject.getString("address_1")) : "");
                    edtxtAddress2.setText(jsonObject.has("address_2") ? (jsonObject.isNull("address_2") ? "" : jsonObject.getString("address_2")) : "");
                    edtxtCity.setText(jsonObject.has("city") ? (jsonObject.isNull("city") ? "" : jsonObject.getString("city")) : "");
                    edtxtState.setText(jsonObject.has("State") ? (jsonObject.isNull("State") ? "" : jsonObject.getString("State")) : "");
                    edtxtCountry.setText(jsonObject.has("Country") ? (jsonObject.isNull("Country") ? "" : jsonObject.getString("Country")) : "");
                    edtxtPinCode.setText(jsonObject.has("pincode") ? (jsonObject.isNull("pincode") ? "" : jsonObject.getString("pincode")) : "");
                } catch (Exception e) {
                }
            }
            try {
                jsonObject = new JSONObject(UserDataPreferences.getUserShippingDetail(ShippingDetail.this));
                edtxtBillFirstName.setText(jsonObject.has("first_name") ? (jsonObject.isNull("first_name") ? "" : jsonObject.getString("first_name")) : "");
                edtxtBillLastName.setText(jsonObject.has("last_name") ? (jsonObject.isNull("last_name") ? "" : jsonObject.getString("last_name")) : "");
                edtxtBillAddress1.setText(jsonObject.has("address_1") ? (jsonObject.isNull("address_1") ? "" : jsonObject.getString("address_1")) : "");
                edtxtBillAddress2.setText(jsonObject.has("address_2") ? (jsonObject.isNull("address_2") ? "" : jsonObject.getString("address_2")) : "");
                edtxtBillCity.setText(jsonObject.has("city") ? (jsonObject.isNull("city") ? "" : jsonObject.getString("city")) : "");
                edtxtBillState.setText(jsonObject.has("State") ? (jsonObject.isNull("State") ? "" : jsonObject.getString("State")) : "");
                edtxtBillCountry.setText(jsonObject.has("Country") ? (jsonObject.isNull("Country") ? "" : jsonObject.getString("Country")) : "");
                edtxtBillPinCode.setText(jsonObject.has("pincode") ? (jsonObject.isNull("pincode") ? "" : jsonObject.getString("pincode")) : "");
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkboxSameAsAbove) {
            if (checkboxSameAsAbove.isChecked()) {

                edtxtBillFirstName.setText(edtxtFirstName.getText().toString());
                edtxtBillLastName.setText(edtxtLastName.getText().toString());
                edtxtBillAddress1.setText(edtxtAddress1.getText().toString());
                edtxtBillAddress2.setText(edtxtAddress2.getText().toString());
                edtxtBillCity.setText(edtxtCity.getText().toString());
                edtxtBillState.setText(edtxtState.getText().toString());
                edtxtBillCountry.setText(edtxtCountry.getText().toString());
                edtxtBillPinCode.setText(edtxtPinCode.getText().toString());
            } else {
                edtxtBillFirstName.setText("");
                edtxtBillLastName.setText("");
                edtxtBillAddress1.setText("");
                edtxtBillAddress2.setText("");
                edtxtBillCity.setText("");
                edtxtBillState.setText("");
                edtxtBillCountry.setText("");
                edtxtBillPinCode.setText("");
            }
        } else if (v.getId() == R.id.btnSubmit) {

            if (AppConstant.isNetworkAvailable(ShippingDetail.this)) {
                if (!edtxtFirstName.getText().toString().isEmpty()) {
                    if (!edtxtLastName.getText().toString().isEmpty()) {
                        if (!edtxtAddress1.getText().toString().isEmpty()) {
                            if (!edtxtCity.getText().toString().isEmpty()) {
                                if (!edtxtState.getText().toString().isEmpty()) {
                                    if (!edtxtCountry.getText().toString().isEmpty()) {
                                        if (!edtxtPinCode.getText().toString().isEmpty()) {
                                            try {
                                                jobjShipping.put("first_name", edtxtFirstName.getText().toString());
                                                jobjShipping.put("last_name", edtxtLastName.getText().toString());
                                                jobjShipping.put("address_1", edtxtAddress1.getText().toString());
                                                jobjShipping.put("address_2", edtxtAddress2.getText().toString());
                                                jobjShipping.put("city", edtxtCity.getText().toString());
                                                jobjShipping.put("State", edtxtState.getText().toString());
                                                jobjShipping.put("Country", edtxtCountry.getText().toString());
                                                jobjShipping.put("pincode", edtxtPinCode.getText().toString());

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (!checkboxSameAsAbove.isChecked()) {
                                                try {
                                                    jobjBilling.put("first_name", edtxtBillFirstName.getText().toString());
                                                    jobjBilling.put("last_name", edtxtBillLastName.getText().toString());
                                                    jobjBilling.put("address_1", edtxtBillAddress1.getText().toString());
                                                    jobjBilling.put("address_2", edtxtBillAddress2.getText().toString());
                                                    jobjBilling.put("city", edtxtBillCity.getText().toString());
                                                    jobjBilling.put("State", edtxtBillState.getText().toString());
                                                    jobjBilling.put("Country", edtxtBillCountry.getText().toString());
                                                    jobjBilling.put("pincode", edtxtBillPinCode.getText().toString());

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                jobjBilling = jobjShipping;
                                            }
                                            couponId = edtxtCouponId.getText().toString();
                                            officeName=edtxtStoreName.getText().toString();
                                            officeURL=edtxtStoreUrl.getText().toString();
                                            paymentMethod=1;
                                            deliveryMethod=1;
                                            UserDataPreferences.saveUserShippingDetail(ShippingDetail.this, jobjShipping.toString(), jobjBilling.toString());
                                            new ItemOrderTask(v).execute();

                                        } else {
                                            AppConstant.displayErroMessage(v, "Must enter pincode", ShippingDetail.this);
                                        }
                                    } else {
                                        AppConstant.displayErroMessage(v, "Must enter country", ShippingDetail.this);
                                    }
                                } else {
                                    AppConstant.displayErroMessage(v, "Must enter state", ShippingDetail.this);
                                }
                            } else {
                                AppConstant.displayErroMessage(v, "Must enter city", ShippingDetail.this);
                            }
                        } else {
                            AppConstant.displayErroMessage(v, "Must enter address1", ShippingDetail.this);
                        }
                    } else {
                        AppConstant.displayErroMessage(v, "Must enter last name", ShippingDetail.this);
                    }
                } else {
                    AppConstant.displayErroMessage(v, "Must enter first name", ShippingDetail.this);
                }

            } else {
                AppConstant.showNetworkError(ShippingDetail.this);
            }

        }
    }

    public class ItemOrderTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;
        JSONObject jObj;
        boolean flag = false;
        private int responseCode;
        private View view;
        private String message;

        public ItemOrderTask(View view) {
            this.view = view;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ShippingDetail.this);
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jParser = new JSONParser(ShippingDetail.this);
            try {
                JSONStringer jsonStringer;
                if (couponId.length() == 0) {
                    jsonStringer = new JSONStringer().object()
                            .key("shipping_details").value(jobjShipping)
                            .key("billing_details").value(jobjBilling)
                            .key("products").value(products)
                            .key("store_name").value(officeName)
                            .key("store_url").value(officeURL)
                            .key("delivery_method").value(deliveryMethod)
                            .key("payment_method").value(paymentMethod)

                            .endObject();
                } else {
                    jsonStringer = new JSONStringer().object()
                            .key("shipping_details").value(jobjShipping)
                            .key("billing_details").value(jobjBilling)
                            .key("products").value(products)
                            .key("coupon_code").value(couponId)
                            .key("store_name").value(officeName)
                            .key("store_url").value(officeURL)
                            .key("delivery_method").value(deliveryMethod)
                            .key("payment_method").value(paymentMethod)
                            .endObject();
                }
                String data[] = jParser.sendPostReq(Constants.api_v1 + Constants.api_order, jsonStringer.toString());
                responseCode = Integer.parseInt(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                    message = jObj.has("message") ? (jObj.isNull("message") ? "" : jObj.getString("message")) : "";

                    if (flag) {
                        JSONObject dataObj = jObj.getJSONObject("data");

                    }
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
                if (dialog != null)
                    dialog.dismiss();
                if (responseCode == 200) {
                    if (flag) {
                        AppConstant.showToastShort(ShippingDetail.this, "Order successfully placed.");
                    } else {
                        new AlertDialog.Builder(ShippingDetail.this)
                                .setTitle("Order")
                                .setMessage(message + ", Please try again.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
