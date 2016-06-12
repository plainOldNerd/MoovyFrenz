package com.mouat.moovyfrenz.Model;

import android.content.BroadcastReceiver;
import android.content.Context;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by Andrew on 16/08/2015.
 */
public class Mediator
{
    private static Mediator mediator = new Mediator();
    private String currentUser = "Andrew";

    private List<User> users = new ArrayList<User>();
    private List<Movie> movies = new ArrayList<Movie>();
    private List<Party> parties = new ArrayList<Party>();


    private Party currentParty = null;
    private Movie newPartyMovie = new Movie( "", "", "", "", "", "" );
    private Party newParty = new Party( "", "", "", "", "" );

    private ConnectivityChecker conn;
    private boolean connected;
    private ArrayList<PendingOnlineTask> pendingTasks = new ArrayList<PendingOnlineTask>();

    private List<Movie> searchResults = new ArrayList<Movie>();
    private String searchkey = null;
    private List<Searcher> searchers = new ArrayList<Searcher>();

    public static Mediator getInstance()
    {   return mediator;    }

    private Mediator()
    {
        users.add( new User( currentUser , "0401 011 100") );

        // Chain of Responsibility is implemented in doSearch of Search methods
    }

    /*********************************************************************************************
                    USER METHODS
     *********************************************************************************************/

    public String getCurrentUser()
    {   return currentUser; }

    public User getUser(String username)
    {
        //  Note for refactoring: users will have been added alphabetically, so this could be made more
        //  efficient
        for(int i=0; i<users.size(); ++i)
        {
            if(users.get(i).getName().compareTo(username) == 0)
                return users.get(i);
        }
        return null;
    }

    /*********************************************************************************************
                    MOVIE METHODS
     *********************************************************************************************/

    public List<Movie> getMovies()
    {   return movies;  }

    public Movie getMovie( String omdbid )
    {
        int i=0;
        while( i<movies.size() || i<searchResults.size() )
        {
            if( i<movies.size() && movies.get(i).getID().compareTo( omdbid ) == 0 )
            {   return movies.get(i);   }
            if( i<searchResults.size() && searchResults.get(i).getID().compareTo( omdbid ) == 0 )
            {   return searchResults.get(i);    }
            ++i;
        }

        return null;
    }

    public void newMovie( String title, String year, String omdbid, String shortplot,
                          String fullplot, String posterURL )
    {
        newPartyMovie = getMovie( omdbid );

        if( newPartyMovie == null )
        {   newPartyMovie = new Movie( title, year, omdbid, shortplot, fullplot, posterURL ); }
    }

    public void addMovieFromDB( Movie movie )
    {   movies.add( movie );    }

    /*********************************************************************************************
                     PARTY METHODS
     *********************************************************************************************/

    public void initPartyID( Context gc )
    {
        String nextID = new Databaser( gc ).initPartyStaticID();

        char IDchars[] = nextID.toCharArray();
        int i = IDchars.length-1;

        while( IDchars[i] == '9' && i>0 )
        {
            IDchars[i] = 'A';
            --i;
        }
        if( IDchars[i] == 'Z' )
        {   IDchars[i] = '0';   }
        else
        {   IDchars[i] = (char) (IDchars[i] + 1); }

        nextID = new String( IDchars );

        Party.initIDs( nextID );
    }

    public void setParties( List<Party> parties )
    {   this.parties = parties;     }

    public List<Party> getParties( Context gc )
    {   return new Databaser( gc ).getParties();    }

    public void createParty( List<User> invitees, Context gc )
    {
    //  Perhaps on this method we could find the the NEW invitees and send a text message exclusively
    //  to them
        newParty.changeInvitees(invitees, gc);
        setSearchResults( new ArrayList<Movie>(), gc );

        parties.add( newParty );

        Databaser db = new Databaser( gc );
        db.insertParty( newParty );
    }

    public void newParty( String omdbid, String date, String time, String venue )
    {   newParty = new Party( currentUser, omdbid, date, time, venue );     }

    public Party findParty( String partyID, Context gc )
    {
        List<Party> parties = getParties( gc );

        for( Party party : parties )
        {
            List<Invite> partyInvitees = party.getInvitees();
            for( Invite invitation : partyInvitees )
            {
                if( party.getID().compareTo( partyID ) == 0 )
                {   return party;   }
            }
        }

        return null;
    }

    public void replyInvite( Party party, String acceptance, Context gc )
    {
        List<Invite> invitations = party.getInvitees();
        for( Invite invitation : invitations )
        {
            if( invitation.getUser().getName().compareTo( currentUser ) == 0 )
            {   invitation.setAcceptance( acceptance );     }
        }

        int accepted = 0;
        switch( acceptance )
        {
            case "@string/accept":
            {
                accepted = 1;
                break;
            }
            case "@string/maybe":
            {
                accepted = 2;
                break;
            }
            case "@string/decline":
            {
                accepted = 3;
            }
            default:
        }
        new Databaser( gc ).changeAcceptance( party.getID(), currentUser, accepted );
    }

    /*********************************************************************************************
     CONNECTIVITY METHODS
     *********************************************************************************************/

    public BroadcastReceiver setConnChecker( Context gc )
    {
        ConnectivityChecker cc = new ConnectivityChecker( gc );
        return cc.getReceiver();
    }

    public void setConnected( boolean conn )
    {
        connected = conn;
        if( connected )
        {
            try
            {
                for (PendingOnlineTask todo : pendingTasks)
                {   todo.execute();     }
            }
            catch( ConcurrentModificationException cme )
            {   cme.printStackTrace();  }
        }
    }

    public boolean isConnected()
    {   return connected;   }

    public void addPendingTask( PendingOnlineTask todo)
    {   pendingTasks.add(todo);   }

    public void removePendingTask( PendingOnlineTask done )
    {   pendingTasks.remove(done);    }

    /*********************************************************************************************
                    SEARCH METHODS
     *********************************************************************************************/

    public void doSearch( String kiiword, Context gc )
    {
        //  Set up the Chain of Responsibility
        searchers.add( searchers.size(), new modelSearch() );
        Databaser db = new Databaser( gc );
        searchers.add( searchers.size(), db );
        searchers.add(searchers.size(), new omdbSearch());
        //  Done

        //  if there's a search pending connectivity
        int i = 0;
        if( kiiword == null )
        {   return;     }
        searchResults = new ArrayList<Movie>();
        String keyword = kiiword.toLowerCase();

        /*  otherwise, try all searchers in order  */
        while( i<searchers.size() )
        {
            searchResults = searchers.get(i).doSearch( keyword, gc );
            if( searchResults != null && searchResults.size() != 0 )
            {
                movies.addAll( searchResults );
                db.addMovieList(searchResults);

                searchkey = null;
                return;
            }
            ++i;
        }
    }

    public void setSearchResults( List<Movie> searchResults, Context gc )
    {
        this.searchResults = searchResults;
        movies.addAll( searchResults );

        if( searchResults.size() != 0 )
        {
            Databaser db = new Databaser( gc );
            db.addMovieList( searchResults );
        }
    }

    public List<Movie> getSearchResults()
    {   return searchResults;   }

    public void setSearchkey( String searchkey )
    {   this.searchkey = searchkey;    }

    public String getSearchkey()
    {   return searchkey;   }
}
