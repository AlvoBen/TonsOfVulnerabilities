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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.services.security.login.SecurityContext;
import com.sap.engine.services.security.login.SecuritySession;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.engine.services.security.server.AuthenticationContextImpl;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;

/**
	*	Terminates security sessions. 
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/

public class TerminateSessionCommand implements Command {

  private PrintWriter out = null;
 	private final String HELP_MESSAGE = "Terminates the session with the specified session id.\n" +
 			"Usage: TERMINATE_SESSION <sessionId>\nArguments:\n\t<sessionId>\t- ID of the session. ";


  public TerminateSessionCommand(com.sap.engine.interfaces.security.SecurityContext root) {

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
  	
  	if (params.length == 1) {
  	  long sessionId = -1;
  	  long currentSessionId = -1;

  	  try {
      	sessionId = Long.parseLong(params[0]);
    	} catch (NumberFormatException e) {
        out.println(HELP_MESSAGE);
        return;
    	}
    	
    	if (sessionId >= 0) {
       	try {
          ThreadContext context = SecurityServerFrame.threadContext.getThreadContext();

          if (context != null) {
            SecurityContext security = (SecurityContext) context.getContextObject("security");

            if (security.getSession() != null) {
            	currentSessionId = security.getSession().getSessionNumber();
            }
          }
          
          if (sessionId != currentSessionId) {
          	SecuritySession[] sessions = AuthenticationContextImpl.getSessionPool().listSessions();
          	
          	if (sessions != null && sessions.length > 0){
          		for(int i = 0; i < sessions.length; i++){
          			if (sessions[i].getSessionNumber() == sessionId) {
          				AuthenticationContextImpl.getSessionPool().removeSessions(sessions[i]);
               		return;
          			}
          		}
          		out.println("Specified sessionId is not a valid session id");
          	} else {
           		out.println("No active security sessions");
          	}
          } else {
            out.println("Cannot terminate the current session");
          }
      	} catch (ThreadDeath td) {
  			  throw td;
				} catch (OutOfMemoryError aome) {
					throw aome;
				} catch (Throwable t) {
					t.printStackTrace();
          out.println("Cannot terminate session");
          
      	}    	
    	} else {
        out.println(HELP_MESSAGE);
    	}
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
    return "terminate_session";
  }

  public String[] getSupportedShellProviderNames() {
    return null;
  }
}

