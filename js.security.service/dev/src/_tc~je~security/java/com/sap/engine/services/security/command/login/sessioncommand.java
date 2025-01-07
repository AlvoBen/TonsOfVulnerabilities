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

import java.io.*;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.services.security.login.SecurityContext;
import com.sap.engine.services.security.login.SecuritySession;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.server.AuthenticationContextImpl;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;

/**
 *  This class uses the login module of the security service to login a user.
 *
 * @author  Svetlana Stancheva
 * @version 4.0.3
 */
public class SessionCommand implements Command {

  private PrintWriter out = null;
  
  final String HELP_MESSAGE = "Displays information about user sessions.\nUsage: " + 
			"SESSION [parameter]\nParameters:\n\t[parameter]:\n\t-list\t\t- Lists all security sessions." +
			"If no parameter is specified, displays information about current user's session." + 
			"\n\t-count\t\t- Returns count of all started security sessions";

  /**
   *  Constructs a login command that uses the given LoginManager.
   *
   * @param  root  the login module implementation of the security service.
   */
  public SessionCommand(com.sap.engine.interfaces.security.SecurityContext root) {

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

    if (params.length == 0){
    	try {
        ThreadContext context = SecurityServerFrame.threadContext.getThreadContext();

        if (context == null) {
          out.println(" System call ");
        } else {
          SecurityContext security = (SecurityContext) context.getContextObject("security");

          if (security.getSession() == null) {
            out.println(" No security session ");
          } else {
            out.println(security.toString());
          }
        }
      } catch (ThreadDeath td) {
				throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable e) {
				e.printStackTrace();
				out.println("Cannot load info for current session.");
			}
    } else if ((params.length == 1) && (params[0].equals("-?") || params[0].equalsIgnoreCase("-h") || params[0].equalsIgnoreCase("-help"))) {
      out.println(getHelpMessage());
      return;
    } else if ((params.length == 1) && (params[0].equals("-dump"))) {
      try {
        out.println(AuthenticationContextImpl.getSessionPool().toString());
      } catch (ThreadDeath td) {
				throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable e) {
				e.printStackTrace();
      }
    } else if ((params.length == 1) && (params[0].equals("-count"))) {
    	try {
      	SecuritySession[] sessions = AuthenticationContextImpl.getSessionPool().listSessions();
      	
      	if (sessions == null){
      		out.println(" Number of security sessions: 0"  );
      	} else {
        	out.println(" Number of security sessions: " + sessions.length);
      	}

    	} catch (ThreadDeath td) {
				  throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable t) {
				t.printStackTrace();
				out.println("Cannot count the currently opened security sessions.");
			}
    } else if ((params.length == 1) && (params[0].equals("-list"))) {
      try {
        SecuritySession[] sessions = AuthenticationContextImpl.getSessionPool().listSessions();

        if ((sessions == null) || (sessions.length == 0)) {
          out.println(" No security sessions ");
          return;
        }

        for (int i = 0; i < sessions.length; i++) {
          out.print(" ");
          out.print(i + 1);
          out.print(". ");
          out.println(sessions[i]);	
         
        }
        out.println();
      } catch (ThreadDeath td) {
				throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable e) {
				e.printStackTrace();
				out.println("Cannot load the list of currently opened security sessions.");
			}
    } else if (params.length > 1){
    	out.println(HELP_MESSAGE);
    } else {
    	out.println(HELP_MESSAGE);
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
      return HELP_MESSAGE;
  }

  /**
   * Gets a name for this command.
   *
   * @return     the message
   */
  public String getName() {
    return "session";
  }

  public String[] getSupportedShellProviderNames() {
    return null;
  }
}