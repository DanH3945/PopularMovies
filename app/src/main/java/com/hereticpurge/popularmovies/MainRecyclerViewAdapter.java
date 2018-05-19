package com.hereticpurge.popularmovies;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mUrlList;
    private ArrayList<String> mIdList;
    private final MainActivity mMainActivity;

    // constants to put into an intent to start the detail view.
    private static final String MOVIE_ID_TAG = "movieId";
    private static final String POSTER_PATH_TAG = "posterPath";

    // constructor for the adapter.  Takes a MainActivity instead of a Context so picasso and an intent can use it
    // as a reference
    public MainRecyclerViewAdapter(ArrayList<String> urlList, ArrayList<String> idList, MainActivity mainActivity) {
        this.mIdList = idList;
        this.mUrlList = urlList;
        this.mMainActivity = mainActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    // changed the normal int position to be final so the click listener can use the position.
    // It hasn't broken anything.... yet....
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        // Picasso implementation loads the image into the posterView inside each view holder.
        Picasso.with(mMainActivity)
                .load(mUrlList.get(position))
                .into(holder.posterView);

        // click listener is bound to each image in the recycler view.  when a user clicks an image
        // the detail view for that movie is started.
        holder.posterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mMainActivity, DetailActivity.class);
                intent.putExtra(MOVIE_ID_TAG, mIdList.get(position));
                intent.putExtra(POSTER_PATH_TAG, mUrlList.get(position));
                mMainActivity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUrlList.size();
    }

    // enables lists to be updated from outside the class for sorting etc
    public void updateLists(ArrayList<String> ids, ArrayList<String> urls) {
        this.mIdList = ids;
        this.mUrlList = urls;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView posterView;

        ViewHolder(View itemView) {
            super(itemView);
            this.posterView = itemView.findViewById(R.id.recycler_main_image_view);
        }
    }
}
