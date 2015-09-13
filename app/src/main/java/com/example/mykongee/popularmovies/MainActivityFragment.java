package com.example.mykongee.popularmovies;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.example.mykongee.popularmovies.Adapters.MovieAdapter;
import com.example.mykongee.popularmovies.Adapters.MovieCursorAdapter;
import com.example.mykongee.popularmovies.Models.Movie;
import com.example.mykongee.popularmovies.Models.Review;
import com.example.mykongee.popularmovies.Models.Trailer;
import com.example.mykongee.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final static int MOVIE_LOADER_ID = 0;
    private MovieAdapter movieAdapter;
    private MovieCursorAdapter movieCursorAdapter;

    GridView gridView;
    ArrayList<Movie> movieArrayList;
    private CursorLoader cursorLoader;
    public String mSortOrder;

    private String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_TITLE,
    };


    // These are tied to MOVIE_COLUMNS, if that is changed, these integers must change
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_OVERVIEW = 2;
    static final int COL_MOVIE_POSTER = 3;
    static final int COL_MOVIE_RATING = 4;
    static final int COL_MOVIE_RELEASE = 5;
    static final int COL_MOVIE_TITLE = 6;

    // For switch case statements

    public MainActivityFragment() {
    }

    public interface Callback {
        // Two onItemSelected methods to handle clicks from a MovieCursorAdapter and MovieAdapter

        void onItemSelected(Movie movie);

        void onItemSelected(Bundle movieInfo);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
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

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Create the GridView object from the xml layout.
        // NOTE: Since rootView is the xml layout file containing the xml for gridview,
        // rootView.findViewById is used. NOT getActivity() method
        gridView = (GridView) rootView.findViewById(R.id.gridview);

        // Get a cursor from the ContentResolver with movie information

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

//                // Notify callback instead of launching a new activity through intent
//                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
//                Bundle extras = new Bundle();
//                // Pass a Movie object in the intent
//
//                extras.putParcelable("MOVIE", movie);
//                intent.putExtras(extras);
//                startActivity(intent);
                ((Callback) getActivity()).onItemSelected(movie);

            }
        });

        return rootView;
    }

    public void updateMovies() {
        mSortOrder = Utility.getPreferredSortOrder(getActivity());

        // Switch statement to handle different sort preferences
        switch (mSortOrder) {
            case "popularity.desc": {
                getLoaderManager().destroyLoader(MOVIE_LOADER_ID);
                String[] prefs = {mSortOrder};
                movieAdapter = new MovieAdapter(
                        getActivity(), //Context in which we want to place the adapter into
                        R.layout.list_item_movie, //The format of the views inside the GridView
                        R.id.list_item_movie_imageview, //ID of the imageview to populate
                        movieArrayList
                        // /new ArrayList<Movie>() //the data source we want to populate the ListView with
                );
                gridView.setAdapter(movieAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Movie movie = (Movie) gridView.getItemAtPosition(position);
                        ((Callback) getActivity()).onItemSelected(movie);

                    }
                });

                FetchMovieTask fetchMovieTask = new FetchMovieTask();
                fetchMovieTask.execute(prefs);
                break;
            }
            case "vote_average.desc": {
                getLoaderManager().destroyLoader(MOVIE_LOADER_ID);
                String[] prefs = {mSortOrder};
                movieAdapter = new MovieAdapter(
                        getActivity(), //Context in which we want to place the adapter into
                        R.layout.list_item_movie, //The format of the views inside the GridView
                        R.id.list_item_movie_imageview, //ID of the imageview to populate
                        movieArrayList
                        // /new ArrayList<Movie>() //the data source we want to populate the ListView with
                );
                gridView.setAdapter(movieAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Movie movie = (Movie) gridView.getItemAtPosition(position);
                        ((Callback) getActivity()).onItemSelected(movie);

                    }
                });

                FetchMovieTask fetchMovieTask = new FetchMovieTask();
                fetchMovieTask.execute(prefs);
                break;
            }
            case "favorites": {
                movieCursorAdapter = new MovieCursorAdapter(getActivity(), null, 0);
                gridView.setAdapter(movieCursorAdapter);
                getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CursorWrapper c = (CursorWrapper) parent.getItemAtPosition(position);
                        c.moveToPosition(position);

                        Bundle extras = new Bundle();
                        extras.putString("OVERVIEW", c.getString(COL_MOVIE_OVERVIEW));
                        extras.putString("POSTERPATH", c.getString(COL_MOVIE_POSTER));
                        extras.putString("RATING", c.getString(COL_MOVIE_RATING));
                        extras.putString("RELEASEDATE", c.getString(COL_MOVIE_RELEASE));
                        extras.putString("TITLE", c.getString(COL_MOVIE_TITLE));
                        ((Callback) getActivity()).onItemSelected(extras);

                    }
                });
                break;
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(LOG_TAG, "onActivityCreated");
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("POPULARMOVIES", "onStart()");
        updateMovies();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "created loader");
        cursorLoader = new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null);
        return cursorLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "row count: " + data.getCount());
        if (movieCursorAdapter != null) {
            movieCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieCursorAdapter.swapCursor(null);
    }


    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        protected ArrayList<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException {

            //Names of the JSON Objects we will extract
            final String TMDB_RESULTS = "results";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieJsonArray = movieJson.getJSONArray(TMDB_RESULTS);
            Log.v(LOG_TAG, "Got the JSON Array");

            ArrayList<Movie> movieArrayList = Movie.fromJsonArray(movieJsonArray);
            Log.v(LOG_TAG, "got the movies");
            return movieArrayList;

            //Return a List of Movie objects that the adapter will use
        }

        protected ArrayList<ArrayList> getReviewsAndTrailersFromJson(String jsonString)
                throws JSONException {
            // Return this first array containing two ArrayLists of Trailers and Reviews
            ArrayList<ArrayList> returnList = new ArrayList<ArrayList>();
            ArrayList<Trailer> trailerList = new ArrayList<Trailer>();
            ArrayList<Review> reviewList = new ArrayList<Review>();
            final String TMDB_TRAILERS = "trailers";
            final String TMDB_YOUTUBE = "youtube";
            final String TMDB_REVIEWS = "reviews";
            final String TMDB_RESULTS = "results";

            JSONObject movieJson = new JSONObject(jsonString);

            // Might have to handle cases in where there are no trailers or reviews

            // Get trailers
            JSONObject trailerJsonObject = movieJson.getJSONObject(TMDB_TRAILERS);
            JSONArray youtubeJsonArray = trailerJsonObject.getJSONArray(TMDB_YOUTUBE);

            // Get reviews
            JSONObject reviewJsonObject = movieJson.getJSONObject(TMDB_REVIEWS);
            JSONArray resultsJsonArray = reviewJsonObject.getJSONArray(TMDB_RESULTS);
            if (youtubeJsonArray.length() != 0) {
                trailerList = Trailer.fromJsonArray(youtubeJsonArray);
            }

            if (resultsJsonArray.length() != 0) {
                reviewList = Review.fromJsonArray(resultsJsonArray);
            }

            returnList.add(trailerList);
            returnList.add(reviewList);

            return returnList;
        }

        protected void getReviewsAndTrailersFromMovieList(ArrayList<Movie> arrayList) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewAndTrailerJsonStr = null;
            ArrayList<ArrayList> arrayLists = null;
            //http://api.themoviedb.org/3/movie/{movie_id}?api_key=your_key&append_to_response=trailers,reviews

            for (int i = 0; i < arrayList.size(); i++) {
                final Movie movie = arrayList.get(i);
                try {
                    // get movie id of movie in position i of array
                    final String MOVIE_ID = arrayList.get(i).getId();
                    final String BASE_URL = "http://api.themoviedb.org/3/movie/" + MOVIE_ID;
                    final String API_PARAM = "api_key";
                    final String APPEND_PARAM = "append_to_response";
                    final String RESPONSE = "trailers,reviews";

                    // TODO take both API keys out when commiting
                    final String API_KEY = "YOUR_API_KEY";

                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(API_PARAM, API_KEY)
                            .appendQueryParameter(APPEND_PARAM, RESPONSE)
                            .build();

                    URL url = new URL(builtUri.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer stringBuffer = new StringBuffer();
                    if (inputStream == null) {
                        return;
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuffer.append(line + "/n");
                    }

                    if (stringBuffer.length() == 0) {
                        return;
                    }

                    reviewAndTrailerJsonStr = stringBuffer.toString();

                    try {
                        arrayLists = getReviewsAndTrailersFromJson(reviewAndTrailerJsonStr);
                        movie.setMovieTrailers(arrayLists.get(0));
                        movie.setMovieReviews(arrayLists.get(1));

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        urlConnection.disconnect();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            //Declared outside try/catch block
            //In order to close these in the finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            ArrayList<Movie> movies = null;

            //String of the raw JSON response
            String movieJsonStr = null;

            //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[APIKEY]

            try {
                final String API_KEY = "YOUR_API_KEY";
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";
                //final String SIZE = "";

                //Build an URI with given parameters
                Uri builtUri = Uri.parse(BASE_URL).buildUpon().
                        appendQueryParameter(SORT_PARAM, params[0]).
                        appendQueryParameter(API_PARAM, API_KEY)
                        .build();

                //Build URL with Uri
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URL");

                //Create HttpUrlConnection and open the connection
                //Set request method to "GET"
                //Finally connect through the HttpURLConnection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Because we are now connected, we can read in data
                //Create inputstream from the HttpURLConnection
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                if (inputStream == null) {
                    //Nothing to do
                    return null;
                }

                //Chain InputStream to a BufferedReader
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                //Now create the data string with StringBuffer
                while ((line = reader.readLine()) != null) {
                    // Adding a newline makes it easier to debug
                    // Will not affect the parsing
                    stringBuffer.append(line + "/n");
                }

                if (stringBuffer.length() == 0) {
                    //Stream was empty, so nothing to parse
                    return null;
                }

                movieJsonStr = stringBuffer.toString();
                //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[api_key]
                //parse json str to get data

                try {
                    movies = getMovieDataFromJson(movieJsonStr);
                    Log.v(LOG_TAG, "got movies");
                    getReviewsAndTrailersFromMovieList(movies);
                    Log.v(LOG_TAG, "got trailers and reviews");
                    return movies;
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                Log.e(LOG_TAG, "Fragment error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (result != null && movieAdapter != null) {
                movieAdapter.clear();
                for (Movie movie : result) {
                    movieAdapter.add(movie);
                }
            }

        }
    }

}
