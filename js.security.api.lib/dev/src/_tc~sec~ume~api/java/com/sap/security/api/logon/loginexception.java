package com.sap.security.api.logon;

public class LoginException extends Exception
{
	private static final long serialVersionUID = 3670680004060725271L;
	
    public LoginException ()
    {
        super ();
    }

    public LoginException (String msg)
    {
        super (msg);
    }
}
