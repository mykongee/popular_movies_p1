package com.example.mykongee.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        public MovieFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState == null || !savedInstanceState.containsKey("trailers")) {
                trailerList = new ArrayList<Trailer>();
            } else {
                trailerList = savedInstanceState.getParcelableArrayList("trailers");
            }
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            LinearLayout parentLayout = (LinearLayout) rootView;

            LayoutInflater layoutInflater = getLayoutInflater(savedInstanceState);
            View view;

            Intent intent = getActivity().getIntent();
            Bundle extras = intent.getExtras();
            Movie movie = extras.getParcelable("MOVIE");

            trailerList = movie.getMovieTrailers();
            reviewList = movie.getMovieReviews();

            if ((trailerList != null) && trailerList.size() != 0) {
                view = layoutInflater.inflate(R.layout.header, parentLayout, false);

                TextView header = (TextView) view.findViewById(R.id.header);
                header.setText("Trailers");
                parentLayout.addView(view);

                for (int i = 0; i < trailerList.size(); i++) {
                    final int x = i;
                    view = layoutInflater.inflate(R.layout.cardview_trailer, parentLayout, false);

                    TextView textView = (TextView) view.findViewById(R.id.name);
                    textView.setText(trailerList.get(i).getName());

                    view.setOnClickListener(new View.OnClickListener() {
                        String source = trailerList.get(x).getSource();

                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + source)));
                        }
                    });

                    if (view != null) {
                        parentLayout.addView(view);
                    }
                }
            }

            if ((reviewList != null) && reviewList.size() != 0) {
                view = layoutInflater.inflate(R.layout.header, parentLayout, false);

                TextView header = (TextView) view.findViewById(R.id.header);
                header.setText("Reviews");
                parentLayout.addView(view);
                for (int i = 0; i < reviewList.size(); i++) {
                    view = layoutInflater.inflate(R.layout.cardview_review, parentLayout, false);

                    TextView author = (TextView) view.findViewById(R.id.author);
                    author.setText(reviewList.get(i).getAuthor());
                    TextView content = (TextView) view.findViewById(R.id.content);
                    content.setText(reviewList.get(i).getContent());

                    if (view != null) {
                        parentLayout.addView(view);
                    }
                }
            }

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

