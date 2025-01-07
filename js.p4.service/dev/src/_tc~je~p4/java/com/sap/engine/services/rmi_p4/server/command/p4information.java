package com.sap.engine.services.rmi_p4.server.command;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.P4RemoteObject;
import com.sap.engine.services.rmi_p4.monitor.P4RuntimeControlInterface;
import com.sap.engine.services.rmi_p4.server.P4ServiceFrame;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.services.rmi_p4.interfaces.ConnectionObjectInt;
import com.sap.engine.services.rmi_p4.all.ConnectionProfile;
import com.sap.bc.proj.jstartup.sadm.ShmAccessPoint;
import com.sap.bc.proj.jstartup.sadm.ShmException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Enumeration;

public class P4Information implements Command {


  private String formatResult(boolean detailInfo) {

    P4ObjectBroker broker = P4ObjectBroker.getBroker();
    if (broker == null) {
      return "P4ObjectBroker is closed. Maybe the server node is in stopping mode";
    } else {
      StringBuilder sb = new StringBuilder();
      sb.append("...::: P4 Information Command :::...\r\n");
      sb.append("\r\n          P4ObjectBroker : \r\n - " + broker);
      sb.append("\r\n                brokerId : " + broker.brokerId);
      sb.append("\r\n                serverId : " + broker.id);
      sb.append("\r\n              brokerName : " + com.sap.bc.proj.jstartup.JStartupFramework.getParam("j2ee/ms/host") + "_" + com.sap.bc.proj.jstartup.JStartupFramework.getParam("j2ee/ms/port"));
      sb.append("\r\nAttachedThreadMonitoring : " + broker.getAttachedThreadMonitoringInfo());
      sb.append("\r\n                 Threads : " + P4ServiceFrame.getThreadCount());
      sb.append("\r\n              Queue Size : " + P4ServiceFrame.getRequestQueueSize());

      try {
        ShmAccessPoint[] pid_p4 = ShmAccessPoint.getAllAccessPoints(ShmAccessPoint.PID_P4);
        if(pid_p4 != null && pid_p4.length > 0){
          sb.append("\r\n        P4 Access Points : ");
          for (int i = 0; i < pid_p4.length; i++) {
            sb.append("\r\n  [" + i + "] - " + pid_p4[i]);

          }
        }
      } catch (ShmException e) {
        if(P4Logger.getLocation().bePath()){
          P4Logger.getLocation().pathT("P4Information command-exception while try to get the p4's access points");
          P4Logger.getLocation().throwing(e);
        }

      }
      sb.append("\r\nRuntime control information----------------------------\r\n");
      try {
          P4RuntimeControlInterface p4rtc = P4ServiceFrame.getP4RuntimeControl();
          if (p4rtc != null) {
              sb.append("Number of exported objects: ").append(p4rtc.getExportedRemoteObjectsCount()).append("\r\n");
              sb.append("Number of successful requests: ").append(p4rtc.getRequestCount()).append("\r\n");
              sb.append("Number of failed requests: ").append(p4rtc.getErrorRequestCount()).append("\r\n");
              sb.append("Thread usage: ").append(p4rtc.getP4ThreadUsageRate()).append("%\r\n");
          }
      } catch (Exception e) {
         sb.append("Cannot read monitoring info\r\n");
      }
      sb.append("\r\n--------------------------------------------------------\r\n");
      ConnectionProfile cp [] = broker.getConnectionProfiles();
      if (cp != null && cp.length > 0) {
        sb.append("\r\n     Connection Profiles :");
        for (int i = 0; i < cp.length; i++) {
          sb.append("\r\n    [" + i + "] - " + cp[i]);
        }
        sb.append("\r\n==================================");
      }
      Hashtable iob = broker.initObjects;
      if (iob != null && iob.size() > 0) {
        sb.append("\r\n         Initial Objects :");
        Enumeration keys = iob.keys();
        while (keys.hasMoreElements()) {
          String key = (String) keys.nextElement();
          sb.append("\r\n--------------------------------------------------------\r\n");
          P4RemoteObject p4Value = (P4RemoteObject) iob.get(key);
          sb.append("\r\n             key : " + key);
          sb.append("\r\n        p4Object : " + p4Value);
          if (p4Value != null && detailInfo) {
            sb.append("\r\n    info Object:\r\n" + p4Value.getInfo());
          }

        }
        sb.append("\r\n==================================");
      }
      ConnectionObjectInt coi[] = broker.listConnections();
      if (coi != null && coi.length > 0) {
        sb.append("\r\n      ConnectionObjects:");
        for (int i = 0; i < coi.length; i++) {
          sb.append("\r\n[" + i + "] - " + coi[i]);

        }
      }
      return sb.toString();

    }
  }

  public void exec(Environment environment, InputStream in, OutputStream output, String[] strings) {
    PrintWriter pw = new PrintWriter(output, true);
    try {
      if (strings != null && strings.length > 0) {
        if (strings[0].equalsIgnoreCase("-a")) {
          pw.println(formatResult(true));
        }
      } else {
        pw.println(formatResult(false));
      }
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
    return "p4info";
  }

  public String getGroup() {
    return "p4";
  }

  public String[] getSupportedShellProviderNames() {
    return new String[]{"InQMyShell"};
  }

  public String getHelpMessage() {
    StringBuilder sb = new StringBuilder();
    sb.append("List P4 Information about the server node\r\n");
    sb.append("examples:\r\n");
    sb.append("    p4info\r\n");
    sb.append("    p4info -a\r\n");
    return sb.toString();
  }
}
