package com.mouat.moovyfrenz.View;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mouat.moovyfrenz.Controller.NewInviteesAA;
import com.mouat.moovyfrenz.Model.Databaser;
import com.mouat.moovyfrenz.Model.Mediator;
import com.mouat.moovyfrenz.Model.Movie;
import com.mouat.moovyfrenz.Model.Party;
import com.mouat.moovyfrenz.Model.User;
import com.mouat.moovyfrenz.R;

import java.util.ArrayList;
import java.util.List;

public class CreateInvitees extends Activity
{
    private final CreateInvitees ci = this;
    private Mediator mediator = Mediator.getInstance();
    private Movie selected;
    private BroadcastReceiver br;

    List<User> invitees = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_invitees);

        br = mediator.setConnChecker( this );

        Intent recInt = getIntent();
        selected = mediator.getMovie(recInt.getStringExtra("omdbid"));

        String omdbid = recInt.getStringExtra( "omdbid" );
        String date = recInt.getStringExtra( "date" );
        String time = recInt.getStringExtra( "time" );
        String venue = recInt.getStringExtra( "venue" );
        String partyID = recInt.getStringExtra( "partyID" );

        final Party party = mediator.findParty( partyID, ci );
        if( party != null )
        {   invitees = party.getInvitedUsers();     }

        final ArrayAdapter<User> inviteesAdapter = new NewInviteesAA( ci, invitees );
        ListView inviteesLV = (ListView) findViewById( R.id.inviteesLV );
        inviteesLV.setAdapter( inviteesAdapter );

        Button inviteMore = (Button) findViewById( R.id.inviteMore );
        TextView nameLabel = (TextView) findViewById( R.id.newInviteeNameLabel );
        EditText nameText = (EditText) findViewById( R.id.newInviteeNameText );
        TextView numberLabel = (TextView) findViewById( R.id.newInviteeNumberLabel );
        EditText numberText = (EditText) findViewById( R.id.newInviteeNumberText );
        Button sendAndCreate = (Button) findViewById( R.id.makePartyAndSend );

        if( invitees.size()<1 )
        {   sendAndCreate.setVisibility( View.INVISIBLE );   }

        inviteMore.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    EditText inviteeName = (EditText) findViewById( R.id.newInviteeNameText );
                    EditText inviteeNumber = (EditText) findViewById( R.id.newInviteeNumberText );

                    String name = inviteeName.getText().toString();
                    String number = inviteeNumber.getText().toString();
                    inviteeName.setText( "" );
                    inviteeNumber.setText( "" );

                    User user = mediator.getUser( name );

                    if( user == null )
                    {
                        user = new User( name, number );

                        Databaser db = new Databaser( ci );
                        db.insertUser( user );
                    }
                    invitees.add( invitees.size(), user );

                    //Now update the ListView and View
                    ((Button) findViewById( R.id.makePartyAndSend )).setVisibility( View.VISIBLE );
                    inviteesAdapter.notifyDataSetChanged();
                }
            }
        );

        sendAndCreate.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    if( party != null )
                    {   party.changeInvitees( invitees, ci );     }
                    else
                    {   mediator.createParty( invitees, ci );   }

                    Intent returnHome = new Intent( ci, StartOptions.class );
                    returnHome.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity( returnHome );
                }
            }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_invitees, menu);
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
