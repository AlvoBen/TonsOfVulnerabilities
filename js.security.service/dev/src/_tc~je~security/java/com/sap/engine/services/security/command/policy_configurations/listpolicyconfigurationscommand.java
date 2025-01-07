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
import java.util.Arrays;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.interfaces.security.SecurityContext;


/**
	*	Displays all available policy configurations.
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class ListPolicyConfigurationsCommand implements Command{
	
  private PrintWriter out = null;
  private SecurityContext security = null;
  
  public static final String SERVICE = "service.";
  public static final String BASIC = "basic";
  public static final String CLIENT_CERT = "client_cert";
  public static final String DIGEST = "digest";
  public static final String FORM = "form";
  public static final String TICKET = "ticket";
  public static final String ASSERTION_TICKET = "evaluate_assertion_ticket";
  
  private final String TEMPLATE_TYPE = "template";
  private final String SERVICE_TYPE = "service";
  private final String WEB_SERVICE_TYPE = "web service";
  private final String WEB_COMPONENT_TYPE = "web component";
  private final String EJB_COMPONENT_TYPE = "ejb component";
  private final String CUSTOM_TYPE = "custom";
  private final String OTHER_TYPE = "other";
  private final String INVALID_TYPE = "not set or invalid";
  
  
  
 	private final String HELP_MESSAGE = "Displays all available policy configurations.\n" + 
   		"Usage: LIST_POLICY_CONFIGURATIONS"; 

	public ListPolicyConfigurationsCommand(com.sap.engine.interfaces.security.SecurityContext root) {
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
	
		try {
			String[] configurations = security.listPolicyConfigurations();
			byte type = SecurityContext.TYPE_INVALID;
			String typeString = null;
			
			if (params.length == 0) {
			  sortList(configurations); 
			  
				for (int i = 0; i < configurations.length; i++) {
		    	if (configurations[i] != null) {
		    	  
						type = security.getPolicyConfigurationContext(configurations[i]).getPolicyConfigurationType();
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
						
						out.println("" + String.valueOf(i+1) + ". " + configurations[i] + " type: " + typeString);
		    	}
				}
			} else {
				out.println(HELP_MESSAGE);
			}
		} catch (ThreadDeath td) {
			throw td;
		} catch (OutOfMemoryError aome) {
			throw aome;
		} catch (Throwable t) {
			t.printStackTrace();
			out.println("Cannot load the list of policy configurations.");
		}
	}
	
	
  public static void sortList(Object[] list) {
      Arrays.sort(list);
      
      sort(0, list, SecurityContext.ROOT_POLICY_CONFIGURATION);
      sort(1, list, BASIC);
      sort(2, list, CLIENT_CERT);
      sort(3, list, DIGEST);
      sort(4, list, FORM);
      sort(5, list, TICKET);
      sort(6, list, ASSERTION_TICKET);
      sort(7, list);
    }

    private static void sort(int start, Object[] list, String component) {
      Object temp = null;

      for (int i = start; i < list.length; i++) {
        if (component.equals(list[i])) {
          temp = list[i];
          for (int j = i; j > start; j-- ) {
            list[j] = list[j - 1];
          }
          list[start] = temp;
          break;
        }
      }
    }

    private static void sort(int start, Object[] list) {
      Object temp = null;

      for (int i = start; i < list.length; i++) {
        if (((String) list[i]).startsWith(SERVICE)) {
          temp = list[i];
          for (int j = i; j > start; j-- ) {
            list[j] = list[j - 1];
          }
          list[start] = temp;
          start++;
        }
      }
    }
	
	public String getGroup() {
		return "policy_configurations";
	}
	
	
	public String getHelpMessage() {
 		return HELP_MESSAGE;
	}
	
	public String getName() {
		return "list_policy_configurations";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
}
