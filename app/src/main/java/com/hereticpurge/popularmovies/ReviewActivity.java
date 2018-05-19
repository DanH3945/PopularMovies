package com.hereticpurge.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hereticpurge.popularmovies.model.Movie;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    private static final String MOVIE_OBJECT_TAG = "movieObject";
    private Movie mMovie = null;
    private ArrayList<String> reviewList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reviews);

        Bundle extras = getIntent().getExtras();

        if (extras.containsKey(MOVIE_OBJECT_TAG)) {
            mMovie = (Movie) extras.get(MOVIE_OBJECT_TAG);
        }

        if (mMovie != null) {
            reviewList = new ArrayList<>();
            ArrayList<String> reviewAuthor = new ArrayList<>();
            ArrayList<String> reviewBody = new ArrayList<>();

            reviewAuthor.addAll(mMovie.getReviews().keySet());
            reviewBody.addAll(mMovie.getReviews().values());

            if (reviewAuthor.size() == reviewBody.size()) {
                for (int i = 0; i < reviewAuthor.size(); i++) {
                    reviewList.add("Author: " +
                            reviewAuthor.get(i) +
                            "\n\n" +
                            reviewBody.get(i));
                }
            }
        }

        ListView listView = findViewById(R.id.detail_review_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.activity_detail_reviews_list_item,
                reviewList);

        listView.setAdapter(adapter);

    }
}
