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
 * Manages http aliases.
 *
 * @author Pavel Zlatarev
 * @version 6.25
 */
import java.io.*;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.httpserver.HttpRuntimeInterface;
import com.sap.engine.services.httpserver.HostPropertiesRuntimeInterface;
import com.sap.engine.boot.SystemProperties;

public class HttpAliasCommand implements Command {

  HttpRuntimeInterface http;

  public HttpAliasCommand(HttpRuntimeInterface http) {
    this.http = http;
  }

  public void exec(Environment env, InputStream is, OutputStream os, String[] params) {
    PrintWriter pw = new PrintWriter(os, true);
    int count = params.length;
    String host = null;
    String alias = null;
    String path = null;
    boolean remove = false; 
    if(count > 0 && (params[0].toUpperCase().equals("-H") || params[0].equals("-?") || params[0].toUpperCase().equals("-HELP"))) {
      pw.println(getHelpMessage());
      return;
    } else {
      try {
        for (int c = 0; c < count; c++) {
          if ("-host".equals(params[c])) {
            if (host != null) {
              throw new Exception("Host Name Specified Twice");
            }
            if( params.length < c){
              throw new Exception("No host specified.");
            }
            host = params[++c];
          } else if ("-remove".equals(params[c])) {
            if (remove) {
              throw new Exception("Remove Specified Twice");
            }
            remove = true;
          } else {
            if (alias == null) {
              alias = params[c];
            } else if (path == null) {
              path = params[c];
            } else {
              throw new Exception("Path Specified Twice");
            }
          }
        }

        if (host == null) {
          host = "default";
        }
        if (alias == null && remove) {
          throw new Exception("Alias Name Not Specified");
        }
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        pw.println("ERROR: " + e.getMessage());
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
      if (alias == null) {
        pw.println("HTTP aliases:");
        listAll(pw, hosts[hostIndex], "  ");
        pw.println();
      } else {
        String oldPath = hosts[hostIndex].getAliasValue(alias);
        if (remove) {
          if (oldPath == null) {
            pw.println("HTTP Alias " + alias + " Not Found");
          } else {
            hosts[hostIndex].removeHttpAlias(alias);
          }
        } else {
          //////////////////////////////////////////
          if (path != null) {
            hosts[hostIndex].addHttpAlias(alias, path);
            pw.println("Alias " + alias + " added successfully");
          } else {
            if (oldPath == null) {
              throw new Exception("HTTP Alias " + alias + " Not Found");
            }
            list(pw, alias, oldPath, "  ");
          }
        }

      }

      http.clearCache(host);
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable e) {
      pw.println("ERROR: " + e.getMessage());
    }
  }

  private void listAll(PrintWriter pw, HostPropertiesRuntimeInterface hostProperties, String tab) throws Exception {
    String[] en = hostProperties.getAliasNames();
    for (int i = 0; en != null && i < en.length; i++) {
      if (!hostProperties.isApplicationAlias(en[i])) {
        list(pw, en[i], hostProperties.getAliasValue(en[i]), tab);
      }
    }
  }

  private void list(PrintWriter pw, String alias, Object path, String tab) throws Exception {
    if (path == null) {
      pw.println(tab + alias);
    } else {
      pw.println(tab + alias + " -> " + HostCommand.p(path));
    }
  }

  public String getName() {
    return "HTTP_ALIAS";
  }

  public String getGroup() {
    return "http";
  }

  public String getHelpMessage() {
    String nl = SystemProperties.getProperty("line.separator");
    return "Manages http aliases." + nl +
           "USAGE: " + getName() + " [-host <hostName>] <alias> [path] [-remove]" + nl +
           nl +
           "Parameters:" + nl +
           "  -host <hostName>      The virtual host name. If it is not specified, the default host is displayed." + nl +
           "  <alias>               The alias name. If only <alias> is specified the command returns the alias path." + nl +
           "  <path>                The alias path." + nl +
           "  -remove               Removes the alias." + nl +
           nl +
           "Used without parameters the command lists all http aliases with their paths." + nl +
           "Example:" + nl +
           getName() + " <alias> <path>        Creates http alias with alias name specified with <alias> and alias path - <path>." + nl;
  }

  public String[] getSupportedShellProviderNames() {
    return new String[] {"InQMyShell"};
  }

}

