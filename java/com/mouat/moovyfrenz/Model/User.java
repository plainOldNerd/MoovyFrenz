package com.mouat.moovyfrenz.Model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 16/08/2015.
 */
public class User
{
    private String name = null, phNo = null;
    private List<Rating> ratings = new ArrayList<Rating>();

    public User( String name, String number )
    {
        this.name = name;
        phNo = number;
    }

    public String getName()
    {   return name;    }

    public String getNumber()
    {   return phNo;    }

    public void setRating( String movieID, float newRating, Context gc )
    {   new Databaser( gc ).setRating( this, movieID, newRating );      }

    public float getRating( Movie movie, Context gc )
    {   return new Databaser( gc ).getRating( this, movie );    }
}
