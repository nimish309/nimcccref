package com.coruscate.centrecourt.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.coruscate.centrecourt.CustomControls.TypefacedTextView;
import com.coruscate.centrecourt.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by cis on 9/12/2015.
 */
public class NewsLetterAdapter extends RecyclerView.Adapter<NewsLetterAdapter.RecyclerViewHolder> {

    private ArrayList<String> newsLetter;
    private Context context;

    public NewsLetterAdapter(ArrayList<String> newsLetter, Context context) {
        this.newsLetter = newsLetter;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.news_letter_list_row, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder vh, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;//newsLetter.size();
    }



class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public RecyclerViewHolder(View v) {
        super(v);
    }

    @Override
    public void onClick(View v) {

    }

}
}
