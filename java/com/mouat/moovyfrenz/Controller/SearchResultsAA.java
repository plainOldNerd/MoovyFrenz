package com.mouat.moovyfrenz.Controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mouat.moovyfrenz.Model.Movie;
import com.mouat.moovyfrenz.R;

import java.util.List;

/**
 * Created by Andrew on 27/08/2015.
 */
public class SearchResultsAA extends ArrayAdapter<Movie>
{
    public SearchResultsAA( Context context, List<Movie> searchResults )
    {   super( context, 0, searchResults );     }

    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        Movie result = getItem( position );

        if( convertView == null )
        {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.single_search_result, parent, false);
        }

        ImageView poster = (ImageView) convertView.findViewById( R.id.poster );
        TextView title = (TextView) convertView.findViewById( R.id.title );
        TextView year = (TextView) convertView.findViewById( R.id.year );
        TextView shortplot = (TextView) convertView.findViewById( R.id.resultsShort );

    //  The next 8 lines of code resize the image to 1/3 of the screen width   ;)
        DisplayMetrics dm = new DisplayMetrics();
        int posterMaxWidth;
        ((Activity) convertView.getContext()).getWindowManager().getDefaultDisplay().getMetrics( dm );
        posterMaxWidth = dm.widthPixels / 3;

        poster.setAdjustViewBounds(true);
        poster.setMaxWidth(posterMaxWidth);
        poster.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        //  set the image to be the poster if it is not null, or a generic "image doesn't
        //  exist" indicative image
        Bitmap posterImage = result.getPoster();
        if( posterImage != null )
        {   poster.setImageBitmap(posterImage);   }
        else
        {   poster.setImageResource( R.drawable.noposter);     }

        title.setText(result.getTitle());
        year.setText( result.getYear() );
        shortplot.setText( result.getShortPlot() );

        return convertView;
    }
}
