package com.coruscate.centrecourt.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;
import com.coruscate.centrecourt.UserInterface.Fragments.Dashboard.DashBoardCakesFragment;
import com.coruscate.centrecourt.UserInterface.Fragments.DetailFragment;
import com.coruscate.centrecourt.Util.AppConstant;
import com.coruscate.centrecourt.Util.Constants;
import com.coruscate.centrecourt.Util.UserDataPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cis on 7/30/2015.
 */
public class DashboardCakesAdapter extends RecyclerView.Adapter<DashboardCakesAdapter.RecyclerViewHolder> {
    private ArrayList<String> dashboardCakesList;
    private JSONObject item;
    private MainActivity context;
    private String imageUrl;
    private String id;
    DashBoardCakesFragment dashBoardFrag;
    int px8,px4;
    CardView.LayoutParams layoutParams = new CardView.LayoutParams(
            CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
    public DashboardCakesAdapter(ArrayList<String> dashboardCakesList, MainActivity context,DashBoardCakesFragment dashBoardFrag) {
        this.dashboardCakesList = dashboardCakesList;
        this.context = context;
        this.dashBoardFrag=dashBoardFrag;
        px8 = AppConstant.dpToPx(8, context);
        px4 = AppConstant.dpToPx(4, context);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.dashboard_cakes_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder vh, int position) {
        if(position==dashboardCakesList.size()-1) {
            layoutParams.setMargins(px8, px8, px8, px8);
            vh.cardView.setLayoutParams(layoutParams);
        }
        item = getItem(position);
        if (item != null) {
            try {
                vh.txtName.setText(item.has("title") ? (item.isNull("title") ? "" : item.getString("title")) : "");
                imageUrl = item.has("image") ? (item.isNull("image") ? "" : item.getString("image")) : "";
                setImage(imageUrl, vh.ImgRowImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return dashboardCakesList.size();
    }

    private void makeFragmentVisible(String id) {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        Fragment fragment=new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        fragment.setArguments(bundle);
        UserDataPreferences.saveLastFrag(context, DashBoardCakesFragment.TAG);
        FragmentTransaction ft=fragmentManager.beginTransaction();
        ft.add(R.id.nav_contentframe, fragment);
        ft.hide(dashBoardFrag);
        ft.addToBackStack(DetailFragment.TAG);
        ft.commit();
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

    public JSONObject getItem(int position) {
        try {
            return new JSONObject(dashboardCakesList.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ImgRowImage;
        TextView txtName;
        CardView cardView;

        public RecyclerViewHolder(View v) {
            super(v);
            ImgRowImage = (ImageView) v.findViewById(R.id.ImgRowImage);
            txtName = (TextView) v.findViewById(R.id.txtName);
            cardView=(CardView)v.findViewById(R.id.cardViewDashboardListRow);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            item = getItem(getLayoutPosition());
            if (item != null) {
                try {
                    id = item.has("_id") ? (item.isNull("_id") ? "" : item.getString("_id")) : "";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (id != null && id.length() > 0) {
                makeFragmentVisible(id);
            }
        }

    }
}
