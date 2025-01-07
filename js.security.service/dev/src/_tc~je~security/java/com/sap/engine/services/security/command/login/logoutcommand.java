/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.security.command.login;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.lib.security.ReusableLoginContext;

import javax.security.auth.login.LoginContext;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 *  This class uses the login module of the security service to logout a user.
 *
 * @author  Svetlana Stancheva
 * @version 4.0.3
 */
public class LogoutCommand implements Command {

  private PrintWriter out = null;

  /**
   *  Constructs a login command that uses the given LoginManager.
   */
  public LogoutCommand(SecurityContext root) {
  }

  /**
   *  Executes the command.
   *
   * @param  env  the environment of the corresponding process ,which executes the command
   * @param  is   an input stream for this command
   * @param  os   an output stream for the resusts of this command
   * @param  params  parameters of the command
   */
  public void exec(Environment env, InputStream is, OutputStream os, String[] params) {
    out = new PrintWriter(os, true);

    if ((params.length > 0) && (params[0].equals("-?") || params[0].equalsIgnoreCase("-h") || params[0].equalsIgnoreCase("-help"))) {
      out.println(getHelpMessage());
      return;
    }

    try {
      LoginContext login = (LoginContext) ((Hashtable) env.getContext()).get("LOGIN_CONTEXT");

      if (login != null) {
        login.logout();

        if (login instanceof ReusableLoginContext) {
          ((ReusableLoginContext) login).close();
        }
        ((Hashtable) env.getContext()).remove("LOGIN_CONTEXT");

        out.println(" user logged out successfully.");
      } else {
        out.println(" user is not logged in.");
      }
    } catch (Exception ex) {
      PrintWriter err = new PrintWriter(env.getErrorStream(), true);
      err.println("Logout failed! ");

      if (ex.getMessage() != null) {
        err.println(" reason: " + ex.getMessage());
      }
    }
  }

  /**
   *  Returns the name of the group of this command.
   *
   * @return  the name of the group of this command.
   */
  public String getGroup() {
    return "login";
  }

  /**
   *  Returns a printable explanation of how the command is used.
   *
   * @return  explanatory printable string.
   */
  public String getHelpMessage() {
    return "Logs out the current user.\nUsage: LOGOUT";
  }

  /**
   * Gets a name for this command.
   *
   * @return     the message
   */
  public String getName() {
    return "logout";
  }

  public String[] getSupportedShellProviderNames() {
    return null;
  }

}