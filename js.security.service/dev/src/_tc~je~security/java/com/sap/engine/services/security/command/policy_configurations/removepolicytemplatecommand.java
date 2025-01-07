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
	*	Removes configuration template from a policy configuration. 
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class RemovePolicyTemplateCommand implements Command{
	
  private SecurityContext security = null;
  private PrintWriter out = null;
 	private final String HELP_MESSAGE = "Removes template from the specified policy configuration" +
 	    "if such is used.\nUsage: REMOVE_POLICY_TEMPLATE <configurationName> \nArguments:\n\t" +
 	    "<configurationName>\t- The name of the configuration.";

	public RemovePolicyTemplateCommand(com.sap.engine.interfaces.security.SecurityContext root) {
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
			String template = null;
			try{
				if(security.getPolicyConfigurationContext(params[0]) == null) {
		    	out.println("The specified policy configuration does not exist. Cannot remove the template.");
				} else {
				  authentication = security.getPolicyConfigurationContext(params[0]).getAuthenticationContext();
					template = authentication.getTemplate();
					if (template == null) {
					  out.println("No template is assigned to the specified policy configuration.");
					} else {
					  template = null;
					  authentication.setLoginModules(template);
					}
				}
			} catch (SecurityException se) {
				out.println("Cannot remove policy template");
				se.printStackTrace();
			} catch (ThreadDeath td) {
				throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable t) {
				out.println("Cannot remove policy template");
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
		return "remove_policy_template";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
}