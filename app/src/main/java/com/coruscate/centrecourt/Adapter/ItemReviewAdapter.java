package com.coruscate.centrecourt.Adapter;

import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Activity.ItemReviewActivity;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.JSONData;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by cis on 8/19/2015.
 */
public class ItemReviewAdapter extends RecyclerView.Adapter<ItemReviewAdapter.RecyclerViewHolder> {
    private ArrayList<String> reviewList;
    private ItemReviewActivity itemReviewActivity;

    public ItemReviewAdapter(ArrayList<String> reviewList, ItemReviewActivity itemReviewActivity) {
        this.reviewList = reviewList;
        this.itemReviewActivity = itemReviewActivity;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_review_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder vh, int position) {
        DrawableCompat.setTint(DrawableCompat.wrap(((LayerDrawable) vh.rattingBar.getProgressDrawable()).getDrawable(2)), Color.YELLOW);
        JSONObject item = getItem(position);
        if (item != null) {
            if (position == 0) {
                itemReviewActivity.product_id = JSONData.getString(item, "product_id");
            }
            JSONObject user = JSONData.getJSONObjectDefNull(item, "user");
            if (user != null) {
                vh.txtName.setText((JSONData.getString(user, "first_name") + " " + JSONData.getString(user, "last_name")).trim());
            } else {
                vh.txtName.setText("");
            }
            vh.rattingBar.setRating(Float.parseFloat(JSONData.getInt(item, "rating") + ""));
            vh.txtDays.setText(AppConstant.getTimeDiff(JSONData.getString(item, "created_at")));
            vh.txtDescription.setText(JSONData.getString(item, "review"));
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public JSONObject getItem(int position) {
        try {
            return new JSONObject(reviewList.get(position));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.txtName)
        TypefacedTextView txtName;
        @InjectView(R.id.rattingBar)
        RatingBar rattingBar;
        @InjectView(R.id.txtDays)
        TypefacedTextView txtDays;
        @InjectView(R.id.txtDescription)
        TypefacedTextView txtDescription;

        public RecyclerViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }

        @Override
        public void onClick(View view) {

        }

    }


}
