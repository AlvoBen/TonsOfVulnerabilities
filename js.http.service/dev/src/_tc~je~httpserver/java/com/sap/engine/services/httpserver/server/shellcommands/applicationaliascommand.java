/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */

package com.sap.engine.services.httpserver.server.shellcommands;

import java.io.*;
import java.rmi.RemoteException;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.boot.SystemProperties;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.httpserver.HttpRuntimeInterface;
import com.sap.engine.services.httpserver.HostPropertiesRuntimeInterface;

/*
 * Manages application aliases.
 *
 * @author Violeta Uzunova
 * @version 6.30
 */
public class ApplicationAliasCommand implements Command {

  private HttpRuntimeInterface http = null;
  private ApplicationServiceContext context = null;

  public ApplicationAliasCommand(ApplicationServiceContext context, HttpRuntimeInterface http) {
    this.http = http;
    this.context = context;
  }

  /**
   * A method that executes the command .
   *
   * @param   environment  An implementation of Environment.
   * @param   input  The InputStream , used by the command.
   * @param   output  The OutputStream , used by the command.
   * @param   params  Parameters of the command.
   *
   */
  public void exec(Environment environment, InputStream input, OutputStream output, String[] params) {
    PrintWriter pw = new PrintWriter(output, true);
    int count = params.length;
    String host = null;
    String alias = null;
    boolean disable = false;
    boolean enable = false;
    boolean printAppName = false;
    if(count > 0 && (params[0].toUpperCase().equals("-H") || params[0].equals("-?") || params[0].toUpperCase().equals("-HELP"))) {
      pw.println(getHelpMessage());
      return;
    } else {
      try {
        for (int c = 0; c < count; c++) {
          if ("-host".equals(params[c])) {
            if (host != null) {
              throw new Exception("Host name specified twice.");
            }
            host = params[++c];
          } else if ("-disable".equals(params[c])) {
            if (disable) {
              throw new Exception("Disable specified twice.");
            } else if (enable) {
              throw new Exception("Both parameter disable and parameter enable specified.");
            }
            disable = true;
          } else if ("-enable".equals(params[c])) {
            if (enable) {
              throw new Exception("Enable specified twice.");
            } else if (disable) {
              throw new Exception("Both parameter disable and parameter enable specified.");
            }
            enable = true;
          } else if ("-printAppName".equalsIgnoreCase(params[c])) {
            if (printAppName) {
              throw new Exception("-printAppName must appear only once.");
            } else {
              printAppName = true;
            }
          } else {
            if (alias == null) {
              alias = params[c];
            } else {
              throw new Exception("Alias name specified twice.");
            }
          }
        }

        if (host == null) {
          host = "default";
        }
        if (alias == null && (disable || enable)) {
          throw new Exception("Alias name not specified.");
        }
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        pw.print("ERROR: ");
        e.printStackTrace(pw);
        pw.println();
        pw.println(getHelpMessage());
        return;
      }
    }
    try {
      HostPropertiesRuntimeInterface[] hosts = http.getAllHostsTemp();
      int hostIndex = -1;

      for (int c = hosts.length; --c >= 0;) {
        if (hosts[c].getHostName().equals(host)) {
          hostIndex = c;
          break;
        }
      }

      if (hostIndex == -1) {
        throw new Exception("Virtual Host " + host + " Not Found");
      }
      
      if (printAppName) {
        if (null == alias) {
          listAllByAppName(pw, hosts[hostIndex]);
        } else {
          findAppNameByAlias(pw, hosts[hostIndex], alias);
        }
        
        return;
      }
      
      if (alias == null) {
        pw.println("Application Aliases:");
        listAll(pw, hosts[hostIndex], host, "  ");
      } else {
        if (enable && !hosts[hostIndex].isApplicationAliasEnabled(alias)) {
          hosts[hostIndex].enableApplicationAlias(alias);
          pw.println("Application Aliases:");
          listAll(pw, hosts[hostIndex], host, "  ");
          http.clearCache(host);
        } else if (disable && hosts[hostIndex].isApplicationAliasEnabled(alias)) {
          hosts[hostIndex].disableApplicationAlias(alias);
          pw.println("Application Aliases:");
          listAll(pw, hosts[hostIndex], host, "  ");
          http.clearCache(host);
        }
        
        if (hosts[hostIndex].isApplicationAliasEnabled(alias)) {
          pw.println();
          pw.println("Application alias " + alias + " is available on " + host + " host");
        } else {
          pw.println();
          pw.println("Application alias " + alias + " is not available on " + host + " host");
        }
      }
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      pw.println("ERROR: ");
      e.printStackTrace(pw);
    }

  }

  private void listAll(PrintWriter pw, HostPropertiesRuntimeInterface hostProperties, String host, String tab) throws RemoteException {
    String[] en = hostProperties.getAliasNames();
    for (int i = 0; en != null && i < en.length; i++) {
      String alias = en[i];
      if (!hostProperties.isApplicationAlias(alias)) {
        continue;
      }
      if (hostProperties.isApplicationAliasEnabled(alias)) {
        pw.println(tab + alias + tab + "- (active on "+ host +" host)");
      } else {
        pw.println(tab + alias + tab + "- (inactive on "+ host +" host)");
      }
    }
  }
  
  private void listAllByAppName(PrintWriter writer, HostPropertiesRuntimeInterface hostProperties) throws RemoteException {
    printHeader(writer);
    String container = "servlet_jsp";
    String[] servers = new String[1];
    servers[0] = context.getClusterContext().getClusterMonitor()
        .getCurrentParticipant().getName();
    DeployService deploy = (DeployService) context.getContainerContext()
        .getObjectRegistry().getServiceInterface("deploy");
    String[] apps = deploy.listApplications(container, servers);
    for (int i = 0; i < apps.length; i++) {
      String app = (String) apps[i];
      printAppName(app, deploy.getApplicationStatus(app, servers[0]), writer);
      String[] elements = deploy.listElements(container, app, servers);
      for (int j = 0; j < elements.length; j++) {
        String element = elements[j];
        int lastIndex = element.lastIndexOf("- web");
        if (lastIndex < 0) { continue; }
        String alias = element.substring(0, lastIndex).trim();
        if (hostProperties.isApplicationAlias(alias)) {
          printAlias(alias, hostProperties.isApplicationAliasEnabled(alias), 
            hostProperties.getHostName(), writer);
        }
      }
    }
  }

  private void findAppNameByAlias(PrintWriter writer, HostPropertiesRuntimeInterface hostProperties, String aliasToFind)
      throws RemoteException {
    printHeader(writer);
    String container = "servlet_jsp";
    String[] servers = new String[1];
    servers[0] = context.getClusterContext().getClusterMonitor()
        .getCurrentParticipant().getName();
    DeployService deploy = (DeployService) context.getContainerContext()
        .getObjectRegistry().getServiceInterface("deploy");
    String[] apps = deploy.listApplications(container, servers);
    for (int i = 0; i < apps.length; i++) {
      String app = (String) apps[i];
      String[] elements = deploy.listElements(container, app, servers);
      for (int j = 0; j < elements.length; j++) {
        String element = elements[j];
        String alias = element.substring(0, element.lastIndexOf("- web")).trim();
        if (hostProperties.isApplicationAlias(alias) && aliasToFind.equals(alias)) {
          printAppName(app, deploy.getApplicationStatus(app, servers[0]), writer);
          printAlias(alias, hostProperties.isApplicationAliasEnabled(alias), 
            hostProperties.getHostName(), writer);
          return;
        }
      }
    }
    
    writer.print("Application alias ");
    writer.print(aliasToFind);
    writer.print(" is not available on host ");
    writer.println(hostProperties.getHostName());
  }
  
  private void printHeader(PrintWriter writer) {
    writer.println();
    writer.println("APPLICATION/ALIAS                                   STATUS");
    writer.println("----------------------------------------------------------------------");
  }
  
  private void printAppName(String appName, String status, PrintWriter writer) {
    writer.print("[36;1m");
    writer.print(appName);
    writer.print("  ");
    printChar(' ', 50 - appName.length(), writer);
    writer.print("STOPPED".equals(status) ? "[31;1m" : "[36;1m");
    writer.print(status);
    writer.println("[37;0m");
  }
  
  private void printAlias(String alias, boolean enabled, String host, PrintWriter writer) {
    writer.print("  ");
    writer.print(alias);
    writer.print("  ");
    printChar(' ', 50 - alias.length() - 2, writer);
    writer.print(enabled?"[37;0m":"[31;1m");
    writer.print(enabled ? "active" : "inactive");
    writer.println("[37;0m");
  }
  
  private void printChar(char ch, int length, PrintWriter writer) {
    for (int i = 0; i < length; i++) {
      writer.print(ch);
    }
  }

  /**
   * Gets the name of the command
   *
   * @return   The name of the command.
   */
  public String getName() {
    return "APPLICATION_ALIAS";
  }

  /**
   * Returns the name of the group the command belongs to
   *
   * @return   The name of the group of commands, in which this command belongs.
   */
  public String getGroup() {
    return "http";
  }

  /**
   * Gives the name of the supported shell providers
   *
   * @return   The Shell providers' names who supports this command.
   */
  public String[] getSupportedShellProviderNames() {
    return new String[] {"InQMyShell"};
  }

  /**
   * Gives a short help message about the command
   *
   * @return   A help message for this command.
   */
  public String getHelpMessage() {
    String nl = SystemProperties.getProperty("line.separator");
    return "Manages application aliases" + nl + "USAGE: " + getName() + " [-host <hostName>] <aliasName> [-disable | -enable] [-printAppName]" +  nl +
            nl +
           "Parameters:" + nl +
           "  -host <host name>      The virtual host name. If it is not specified, the default host is displayed" + nl +
           "  <aliasName>            The name of an alias on the virtual host" + nl +
           "  -disable               Disable the alias on the specific host" + nl +
           "  -enable                Enable the alias on the specific host" + nl +
           "  -printAppName          If alias is available - the name of the application" + nl +
           "                         that owns this alias, else - list of application " + nl +
           "                         aliases grouped by application" + nl + 
           nl +
           "Used without parameters the command lists all application aliases available on " + nl +
           "the virtual host (if specified)." + nl +
           "If only <aliasName> is specified the command returns if this alias is available" + nl +
           "on the virtual host" + nl;
  }

}
