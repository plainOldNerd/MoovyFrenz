package com.mouat.moovyfrenz.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mouat.moovyfrenz.Model.User;
import com.mouat.moovyfrenz.R;

import java.util.List;

/**
 * Created by Andrew on 29/08/2015.
 */
public class NewInviteesAA extends ArrayAdapter<User>
{
    public NewInviteesAA( Context context, List<User> newInvitees )
    {   super( context, 0, newInvitees );   }

    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        if( convertView == null )
        {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.single_invitee, parent, false);
        }

        User invitee = getItem(position);
        TextView tv = (TextView) convertView.findViewById( R.id.inviteeName );
        tv.setText( invitee.getName() );

        return tv;
    }
}
