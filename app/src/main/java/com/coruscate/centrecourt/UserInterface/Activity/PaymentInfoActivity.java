package com.coruscate.centrecourt.UserInterface.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Fragments.Payments.DeliveryFragment;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by User on 9/12/2015.
 */

public class PaymentInfoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "Payment";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.imgDelivery)
    ImageView imgDelivery;
    @InjectView(R.id.txtDelivery)
    TextView txtDelivery;
    @InjectView(R.id.viewBetween)
    View viewBetween;
    @InjectView(R.id.imgPayment)
    ImageView imgPayment;
    @InjectView(R.id.txtPayment)
    TextView txtPayment;
    public String product;
    public double shippingPrice = 0;
    JSONArray jsonArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);
        ButterKnife.inject(this);
        setUpToolbar();
        product = getIntent().getStringExtra("products");
        try {
            jsonArray = new JSONArray(product);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject object = new JSONObject();
        ArrayList<String> accessoriesList = UserDataPreferences.getAccessoriesItemList(this);
        if (accessoriesList != null && accessoriesList.size() > 0) {
            for (int i = 0; i < accessoriesList.size(); i++) {
                object = new JSONObject();
                try {
                    JSONObject jsonObject1 = new JSONObject(accessoriesList.get(i));
                    JSONObject products = JSONData.getJSONObject(jsonObject1, "products");
                    int qty1 = JSONData.getInt(jsonObject1, "qty");
                    object.put("id", JSONData.getString(products, "_id"));
                    object.put("qty", qty1);
                    jsonArray.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (AppConstant.isNetworkAvailable(this)) {
            new getShippingChargies(savedInstanceState).execute();
        } else {
            AppConstant.showNetworkError(this);
        }

    }

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("My Shopping", this));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Miscellaneous==>", "requestCode==>" + requestCode + "\nresultCode==>" + resultCode);
        if (requestCode == 11 && resultCode == 11) {
            Intent i1 = new Intent();
            setResult(5, i1);
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
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


    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @Override
    public void onClick(View v) {

    }

    private void closeActivity() {
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public class getShippingChargies extends AsyncTask<Void, Void, Void> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(PaymentInfoActivity.this);
        boolean flag;
        JSONObject jObj;
        private int responseCode;
        private Bundle savedInstanceState;

        public getShippingChargies(Bundle savedInstanceState) {
            this.savedInstanceState = savedInstanceState;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONParser jsonParser = new JSONParser(PaymentInfoActivity.this);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("products", jsonArray);
                String[] data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_shipping_price, jsonObject.toString());
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
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
                if (responseCode == 200 && flag) {
                    shippingPrice = JSONData.getInt(jObj, "data");
                    setFragment(savedInstanceState);

                } else {
                    closeActivity();
                    AppConstant.showToastShort(PaymentInfoActivity.this, "Something went wrong.Please try again");
                }
            } catch (Exception e) {

            }
        }

    }

    private void setFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
            DeliveryFragment deliveryFragment = new DeliveryFragment();
            fragmentManager.beginTransaction()
                    .addToBackStack(DeliveryFragment.TAG)
                    .replace(R.id.nav_contentframe, deliveryFragment).commit();
        }
    }

}
