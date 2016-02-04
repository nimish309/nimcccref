package com.coruscate.centrecourt.AsynkTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.UserInterface.Activity.ItemDetailActivity;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONObject;

/**
 * Created by Prashant on 9/24/2015.
 */
public class SettingTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    boolean flag = false;
    private int responseCode;
    JSONObject jObj;
    CustomProgressDialog dialog;
    boolean isDialog;

    public SettingTask(Context context, boolean isDialog) {
        this.context = context;
        this.isDialog = isDialog;
        dialog=CustomProgressDialog.createProgressBar(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (isDialog) {
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        JSONParser jParser = new JSONParser(context);
        try {
            String data[] = jParser.sendGetReq(Constants.api_v1 + Constants.api_setting);
            responseCode = Integer.valueOf(data[0]);
            if (responseCode == 200) {
                jObj = new JSONObject(data[1]);
                Log.d("This is==>", jObj.toString());
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
        if (isDialog) {
            if (dialog.isShowing())
                dialog.dismiss();
        }
        if (responseCode == 200) {
            if (flag) {
                try {
                    JSONObject object = jObj.getJSONObject("data");
                    UserDataPreferences.saveSettingDetail(context, object.toString());
                } catch (Exception e) {

                }
            }
        }
    }
}
