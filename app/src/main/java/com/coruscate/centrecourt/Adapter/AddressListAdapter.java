package com.coruscate.centrecourt.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Activity.AddAddressActivity;
import com.coruscate.centrecourt.UserInterface.Activity.AddressListActivity;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by cis on 9/9/2015.
 */
public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.RecyclerViewHolder> {

    private ArrayList<String> dataList;
    private AddressListActivity addressListActivity;
    private boolean isChangeAddress; // isChangeAddress for Change Address from Delivery Fragment
    private JSONObject item;
    private int px;
    private CardView.LayoutParams layoutParams = new CardView.LayoutParams(
            CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);

    public AddressListAdapter(ArrayList<String> dataList, AddressListActivity addressListActivity) {
        this.dataList = dataList;
        this.addressListActivity = addressListActivity;
        isChangeAddress = addressListActivity.getIntent().getBooleanExtra("isChangeAddress", false);
        px = AppConstant.dpToPx(8, addressListActivity);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.address_list_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder vh, int position) {
        if (position == getItemCount() - 1) {
            layoutParams.setMargins(px, px, px, px);
        } else {
            layoutParams.setMargins(px, px, px, 0);
        }
        vh.cardView.setLayoutParams(layoutParams);

        item = getItem(position);
        if (null != item) {
            try {
                vh.txtName.setText(JSONData.getString(item, "first_name") + " " + JSONData.getString(item, "last_name"));
                vh.txtContactNumber.setText(JSONData.getString(item, "mobile"));
                String line2 = JSONData.getString(item, "line2");
                if (line2.length() > 0) {
                    line2 += "\n";
                }
                vh.txtAddress.setText(JSONData.getString(item, "line1") + "\n" + line2 +
                        JSONData.getString(item, "city") + "," + JSONData.getString(item, "state") + "-" +
                        JSONData.getString(item, "pincode") + "\n" +
                        JSONData.getString(item, "country"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    protected void showPopup(View anchorView, final int position) {
        final PopupWindow popup = new PopupWindow(addressListActivity);
        LayoutInflater vi = (LayoutInflater) addressListActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = vi.inflate(R.layout.adress_add_remove_popup, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.showAsDropDown(anchorView);
        final Button btnEdit;
        final Button btnRemove;

        btnEdit = (Button) layout.findViewById(R.id.btnEdit);
        btnRemove = (Button) layout.findViewById(R.id.btnRemove);

        if (dataList.size() > 1) {
            btnRemove.setVisibility(View.VISIBLE);
        } else {
            btnRemove.setVisibility(View.GONE);
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                Intent intent = new Intent(addressListActivity, AddAddressActivity.class);
                intent.putExtra("title", "Edit Address");
                intent.putExtra("position", position);
                addressListActivity.startActivityForResult(intent, 3);
                addressListActivity.overridePendingTransition(R.anim.animation, R.anim.animation2);
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                if (AppConstant.isNetworkAvailable(addressListActivity)) {
                    new RemoveAddressTask(position).execute();
                } else {
                    AppConstant.showNetworkError(addressListActivity);
                }
            }
        });


    }

    public JSONObject getItem(int position) {
        try {
            return new JSONObject(dataList.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.txtName)
        TypefacedTextView txtName;
        @InjectView(R.id.txtContactNumber)
        TypefacedTextView txtContactNumber;
        @InjectView(R.id.txtAddress)
        TypefacedTextView txtAddress;
        @InjectView(R.id.Imgpopupt)
        ImageView Imgpopupt;
        @InjectView(R.id.cardView)
        CardView cardView;
        @InjectView(R.id.rawLayout)
        LinearLayout rawLayout;

        public RecyclerViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
            Imgpopupt.setOnClickListener(this);
            if (isChangeAddress) {
                rawLayout.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Imgpopupt:
                    showPopup(v, getLayoutPosition());
                    break;
                case R.id.rawLayout:
                    Intent i = new Intent();
                    i.putExtra("addressObject", dataList.get(getLayoutPosition()));
                    addressListActivity.setResult(2, i);
                    addressListActivity.finish();
                    addressListActivity.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                    break;
            }
        }

    }

    public class RemoveAddressTask extends AsyncTask<Void, Void, Void> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(addressListActivity);
        boolean flag;
        JSONObject jObj;
        private int responseCode;
        private String message;
        private int position;
        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        public RemoveAddressTask(int position) {
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONArray jsonArray = UserDataPreferences.getUserAddressBook(addressListActivity);

                if (jsonArray != null) {
                    jsonObject = jsonArray.getJSONObject(position);
                }
                JSONParser jsonParser = new JSONParser(addressListActivity);
                JSONObject jsonData = new JSONObject();
                jsonData.put("address_book", jsonObject);
                String[] data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_delete_address, jsonData.toString());
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = JSONData.getBoolean(jObj, "flag");
                    message = JSONData.getString(jObj, "message");
                    if (flag) {
                        JSONObject jsonObject = JSONData.getJSONObjectDefNull(jObj, "data");
                        if (jsonObject != null) {
                            UserDataPreferences.saveUserInfo(addressListActivity, jsonObject.toString());
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

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (responseCode == 200) {
                if (flag) {
                    addressListActivity.refreshListData();
                }
                AppConstant.showToastShort(addressListActivity, message);
            }
        }
    }

}
