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

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.etFirstName)
    TypefacedEditText etFirstName;
    @InjectView(R.id.etLastName)
    TypefacedEditText etLastName;
    @InjectView(R.id.etMobileNo)
    TypefacedEditText etMobileNo;
    @InjectView(R.id.etAddress)
    TypefacedEditText etAddress;
    @InjectView(R.id.etAddress2)
    TypefacedEditText etAddress2;
    @InjectView(R.id.etCity)
    TypefacedEditText etCity;
    @InjectView(R.id.etState)
    TypefacedEditText etState;
    @InjectView(R.id.etCountry)
    TypefacedEditText etCountry;
    @InjectView(R.id.etPinCode)
    TypefacedEditText etPinCode;
    @InjectView(R.id.btnSubmit)
    TypedfacedButton btnSubmit;

    private Intent i;
    private String title;
    private int position = -1, id;
    private JSONObject jsonObject = new JSONObject();

    private String first_name, last_name, mobile, phone, line1, line2, city, state, country, pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ButterKnife.inject(this);
       /* try {
            AppConstant.setToolBarColor(AddAddressActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        i = getIntent();
        title = i.getStringExtra("title");
        position = i.getIntExtra("position", -1);

        if (title != null) {
            setUpToolbar(title);
        } else {
            setUpToolbar("Add Address");
        }

        btnSubmit.setOnClickListener(this);

        etState.setText("Gujarat");
        etCountry.setText("India");
        etCity.setText("Surat");
        if (position != -1) {
            setDetail(position);
        }
        etPinCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        try {
                            addAddress(v);
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

    private void setUpToolbar(String title) {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont(title, this));
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

    private void getDataFromUI() {
        first_name = etFirstName.getText().toString().trim();
        last_name = etLastName.getText().toString().trim();
        mobile = etMobileNo.getText().toString().trim();
        line1 = etAddress.getText().toString().trim();
        line2 = etAddress2.getText().toString().trim();
        city = etCity.getText().toString().trim();
        state = etState.getText().toString().trim();
        country = etCountry.getText().toString().trim();
        pincode = etPinCode.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmit) {
            addAddress(v);
        }
    }

    private void addAddress(View v) {
        if (AppConstant.isNetworkAvailable(AddAddressActivity.this)) {
            getDataFromUI();
            if (first_name.length() > 0) {
                if (mobile.length() > 0) {
                    if (mobile.length() == 10) {
                        if (line1.length() > 0) {
                            if (city.length() > 0) {
                                if (state.length() > 0) {
                                    if (country.length() > 0) {
                                        if (pincode.length() > 0) {
                                            if (pincode.length() == 6) {
                                                try {
                                                    jsonObject.put("first_name", first_name);
                                                    if (last_name.length() > 0) {
                                                        jsonObject.put("last_name", last_name);
                                                    }
                                                    jsonObject.put("mobile", mobile);
                                                    if (phone.length() > 0) {
                                                        jsonObject.put("phone", phone);
                                                    }
                                                    jsonObject.put("line1", line1);
                                                    if (line2.length() > 0) {
                                                        jsonObject.put("line2", line2);
                                                    }
                                                    jsonObject.put("city", city);
                                                    jsonObject.put("state", state);
                                                    jsonObject.put("country", country);
                                                    jsonObject.put("pincode", pincode);
                                                    if (id != 0) {
                                                        jsonObject.put("id", id);
                                                    }
                                                    new AddAddressTask().execute();


                                                } catch (Exception e) {
                                                    AppConstant.displayErroMessage(v, "somthing went wrong, please try again", this);
                                                }
                                            } else {
                                                AppConstant.displayErroMessage(v, "Please enter 6 Digit Pincode", this);
                                            }
                                        } else {
                                            AppConstant.displayErroMessage(v, "Please enter Pincode", this);
                                        }
                                    } else {
                                        AppConstant.displayErroMessage(v, "Please enter Country", this);
                                    }
                                } else {
                                    AppConstant.displayErroMessage(v, "Please enter State", this);
                                }
                            } else {
                                AppConstant.displayErroMessage(v, "Please enter City", this);
                            }
                        } else {
                            AppConstant.displayErroMessage(v, "Please enter Address", this);
                        }
                    } else {
                        AppConstant.displayErroMessage(v, "Please enter 10 Digit Mobile No", this);
                    }
                } else {
                    AppConstant.displayErroMessage(v, "Please enter Mobile No", this);
                }
            } else {
                AppConstant.displayErroMessage(v, "Please enter First Name", this);
            }

        } else {
            AppConstant.showNetworkError(this);
        }
    }

    private void setDetail(int position) {
        try {
            JSONArray jsonArray = UserDataPreferences.getUserAddressBook(this);
            if (jsonArray != null) {
                JSONObject object = jsonArray.getJSONObject(position);
                etFirstName.setText(JSONData.getString(object, "first_name"));
                etLastName.setText(JSONData.getString(object, "last_name"));
                etMobileNo.setText(JSONData.getString(object, "mobile"));
                etAddress.setText(JSONData.getString(object, "line1"));
                etAddress2.setText(JSONData.getString(object, "line2"));
                etCity.setText(JSONData.getString(object, "city"));
                etState.setText(JSONData.getString(object, "state"));
                etCountry.setText(JSONData.getString(object, "country"));
                etPinCode.setText(JSONData.getString(object, "pincode"));
                id = JSONData.getInt(object, "id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class AddAddressTask extends AsyncTask<Void, Void, Void> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(AddAddressActivity.this);
        boolean flag;
        JSONObject jObj;
        private int responseCode;
        private String message;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONParser jsonParser = new JSONParser(AddAddressActivity.this);
                JSONObject jsonData = new JSONObject();
                jsonData.put("address_book", jsonObject);
                String[] data;
                if (id == 0) {
                    data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_add_address, jsonData.toString());
                } else {
                    data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_update_address, jsonData.toString());
                }
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = JSONData.getBoolean(jObj, "flag");
                    message = JSONData.getString(jObj, "message");
                    if (flag) {
                        JSONObject jsonObject = JSONData.getJSONObjectDefNull(jObj, "data");
                        if (jsonObject != null) {
                            UserDataPreferences.saveUserInfo(AddAddressActivity.this, jsonObject.toString());
                        }
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
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (responseCode == 200) {
                    if (flag) {
                        Intent i = new Intent();
                        if (id == 0) {
                            AddAddressActivity.this.setResult(1, i);
                        } else {
                            AddAddressActivity.this.setResult(2, i);
                        }
                        AddAddressActivity.this.finish();
                        AddAddressActivity.this.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    }
                    AppConstant.showToastShort(AddAddressActivity.this, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
