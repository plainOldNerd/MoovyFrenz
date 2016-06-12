package com.mouat.moovyfrenz.Model;

import android.content.Context;

import java.util.List;

/**
 * Created by Andrew on 9/10/2015.
 */
public interface Searcher
{
    public List<Movie> doSearch( String keyword, Context gc );
}
