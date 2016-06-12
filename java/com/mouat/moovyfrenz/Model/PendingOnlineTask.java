package com.mouat.moovyfrenz.Model;

import android.content.Context;

import java.util.List;

/**
 * Created by Andrew on 14/10/2015.
 */
public class PendingOnlineTask
{
    private Mediator m =Mediator.getInstance();
    //  The parameter could be a url or a searchkey
    private String task, parameter;
    private Movie movie = null;
    private Context gc = null;

    public PendingOnlineTask( String task, String parameter, Movie movie )
    {
        this.task = task;
        this.parameter = parameter;
        this.movie = movie;
    }

    public PendingOnlineTask( String task, String parameter, Context gc )
    {
        this.task = task;
        this.parameter = parameter;
        this.gc = gc;
    }

    public void execute()
    {
        switch( task )
        {
            case "finishSearch":
            {
                Databaser db = new Databaser( gc );

                List<Movie> searchResults = new omdbSearch().doSearch(parameter, gc);
                db.addMovieList( searchResults );
                m.removePendingTask( this );
                break;
            }
            case "downloadImage":
            {
                new ImageDownloader( movie, parameter ).start();
                m.removePendingTask( this );
                break;
            }
            default:
        }
    }
}
