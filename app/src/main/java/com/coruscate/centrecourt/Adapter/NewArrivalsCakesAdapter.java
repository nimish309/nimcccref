package com.coruscate.centrecourt.Adapter;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.coruscate.centrecourt.MainActivity;
import com.coruscate.centrecourt.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cis on 7/28/2015.
 */
public class NewArrivalsCakesAdapter extends RecyclerView.Adapter<NewArrivalsCakesAdapter.RecyclerViewHolder> {
    private ArrayList<String> newArrivalCakesList;
    private JSONObject item;
    private MainActivity context;

    public NewArrivalsCakesAdapter(ArrayList<String> newArrivalCakesList, MainActivity context) {
        this.newArrivalCakesList = newArrivalCakesList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.new_arrivals_cakes_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ImgRowImage,ImgSmallIcon,imgShowPopup;
        private TextView txtName;
        private CardView cardViewTop;

        public RecyclerViewHolder(View v) {
            super(v);
            ImgRowImage = (ImageView) v.findViewById(R.id.ImgRowImage);
            ImgSmallIcon = (ImageView) v.findViewById(R.id.ImgSmallIcon);
            imgShowPopup = (ImageView) v.findViewById(R.id.imgShowPopup);
            txtName = (TextView) v.findViewById(R.id.txtName);
            cardViewTop=(CardView)v.findViewById(R.id.cardViewTop);
            cardViewTop.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.cardViewTop) {
               /* Intent intent = new Intent(context, DetailInformationActivity.class);
                intent.putExtra("name", "Chocolate Hart");
                context.startActivity(intent);*/
            }
        }

    }
}
