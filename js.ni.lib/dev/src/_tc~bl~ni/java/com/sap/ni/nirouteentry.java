package com.sap.ni;

public class NiRouteEntry 
{
    private String hostname;
    private String service;
    private String password;
    
    public NiRouteEntry ( String host, String ser, String pass ) 
	{
	    hostname = null;
	    service  = null;
	    password = null;
	    
	    if (host != null)
		hostname = new String ( host );
	    if (ser != null)
		service  = new String ( ser );
	    else 
		service = new String ("3299");
	    if (pass != null)
		password = new String (pass);
	    
	}
    
    public String getHost() 
	{
	    return hostname ;
	}
    public String getPort() 
	{
	    return service ;
	}
    public String getPass() 
	{
	    return password ;
	}
    
    public int getLength() 
	{
	    int len = 0;
	    if (hostname != null)
		len += hostname.length ();
	    if (service != null)
		len += service.length ();
	    if (password != null)
		len += password.length ();
	    len += 3;
	    return len;
	}
    
}

