package com.sap.ni;

import java.net.*;
import java.io.*;

public class NiServer
{
    private ServerSocket socket;
    private int state;
    
    public NiServer(int port)  throws IOException
	{
	    socket = new ServerSocket(port);
	}
    
    public NiHandle accept () 
	{
	    NiHandle h = null;
	    
	    try {
		h = new NiHandle(socket.accept());
	    } catch ( IOException e ) {
		System.out.println ( " Exception suring accept (): " + e );
	    }
	    
	    return h;
	}
    
}

	    
