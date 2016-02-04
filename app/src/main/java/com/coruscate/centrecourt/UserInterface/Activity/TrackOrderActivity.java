package com.coruscate.centrecourt.UserInterface.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.coruscate.centrecourt.CustomControls.RangeSlider.RangeSliderView;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.JSONData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TrackOrderActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.rsv_custom)
    RangeSliderView rsvCustom;
    @InjectView(R.id.txtProccessing)
    TypefacedTextView txtProccessing;
    @InjectView(R.id.txtShipped)
    TypefacedTextView txtShipped;
    @InjectView(R.id.txtDelivered)
    TypefacedTextView txtDelivered;
    @InjectView(R.id.txtOrderId)
    TypefacedTextView txtOrderId;
    @InjectView(R.id.txtTotalQuantity)
    TypefacedTextView txtTotalQuantity;
    @InjectView(R.id.txtPaymentType)
    TypefacedTextView txtPaymentType;
    @InjectView(R.id.txtDeliveryType)
    TypefacedTextView txtDeliveryType;
    @InjectView(R.id.txtTotalPaidAmount)
    TypefacedTextView txtTotalPaidAmount;
    @InjectView(R.id.cardView)
    CardView cardView;
    @InjectView(R.id.txtShippingDetail)
    TypefacedTextView txtShippingDetail;
    @InjectView(R.id.txtBillingDetail)
    TypefacedTextView txtBillingDetail;
    @InjectView(R.id.txtUserInfo)
    TypefacedTextView txtUserInfo;
    @InjectView(R.id.linearLayoutShippingDetail)
    LinearLayout linearLayoutShippingDetail;
    private String detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);
        ButterKnife.inject(this);
        detail = getIntent().getStringExtra("detail");
        /*try {
            AppConstant.setToolBarColor(this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        setUpToolbar();
        setInformation();
        rsvCustom.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                AppConstant.showToastShort(TrackOrderActivity.this, "Hi index: " + index);
            }
        });
    }

    @Override
    public void onBackPressed() {
        closeActivity();
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
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("Order Summery", this));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void setInformation() {
        try {
            JSONObject item = new JSONObject(detail);
            txtOrderId.setText(JSONData.getString(item, "order_id"));
            int paymentMethod = JSONData.getInt(item, "payment_method");
            int delivery_method = JSONData.getInt(item, "delivery_method");

            if (paymentMethod == 1) {
                txtPaymentType.setText("Online");
            } else if (paymentMethod == 2) {
                txtPaymentType.setText("COD");
            }
            int status = JSONData.getInt(item, "status");
            if(delivery_method==1) {
                if (status == 2) {
                    rsvCustom.setRangeCount(2);
                    rsvCustom.setInitialIndex(1);
                    txtDelivered.setVisibility(View.GONE);
                    txtShipped.setText("cancelled");
                    rsvCustom.setFilledColor(getResources().getColor(R.color.labelRed));
                } else if (status == 4) {
                    rsvCustom.setInitialIndex(1);
                    txtDelivered.setVisibility(View.VISIBLE);
                } else if (status == 1) {
                    rsvCustom.setInitialIndex(2);
                    txtDelivered.setVisibility(View.VISIBLE);
                }
            }else {
                if (status == 2) {
                    rsvCustom.setRangeCount(2);
                    rsvCustom.setInitialIndex(1);
                    txtDelivered.setVisibility(View.GONE);
                    txtShipped.setText("cancelled");
                    rsvCustom.setFilledColor(getResources().getColor(R.color.labelRed));
                }else if (status == 1) {
                    rsvCustom.setRangeCount(2);
                    rsvCustom.setInitialIndex(1);
                    txtDelivered.setVisibility(View.GONE);
                    txtShipped.setText("delivered");
                    rsvCustom.setFilledColor(getResources().getColor(R.color.labelGreen));
                }else {
                    rsvCustom.setRangeCount(2);
                    rsvCustom.setInitialIndex(0);
                    txtDelivered.setVisibility(View.GONE);
                    txtShipped.setText("delivered");
                    rsvCustom.setFilledColor(getResources().getColor(R.color.labelGreen));
                }
            }
            JSONArray order_items = JSONData.getJSONArray(item, "order_items");
            int totalItem = JSONData.getInt(item, "total_qty");
            txtTotalQuantity.setText(totalItem + "");
            double totalPrice = JSONData.getDouble(item, "total_price");
            txtTotalPaidAmount.setText(AppConstant.rupee_symbol+totalPrice + "");
            try {
                JSONObject object = item.getJSONObject("shipping_address");
                txtUserInfo.setText(JSONData.getString(object, "first_name") + " " + JSONData.getString(object, "last_name") +
                        "\n" + JSONData.getString(object, "mobile"));

                txtShippingDetail.setText(JSONData.getString(object, "line1") + "\n" + JSONData.getString(object, "line2") +
                        JSONData.getString(object, "city") + "," + JSONData.getString(object, "state") + "-" +
                        JSONData.getString(object, "pincode") + "\n" +
                        JSONData.getString(object, "country"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                JSONObject object = item.getJSONObject("billing_address");
                txtBillingDetail.setText(JSONData.getString(object, "line1") + "\n" + JSONData.getString(object, "line2") +
                        JSONData.getString(object, "city") + "," + JSONData.getString(object, "state") + "-" +
                        JSONData.getString(object, "pincode") + "\n" +
                        JSONData.getString(object, "country"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (delivery_method == 1) {
                txtDeliveryType.setText("Flat shipping");
                linearLayoutShippingDetail.setVisibility(View.VISIBLE);
            } else {
                txtDeliveryType.setText("Pickup from store");
                linearLayoutShippingDetail.setVisibility(View.GONE);
            }
        } catch (JSONException e) {

        }
    }

}
