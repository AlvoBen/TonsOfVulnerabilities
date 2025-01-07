package com.sap.ni;

import java.net.*;
import java.io.*;
import java.nio.channels.SocketChannel;

public class NiHandle
{

    private Socket socket;
    private NiRoute route;

    private int talkmode;
    private int state;

    private boolean waitForPong;

    private int tracelevel;


    public NiHandle(Socket s, int tl, byte talkmode_param)
	{
	    socket = s;
	    talkmode = talkmode_param;
	    waitForPong = false;
	    tracelevel = tl;
	}

    public NiHandle(Socket s, int tl)
	{
            this( s, tl, NI.NI_TALK_NATIVE2 );
	}

    public NiHandle(Socket s)
	{
	    this (s, 2);
	}

    public NiHandle ( String s, int tl, byte talkmode_param ) throws UnknownHostException,
	IOException,
	NiException
	{
	    InetAddress ip;
	    int         port;
	    tracelevel = tl;

	    route = new NiRoute ( s, talkmode_param );
	    if (tracelevel >= 3)
		route.printIt ();

	    ip   = route.getNextHost();
	    port = route.getNextPort ();

	    try
            {
		socket = new Socket ( ip, port) ;
		if ( route.getRestEntries () > 0) {
		    NiMsg m = new NiMsg ();
		    waitForPong = true;
		    send ( route.toStream () );
		    recv(m);
                    trace ( "Route ok!" );
		} else {
		    trace ( " no route " );
		}
	    }
	    catch ( ConnectException e ){
		trace (1,  " Connect Exception caught " );
		throw new NiException ( e );
	    }
	    waitForPong = false;
	}

  public NiHandle (String s, int tl, byte talkmode_param, boolean isNio) throws UnknownHostException, 	IOException,	NiException	{
    InetAddress ip;
    int port;
    tracelevel = tl;

    route = new NiRoute (s, talkmode_param);
    if (tracelevel >= 3) {
      route.printIt ();
    }

    ip   = route.getNextHost();
    port = route.getNextPort ();

    try {
      socket = SocketChannel.open(new InetSocketAddress(ip, port)).socket();
      if ( route.getRestEntries () > 0) {
        NiMsg m = new NiMsg();
        waitForPong = true;
        send (route.toStream());
        recv(m);
        trace ("Route ok!");
      } else {
        trace (" no route ");
      }
    }
    catch (ConnectException e) {
      trace (1,  " Connect Exception caught " );
      throw new NiException ( e );
    }
    waitForPong = false;
  }

    public NiHandle ( String s, int tl ) throws UnknownHostException,
	IOException,
	NiException
	{
            this ( s, tl, NI.NI_TALK_NATIVE2 );
        }
    
    public NiHandle(String s) throws UnknownHostException,
	IOException,
	NiException
	{
	    this (s, 2);
	}


    public Socket getSocket(  )
        {
            return socket;
        }
    
    public void recv( NiMsg m ) throws NiException
	{
	    int len;
            int retLen = 0;
	    InputStream is = null;
	    byte [] len_b = new byte[4];

	    while ( true ) {

		try {
                   while (is == null) // || is.available() != 0)
                    {
                        is = socket.getInputStream();
                    }

          	    is.read(len_b, 0, 4);
		    len = NI.hostInt(len_b);
 		    m.msg = new byte[len];
		    m.len = len;
                    if (len <= 0)
                    {
                        throw new NiException(" Connection closed ");
                    }
                    
                    len = 0;
                    
                    do {		    	
                        retLen = is.read(m.msg, len, m.len - len);
                        len += retLen;
		    } while (retLen >= 0 && len < m.len);

		} catch ( IOException e ) {
		    trace( " Exception during read (): " + e );
		    if (tracelevel >= 3)
			e.printStackTrace ();
		    throw new NiException (e);

		}

                if (m.isNiRouteErr())
                {
                    NiRouteErr err = new NiRouteErr(m);
                    throw new NiException(err.getMessage());
                }
                
		if (m.len != NI.NI_PING.length)
		    break;

		if (m.isNiPing ())
                {
		    trace ( "NiHandle.recv: NI_PING received\n");
		    send(NI.NI_PONG);
		    continue;
		}
		if (m.isNiPong() && waitForPong == true) {
		    waitForPong = false;
		    return;
		}
	    }
	    return;
	}

    public void send ( byte [] buffer )  throws NiException
	{
	    int len = buffer.length;
	    byte [] b = NI.netInt(len);

	    try  {
		OutputStream os = socket.getOutputStream();
		os.write ( b );
		os.write ( buffer );
	    } catch ( IOException e ) {
		trace ( " Exception during send (): " + e );
		throw new NiException (e);
	    }

	}

    public void niPing ()
	{
	    NiMsg m = new NiMsg ();
	    try {
		send(NI.NI_PING);
		trace ( "NI_PING sent" );
		waitForPong = true;
		recv(m);
	    } catch (NiException e) {
                // $JL-EXC$
	    }

	    if (waitForPong == false) {
		trace ("NI_PONG received\n");
	    }
	    else {
		trace ( "no NI_PONG received\n" );
		m.dump ();
	    }

	}

    public void trace ( int level, String s )
	{
	    if ( tracelevel >= level ) {
		//System.out.println ( s );
	    }

	}

    public void trace ( String s )
	{
	    trace ( 2, s );

	}

    public void setTracelevel ( int level )
	{
	    if (level < 1)
		tracelevel = 1;
	    else if (tracelevel > 3 )
		tracelevel = 3;
	    else
		tracelevel = level;
	}

    public int getTracelevel ( )
	{
	    return tracelevel;
	}

}


