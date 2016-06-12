package com.mouat.moovyfrenz.Model;

/**
 * Created by Andrew on 16/08/2015.
 */
public class Invite
{
    private User invitee;
    private boolean accepted = false, declined = false, maybe = false;

    public Invite( User invitee )
    {
        this.invitee = invitee;
    }

    public User getUser()
    {   return invitee;    }

    public void setAcceptance( String acceptance ) {
        accepted = false;
        declined = false;
        maybe = false;

        switch (acceptance)
        {
            case "@string/accept":
            {
                accepted = true;
                break;
            }
            case "@string/maybe":
            {
                maybe = true;
                break;
            }
            case "@string/decline":
            {
                declined = true;
            }
            default:
        }
    }
}
