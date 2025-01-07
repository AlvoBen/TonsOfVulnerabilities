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

package com.sap.engine.services.rmi_p4.server.command;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.interfaces.cross.CrossObjectBroker;
import com.sap.engine.boot.SystemProperties;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.ContextObjectClassReceiver;
import com.sap.engine.services.rmi_p4.ContextObjectClassReceiver_Stub;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.services.rmi_p4.server.P4ObjectBrokerServerImpl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;
import java.rmi.Remote;

public class Connect implements Command {

  private String formatResult(String address) throws Exception {
    try {
      ContextObjectClassReceiver_Stub stub = (ContextObjectClassReceiver_Stub)CrossObjectBroker.getDestination(address, null).getRemoteBroker().resolveInitialReference("cocr", ContextObjectClassReceiver.class);
      return stub.getInfo().toString();
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
    int severity = P4Logger.getLocation().getEffectiveSeverity();
    P4Logger.getLocation().setEffectiveSeverity(100); //DEBUG
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
      } finally {
        P4Logger.getLocation().setEffectiveSeverity(severity);
      }
  }

  public String getName() {
    return "p4_connect";
  }

  public String getGroup() {
    return "p4";
  }

  public String[] getSupportedShellProviderNames() {
    return new String[]{"InQMyShell"};
  }

  public String getHelpMessage() {
    String nl = SystemProperties.getProperty("line.separator");
    return "Checks if a p4 connection can be establised between two instances" +
            nl + "Usage: " + getName() + "p4_url. Example: " + getName() + " p4://192.168.1.1:50004";
  }
}
