package com.mouat.moovyfrenz.View;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mouat.moovyfrenz.Model.Mediator;
import com.mouat.moovyfrenz.Model.Movie;
import com.mouat.moovyfrenz.R;

import java.util.List;

public class SearchMovies extends Activity
{
    private Mediator mediator = Mediator.getInstance();
    private SearchMovies sm = this;
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_movies);

        br = mediator.setConnChecker(this);

        String searchkey = mediator.getSearchkey();
        if( searchkey != null )
        {   returnHome();   }

        List<Movie> searchResults = mediator.getSearchResults();
        if( searchResults != null && searchResults.size() > 0 )
        {
            Intent intent = new Intent( sm, SearchResults.class );
            startActivity( intent );
        }

        findViewById( R.id.searchButton ).setOnClickListener
        (
            new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Intent selectMovie = new Intent(sm, SearchResults.class);
                    String searchkey = ((EditText) findViewById(R.id.searchMovies)).getText().toString();
                    mediator.setSearchkey( searchkey );
                    startActivity(selectMovie);
                }
            }
        );
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mediator.setConnChecker(this);

        String searchkey = mediator.getSearchkey();
        if( searchkey != null )
        {   returnHome();   }

        List<Movie> searchResults = mediator.getSearchResults();
        if( searchResults != null && searchResults.size() > 0 )
        {
            Intent intent = new Intent( sm, SearchResults.class );
            startActivity(intent);
        }
    }

    private void returnHome()
    {
        Intent intent = new Intent( sm, StartOptions.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ).putExtra("noCanDoSearch",
                "A search is already in progress.");
        startActivity( intent );
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent( sm, StartOptions.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_party, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {   return true;    }

        return super.onOptionsItemSelected(item);
    }
}
