package com.mouat.moovyfrenz.Model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 16/08/2015.
 */
public class Party
{
    //  Java seems to call a Party constructor before calling a static method
    private static String nextID = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA@";
    private Mediator mediator = Mediator.getInstance();

    private String ID, host, movieID, date, time, venue, coords;
    private Movie movie = null;
    private List<Invite> invitees = new ArrayList<Invite>();

    public Party( String creator, String omdbid, String date, String time, String venue )
    {
        ID = new String( nextID );

        char[] IDchars = nextID.toCharArray();
        int i = IDchars.length-1;
        while( IDchars[i] == '9' && i > 0 )
        {
            IDchars[i] = 'A';
            --i;
        }
        if( IDchars[i] == 'Z' )
        {   IDchars[i] = '0';   }
        else
        {   IDchars[i] = (char) (IDchars[i] + 1);   }
    //  After 36^34 party IDs we will run out.  There should be some way to handle this by then.
        nextID = new String ( IDchars );

        host = creator;
        movieID = omdbid;
        this.date = date;
        this.time = time;
        this.venue = venue;
    }

    public Party( String ID, String creator, String omdbid, String date, String time, String venue )
    {
        this.ID = ID;
        host = creator;
        movieID = omdbid;
        this.date = date;
        this.time = time;
        this.venue = venue;
    }

    public static void initIDs( String nextID )
    {
        if( nextID.compareTo( Party.nextID ) > 0 )
        {   Party.nextID = nextID;  }
    }

    public String getID()
    {   return ID;  }

    public String getHost()
    {   return host;    }

    public Movie getMovie()
    {
        if( movie == null )
        {   movie = mediator.getMovie( movieID );    }
        return movie;
    }

    public String getDate()
    {   return date;    }

    public String getTime()
    {   return time;    }

    public String getVenue()
    {   return venue;   }

    public List<Invite> getInvitees()
    {   return invitees;    }

    public List<User> getInvitedUsers()
    {
        List<User> invitedUsers = new ArrayList<User>();

        for( Invite invitation : invitees )
        {   invitedUsers.add( invitation.getUser() );   }

        return invitedUsers;
    }

    public void changeDetails( String date, String time, String venue, Context gc )
    {
        this.date = date;
        this.time = time;
        this.venue = venue;

        new Databaser( gc ).changePartyDetails( this, date, time, venue );
    }
/*
    public void changeDate( String date, Context gc )
    {
        this.date = date;

        Databaser db = new Databaser( gc );
        db.changeDate(date, this);
    }

    public void changeTime( String time, Context gc )
    {
        this.time = time;

        Databaser db = new Databaser( gc );
        db.changeTime(time, this);
    }

    public void changeVenue( String venue, Context gc )
    {
        this.venue = venue;

        Databaser db = new Databaser( gc );
        db.changeVenue(venue, this);
    }
*/
    public void changeMovie( String omdbid, Context gc )
    {
        movie = mediator.getMovie( omdbid );

        Databaser db = new Databaser( gc );
        db.changeMovie(omdbid, this);
    }

    public void changeInvitees( List<User> invitees, Context gc )
    {
        for( User invitee : invitees )
        {
            if( !alreadyInvited( invitee.getName() ) )
            {
                this.invitees.add( new Invite( invitee ) );

                Databaser db = new Databaser( gc );
                db.addInvitee(invitee, this);
            }
        }
    }

    private boolean alreadyInvited( String username )
    {
        for( Invite invite : invitees )
        {
            if( invite.getUser().getName().compareTo( username ) == 0 )
            {   return true;    }
        }

        return false;
    }

    public void setInvitesFromDB( List<Invite> invites )
    {   invitees = invites;     }

    //  Returns 0 if partycrasher is an invitee, 1 if host, and 2 if not invited
    public int isInvited( String partycrasher )
    {
        if( partycrasher.compareTo( host ) == 0 )
        {    return 1;  }
        for( Invite invitee : invitees )
        {
            if( invitee.getUser().getName().compareTo( partycrasher ) == 0 )
            {    return 0;  }
        }

        return 2;
    }

    public boolean hasAccepted( Context gc )
    {
        int acceptance = new Databaser( gc ).getAcceptance( mediator.getCurrentUser(), this );

        if( acceptance == 1 )
        {   return true;    }

        return false;
    }

    public boolean hasMaybeed( Context gc )
    {
        int acceptance = new Databaser( gc ).getAcceptance( mediator.getCurrentUser(), this );

        if( acceptance == 2 )
        {   return true;    }

        return false;
    }

    public boolean hasDeclined( Context gc )
    {
        int acceptance = new Databaser( gc ).getAcceptance( mediator.getCurrentUser(), this );

        if( acceptance == 3 )
        {   return true;    }

        return false;
    }
}
