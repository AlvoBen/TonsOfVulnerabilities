/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

package com.sap.engine.rmic.log;

import com.sap.engine.rmic.RMIC;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Calendar;

/**
 * @author Mladen Droshev
 */

public class RMICLogger {//$JL-LOG_CONFIG$ $JL-SYS_OUT_ERR$ $JL-EXC$

  private static FileOutputStream out = null;

  static {
    File path = null;
    if (RMIC.logDir != null) {
      path = new File(RMIC.logDir + File.separator + "rmic_logs");
    } else {
      path = new File(RMIC.projectDir + File.separator + "rmic_logs");
    }
    if (!path.exists()) {
      path.mkdirs();
    }
    File fileToLog = new File(path, "rmic_" + RMIC.projectName + "_" + System.currentTimeMillis() + ".log");
    try {
      out = new FileOutputStream(fileToLog);
      writeBeginMSG();
    } catch (FileNotFoundException e) {
      System.out.println(">> ERROR : Cannot open stream for log Exception : " + e.getMessage());
    }
  }


  public static synchronized void logMSG(String msg) {
    try {
      out.write(("[" + Calendar.getInstance().getTime() + "] " + msg + "\r\n").getBytes());
//      if (RMIC.dumpOnAntConsole && RMIC.project != null) {
//        RMIC.project.log("[" + Calendar.getInstance().getTime() + "] " + msg);
//      }
    } catch (IOException e) {
      System.out.println(">> Exception : " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static synchronized void logEnterMethod(String msg) {
    try {
      out.write(("[" + Calendar.getInstance().getTime() + "] " + "entering in the method -> " + msg + "\r\n").getBytes());
//      if (RMIC.dumpOnAntConsole && RMIC.project != null) {
//        RMIC.project.log("[" + Calendar.getInstance().getTime() + "] " + "entering in the method -> " + msg);
//      }
    } catch (IOException e) {
      System.out.println(">> Exception : " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static synchronized void logExitMethod(String msg) {
    try {
      out.write(("[" + Calendar.getInstance().getTime() + "] " + "exit from the method -> " + msg + "\r\n").getBytes());
//      if (RMIC.dumpOnAntConsole && RMIC.project != null) {
//        RMIC.project.log("[" + Calendar.getInstance().getTime() + "] " + "exit from the method -> " + msg);
//      }
    } catch (IOException e) {
      System.out.println(">> Exception : " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static synchronized void throwing(Throwable t) {
    try {
      out.write(("[" + Calendar.getInstance().getTime() + "] " + "throwing : -> " + exceptionTrace(t) + "\r\n").getBytes());
//      if (RMIC.dumpOnAntConsole && RMIC.project != null) {
//        RMIC.project.log("[" + Calendar.getInstance().getTime() + "] " + "throwing : -> " + exceptionTrace(t));
//      }
    } catch (IOException e) {
      System.out.println(">> Exception : " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static String exceptionTrace(Throwable thr) {
    ByteArrayOutputStream ostr = new ByteArrayOutputStream();
    thr.printStackTrace(new PrintStream(ostr));
    return "\r\n" + ostr.toString();
  }

  public static synchronized void writeBeginMSG() {
    try {
      out.write(" [SAP's RMIC GENERATOR for p4/iiop remote support classes ] \r\n".getBytes());
//      if (RMIC.dumpOnAntConsole && RMIC.project != null) {
//        RMIC.project.log(" [SAP's RMIC GENERATOR for p4/iiop remote support classes ]");
//      }
    } catch (IOException e) {
      System.out.println(">> Exception : " + e.getMessage());
      e.printStackTrace();
    }
  }

  protected void finalize() throws Throwable{//$JL-FINALIZE$
    super.finalize();
    try {
      out.flush();
      out.close();
    } catch (IOException e) { //$JL-EXC$
      System.out.println(">> Cannot close the log file : Exception : " + e.getMessage());
    }
  }




}
