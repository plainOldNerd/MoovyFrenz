package com.mouat.moovyfrenz.Model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 18/10/2015.
 */
public class modelSearch implements Searcher
{
    Mediator m = Mediator.getInstance();

    @Override
    public List<Movie> doSearch( String keyword, Context gc )
    {
        List<Movie> movies = m.getMovies();

        List<Movie> searchResults = new ArrayList<Movie>();

        for( Movie currentMovie : movies )
        {
            boolean addMovie = true;
            String title = currentMovie.getTitle().toLowerCase();

            for( String word : keyword.split( " " ) )
            {
                if( !matches(title, keyword) )
                {
                    addMovie = false;
                    break;
                }
            }
            if( addMovie )
            {   searchResults.add( currentMovie );    }
        }

        return searchResults;
    }

    private boolean matches( String title, String keyword )
    {
        int index = 0;

        while( index > -1 )
        {
            index = title.indexOf( keyword, index );

            if( index > 0 )
            {
                if( title.charAt( index-1 ) >= 'a' && title.charAt( index-1 ) <= 'z' )
                {
                    ++index;
                    continue;
                }
            }
            if( index != -1 && index + keyword.length() < title.length() )
            {
                if( title.charAt( index+keyword.length() ) >= 'a' && title.charAt( index+keyword.length() ) <= 'z' )
                {
                    ++index;
                    continue;
                }
            }
            if( index != -1 )
            {
                return true;
            }
        }

        return false;
    }
}
