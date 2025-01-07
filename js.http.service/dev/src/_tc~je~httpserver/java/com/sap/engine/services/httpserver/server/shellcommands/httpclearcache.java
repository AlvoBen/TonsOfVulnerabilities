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
 * HTTPClearCache class releases the execution of CLEARHTTPCACHE command.
 *
 * @author Bojidar Kadrev
 * @version 4.0
 */
import java.io.*;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.httpserver.interfaces.HttpProvider;
import com.sap.engine.boot.SystemProperties;

public class HTTPClearCache implements Command {
  private HttpProvider http;

  public HTTPClearCache(HttpProvider http) {
    this.http = http;
  }

  /**
   * The implementation of the corresponding method in the Command interface.
   *
   * @param   env  Environment object
   * @param   is  InputStream object
   * @param   os  OutputStream object
   * @param   params  an array of String objects which are the input parameters of the
   *                  corresponding command
   */
  public void exec(Environment env, InputStream is, OutputStream os, String[] params) {
    PrintWriter pw = new PrintWriter(os, true);
    int count = params.length;

    if ((count != 0) || (count > 0 && (params[0].toUpperCase().equals("-H") || params[0].equals("-?") || params[0].toUpperCase().equals("-HELP")))) {
      pw.println(getHelpMessage());
    } else {
      try {
        http.clearCache();
        pw.println("\nHTTPCache cleared.");
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        pw.println("ERROR: ");
        e.printStackTrace();
        pw.println(getHelpMessage());
        return;
      }
    }
  }

  /**
   * Gets the command's name
   *
   * @return Name of the command
   */
  public String getName() {
    return "CLEAR_HTTP_CACHE";
  }

  /**
   * Gets the command's group
   *
   * @return Group name of the command
   */
  public String getGroup() {
    return "http";
  }

  /**
   * Gets the command's help message
   *
   */
  public String getHelpMessage() {
    String nl = SystemProperties.getProperty("line.separator");
    return "Removes all files in the cache previously stored there by the HTTP Provider Service. " + nl +
           "When using the cache, if a file has been modified the cache returns the " + nl +
           "previous content (not the modified one). For a modification to take effect " + nl +
           "the cache has to be cleared first and then it will cache the new content. " + nl +
           "Usage: " + getName() + "";
  }

  /**
   * ### !!! TO WRITE JAVA DOC !!! ###
   *
   * @return
   */
  public String[] getSupportedShellProviderNames() {
    return new String[] {"InQMyShell"};
  }

}

