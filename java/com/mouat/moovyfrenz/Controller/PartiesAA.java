package com.mouat.moovyfrenz.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mouat.moovyfrenz.Model.Mediator;
import com.mouat.moovyfrenz.Model.Movie;
import com.mouat.moovyfrenz.Model.Party;
import com.mouat.moovyfrenz.R;
import com.mouat.moovyfrenz.View.PartyDetails;

import java.util.List;

/**
 * Created by Andrew on 30/08/2015.
 */
public class PartiesAA extends ArrayAdapter<Party>
{
    private Mediator mediator = Mediator.getInstance();
    private String currentUser = mediator.getCurrentUser();
    private Context gc;

    public PartiesAA( Context context, List<Party> parties )
    {
        super( context, 0, parties );
        gc = context;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        final Party party = getItem( position );
        final Movie movie = party.getMovie();

        if( convertView == null )
        {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.single_party, parent, false);
        }

        ImageView poster = (ImageView) convertView.findViewById( R.id.poster2 );
        TextView title = (TextView) convertView.findViewById( R.id.title2 );
        TextView year = (TextView) convertView.findViewById( R.id.year2 );
        TextView shortplot = (TextView) convertView.findViewById( R.id.resultsShort2 );
        RatingBar ratingbar = (RatingBar) convertView.findViewById( R.id.ratingbar );
        Button viewEditDetails = (Button) convertView.findViewById( R.id.viewEditDetails );

        //  The next 8 lines of code resize the image to 1/3 of the screen width   ;)
        DisplayMetrics dm = new DisplayMetrics();
        int posterMaxWidth;
        ((Activity) convertView.getContext()).getWindowManager().getDefaultDisplay().getMetrics( dm );
        posterMaxWidth = dm.widthPixels / 3;

        poster.setAdjustViewBounds(true);
        poster.setMaxWidth(posterMaxWidth);
        poster.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        Bitmap posterImage = movie.getPoster();

        if( posterImage != null )
        {   poster.setImageBitmap( posterImage );   }
        else
        {   poster.setImageResource( R.drawable.noposter);     }

        title.setText( movie.getTitle() );
        year.setText( movie.getYear() );
        shortplot.setText( movie.getShortPlot() );

        float rating = mediator.getUser( currentUser ).getRating( movie, gc );
        if( rating>=0 )
        {   ratingbar.setRating( rating );  }
        ratingbar.setOnRatingBarChangeListener
        (
            new RatingBar.OnRatingBarChangeListener()
            {
                @Override
                public void onRatingChanged( RatingBar ratingbar, float rating, boolean fromUser )
                {
                    if( fromUser )
                    {   mediator.getUser( currentUser ).setRating( movie.getID(), rating, gc ); }
                }
            }
        );

        String currentUserIsHost;
        if( party.getHost().compareTo( currentUser ) == 0 )
        {
            viewEditDetails.setText( R.string.editDetails );
            currentUserIsHost = "@string/switchToViewParties1";
        }
        else
        {
            viewEditDetails.setText( R.string.viewDetails );
            currentUserIsHost = "@string/switchToViewParties2";
        }

        final Context justBecause = (Activity) convertView.getContext();
        final String anotherJustBecause = currentUserIsHost;
        viewEditDetails.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    Intent viewDetails = new Intent( justBecause, PartyDetails.class );
                    viewDetails.putExtra("title", movie.getTitle())
                            .putExtra("year", movie.getYear())
                            .putExtra("shortplot", movie.getShortPlot())
                            .putExtra("fullplot", movie.getFullPlot())
                            .putExtra("previousScreen", anotherJustBecause )
                            .putExtra("omdbid", movie.getID()).putExtra( "partyID", party.getID() )
                            .putExtra("posterURL",movie.getPosterURL());
                    justBecause.startActivity( viewDetails );
                }
            }
        );

        return convertView;
    }
}
