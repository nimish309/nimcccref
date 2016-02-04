package com.coruscate.centrecourt.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by User on 8/6/2015.
 */

public class OrderFragAdapter extends RecyclerView.Adapter<OrderFragAdapter.RecyclerViewHolder>{
    private MainActivity context;
    public OrderFragAdapter(ArrayList<String> orderList, MainActivity context) {
        this.context=context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.wishlist_row, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ImgRowImage;
        TextView txtName;

        public RecyclerViewHolder(View v) {
            super(v);
            ImgRowImage = (ImageView) v.findViewById(R.id.ImgRowImage);
            txtName = (TextView) v.findViewById(R.id.txtName);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
