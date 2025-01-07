package com.sap.engine.lib.xml.parser.dtd;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.*;
import com.sap.engine.lib.xml.parser.*;
import com.sap.engine.lib.xml.parser.handlers.*;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version Dec 18, 2001, 5:33:22 PM
 */
public final class CommandLine {

  private static void printUsage() {
    LogWriter.getSystemLogWriter().println(Info.COMPANY + " Document Type Declaration (DTD) validator"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("\n"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("Usage: " + Info.BATCH_DTD + " filename.xml"); //$JL-SYS_OUT_ERR$
  }

  public static void main(String[] args) throws Exception {
    if ((args.length == 0) || (args.length > 1)) {
      printUsage();
      return;
    }

    XMLParser p = new XMLParser();
    DocHandler h = new EmptyDocHandler();
    p.setValidation(true);
    LogWriter.getSystemLogWriter().println("Attempt for validation of '" + args[0] + "'..."); //$JL-SYS_OUT_ERR$
    p.parse(args[0], h);
    LogWriter.getSystemLogWriter().println("Validation was successful."); //$JL-SYS_OUT_ERR$
  }

}

