package com.sap.engine.lib.xsl.xpath;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.Info;
import com.sap.engine.lib.xml.SystemProperties;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      September 2001
 */
public final class CommandLine {

  private static void usage() {
    LogWriter.getSystemLogWriter().println(Info.COMPANY + " XPath query evaluator"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("Allows you to evaluate from the console multiple XPath expressions"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("with respect to a single XML file."); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("\n"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("Usage: " + Info.BATCH_XPATH + " filename.xml"); //$JL-SYS_OUT_ERR$
  }

//  public static void main(String[] args) throws Exception {
//    SystemProperties.setProperty("javax.xml.parsers.SAXParserFactory", "com.sap.engine.lib.jaxp.SAXParserFactoryImpl");
//    SystemProperties.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sap.engine.lib.jaxp.DocumentBuilderFactoryImpl");
//    SystemProperties.setProperty("javax.xml.transform.TransformerFactory", "com.sap.engine.lib.jaxp.TransformerFactoryImpl");
//
//    if (args.length != 1) {
//      usage();
//      return;
//    }
//
//    XPathMatcher matcher = new XPathMatcher(args[0]);
//    LogWriter.getSystemLogWriter().println("Type your queries, empty string to exit."); //$JL-SYS_OUT_ERR$
//    LogWriter.getSystemLogWriter().println("If the query starts with 'debug', the expression tree is printed."); //$JL-SYS_OUT_ERR$
//    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//
//    while (true) {
//      LogWriter.getSystemLogWriter().println("\n"); //$JL-SYS_OUT_ERR$
//      LogWriter.getSystemLogWriter().print("QUERY: "); //$JL-SYS_OUT_ERR$
//      String s = in.readLine();
//
//      if ((s == null) || (s.length() == 0)) {
//        break;
//      }
//
//      boolean debugMode = false;
//
//      if (s.startsWith("debug")) {
//        debugMode = true;
//        s = s.substring(5);
//      }
//
//      try {
//        XObject xo = matcher.process(s);
//        LogWriter.getSystemLogWriter().println(xo.toXString().toString()); //$JL-SYS_OUT_ERR$
//      } catch (Exception e) {
//        printException(e);
//      }
//
//      if (debugMode) {
//        try {
//          matcher.printET(s);
//        } catch (Exception e) {
//          //$JL-EXC$
//          //expected exception
//        }
//      }
//    }
//  }

  private static void printException(Exception e) {
    String a = e.getClass().getName();
    int p = a.lastIndexOf('.');
    a = (p == -1) ? a : a.substring(p + 1);
    String m = e.getMessage();

    if (m != null) {
      a = a + ": " + m;
    }

    LogWriter.getSystemLogWriter().println(a); //$JL-SYS_OUT_ERR$
  }

}

