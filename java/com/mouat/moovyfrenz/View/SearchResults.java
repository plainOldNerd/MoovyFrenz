package com.mouat.moovyfrenz.View;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mouat.moovyfrenz.Controller.SearchResultsAA;
import com.mouat.moovyfrenz.Model.Mediator;
import com.mouat.moovyfrenz.Model.Movie;
import com.mouat.moovyfrenz.R;

import java.util.ArrayList;
import java.util.List;

public class SearchResults extends Activity
{
    private SearchResults sr = this;
    private Mediator mediator = Mediator.getInstance();
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        getShitReady();
    }

    private void getShitReady()
    {
        br = mediator.setConnChecker(this);

        String searchkey = mediator.getSearchkey();
        if( searchkey != null )
        {   mediator.doSearch( searchkey, sr );     }

        List<Movie> searchResults = mediator.getSearchResults();

        if( searchResults == null )
        {
            Intent intent = new Intent( sr, StartOptions.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ).putExtra( "noCanDoSearch",
                    "There is no internet connection.  The search will resume later." );
            startActivity( intent );
        }
        else
        {
            ArrayAdapter<Movie> resultsAdapter =
                    new SearchResultsAA( this, searchResults );
            ListView searchResultsLV = (ListView) findViewById( R.id.searchResultsLV );
            searchResultsLV.setAdapter(resultsAdapter);
            searchResultsLV.setOnItemClickListener
                (
                    new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            Intent getDetails = new Intent(sr, PartyDetails.class);
                            Movie selected = (Movie) parent.getItemAtPosition(position);
                            getDetails.putExtra("title", selected.getTitle())
                                    .putExtra("year", selected.getYear())
                                    .putExtra("shortplot", selected.getShortPlot())
                                    .putExtra("fullplot", selected.getFullPlot())
                                    .putExtra("previousScreen", "@string/comingFromCreate")
                                    .putExtra("omdbid", selected.getID()).putExtra("partyID", "")
                                    .putExtra("posterURL", selected.getPosterURL());
                            startActivity(getDetails);
                        }
                    }
                );
        }
    }

    @Override
    public void onBackPressed()
    {
        mediator.setSearchResults( new ArrayList<Movie>(), sr );
        Intent goBack = new Intent( sr, SearchMovies.class );
        goBack.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity( goBack );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_results, menu);
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
