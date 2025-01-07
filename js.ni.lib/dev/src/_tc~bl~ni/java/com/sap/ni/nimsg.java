package com.sap.ni;

import java.io.*;

public class NiMsg 
{
    public byte [] msg;
    public int     len;
    
    public NiMsg ( byte [] array, int length ) 
	{
	    msg = array;
	    len = length;
	}
    public NiMsg ( int length ) 
	{
	    msg = new byte [length];
	    len = length;
	}
    public NiMsg ( ) 
	{
	    msg = null;
	    len = 0;
	}

    public boolean isNiPing () 
	{
	    if (len != NI.NI_PING.length)
		return false;
	    
	    for (int i = 0; i < NI.NI_PING.length; i++)
		if (msg[i] != NI.NI_PING[i])
		    return false;
	    return true;
	}
    public boolean isNiPong () 
	{
	    if (len != NI.NI_PONG.length)
		return false;
	    
	    for (int i = 0; i < NI.NI_PONG.length; i++)
		if (msg[i] != NI.NI_PONG[i])
		    return false;
	    return true;
	}
    public boolean isNiRouteErr () 
	{
	    for (int i = 0; i < NI.NI_RTERR.length; i++)
            {
		if (msg[i] != NI.NI_RTERR[i])
		    return false;
            }
            
            return true;
	}

    public void dump () 
	{
	    System.out.println ( msg );
	}
    
}
