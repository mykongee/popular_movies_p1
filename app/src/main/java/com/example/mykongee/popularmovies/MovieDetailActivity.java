package com.example.mykongee.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mykongee.popularmovies.Adapters.ReviewAdapter;
import com.example.mykongee.popularmovies.Adapters.TrailerAdapter;
import com.example.mykongee.popularmovies.Models.Movie;
import com.example.mykongee.popularmovies.Models.Review;
import com.example.mykongee.popularmovies.Models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new MovieFragment())
                    .commit();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MovieFragment extends Fragment {
        final String LOG_TAG = MovieFragment.class.getSimpleName();
        ArrayList<Trailer> trailerList;
        ArrayList<Review> reviewList;
        private TrailerAdapter trailerAdapter;
        private ReviewAdapter reviewAdapter;

        public MovieFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            if (savedInstanceState == null || !savedInstanceState.containsKey("trailers")){
//                trailerList = new ArrayList<Trailer>();
//            } else {
//                trailerList = savedInstanceState.getParcelableArrayList("trailers");
//            }
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            Intent intent = getActivity().getIntent();
            Bundle extras = intent.getExtras();
            Movie movie = extras.getParcelable("MOVIE");

            trailerList = movie.getMovieTrailers();
            reviewList = movie.getMovieReviews();
            Log.v(LOG_TAG, "detail fragment review list size: " + reviewList.size());
            trailerAdapter = new TrailerAdapter(
                    getActivity(), //Context in which we want to place the adapter into
                    R.layout.list_item_trailer,
                    R.id.list_item_trailer,
                    trailerList
                    // /new ArrayList<Trailer>() //the data source we want to populate the ListView with
            );

//            reviewAdapter = new ReviewAdapter(
//                    getActivity(), R.layout.list_item_review, R.id.list_item_review, reviewList
//            );

//            if (trailerAdapter != null) {
//                trailerAdapter.clear();
//                for (Trailer trailer : trailerList) {
//                    trailerAdapter.add(trailer);
//                }
//            }
//
//            if (reviewAdapter != null) {
//                reviewAdapter.clear();
//                for (Review review : reviewList) {
//                    reviewAdapter.add(review);
//                }
//            }


            ListView trailers = (ListView) rootView.findViewById(R.id.trailers);

            RecyclerView reviews = (RecyclerView) rootView.findViewById(R.id.reviews);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());

            ReviewAdapter reviewAdapter = new ReviewAdapter(reviewList);

            trailers.setAdapter(trailerAdapter);
            reviews.setLayoutManager(llm);
            reviews.setAdapter(reviewAdapter);

            ((TextView) rootView.findViewById(R.id.title)).setText(movie.getTitle());
            ((ImageView) rootView.findViewById(R.id.poster)).
                    setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(getActivity())
                    .load("http://image.tmdb.org/t/p/w342/" + movie.getPosterPath())
                    .into((ImageView) rootView.findViewById(R.id.poster));
            ((TextView) rootView.findViewById(R.id.overview)).setText(movie.getOverview());
            ((TextView) rootView.findViewById(R.id.rating))
                    .setText(movie.getRating() + "/10");
            ((TextView) rootView.findViewById(R.id.release_date))
                    .setText(movie.getReleaseDate());

            return rootView;
        }
    }
}

