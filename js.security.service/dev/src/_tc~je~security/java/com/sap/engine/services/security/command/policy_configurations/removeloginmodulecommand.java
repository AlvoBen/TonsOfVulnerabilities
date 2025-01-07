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
import javax.security.auth.login.AppConfigurationEntry;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.SecurityContext;

/**
 *	Removes a login module from policy configuration. 
 *
 * 	@author Diana Berberova
 *	@version 1.0
 * 
 */
public class RemoveLoginModuleCommand implements Command{
	
  private SecurityContext security = null;
	private PrintWriter out = null;
  private final String HELP_MESSAGE = "Removes the specified module from the policy configuration." + 
			"If a configuration template was applied to the specified policy configuration, error message is returned." +
			"\nUsage: REMOVE_LOGIN_MODULE <configurationName> <modulePosition> \nArguments:\n\t<configurationName>\t-" + 
			" The name of the configuration.\n\t<modulePosition>\t - The position of the module to be removed.";
	
  public RemoveLoginModuleCommand(com.sap.engine.interfaces.security.SecurityContext root) {
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
			AppConfigurationEntry[] loginModules = null;
			AppConfigurationEntry[] newLoginModules = null;
			AuthenticationContext authentication = null;

			try{
				if(security.getPolicyConfigurationContext(params[0]) == null) {
		    	out.println("The specified policy configuration does not exist. Cannot remove the template.");
				} else {

				  authentication = security.getPolicyConfigurationContext(params[0]).getAuthenticationContext();
					loginModules = authentication.getLoginModules();

					if (authentication.getTemplate() != null) {
						out.println("Cannot remove login module. Template is assigned to the policy configuration.");
					} else {					
					  int removeIndex = Integer.parseInt(params[1]);

			      if (removeIndex > 0 && removeIndex <= loginModules.length) {
							
			      	newLoginModules = new AppConfigurationEntry[loginModules.length - 1];

							System.arraycopy(loginModules, 0, newLoginModules, 0, (removeIndex - 1));
							if (removeIndex < loginModules.length) {
				        System.arraycopy(loginModules, removeIndex, newLoginModules, (removeIndex - 1), (loginModules.length - removeIndex));
							}

							authentication.setLoginModules(newLoginModules);
			      } else {
							out.println("The specified position is not valid position of login module.");
			      }
					}
				}
			} catch (SecurityException se) {
				se.printStackTrace();
				out.println("Cannot remove login module.");
			} catch (NumberFormatException e) {
				out.println(HELP_MESSAGE);
	    } catch (ThreadDeath td) {
	  		throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable t) {
				t.printStackTrace();
				out.println("Cannot remove login module.");
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
		return "remove_login_module";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }  
}
