package com.sap.engine.session.logging;

import com.sap.engine.session.SessionDomain;

import java.util.Date;


/**
 * This class is used for unification of all the session management logs
 *
 * @author Nikolai Neichev
 */
public class LogMessageSystem {

  private final static String NL = "\r\n";
  private final static String DELIM = "<*>";

  /**
   * Adds a human readable date in front of the log message
   * @param message the log massage
   * @return the result message
   */
  public static String getDateLog(String message) {
    return NL + new Date().toString() + NL + message;
  }

  /**
   * Generates a log identificator depending on the specified domain and sessionID
   *
   * @param domain the fomain
   * @param sessionId the sessionID
   * @return the generated identificator
   */
  public static String getLogIdentificator(SessionDomain domain, String sessionId) {
    return DELIM + sessionId + "@" + domain.getName() + DELIM + NL;
  }

  public static String composeLogMessage(SessionDomain domain, String sessionId, String message) {
    return composeLogMessage(getLogIdentificator(domain, sessionId), message);
  }

  public static String composeLogMessage(String identificator, String message) {
    return getDateLog(identificator + message);
  }

  public static String composeLogMessage(String identificator) {
    return getDateLog(identificator);
  }

}
