package com.sap.ni;

import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.MalformedURLException;

public class NiRoute
{

    public static byte [] eyecatcher = { 78, 73, 95, 82, 79, 85, 84, 69, 0 };
    public static int VERSION = 2;
    private int  iroute_version;
    private int  ni_version;
    private int  total_entries;
    private byte talkmode;
    private byte options;
    private byte unused;
    private int  rest_entries;
    private int  space_used;
    private int  next_entry;

    private int  next_offset;
    private int total_length;
    Vector  route_entries;

    /**
       construct a route from a character string
    */
    public NiRoute(String s, byte talkmode_param ) throws NiException
	{
	    int count;
	    String hostname = null,
                service = null,
                password = null;
	    String canon;
	    boolean invalid = false;
	    route_entries = new Vector();

	    iroute_version = NiRoute.VERSION;
	    ni_version     = NI.VERSION;

            // parse the route
	    canon = new String (s);
	    StringTokenizer st = new StringTokenizer ( canon, "/" );
	    if ( (count =  st.countTokens()) == 1 ){
		hostname = new String (s);
		System.out.println("hostname = " + hostname );

		//		return;
	    }

	    for ( int i = 0; i < count  && invalid == false; i++ ) {

		String elem = st.nextToken();

		if (elem.length() != 1) {
		    throw new NiException ("route " + s +
					   ": /" + elem + "/ not allowed");
		}

		char c = elem.charAt (0);
		switch ( c ) {
                    case 'h':
                    case 'H':
                        if ( i > 0 )
                            route_entries.addElement(
                                new NiRouteEntry
				(hostname, service, password)
				);
                        
                        if (i < count - 1){
                            hostname = new String (st.nextToken());
                            i++;
                            service = password = null;
                        }
                        else
                        {
                            throw new NiException("route " + s +
                                                  " last token /" + c +
                                                  "/ not allowed");
                        }
                        break;
                    case 's':
                    case 'S':                        
                        if (i < count - 1){
                            service = new String (st.nextToken());
                            i++;
                        }
                        else
                        {
                            throw new NiException("route " + s +
                                                  " last token /" + c +
                                                  "/ not allowed");
                        }
                        break;
                    case 'p':
                    case 'P':                    
                        if (i < count - 1)
                        {
                            password = new String (st.nextToken());
                            i++;
                        }
                        else
                        {
                            throw new NiException("route " + s +
                                                  " last token /" + c +
                                                  "/ not allowed");
                        }
                        break;
                    default:
                        throw new NiException (s + ": token /" + c + "/ invalid");
		}
	    }

	    route_entries.addElement(
		new NiRouteEntry
                (hostname, service, password)
                );

	    for (int i = 0; i < route_entries.size(); i++ ) {
		total_length += ((NiRouteEntry )
                                 route_entries.elementAt(i)).getLength();
	    }

	    space_used = total_length;
	    total_length += 24;
            talkmode = talkmode_param;
	    total_entries = route_entries.size();
	    next_entry = 0;
	    next_offset = ((NiRouteEntry)route_entries.
                           elementAt(0)).getLength ();
	    rest_entries = total_entries - next_entry - 1;

	}
    
    public NiRoute(String s) throws NiException
        {
            this (s, NI.NI_TALK_NATIVE2 );
        }
    

    /**
       Read a route from a byte stream.
       This could be used to implement a saprouter .
    */
    public NiRoute ( byte [] stream ) throws NiException
	{
	    DataInputStream dis =
		new DataInputStream(new ByteArrayInputStream(stream)) ;

	    int offset = 0;
	    int total_used = stream.length;
	    int next = 0;
	    String host, serv, pass;

	    route_entries = new Vector();

	    try {
		for ( int i = 0; i < eyecatcher.length; i++) {
		    if (dis.readByte() != eyecatcher[i]) {
			throw new NiException( " invalid route " );
		    }
		}

		iroute_version = (int)dis.readByte();
		ni_version     = (int)dis.readByte();
		total_entries  = (int)dis.readByte();
		talkmode       = dis.readByte();
		options        = dis.readByte();
		unused         = dis.readByte();
		rest_entries   = (int)dis.readByte();
		space_used     = dis.readInt();
		next           = dis.readInt();

		for ( int i = 0; i < total_entries; i++ ) {
		    if (offset == next)
			next_entry = i;
		    host = getNextString(stream, offset);
		    offset += host.length() + 1;

		    serv = getNextString(stream, offset);
		    offset += serv.length() + 1;

		    pass = getNextString(stream, offset);
		    offset += pass.length() + 1;

		    System.out.println("host: " + host + " server " + serv);

		    if (offset > space_used) {
			System.out.print("NiRoute( byte [] ): invalid Route. ");
			System.out.println("offset: " + offset);
			this.printIt();
		    }
		    route_entries.addElement(new NiRouteEntry(host, serv, pass));
		}
	    } catch (IOException e) {
		System.out.println("IOException caught: " + e);
	    }

	    next_offset = next +  ((NiRouteEntry)route_entries.
				   elementAt(0)).getLength ();

	    for (int i = 0; i < route_entries.size(); i++ ) {
		total_length += ((NiRouteEntry )
                                 route_entries.elementAt(i)).getLength();
	    }
	    total_length += 24;

	    if (total_entries != route_entries.size()) {
		System.out.print("NiRoute( byte [] ): invalid Route. ");
		System.out.println("total_entries: " + total_entries);
		System.out.println("route_entries.size() = "
				   + route_entries.size());
		this.printIt();
	    }
	}


    private String getNextString(byte [] s, int off)
	{
	    int i = off + 24;
	    int len = 0;

	    for (i = off + 24;s[i] != 0 && i < 10000; len++, i++)
		;
	    return new String(s, off + 24, len);
	}

    /**
       convert a route to a byte stream (ie. serialize).
    */
    public byte [] toStream() throws IOException
	{
	    int len = total_length;
	    byte [] buffer = new byte [total_length];
	    byte [] b;
	    int offset = 0;

	    ByteArrayOutputStream bos = new ByteArrayOutputStream ( total_length );
	    DataOutputStream dos = new DataOutputStream(bos);

	    dos.write(eyecatcher, 0, eyecatcher.length );
	    dos.writeByte(iroute_version);
	    dos.writeByte(ni_version);
	    dos.writeByte(total_entries);
	    dos.writeByte(talkmode);
	    dos.writeByte(options);
	    dos.writeByte(unused);
	    dos.writeByte(rest_entries);
	    dos.writeInt(space_used);
	    dos.writeInt(next_offset);

	    b = bos.toByteArray();
	    for (offset = 0; offset < b.length; offset++) {
		buffer[offset] = b[offset];
	    }

	    for (int i = 0; i < route_entries.size() ; i++ ) {
		NiRouteEntry re = (NiRouteEntry)route_entries.elementAt(i);
		String s;

		s = re.getHost();
		if (s != null) {
		    b = s.getBytes ();
		    for (int j = 0; j < b.length; j++)
			buffer[offset++] = b[j];
		} else {
		}
		buffer[offset++] = 0;

		s = re.getPort();
		if (s != null) {
		    b = s.getBytes ();
		    for (int j = 0; j < b.length; j++)
			buffer[offset++] = b[j];
		} else {
		}
		buffer[offset++] = 0;
		s = re.getPass();
		if (s != null) {
		    b = s.getBytes ();
		    for (int j = 0; j < b.length; j++)
			buffer[offset++] = b[j];
		} else {
		}
		buffer[offset++] = 0;

	    }
	    return buffer;
	}

    /**
       dump a route.
    */
    public void printIt()
	{
	    int count = route_entries.size();
	    for ( int i = 0; i < count; i++ ) {
		NiRouteEntry e = (NiRouteEntry) route_entries.elementAt(i);
		System.out.print ( "Entry " + i + " Hostname: " + e.getHost());
		System.out.print ( " Service: " + e.getPort());
		System.out.println ( " Password: " + e.getPass());
	    }
	    System.out.println("iroute_version: " + iroute_version);
	    System.out.println("total_entries: " + total_entries);
	    System.out.println("talkmode: " + talkmode);
	    System.out.println("options: " + options);
	    System.out.println("rest_entries: " + rest_entries);
	    System.out.println("space_used: " + space_used);
	    System.out.println("next_entry: " + next_entry);
	}

    /**
       return the next hostaddress in this route
    */
    public InetAddress getNextHost () throws java.net.UnknownHostException
	{
	    String h;
	    NiRouteEntry re = (NiRouteEntry) route_entries.elementAt(next_entry);
	    if ((h = re.getHost()) != null) {
		return InetAddress.getByName(h);
	    }
	    return null;
	}

    /**
       return the next port in this route
    */
    public int getNextPort ()
	{
	    String s;
	    NiRouteEntry re = (NiRouteEntry) route_entries.elementAt(next_entry);
	    if ((s = re.getPort()) != null) {
		try {
		    URL u = new URL ( "http://a:" + s );
		    return u.getPort() ;
		} catch (MalformedURLException e) {
		    System.out.println ( e );
		}


	    }
	    return 2;
	}
    /**
       return the number of remaining
       entries in this route
    */
    public int getRestEntries ()
	{
	    return rest_entries;
	}

}

