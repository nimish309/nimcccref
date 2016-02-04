package com.coruscate.centrecourt.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.coruscate.centrecourt.CustomControls.CustomProgressDialog;
import com.coruscate.centrecourt.CustomControls.TypedfacedButton;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Activity.OrderListActivity;
import com.coruscate.centrecourt.UserInterface.Activity.TrackOrderActivity;
import com.coruscate.centrecourt.UserInterface.Fragments.Profile.ProfileFragment;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.JSONParser;
import com.marshalchen.ultimaterecyclerview.swipe.SwipeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Prashant on 9/24/2015.
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.RecyclerViewHolder> {

    private ArrayList<String> myOrderList;
    private Activity orderListActivity;
    private int total;
    private int totalItem = 0;
    private double totalAmount;
    private JSONObject object, jsonObject, item;
    private JSONArray jsonArray;
    int px8, px4;
    CardView.LayoutParams layoutParams = new CardView.LayoutParams(
            CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);

    public OrderHistoryAdapter(ArrayList<String> myOrderList, Activity orderListActivity) {
        this.myOrderList = myOrderList;
        this.orderListActivity = orderListActivity;
        px8 = AppConstant.dpToPx(8, orderListActivity);
        px4 = AppConstant.dpToPx(4, orderListActivity);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.order_list_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(RecyclerViewHolder vh, final int position) {
        if (position == myOrderList.size() - 1) {
            layoutParams.setMargins(px8, px8, px8, px8);
            vh.cardViewTop.setLayoutParams(layoutParams);
        }
        JSONObject item = getItem(position);
        if (item != null) {
            try {
                vh.itemsLayout.removeAllViews();
                vh.txtOrderId.setText(JSONData.getString(item, "order_id"));
                JSONArray order_items = JSONData.getJSONArray(item, "order_items");

                final String orderId = JSONData.getString(item, "_id");
                int totalItem = JSONData.getInt(item, "total_qty");
                final int orderStatus = JSONData.getInt(item, "status");
                vh.btnCancel.setClickable(true);
                vh.btnCancel.setTextColor(orderListActivity.getResources().getColor(R.color.white));
                if (orderStatus == 1) {
                    vh.txtDeliveryStatus.setText("Success");
                    vh.txtDeliveryStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                } else if (orderStatus == 2) {
                    vh.txtDeliveryStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelRed));
                    vh.btnCancel.setClickable(false);
                    vh.btnCancel.setTextColor(orderListActivity.getResources().getColor(R.color.header_values_fontcolor));
                    vh.txtDeliveryStatus.setText("Cancelled");
                } else if (orderStatus == 3) {
                    vh.txtDeliveryStatus.setText("Delayed");
                    vh.txtDeliveryStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                } else if (orderStatus == 4) {
                    vh.txtDeliveryStatus.setText("Dispatched");
                    vh.txtDeliveryStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                } else if (orderStatus == 5) {
                    vh.txtDeliveryStatus.setText("In progress");
                    vh.txtDeliveryStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                } else if (orderStatus == 6) {
                    vh.txtDeliveryStatus.setText("Approval");
                    vh.txtDeliveryStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                } else if (orderStatus == 7) {
                    vh.txtDeliveryStatus.setText("Waiting for payment");
                    vh.txtDeliveryStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                } else if (orderStatus == 8) {
                    vh.txtDeliveryStatus.setText("Return refund");
                    vh.txtDeliveryStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                } else if (orderStatus == 9) {
                    vh.txtDeliveryStatus.setText("Location unfullfilable");
                    vh.txtDeliveryStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                } else if (orderStatus == 10) {
                    vh.txtDeliveryStatus.setText("Partially cancelled");
                    vh.txtDeliveryStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                } else if (orderStatus == 11) {
                    vh.txtDeliveryStatus.setText("Partially Dispatched");
                    vh.txtDeliveryStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                }
                if (totalItem > 0) {
                    vh.txtTotalItem.setText(totalItem + " item");
                } else {
                    vh.txtTotalItem.setText("");
                }
                if (order_items.length() > 0) {
                    vh.rowLayout.setVisibility(View.VISIBLE);
                    vh.txtTotalQty.setText(order_items.length() + " items");
                    for (int i = 0; i < order_items.length(); i++) {
                        try {
                            JSONObject orderObject = order_items.getJSONObject(i);
                            final String id = JSONData.getString(orderObject, "product_id");
                            View order_item_row = (LinearLayout) LayoutInflater.from(orderListActivity).inflate(
                                    R.layout.order_item_row, null);
                            SwipeLayout swipeLayout = (SwipeLayout) order_item_row.findViewById(R.id.swipeLayout);
                            swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
                            swipeLayout.setTag("" + i);
                            TextView txtItemName = (TextView) order_item_row.findViewById(R.id.txtItemName);
                            LinearLayout layoutQuantity = (LinearLayout) order_item_row.findViewById(R.id.layoutQuantity);
                            TextView txtQuantity = (TextView) order_item_row.findViewById(R.id.txtQuantity);
                            TextView txtCurentStatus = (TextView) order_item_row.findViewById(R.id.txtCurentStatus);
                            ImageView imgItem = (ImageView) order_item_row.findViewById(R.id.imgItem);
                            ImageView imgCancel = (ImageView) order_item_row.findViewById(R.id.imgCancel);
                            TextView txtDeliveryDate = (TextView) order_item_row.findViewById(R.id.txtDeliveryDate);
                            LinearLayout deliveredOn = (LinearLayout) order_item_row.findViewById(R.id.deliveredOn);
                            if (AppConstant.isAndroid5()) {
                                imgCancel.setBackground(orderListActivity.getDrawable(R.drawable.ripple_accent));
                            }
                            JSONObject jObj = orderObject.getJSONObject("product");
                            txtItemName.setText(JSONData.getString(jObj, "name"));
                            int quantity = JSONData.getInt(orderObject, "qty");
                            JSONObject object = jObj.getJSONObject("main_image");
                            String imageUrl = JSONData.getString(object, "medium");
                            setImage(imageUrl, imgItem);
                            if (quantity >= 1) {
                                layoutQuantity.setVisibility(View.VISIBLE);
                                txtQuantity.setText(quantity + "");
                            } else {
                                layoutQuantity.setVisibility(View.GONE);
                            }
                            int status = JSONData.getInt(orderObject, "status");
                            if (status == 1) {
                                txtCurentStatus.setText("Success");
                                txtCurentStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                                swipeLayout.setSwipeEnabled(true);
                            } else if (status == 2) {
                                imgCancel.setClickable(false);
                                txtCurentStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelRed));
                                txtCurentStatus.setText("Cancelled");
                                swipeLayout.setSwipeEnabled(false);
                            } else if (status == 3) {
                                txtCurentStatus.setText("Delayed");
                                txtCurentStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                                swipeLayout.setSwipeEnabled(true);
                            } else if (status == 4) {
                                txtCurentStatus.setText("Dispatched");
                                txtCurentStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                                swipeLayout.setSwipeEnabled(true);
                            } else if (status == 5) {
                                txtCurentStatus.setText("In progress");
                                txtCurentStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                                swipeLayout.setSwipeEnabled(true);
                            } else if (status == 6) {
                                txtCurentStatus.setText("Approval");
                                txtCurentStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                                swipeLayout.setSwipeEnabled(true);
                            } else if (status == 7) {
                                txtCurentStatus.setText("Waiting for payment");
                                txtCurentStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                                swipeLayout.setSwipeEnabled(true);
                            } else if (status == 8) {
                                txtCurentStatus.setText("Return refund");
                                txtCurentStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                                swipeLayout.setSwipeEnabled(true);
                            } else if (status == 9) {
                                txtCurentStatus.setText("Location unfullfilable");
                                txtCurentStatus.setTextColor(orderListActivity.getResources().getColor(R.color.labelGreen));
                                swipeLayout.setSwipeEnabled(true);
                            }
                            if (orderStatus == 2) {
                                imgCancel.setClickable(false);
                            } else {
                                imgCancel.setClickable(true);
                            }
                            try {
                                long deliveryDate = Long.parseLong(JSONData.getString(orderObject, "delivery_date"));
                                txtDeliveryDate.setText(AppConstant.getDate(deliveryDate) + "");

                            } catch (Exception e) {
                                e.printStackTrace();
                                deliveredOn.setVisibility(View.GONE);

                            }


                            imgCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (AppConstant.isNetworkAvailable(orderListActivity)) {
                                        if (orderStatus != 2) {
                                            new CancelOrderProductTask(orderId, id, position).execute();
                                        }
                                    } else {
                                        AppConstant.showNetworkError(orderListActivity);
                                    }
                                }
                            });
                            vh.itemsLayout.addView(order_item_row);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    vh.rowLayout.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return myOrderList.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.txtOrderId)
        TypefacedTextView txtOrderId;
        @InjectView(R.id.txtDeliveryDate)
        TypefacedTextView txtDeliveryDate;
        @InjectView(R.id.txtTotalItem)
        TypefacedTextView txtTotalItem;
        @InjectView(R.id.txtDeliveryStatus)
        TypefacedTextView txtDeliveryStatus;
        @InjectView(R.id.itemsLayout)
        LinearLayout itemsLayout;
        @InjectView(R.id.txtTotalItem1)
        TypefacedTextView txtTotalItem1;
        @InjectView(R.id.txtTotalAmount)
        TypefacedTextView txtTotalAmount;
        @InjectView(R.id.txtTotalQty)
        TypefacedTextView txtTotalQty;
        @InjectView(R.id.txtPaymentStatus)
        TypefacedTextView txtPaymentStatus;
        @InjectView(R.id.btnTrack)
        TypedfacedButton btnTrack;
        @InjectView(R.id.btnCancel)
        TypedfacedButton btnCancel;
        @InjectView(R.id.linearlayoutBottom)
        LinearLayout linearlayoutBottom;
        @InjectView(R.id.rowLayout)
        LinearLayout rowLayout;
        @InjectView(R.id.cardViewTop)
        CardView cardViewTop;

        @SuppressLint("NewApi")
        public RecyclerViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
            btnTrack.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            if (AppConstant.isAndroid5()) {
                btnTrack.setBackground(orderListActivity.getDrawable(R.drawable.ripple_accent));
                btnCancel.setBackground(orderListActivity.getDrawable(R.drawable.ripple_accent));
                Typeface typeface = Typeface.createFromAsset(orderListActivity.getAssets(),
                        "fonts/AvenirNext-Bold.ttf");
                btnTrack.setTypeface(typeface);
                btnCancel.setTypeface(typeface);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnTrack:
                    Intent intent = new Intent(orderListActivity, TrackOrderActivity.class);
                    intent.putExtra("detail", myOrderList.get(getLayoutPosition()).toString());
                    orderListActivity.startActivity(intent);
                    orderListActivity.overridePendingTransition(R.anim.animation, R.anim.animation2);
                    break;
                case R.id.btnCancel:
                    if (AppConstant.isNetworkAvailable(orderListActivity)) {
                        JSONObject jsonObject = getItem(getLayoutPosition());
                        String id = JSONData.getString(jsonObject, "_id");
                        new CancelOrderTask(id, getLayoutPosition()).execute();

                    } else {
                        AppConstant.showNetworkError(orderListActivity);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    public JSONObject getItem(int position) {
        try {
            return new JSONObject(myOrderList.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setImage(String imageUrl, ImageView imageViewIcon) {
        if (!imageUrl.equals("")) {
            Glide.with(orderListActivity)
                    .load(Constants.api_ip + imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.icon_default_large)
                    .centerCrop()
                    .crossFade()
                    .into(imageViewIcon);
        }
    }


    public class CancelOrderTask extends AsyncTask<Void, Void, Void> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(orderListActivity);
        boolean flag;
        JSONObject jObj;
        int position;
        private int responseCode;
        private JSONArray jsonArray;
        private String id, message;

        public CancelOrderTask(String id, int position) {
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
                JSONParser jsonParser = new JSONParser(orderListActivity);
                JSONStringer jsonData = new JSONStringer().object()
                        .key("order_id").value(id).endObject();
                String[] data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_cancel_order, jsonData.toString());
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                    message = JSONData.getString(jObj, "message");
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
                        JSONObject jsonObject = jObj.getJSONObject("data");
                        myOrderList.set(position, jsonObject.toString());
                        AppConstant.showToastShort(orderListActivity, message);
                        notifyItemChanged(position);
                    } else {
                        AppConstant.showToastShort(orderListActivity, message);
                    }
                } else if (responseCode != 200) {
                    AppConstant.showSingleButtonAlertDialog(
                            orderListActivity, "Error",
                            "Something went wrong.\nPlease try again");
                } else {
                    AppConstant.showSingleButtonAlertDialog(orderListActivity, "Error", "No Data Found.");
                }
            } catch (Exception e) {

            }
        }

    }

    public class CancelOrderProductTask extends AsyncTask<Void, Void, Void> {

        private final CustomProgressDialog dialog = CustomProgressDialog.createProgressBar(orderListActivity);
        boolean flag;
        JSONObject jObj;
        int position;
        private int responseCode;
        private JSONArray jsonArray;
        private String OrderId, itemId, message;

        public CancelOrderProductTask(String OrderId, String itemId, int position) {
            this.OrderId = OrderId;
            this.itemId = itemId;
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
                JSONParser jsonParser = new JSONParser(orderListActivity);
                JSONStringer jsonData = new JSONStringer().object()
                        .key("order_id").value(OrderId).key("item_id").value(itemId).endObject();
                String[] data = jsonParser.sendPostReq(Constants.api_v1 + Constants.api_cancel_order_item, jsonData.toString());
                responseCode = Integer.valueOf(data[0]);
                if (responseCode == 200) {
                    jObj = new JSONObject(data[1]);
                    flag = jObj.has("flag") ? jObj.getBoolean("flag") : false;
                    message = JSONData.getString(jObj, "message");
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
                        JSONObject jsonObject = jObj.getJSONObject("data");
                        myOrderList.set(position, jsonObject.toString());
                        AppConstant.showToastShort(orderListActivity, message);
                        notifyItemChanged(position);
                    } else {
                        AppConstant.showToastShort(orderListActivity, message);
                    }
                } else if (responseCode != 200) {
                    AppConstant.showSingleButtonAlertDialog(
                            orderListActivity, "Error",
                            "Something went wrong.\nPlease try again");
                } else {
                    AppConstant.showSingleButtonAlertDialog(orderListActivity, "Error", "No Data Found.");
                }
            } catch (Exception e) {

            }
        }

    }


}
