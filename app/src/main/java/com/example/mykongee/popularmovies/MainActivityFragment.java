package com.example.mykongee.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    MovieAdapter movieAdapter;
    GridView gridView;
    ArrayList<Movie> movieArrayList;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")){
            movieArrayList = new ArrayList<Movie>();
        } else {
            movieArrayList = savedInstanceState.getParcelableArrayList("movies");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList("movies", movieArrayList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container);

        // Create the GridView object from the xml layout.
        // NOTE: Since rootView is the xml layout file containing the xml for gridview,
        // rootView.findViewById is used. NOT getActivity()

        //final ArrayList<Movie> movieArrayList = new ArrayList<Movie>();

        movieAdapter = new MovieAdapter(
                getActivity(), //Context in which we want to place the adapter into
                R.layout.list_item_movie, //The format of the views inside the GridView
                R.id.list_item_movie_imageview, //ID of the imageview to populate
                movieArrayList
                // /new ArrayList<Movie>() //the data source we want to populate the ListView with
        );

        gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) gridView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("TITLE", movie.getTitle());
                extras.putString("POSTER", movie.getPosterPath());
                extras.putString("OVERVIEW", movie.getOverview());
                extras.putString("RELEASE_DATE", movie.getReleaseDate());
                extras.putString("RATING", movie.getRating().toString());
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void updateMovies() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getActivity());
        String sortPref = sharedPreferences.getString(getString(R.string.sort_by_key),
                getString(R.string.default_sort_by_value));
        // doInBackground takes in a String[]
        String[] prefs = {sortPref};
        Log.v("POPULARMOVIES", "made a prefs String[]");
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute(prefs);
        Log.v("POPULARMOVIES", "updated movies");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("POPULARMOVIES", "onStart()");
        updateMovies();
    }

}
