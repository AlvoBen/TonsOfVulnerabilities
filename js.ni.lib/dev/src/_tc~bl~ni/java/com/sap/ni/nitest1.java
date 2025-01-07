package com.sap.ni;

import com.sap.ni.NiServer;
import com.sap.ni.NiHandle;
import com.sap.ni.NiMsg;
import com.sap.ni.NiRoute;
import com.sap.ni.*;
import java.io.*;
import java.net.*;


public class NiTest1
{
    public static void main ( String args [] ) throws IOException
	{
	    NiHandle h;
	    NiRoute r1;
	    int tracelevel = 3;

	    if (args.length < 1) {
		System.out.println ( "invalid arguments : " + args );
		System.exit ( 1 );
	    }

            if (args.length > 1)
            {
              if (null != args[1]){
		tracelevel = Integer.parseInt(args[1]);
              }
            }

	    byte [] buffer;
	    int len;
	    try {
		h = new NiHandle ( args[0], tracelevel, NI.NI_TALK_NI );
                NiMsg m = new NiMsg();
                h.recv( m);
                h.niPing ();
                
	    }
	    catch (com.sap.ni.NiException  e)
            {
 		System.out.println( e.getMessage () );
	    }
        }
    
}



