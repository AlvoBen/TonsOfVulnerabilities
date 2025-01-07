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
import com.sap.engine.boot.SystemProperties;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.server.ClusterOrganizer;
import com.sap.engine.services.rmi_p4.all.ConnectionProfile;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * User: mladen-d
 * Date: 2005-11-16
 */
public class ListProfiles implements Command  {
  ClusterOrganizer organizer = null;

  public ListProfiles(ClusterOrganizer cOrganizer){
    this.organizer = cOrganizer;
  }

  private String formatResult() {
    String result = "";
    ConnectionProfile[] profiles = organizer.getAllProfiles();
    if(profiles != null){
      result += "\r\n  The P4's connection profiles   \r\n";
      for (int i = 0; i < profiles.length; i++) {
        result +=  "      [" + i + ".] " + profiles[i] + "\r\n";

      }
    } else {
      result += "------The p4 doesn't have profiles-------\r\n";
    }
    return result;
  }

  public void exec(Environment environment, InputStream input, OutputStream output, String[] strings) {
    PrintWriter pw = new PrintWriter(output, true);
    try {
        pw.println(formatResult());
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
    return "p4_profiles";
  }

  public String getGroup() {
    return "p4";
  }

  public String[] getSupportedShellProviderNames() {
    return new String[]{"InQMyShell"};
  }

  public String getHelpMessage() {
    String nl = SystemProperties.getProperty("line.separator");
    return "Lists all p4 objects with the corresponding infos. " +
            nl + "Usage: " + getName();
  }
}
