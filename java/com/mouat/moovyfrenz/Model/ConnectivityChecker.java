package com.mouat.moovyfrenz.Model;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

/**
 * Created by Andrew on 14/10/2015.
 */
public class ConnectivityChecker extends Activity
{
    IntentFilter IF;
    BroadcastReceiver br;

    public ConnectivityChecker( Context gc )
    {
        ConnectivityManager cm = (ConnectivityManager) gc.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null)
        {
            Mediator.getInstance().setConnected( ni.isConnected() );
        }

        br = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Bundle passedIn = intent.getExtras();
                NetworkInfo ni = passedIn.getParcelable( "networkInfo" );

                if( ni != null )
                {
                    NetworkInfo.State connected = ni.getState();

                    if( connected == NetworkInfo.State.CONNECTED )
                    {
                        Mediator.getInstance().setConnected( true );
                    }
                    else
                    {
                        Mediator.getInstance().setConnected( false );
                    }
                }
            }
        };

        final IntentFilter IF = new IntentFilter();
        IF.addAction( ConnectivityManager.CONNECTIVITY_ACTION );
        gc.registerReceiver( br, IF );
    }

    public BroadcastReceiver getReceiver()
    {   return br;  }
}
