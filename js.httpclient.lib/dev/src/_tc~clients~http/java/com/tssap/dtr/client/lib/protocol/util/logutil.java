package com.tssap.dtr.client.lib.protocol.util;


import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Helper class that supports writing stack traces to logs.
 */
public final class LogUtil
{
	public static Location TIME_LOCATION = Location.getLocation("com.tssap.dtr.client.lib.util.TimeLog");

	public static void logElapsedTime (long elapsedMillis, String message) {
		TIME_LOCATION.debugT("[{0} ms]: {1}", new Object[] {new Long(elapsedMillis), message});
	}



	/**
	 * Logs the given exception with severity "error".
     * Stack trace is written for this exception.
	 * @param location  the location to trace to
	 * @param throwable  the exception to trace
	 */
	public static void logException ( Location location, Throwable throwable )
	{
		if ( location.beError() ) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter( stringWriter );
			printWriter.println();
			throwable.printStackTrace( printWriter );
			//$JL-SEVERITY_TEST$
			location.errorT( stringWriter.toString() );
		}
	}


	/**
	 * Logs the given exception with severity "debug".
     * Stack trace is written for this exception.
	 * @param location  the location to trace to
	 * @param throwable  the exception to trace
	 */
	public static void debugLogException ( Location location, Throwable throwable )
	{
		if ( location.beDebug() ) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter( stringWriter );
			printWriter.println();
			throwable.printStackTrace( printWriter );
			location.debugT( stringWriter.toString() );
		}
	}

	/**
	 * Logs the given caught exception with severity "error".
     * No stack trace is written for this exception.
	 * @param location  the location to trace to
	 * @param throwable  the exception to trace
	 */
	public static void logCaughtException ( Location location, Throwable throwable )
	{
		if ( location.beError() ) {
			//$JL-SEVERITY_TEST$
			location.errorT( "Caught " + throwable.getClass().getName() + ":" + throwable.getMessage() );
		}
	}


	/**
	 * Logs the given exception with severity "debug" or "info" dependening
	 * on parameter <code>doDebugLogException</code>.
     * Only if the exception is traced with severity "debug" then a stack trace is
     * written.
	 * @param location  the location to trace to
	 * @param throwable  the exception to trace
	 */
    public static void infoLogException ( Location location, Throwable throwable, boolean doDebugLogException )
    {
        if ( doDebugLogException && location.beDebug() ) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter( stringWriter );
	        printWriter.println();
            throwable.printStackTrace( printWriter );
            location.debugT( stringWriter.toString() );

        } else if ( location.beInfo() ){
            location.infoT( "Info exception " + throwable.getClass().getName() + ": " + throwable.getMessage() );
        }
    }


    /**
     * Logs the given caught exception with the given severity.
     * No stack trace is written for this exception.
     * @param location  the location to trace to
     * @param throwable  the exception to trace
     * @param severity   the severity of the trace message
     */
    public static void traceCaughtException ( Location location, Throwable throwable, int severity )
    {
        if ( severity == Severity.DEBUG ) {
            if ( location.beDebug() ) {
                location.debugT( "Caught " + throwable.getClass().getName() + ":" + throwable.getMessage() );
            }
        }  else if ( severity == Severity.INFO ) {
            if ( location.beInfo() ) {
                location.infoT( "Caught " + throwable.getClass().getName() + ":" + throwable.getMessage() );
            }
        } else if ( severity == Severity.WARNING ) {
            if ( location.beWarning() ) {
				//$JL-SEVERITY_TEST$
                location.warningT( "Caught " + throwable.getClass().getName() + ":" + throwable.getMessage() );
            }
        } else if ( severity == Severity.ERROR ) {
            if ( location.beError() ) {
				//$JL-SEVERITY_TEST$
                location.errorT( "Caught " + throwable.getClass().getName() + ":" + throwable.getMessage() );
            }
        } else {
            // Trace with severity "error" when invalid severity is given
            // or one of the severities that are not useful for caught exceptions (path, fatal).
			//$JL-SEVERITY_TEST$
            location.errorT( "Caught " + throwable.getClass().getName() + ":" + throwable.getMessage() );
        }
    }


    /**
     * Logs the given caught exception with the given severity.
     * If stackTrace is true, then a stack trace is written for this exception.
     * @param location  the location to trace to
     * @param throwable  the exception to trace
     * @param severity   the severity of the trace message
     */
    public static void traceCaughtExceptionWithStackTrace ( Location location, Throwable throwable, int severity  )
    {
        if ( severity == Severity.ERROR ) {
            if ( location.beError() ) {
                StringWriter stringWriter = new StringWriter();
                writeStackTrace( stringWriter, throwable, "Caught " + throwable.getClass().getName() + ":" + throwable.getMessage() );
				//$JL-SEVERITY_TEST$                
                location.errorT( stringWriter.toString() );
            }
        } else if ( severity == Severity.WARNING ) {
            if ( location.beWarning() ) {
                StringWriter stringWriter = new StringWriter();
                writeStackTrace( stringWriter, throwable, "Caught " + throwable.getClass().getName() + ":" + throwable.getMessage() );
				//$JL-SEVERITY_TEST$                
                location.warningT( stringWriter.toString() );
            }
        } else if ( severity == Severity.INFO ) {
            if ( location.beInfo() ) {
                StringWriter stringWriter = new StringWriter();
                writeStackTrace( stringWriter, throwable, "Caught " + throwable.getClass().getName() + ":" + throwable.getMessage() );
                location.infoT( stringWriter.toString() );
            }
        } else if ( severity == Severity.DEBUG ) {
            if ( location.beDebug() ) {
                StringWriter stringWriter = new StringWriter();
                writeStackTrace( stringWriter, throwable, "Caught " + throwable.getClass().getName() + ":" + throwable.getMessage() );
                location.debugT( stringWriter.toString() );
            }
        } else {
            // Trace with severity "error" when invalid severity is given.
            // or one of the severities that are not useful for caught exceptions (path, fatal).
            StringWriter stringWriter = new StringWriter();
            writeStackTrace( stringWriter, throwable , "Caught " + throwable.getClass().getName() + ":" + throwable.getMessage() );
			//$JL-SEVERITY_TEST$
            location.errorT( stringWriter.toString() );
        }

    }


    private static void writeStackTrace ( StringWriter stringWriter, Throwable throwable, String title )
    {
        PrintWriter printWriter = new PrintWriter( stringWriter );
        if ( title != null ){
            printWriter.print( title );
        }
        printWriter.println();
        throwable.printStackTrace( printWriter );
    }


}
