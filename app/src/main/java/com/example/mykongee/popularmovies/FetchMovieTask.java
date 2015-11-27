package com.example.mykongee.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by Mykongee on 9/5/15.
 */

public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    public MovieAdapter movieAdapter;

    protected ArrayList<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException {

        //Names of the JSON Objects we will extract
        final String TMDB_RESULTS = "results";
        final String TMDB_TITLE = "original_title";
        final String TMDB_IMAGE = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_AVG_VOTE = "vote_average";


        //Get the JSON Array of the movies
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieJsonArray = movieJson.getJSONArray(TMDB_RESULTS);
        Log.v(LOG_TAG, "Got the JSON Array");

        //Populate the ArrayList with Movie objects constructed from
        //JSON Objects
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
            final String API_KEY = "c99a4285b4e0a83397b9deca2e4d9d16";
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
                return getMovieDataFromJson(movieJsonStr);
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