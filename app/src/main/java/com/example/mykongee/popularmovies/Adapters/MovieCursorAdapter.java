package com.example.mykongee.popularmovies.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.mykongee.popularmovies.R;
import com.example.mykongee.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Mykongee on 9/7/15.
 */
public class MovieCursorAdapter extends CursorAdapter {

    private String LOG_TAG = MovieCursorAdapter.class.getSimpleName();

    public MovieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.list_item_movie, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int posterPathIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        String posterPath = cursor.getString(posterPathIndex);
        Log.v(LOG_TAG, "Got posterPath");

        ImageView moviePoster = (ImageView) view;
        Picasso.with(context).load("http://image.tmdb.org/t/p/w342/" + posterPath)
                .into(moviePoster);
    }
}
