package com.coruscate.centrecourt.AsynkTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.coruscate.centrecourt.Adapter.OrderHistoryAdapter;
import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.UserInterface.Activity.OrderListActivity;
import com.coruscate.centrecourt.UserInterface.Fragments.Profile.ProfileFragment;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by prashant.dodia on 11/28/2015.
 */
public class GetOrderListTask extends AsyncTask<Void, Void, String[]> {
    private Activity activity;
    private final CustomProgressDialog dialog;
    boolean flag;
    private int responseCode;

    public GetOrderListTask(Activity activity) {
        this.activity = activity;
        dialog = CustomProgressDialog.createProgressBar(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected String[] doInBackground(Void... params) {
        JSONParser jParser = new JSONParser(activity);
        try {
            return jParser.sendGetReq(Constants.api_v1 + Constants.api_order_history);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(String[] data) {
        super.onPostExecute(data);
        try {
            if (dialog.isShowing())
                dialog.dismiss();
            responseCode = Integer.valueOf(data[0]);

            if (responseCode == 200) {
                JSONObject jObj = new JSONObject(data[1]);
                flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                JSONArray jsonArray = JSONData.getJSONArray(jObj, "data");
                if (flag) {
                    if (activity instanceof MainActivity) {
                        AppConstant.copyJSONArray(jsonArray, ProfileFragment.orderList);
                        if (ProfileFragment.orderList.size() == 1) {
                            ProfileFragment.txtNoOrder.setText("You have " + ProfileFragment.orderList.size() + " order");
                        } else {
                            ProfileFragment.txtNoOrder.setText("You have " + ProfileFragment.orderList.size() + " orders");
                        }
                    } else if (activity instanceof OrderListActivity) {
                        ArrayList<String> orderList = new ArrayList<>();
                        AppConstant.copyJSONArray(jsonArray, orderList);
                        if (orderList.size() > 0) {
                            OrderListActivity.adapter = new OrderHistoryAdapter(orderList, activity);
                            OrderListActivity.myOrderRecycler.setAdapter(OrderListActivity.adapter);
                        } else {
                            OrderListActivity.myOrderRecycler.setVisibility(View.GONE);
                            OrderListActivity.txtNoResult.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (activity instanceof OrderListActivity) {
                        OrderListActivity.myOrderRecycler.setVisibility(View.GONE);
                        OrderListActivity.txtNoResult.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (activity instanceof OrderListActivity) {
                    OrderListActivity.myOrderRecycler.setVisibility(View.GONE);
                    OrderListActivity.txtNoResult.setVisibility(View.VISIBLE);
                    AppConstant.showNetworkError(activity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
