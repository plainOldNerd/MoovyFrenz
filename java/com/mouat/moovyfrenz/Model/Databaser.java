package com.mouat.moovyfrenz.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 16/10/2015.
 */
public class Databaser extends SQLiteOpenHelper implements Searcher
{
    private Mediator m = Mediator.getInstance();
    private final String currentUsername = "Andrew";

    public Databaser( Context gc )
    {
        //  some version validation would be an idea for future releases
        super(gc, "PartiesAndStuff.db", null, 1);
        getWritableDatabase();
        getReadableDatabase();

        insertUser(new User(currentUsername, "0401 011 100"));
    }

    @Override
    public void onCreate( SQLiteDatabase db )
    {
        //  the phone number is expected to be gotten from the SIM card, but we can expect that the
        //  max length of a ph # will be something alike "+61 401 011 100"
        //  some sort of UserName validation is expected to have been performed
        db.execSQL( "create table User ( Name varchar( 10 ), PhNo varchar( 15 ), primary key (Name) );" );

        //  likely limits for plot descriptions and poster URLs are taken from Star Wars search
        db.execSQL("create table Movie ( MovieID char( 9 ), Title varchar( 50 ), Year char( 9 ), " +
                "Short varchar( 250 ), Full varchar ( 1000 ), PosterURL varchar( 140 )," +
                "primary key (MovieID) );");

        db.execSQL("create table Party ( PartyID char( 34 ),  MovieID char(9), Host varchar( 20 )" +
                ", Date char( 11 ), Time char( 8 ), Venue varchar( 40 )," +
                "primary key (PartyID), " +
                "foreign key (MovieID) references Movie(MovieID)," +
                "foreign key (Host) references User(Name) );");

        db.execSQL("create table Invited ( PartyID char( 34 ), Username varchar( 20 ), Accepted int," +
                "primary key (PartyID, Username)," +
                "foreign key (PartyID) references Party(PartyID)," +
                "foreign key (Username) references User(Name) );");

        db.execSQL("create table Rating( Username varchar( 20 ), MovieID char( 9 ), Score float," +
                "primary key (Username, MovieID)," +
                "foreign key (Username) references User(Name)," +
                "foreign key (MovieID) references Movie(MovieID) );");
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion )
    {}

    /*********************************************************************************************
     USER METHODS
     *********************************************************************************************/

    public void insertUser( User user )
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put( "Name", user.getName());
        cv.put("PhNo", user.getNumber() );

        try
        {   db.insert( "User", null, cv );  }
        catch( Exception e ){}

        db.close();
    }

    public float getRating( User user, Movie movie )
    {
        SQLiteDatabase db = getReadableDatabase();
        float rating = -1;
        Cursor c = db.rawQuery( "select Score from Rating " +
                "where Username = '" + user.getName() + "' and MovieID = '" + movie.getID() + "';", null );
        while( c.moveToNext() )
        {   rating = c.getFloat(0);  }

        return rating;
    }

    public void setRating( User user, String movieID, float rating )
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteDatabase db2 = getWritableDatabase();

        Cursor c = db.rawQuery( "select * from Rating " +
                "where Username = '" + user.getName() + "' and MovieID = '" + movieID + "';", null );
        if( c.getCount() != 0 )
        {
            ContentValues cv = new ContentValues();

            cv.put( "Username", user.getName() );
            cv.put( "MovieID", movieID );
            cv.put( "Score", rating );

            db2.update( "Rating", cv, "Username = '" + user.getName() +
                    "' and MovieID = '" + movieID + "'", null );
        }
         else
         {
             ContentValues cv = new ContentValues();

             cv.put( "Username", user.getName() );
             cv.put( "MovieID", movieID );
             cv.put( "Score", rating );

             db2.insert( "Rating", null, cv );
         }

        db.close();
        db2.close();
    }

    /*********************************************************************************************
     MOVIE METHODS
     *********************************************************************************************/

    @Override
    public List<Movie> doSearch( String kiiword, Context gc )
    {
        List<Movie> searchResults = new ArrayList<Movie>();

        SQLiteDatabase db = getReadableDatabase();
        String keywordsPlural[] = kiiword.split(" ");

        StringBuilder queryBuilder = new StringBuilder( "select * from Movie where " );
        int i=0;
        while( i<keywordsPlural.length-1 )
        {
            queryBuilder.append( "Title collate nocase = '"+keywordsPlural[i]+"' " +
                    "or Title collate nocase like '% "+keywordsPlural[i]+"' " +
                    "or Title collate nocase like '"+keywordsPlural[i]+" %' " +
                    "or Title collate nocase like '% "+keywordsPlural[i]+" %' and " );
            ++i;
        }
        queryBuilder.append("Title collate nocase = '" + keywordsPlural[i] + "' " +
                "or Title collate nocase like '% " + keywordsPlural[i] + "' " +
                "or Title collate nocase like '" + keywordsPlural[i] + " %' " +
                "or Title collate nocase like '% " + keywordsPlural[i] + " %';");
        String query = queryBuilder.toString();

        Cursor c = db.rawQuery( query, null );

        while( c.moveToNext() )
        {
            String omdbid = c.getString(0), title = c.getString(1), year = c.getString(2),
                    shortplot = c.getString(3), fullplot = c.getString(4), posterURL = c.getString(5);

            searchResults.add( new Movie( title, year, omdbid, shortplot, fullplot, posterURL ) );
        }

        db.close();
        return searchResults;
    }

    public void addMovieList( List<Movie> toAdd )
    {
        SQLiteDatabase db = getWritableDatabase();

        for( Movie movie : toAdd )
        {
                ContentValues cv = new ContentValues();

                cv.put("MovieID", movie.getID());
                cv.put("Title", movie.getTitle());
                cv.put("Year", movie.getYear());
                cv.put("Short", movie.getShortPlot());
                cv.put("Full", movie.getFullPlot());
                cv.put("PosterURL", movie.getPosterURL());
            try
            {   db.insert("Movie", null, cv);   }
            catch( Exception e ){}
        }

        db.close();
    }

    /*********************************************************************************************
     PARTY METHODS
     *********************************************************************************************/

    public String initPartyStaticID()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery( "select PartyID from Party;", null );
        //  looking at an ASCII table, "@" comes immediately before "A"
        String ID = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA@";

        while( c.moveToNext() )
        {
            if( c.getString(0).compareTo( ID ) > 0 )
            {   ID = c.getString(0);    }
        }

        db.close();

        return ID;
    }

    public List<Party> getParties()
    {

        SQLiteDatabase db = getReadableDatabase();

        List<Party> parties = new ArrayList<Party>();
        String currentUser = m.getCurrentUser();
        ArrayList<String> partyIDs = new ArrayList<String>();

        //  first, let's just find party IDs of parties the user has hosted or been invited to
        String query = "select distinct PartyID from Party " +
                "where Host = '" + currentUser + "' or exists " +
                "            (select Username from Invited " +
                "             where Party.PartyID = Invited.PartyID and " +
                "             Username = '" + currentUser + "');";

        try
        {
            Cursor c = db.rawQuery( query, null );

            int i = 0;

            while (c.moveToNext())
            {
                partyIDs.add( i++, c.getString(0) );
            }

            for( String ID : partyIDs )
            {
                c = db.rawQuery( "select MovieID, Host, Date, Time, Venue " +
                        "from Party where PartyID = '" + ID + "';", null );
                while( c.moveToNext() )
                {
                    String MovieID = c.getString(0), host = c.getString(1), date = c.getString(2),
                            time = c.getString(3), venue = c.getString(4);

                    //  we need the second constructor now, so as not to generate a new ID!!!
                    Party party = new Party( ID, host, MovieID, date, time, venue );

                    //  next, let's make sure the Movie exists in the Model
                    Cursor d = db.rawQuery( "select * from Movie where MovieID = '" + MovieID + "';", null );

                    while( d.moveToNext() )
                    {
                        String title = d.getString(1), year = d.getString(2), shortplot = d.getString(3),
                                fullplot = d.getString(4), posterURL = d.getString(5);

                        m.addMovieFromDB( new Movie( title, year, MovieID, shortplot, fullplot, posterURL ) );
                    }

                    //  Now we need to make sure that the Party has the correct list of invitees
                    d = db.rawQuery( "select * from Invited where PartyID = '" + ID + "';", null );
                    List<Invite> invitations = new ArrayList<Invite>();
                    while( d.moveToNext() )
                    {
                        String username = d.getString(1);
                        Cursor e = db.rawQuery( "select * from User where Name = '" + username + "';", null );
                        while( e.moveToNext() )
                        {
                            String phNo = e.getString(1);
                            User user = new User( username, phNo );
                            Invite invite = new Invite( user );
                            int accepted = d.getInt(2);
                            switch( accepted )
                            {
                                case 1:
                                {
                                    String acc = "@string/accept";
                                    invite.setAcceptance(acc);
                                    break;
                                }
                                case 2:
                                {
                                    String acc = "@string/maybe";
                                    invite.setAcceptance(acc);
                                    break;
                                }
                                case 3:
                                {
                                    String acc = "@string/decline";
                                    invite.setAcceptance(acc);
                                    break;
                                }
                                default:
                            }
                            invitations.add( invite );
                        }
                    }
                    //  after all that, set the invitations to the party
                    party.setInvitesFromDB( invitations );
                    parties.add( party );
                }
            }
        }
        catch( Exception e ){}

        m.setParties(parties);

        db.close();

        return parties;
    }

    public void insertParty( Party party )
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        String partyID = party.getID();

        cv.put( "PartyID", partyID );
        cv.put( "MovieID", party.getMovie().getID() );
        cv.put( "Host", party.getHost() );
        cv.put( "Date", party.getDate() );
        cv.put( "Time", party.getTime() );
        cv.put( "Venue", party.getVenue() );

        try
        {   db.insert( "Party", null, cv );     }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        List<User> invitees = party.getInvitedUsers();

        for( User partyAnimal : invitees )
        {
            cv = new ContentValues();
            cv.put( "PartyId", partyID );
            cv.put( "Username", partyAnimal.getName() );
            /*
                 0 => not replied
                 1 => accepted
                 2 => maybeed
                 3 => declined
             */
            cv.put( "Accepted", 0 );

            try
            {   db.insert( "Invited", null, cv );   }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }

        db.close();
    }

    public void changePartyDetails( Party party, String date, String time, String venue )
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put( "Date", date );
        cv.put( "Time",  time);
        cv.put( "Venue", venue );

        db.update("Party", cv, "PartyID = '" + party.getID() + "'", null);
    }

    public void changeMovie( String omdbid, Party party )
    {

    }

    public void addInvitee( User invitee, Party party )
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put( "PartyID", party.getID() );
        cv.put( "Username", invitee.getName() );
        cv.put( "Accepted", 0 );

        try
        {   db.insert( "Invited", null, cv );   }
        catch( Exception e ){}

        db.close();
    }

    public void changeAcceptance( String partyID, String username, int accepted )
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put( "PartyID", partyID );
        cv.put( "Username", username );
        cv.put( "Accepted", accepted );

        db.update("Invited", cv, "Username = '" + username +
                "' and PartyID = '" + partyID + "'", null);
    }

    public int getAcceptance( String username, Party party )
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select Accepted from Invited " +
                "where Username = '" + username + "' and PartyID = '" + party.getID() + "';", null);
        int accepted = -1;

        while( c.moveToNext() )
        {   accepted = c.getInt(0); }

        return accepted;
    }
}
