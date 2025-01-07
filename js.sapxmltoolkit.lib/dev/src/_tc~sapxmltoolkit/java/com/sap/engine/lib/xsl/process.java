package com.sap.engine.lib.xsl;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.sap.engine.lib.log.LogWriter;

/**
 * This class can be used to to do simple CommandLine transformations
 * <p>
 * To use it simply run it on the command line with the following parameters:
 *  -xsl=stylesheet - the stylesheet to be used when transforming
 *  -xml=source     - the source file
 *  -out=output     - the output file for the transformation
 *
 * If some exception occures while transforming, it is printed on the command line
 * example: java com.sap.engine.lib.xsl.Process -xsl=stylesheet -xml=source -out=output
 *
 * @author Vladimir Savchenko, vladimir.savchenko@sap.com
 */
public class Process {

  public static void printHelp() {
    LogWriter.getSystemLogWriter().println("XSLT CommandLine Processor. Version 1.0"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println(" -Usage:"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("    -java com.sap.engine.lib.xsl.Process -xsl=stylesheet -xml=source -out=output"); //$JL-SYS_OUT_ERR$
  }

  public static void main(String args[]) throws Exception {
    if (args.length == 0) {
      printHelp();
      System.exit(0);
    } else {
      String xml = null, xsl = null, out = null;

      for (int i = 0; i < args.length; i++) {
        if (args[i].startsWith("-xml=")) {
          xml = args[i].substring("-xml=".length());
        } else if (args[i].startsWith("-xsl=")) {
          xsl = args[i].substring("-xsl=".length());
        } else if (args[i].startsWith("-out=")) {
          out = args[i].substring("-out=".length());
        } else {
          LogWriter.getSystemLogWriter().println("Unsupported argument: " + args[i]); //$JL-SYS_OUT_ERR$
        }
      } 

      if (xml == null || xsl == null) {
        LogWriter.getSystemLogWriter().println("Error: you need to supply -xml and -xsl parameters"); //$JL-SYS_OUT_ERR$
        System.exit(0);
      }

      if (out == null) {
        out = xml + ".out";
        LogWriter.getSystemLogWriter().println("Warning: no -out parameter supplied. using default output to: " + out); //$JL-SYS_OUT_ERR$
      }

      LogWriter.getSystemLogWriter().println("Processing transformation:"); //$JL-SYS_OUT_ERR$
      LogWriter.getSystemLogWriter().println("  -xml=" + xml); //$JL-SYS_OUT_ERR$
      LogWriter.getSystemLogWriter().println("  -xsl=" + xsl); //$JL-SYS_OUT_ERR$
      LogWriter.getSystemLogWriter().println("  -out=" + out); //$JL-SYS_OUT_ERR$
      TransformerFactory.newInstance().newTransformer(new StreamSource(xsl)).transform(new StreamSource(xml), new StreamResult(out));
      LogWriter.getSystemLogWriter().println("Done."); //$JL-SYS_OUT_ERR$
    }
  }

}

