package com.sap.security.core.server.jaas;

import java.io.StringWriter;
import java.io.PrintWriter;

import com.sap.tc.logging.Location;

/**
 * Class for exception logging.
 * <br>
 * <p>Copyright (c) 2003 SAP AG</p>
 * @version 1.0
 */
public class ExceptionLogger {

  public static final Location LOCATION = Location.getLocation("com.sap.security.core.server.jaas");

	public static Throwable log(int severity, Throwable e) {
    if (LOCATION.beLogged(severity)) {
      LOCATION.traceThrowableT(severity, e.getLocalizedMessage(), e);
    }

		return e;
	}

  public static String getStackTrace(Throwable t) {
    StringWriter stringWritter = new StringWriter();
    t.printStackTrace(new PrintWriter(stringWritter));

    return stringWritter.toString();
  }

}
