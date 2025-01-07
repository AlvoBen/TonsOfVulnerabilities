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
package com.sap.engine.services.security.command.policy_configurations;

import java.io.*;
import java.lang.OutOfMemoryError;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.SecurityContext;


/**
	*	Adds a new policy configuration.
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class AddPolicyConfigurationCommand implements Command{
  private SecurityContext security = null;
  private PrintWriter out = null;
 	private final String HELP_MESSAGE = "Adds a new policy configuration.\n" +
 			"Usage: ADD_POLICY_CONFIGURATION <configurationName>\nArguments:\n\t<configurationName>\t-" + 
 			" The name of the configuration.";
 	
	public AddPolicyConfigurationCommand(com.sap.engine.interfaces.security.SecurityContext root) {
		security = root;
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
			AuthenticationContext authentication = null;
			try {
				if(security.getPolicyConfigurationContext(params[0]) != null) {
				  out.println("Policy configuration with the specified name already exists");  
				} else {
		   	  security.registerPolicyConfiguration(params[0], SecurityContext.TYPE_CUSTOM);
		   	  authentication = security.getPolicyConfigurationContext(params[0]).getAuthenticationContext();
				  if (authentication == null) {
				    out.println("Cannot register policy configuration!");  
				  }
				} 
			} catch (SecurityException se) {
				out.println("Cannot register policy configuration!");
				se.printStackTrace();
			} catch (ThreadDeath td) {
			  throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable t) {
				out.println("Cannot register policy configuration!");
				t.printStackTrace();
			}

		} else {
    	out.println(HELP_MESSAGE);
		}
	}
	
	public String getGroup() {
		return "policy_configurations";
	}
	
	public String getHelpMessage() {
		return HELP_MESSAGE;
	}
	
	public String getName() {
		return "add_policy_configuration";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
}