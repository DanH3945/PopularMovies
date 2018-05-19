package com.hereticpurge.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailRecyclerViewAdapter extends RecyclerView.Adapter<DetailRecyclerViewAdapter.ViewHolder> {

    private final DetailActivity mDetailActivity;
    private ArrayList<String> mUrls = new ArrayList<>();
    private ArrayList<String> mTitles = new ArrayList<>();

    public DetailRecyclerViewAdapter(DetailActivity detailActivity) {
        this.mDetailActivity = detailActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_recycler_view_item, parent, false);
        return new DetailRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.textView.setText(mTitles.get(position));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDetailActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mUrls.get(position))));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public void setUrls(ArrayList<String> urls) {
        this.mUrls = urls;
    }

    public void setTitles(ArrayList<String> titles) {
        this.mTitles = titles;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.recycler_detail_trailer_text_view);

        }
    }
}
