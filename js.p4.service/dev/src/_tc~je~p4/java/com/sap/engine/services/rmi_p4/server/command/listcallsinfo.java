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

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.rmi_p4.Call;
import com.sap.engine.services.rmi_p4.P4Call;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.services.rmi_p4.P4RuntimeException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;

public class ListCallsInfo implements Command {


  //private P4SessionProcessor proc;

  public ListCallsInfo() {
    //this.proc = proc;
  }

  private String formatResult() {
    String result = "                    .:: P4 CALLS INFO ::.  \n";
    Object[] calls = P4Call.getAllCalls();
    if (calls != null) {
      result += "Waiting calls on server node " + P4ObjectBroker.getBroker().getId() + " : " + calls.length + "\n\n";
      try {
        result += "=============================================================================\n";
        for (int i =0; i < calls.length; i++) {
          if (calls[i] instanceof Call) {
            result += getCallInfo((Call)calls[i]);
          }
        }
      } catch (Throwable e) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT("ListCallsInfo.formatResult()", P4Logger.exceptionTrace(e));
        }
        result += "there is a exception : " + e + "\n\n";
      }
    }
    return result;
  }

  public String getCallInfo(Call call) {
      StringBuffer result = new StringBuffer(" -- Call: " + call + "\n");
      if (call.repliable != null) {
        result.append("    |- Connection id  : ").append(call.repliable.getIdAslong()).append("\n");
        result.append("    |- Destination    : " + call.repliable.getUnderlyingProfile());
      }
      result.append("    |- Stub           : ").append(call.stub).append("\n");
      if (call.stub != null) {
        if (call.stub.p4_getInfo() != null) {
            result.append("    |- server ID      : ").append(call.stub.p4_getInfo().server_id).append("\n");
            result.append("    |- client ID      : ").append(call.stub.p4_getInfo().client_id).append("\n");
         }
          result.append("    |- connectionType : ").append(call.stub.connectionType).append("\n");
          result.append("    |- isLocal        : ").append(call.stub.isLocal).append("\n");
          result.append("    |- classLoader    : ").append(call.stub.p4_getClassLoader()).append("\n");
          result.append("    |- info           : ").append(call.stub.p4_getInfo()).append("\n");
       }
       result.append("-----------------------------------------------------------------------------\n\n");
       return result.toString();

  }

  public String getCallList() {
    StringBuffer result = new StringBuffer(" .:: P4 CALLS INFO ::.  \n");
    Object[] calls = P4Call.getAllCalls();
    if (calls != null) {
        result.append("Waiting calls on server node ").append(P4ObjectBroker.getBroker().getId()).append(" : ").append(calls.length).append("\n\n");
        result.append("=============================================================================\n");
        for (int i =0; i < calls.length; i++) {
            result.append(calls[i].toString()).append("\n");
        }
      }
      return result.toString();

  }
  public void exec(Environment environment, InputStream input, OutputStream output, String[] params) {
    PrintWriter pw = new PrintWriter(output, true);
    if (params.length > 0 && !params[0].equalsIgnoreCase("list")) {
      if (params[0].equalsIgnoreCase("cancel")) {
          if (params.length == 1) {
            pw.println(formatResult());
          } else {
            if (params[1].equalsIgnoreCase("all")) {
             Object[] calls = P4Call.getAllCalls();
              for (int i = 0; i < calls.length; i++) {
                 P4Call call = (P4Call)calls[i];
                 cancelCall(call, pw);
              }
            } else {
              long id;
              try {
                id = Long.parseLong(params[1]);
              } catch (Exception e) {
                pw.println(getHelpMessage());
                return;
              }
              P4Call call = P4Call.getCall(id);
              if (call == null) {
                pw.println("Call " + id +  " not found");
                return;
              }
              cancelCall(call, pw);
            }
          }
      } else if (params[0].equalsIgnoreCase("show")) {
        if (params.length == 1) {
          pw.println(formatResult());
        } else {
          long id;
          try {
            id = Long.parseLong(params[1]);
          } catch (Exception e) {
            pw.println(getHelpMessage());
            return;
          }
          P4Call call = P4Call.getCallReference(id);
          if (call == null) {
            pw.println("Call " + id +  " not found");
          } else {
            if (call instanceof Call) {
              pw.println(getCallInfo((Call)call));
            } else {
               pw.println(call.toString());
            }
          }
        }
      } else {
        pw.println(getHelpMessage());
      }
    } else {
      try {
        pw.println(getCallList());
      } catch (OutOfMemoryError e) {
        //$JL-EXC$
        throw e;
      } catch (ThreadDeath e) {
        //$JL-EXC$
        throw e;
      } catch (Throwable e) {
        //$JL-EXC$
        ByteArrayOutputStream ostr = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(ostr));
        pw.println("ERROR: " + ostr);
        pw.println(getHelpMessage());
      }
    }
  }

  private void cancelCall(P4Call call, PrintWriter output) {
    call.setException(new P4RuntimeException("The request was cancelled from the telnet console"));
    output.println("Call " + call.getCall_id() + " was cancelled");
  }

  public String getName() {
    return "p4_calls";
  }

  public String getGroup() {
    return "p4";
  }

  public String[] getSupportedShellProviderNames() {
    return new String[]{"InQMyShell"};
  }

  public String getHelpMessage() {
    String nl = SystemProperties.getProperty("line.separator");
    return "Lists open p4 calls" +
            nl + "Usage: " + nl + "p4_calls list - shows a list of opened p4 calls" + nl + "p4_calls show [CALL_ID] - shows detailed information about all calls or the specified call id" + nl + "p4_calls cancel CALL_ID or \"all\" - cancels the call";
  }
}
