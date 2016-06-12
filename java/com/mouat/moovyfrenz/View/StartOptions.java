package com.mouat.moovyfrenz.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mouat.moovyfrenz.Model.Mediator;
import com.mouat.moovyfrenz.R;

public class StartOptions extends Activity
{
    private StartOptions so = this;
    //  Initialise the Mediator
    private Mediator m = Mediator.getInstance();
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_options);

        br = m.setConnChecker( this );

        m.initPartyID( this );

        findViewById(R.id.myParties).setOnClickListener
        (
            new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Intent viewParties = new Intent( so, ViewParties.class );
                    startActivity( viewParties );
                }
            }
        );
        findViewById(R.id.createParty).setOnClickListener
        (
            new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Intent searchForNew = new Intent( so, SearchMovies.class );
                    startActivity( searchForNew );
                }
            }
        );
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Intent searchfailed = getIntent();
        String errorMessage = searchfailed.getStringExtra( "noCanDoSearch" );

        if( errorMessage != null )
        {
            AlertDialog dialog = new AlertDialog.Builder( this ).create();
            dialog.setMessage( errorMessage );
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {   dialog.dismiss();   }
                    });
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_options, menu);
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
