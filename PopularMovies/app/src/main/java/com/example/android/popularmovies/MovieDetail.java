package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetail extends AppCompatActivity {

    private String movieName;
    private String thumbnailUrl;
    private String synopsis;
    private String userRating;
    private String releaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            //data from intent is stored in variable movieData
            String movieData = intent.getStringExtra(Intent.EXTRA_TEXT);
            try {
                //Calling method getRequiredMovieValues.
                getRequiredMovieValues(movieData);

                //Loading image of movie in imageview using Picasso.
                Picasso.with(this).load("http://image.tmdb.org/t/p/w342/" + thumbnailUrl).into((ImageView) findViewById(R.id.movie_poster));

                //Setting textviews with the respected data of movie.
                ((TextView) findViewById(R.id.movie_title)).setText(movieName);
                ((TextView) findViewById(R.id.release_date)).setText(releaseDate);
                ((TextView) findViewById(R.id.movie_rating)).setText(userRating);
                ((TextView) findViewById(R.id.movie_description)).setText(synopsis);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * DESCRIPTION: using a JSON it assign values to movieDetail variables.
     *
     * @param movieData
     * @throws JSONException
     */
    private void getRequiredMovieValues(String movieData) throws JSONException {
        //converts JSON string to JSON object.
        JSONObject movieDataObject = new JSONObject(movieData);

        //assign values to movie detail variables
        movieName = movieDataObject.getString("original_title");
        thumbnailUrl = movieDataObject.getString("backdrop_path");
        synopsis = movieDataObject.getString("overview");
        userRating = movieDataObject.getString("vote_average") + "/10";
        releaseDate = movieDataObject.getString("release_date").substring(0, 4);
    }
}
