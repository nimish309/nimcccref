package com.coruscate.centrecourt.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.coruscate.centrecourt.AsynkTask.AddToWishListTask;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Activity.ItemDetailActivity;
import com.coruscate.centrecourt.UserInterface.Fragments.DetailFragment;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.JSONData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cis on 7/27/2015.
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.RecyclerViewHolder> {

    CardView.LayoutParams layoutParamsGrid = new CardView.LayoutParams(
            CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT);
    CardView.LayoutParams layoutParams = new CardView.LayoutParams(
            CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
    int px8, px4;
    private ArrayList<String> detailList;
    private JSONObject item;
    private MainActivity context;
    private String imageUrl;
    private DetailFragment detailFragment;

    public DetailAdapter(ArrayList<String> detailList, MainActivity context, DetailFragment detailFragment) {
        this.detailList = detailList;
        this.context = context;
        this.detailFragment = detailFragment;
        px8 = AppConstant.dpToPx(8, context);
        px4 = AppConstant.dpToPx(4, context);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.detail_list_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder vh, int position) {


        if (detailFragment.layoutMode == 0) {

            vh.ImgDetailLeft.setVisibility(View.GONE);
            vh.ImgDetailRowImage.setVisibility(View.VISIBLE);
            if(!AppConstant.isAndroid5()){
                vh.ImgDetailRowImage.setPadding(px4,px4,px4,px4);
            }

            if (position > (getItemCount() - (getItemCount() % 2 == 0 ? 2 : getItemCount() % 2) - 1)) {
                if (position % 2 == 0) {
                    layoutParamsGrid.setMargins(px8, px8, px8, px8);
                } else {
                    layoutParamsGrid.setMargins(0, px8, px8, px8);
                }
            } else {
                if (position % 2 == 0) {
                    layoutParamsGrid.setMargins(px8, px8, px8, 0);
                } else {
                    layoutParamsGrid.setMargins(0, px8, px8, 0);
                }
            }
            vh.cardViewDetailListRow.setLayoutParams(layoutParamsGrid);


        } else {
            vh.ImgDetailLeft.setVisibility(View.VISIBLE);
            vh.ImgDetailRowImage.setVisibility(View.GONE);

            if (position == detailList.size() - 1) {
                layoutParams.setMargins(px8, px8, px8, px8);
            } else {
                layoutParams.setMargins(px8, px8, px8, 0);
            }
            vh.cardViewDetailListRow.setLayoutParams(layoutParams);
        }


        DrawableCompat.setTint(DrawableCompat.wrap(((LayerDrawable) vh.rattingBar.getProgressDrawable()).getDrawable(2)), Color.YELLOW);
        if (detailList.size() > 0) {
            item = getItem(position);

            if (null != item) {
                try {
                    final String name = item.has("name") ? (item.isNull("name") ? "" : item.getString("name")) : "";
                    final String desc = item.has("desc") ? (item.isNull("desc") ? "" : item.getString("desc")) : "";
                    if (JSONData.getBoolean(item, "eggless")) {
                        vh.imgIconEggLess.setVisibility(View.VISIBLE);
                    } else {
                        vh.imgIconEggLess.setVisibility(View.GONE);
                    }
                    if (JSONData.getBoolean(item, "eggwith")) {
                        vh.imgIconEgg.setVisibility(View.VISIBLE);
                    } else {
                        vh.imgIconEgg.setVisibility(View.GONE);
                    }
                    if (!name.equals("")) {
                        vh.txtName.setText(name);
                    } else {
                        vh.txtName.setVisibility(View.GONE);
                    }
                    if (!desc.equals("")) {
                        vh.txtNewSession.setText(desc);
                    } else {
                        vh.txtNewSession.setVisibility(View.GONE);
                    }

                    JSONObject jsonObject;

                    jsonObject = JSONData.getJSONObjectDefNull(item, "ratings");
                    if (null != jsonObject) {
                        vh.rattingBar.setRating(Float.parseFloat(String.valueOf(JSONData.getDouble(jsonObject, "average"))));
                    } else {
                        vh.rattingBar.setRating(0);
                    }
                    try {
                        JSONObject object = item.getJSONObject("minimum_price");
                        double minPrice = JSONData.getDouble(object, "base_price");
                        double minWeight = JSONData.getDouble(object, "base_weight");
                        try {
                            JSONArray jsonArray = item.getJSONArray("price_per_kg");
                            if (jsonArray != null && jsonArray.length() > 0) {
//                                if (minWeight == 1)
                                vh.txtPrice.setText(AppConstant.rupee_symbol + (int) minPrice + " ( " + minWeight + " KG )");
//                                else if (minWeight < 1) {
//                                    vh.txtPrice.setText(AppConstant.rupee_symbol + (int) minPrice + " ( " +  (minWeight) + "KG )");
//                                } else {
//                                    vh.txtPrice.setText(AppConstant.rupee_symbol + (int) minPrice + " ( " + minWeight + "KG )");
//                                }
                            }
                        } catch (Exception e) {
                        }
                        try {
                            jsonObject = item.getJSONObject("price_per_piece");
                            minWeight = JSONData.getDouble(jsonObject, "weight");
                            if (jsonObject != null) {
                                if (minWeight <= 1) {
                                    vh.txtPrice.setText(AppConstant.rupee_symbol + (int) minPrice);
                                } else {
                                    vh.txtPrice.setText(AppConstant.rupee_symbol + (int) minPrice + "/" + minWeight + "pcs");
                                }

                            }

                        } catch (Exception e) {
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    jsonObject = item.getJSONObject("main_image");
                    imageUrl = jsonObject.has("medium") ? (jsonObject.isNull("medium") ? "" : jsonObject.getString("medium")) : "";
                    boolean isInWishList = item.has("is_in_wishlist") ? item.getBoolean("is_in_wishlist") : false;
                    if (isInWishList) {
                        vh.ImgWishList.setImageResource(R.drawable.icon_heart_2);
                    } else {
                        vh.ImgWishList.setImageResource(R.drawable.icon_heart_1);
                    }
                    setImage(imageUrl, vh.ImgDetailRowImage);
                    setImage(imageUrl, vh.ImgDetailLeft);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return detailList.size();
    }

    public JSONObject getItem(int position) {
        try {
            return new JSONObject(detailList.get(position));
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
        ImageView ImgDetailRowImage, ImgWishList, ImgDetailLeft, imgIconEggLess, imgIconEgg;
        TextView txtName, txtNewSession, txtPrice;
        private RatingBar rattingBar;
        private CardView cardViewDetailListRow;

        public RecyclerViewHolder(View v) {
            super(v);
            ImgDetailRowImage = (ImageView) v.findViewById(R.id.ImgDetailRowImage);
            ImgDetailLeft = (ImageView) v.findViewById(R.id.ImgDetailLeft);
            ImgWishList = (ImageView) v.findViewById(R.id.ImgWishList);
            imgIconEggLess = (ImageView) v.findViewById(R.id.imgIconEggLess);
            imgIconEgg = (ImageView) v.findViewById(R.id.imgIconEgg);
            txtName = (TextView) v.findViewById(R.id.txtName);
            txtNewSession = (TextView) v.findViewById(R.id.txtNewSession);
            rattingBar = (RatingBar) v.findViewById(R.id.rattingBar);
            txtPrice = (TextView) v.findViewById(R.id.txtPrice);
            cardViewDetailListRow = (CardView) v.findViewById(R.id.cardViewDetailListRow);
            cardViewDetailListRow.setOnClickListener(this);
            ImgWishList.setOnClickListener(this);
            AppConstant.scaleAnimationOfView(v, context);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cardViewDetailListRow) {

                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra("position", getLayoutPosition());
                intent.putExtra("type", "ItemDetail");
                intent.putExtra("detail", detailList.get(getLayoutPosition()).toString());
                context.startActivityForResult(intent, 8);
                context.overridePendingTransition(R.anim.animation, R.anim.animation2);
            } else if (v.getId() == R.id.ImgWishList) {
                if (AppConstant.isNetworkAvailable(context)) {
                    String id;
                    boolean isInWishList;
                    if (detailList.size() > 0) {
                        item = getItem(getLayoutPosition());
                        if (null != item) {
                            try {
                                id = item.has("_id") ? (item.isNull("_id") ? "" : item.getString("_id")) : "";
                                isInWishList = JSONData.getBoolean(item, "is_in_wishlist");
                                ImgWishList.setClickable(false);
                                if (!isInWishList) {
                                    ImgWishList.setImageResource(R.drawable.icon_heart_2);
                                } else {
                                    ImgWishList.setImageResource(R.drawable.icon_heart_1);
                                }
                                AppConstant.scaleAnimation(ImgWishList, context);
                                new AddToWishListTask(ImgWishList, id, context, isInWishList, "itemDetail", getLayoutPosition(), null).execute();

                            } catch (Exception e) {

                            }
                        }
                    }
                } else {
                    AppConstant.showNetworkError(context);
                }
            }
        }

    }

}
