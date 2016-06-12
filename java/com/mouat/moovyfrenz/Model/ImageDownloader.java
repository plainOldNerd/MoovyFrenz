package com.mouat.moovyfrenz.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Andrew on 14/10/2015.
 */
public class ImageDownloader extends Thread
{
    private Movie movie;
    private String url;
    private Bitmap poster = null;

    public ImageDownloader( Movie movie, String posterURL )
    {
        this.movie = movie;
        url = posterURL;
    }

    @Override
    public void run()
    {
        if( Mediator.getInstance().isConnected() )
        {
            if (url.compareTo("N/A") != 0)
            {
                try
                {
                    URL posterAt = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) posterAt.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream in = conn.getInputStream();
                    poster = BitmapFactory.decodeStream( in );
                    conn.disconnect();
                }
                catch (Exception e)
                {}

                movie.setPoster( poster );
            }
        }
        else
        {
            PendingOnlineTask downloadImage = new PendingOnlineTask( "downloadImage", url, movie );
            Mediator.getInstance().addPendingTask( downloadImage );
        }
    }
}
