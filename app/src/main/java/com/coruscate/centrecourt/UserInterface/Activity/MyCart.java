package com.coruscate.centrecourt.UserInterface.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.coruscate.centrecourt.Adapter.MyCartDetailAdapter;
import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.CustomeDateAndTimePicker;
import com.coruscate.centrecourt.CustomControls.TypedfacedButton;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.animators.ScaleInAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import recycler.MaterialShowcaseView;
import recycler.Util;

/**
 * Created by cis on 7/28/2015.
 */
public class MyCart extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MyCart";
    public TextView txtSubTotal;
    public TextView txtTotalPrice;
    private TextView txtNoResult;
    private UltimateRecyclerView myCartRecycler;
    public SearchView searchView;
    private boolean isProceedToCheckOut = true;

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.linearLayoutMain)
    LinearLayout linearLayoutMain;
    @InjectView(R.id.relativeLayoutTotal)
    RelativeLayout relativeLayoutTotal;
    @InjectView(R.id.btnSubmit)
    TypedfacedButton btnSubmit;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.btnContiueShopping)
    TypedfacedButton btnContiueShopping;
    @InjectView(R.id.txtDeliveryDate)
    TypefacedTextView txtDeliveryDate;
    public CardView cardViewChangeDelivery;

    private RecyclerView.LayoutManager myLayoutManager;
    private static MyCartDetailAdapter adapter;
    private static ArrayList<String> myCartList;
    private ArrayList<String> myCartMainList;
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM,yyyy hh:mm a");
    private CustomeDateAndTimePicker customeDateAndTimePicker;
    private ArrayList<String> notSetisfyDelivery;
    private Calendar updateTimeCalander;
    private static Context context;

    public void makeRecyclerGone(String strNoResult) {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText(strNoResult);
        myCartRecycler.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6 && resultCode == 5) {
            Intent i1 = new Intent();
            setResult(8, i1);
            finish();
            Intent intent = new Intent(MyCart.this, OrderListActivity.class);
            intent.putExtra("isCart", true);
            startActivity(intent);
            overridePendingTransition(R.anim.animation, R.anim.animation2);

        } else if (requestCode == 9 && resultCode == 1) {
            Intent intent = new Intent(MyCart.this, PaymentInfoActivity.class);
            intent.putExtra("products", setProducts().toString());
            startActivityForResult(intent, 6);
            overridePendingTransition(R.anim.animation, R.anim.animation2);
        } else if (resultCode == Constants.WEBVIEW_CODE) {
//                getActivity().finish();
            Intent i = new Intent();
            setResult(Constants.WEBVIEW_CODE, i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_cart_fragment);
        ButterKnife.inject(this);
       /* try {
            AppConstant.setToolBarColor(MyCart.this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        setUpToolbar();
        initializeViews();
        if (AppConstant.isNetworkAvailable(MyCart.this)) {
            new ViewCartTask().execute();
        } else {
            AppConstant.showSingleButtonAlertDialog(MyCart.this,
                    "Error",
                    "Unable to connect Web Services.\nPlease try again.");
        }
        btnSubmit.setOnClickListener(this);
        btnContiueShopping.setOnClickListener(this);
        cardViewChangeDelivery.setOnClickListener(this);
    }

    @SuppressLint("NewApi")
    private void initializeViews() {
        updateTimeCalander = Calendar.getInstance();
        cardViewChangeDelivery = (CardView) findViewById(R.id.cardViewChangeDelivery);
        txtSubTotal = (TextView) findViewById(R.id.txtSubTotal);
        txtTotalPrice = (TextView) findViewById(R.id.txtTotalPrice);
        txtNoResult = (TextView) findViewById(R.id.txtNoResult);
        myCartRecycler = (UltimateRecyclerView) findViewById(R.id.myCartRecycler);
        progressBar.setVisibility(View.GONE);
        txtNoResult.setVisibility(View.GONE);
        myCartRecycler.setHasFixedSize(true);
        myCartRecycler.setItemAnimator(new ScaleInAnimator());
        myCartRecycler.getItemAnimator().setRemoveDuration(500);
        myLayoutManager = new LinearLayoutManager(this);
        myCartRecycler.setLayoutManager(myLayoutManager);
        myCartList = new ArrayList<>();
        notSetisfyDelivery = new ArrayList<>();
        myCartMainList = new ArrayList<>();
        adapter = new MyCartDetailAdapter(myCartList, MyCart.this);
        myCartRecycler.setAdapter(adapter);
        if (AppConstant.isAndroid5()) {
            btnSubmit.setBackground(getDrawable(R.drawable.ripple_accent));
            btnContiueShopping.setBackground(getDrawable(R.drawable.ripple_accent));
        }
        myCartRecycler.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        myCartRecycler.setRefreshing(false);
                    }
                }, 1);
            }
        });
        context = MyCart.this;

    }

    private void setUpToolbar() {
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(AppConstant.spanFont("My Cart", this));
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
        searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                itemCart.setVisible(false);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCart.setVisible(false);
                searchView.requestFocus();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                filter(query);
                return true;
            }
        });
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

    private void makeRecyclerVisible() {
        txtNoResult.setVisibility(View.GONE);
        myCartRecycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        closeActivity();

    }

    public void afterRemoveCart(int position) {
        try {
            myCartList.remove(position);
            UserDataPreferences.saveCartCount(context, myCartList.size());
            if (myCartList.size() == 0) {
                txtSubTotal.setVisibility(View.GONE);
                txtTotalPrice.setVisibility(View.GONE);
                MainActivity.txtCart.setVisibility(View.GONE);
                makeRecyclerGone("No item in cart");
            } else {
                MainActivity.txtCart.setVisibility(View.VISIBLE);
                MainActivity.txtCart.setText(myCartList.size() + "");
                JSONObject item;
                int totalItem = 0;
                double totalAmount = 0.0;
                for (int i = 0; i < myCartList.size(); i++) {
                    try {
                        item = new JSONObject(myCartList.get(i).toString());
                        final int qty = JSONData.getInt(item, "qty");
                        final double amt = JSONData.getInt(item, "total");
                        totalItem += qty;
                        totalAmount += amt;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                txtSubTotal.setText("Total item : " + totalItem + "");
                txtTotalPrice.setText(AppConstant.rupee_symbol + totalAmount + "");
            }
            adapter.notifyItemRemoved(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmit) {
//            if (MyCartDetailAdapter.myCartList.size() > 0) {
//                JSONArray jsonArray = new JSONArray();
//                for (int i = 0; i < MyCartDetailAdapter.myCartList.size(); i++) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(MyCartDetailAdapter.myCartList.get(i));
//                        jsonArray.put(jsonObject);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                Intent intent = new Intent(MyCart.this, ShippingDetail.class);
//                intent.putExtra("products", jsonArray.toString());
//                startActivity(intent);
//                overridePendingTransition(R.anim.start_activity, R.anim.close_activity);
//            } else {
//                AppConstant.displayErroMessage(v, "Opps! Tere is no product available in cart", MyCart.this);
//            }
            if (UserDataPreferences.getUserInfo(this).equals("")) {
                Intent login = new Intent(this,
                        LoginActivity.class);
                UserDataPreferences.saveResultCode(this, 1);
                startActivityForResult(login, 1);
            } else {
                try {
                    JSONArray jsonArray = setProducts();
                    if (jsonArray != null && jsonArray.length() > 0) {
                        new checkIsProceedTocheckOut().execute();
                    } else {
                        if (myCartList.size() == 0) {
                            AppConstant.displayErroMessage(v, "You have no item in cart", MyCart.this);
                        } else {
                            AppConstant.displayErroMessage(v, "Your delivery date is lessthen current date ", MyCart.this);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (v.getId() == R.id.btnContiueShopping) {

            setResult(1, new Intent());
            finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        } else if (v.getId() == R.id.cardViewChangeDelivery) {
            customeDateAndTimePicker.showDialog();
        }

    }

    private void continueCheckOut() {
        JSONArray jsonArray = setProducts();
        if (AppConstant.isNetworkAvailable(this)) {
            new GetMiscellaneousTask(true, jsonArray).execute();
        } else {
            AppConstant.showNetworkError(this);
        }
    }


    private JSONArray setProducts() {
        JSONObject jsonObject;
        JSONArray jsonArray = new JSONArray();
        JSONObject object;
        JSONObject obj;
        JSONObject itemDetail;

        for (int i = 0; i < myCartList.size(); i++) {
            try {
                jsonObject = new JSONObject(myCartList.get(i).toString());

                object = new JSONObject();
                object.put("id", JSONData.getString(jsonObject, "id"));
                object.put("qty", JSONData.getString(jsonObject, "qty"));
                object.put("weight", JSONData.getString(jsonObject, "weight"));
                if (JSONData.getBoolean(jsonObject, "eggwith")) {
                    object.put("eggwith", true);
                } else if (JSONData.getBoolean(jsonObject, "eggless")) {
                    object.put("eggless", true);
                }
                obj = jsonObject.getJSONObject("item_details");
                object.put("item_details", obj);
                object.put("name", JSONData.getString(obj, "name"));
                object.put("Desc", JSONData.getString(obj, "desc"));
                object.put("total", JSONData.getString(jsonObject, "total"));
                object.put("flavour_id", JSONData.getString(jsonObject, "flavour_id"));
                object.put("delivery_time", JSONData.getLong(jsonObject, "delivery_time"));
                object.put("delivery_date", JSONData.getLong(jsonObject, "delivery_date"));


//                if (JSONData.getLong(jsonObject, "delivery_date") > Calendar.getInstance().getTimeInMillis()) {
                jsonArray.put(object);
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    private void closeActivity() {
        setResult(-1, new Intent());
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public void filter(String strName) {
        JSONObject jsonObject, object;
        myCartList.clear();
        if (null == strName) {
            myCartList.addAll(myCartMainList);
        } else {
            strName = strName.toLowerCase(Locale.getDefault());
            for (int i = 0; i < myCartMainList.size(); i++) {
                try {
                    jsonObject = new JSONObject(myCartMainList.get(i).toString());
                    object = jsonObject.getJSONObject("item_details");
                    String name = object.has("name") ? (object.isNull("name") ? "" : object.getString("name")) : "";
                    if (name.toLowerCase(Locale.getDefault()).contains(strName)) {
                        myCartList.add(myCartMainList.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (myCartList.size() == 0) {
            makeRecyclerGone("No item in cart");
            adapter.notifyDataSetChanged();
        } else {
            makeRecyclerVisible();
            adapter.notifyDataSetChanged();
        }
    }



    /*
        private void getDetailOfDeliveryDate() {
            JSONObject jsonObject, obj;
            int availableTime=0;
            for (int i = 0; i < myCartList.size(); i++) {
                try {
                    jsonObject = new JSONObject(myCartList.get(i).toString());
                    if (i == 0) {
                        cardViewChangeDelivery.setVisibility(View.GONE);
                        txtDeliveryDate.setText("Delivery date : " + AppConstant.getDate(JSONData.getLong(jsonObject, "delivery_date")));
                        defaultDate = JSONData.getLong(jsonObject, "delivery_date");
                    }

                    obj = jsonObject.getJSONObject("item_details");
                    availableTime = JSONData.getInt(obj, "product_available_in");
                    if (i == 0) {
                        itemAvailableTime = availableTime;
                           itemAvailableTime = availableTime;
                            myCalendar.setTimeInMillis(JSONData.getLong(jsonObject, "delivery_date"));
                        }
                        if (availableTime > 0) {
                            if (itemAvailableTime < availableTime) {
                            }
                    }
                } catch (Exception e) {

                }
            }
        }*/

    private class checkIsProceedTocheckOut extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            notSetisfyDelivery.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            isProceedToCheckOut();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String message = "";
            if (notSetisfyDelivery.size() > 0) {
                for (int i = 0; i < notSetisfyDelivery.size(); i++) {
                    message += notSetisfyDelivery.get(i);
                }
                AppConstant.showSingleButtonAlertDialog(MyCart.this, "Not satisfy delivery time ", message);
            } else {
                continueCheckOut();
            }
        }
    }

    private void isProceedToCheckOut() {
        JSONObject jsonObject, obj;
        int availableTime;
        String itemName = "";
        Calendar myCalendar = Calendar.getInstance();
        for (int i = 0; i < myCartList.size(); i++) {

            try {
                jsonObject = new JSONObject(myCartList.get(i).toString());
                obj = jsonObject.getJSONObject("item_details");
                availableTime = JSONData.getInt(obj, "product_available_in");
                itemName = JSONData.getString(obj, "name");
                myCalendar.setTimeInMillis(JSONData.getLong(jsonObject, "delivery_date"));
                checkItemAvalibility(true, availableTime, myCalendar, itemName, false, "", 0);
            } catch (Exception e) {

            }
        }
    }

    private void checkItemAvalibility(boolean addCart, int itemAvailableTime, Calendar myCalendar, String itemName, boolean changeTime, String itemId, int position) {
        try {

            String msg = "";
            boolean availibility = false, satisfyDiliveryTime = false;
            double diff_notSatisfy = 0;
            JSONObject jObj = new JSONObject(UserDataPreferences.getSettingDetail(this));
            JSONObject data = JSONData.getJSONObject(jObj, "data");
            JSONObject setting = JSONData.getJSONObject(jObj, "setting");
            JSONArray deliveryArray = JSONData.getJSONArray(jObj, "deliveries");
            Calendar today = Calendar.getInstance();
            Calendar caltime = Calendar.getInstance();
            Calendar calNextDay = Calendar.getInstance();


            JSONObject opening_hour = JSONData.getJSONObject(setting, "opening_hour");
            long openFrom = JSONData.getLong(opening_hour, "from");
            long openTo = JSONData.getLong(opening_hour, "to");
            Calendar openingHour = Calendar.getInstance(Locale.ENGLISH);
            openingHour.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openFrom));
            openingHour.set(Calendar.MINUTE, (Integer.parseInt(AppConstant.getMinutes(openFrom))));
            int diff_current_sroreOpenTime = (int) ((today.getTimeInMillis() - openingHour.getTimeInMillis()) / (60 * 1000));
            if (diff_current_sroreOpenTime < 0) {
                today.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openFrom));
                today.set(Calendar.MINUTE, (Integer.parseInt(AppConstant.getMinutes(openFrom))));
                caltime.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openFrom));
                caltime.set(Calendar.MINUTE, (Integer.parseInt(AppConstant.getMinutes(openFrom))));
                calNextDay.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openFrom));
                calNextDay.set(Calendar.MINUTE, (Integer.parseInt(AppConstant.getMinutes(openFrom))));
            }
            Calendar closingHour = Calendar.getInstance(Locale.ENGLISH);
            closingHour.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openTo));
            closingHour.set(Calendar.MINUTE, (Integer.parseInt(AppConstant.getMinutes(openTo))));
            Log.d("current", caltime + "");
            caltime.add(Calendar.HOUR_OF_DAY, itemAvailableTime);
            Log.d("current+available", caltime + "");
            int diff = (int) ((caltime.getTimeInMillis() - today.getTimeInMillis()) / (1000 * 60 * 60 * 24));
            Log.d("difference", diff + "");
            double storeOpen = Double.parseDouble(AppConstant.getHour(openFrom) + "." + AppConstant.getMinutes(openFrom));
            double storeClose = Double.parseDouble(AppConstant.getHour(openTo) + "." + AppConstant.getMinutes(openTo));
            double selectedTimeHour_minut = Double.parseDouble(AppConstant.getHour(caltime.getTimeInMillis()) + "." + AppConstant.getMinutes(caltime.getTimeInMillis()));
            int diff_shopclose_and_day_over = (24 * 60) - ((AppConstant.getHour(openTo) * 60) + (Integer.parseInt(AppConstant.getMinutes(openTo))));

            if (caltime.getTimeInMillis() < closingHour.getTimeInMillis()) {
                if (selectedTimeHour_minut >= storeOpen && selectedTimeHour_minut <= storeClose) {
                    availibility = true;
                    satisfyDiliveryTime = checkDelivery(deliveryArray, selectedTimeHour_minut, msg, caltime, satisfyDiliveryTime, addCart, myCalendar, itemName, changeTime, itemId, position);
                }
            } else {
                long totalHourDiff = (int) ((caltime.getTimeInMillis() - today.getTimeInMillis()) / (60 * 1000));
                Log.d("total hour difference", totalHourDiff + "");
                calNextDay.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(openTo));
                calNextDay.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(openTo)));
                Log.d("calNextDay=", sdf.format(calNextDay.getTime()).toString() + "");
                long completeTaskTime = (int) ((calNextDay.getTimeInMillis() - today.getTimeInMillis()) / (60 * 1000));
                Log.d("completeTaskTime=", completeTaskTime + "");
                long compleTask = completeTaskTime;

                long remainingTaskTime = totalHourDiff - completeTaskTime;
                Log.d("remainingTaskTime=", remainingTaskTime + "");
                boolean flag = false;
                long workingHr = (closingHour.getTimeInMillis() - openingHour.getTimeInMillis()) / (60 * 1000);
                calNextDay.setTimeInMillis(today.getTimeInMillis());
                calNextDay.add(Calendar.MINUTE, (Integer.parseInt(completeTaskTime + "")));
                do {
                    if (remainingTaskTime > (workingHr)) {
                        calNextDay.add(Calendar.MINUTE, ((AppConstant.getHour(openFrom) * 60) + Integer.parseInt(AppConstant.getMinutes(openFrom)) + Integer.parseInt(workingHr + "") + diff_shopclose_and_day_over));
                        remainingTaskTime -= workingHr;
                        flag = true;
                    } else {
                        flag = false;
                        calNextDay.add(Calendar.MINUTE, ((AppConstant.getHour(openFrom) * 60) + Integer.parseInt(AppConstant.getMinutes(openFrom)) + Integer.parseInt(remainingTaskTime + "") + diff_shopclose_and_day_over));
                    }
                } while (flag);
                double selectedNextDayTimeHour_minut = Double.parseDouble(AppConstant.getHour(calNextDay.getTimeInMillis()) + "." + AppConstant.getMinutes(calNextDay.getTimeInMillis()));
                satisfyDiliveryTime = checkDelivery(deliveryArray, selectedNextDayTimeHour_minut, msg, calNextDay, satisfyDiliveryTime, addCart, myCalendar, itemName, changeTime, itemId, position);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkDelivery(JSONArray deliveryArray, double selectedTimeHour_minut, String msg, Calendar caltime, boolean satisfyDiliveryTime, boolean addCart, Calendar myCalendar, String itemName, boolean changeTime, String itemId, int position) {
        JSONObject deliveryObj = null;
        long minTime = 0;
        Calendar lastDelivery = Calendar.getInstance();
        lastDelivery.setTimeInMillis(caltime.getTimeInMillis());
        for (int i = 0; i < deliveryArray.length(); i++) {
            try {
                deliveryObj = deliveryArray.getJSONObject(i);
                if (i == 0) {
                    lastDelivery.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(deliveryArray.getJSONObject(deliveryArray.length() - 1).getLong("end_time")));
                    lastDelivery.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(deliveryArray.getJSONObject(deliveryArray.length() - 1).getLong("end_time"))));
                }
                double startTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("start_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("start_time")));
                double endTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("end_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("end_time")));

                if (selectedTimeHour_minut >= startTime && selectedTimeHour_minut <= endTime) {
                    msg = "at " + sdf.format(caltime.getTime()).toString();
                    satisfyDiliveryTime = true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (addCart) {
            if (((myCalendar.getTimeInMillis() - caltime.getTimeInMillis()) / (60 * 1000)) >= 0) {
                boolean deliverySatisfy = false;
                double delivery = Double.parseDouble(myCalendar.get(Calendar.HOUR_OF_DAY) + "." + myCalendar.get(Calendar.MINUTE) + "");
                for (int i = 0; i < deliveryArray.length(); i++) {
                    try {
                        deliveryObj = deliveryArray.getJSONObject(i);
                        if (i == 0) {
                            lastDelivery.setTimeInMillis(myCalendar.getTimeInMillis());
                            lastDelivery.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(deliveryArray.getJSONObject(deliveryArray.length() - 1).getLong("end_time")));
                            lastDelivery.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(deliveryArray.getJSONObject(deliveryArray.length() - 1).getLong("end_time"))));
                        }
                        double startTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("start_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("start_time")));
                        double endTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("end_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("end_time")));
                        if (delivery >= startTime && delivery <= endTime) {
                            deliverySatisfy = true;
                            if (changeTime) {
                                if (AppConstant.isNetworkAvailable(MyCart.this))
                                    new UpdateItemDeliveryDate(itemId, position, myCalendar.getTimeInMillis()).execute();
                                else {
                                    AppConstant.showNetworkError(MyCart.this);
                                }
                            } else {
                                isProceedToCheckOut = true;
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!deliverySatisfy) {
                    try {
                        caltime = setDeliveryNotSatisfyTime(myCalendar, deliveryArray, lastDelivery, delivery);
                        msg = itemName + " is available to you around " + "at " + sdf.format(caltime.getTime()).toString() + "\n\n";
                        if (changeTime) {
                            AppConstant.showSingleButtonAlertDialog(MyCart.this, "Message", "Product is not available at your time.It will be available to you around " + sdf.format(caltime.getTime()).toString());
                        } else {
                            isProceedToCheckOut = false;
                            notSetisfyDelivery.add(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (changeTime) {
                    AppConstant.showSingleButtonAlertDialog(MyCart.this, "Message", "Product is not available at your time.It will be available to you around " + sdf.format(caltime.getTime()).toString());
                } else {
                    isProceedToCheckOut = false;
                    msg = itemName + " is available to you around " + "at " + sdf.format(caltime.getTime()).toString() + "\n\n";
                    notSetisfyDelivery.add(msg);
                }
            }
        } else {
            if (!satisfyDiliveryTime) {
                try {

                    caltime = setDeliveryNotSatisfyTime(caltime, deliveryArray, lastDelivery, selectedTimeHour_minut);
                    satisfyDiliveryTime = true;
                    msg = "at " + sdf.format(caltime.getTime()).toString();
                    AppConstant.showSingleButtonAlertDialog(MyCart.this, "Message", "Product is available  to you around " + msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                AppConstant.showSingleButtonAlertDialog(MyCart.this, "Message", "Product is available  to you around " + msg);
            }
        }


        return satisfyDiliveryTime;
    }

    private Calendar setDeliveryNotSatisfyTime(Calendar caltimefinal, JSONArray deliveryArray, Calendar lastDelivery, double selectedTimeHour_minut) {
        Calendar caltime = Calendar.getInstance();
        caltime.setTimeInMillis(caltimefinal.getTimeInMillis());
        try {
            JSONObject deliveryObj;
            if (caltime.getTimeInMillis() < lastDelivery.getTimeInMillis()) {
                for (int i = 0; i < deliveryArray.length() - 1; i++) {
                    deliveryObj = deliveryArray.getJSONObject(i);
                    double startTime = Double.parseDouble(AppConstant.getHour(deliveryArray.getJSONObject(i + 1).getLong("start_time")) + "." + AppConstant.getMinutes(deliveryArray.getJSONObject(i + 1).getLong("start_time")));
                    double endTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("end_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("end_time")));
                    if (selectedTimeHour_minut >= endTime && selectedTimeHour_minut <= startTime) {
                        caltime.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(deliveryArray.getJSONObject(i + 1).getLong("start_time")));
                        caltime.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(deliveryArray.getJSONObject(i + 1).getLong("start_time"))));
                        break;
                    }
                }
            } else {
                caltime.add(Calendar.DAY_OF_MONTH, 1);
                caltime.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(deliveryArray.getJSONObject(0).getLong("start_time")));
                caltime.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(deliveryArray.getJSONObject(0).getLong("start_time"))));
            }
        } catch (Exception e) {
        }
        return caltime;
    }

    @SuppressWarnings("deprecation")
    class ViewCartTask extends AsyncTask<Void, Void, Void> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(MyCart.this);
        JSONObject jObj;
        boolean flag = false;
        private int responseCode;

        public ViewCartTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            JSONParser jParser = new JSONParser(MyCart.this);
            try {
                String data[] = jParser.sendGetReq(Constants.api_v1 + Constants.api_view_cart);
                responseCode = Integer.parseInt(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = Boolean.parseBoolean(jObj.getString("flag"));
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (responseCode == 200) {
                    if (flag) {
                        try {
                            makeRecyclerVisible();
                            JSONObject object = jObj.getJSONObject("data");
                            JSONArray jsonArray = object.getJSONArray("cart");
                            try {
                                JSONObject item = object.getJSONObject("summary");
                                final int qty = Integer.parseInt(item.has("qty") ? (item.isNull("qty") ? "0" : item.getString("qty")) : "0");
                                final double amt = Double.parseDouble(item.has("total") ? (item.isNull("total") ? "0" : item.getString("total")) : "0");
                                if (qty > 0 && amt > 0) {
                                    txtSubTotal.setText("Total item : " + qty + "");
                                    txtTotalPrice.setText(AppConstant.rupee_symbol + amt + "");

                                    AppConstant.copyJSONArray(jsonArray, myCartList);
                                    myCartMainList.addAll(myCartList);
                                    UserDataPreferences.saveCartCount(MyCart.this, myCartList.size());
                                    if (myCartList.size() > 0) {
                                        MainActivity.txtCart.setVisibility(View.VISIBLE);
                                        MainActivity.txtCart.setText(UserDataPreferences.getCartCount(MyCart.this) + "");
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                AppConstant.showSingleButtonAlertDialog(MyCart.this,
                                        "Error",
                                        "Unable to connect Web Services.\nPlease try again.");
                                Log.e(e.getClass().getName(), e.getMessage(), e);
                            }

                            AppConstant.copyJSONArray(jsonArray, myCartList);
                            myCartMainList.addAll(myCartList);
                            UserDataPreferences.saveCartCount(MyCart.this, myCartList.size());
                            if (myCartList.size() > 0) {
                                MainActivity.txtCart.setVisibility(View.VISIBLE);
                                MainActivity.txtCart.setText(myCartList.size() + "");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // showcaseRecyclerView();
                                    }
                                }, 1);


                            }
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            AppConstant.showSingleButtonAlertDialog(MyCart.this,
                                    "Error",
                                    "Unable to connect Web Services.\nPlease try again.");
                            Log.e(e.getClass().getName(), e.getMessage(), e);
                        }
                    } else {
                        makeRecyclerGone("No item in cart");
                        UserDataPreferences.saveCartCount(MyCart.this, 0);
                        MainActivity.txtCart.setVisibility(View.GONE);
                    }

                } else {
                    makeRecyclerGone("No item in cart");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class GetMiscellaneousTask extends AsyncTask<Void, Void, Void> {
        boolean showProgressBar;
        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(MyCart.this);
        boolean flag;
        JSONObject jObj;
        private JSONObject getjObj;
        private int responseCode;
        private JSONArray jsonArray;


        public GetMiscellaneousTask(boolean showProgressBar, JSONArray jsonArray) {
            this.showProgressBar = showProgressBar;
            this.jsonArray = jsonArray;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (showProgressBar) {
                dialog.setCancelable(true);
                dialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONParser jParser = new JSONParser(MyCart.this);
            try {
                String data[] = jParser.sendGetReq(Constants.api_v1 + Constants.api_item_accessories);
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    Log.d("This is==>", jObj.toString());
                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                    if (flag) {
                        getjObj = jObj.getJSONObject("data");
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
                if (showProgressBar) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                }

                if (responseCode == 200 && flag) {
                    UserDataPreferences.saveMiscellaneousInfo(MyCart.this, getjObj.toString());
                    Intent intent = new Intent(MyCart.this, MiscellaneousActivity.class);
                    intent.putExtra("products", jsonArray.toString());
                    startActivityForResult(intent, 6);
                    overridePendingTransition(R.anim.animation, R.anim.animation2);
                } else if (responseCode != 200) {
                    AppConstant.unableConnectServer(MyCart.this);
                } else if (responseCode == 200 && !flag) {
//                    Intent intent = new Intent(MyCart.this, PaymentInfoActivity.class);
//                    intent.putExtra("products", jsonArray.toString());
//                    startActivityForResult(intent, 6);
//                    overridePendingTransition(R.anim.animation, R.anim.animation2);
                    JSONArray jsonArray1 = UserDataPreferences.getUserAddressBook(MyCart.this);
                    if (null != jsonArray1 && jsonArray1.length() > 0) {
                        Intent intent = new Intent(MyCart.this, PaymentInfoActivity.class);
                        intent.putExtra("products", jsonArray.toString());
                        startActivityForResult(intent, 6);
                        overridePendingTransition(R.anim.animation, R.anim.animation2);
                    } else {
                        Intent intent = new Intent(MyCart.this, AddAddressActivity.class);
                        startActivityForResult(intent, 9);
                        overridePendingTransition(R.anim.animation, R.anim.animation2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showcaseRecyclerView() {
        myCartRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                RelativeLayout childAt = (RelativeLayout) myCartRecycler.getChildAt(0).findViewById(R.id.relativeLayoutrow);

                int[] location = new int[2];
                childAt.getLocationInWindow(location);
                int x = location[0] + childAt.getWidth() / 2;
                int y = location[1] + childAt.getHeight() / 2;

                int left = location[0];
                int Top = location[1];
                int bottom = Top + childAt.getHeight();
                int[] rec = new int[4];

                rec[0] = left;
                rec[1] = Top;
                rec[2] = childAt.getWidth();
                rec[3] = bottom;

                new MaterialShowcaseView.Builder(MyCart.this)
                        .setTarget(myCartRecycler, new Point(x, y), rec, Util.WHOLE_ROW)
                        .setDismissText("GOT IT")
                        .setImage(getResources().getDrawable(R.drawable.swipe_left))
                        .setContentText("Swipe from right to left to delete Cart Item.")
                        .setDelay(10) // optional but starting animations immediately in onCreate can make them choppy
                        .singleUse("cartrecycler") // provide a unique ID used to ensure it is only shown once
                        .show();
                myCartRecycler.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });


    }

    public class UpdateItemDeliveryDate extends AsyncTask<Void, Void, Void> {
        boolean flag;
        JSONObject jObj, item;
        int position;
        private int responseCode;
        private long deliveryDate;
        private String id;
        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(MyCart.this);

        public UpdateItemDeliveryDate(String id, int position, long deliveryDate) {
            this.id = id;
            this.position = position;
            this.deliveryDate = deliveryDate;
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
                JSONParser jsonParser = new JSONParser(MyCart.this);
                JSONStringer jsonData = new JSONStringer().object()
                        .key("item_id").value(id)
                        .key("delivery_date").value(deliveryDate)
                        .key("delivery_time").value(deliveryDate)
                        .endObject();
                String[] data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_Change_delivery_date, jsonData.toString());
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
                if (dialog.isShowing())
                    dialog.dismiss();

                if (responseCode == 200 && flag) {
                    try {
                        try {
                            item = new JSONObject(myCartList.get(position).toString());
                            item.put("delivery_date", deliveryDate);
                            item.put("delivery_time", deliveryDate);
                            myCartList.set(position, item.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        adapter.notifyItemChanged(position);
                    } catch (Exception e) {

                    }

                } else if (responseCode != 200) {
                    AppConstant.showSingleButtonAlertDialog(
                            MyCart.this, "Error", "Something wen't wrong.\nPlease try again");
                } else if (responseCode == 200 && !flag) {
                    AppConstant.showSingleButtonAlertDialog(MyCart.this, "Error", "No Data Found.");
                }
            } catch (Exception e) {

            }

        }
    }

    @SuppressLint("NewApi")
    public void showDateTimePicker(final String itemId, final int position, final int itemAvailableTime) {

        LinearLayout.LayoutParams linear_wrap_wrap = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        final Dialog dateTimePicker = new Dialog(this);
        dateTimePicker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dateTimePicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dateTimePicker.setContentView(R.layout.date_time_piker_dialog);
        dateTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });
        Calendar calendar;
        calendar = Calendar.getInstance();
        updateTimeCalander.setTimeInMillis(calendar.getTimeInMillis());
        final DatePicker datePicker = (DatePicker) dateTimePicker.findViewById(R.id.datePicker);
        datePicker.setMinDate(calendar.getTimeInMillis());
        final TimePicker timePicker = (TimePicker) dateTimePicker.findViewById(R.id.timePicker);
        final ImageView imgDate = (ImageView) dateTimePicker.findViewById(R.id.imgDate);
        final ImageView imgTime = (ImageView) dateTimePicker.findViewById(R.id.imgTime);
        final Button btnSet = (Button) dateTimePicker.findViewById(R.id.btnSet);
        final Button btnCancel = (Button) dateTimePicker.findViewById(R.id.btnCancel);
        if (AppConstant.isAndroid5()) {
            btnSet.setBackground(getDrawable(R.drawable.ripple_accent_round_corner));
            btnCancel.setBackground(getDrawable(R.drawable.ripple_accent_round_corner));
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimePicker.dismiss();
            }
        });
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTimeCalander.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                Log.d("update time cal", sdf.format(updateTimeCalander.getTime()).toString() + "");
                checkItemAvalibility(true, itemAvailableTime, updateTimeCalander, "", true, itemId, position);
                dateTimePicker.dismiss();
            }
        });
        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.setVisibility(View.GONE);
                datePicker.setVisibility(View.VISIBLE);
                imgDate.setClickable(false);
                imgDate.setImageDrawable(getResources().getDrawable(R.drawable.icon_date_1));
                imgTime.setImageDrawable(getResources().getDrawable(R.drawable.icon_time_2));
                imgTime.setClickable(true);
            }
        });
        imgTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.setVisibility(View.GONE);
                timePicker.setVisibility(View.VISIBLE);
                imgDate.setClickable(true);
                imgTime.setImageDrawable(getResources().getDrawable(R.drawable.icon_time_1));
                imgDate.setImageDrawable(getResources().getDrawable(R.drawable.icon_date_2));
                imgTime.setClickable(false);
            }
        });
        dateTimePicker.show();
        dateTimePicker.setCancelable(true);
    }


}


