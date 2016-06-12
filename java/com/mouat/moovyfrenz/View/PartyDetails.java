package com.mouat.moovyfrenz.View;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mouat.moovyfrenz.Model.Mediator;
import com.mouat.moovyfrenz.Model.Movie;
import com.mouat.moovyfrenz.Model.Party;
import com.mouat.moovyfrenz.R;

import java.util.Calendar;
import java.util.Scanner;

public class PartyDetails extends Activity
{
    private PartyDetails pd = this;
    Mediator mediator = Mediator.getInstance();
    private BroadcastReceiver br;

    private String omdbid, movieTitle, movieYear, movieShortplot, movieFullplot, previousScreen, posterURL;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party_details);

        getShitReady();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        getShitReady();
    }

    private void getShitReady()
    {
        br = mediator.setConnChecker( this );

        Intent recInt = getIntent();
    //  We may have come to this Activity via a Searchresults Activity, or via an "View details" button
        previousScreen = recInt.getStringExtra("previousScreen");

        final Party party = mediator.findParty( recInt.getStringExtra( "partyID" ), pd );
        omdbid = recInt.getStringExtra( "omdbid" );
        movieTitle = recInt.getStringExtra( "title" );
        movieYear = recInt.getStringExtra( "year" );
        movieShortplot = recInt.getStringExtra( "shortplot" );
        movieFullplot = recInt.getStringExtra("fullplot");
        posterURL = recInt.getStringExtra("posterURL");

        ImageView poster = (ImageView) findViewById(R.id.poster2);
        TextView title = (TextView) findViewById(R.id.title2);
        TextView year = (TextView) findViewById(R.id.year2);
        final TextView plot = (TextView) findViewById(R.id.plot);

        final EditText DateField = (EditText) findViewById(R.id.DateText);
        final EditText TimeField = (EditText) findViewById(R.id.TimeText);
        final EditText VenueField = (EditText) findViewById(R.id.VenueText);

        final RatingBar ratingbar = (RatingBar) findViewById(R.id.ratingOnDetails);

        final Movie thisMovie = mediator.getMovie(omdbid);
        float rating = mediator.getUser(mediator.getCurrentUser()).getRating( thisMovie, pd );

        if( rating>=0 )
        {    ratingbar.setRating(rating); }

        ratingbar.setOnRatingBarChangeListener
        (
            new RatingBar.OnRatingBarChangeListener()
            {
                @Override
                public void onRatingChanged(RatingBar ratingbar, float rating, boolean fromUser)
                {   mediator.getUser(mediator.getCurrentUser()).setRating(thisMovie.getID(), rating, pd );   }
            }
        );

        final Button inviteFriends = (Button) findViewById( R.id.startInvites );

        if( party != null )
        {
            DateField.setText( party.getDate() );
            TimeField.setText( party.getTime() );
            VenueField.setText( party.getVenue() );
        }

        DateField.setInputType(InputType.TYPE_NULL);

        final DatePickerDialog.OnDateSetListener odsl = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet( DatePicker dp, int year, int month, int day )
            {
                String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                    "Oct", "Nov", "Dec" };
                DateField.setText( Integer.toString( day ) + " " + months[month] + " " +
                    Integer.toString( year ) );
            }
        };
        final Calendar c = Calendar.getInstance();

        final View.OnClickListener dateOCL =
            new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    int startYear, startMonth, startDay;
                    String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                            "Sep", "Oct", "Nov", "Dec" };
                    String date = DateField.getText().toString();

                    if( date.compareTo( "" ) == 0 )
                    {
                        startYear = c.get( Calendar.YEAR );
                        startMonth = c.get( Calendar.MONTH );
                        startDay = c.get( Calendar.DATE );
                    }
                    else
                    {
                        Scanner sc = new Scanner( date );

                        startDay = sc.nextInt();
                        String startMonthCompar = sc.next();
                        startMonth = -1;
                        for( int i=0; i<12; ++i )
                        {
                            if( startMonthCompar.compareTo( months[i] ) == 0 )
                                startMonth = i;
                        }
                        startYear = sc.nextInt();
                    }

                    DatePickerDialog dpd = new DatePickerDialog( pd, 3, odsl, startYear, startMonth,
                            startDay );
                    DatePicker dp = dpd.getDatePicker();
                    dp.setMinDate( c.getTimeInMillis() );
                    dpd.show();
                }
            };

        View.OnFocusChangeListener dateOFCL =
            new View.OnFocusChangeListener()
            {
                @Override
                public void onFocusChange (View v,boolean hasFocus)
                {
                    if( v.hasFocus() )
                        dateOCL.onClick( v );
                }
            };

        DateField.setOnFocusChangeListener( dateOFCL );
        DateField.setOnClickListener( dateOCL );

        TimeField.setInputType(InputType.TYPE_NULL);

        final TimePickerDialog.OnTimeSetListener otsl =
            new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                {
                    String ampm;

                    if( hourOfDay >= 12 )
                    {
                        ampm = "PM";
                        hourOfDay -= 12;
                    }
                    else
                        ampm = "AM";

                    if( hourOfDay == 0 )
                        hourOfDay = 12;

                    TimeField.setText(Integer.toString(hourOfDay) + ":" +
                        String.format("%02d", minute) + " " + ampm);
                }
            };

        final View.OnClickListener timeOCL =
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    String starterTime = TimeField.getText().toString(), ampm;
                    int starterHour, starterMinute;

                    if (starterTime.compareTo("") == 0)
                    {
                        Calendar c = Calendar.getInstance();

                        starterHour = c.get(Calendar.HOUR);
                        starterMinute = c.get(Calendar.MINUTE);

                        if (c.get(Calendar.AM_PM) == Calendar.PM)
                            starterHour += 12;
                    }
                    else
                    {
                        Scanner sc = new Scanner(starterTime.replace(':', ' '));
                        starterHour = sc.nextInt();
                        starterMinute = sc.nextInt();
                        ampm = sc.next();

                        if (ampm.compareTo("PM") == 0)
                            starterHour += 12;
                    }

                        TimePickerDialog tpd = new TimePickerDialog( pd, 3, otsl,
                            starterHour, starterMinute, false);
                        tpd.show();
                }
            };

        View.OnFocusChangeListener timeOFCL =
            new View.OnFocusChangeListener()
            {
                @Override
                public void onFocusChange( View v, boolean hasFocus )
                {
                    if( hasFocus )
                        timeOCL.onClick( v );
                }
            };

        TimeField.setOnFocusChangeListener( timeOFCL );
        TimeField.setOnClickListener( timeOCL );

        inviteFriends.setOnClickListener
        (
            new View.OnClickListener()
            {
                public void onClick(View v) {
                    String date = DateField.getText().toString();
                    String time = TimeField.getText().toString();
                    String venue = VenueField.getText().toString();

                    Intent getInvitees = new Intent(pd, CreateInvitees.class)
                            .putExtra("omdbid", omdbid).putExtra("date",date).putExtra("time",time)
                            .putExtra("venue",venue);

                    if( party != null )
                    {
                        party.changeDetails( date, time, venue, pd );
                        /*
                        party.changeDate( date, pd );
                        party.changeTime( time, pd );
                        party.changeVenue( venue, pd );
                        */
                        getInvitees.putExtra( "partyID", party.getID() );
                    }
                    else
                    {
                        mediator.newMovie( movieTitle, movieYear, omdbid, movieShortplot,
                                movieFullplot, posterURL );

                        mediator.newParty(omdbid, date, time, venue );

                        getInvitees.putExtra( "partyID", "" );
                    }

                    startActivity( getInvitees );
                }
            }
        );

        final Button accept = (Button) findViewById( R.id.accept );
        final Button maybe = (Button) findViewById( R.id.maybe );
        final Button decline = (Button) findViewById( R.id.decline );
        accept.setBackgroundResource( android.R.drawable.btn_default );
        maybe.setBackgroundResource( android.R.drawable.btn_default );
        decline.setBackgroundResource( android.R.drawable.btn_default );

        accept.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick( View v ) {
                    mediator.replyInvite( party, "@string/accept", pd );
                    accept.setBackgroundColor( Color.GREEN );
                    maybe.setBackgroundResource( android.R.drawable.btn_default );
                    decline.setBackgroundResource( android.R.drawable.btn_default );
                }
            }
        );
        if( party != null && party.hasAccepted( pd ) )
        {   accept.setBackgroundColor( Color.GREEN );     }

        maybe.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    mediator.replyInvite( party, "@string/maybe", pd );
                    accept.setBackgroundResource( android.R.drawable.btn_default );
                    maybe.setBackgroundColor( Color.GREEN );
                    decline.setBackgroundResource( android.R.drawable.btn_default );
                }
            }
        );
        if( party != null && party.hasMaybeed( pd ) )
        {   maybe.setBackgroundColor( Color.GREEN );  }

        decline.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    mediator.replyInvite( party, "@string/decline", pd );
                    accept.setBackgroundResource( android.R.drawable.btn_default );
                    maybe.setBackgroundResource( android.R.drawable.btn_default );
                    decline.setBackgroundColor( Color.GREEN );
                }
            }
        );
        if( party != null && party.hasDeclined( pd ) )
        {   decline.setBackgroundColor( Color.GREEN );    }

    //  The next 8 lines of code resize the image to 1/3 of the screen width   ;)
        DisplayMetrics dm = new DisplayMetrics();
        int posterMaxWidth;
        getWindowManager().getDefaultDisplay().getMetrics( dm );
        posterMaxWidth = dm.widthPixels / 3;

        poster.setAdjustViewBounds(true);
        poster.setMaxWidth(posterMaxWidth);
        poster.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        if( thisMovie.getPoster() != null )
        {   poster.setImageBitmap( thisMovie.getPoster() );     }
        else
        {   poster.setImageResource( R.drawable.noposter);  }

        title.setText(movieTitle);
        year.setText(movieYear);
        plot.setText(movieShortplot);

        ((Button) findViewById(R.id.shortFullButton)).setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (((Button) v).getText().toString()
                            .compareTo(getResources().getText(R.string.fullPlot).toString()) == 0)
                    {
                        plot.setText(movieFullplot);
                        ((Button) v).setText(getResources().getText(R.string.shortPlot));
                    }
                    else
                    {
                        plot.setText(movieShortplot);
                        ((Button) v).setText(getResources().getText(R.string.fullPlot));
                    }
                }
            }
        );

    //  If we are coming to this screen from SearchResults
        if( previousScreen.compareTo( "@string/comingFromCreate" ) == 0 )
        {
            ratingbar.setVisibility(View.INVISIBLE);

            inviteFriends.setVisibility(View.VISIBLE);
            accept.setVisibility(View.INVISIBLE);
            maybe.setVisibility(View.INVISIBLE);
            decline.setVisibility(View.INVISIBLE);

            DateField.setEnabled(true);
            TimeField.setEnabled(true);
            VenueField.setEnabled(true);
        }
        if( previousScreen.compareTo( "@string/switchToViewParties1" ) == 0 )
    //  We have clicked "Edit Details" in the previous screen
        {
            ratingbar.setVisibility(View.VISIBLE);

            inviteFriends.setVisibility(View.VISIBLE);
            accept.setVisibility(View.INVISIBLE);
            maybe.setVisibility(View.INVISIBLE);
            decline.setVisibility(View.INVISIBLE);

            DateField.setEnabled(true);
            TimeField.setEnabled(true);
            VenueField.setEnabled(true);
        }
        if( previousScreen.compareTo( "@string/switchToViewParties2" ) == 0 )
    //  Otherwise we are coming from a "View Details" button
        {
            ratingbar.setVisibility(View.VISIBLE);
            inviteFriends.setVisibility(View.INVISIBLE);
            accept.setVisibility(View.VISIBLE);
            maybe.setVisibility(View.VISIBLE);
            decline.setVisibility(View.VISIBLE);

            DateField.setEnabled(false);
            TimeField.setEnabled(false);
            VenueField.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.party_details, menu);
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
