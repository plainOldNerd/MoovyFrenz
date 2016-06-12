package com.mouat.moovyfrenz.Model;

import android.graphics.Bitmap;

/**
 * Created by Andrew on 16/08/2015.
 */
public class Movie
{
    private String title, year, omdbid, shortplot, fullplot, posterURL;
    private Bitmap poster = null;

    public Movie( String title, String year, String imdbID, String shortplot, String fullplot, String posterURL )
    {
        this.title = title;
        this.year = year;
        omdbid = imdbID;
        this.shortplot = shortplot;
        this.fullplot = fullplot;
        this.posterURL = posterURL;

        new ImageDownloader( this, posterURL ).start();
    }

    public String getTitle()
    {   return title;   }

    public String getYear()
    {   return year;    }

    public String getID()
    {   return omdbid;  }

    public String getShortPlot()
    {   return shortplot;   }

    public String getFullPlot()
    {   return fullplot;    }

    public String getPosterURL()
    {   return posterURL;   }

    public void setPoster( Bitmap poster )
    {   this.poster = poster;   }

    public Bitmap getPoster()
    {   return poster;  }
}
