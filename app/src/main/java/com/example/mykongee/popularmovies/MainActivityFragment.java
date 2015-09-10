package com.example.mykongee.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.mykongee.popularmovies.data.MovieContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // MovieAdapter movieAdapter;
    final int MOVIE_LOADER_ID = 0;
    private MovieCursorAdapter movieAdapter;
    GridView gridView;
    ArrayList<Movie> movieArrayList;
    private String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private CursorLoader cursorLoader;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POPULARITY
    };


    // These are tied to MOVIE_COLUMNS, if that is changed, these integers must change
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_OVERVIEW = 2;
    static final int COL_MOVIE_POSTER = 3;
    static final int COL_MOVIE_RATING = 4;
    static final int COL_MOVIE_RELEASE = 5;
    static final int COL_MOVIE_TITLE = 6;
    static final int COL_MOVIE_POPULARITY = 7;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")){
            movieArrayList = new ArrayList<Movie>();
        } else {
            movieArrayList = savedInstanceState.getParcelableArrayList("movies");
        }*/
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
        // rootView.findViewById is used. NOT getActivity() method
        gridView = (GridView) rootView.findViewById(R.id.gridview);

        // Get a cursor from the ContentResolver with movie information

        movieAdapter = new MovieCursorAdapter(getActivity(), null, 0);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position or getItem(), or null
                // if it cannot seek to that position
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Bundle extras = new Bundle();
                    extras.putString("OVERVIEW", cursor.getString(2));
                    extras.putString("POSTER_PATH", cursor.getString(3));
                    extras.putString("RATING", cursor.getString(4));
                    extras.putString("RELEASE_DATE", cursor.getString(5));
                    extras.putString("TITLE", cursor.getString(6));
                    extras.putDouble("POPULARITY", cursor.getDouble(7));
                    Log.v(LOG_TAG, "" + cursor.getDouble(7));

                    Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    void onSortOrderChange() {
        updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    public void updateMovies() {
        String sortPref = Utility.getPreferredSortOrder(getActivity());
        Log.v(LOG_TAG, "updateMovies() sortPref = " + sortPref);
        // doInBackground takes in a String[]
        String[] prefs = {sortPref};
        FetchMovieTask fetchMovieTask = new FetchMovieTask(getActivity());
        fetchMovieTask.execute(prefs);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Initialize the appropriate loader(s)
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // TODO Create onChangeSortOrder to reload the cursor with a different sort order based
    // on preference

    @Override
    public void onStart() {
        super.onStart();
        Log.v("POPULARMOVIES", "onStart()");
        updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case MOVIE_LOADER_ID:
                // String sortOrder = Utility.getPreferredSortOrder(getActivity());
                String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY;
                cursorLoader = new CursorLoader(getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        sortOrder);
        }
        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "row count: " + data.getCount());
        movieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}
