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

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.SecurityContext;

/**
	*	Removes the specified policy configuration.
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class RemovePolicyConfigurationCommand implements Command{
  private SecurityContext security = null;
	private PrintWriter out = null;
	private final static String IS_CUSTOM = "custom";
  private byte type = SecurityContext.TYPE_INVALID;
 	private final String HELP_MESSAGE = "Removes the specified policy configuration.\n" +
 			"Usage: REMOVE_POLICY_CONFIGURATION <configurationName> \nArguments:\n\t" + 
 			"<configurationName>\t- The name of the configuration.";
	
	public RemovePolicyConfigurationCommand(com.sap.engine.interfaces.security.SecurityContext root) {
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
			try{
				if(security.getPolicyConfigurationContext(params[0]) == null) {
		    	out.println("The specified policy configuration does not exist. Cannot be removed.");
				} else {
				  type = security.getPolicyConfigurationContext(params[0]).getPolicyConfigurationType();
				  authentication = security.getPolicyConfigurationContext(params[0]).getAuthenticationContext();
					if ((type == SecurityContext.TYPE_CUSTOM) 
					    || ("yes".equalsIgnoreCase(authentication.getProperty(IS_CUSTOM))))  {
						security.unregisterPolicyConfiguration(params[0]);
					}	else {
					  out.println("Cannot remove the specified policy configuration.");
					}
				}
			} catch (SecurityException se) {
				se.printStackTrace();
				out.println("The specified policy cannot be removed.");
			} catch (ThreadDeath td) {
				throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable e) {
				out.println("The specified policy cannot be removed.");
				e.printStackTrace();
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
		return "remove_policy_configuration";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }
 
}
