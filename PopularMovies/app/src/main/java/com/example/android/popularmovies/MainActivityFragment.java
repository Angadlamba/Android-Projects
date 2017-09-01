package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

//http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=8d4628298efdcca6d7e373b670dfa92d

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    //custom Adapter, object of CustomList
    CustomList mAdapter;

    //Creating object of class FetchMovieData
    FetchMovieData fetchMovieData = new FetchMovieData();

    //JSON from MovieDb is stored as a String variable.
    String movieDbJsonStr;

    //Image url.
    String imgUrl= "http://image.tmdb.org/t/p/w342/";

    GridView grid;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Sorts poster in grid by popurality. Default Option
        if (id == R.id.action_sortBy_popurality) {
            FetchMovieData fetchMovieData = new FetchMovieData();
            fetchMovieData.execute("popularity.desc");
            return true;
        }
        //Sorts poster in grid by ratings.
        else if (id == R.id.action_sortBy_ratings) {
            FetchMovieData fetchMovieData = new FetchMovieData();
            fetchMovieData.execute("vote_average.desc");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * DESCRIPTION: fetches and returns Movie Details JSON object.
     *
     * @param url
     * @return Object
     */
    private Object fetchMovieJsonData(String url) throws JSONException {
        //converts JSON string to JSON object.
        JSONObject movieDbJson = new JSONObject(movieDbJsonStr);

        //gets JSONArray name results.
        JSONArray results = movieDbJson.getJSONArray("results");

        //loops through results array to find the movie with the matching movie poster url.
        int i = 0;
        while (!(imgUrl + results.getJSONObject(i).getString("poster_path")).equalsIgnoreCase(url)) {
            if (i >= results.length())
                //if not found returns null.
                return null;
            i++;
        }

        //fetches the movie's JSON object from result which matches the looping condition(above).
        JSONObject movieData = results.getJSONObject(i);
        return movieData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflates View with the layout xml file.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //creates a GridView of id listview_movie.
        grid = (GridView) rootView.findViewById(R.id.listview_movie);

        fetchMovieData.execute("popularity.desc");

        //opens a new activity name movieDetails on click an item in grid.
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movieUrl = mAdapter.getItem(position);
                try {
                    Object movieData = fetchMovieJsonData(movieUrl);
                    Intent intent = new Intent(getActivity(), MovieDetail.class)
                            .putExtra(Intent.EXTRA_TEXT, movieData.toString());
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    public class FetchMovieData extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieData.class.getSimpleName();

        /**
         * DESCRIPTION: fetches and returns Movie Details array of urls of movie posters from the JSON.
         *
         * @param movieDbJsonStr
         * @return String[]
         */
        private String[] getMovieImageUrl(String movieDbJsonStr) throws JSONException {
            //converts String to JSON object.
            JSONObject movieDbJson = new JSONObject(movieDbJsonStr);

            //gets JSONArray name results.
            JSONArray results = movieDbJson.getJSONArray("results");

            //number of movies in the JSON data.
            int movieNumber = results.length();

            //Stores url of movie posters.
            String[] url = new String[movieNumber];

            //loops through the JSON array to find all urls.
            for (int i = 0; i < movieNumber; i++) {
                JSONObject movieData = results.getJSONObject(i);
                String posterId = movieData.getString("poster_path");
                url[i] = imgUrl + posterId;
            }
            return url;
        }

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            movieDbJsonStr = null;

            try {
                // Construct the URL for the MovieDbApi query
                final String MOVIEDB_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie";
                final String SORTBY_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendQueryParameter(SORTBY_PARAM, params[0])
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();


                URL url = new URL(builtUri.toString());

                // Create the request to MovieDbApi, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieDbJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attemping
                // to parse it.
                return null;
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

            try {
                String[] url = getMovieImageUrl(movieDbJsonStr);
                return url;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result == null) {
                //do nothing. Since HTTPconnection was failed to established.
            } else {
                //create new object of CustomList adapter.
                mAdapter = new CustomList(result, getActivity());

                //sets data behind in grid GridView.
                grid.setAdapter(mAdapter);
            }
        }
    }
}
