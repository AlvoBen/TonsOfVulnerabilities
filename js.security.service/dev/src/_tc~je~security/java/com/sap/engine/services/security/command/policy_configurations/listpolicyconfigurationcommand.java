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
import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;
import com.sap.engine.interfaces.security.AuthenticationContext;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.interfaces.security.SecurityContext;

/**
	*	Displays information about login modules in a policy configuration
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class ListPolicyConfigurationCommand implements Command{
  private SecurityContext security = null;
  private PrintWriter out = null;
  
  private final String TEMPLATE_TYPE = "template";
  private final String SERVICE_TYPE = "service";
  private final String WEB_SERVICE_TYPE = "web service";
  private final String WEB_COMPONENT_TYPE = "web component";
  private final String EJB_COMPONENT_TYPE = "ejb component";
  private final String CUSTOM_TYPE = "custom";
  private final String OTHER_TYPE = "other";
  private final String INVALID_TYPE = "not set or invalid";
  
  private final String HELP_MESSAGE = "Displays information about login modules in the specified policy configuration." +
  		"\nUsage: LIST_POLICY_CONFIGURATION <configurationName> [parameters] \nArguments:\n\t<configurationName>\t-" + 
  		" The name of the configuration.\n\tparameters:\n\t\t -t \t - Displays the configuration template, if such is used." + 
  		"\n\t\t -o \t - Displays all options and values for every login module." ;

	public ListPolicyConfigurationCommand(com.sap.engine.interfaces.security.SecurityContext root) {
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
		try{
			if (params.length > 0 && params.length < 4) {
			  byte type = SecurityContext.TYPE_INVALID;
			  AppConfigurationEntry[] loginModules = null;
				AuthenticationContext authentication = null;
				
				if(security.getPolicyConfigurationContext(params[0]) == null) {
		    	out.println("The specified policy configuration does not exist.");
				} else {

					if (((params.length > 1) && (params[1] != null) && (!params[1].equalsIgnoreCase("-t")) && (!params[1].equalsIgnoreCase("-o"))) ||
					  ((params.length > 2) && (params[2] != null) && (!params[2].endsWith("-t")) && (!params[2].equalsIgnoreCase("-o")))) {
					  out.println(HELP_MESSAGE);
					  return;
					}

					authentication = security.getPolicyConfigurationContext(params[0]).getAuthenticationContext();
				  loginModules = authentication.getLoginModules();
					type = security.getPolicyConfigurationContext(params[0]).getPolicyConfigurationType();

					String typeString = null;
					
					switch (type) {
						case SecurityContext.TYPE_CUSTOM:
						  typeString = CUSTOM_TYPE;
							break;
	
	
						case SecurityContext.TYPE_EJB_COMPONENT:
						  typeString = EJB_COMPONENT_TYPE;
							break;
	
						case SecurityContext.TYPE_OTHER:
						  typeString = OTHER_TYPE;
							break;
	
						case SecurityContext.TYPE_SERVICE:
						  typeString = SERVICE_TYPE; 
							break;
							
						case SecurityContext.TYPE_WEB_SERVICE:
						  typeString = WEB_SERVICE_TYPE; 
							break;
	
						case SecurityContext.TYPE_WEB_COMPONENT:
						  typeString = WEB_COMPONENT_TYPE; 
							break;
						
						case SecurityContext.TYPE_TEMPLATE:
						  typeString = TEMPLATE_TYPE; 
							break;
	
						case SecurityContext.TYPE_INVALID:
						
						default:
							  typeString = INVALID_TYPE;
						
					}
					
					out.println("Type: " + typeString);

					
					if (((params.length == 3) && (params[1].equalsIgnoreCase("-t") || params[2].equalsIgnoreCase("-t")))
							||((params.length == 2) && params[1].equalsIgnoreCase("-t"))){
						String template = authentication.getTemplate();
						if (template == null) {
							out.println("Template: none");
						} else {
							out.println("Template: " + template);
						}
					} 
						
					if (loginModules.length == 0) {
						out.println("Login Modules: none");
					} else {
						out.println("Login Modules: ");
		
						for (int i = 0; i < loginModules.length; i++) {
							String name = loginModules[i].getLoginModuleName();
							String controlFlag = loginModules[i].getControlFlag().toString();
							String flag = controlFlag.substring(controlFlag.lastIndexOf(" "));
							
							out.println(String.valueOf(i + 1) + ". " + name + " " + flag);
							
							if (((params.length == 3) && (params[1].equalsIgnoreCase("-o") || params[2].equalsIgnoreCase("-o")))
									||((params.length == 2) && params[1].equalsIgnoreCase("-o"))){
								Map options = loginModules[i].getOptions();
								Object[] keys = options.keySet().toArray();
								
								if (keys.length > 0){
									out.println("	Options: ");
									for (int j = 0; j <keys.length; j++) {
										String key = (String)keys[j];
										String value = (String) options.get(keys[j]);
										out.println("	" + key + " = " + value);
									}
								} else {
									out.println("	Options: none");
								}
							}
						}
					}
				}
			} else {
	    	out.println(HELP_MESSAGE);
			}			
		} catch (SecurityException se) {
			se.printStackTrace();
			out.println("Cannot load information for the specified policy configuration");
		} catch (ThreadDeath td) {
		  throw td;
		} catch (OutOfMemoryError aome) {
			throw aome;
		} catch (Throwable t) {
			t.printStackTrace();
			out.println("Cannot load information for the specified policy configuration");
		}
	}
	
	public String getGroup() {
		return "policy_configurations";
	}
	
	public String getHelpMessage() {
		return HELP_MESSAGE;
	}
	
	public String getName() {
		return "list_policy_configuration";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
}
