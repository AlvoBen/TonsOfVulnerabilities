package com.sap.jms.util;

import com.sap.tc.logging.Location;
//import com.sap.tc.logging.SimpleLogger;

public class Logging {

	// TODO should deal with Severity in a consistent enum way
	
	/*
	 * Goes to developer log
	 * @param o
	 * @param severity - one of the defines in 
	 * @param args - comma separated list of argiments to print (e.g. sting, integer, long etc)
	 */
	public static void log(Object o, int severity, String messageID, Object[] args) {

		Location location = Location.getLocation(o);
		if (location.getEffectiveSeverity() < severity ) {
			return;
		}
		
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i < args.length; i++) {
			buffer.append(args[i]);
			buffer.append(" ");
		}

//		SimpleLogger.trace(severity, location, buffer.toString());
	}

	/*
	 * Goes to customer log
	 * @param o
	 * @param severity - one of the defines in 
	 * @param args - comma separated list of argiments to print (e.g. sting, integer, long etc)
	 */
	public static void customerLog(Object o, int severity, String messageID, Object[] args) {

		Location location = Location.getLocation(o);
		if (location.getEffectiveSeverity() < severity ) {
			return;
		}
		
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i < args.length; i++) {
			buffer.append(args[i]);
			buffer.append(" ");
		}

		//SimpleLogger.log(severity, category, location, messageId, buffer.toString());
	}

	public static void exception(Object o, int severity, String messageID, String message, Throwable exc) {
		
		Location location = Location.getLocation(o);
		if (location.getEffectiveSeverity() < severity ) {
			return;
		}
		
//		SimpleLogger.traceThrowable(severity, location, messageID, message, exc);
	}
}
