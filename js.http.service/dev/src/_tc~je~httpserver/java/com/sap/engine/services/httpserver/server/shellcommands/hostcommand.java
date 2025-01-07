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
package com.sap.engine.services.httpserver.server.shellcommands;

/*
 * Manages virtual hosts.
 *
 * @author Pavel Zlatarev
 * @version 6.25
 */
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.httpserver.HttpRuntimeInterface;
import com.sap.engine.services.httpserver.HostPropertiesRuntimeInterface;
import com.sap.engine.boot.SystemProperties;

import java.io.*;

public class HostCommand implements Command {

  HttpRuntimeInterface http;

  public HostCommand(HttpRuntimeInterface http) {
    this.http = http;
  }

  public void exec(Environment env, InputStream is, OutputStream os, String[] params) {
    PrintWriter pw = new PrintWriter(os, true);
    int count = params.length;
    boolean add = false;
    boolean remove = false;
    String root = null;
    String startPage = null;
    String host = null;
    int log = 0;
    int cache = 0;
    int dirList = 0;
    int keepAlive = 0;
    if(count > 0 && (params[0].toUpperCase().equals("-H") || params[0].equals("-?") || params[0].toUpperCase().equals("-HELP"))) {
      pw.println(getHelpMessage());
      return;
    } else {
      try {
        for (int c = 0; c < count; c++) {
          if ("-add".equals(params[c])) {
            if (add) {
              throw new Exception("Add Specified Twice");
            }
            add = true;
          } else if ("-remove".equals(params[c])) {
            if (remove) {
              throw new Exception("Remove Specified Twice");
            }
            remove = true;
          } else if ("-root".equals(params[c])) {
            if (root != null) {
              throw new Exception("Root Specified Twice");
            }
            root = params[++c];
          } else if ("-startPage".equals(params[c])) {
            if (startPage != null) {
              throw new Exception("Start Page Specified Twice");
            }
            startPage = params[++c];
          } else if ("-enable".equals(params[c])) {
            if (params.length > c && params[c+1].equals("log")) {
              log = +1;
              c++;
            } else if (params.length > c && params[c+1].equals("cache")) {
              cache = +1;
              c++;
            } else if (params.length > c && params[c+1].equals("dirList")) {
              dirList = +1;
              c++;
            } else if (params.length > c && params[c+1].equals("keepAlive")) {
              keepAlive = +1;
              c++;
            }
          } else if ("-disable".equals(params[c])) {
            if (params.length > c && params[c+1].equals("log")) {
              log = -1;
              c++;
            } else if (params.length > c && params[c+1].equals("cache")) {
              cache = -1;
              c++;
            } else if (params.length > c && params[c+1].equals("dirList")) {
              dirList = -1;
              c++;
            } else if (params.length > c && params[c+1].equals("keepAlive")) {
              keepAlive = -1;
              c++;
            }
          } else {
            if (host != null) {
              throw new Exception("Unknown parameter:"+params[c]);
            }
            host = params[c];
          }
        }

        if (host == null && (add || remove || root != null || startPage != null)) {
          throw new Exception("Host Name Not Specified");
        }
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        pw.println("ERROR: "+e.getMessage());        
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

      if (count == 0) {
        listAll(pw, hosts);
        return;
      }

      if (count == 1) {
        list(pw, host, hosts, hostIndex);
        return;
      }

      if (remove) {
        if (hostIndex == -1) {
          throw new Exception("Virtual Host " + host + " Not Found");
        }
        if ("default".equals(host)) {
          throw new Exception("Can Not Remove The Default Host");
        }
        http.removeHost(host);
        hostIndex = -1;
      }

      if (add || (hostIndex == -1 && !remove)) {
        if (hostIndex != -1) {
          throw new Exception("Virtual Host " + host + " Already Exists");
        }

        if (add) {
          http.createHost(host);
        }
        if (host == null) {
          host = "default";
        }
        hosts = http.getAllHostsTemp();
        hostIndex = -1;
        for (int c = hosts.length; --c >= 0;) {
          if (hosts[c].getHostName().equals(host)) {
            hostIndex = c;
            break;
          }
        }
      }

      if (hostIndex == -1 && (root != null || startPage != null || log != 0 || cache != 0 || dirList != 0 || keepAlive != 0)) {
        throw new Exception("Virtual Host " + host + " Not Found");
      }

      if (root != null) {
        hosts[hostIndex].setRootDir(root);
      }

      if (startPage != null) {
        hosts[hostIndex].setStartPage(startPage);
      }

      if (log != 0) {
        hosts[hostIndex].setLogEnabled(log == 1);
      }

      if (cache != 0) {
        hosts[hostIndex].setUseCache(cache == 1);
      }

      if (dirList != 0) {
        hosts[hostIndex].setList(dirList == 1);
      }

      if (keepAlive != 0) {
        hosts[hostIndex].setKeepAliveEnabled(keepAlive == 1);
      }

      if (count > 1) {
        http.clearCache(host);
      }
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      pw.println("ERROR: " + e.getMessage());
    }
  }

  private void listAll(PrintWriter pw, HostPropertiesRuntimeInterface[] hosts) throws Exception {
    for (int c = 0; c < hosts.length; c++) {
      list(pw, hosts[c]);
    }
  }

  private void list(PrintWriter pw, String hostName, HostPropertiesRuntimeInterface[] hosts, int hostIndex) throws Exception {
    if (hostIndex == -1) {
      pw.println("Virtual Host " + hostName + " Not Found");
    } else {
      list(pw, hosts[hostIndex]);
    }
  }

  private void list(PrintWriter pw, HostPropertiesRuntimeInterface hostProps) throws Exception {
    pw.println();
    pw.println(hostProps.getHostName());
    pw.println("  root        " + p(hostProps.getRootDir()));
    pw.println("  startPage   " + p(hostProps.getStartPage()));
    pw.println();
    pw.print("  " + (hostProps.isLogEnabled() ? "enabled" : "disabled") + " log");
    pw.print("  " + (hostProps.isUseCache() ? "enabled" : "disabled") + " cache");
    pw.print("  " + (hostProps.isList() ? "enabled" : "disabled") + " dirList");
    pw.print("  " + (hostProps.isKeepAliveEnabled() ? "enabled" : "disabled") + " keepAlive");
    pw.println();
    pw.println();
    pw.println("  HTTP aliases:");
    listHttpAliases(pw, hostProps, "    ");
    pw.println();
    pw.println("  Applications aliases:");
    listAppAliases(pw, hostProps, hostProps.getHostName(), "    ");
  }

  static String p(Object s) {
    return s == null ? "" : s.toString();
  }

  public String getName() {
    return "HOST";
  }

  public String getGroup() {
    return "http";
  }

  public String getHelpMessage() {
    String nl = SystemProperties.getProperty("line.separator");
    return "Manages virtual hosts" + nl +
           "USAGE: " + getName() + " <hostName> [-add] [-remove] [-root <httpRootPath>] [-startPage <startPagePath>]" + nl +
           "                       [-enable | -disable log] [-enable | -disable cache] [-enable | -disable dirList]" + nl +
           nl +
           "Parameters:" + nl +
           "  <hostName>                     The virtual host name" + nl +
           "  -add                           Adds a new virtual host" + nl +
           "  -remove                        Removes the specified virtual host" + nl +
           "  -root <httpRootPath>           Sets the specified path as a http root" + nl +
           "  -startPage <startPagePath>     Sets the specified path as a http start page" + nl +
           "  -enable | -disable log         Enables|Disables logging" + nl +
           "  -enable | -disable cache       Enables|Disables caching" + nl +
           "  -enable | -disable dirList     Enables|Disables directory listing" + nl +
           "  -enable | -disable keepAlive   Enables|Disables keep-alive" + nl +
           nl +
           "Used without parameters the command lists all virtual hosts with their properties" + nl +
           "If only <hostName> is specified the command lists all properties of the host" + nl +
           "Each parameter except <hostName> forces this host cache cleaning (even when the parameter is not recognized)" + nl;
  }

  public String[] getSupportedShellProviderNames() {
    return new String[] {"InQMyShell"};
  }

  private void listAppAliases(PrintWriter pw, HostPropertiesRuntimeInterface hostProperties, String host, String tab) throws Exception {
    String[] en = hostProperties.getAliasNames();
    for (int i = 0; en != null && i < en.length; i++) {
      if (!hostProperties.isApplicationAlias(en[i])) {
        continue;
      }
      if (hostProperties.isApplicationAliasEnabled(en[i])) {
        pw.println(tab + en[i] + tab + "- (active on "+ host +" host)");
      } else {
        pw.println(tab + en[i] + tab + "- (inactive on "+ host +" host)");
      }
    }
  }

  private void listHttpAliases(PrintWriter pw, HostPropertiesRuntimeInterface hostProperties, String tab) throws Exception {
    String[] en = hostProperties.getAliasNames();
    for (int i = 0; en != null && i < en.length; i++) {
      if (!hostProperties.isApplicationAlias(en[i])) {
        pw.println(tab + en[i] + " -> " + p(hostProperties.getAliasValue(en[i])));
      }
    }
  }
}

