package com.mouat.moovyfrenz.View;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mouat.moovyfrenz.Controller.PartiesAA;
import com.mouat.moovyfrenz.Model.Mediator;
import com.mouat.moovyfrenz.Model.Party;
import com.mouat.moovyfrenz.R;

import java.util.List;

public class ViewParties extends Activity
{
    private ViewParties vp = this;
    private Mediator mediator = Mediator.getInstance();
    private final String currentUser = mediator.getCurrentUser();
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_parties);

        getShitReady();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        getShitReady();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        getShitReady();
    }

    private void getShitReady()
    {
        br = mediator.setConnChecker(this);

        List<Party> parties = mediator.getParties( vp );

        ArrayAdapter<Party> partiesAdapter =
                new PartiesAA( this, parties );
        ListView partiesLV = (ListView) findViewById( R.id.partiesLV );
        partiesLV.setAdapter(partiesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_parties, menu);
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
