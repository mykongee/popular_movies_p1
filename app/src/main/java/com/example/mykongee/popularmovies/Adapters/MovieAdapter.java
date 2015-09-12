package com.example.mykongee.popularmovies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.mykongee.popularmovies.Models.Movie;
import com.example.mykongee.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<Movie> {

    private Context context;
    private int resource;
    private ArrayList<Movie> movieList;

    public MovieAdapter(Context c, int layout, int r, ArrayList<Movie> data) {
        super(c, r, data);
        movieList = data;
        context = c;
        resource = r;

    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.list_item_movie, parent, false);
        }

        ImageView movieView = (ImageView) convertView.findViewById(R.id.list_item_movie_imageview);
        movieView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w342/" + movie.getPosterPath())
                .into(movieView);

        return convertView;
    }

}