package com.sap.engine.session.logging;

import java.io.RandomAccessFile;
import java.util.Date;
import com.sap.tc.logging.Location;


public class Dump {

  public static Location activations_loc = Location.getLocation("session.management.activations");


  public String filename;

  // for manual file debug of the session activations activations
  public static void out(String fName, Throwable ex) {
    try {
      out(fName, ex.getMessage(), false);
      StackTraceElement[] stacktrace = ex.getStackTrace();
      for (StackTraceElement aStacktrace : stacktrace) {
        out(fName, aStacktrace.toString(), false);
      }
    } catch (Exception e) {
    }
  }

  public static void out(String fName, Throwable ex, boolean showInfo) {
    try {
      out(fName, ex.getMessage(), true);
      StackTraceElement[] stacktrace = ex.getStackTrace();
      for (StackTraceElement aStacktrace : stacktrace) {
        out(fName, aStacktrace.toString(), false);
      }
    } catch (Exception e) {
    }
  }  

  public static void out(String fName, String str) {
    out(fName, str, false);
  }

  public static void out(String fName, String str, boolean showInfo) {
    RandomAccessFile raf = null;
    try {
      raf = new RandomAccessFile("_" + fName + ".txt", "rw");
      raf.seek(raf.length());
      if (!showInfo) { // trace
        raf.writeBytes("  " + str + "\r\n");
      } else {
        raf.writeBytes("*** " + new Date() + "***" + Thread.currentThread().getName() + "|" + Thread.currentThread().getId() + "\r\n");
        raf.writeBytes(str);
        raf.writeBytes("\r\n");
      }
    } catch (Exception e) {
    } finally {
      if (raf != null) {
        try {
          raf.close();
        } catch (Exception ee) {
        }
      }
    }
  }


}