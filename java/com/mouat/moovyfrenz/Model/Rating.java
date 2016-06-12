package com.mouat.moovyfrenz.Model;

/**
 * Created by Andrew on 16/08/2015.
 */
public class Rating
{
    private String imdbid;
    private float rating;

    public Rating( String imdbid, float rating )
    {
        this.imdbid = imdbid;
        this.rating = rating;
    }

    public String getID()
    {   return imdbid;  }

    public float getRating()
    {   return rating;  }

    public void setRating( float rating )
    {   this.rating = rating;   }
}
