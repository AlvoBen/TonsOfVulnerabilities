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
import java.util.*;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.userstore.config.LoginModuleConfiguration;
import javax.security.auth.login.AppConfigurationEntry;


/**
	*	Adds a new login module to a policy configuration.
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class AddLoginModuleCommand implements Command{
	
  private SecurityContext security = null;
	private PrintWriter out = null;
	private final String HELP_MESSAGE = "Adds a new login module. If the optional parameters " +
			"[flag] and [position] are not provided, the module is added with the default flag " +
			"(sufficient) and at the last possible position in the list of login modules. " +
			"If a configuration template was applied to the specified policy configuration, " + 
			"error message is returned. \nUsage: ADD_LOGIN_MODULE  [<configurationName> <moduleName> " + 
			"[<flag>] [<position>]]  |  -available \nArguments:\n\t<configurationName>\t- " + 
			"The name of the configuration.\n\t<moduleName>\t - The name of the module to be added." + 
			"\n\t<flag>\t- The name of the flag to be added. Possible values are " + 
			"REQUISITE | REQUIRED | SUFFICIENT | OPTIONAL. \n\t <position>\t - " + 
			"Position of the module in the list.  \n\t-available \t - " + 
			"Displays all login modules that you can add to the current policy configuration.";
		  
    
	public AddLoginModuleCommand(com.sap.engine.interfaces.security.SecurityContext root) {
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
		
		String sufficient = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT.toString().substring(AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT.toString().lastIndexOf(" ") + 1);
		String required = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED.toString().substring(AppConfigurationEntry.LoginModuleControlFlag.REQUIRED.toString().lastIndexOf(" ") + 1);
		String requisite = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE.toString().substring(AppConfigurationEntry.LoginModuleControlFlag.REQUISITE.toString().lastIndexOf(" ") + 1);
		String optional = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL.toString().substring(AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL.toString().lastIndexOf(" ") + 1);
		
		
    if ((params.length > 0) && (params[0].equals("-?") || params[0].equalsIgnoreCase("-h") || params[0].equalsIgnoreCase("-help"))) {
      out.println(getHelpMessage());
      return;
    }
		
    try {
  		AppConfigurationEntry[] loginModules = null;
  		AppConfigurationEntry[] newLoginModules = null;
  		LoginModuleConfiguration[] availableModules = null;
  		AppConfigurationEntry.LoginModuleControlFlag controlFlag = null;
  		
  		Map options = null;
  		String moduleName = null;
  		String flag = null;
  		int addIndex = 0;

			AuthenticationContext authentication = null;
		  
			availableModules = security.getUserStoreContext().getActiveUserStore().getConfiguration().getLoginModules();
			
			if (params.length == 1 && params[0].equalsIgnoreCase("-available")) {
					for (int i = 0; i < availableModules.length; i++) {
					out.println(availableModules[i].getName());
				}
			} else if (params.length > 1 && params.length < 5) {
					
			  if(security.getPolicyConfigurationContext(params[0]) == null) {
		    	out.println("The specified policy configuration does not exist. Cannot add login module.");
				} else {

				  authentication = security.getPolicyConfigurationContext(params[0]).getAuthenticationContext();
	
				  for (int i = 0; i < availableModules.length; i++) {
						if (params[1].equalsIgnoreCase(availableModules[i].getName())) {
							moduleName = availableModules[i].getName(); 
						}
					}
					
					if (moduleName == null) {
						out.println("The specified login module does not exist. ");
						return;
					} 
						
					loginModules = authentication.getLoginModules();
	
					if (authentication.getTemplate() != null) {
						out.println("Cannot add login module. Template is assigned to the policy configuration.");
						return;
					}
					
					
					if (params.length == 2) {
						flag = sufficient;
						addIndex = loginModules.length + 1;
					} else if (params.length == 3) {
						try {
							addIndex = Integer.parseInt(params[2]);
	
							if (addIndex <= 0 || addIndex > (loginModules.length + 1)) {
								out.println("The specified position is not valid a position in the list of login modules.");
								return;
							}
							flag = sufficient;
						} catch (NumberFormatException e) {
							if (params[2].equalsIgnoreCase(sufficient) || params[2].equalsIgnoreCase(requisite) || params[2].equalsIgnoreCase(required) || params[2].equalsIgnoreCase(optional)) {
								flag = params[2];
							} else {
								out.println("The specified flag is not a valid flag of login modules. Must be REQUISITE | REQUIRED | SUFFICIENT | OPTIONAL.");
								return;
							}
							addIndex = loginModules.length + 1;
						}
					} else if (params.length == 4) {
						try {
							addIndex = Integer.parseInt(params[2]);
							if (addIndex <= 0 || addIndex > (loginModules.length + 1)) {
								out.println("The specified position is not valid a position in the list of login modules.");
								return;
							}					
							if (params[3].equalsIgnoreCase(sufficient) || params[3].equalsIgnoreCase(requisite) || params[3].equalsIgnoreCase(required) || params[3].equalsIgnoreCase(optional)) {
								flag = params[3];
							} else {
								out.println("The specified flag is not a valid flag of login modules. Must be REQUISITE | REQUIRED | SUFFICIENT | OPTIONAL.");
								return;
							}
	
						} catch (NumberFormatException e) {
							try {
								addIndex = Integer.parseInt(params[3]);
								if (addIndex <= 0 || addIndex > (loginModules.length + 1)) {
								out.println("The specified position is not valid a position in the list of login modules.");
									return;
								}
								
								if (params[2].equalsIgnoreCase(sufficient) || params[2].equalsIgnoreCase(requisite) || params[2].equalsIgnoreCase(required) || params[2].equalsIgnoreCase(optional)) {
									flag = params[2];
								} else {
									out.println("The specified flag is not a valid flag of login modules. Must be REQUISITE | REQUIRED | SUFFICIENT | OPTIONAL.");
									return;
								}
							} catch (NumberFormatException ne) { 
									out.println(HELP_MESSAGE);
								return;
							}
						}
					} else {
			    	out.println(HELP_MESSAGE);
					}
					
		
					if (flag.equalsIgnoreCase(required)) {
						controlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
					} else if (flag.equalsIgnoreCase(requisite)) {
						controlFlag = AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;
					} else if (flag.equalsIgnoreCase(optional)) {
						controlFlag = AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;
					} else if (flag.equalsIgnoreCase(sufficient)) {
						controlFlag = AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;
					} 
					
					for (int i = 0; i < availableModules.length; i++) {
						if (availableModules[i].getName().equalsIgnoreCase(moduleName)) {
							options = availableModules[i].getOptions();
							break;
						}
					}
						
					if (options != null && controlFlag != null) {
						AppConfigurationEntry entry = new  AppConfigurationEntry(moduleName, controlFlag, options);
						newLoginModules = new AppConfigurationEntry[loginModules.length + 1];
						
						System.arraycopy(loginModules, 0, newLoginModules, 0, addIndex - 1);
						newLoginModules[addIndex - 1] = entry;
						System.arraycopy(loginModules, (addIndex - 1), newLoginModules, addIndex, newLoginModules.length - addIndex);
						
						authentication.setLoginModules(newLoginModules);
					}
				}
			} else {
	    	out.println(HELP_MESSAGE);
			}

    }	catch (SecurityException se) {
			se.printStackTrace();
			out.println("Cannot add login module. ");
		}	catch (ThreadDeath td) {
			throw td;
		} catch (OutOfMemoryError aome) {
			throw aome;
		} catch (Throwable t) {
			t.printStackTrace();
			out.println("Cannot add login module. ");
		}
	}
	
	public String getGroup() {
		return "policy_configurations";
	}
	
	public String getHelpMessage() {
		return HELP_MESSAGE;
	}
	
	public String getName() {
		return "add_login_module";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
}