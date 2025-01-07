package com.sap.portlet.jdbc;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.sap.tc.logging.Location;



public class LogContext {
	public static final Location log = Location.getLocation("com.sap.portletbrowser.portlet.jdbc");
	
	public static String getExceptionStackTrace(Throwable t) {
	    ByteArrayOutputStream ostr = new ByteArrayOutputStream();
	    t.printStackTrace(new PrintStream(ostr));
	    return ostr.toString();
	  }

}
