package com.coruscate.centrecourt.UserInterface.Fragments.Payments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Activity.AddressListActivity;
import com.coruscate.centrecourt.UserInterface.Activity.PaymentInfoActivity;
import com.coruscate.centrecourt.UserInterface.Activity.WebViewActivity;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.AvenuesParams;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.ServiceUtility;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by User on 9/12/2015.
 */
public class DeliveryFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener, android.content.DialogInterface.OnClickListener {

    public static final String TAG = "DeliveryFragment";
    AlertDialog alert;
    private PaymentInfoActivity paymentInfoActivity;
    private TextView txtChangeAddress, txtName, txtContact, txtAddress, txtChangeAddress1, txtName1, txtContact1, txtAddress1;
    private LinearLayout layoutOrderItemDetails, liniarLayoutAddressDetail;
    private TextView txtQuantity, txtDeliveryCharge, txtTotalAmount, txtDeliveryDate, txtproductTotalPrice;
    private EditText etDeliveryMode, etPaymentMode, etCoupanCode;
    private String[] paymentModeList;
    private String[] deliveryModeList;
    private String[] listItems;
    private int selectedMode; //1 : delivery mode,2: payment mode
    private String orderProductDetail;
    private JSONArray jsonArrayList;
    private Long deliveryDate;
    private Button btnMakePayment, btnCheckCoupan;
    private JSONObject shipping_address, billing_address, products_order;
    private JSONArray products = new JSONArray();
    private double totalPrice;
    private TextView txtShippimngPrice;
    int disc = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        paymentInfoActivity = (PaymentInfoActivity) activity;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("==>", "requestCode==>" + requestCode + "  resultCode==>" + resultCode);
        if (resultCode == 2) {
            if (requestCode == 2) {
                String addressObject = data.getStringExtra("addressObject");
                if (addressObject != null) {
                    try {
                        shipping_address = new JSONObject(addressObject);
                        displayDeliveryAddress(shipping_address);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == 3) {
                String addressObject = data.getStringExtra("addressObject");
                if (addressObject != null) {
                    try {
                        billing_address = new JSONObject(addressObject);
                        displayBillingAddress(billing_address);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GeneralTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.delivery_fragment, container, false);
        initializeViews(view);
        try {
            orderProductDetail = paymentInfoActivity.getIntent().getStringExtra("products");
        } catch (Exception e) {

        }
        displayOrderDetail();
        JSONArray jsonArray = UserDataPreferences.getUserAddressBook(paymentInfoActivity);
        if (jsonArray != null) {
            if (jsonArray.length() > 0) {
                try {
                    shipping_address = jsonArray.getJSONObject(0);
                    displayDeliveryAddress(shipping_address);
                    billing_address = jsonArray.getJSONObject(0);
                    displayBillingAddress(billing_address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (AppConstant.isAndroid5()) {
            btnCheckCoupan.setBackground(paymentInfoActivity.getDrawable(R.drawable.ripple_accent_round_corner));
            btnMakePayment.setBackground(paymentInfoActivity.getDrawable(R.drawable.ripple_accent_round_corner));
        }

        txtChangeAddress.setOnClickListener(this);
        txtChangeAddress1.setOnClickListener(this);
        etPaymentMode.setOnClickListener(this);
        etDeliveryMode.setOnClickListener(this);
        btnCheckCoupan.setOnClickListener(this);
        btnMakePayment.setOnClickListener(this);

        return view;
    }

    private void displayOrderDetail() {
        if (orderProductDetail != null) {
            try {
                jsonArrayList = new JSONArray(orderProductDetail.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LinearLayout item, shippingRateLayout;
        TextView txtName, txtproductQty;
        totalPrice = 0.0;
        double total;
        int totalQty = 0, qty;
        JSONObject jsonObject;
        Calendar minDate = Calendar.getInstance();
        Calendar compareDate = Calendar.getInstance();
        String minDeliveryDate = "";

        for (int i = 0; i < jsonArrayList.length(); i++) {
            products_order = new JSONObject();
            item = (LinearLayout) LayoutInflater.from(paymentInfoActivity).inflate(R.layout.order_item_view, null);
            txtName = (TextView) item.findViewById(R.id.txtName);
            txtproductQty = (TextView) item.findViewById(R.id.txtproductQty);
            txtproductTotalPrice = (TextView) item.findViewById(R.id.txtproductTotalPrice);

            try {
                jsonObject = jsonArrayList.getJSONObject(i);

                if (i == 0) {
                    minDate.setTimeInMillis(Long.parseLong(JSONData.getString(jsonObject, "delivery_date")));
                    compareDate.setTimeInMillis(Long.parseLong(JSONData.getString(jsonObject, "delivery_date")));
                } else {
                    minDeliveryDate = JSONData.getString(jsonObject, "delivery_date");
                    if (minDeliveryDate.length() > 0) {
                        compareDate.setTimeInMillis(Long.parseLong(minDeliveryDate));
                        if (minDate.getTimeInMillis() > compareDate.getTimeInMillis()) {
                            minDate.setTimeInMillis(compareDate.getTimeInMillis());
                        }
                    }
                }
                total = Double.parseDouble(JSONData.getString(jsonObject, "total"));
                qty = Integer.parseInt(JSONData.getString(jsonObject, "qty"));
                txtName.setText(JSONData.getString(jsonObject, "name"));
                txtproductQty.setText(qty + "");
                txtproductTotalPrice.setText(total + "");
                totalPrice += total;
                totalQty += qty;
                JSONObject item_details = JSONData.getJSONObject(jsonObject, "item_details");
                products_order.put("id", JSONData.getString(item_details, "_id"));
                products_order.put("qty", qty);
                products_order.put("flavour_id", JSONData.getString(jsonObject, "flavour_id"));
                products_order.put("weight", JSONData.getString(jsonObject, "weight"));
                products_order.put("delivery_time", JSONData.getLong(jsonObject, "delivery_time"));
                products_order.put("delivery_date", JSONData.getLong(jsonObject, "delivery_date"));
                products_order.put("eggwith", JSONData.getBoolean(jsonObject, "eggwith"));
                products.put(products_order);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            layoutOrderItemDetails.addView(item);

        }
        deliveryDate = minDate.getTimeInMillis();
        ArrayList<String> accessoriesList = UserDataPreferences.getAccessoriesItemList(paymentInfoActivity);
        if (accessoriesList != null && accessoriesList.size() > 0) {
            Log.d(TAG, accessoriesList.size() + "");
            for (int i = 0; i < accessoriesList.size(); i++) {
                products_order = new JSONObject();
                item = (LinearLayout) LayoutInflater.from(paymentInfoActivity).inflate(R.layout.order_item_view, null);
                txtName = (TextView) item.findViewById(R.id.txtName);
                txtproductQty = (TextView) item.findViewById(R.id.txtproductQty);
                txtproductTotalPrice = (TextView) item.findViewById(R.id.txtproductTotalPrice);
                try {
                    JSONObject jsonObject1 = new JSONObject(accessoriesList.get(i));
                    JSONObject products = JSONData.getJSONObject(jsonObject1, "products");
                    int qty1 = JSONData.getInt(jsonObject1, "qty");
                    JSONObject price_per_piece = JSONData.getJSONObject(products, "price_per_piece");
                    int price = JSONData.getInt(price_per_piece, "price");
                    totalQty += qty1;
                    totalPrice += price * qty1;
                    txtName.setText(JSONData.getString(products, "name"));
                    txtproductQty.setText(qty1 + "");
                    txtproductTotalPrice.setText(Double.parseDouble((qty1 * price) + "") + "");
                    products_order.put("id", JSONData.getString(products, "_id"));
                    products_order.put("qty", qty1);
                    this.products.put(products_order);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                layoutOrderItemDetails.addView(item);
            }
        }


        shippingRateLayout = (LinearLayout) LayoutInflater.from(paymentInfoActivity).inflate(R.layout.order_item_view, null);
        TextView txtName1 = (TextView) shippingRateLayout.findViewById(R.id.txtName);
        txtName1.setText("Shipping Charge");
        TextView txtproductQty1 = (TextView) shippingRateLayout.findViewById(R.id.txtproductQty);
        txtproductQty1.setText("");
        txtShippimngPrice = (TextView) shippingRateLayout.findViewById(R.id.txtproductTotalPrice);
        setShippingRate();
        layoutOrderItemDetails.addView(shippingRateLayout);


        item = (LinearLayout) LayoutInflater.from(paymentInfoActivity).inflate(R.layout.order_item_view, null);
        txtName = (TextView) item.findViewById(R.id.txtName);
        txtproductQty = (TextView) item.findViewById(R.id.txtproductQty);
        txtproductTotalPrice = (TextView) item.findViewById(R.id.txtproductTotalPrice);
        txtName.setText("Total");
        txtproductQty.setText(totalQty + "");
        if (totalPrice > 1000) {
            paymentModeList = new String[1];
            paymentModeList[0] = "Online";
            etPaymentMode.setText(paymentModeList[0]);
        }
        totalPrice = Double.parseDouble(txtShippimngPrice.getText().toString()) + totalPrice;
        txtproductTotalPrice.setText(totalPrice + "");
        layoutOrderItemDetails.addView(item);
        txtDeliveryDate.setText(AppConstant.getDate(deliveryDate) + "");


    }

    private void initializeViews(View view) {

        txtChangeAddress = (TextView) view.findViewById(R.id.txtChangeAddress);
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtContact = (TextView) view.findViewById(R.id.txtContact);
        txtAddress = (TextView) view.findViewById(R.id.txtAddress);

        txtChangeAddress1 = (TextView) view.findViewById(R.id.txtChangeAddress1);
        txtName1 = (TextView) view.findViewById(R.id.txtName1);
        txtContact1 = (TextView) view.findViewById(R.id.txtContact1);
        txtAddress1 = (TextView) view.findViewById(R.id.txtAddress1);
        txtDeliveryDate = (TextView) view.findViewById(R.id.txtDeliveryDate);

        layoutOrderItemDetails = (LinearLayout) view.findViewById(R.id.layoutOrderItemDetails);
        liniarLayoutAddressDetail = (LinearLayout) view.findViewById(R.id.liniarLayoutAddressDetail);

        txtQuantity = (TextView) view.findViewById(R.id.txtQuantity);
        txtDeliveryCharge = (TextView) view.findViewById(R.id.txtDeliveryCharge);
        txtTotalAmount = (TextView) view.findViewById(R.id.txtTotalAmount);
        etDeliveryMode = (EditText) view.findViewById(R.id.etDeliveryMode);
        etPaymentMode = (EditText) view.findViewById(R.id.etPaymentMode);
        etCoupanCode = (EditText) view.findViewById(R.id.etCoupanCode);
        btnCheckCoupan = (Button) view.findViewById(R.id.btnCheckCoupan);
        btnMakePayment = (Button) view.findViewById(R.id.btnMakePayment);

        paymentModeList = new String[2];
        paymentModeList[0] = "Online";
        paymentModeList[1] = "COD";
        deliveryModeList = new String[2];
        deliveryModeList[0] = "Flat Shipping";
        deliveryModeList[1] = "Pickup from store";
        etDeliveryMode.setText(deliveryModeList[0]);
        etPaymentMode.setText(paymentModeList[1]);
    }

    private void displayDeliveryAddress(JSONObject address) {
        if (address != null) {
            txtName.setText(JSONData.getString(address, "first_name") + " " + JSONData.getString(address, "last_name"));
            txtContact.setText(JSONData.getString(address, "mobile"));
            String line2 = JSONData.getString(address, "line2");
            if (line2.length() > 0) {
                line2 += "\n";
            }
            txtAddress.setText(JSONData.getString(address, "line1") + "\n" + line2 +
                    JSONData.getString(address, "city") + "," + JSONData.getString(address, "state") + "-" +
                    JSONData.getString(address, "pincode") + "\n" +
                    JSONData.getString(address, "country"));
        }
    }

    private void displayBillingAddress(JSONObject address) {
        if (address != null) {
            txtName1.setText(JSONData.getString(address, "first_name") + " " + JSONData.getString(address, "last_name"));
            txtContact1.setText(JSONData.getString(address, "mobile"));
            String line2 = JSONData.getString(address, "line2");
            if (line2.length() > 0) {
                line2 += "\n";
            }
            txtAddress1.setText(JSONData.getString(address, "line1") + "\n" + line2 +
                    JSONData.getString(address, "city") + "," + JSONData.getString(address, "state") + "-" +
                    JSONData.getString(address, "pincode") + "\n" +
                    JSONData.getString(address, "country"));
        }
    }

    public void onSelectedMode(String[] listItem, String title, EditText editText) {
        if (editText.getId() == etDeliveryMode.getId()) {
            selectedMode = 1;
        } else if (editText.getId() == etPaymentMode.getId()) {
            selectedMode = 2;
        }
        listItems = listItem;
        AlertDialog.Builder builder = new AlertDialog.Builder(paymentInfoActivity);
        builder.setTitle(title);
        if (selectedMode == 1) {
            if (etDeliveryMode.getText().toString().equals(deliveryModeList[0])) {
                builder.setSingleChoiceItems(listItems, 0, this);
            } else if (etDeliveryMode.getText().toString().equals(deliveryModeList[1])) {
                builder.setSingleChoiceItems(listItems, 1, this);
            } else {
                builder.setSingleChoiceItems(listItems, -1, this);
            }
        } else {
            if (etPaymentMode.getText().toString().equals(paymentModeList[0])) {
                builder.setSingleChoiceItems(listItems, 0, this);
            } else if (etPaymentMode.getText().toString().equals(paymentModeList[1])) {
                builder.setSingleChoiceItems(listItems, 1, this);
            } else {
                builder.setSingleChoiceItems(listItems, -1, this);
            }
        }
        alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtChangeAddress:
                Intent intent = new Intent(paymentInfoActivity, AddressListActivity.class);
                intent.putExtra("isChangeAddress", true);
                startActivityForResult(intent, 2);
                paymentInfoActivity.overridePendingTransition(R.anim.animation, R.anim.animation2);
                break;
            case R.id.txtChangeAddress1:
                Intent i = new Intent(paymentInfoActivity, AddressListActivity.class);
                i.putExtra("isChangeAddress", true);
                startActivityForResult(i, 3);
                paymentInfoActivity.overridePendingTransition(R.anim.animation, R.anim.animation2);
                break;
            case R.id.etDeliveryMode:
                onSelectedMode(deliveryModeList, "Delivery Type", etDeliveryMode);
                break;
            case R.id.etPaymentMode:
                onSelectedMode(paymentModeList, "Payment Type", etPaymentMode);
                break;
            case R.id.btnMakePayment:
                if (AppConstant.isNetworkAvailable(paymentInfoActivity)) {
                    if (JSONData.getString(shipping_address, "city").toLowerCase().equals("surat")) {
                        if (JSONData.getString(shipping_address, "state").toLowerCase().equals("gujarat")) {
                            if (JSONData.getString(shipping_address, "country").toLowerCase().equals("india")) {

//                    if (etPaymentMode.getText().toString().equals(paymentModeList[0])) {
////                        AppConstant.showSingleButtonAlertDialog(paymentInfoActivity, "Message", "Currently online payment is under Technical Maintenance.So please use COD.");
//
//                        try {
//                            callPaymentGateway();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            AppConstant.showSingleButtonAlertDialog(getActivity(), "Error", "Please try after some time");
//                        }
//
//                    } else {
//                        if (etDeliveryMode.getText().toString().trim().equals(deliveryModeList[1])) {

//                    try {
//                        callPaymentGateway("234324");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                        } else {
//                            AppConstant.showSingleButtonAlertDialog(paymentInfoActivity, "Message", "Flat shipping is not available for COD.So please select Delivery type Pickup from store.");
//                        }

//                    }
                                try {
                                    new OrderTask().execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                AppConstant.displayErroMessage(v, "Shipping address state must be India", paymentInfoActivity);
                            }
                        } else {
                            AppConstant.displayErroMessage(v, "Shipping address state must be Gujarat", paymentInfoActivity);
                        }
                    } else {
                        AppConstant.displayErroMessage(v, "Shipping address city must be Surat", paymentInfoActivity);
                    }
                } else {
                    AppConstant.showNetworkError(paymentInfoActivity);
                }
                break;
            case R.id.btnCheckCoupan:
                if (btnCheckCoupan.getText().toString().equals("Remove")) {
                    btnCheckCoupan.setText("Check");
                    etCoupanCode.setEnabled(true);
                    etCoupanCode.setText("");
                    disc = 0;
//                    layoutOrderItemDetails.removeViewAt(layoutOrderItemDetails.getChildCount() - 1);
                    AppConstant.collapse((LinearLayout) layoutOrderItemDetails.getChildAt(layoutOrderItemDetails.getChildCount() - 1));
                } else {
                    if (etCoupanCode.getText().toString().trim().length() > 0) {
                        if (AppConstant.isNetworkAvailable(paymentInfoActivity)) {
                            new CheckCoupanCodeTask().execute();
                        } else {
                            AppConstant.showNetworkError(paymentInfoActivity);
                        }
                    } else {
                        AppConstant.displayErroMessage(v, "Please insert Coupon Code", paymentInfoActivity);
                    }
                }
                break;
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.etDeliveryMode) {
            if (hasFocus) {
                onSelectedMode(deliveryModeList, "Delivery Type", etDeliveryMode);
            }
        } else if (v.getId() == R.id.etPaymentMode) {
            if (hasFocus) {
                onSelectedMode(paymentModeList, "Payment Type", etPaymentMode);
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int position) {
        if (selectedMode == 1) {
            etDeliveryMode.setText(listItems[position]);
            alert.dismiss();
            if (position == 0) {

                if (liniarLayoutAddressDetail.getVisibility() == View.GONE) {
                    setShippingRate();
                    totalPrice = totalPrice + Double.parseDouble(txtShippimngPrice.getText().toString());
                    txtproductTotalPrice.setText(totalPrice + "");
                    AppConstant.expand(liniarLayoutAddressDetail);
                }
            } else {
                if (liniarLayoutAddressDetail.getVisibility() == View.VISIBLE) {
                    totalPrice = totalPrice - Double.parseDouble(txtShippimngPrice.getText().toString());
                    txtproductTotalPrice.setText(totalPrice + "");
                    txtShippimngPrice.setText("0");
                    AppConstant.collapse(liniarLayoutAddressDetail);
                }
            }
        } else if (selectedMode == 2) {
            etPaymentMode.setText(listItems[position]);
            if (position == 0) {
                btnMakePayment.setText("Make Payment");
            } else if (position == 1) {
                btnMakePayment.setText("Place Order");
            }
            alert.dismiss();
        }
    }


    private void setShippingRate() {
        txtShippimngPrice.setText(paymentInfoActivity.shippingPrice + "");
    }

    private class OrderTask extends AsyncTask<Void, Void, Void> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(paymentInfoActivity);
        private boolean flag;
        private String message, orderId;
        private int responseCode;
        private JSONObject data1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jParser = new JSONParser(paymentInfoActivity);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("shipping_address", shipping_address);
                jsonObject.put("billing_address", billing_address);
                if (btnCheckCoupan.getText().equals("Remove")) {
                    jsonObject.put("coupon_code", etCoupanCode.getText().toString().trim());
                }
                if (etDeliveryMode.getText().toString().equals(deliveryModeList[0])) {
                    jsonObject.put("delivery_method", 1);
                } else {
                    jsonObject.put("delivery_method", 2);
                }

                if (etPaymentMode.getText().toString().equals(paymentModeList[0])) {
                    jsonObject.put("payment_method", 1);
                } else {
                    jsonObject.put("payment_method", 2);
                }
                jsonObject.put("products", products);

                String data[] = jParser.sendPostReq(Constants.api_v1 + Constants.api_order, jsonObject.toString());
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    JSONObject jsonObject1 = new JSONObject(data[1]);
                    flag = JSONData.getBoolean(jsonObject1, "flag");
                    JSONArray messageArray = JSONData.getJSONArray(jsonObject1, "message");
                    if (messageArray.length() > 0) {
                        message = messageArray.getString(0);
                    }
                    if (flag) {
                        data1 = JSONData.getJSONObject(jsonObject1, "data");
                        orderId = JSONData.getString(data1, "order_id");

//                        if (message == null) {
                        message = "Order successfully placed.";
//                        }
                        message += "\nYour Order Id is " + JSONData.getString(data1, "order_id") + ".";
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
                if (dialog.isShowing())
                    dialog.dismiss();
                if (responseCode == 200) {
                    if (flag) {

                        if (etPaymentMode.getText().toString().equals(paymentModeList[0])) {
                            callPaymentGateway(orderId);
                        } else {
                            new AlertDialog.Builder(paymentInfoActivity)
                                    .setTitle("Order Detail")
                                    .setMessage(message)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            UserDataPreferences.saveCartCount(paymentInfoActivity, 0);
                                            Intent i1 = new Intent();
                                            paymentInfoActivity.setResult(5, i1);
                                            paymentInfoActivity.finish();
                                            paymentInfoActivity.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                                        }
                                    }).setCancelable(false).show();
                        }
                    } else {
                        AppConstant.showSingleButtonAlertDialog(paymentInfoActivity, "Message", message);
                    }
                } else {
                    AppConstant.unableConnectServer(paymentInfoActivity);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CheckCoupanCodeTask extends AsyncTask<Void, Void, Void> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(paymentInfoActivity);
        private boolean flag;
        private String message;
        int discount;
        private int responseCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jParser = new JSONParser(paymentInfoActivity);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("coupon_code", etCoupanCode.getText().toString().trim());
                jsonObject.put("products", products);
//                jsonStringer = new JSONStringer().object().key("coupon_code").value(etCoupanCode.getText().toString()).endObject();
                String data[] = jParser.sendPostReq(Constants.api_v1 + Constants.api_apply_coupan, jsonObject.toString());
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    JSONObject jsonObject1 = new JSONObject(data[1]);
                    flag = JSONData.getBoolean(jsonObject1, "flag");
                    message = JSONData.getString(jsonObject1, "message");
                    if (flag) {
                        discount = JSONData.getInt(JSONData.getJSONObject(jsonObject1, "data"), "discount");
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
                if (dialog.isShowing())
                    dialog.dismiss();
                if (responseCode == 200) {
                    if (flag) {
                        if (message.trim().length() > 0) {
                            AppConstant.showSingleButtonAlertDialog(paymentInfoActivity, "Message", message);
                        }
                        btnCheckCoupan.setText("Remove");
                        etCoupanCode.setEnabled(false);

                        LinearLayout item = (LinearLayout) LayoutInflater.from(paymentInfoActivity).inflate(R.layout.order_item_view, null);
                        txtName = (TextView) item.findViewById(R.id.txtName);
                        txtName.setText("Discount\nTotal Amount");
                        disc = discount;
                        Log.d(TAG, "disc:" + disc);
                        txtproductTotalPrice = (TextView) item.findViewById(R.id.txtproductTotalPrice);
                        txtproductTotalPrice.setText(-discount + "\n" + (totalPrice - discount));
                        layoutOrderItemDetails.addView(item);
                        AppConstant.expand(item);
                    } else {
                        if (message.trim().length() > 0) {
                            AppConstant.showSingleButtonAlertDialog(paymentInfoActivity, "Message", message);
                        }
                    }
                } else {
                    AppConstant.unableConnectServer(paymentInfoActivity);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private void callPaymentGateway(String orderId) throws JSONException {

        String bill_name = JSONData.getString(billing_address, "first_name") + " " + JSONData.getString(billing_address, "last_name");
        String bill_address = JSONData.getString(billing_address, "line1") + " " + JSONData.getString(billing_address, "line2");
        String bill_country = JSONData.getString(billing_address, "country");
        String bill_city = JSONData.getString(billing_address, "city");
        String bill_state = JSONData.getString(billing_address, "state");
        String bill_pincode = JSONData.getString(billing_address, "pincode");
        String bill_telephone = JSONData.getString(billing_address, "mobile");


        String deli_name = JSONData.getString(shipping_address, "first_name") + " " + JSONData.getString(shipping_address, "last_name");
        String deli_address = JSONData.getString(shipping_address, "line1") + " " + JSONData.getString(shipping_address, "line2");
        String deli_country = JSONData.getString(shipping_address, "country");
        String deli_city = JSONData.getString(shipping_address, "city");
        String deli_state = JSONData.getString(shipping_address, "state");
        String deli_pincode = JSONData.getString(shipping_address, "pincode");
        String deli_telephone = JSONData.getString(shipping_address, "mobile");

        Log.d(TAG, "--->" + deli_telephone);
        Log.d(TAG, "--->" + bill_telephone);


        JSONObject userObj = new JSONObject(UserDataPreferences.getUserInfo(getActivity()));

        String strEmail = userObj.getString("email");
        Log.d(TAG, txtproductTotalPrice.getText().toString());
        Intent intent = new Intent(paymentInfoActivity, WebViewActivity.class);
        intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(getString(R.string.access)).toString().trim());
        intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderId).toString().trim());
        intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(getString(R.string.merchant)).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_NAME, ServiceUtility.chkNull(bill_name).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_ADDRESS, ServiceUtility.chkNull(bill_address).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_COUNTRY, ServiceUtility.chkNull(bill_country).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_STATE, ServiceUtility.chkNull(bill_state).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_CITY, ServiceUtility.chkNull(bill_city).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_ZIP, ServiceUtility.chkNull(bill_pincode).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_TEL, ServiceUtility.chkNull(bill_telephone).toString().trim());
        intent.putExtra(AvenuesParams.BILLING_EMAIL, ServiceUtility.chkNull(strEmail).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_NAME, ServiceUtility.chkNull(deli_name).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_ADDRESS, ServiceUtility.chkNull(deli_address).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_COUNTRY, ServiceUtility.chkNull(deli_country).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_STATE, ServiceUtility.chkNull(deli_state).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_CITY, ServiceUtility.chkNull(deli_city).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_ZIP, ServiceUtility.chkNull(deli_pincode).toString().trim());
        intent.putExtra(AvenuesParams.DELIVERY_TEL, ServiceUtility.chkNull(deli_telephone).toString().trim());
        intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull("INR").toString().trim());
        String amt = String.valueOf(totalPrice - disc);

        intent.putExtra(AvenuesParams.AMOUNT, "1");
        Log.d("deli", "--->" + deli_telephone);
        Log.d("bill", "--->" + bill_telephone);
//        intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull("http://122.182.6.216/merchant/ccavResponseHandler.jsp").toString().trim());
//        intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull("http://122.182.6.216/merchant/ccavResponseHandler.jsp").toString().trim());
        intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(Constants.api_v1 + Constants.api_get_rsa).toString().trim());
        intent.putExtra(AvenuesParams.IS_GIFT_VOUCHER, false);
        paymentInfoActivity.startActivityForResult(intent, 11);

    }

}