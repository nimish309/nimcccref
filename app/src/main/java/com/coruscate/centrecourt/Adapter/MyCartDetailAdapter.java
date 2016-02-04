package com.coruscate.centrecourt.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.coruscate.centrecourt.AsynkTask.AddToWishListTask;
import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.CustomeDateAndTimePicker;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Activity.ItemDetailActivity;
import com.coruscate.centrecourt.UserInterface.Activity.MyCart;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;
import com.marshalchen.ultimaterecyclerview.SwipeableUltimateViewAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.swipe.SwipeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by CIS-16 on 7/3/2015.
 */


public class MyCartDetailAdapter extends SwipeableUltimateViewAdapter {

    int px8;
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM,yyyy hh:mm a");
    CardView.LayoutParams layoutParams = new CardView.LayoutParams(
            CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
    private ArrayList<String> myCartList;
    private MyCart context;
    private int total;
    private int totalItem = 0;
    private double totalAmount;
    private JSONObject objectFlavour, object, jsonObject, item, jobj, jObject;
    private int quntity, maxQty, minQty;
    private CustomeDateAndTimePicker customeDateAndTimePicker;

    public MyCartDetailAdapter(ArrayList<String> myCartList, MyCart context) {
        this.myCartList = myCartList;
        this.context = context;
        px8 = AppConstant.dpToPx(8, context);
    }

    @Override
    public UltimateRecyclerviewViewHolder getViewHolder(View view) {
        return new UltimateRecyclerviewViewHolder(view);
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
        final Context context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.mycart_detail_row, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        SwipeLayout swipeLayout = viewHolder.swipeLayout;
        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.setTag("" + parent);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UltimateRecyclerviewViewHolder vh, final int position) {
        super.onBindViewHolder(vh, position);
        if (position == myCartList.size() - 1) {
            layoutParams.setMargins(px8, px8, px8, px8);
            ((ViewHolder) vh).cardView.setLayoutParams(layoutParams);
        }
        if (myCartList.size() > 0) {
            item = getItem(position);
            try {
                try {
                    final int qty = Integer.parseInt(item.has("qty") ? (item.isNull("qty") ? "0" : item.getString("qty")) : "0");
                    final double amt = Double.parseDouble(item.has("total") ? (item.isNull("total") ? "0" : item.getString("total")) : "0");
                    final double weight = JSONData.getDouble(item, "weight");
                    final String msg = JSONData.getString(item, "message");
                    final String instruction = JSONData.getString(item, "instruction");

                    ((ViewHolder) vh).txtRupeesSymbol.setText(AppConstant.rupee_symbol + "");
                    ((ViewHolder) vh).txtTotal.setText(qty + "");
                    ((ViewHolder) vh).txtPrice.setText(amt + "");

                    if (JSONData.getBoolean(item, "eggless")) {
                        ((ViewHolder) vh).imgIconWithOrWithoutEgg.setVisibility(View.VISIBLE);
                        ((ViewHolder) vh).imgIconWithOrWithoutEgg.setImageResource(R.drawable.icon_egg_less);
                    } else if (JSONData.getBoolean(item, "eggwith")) {
                        ((ViewHolder) vh).imgIconWithOrWithoutEgg.setVisibility(View.VISIBLE);
                        ((ViewHolder) vh).imgIconWithOrWithoutEgg.setImageResource(R.drawable.icon_egg);
                    } else {
                        ((ViewHolder) vh).imgIconWithOrWithoutEgg.setVisibility(View.GONE);
                    }


                    if (weight != 0.0) {
                        ((ViewHolder) vh).linearWeight.setVisibility(View.VISIBLE);
                        ((ViewHolder) vh).txtWeight.setText(String.valueOf(weight) + " KG");
                    } else {
                        ((ViewHolder) vh).linearWeight.setVisibility(View.INVISIBLE);
                    }

                    if (msg.length() > 0) {
                        ((ViewHolder) vh).linMsgOnCake.setVisibility(View.VISIBLE);
                        ((ViewHolder) vh).txtMsgOnCake.setText(msg);
                    } else {
                        ((ViewHolder) vh).linMsgOnCake.setVisibility(View.GONE);
                    }
                    if (instruction.length() > 0) {
                        ((ViewHolder) vh).linSpecialIns.setVisibility(View.VISIBLE);
                        ((ViewHolder) vh).txtSpecialIns.setText(instruction);
                        ((ViewHolder) vh).txtSpecialIns.setSelected(true);
                    } else {
                        ((ViewHolder) vh).linSpecialIns.setVisibility(View.INVISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                objectFlavour = JSONData.getJSONObject(item, "flavour");
                final String flavour = JSONData.getString(objectFlavour, "name");
                if (!flavour.equals("")) {
                    ((ViewHolder) vh).linearFlavour.setVisibility(View.VISIBLE);
                    ((ViewHolder) vh).txtFlavour.setText(flavour);
                } else {
                    ((ViewHolder) vh).linearFlavour.setVisibility(View.INVISIBLE);
                }

                ((ViewHolder) vh).txtDeliveryDate.setText(AppConstant.getDate(JSONData.getLong(item, "delivery_date")));

                try {
                    object = item.getJSONObject("item_details");
                    final String name = (object.has("name") ? (object.isNull("name") ? "" : object.getString("name")) : "");
                    final String desc = "";
                    if (name.toString().equals("")) {
                        ((ViewHolder) vh).txtName.setVisibility(View.GONE);
                    } else {
                        ((ViewHolder) vh).txtName.setVisibility(View.VISIBLE);
                        ((ViewHolder) vh).txtName.setText(name);
                    }
                    if (desc.toString().equals("")) {
                        ((ViewHolder) vh).txtDesc.setVisibility(View.GONE);
                    } else {
                        ((ViewHolder) vh).txtDesc.setVisibility(View.VISIBLE);
                        ((ViewHolder) vh).txtDesc.setText(desc);
                    }

                    ((ViewHolder) vh).txtSku.setText(JSONData.getString(object, "sku"));
                    try {
                        jobj = object.getJSONObject("main_image");
                        final String url = jobj.has("medium") ? (jobj.isNull("medium") ? "" : jobj.getString("medium")) : "";
                        setImage(url, ((ViewHolder) vh).ImgRowImage);
                    } catch (Exception e) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

              /*  long delivery_date = JSONData.getLong(item, "delivery_date");
                Log.d("MyCartDetailAdapter", delivery_date + "");
                Log.d("MyCartDetailAdapter", Calendar.getInstance().getTimeInMillis() + "");
                if (delivery_date < Calendar.getInstance().getTimeInMillis()) {
                    ((ViewHolder) vh).imgIncrease.setClickable(false);
                    ((ViewHolder) vh).imgDecrease.setClickable(false);
                } else {
                    ((ViewHolder) vh).imgIncrease.setClickable(true);
                    ((ViewHolder) vh).imgDecrease.setClickable(true);
                }*/

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return myCartList.size();
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    public JSONObject getItem(int position) {
        try {
            return new JSONObject(myCartList.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setImage(String imageUrl, ImageView imageViewIcon) {
        if (!imageUrl.equals("")) {
            Glide.with(context)
                    .load(Constants.api_ip + imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.icon_default_large)
                    .centerCrop()
                    .crossFade()
                    .into(imageViewIcon);
        }
    }

    private void checkItemAvalibility(boolean addCart, boolean changeTime, int itemAvailableTime, long deliveryDate, long updateDate) {

        try {

            String msg = "";
            boolean satisfyDiliveryTime = false;
            JSONObject jObj = new JSONObject(UserDataPreferences.getSettingDetail(context));
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
                    checkDelivery(deliveryArray, selectedTimeHour_minut, msg, caltime, satisfyDiliveryTime, addCart, changeTime, deliveryDate, updateDate);
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

                Log.d("workingHr=", workingHr + "");
                do {
                    if (remainingTaskTime > (workingHr)) {
                        calNextDay.add(Calendar.MINUTE, ((AppConstant.getHour(openFrom) * 60) + Integer.parseInt(AppConstant.getMinutes(openFrom)) + Integer.parseInt(workingHr + "") + diff_shopclose_and_day_over));
                        remainingTaskTime -= workingHr;
                        flag = true;
                        Log.d("calNextDayfinal time=", sdf.format(calNextDay.getTime()).toString() + "");
                    } else {
                        flag = false;
                        calNextDay.add(Calendar.MINUTE, ((AppConstant.getHour(openFrom) * 60) + Integer.parseInt(AppConstant.getMinutes(openFrom)) + Integer.parseInt(remainingTaskTime + "") + diff_shopclose_and_day_over));

                        Log.d("calNextDayfinal time=", sdf.format(calNextDay.getTime()).toString() + "");
                    }
                } while (flag);
                Log.d("calNextDayfinal time=", sdf.format(calNextDay.getTime()).toString() + "");
                double selectedNextDayTimeHour_minut = Double.parseDouble(AppConstant.getHour(calNextDay.getTimeInMillis()) + "." + AppConstant.getMinutes(calNextDay.getTimeInMillis()));
                checkDelivery(deliveryArray, selectedNextDayTimeHour_minut, msg, calNextDay, satisfyDiliveryTime, addCart, changeTime, deliveryDate, updateDate);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkDelivery(JSONArray deliveryArray, double selectedTimeHour_minut, String msg, Calendar caltime, boolean satisfyDiliveryTime, boolean addCart, boolean changeTime, long deliveryDate, long updateTime) {
        JSONObject deliveryObj = null;
        long minTime = 0;
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.setTimeInMillis(deliveryDate);
        for (int i = 0; i < deliveryArray.length(); i++) {
            try {
                deliveryObj = deliveryArray.getJSONObject(i);

                double startTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("start_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("start_time")));
                double endTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("end_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("end_time")));

                if (selectedTimeHour_minut >= startTime && selectedTimeHour_minut <= endTime) {
                    msg = "at " + sdf.format(caltime.getTime()).toString();
                    satisfyDiliveryTime = true;
                    Log.d("date : ", msg);
                    Log.d("satisfyDeliveryTime", satisfyDiliveryTime + "");

                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!satisfyDiliveryTime) {
            try {
                caltime.add(Calendar.DAY_OF_MONTH, 1);
                caltime.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(deliveryArray.getJSONObject(0).getLong("start_time")));
                caltime.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(deliveryArray.getJSONObject(0).getLong("start_time"))));
                satisfyDiliveryTime = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (addCart) {
                if (changeTime) {
                    myCalendar.setTimeInMillis(updateTime);
                }
                Log.d("mycalander", sdf.format(myCalendar.getTime()).toString() + "");
                Log.d("caltime", sdf.format(caltime.getTime()).toString() + "");
                if (myCalendar.getTimeInMillis() >= caltime.getTimeInMillis()) {
                    boolean deliverySatisfy = false;
                    for (int i = 0; i < deliveryArray.length(); i++) {
                        try {
                            deliveryObj = deliveryArray.getJSONObject(i);
                            double startTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("start_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("start_time")));
                            double endTime = Double.parseDouble(AppConstant.getHour(deliveryObj.getLong("end_time")) + "." + AppConstant.getMinutes(deliveryObj.getLong("end_time")));
                            double delivery = Double.parseDouble(myCalendar.get(Calendar.HOUR_OF_DAY) + "." + myCalendar.get(Calendar.MINUTE) + "");
                            if (delivery >= startTime && delivery <= endTime) {
                                deliverySatisfy = true;
                                if (changeTime) {

                                } else {
                                    //continueCheckOut();
                                }
                                break;
                            } else {

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!deliverySatisfy) {
                        try {
                            Log.d("mycalander", sdf.format(myCalendar.getTime()).toString() + "");
                            double startTime = Double.parseDouble(AppConstant.getHour(deliveryArray.getJSONObject(0).getLong("start_time")) + "." + AppConstant.getMinutes(deliveryArray.getJSONObject(0).getLong("start_time")));
                            if (startTime < Double.parseDouble(myCalendar.get(Calendar.HOUR_OF_DAY) + "." + myCalendar.get(Calendar.MINUTE))) {
                                myCalendar.add(Calendar.DAY_OF_MONTH, 1);
                            }
                            myCalendar.set(Calendar.HOUR_OF_DAY, AppConstant.getHour(deliveryArray.getJSONObject(0).getLong("start_time")));
                            myCalendar.set(Calendar.MINUTE, Integer.parseInt(AppConstant.getMinutes(deliveryArray.getJSONObject(0).getLong("start_time"))));

                            Log.d("mycalander", sdf.format(myCalendar.getTime()).toString() + "");
                            msg = "at " + sdf.format(myCalendar.getTime()).toString();
                            AppConstant.showSingleButtonAlertDialog(context, "Message", "Product is not available at your time.It will be available to you around " + msg);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    AppConstant.showSingleButtonAlertDialog(context, "Message", "Product is not available at your time.It will be available to you around " + msg);
                }
            } else {
                AppConstant.showSingleButtonAlertDialog(context, "Message", "Product is available at your time." + msg);
            }
        }


        return satisfyDiliveryTime;
    }

    class ViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {
        TextView txtName, txtSpecialIns, txtMsgOnCake, txtDesc, txtTotal, txtPrice, txtRupeesSymbol, txtDeliveryDate, txtSku, txtFlavour, txtWeight;
        CardView cardView;
        LinearLayout main_background_view, linearlayout, linearFlavour, linearWeight, linMsgOnCake, linSpecialIns;
        SwipeLayout recyclerview_swipe;
        RelativeLayout relativeLayoutrow;
        private ImageView ImgRowImage, imgIncrease, imgDecrease, imgDelete, imgIconWithOrWithoutEgg, imgWishList;
        private ProgressBar progressBarRow;
        private SwipeLayout swipeLayout;


        public ViewHolder(View v) {
            super(v);
            imgWishList = (ImageView) v.findViewById(R.id.imgWishList);
            ImgRowImage = (ImageView) v.findViewById(R.id.ImgRowImage);
            imgIncrease = (ImageView) v.findViewById(R.id.imgIncrease);
            imgDecrease = (ImageView) v.findViewById(R.id.imgDecrease);
            imgIconWithOrWithoutEgg = (ImageView) v.findViewById(R.id.imgIconWithOrWithoutEgg);
            imgDelete = (ImageView) v.findViewById(R.id.imgDelete);
            txtName = (TextView) v.findViewById(R.id.txtName);
            txtSpecialIns = (TextView) v.findViewById(R.id.txtSpecialIns);
            txtMsgOnCake = (TextView) v.findViewById(R.id.txtMsgOnCake);
            txtFlavour = (TextView) v.findViewById(R.id.txtFlavour);
            txtWeight = (TextView) v.findViewById(R.id.txtWeight);
            txtSku = (TextView) v.findViewById(R.id.txtSku);
            txtRupeesSymbol = (TextView) v.findViewById(R.id.txtRupeesSymbol);
            txtDesc = (TextView) v.findViewById(R.id.txtNewSession);
            txtTotal = (TextView) v.findViewById(R.id.txtTotal);
            txtPrice = (TextView) v.findViewById(R.id.txtPrice);
            txtDeliveryDate = (TextView) v.findViewById(R.id.txtDeliveryDate);
            cardView = (CardView) v.findViewById(R.id.cardView);
            progressBarRow = (ProgressBar) v.findViewById(R.id.progressBarRow);
            main_background_view = (LinearLayout) v.findViewById(R.id.main_background_view);
            recyclerview_swipe = (SwipeLayout) v.findViewById(R.id.recyclerview_swipe);
            relativeLayoutrow = (RelativeLayout) v.findViewById(R.id.relativeLayoutrow);
            swipeLayout = (SwipeLayout) v.findViewById(R.id.recyclerview_swipe);
            linearlayout = (LinearLayout) v.findViewById(R.id.linearlayout);
            linearFlavour = (LinearLayout) v.findViewById(R.id.linearFlavour);
            linearWeight = (LinearLayout) v.findViewById(R.id.linearWeight);
            linMsgOnCake = (LinearLayout) v.findViewById(R.id.linMsgOnCake);
            linSpecialIns = (LinearLayout) v.findViewById(R.id.linSpecialIns);
            imgDelete.setOnClickListener(this);
            imgIncrease.setOnClickListener(this);
            imgDecrease.setOnClickListener(this);
            txtDeliveryDate.setOnClickListener(this);
            imgWishList.setOnClickListener(this);
            AppConstant.scaleAnimationOfView(v, context);
        }


        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imgWishList) {
                if (AppConstant.isNetworkAvailable(context)) {
                    try {
                        JSONObject jsonObject = getItem(getLayoutPosition());
                        imgWishList.setClickable(false);
                        new AddToWishListTask(imgWishList, JSONData.getString(jsonObject, "id"), context, false, "cart", getLayoutPosition(), context).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    AppConstant.showNetworkError(context);
                }

            } else if (v.getId() == R.id.imgDelete) {
                jsonObject = getItem(getLayoutPosition());
                try {
                    if (AppConstant.isNetworkAvailable(context)) {

                        new RemoveItem((jsonObject.has("id") ? (jsonObject.isNull("id") ? "" : jsonObject.getString("id")) : ""), getLayoutPosition()).execute();


                    } else {
                        AppConstant.showNetworkError(context);
                    }
                } catch (Exception e) {
                }

            } else if (v.getId() == R.id.imgIncrease) {
                if (AppConstant.isNetworkAvailable(context)) {
                    jsonObject = getItem(getLayoutPosition());
                    try {
                        if (AppConstant.isNetworkAvailable(context)) {

                            quntity = (Integer.parseInt(txtTotal.getText().toString()) + 1);
                            jObject = jsonObject.getJSONObject("item_details");
                           /* maxQty = Integer.parseInt(jObject.has("max_ordered_quantity") ? (jObject.isNull("max_ordered_quantity") ? "0" : jObject.getString("max_ordered_quantity")) : "0");
                            minQty = Integer.parseInt(jObject.has("min_ordered_quantity") ? (jObject.isNull("min_ordered_quantity") ? "0" : jObject.getString("min_ordered_quantity")) : "0");
                          */
                            maxQty = jObject.has("max_ordered_quantity") ? (jObject.isNull("max_ordered_quantity") ? 10 : jObject.getInt("max_ordered_quantity")) : 10;
                            minQty = jObject.has("min_ordered_quantity") ? (jObject.isNull("min_ordered_quantity") ? 1 : jObject.getInt("min_ordered_quantity")) : 1;

                            if (quntity >= minQty && quntity <= maxQty) {
                                new UpdateItem((jsonObject.has("id") ? (jsonObject.isNull("id") ? "" : jsonObject.getString("id")) : ""), getLayoutPosition(), (Integer.parseInt(txtTotal.getText().toString()) + 1) + "", progressBarRow, imgIncrease, imgDecrease).execute();
                            } else {
                                AppConstant.displayErroMessage(v, "Quntity must between " + minQty + " to " + maxQty, context);
                            }
                        } else {
                            AppConstant.showNetworkError(context);
                        }
                    } catch (Exception e) {
                    }
                }
            } else if (v.getId() == R.id.imgDecrease) {
                total = Integer.parseInt(txtTotal.getText().toString());
                if (total > 1) {
                    if (AppConstant.isNetworkAvailable(context)) {
                        jsonObject = getItem(getLayoutPosition());
                        try {
                            jObject = jsonObject.getJSONObject("item_details");
                            quntity = (Integer.parseInt(txtTotal.getText().toString()) - 1);
                          /*  maxQty = Integer.parseInt(jObject.has("max_ordered_quantity") ? (jObject.isNull("max_ordered_quantity") ? "0" : jObject.getString("max_ordered_quantity")) : "0");
                            minQty = Integer.parseInt(jObject.has("min_ordered_quantity") ? (jObject.isNull("min_ordered_quantity") ? "0" : jObject.getString("min_ordered_quantity")) : "0");
                           */
                            maxQty = jObject.has("max_ordered_quantity") ? (jObject.isNull("max_ordered_quantity") ? 10 : jObject.getInt("max_ordered_quantity")) : 10;
                            minQty = jObject.has("min_ordered_quantity") ? (jObject.isNull("min_ordered_quantity") ? 1 : jObject.getInt("min_ordered_quantity")) : 1;

                            if (quntity >= minQty && quntity <= maxQty) {
                                new UpdateItem((jsonObject.has("id") ? (jsonObject.isNull("id") ? "" : jsonObject.getString("id")) : ""), getLayoutPosition(), (Integer.parseInt(txtTotal.getText().toString()) - 1) + "", progressBarRow, imgIncrease, imgDecrease).execute();
                            } else {
                                AppConstant.displayErroMessage(v, "quntity must between " + minQty + " to " + maxQty, context);
                            }

                        } catch (Exception e) {
                        }

                    }
                } else {
                    AppConstant.displayErroMessage(v, "Quantity must greater then 0", context);
                }
            } else if (v.getId() == R.id.linearlayout) {
                jsonObject = getItem(getLayoutPosition());
                try {
                    jObject = jsonObject.getJSONObject("item_details");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra("position", 0);
                intent.putExtra("type", "MyCart");
                intent.putExtra("detail", jObject.toString());
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.animation, R.anim.animation2);
            } else if (v.getId() == R.id.txtDeliveryDate) {
                jsonObject = getItem(getLayoutPosition());
                try {
                    jObject = jsonObject.getJSONObject("item_details");
                    context.showDateTimePicker(JSONData.getString(jsonObject, "id"), getLayoutPosition(), JSONData.getInt(jObject, "product_available_in"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }


        public class RemoveItem extends AsyncTask<Void, Void, Void> {

            private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(context);
            boolean flag;
            JSONObject jObj;
            int position;
            private int responseCode;
            private JSONArray jsonArray;
            private String id;

            public RemoveItem(String id, int position) {
                this.id = id;
                this.position = position;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JSONParser jsonParser = new JSONParser(context);
                    JSONStringer jsonData = new JSONStringer().object()
                            .key("item_id").value(id).endObject();
                    String[] data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_remove_item_cart, jsonData.toString());
                    responseCode = Integer.valueOf(data[0]);
                    if (responseCode == 200) {
                        jObj = new JSONObject(data[1]);
                        flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                        if (flag) {


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

                    if (responseCode == 200 && flag) {
                        myCartList.remove(position);
                        totalItem = 0;
                        totalAmount = 0;
                        if (myCartList.size() == 0) {
                            context.txtSubTotal.setVisibility(View.GONE);
                            context.txtTotalPrice.setVisibility(View.GONE);
                            context.makeRecyclerGone("No item in cart");
                        } else {
                            for (int i = 0; i < myCartList.size(); i++) {
                                try {
                                    item = new JSONObject(myCartList.get(i).toString());
                                    final int qty = Integer.parseInt(item.has("qty") ? (item.isNull("qty") ? "0" : item.getString("qty")) : "0");
                                    final double amt = Double.parseDouble(item.has("total") ? (item.isNull("total") ? "0" : item.getString("total")) : "0");
                                    totalItem += qty;
                                    totalAmount += amt;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            context.txtSubTotal.setText("Total item : " + totalItem + "");
                            context.txtTotalPrice.setText(AppConstant.rupee_symbol + totalAmount + "");
                        }
                        UserDataPreferences.saveCartCount(context, myCartList.size());
                        if (myCartList.size() > 0) {
                            MainActivity.txtCart.setVisibility(View.VISIBLE);
                            MainActivity.txtCart.setText(myCartList.size() + "");
                        } else {
                            MainActivity.txtCart.setVisibility(View.GONE);
                        }
                        notifyItemRemoved(position);
                    } else if (responseCode != 200) {
                        AppConstant.showSingleButtonAlertDialog(
                                context, "Error",
                                "Something went wrong.\nPlease try again");
                    } else if (responseCode == 200 && !flag) {
                        AppConstant.showSingleButtonAlertDialog(context, "Error", "No Data Found.");
                    }
                } catch (Exception e) {

                }
            }

        }

        public class UpdateItem extends AsyncTask<Void, Void, Void> {
            boolean flag;
            JSONObject jObj;
            int position;
            private int responseCode;
            private JSONArray jsonArray;
            private String id, qty;
            private ProgressBar progressBarRow;
            private ImageView imgIncrease, imgDecrease;

            public UpdateItem(String id, int position, String qty, ProgressBar progressBarRow, ImageView imgIncrease, ImageView imgDecrease) {
                this.id = id;
                this.qty = qty;
                this.position = position;
                this.progressBarRow = progressBarRow;
                this.imgIncrease = imgIncrease;
                this.imgDecrease = imgDecrease;

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBarRow.setVisibility(View.VISIBLE);
                imgIncrease.setClickable(false);
                imgDecrease.setClickable(false);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JSONParser jsonParser = new JSONParser(context);
                    JSONStringer jsonData = new JSONStringer().object()
                            .key("item_id").value(id).key("qty").value(qty).endObject();
                    String[] data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_update_item_cart, jsonData.toString());
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

                progressBarRow.setVisibility(View.GONE);
                imgIncrease.setClickable(true);
                imgDecrease.setClickable(true);
                minQty = 0;
                maxQty = 0;
                if (responseCode == 200 && flag) {
                    try {
                        try {
                            myCartList.set(position, jObj.getJSONObject("data").getJSONObject("itms").toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        totalItem = 0;
                        totalAmount = 0;
                        if (myCartList.size() == 0) {
                            context.makeRecyclerGone("No item in cart");
                        } else {
                            for (int i = 0; i < myCartList.size(); i++) {
                                try {
                                    item = new JSONObject(myCartList.get(i).toString());
                                    final int qty = Integer.parseInt(item.has("qty") ? (item.isNull("qty") ? "0" : item.getString("qty")) : "0");
                                    final double amt = Double.parseDouble(item.has("total") ? (item.isNull("total") ? "0" : item.getString("total")) : "0");
                                    totalItem += qty;
                                    totalAmount += amt;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            context.txtSubTotal.setText("Total item : " + totalItem + "");
                            context.txtTotalPrice.setText(AppConstant.rupee_symbol + totalAmount + "");
                        }

                        notifyDataSetChanged();
                    } catch (Exception e) {

                    }

                } else if (responseCode != 200) {
                    AppConstant.showSingleButtonAlertDialog(
                            context, "Error", "Something went wrong.\nPlease try again");
                } else if (responseCode == 200 && !flag) {
                    AppConstant.showSingleButtonAlertDialog(context, "Error", "No Data Found.");
                }
            }
        }
    }

}


