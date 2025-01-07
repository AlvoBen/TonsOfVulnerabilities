package com.sap.ni;

import java.io.*;
import java.net.*;

public class NiException extends Exception 
{
    static int NIE_NI = 0; // native NI-Exception
    static int NIE_IO = 1; // IO Exception 
    static int NIE_NR = 2; // Name Resolution Exception 
    
    String message;
    Throwable _e;          
    
    NiException ( IOException e ) 
	{
	    message = e.getMessage ();
	    _e = e;
	}
    
    NiException ( String s ) 
	{
	    message = s;
	    _e = null;
	}
    
    
    public String getMessage () 
	{
	    return message;
	}
    
    public void printStackTrace ( ) 
	{
	    if ( _e != null ){
		_e.printStackTrace () ;
	    }
	    else
	    {
		super.printStackTrace () ;
	    }
	}
}




