package com.coruscate.centrecourt.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.coruscate.centrecourt.CustomControls.TypefacedCheckBox;
import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.Model.MiscellaneousData;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Activity.MiscellaneousActivity;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by cis on 7/27/2015.
 */
public class MiscellaneousAdapter extends RecyclerView.Adapter<MiscellaneousAdapter.RecyclerViewHolder> {
    CardView.LayoutParams layoutParams = new CardView.LayoutParams(
            CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT);
    int px;
    private ArrayList<MiscellaneousData> dataList;

    private MiscellaneousActivity context;
    private String imageUrl;
    private JSONObject jsonObject, jobj;
    private int quntity, maxQty, minQty;
    private double pricePerPiece;

    public MiscellaneousAdapter(ArrayList<MiscellaneousData> miscellaneousList, MiscellaneousActivity context) {
        this.dataList = miscellaneousList;
        this.context = context;
        px = AppConstant.dpToPx(8, context);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.miscellaneous_detail_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder vh, int position) {

        if (position == dataList.size() - 1) {
            layoutParams.setMargins(px, px, px, px);
        } else {
            layoutParams.setMargins(px, px, px, 0);
        }
        vh.cardView.setLayoutParams(layoutParams);

        JSONObject item = getItem(position);

        if (item != null) {
            try {
                final String name = item.has("name") ? (item.isNull("name") ? "" : item.getString("name")) : "";
                // final String min_quantity = item.has("min_ordered_quantity") ? (item.isNull("min_ordered_quantity") ? "1" : item.getString("min_ordered_quantity")) : "1";
                final String min_quantity = dataList.get(position).getQty() + "";
                if (!name.equals("")) {
                    vh.txtName.setText(name);
                } else {
                    vh.txtName.setVisibility(View.GONE);
                }
                JSONObject jsonObject;

                if (!min_quantity.equals("")) {
                    vh.txtTotal.setText(min_quantity);
                } else {
                    vh.txtTotal.setText("0");
                }
                try {
                    JSONObject jObj = item.getJSONObject("price_per_piece");
                    JSONData.getJSONObject(item, "price_per_piece");
                    pricePerPiece = JSONData.getDouble(jObj, "price");
                    if (pricePerPiece > 0) {
                        vh.txtPricePer.setVisibility(View.VISIBLE);
                        vh.txtPricePer.setText(AppConstant.rupee_symbol + pricePerPiece + "");
                    } else {
                        vh.txtPricePer.setVisibility(View.GONE);
                    }
                    if (dataList.get(position).isSelected()) {
                        quntity = getSelectedQty(JSONData.getString(item, "_id"));
                        vh.txtTotal.setText(quntity + "");
                    } else {
                        quntity = Integer.parseInt(vh.txtTotal.getText().toString());
                    }
                    if (pricePerPiece > 0) {
                        vh.txtRupeesSymbol.setVisibility(View.VISIBLE);
                        vh.txtPrice.setVisibility(View.VISIBLE);
                        vh.txtPrice.setText((pricePerPiece * quntity) + "");
                    } else {
                        vh.txtRupeesSymbol.setVisibility(View.GONE);
                        vh.txtPrice.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
               /* try {
                    JSONObject jObj = item.getJSONObject("price_per_kg");
                    pricePerPiece = Double.parseDouble(jObj.has("price") ? (jObj.isNull("price") ? "" : jObj.getString("price")) : "");
                    quntity = Integer.parseInt(vh.txtTotal.getText().toString());
                    vh.txtPrice.setText((pricePerPiece * quntity) + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
                vh.txtRupeesSymbol.setText(AppConstant.rupee_symbol);
                jsonObject = item.getJSONObject("main_image");
                imageUrl = jsonObject.has("medium") ? (jsonObject.isNull("medium") ? "" : jsonObject.getString("medium")) : "";
                setImage(imageUrl, vh.ImgRowImage);

                if (dataList.get(position).isSelected())
                    vh.checkItem.setChecked(true);
                else
                    vh.checkItem.setChecked(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public JSONObject getItem(int position) {
        try {
            return new JSONObject(dataList.get(position).getData());
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

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.ImgRowImage)
        ImageView ImgRowImage;
        @InjectView(R.id.txtName)
        TypefacedTextView txtName;
        @InjectView(R.id.txtPricePer)
        TypefacedTextView txtPricePer;
        @InjectView(R.id.imgDecrease)
        ImageView imgDecrease;
        @InjectView(R.id.txtTotal)
        TypefacedTextView txtTotal;
        @InjectView(R.id.imgIncrease)
        ImageView imgIncrease;
        @InjectView(R.id.progressBarRow)
        ProgressBar progressBarRow;
        @InjectView(R.id.txtRupeesSymbol)
        TypefacedTextView txtRupeesSymbol;
        @InjectView(R.id.txtPrice)
        TypefacedTextView txtPrice;
        @InjectView(R.id.checkItem)
        TypefacedCheckBox checkItem;
        @InjectView(R.id.relativeLayoutrow)
        RelativeLayout relativeLayoutrow;
        @InjectView(R.id.cardView)
        CardView cardView;
        private ArrayList<String> accessoriesList;
        private JSONObject object;
        private String id;
        private int qty;
        private double price;

        public RecyclerViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
            imgIncrease.setOnClickListener(this);
            imgDecrease.setOnClickListener(this);
            checkItem.setOnClickListener(this);
            AppConstant.scaleAnimationOfView(v, context);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.imgIncrease:
                    setPrice("increase", v);
                    break;
                case R.id.imgDecrease:
                    setPrice("decrease", v);
                    break;
                case R.id.checkItem:
                    jsonObject = getItem(getLayoutPosition());
                    accessoriesList = UserDataPreferences.getAccessoriesItemList(context);
                    id = JSONData.getString(jsonObject, "_id").toString();
                    if (checkItem.isChecked()) {
                        dataList.get(getAdapterPosition()).setIsSelected(true);
                        object = new JSONObject();
                        try {
                            object.put("_id", id);
                            object.put("products", jsonObject);
                            object.put("qty", Integer.parseInt(txtTotal.getText().toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (accessoriesList == null) {
                            accessoriesList = new ArrayList<>();
                            accessoriesList.add(object.toString());
                            UserDataPreferences.saveAccessoriesItemList(context, accessoriesList);
                        } else {
                            if (accessoriesList.size() == 0) {
                                accessoriesList.add(object.toString());
                                UserDataPreferences.saveAccessoriesItemList(context, accessoriesList);
                            } else {
                                for (int i = 0; i < accessoriesList.size(); i++) {
                                    try {
                                        jobj = new JSONObject(accessoriesList.get(i));
                                        if (id.toString().equals(JSONData.getString(jobj, "_id"))) {
                                            accessoriesList.set(i, object.toString());
                                            UserDataPreferences.saveAccessoriesItemList(context, accessoriesList);
                                            break;
                                        } else {
                                            if (i == accessoriesList.size() - 1) {
                                                accessoriesList.add(object.toString());
                                                UserDataPreferences.saveAccessoriesItemList(context, accessoriesList);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else {
                        dataList.get(getAdapterPosition()).setIsSelected(false);
                        if (accessoriesList != null) {
                            for (int i = 0; i < accessoriesList.size(); i++) {
                                try {
                                    jobj = new JSONObject(accessoriesList.get(i));
                                    if (id.toString().equals(JSONData.getString(jobj, "_id"))) {
                                        accessoriesList.remove(i);
                                        UserDataPreferences.saveAccessoriesItemList(context, accessoriesList);
                                        break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    // notifyDataSetChanged();
                    Log.d("RecyclerViewHolder", accessoriesList.size() + "");
                    break;
            }
        }

        private void setTotalQtyPrice() {
            ArrayList arrayList = UserDataPreferences.getAccessoriesItemList(context);
            if (arrayList != null) {
                context.txtTotalPrice.setVisibility(View.VISIBLE);
                context.txtSubTotal.setVisibility(View.VISIBLE);

            } else {
                context.txtTotalPrice.setVisibility(View.GONE);
                context.txtSubTotal.setVisibility(View.GONE);
            }

        }


        private void setPrice(String increaseOrDecrease, View v) {
            jsonObject = getItem(getLayoutPosition());
            try {
                if (increaseOrDecrease.toString().equals("increase")) {
                    quntity = (Integer.parseInt(txtTotal.getText().toString()) + 1);
                  } else if (increaseOrDecrease.toString().equals("decrease")) {
                    quntity = (Integer.parseInt(txtTotal.getText().toString()) - 1);
                }
                maxQty = jsonObject.has("max_ordered_quantity") ? (jsonObject.isNull("max_ordered_quantity") ? 10 : jsonObject.getInt("max_ordered_quantity")) : 10;
                minQty = jsonObject.has("min_ordered_quantity") ? (jsonObject.isNull("min_ordered_quantity") ? 1 : jsonObject.getInt("min_ordered_quantity")) : 1;

                if (quntity >= minQty && quntity <= maxQty) {
                    txtTotal.setText(quntity + "");
                    dataList.get(getLayoutPosition()).setQty(quntity);
                    try {
                        JSONObject jObj = jsonObject.getJSONObject("price_per_piece");
                        pricePerPiece = Double.parseDouble(jObj.has("price") ? (jObj.isNull("price") ? "" : jObj.getString("price")) : "");
                        txtPrice.setText((pricePerPiece * quntity) + "");
                    } catch (Exception e) {
                    }
                } else {
                    AppConstant.displayErroMessage(v, "Quntity must between " + minQty + " to " + maxQty, context);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (checkItem.isChecked()) {
                Log.d("RecyclerViewHolder", "true");
                accessoriesList = UserDataPreferences.getAccessoriesItemList(context);
                object = new JSONObject();
                id = JSONData.getString(jsonObject, "_id").toString();
                try {
                    object.put("_id", id);
                    object.put("products", jsonObject);
                    object.put("qty", Integer.parseInt(txtTotal.getText().toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (accessoriesList == null) {
                    accessoriesList = new ArrayList<>();
                    accessoriesList.add(object.toString());
                    UserDataPreferences.saveAccessoriesItemList(context, accessoriesList);
                } else {
                    for (int i = 0; i < accessoriesList.size(); i++) {
                        try {
                            jobj = new JSONObject(accessoriesList.get(i));
                            Log.d("RecyclerViewHolder", id);
                            Log.d("RecyclerViewHolder", JSONData.getString(jobj, "_id"));
                            if (id.toString().equals(JSONData.getString(jobj, "_id"))) {
                                accessoriesList.set(i, object.toString());
                                UserDataPreferences.saveAccessoriesItemList(context, accessoriesList);
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private int getSelectedQty(String id) {
        try {
            JSONObject jsonObject;
            ArrayList<String> accessoriesList = UserDataPreferences.getAccessoriesItemList(context);
            for (int i = 0; i < accessoriesList.size(); i++) {
                jsonObject = new JSONObject(accessoriesList.get(i));
                if (id.equals(JSONData.getString(jsonObject, "_id"))) {
                    return JSONData.getInt(jsonObject, "qty");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}


