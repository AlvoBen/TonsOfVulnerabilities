/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */

package com.sap.engine.session.runtime;

import com.sap.engine.session.usr.UserContext;
import com.sap.engine.session.logging.LogFilter;
import java.util.Properties;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Used for filtering the session management logs
 *
 * @author Nikolai Neichev
 */
public class SessionLogFilter implements LogFilter {

  public static String ARG_DOMAIN = "DOMAIN";
  public static String ARG_SESSION = "SESSION";
  public static String ARG_USER = "USER";
  public static String ARG_STIME = "START_TIME";
  public static String ARG_UTIME = "UPDATE_TIME";

  private String name;
  private Properties args = new Properties();

  /**
   * Constructor
   * @param name the filter name
   */
  public SessionLogFilter(String name) {
    this.name = name;
  }

    // new line string
  private final String NL = "\r\n";

  /**
   * Gets the filter description
   * @return the descriptiion
   */
  public String getDescription() {
    return "This filter is used for filtering the sessions logs" + NL +
            "from location : com.sap.engine.session.runtime";
  }

  /**
   * Gets a help message for the filter
   * @return the help message
   */
  public String getHelpMessage() {
    return "Filter arguments: " + NL +
            "  " + ARG_DOMAIN + " - the domain name or part of it" + NL +
            "  " + ARG_SESSION + " - the session ID or part of it" + NL +
            "  " + ARG_USER + " - the session user" + NL +
            "  " + ARG_STIME + " - the session start time" + NL +
            "  " + ARG_UTIME + " - sessions updated before this time, will be filtered" + NL +
            "    ! time format is : yy.MM.dd.HH.mm.ss" + NL;
  }

  /**
   * Checks if the log should be done
   * @param info the info object
   * @return true if it will be loged
   */
  public boolean toLog(Object info) {
    RuntimeSessionModel rsm = (RuntimeSessionModel)info;
      // if there is a domain agrument set and it's not the right one
    String domain = (String) args.get(ARG_DOMAIN); // example value : /monitoring
    if ( (domain != null) && (!rsm.domain().getName().contains(domain))) {
      return false;
    }
      // if there is a sessionId agrument set and it's not the right one
    String sessionId = (String)args.get(ARG_SESSION); // example value : /monitoring
    if ("capture".equals(sessionId)) {
      sessionId = rsm.getSessionId();
      args.put(ARG_SESSION, sessionId);
    }
    if ( (sessionId != null) && (!rsm.getSessionId().contains(sessionId))) {
      return false;
    }

    // if there is a user argument set and it'snot the right one
    String user = (String)args.get(ARG_USER);
    if ( (user != null) && (!user.equals(UserContext.getCurrentUserContext().getUser()))) {
      return false;
    }

    // if there is a start time check set and the time is not the right one
    String stTime = (String)args.get(ARG_STIME);
    if (stTime != null) {
      try {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yy.MM.dd.HH.mm.ss").parse(stTime));
        if ((cal.getTimeInMillis()/1000) != (rsm.lastAccessedTime()/1000)) { // acurracy to second
          return false;
        }
      } catch (ParseException e) {
        // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
        // Please do not remove this comment!
        // date is specified bad, so we ignore it and don't filter
      }
    }

    // if there is a last update time check set and the time has not come yet
    String upTime = (String)args.get(ARG_STIME);
    if (upTime != null) {
      try {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat("yy.MM.dd.HH.mm.ss").parse(upTime));
        if (cal.getTimeInMillis() > rsm.lastAccessedTime()) {
          return false;
        }
      } catch (ParseException e) {
        // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
        // Please do not remove this comment!
        // date is specified bad, so we ignore it and don't filter        
      }
    }
    return true;
  }

  /**
   * Gets the filter name
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the arguments for this filter
   * @param args the arguments
   */
  public void setArgs(Properties args) {
    this.args = args;
  }

  /**
   * Returns a list of the filter arguments
   * @return the arguments
   */
  public Properties listArgs() {
    return args;
  }

}