package com.mouat.moovyfrenz.Model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 23/08/2015.
 */
public class omdbSearch implements Searcher
{
    @Override
    public List<Movie> doSearch( String kiiword, Context genericContext )
    {
        final Mediator m = Mediator.getInstance();
        final String keyword = kiiword;
        final Context gc = genericContext;
        //  I serendipitously discovered that having this be a class variable rather than a method
        //  variable we can add results from each new search to the previous search results
        final List<Movie> resultingMovies = new ArrayList<Movie>();

        m.setSearchkey(keyword);

        Thread omdbQueries = new Thread()
        {
            @Override
            public void run()
            {
                //  Make sure we are connected.
                if( m.isConnected() )
                {
                    //  In case more than one word was entered
                    String[] keywordsPlural = keyword.split(" ");
                    String url = "http://www.omdbapi.com/?s=";
                    StringBuilder builder = new StringBuilder(url);
                    int i = 0;

                    for (String keywordSingular : keywordsPlural)
                    {
                        builder.append(keywordSingular);
                        ++i;
                        if (i != keywordsPlural.length)
                        {   builder.append("+");    }
                    }

                    builder.append("&plot=short&r=json");
                    url = builder.toString();

                    try
                    {
                        URL u = new URL(url);
                        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Accept", "text/html");
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        JSONObject searchResults = new JSONObject(in.readLine());
                        JSONArray results = searchResults.getJSONArray("Search");

                        conn.disconnect();

                        for (i = 0; i < results.length(); ++i)
                        {
                            JSONObject nextMovie = results.getJSONObject(i);
                            String title = nextMovie.getString("Title");
                            String year = nextMovie.getString("Year");
                        //  Do NOT change "imdbID" to "omdbID"!!
                            String omdbid = nextMovie.getString("imdbID");

                            //  Now get the short plot
                            u = new URL("http://www.omdbapi.com/?i=" + omdbid + "&plot=short&r=json");
                            conn = (HttpURLConnection) u.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setRequestProperty("Accept", "text/html");
                            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            searchResults = new JSONObject(in.readLine());

                            String shortplot = searchResults.getString("Plot");

                            conn.disconnect();

                            //  Now to get the full plot and poster
                            u = new URL("http://www.omdbapi.com/?i=" + omdbid + "&plot=full&r=json");
                            conn = (HttpURLConnection) u.openConnection();
                            conn.setRequestMethod("GET");
                            conn.setRequestProperty("Accept", "text/html");
                            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            searchResults = new JSONObject(in.readLine());

                            String fullplot = searchResults.getString("Plot"),
                                    poster = searchResults.getString("Poster");

                            conn.disconnect();

                            resultingMovies.add(i, new Movie(title, year, omdbid, shortplot, fullplot, poster));
                        }
                    }
                    catch (Exception e) {   e.printStackTrace();    }

                    m.setSearchkey( null );
                    m.setSearchResults( resultingMovies, gc );
                }
                else
                {
                    PendingOnlineTask finishSearch = new PendingOnlineTask( "finishSearch", keyword, gc );
                    m.addPendingTask( finishSearch );
                }
            }
        };

        omdbQueries.start();

        if( m.isConnected() )
        {
            try
            {   omdbQueries.join();     }
            catch(Exception e){}

            return resultingMovies;
        }
        else
        {   return null;    }


    }
}
