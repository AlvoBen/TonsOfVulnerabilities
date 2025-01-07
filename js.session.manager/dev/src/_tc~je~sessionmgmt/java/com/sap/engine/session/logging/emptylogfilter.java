package com.sap.engine.session.logging;

import java.util.Properties;

/**
 * This class is used for empty implementation of a log filter
 */
public class EmptyLogFilter implements LogFilter {

  public String getName() {
    return "EmptyLogFilter";
  }

  public boolean toLog(Object info) {
    return true;
  }

  public void setArgs(Properties args) {
    // useless
  }

  public Properties listArgs() {
    return new Properties();
  }

  public String getDescription() {
    return "This filter is used if there is nothong to be filtered";
  }

  public String getHelpMessage() {
    return "No arguments";
  }
}
