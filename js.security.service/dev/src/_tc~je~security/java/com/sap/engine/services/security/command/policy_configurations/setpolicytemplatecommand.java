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

import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.interfaces.security.SecurityContext;

/**
	*	Sets a configuration template for a policy configuration. 
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class SetPolicyTemplateCommand implements Command{
  
  private SecurityContext security = null;
  private PrintWriter out = null;
 	private final String HELP_MESSAGE = "Replaces all current modules or templates from the " + 
 		"policy configuration and adds all modules from the selected template. \n" +
 		"Usage: SET_POLICY_TEMPLATE <configurationName> <templateConfigurationName> \n" + 
 		"Arguments:\n\t<configurationName>\t- The name of the configuration.\n\t" +
 		"<templateConfigurationName>\t- The name of the template to set for the configuration.";

	public SetPolicyTemplateCommand(com.sap.engine.interfaces.security.SecurityContext root) {
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
		
		if (params.length == 2) {			
			AuthenticationContext authentication = null;
			String policyName = params[0];
			String templateName = params[1];

			try{

			  if(security.getPolicyConfigurationContext(policyName) == null) {
		    	out.println("The specified policy configuration does not exist. Cannot apply the specified template");
				} else if(security.getPolicyConfigurationContext(templateName) == null) {
		    	out.println("The specified template configuration does not exist. Cannot apply the template. ");
				}	else {
				  authentication = security.getPolicyConfigurationContext(policyName).getAuthenticationContext();
				  authentication.setLoginModules(templateName);
				}				
			} catch (SecurityException se) {
				out.println("Cannot set policy template");
				se.printStackTrace();
			} catch (ThreadDeath td) {
				throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable t) {
				out.println("Cannot set policy template");
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
		return "set_policy_template";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
}
