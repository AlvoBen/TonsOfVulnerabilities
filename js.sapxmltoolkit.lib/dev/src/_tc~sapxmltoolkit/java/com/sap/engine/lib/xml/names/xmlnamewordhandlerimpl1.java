package com.sap.engine.lib.xml.names;

import com.sap.engine.lib.log.LogWriter;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      October 2001
 */
public final class XMLNameWordHandlerImpl1 implements XMLNameWordHandler {

  public void start() {
    LogWriter.getSystemLogWriter().print("( ");
  }

  public void word(char[] a, int start, int end) {
    LogWriter.getSystemLogWriter().print("\"");

    for (int i = start; i < end; i++) {
      LogWriter.getSystemLogWriter().print("" + a[i]);
    } 

    LogWriter.getSystemLogWriter().print("\" ");
  }

  public void end() {
    LogWriter.getSystemLogWriter().println(")");
  }

}

