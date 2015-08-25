package com.example.mykongee.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
public class MainActivityFragment extends Fragment {

    MovieAdapter movieAdapter;
    GridView gridView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container);

        // Create the GridView object from the xml layout.
        // NOTE: Since rootView is the xml layout file containing the xml for gridview,
        // rootView.findViewById is used. NOT getActivity()
        final ArrayList<Movie> movieArrayList = new ArrayList<Movie>();

        movieAdapter = new MovieAdapter(
                getActivity(), //Context in which we want to place the adapter into
                R.layout.list_item_movie, //The format of the views inside the GridView
                R.id.list_item_movie_imageview, //ID of the imageview to populate
                new ArrayList<Movie>() //the data source we want to populate the ListView with
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


    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>>{

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        protected ArrayList<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException{

            //Names of the JSON Objects we will extract
            final String TMDB_RESULTS = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_IMAGE = "poster_path";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_AVG_VOTE = "vote_average";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieJsonArray = movieJson.getJSONArray(TMDB_RESULTS);
            Log.v(LOG_TAG, "Got the JSON Array");

            ArrayList<Movie> movieArrayList = Movie.fromJsonArray(movieJsonArray);
            return movieArrayList;

            //Return a List of Movie objects that the adapter will use
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            //Declared outside try/catch block
            //In order to close these in the finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //String of the raw JSON response
            String movieJsonStr = null;

            //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[APIKEY]

            try {
                final String API_KEY = "YOUR_API_KEY";
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM="api_key";
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
                    return getMovieDataFromJson(movieJsonStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (IOException e){
                Log.e(LOG_TAG, "Fragment error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                } if (reader != null) {
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
        protected void onPostExecute(ArrayList<Movie> result){
            if (result != null && movieAdapter != null) {
                movieAdapter.clear();
                for (Movie movie: result) {
                    movieAdapter.add(movie);
                }
            }

        }
    }

}
