/**
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rmi_p4.server.command;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.boot.SystemProperties;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.exception.P4Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


/**
 * This command enables or disables gathering of statistics for P4 service by
 * Accounting service infrastructure. 
 * 
 * @author I041949
 */
public class AccountingServiceManager implements Command {

  private String formatResult(String value) throws Exception {
    try {
      if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("start") || value.equalsIgnoreCase("-start")){
        P4ObjectBroker.setAccountingFlag(true);
        return "Done";
      }
      if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("stop") || value.equalsIgnoreCase("-stop")) {
        if (P4ObjectBroker.isEnabledAccounting()) {
          P4ObjectBroker.setAccountingFlag(false);
          return "Done";
        } else {
          return "Accounting for P4 service was not started";
        }
      }
      if (value.equalsIgnoreCase("state") || value.equalsIgnoreCase("-state") || value.equalsIgnoreCase("status")) {
        if (P4ObjectBroker.isEnabledAccounting()){
          return "Started";
        } else {
          return "Stopped";
        }
      }
      return getHelpMessage();
    } catch (Exception e) {
      P4Logger.getLocation().debugT(P4Logger.exceptionTrace(e));
      return e.toString();
    }
  }

  public void exec(Environment environment, InputStream input, OutputStream output, String[] strings) {
    PrintWriter pw = new PrintWriter(output, true);
    if (strings.length != 1) {
      pw.println(getHelpMessage());
      return;
    }
    try {
        pw.println(formatResult(strings[0]));
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        ByteArrayOutputStream ostr = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(ostr));
        pw.println("ERROR: " + ostr);
        pw.println(getHelpMessage());
      }
  }

  public String getName() {
    return "p4_accounting";
  }

  public String getGroup() {
    return "p4";
  }

  public String[] getSupportedShellProviderNames() {
    return new String[]{"InQMyShell"};
  }

  public String getHelpMessage() {
    String nl = SystemProperties.getProperty("line.separator");
    return "Command enable or disable gathering of statistics for P4 service by accounting service infrastructure" +
            nl + "Usage: " + getName() + " [start|stop|state] " +
            nl + "Example: " + 
            nl + getName() + " start";
  }
}