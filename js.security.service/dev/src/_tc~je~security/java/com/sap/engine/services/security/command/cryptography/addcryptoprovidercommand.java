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
package com.sap.engine.services.security.command.cryptography;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.Provider;
import java.security.Security;
import java.lang.reflect.Modifier;

import com.sap.engine.interfaces.security.auth.AuthenticationTraces;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.security.restriction.Restrictions;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
	*	Adds new cryptography provider to the list of cryptography providers.
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class AddCryptoProviderCommand implements Command{

  private static final Location LOCATION = Location.getLocation(AddCryptoProviderCommand.class);

  private PrintWriter out = null;
	final String HELP_MESSAGE = "Adds the specified provider to the list of cryptography providers.\n" + 
			"Usage: ADD_CRYPTO_PROVIDER <providerClassName>\nArguments:\n\t<providerClassName>\t-" + 
			" Class name of the provider to be added."; 
	    
	    
	public AddCryptoProviderCommand(com.sap.engine.interfaces.security.SecurityContext root) {
		
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
		  String providerClassName = params[0];
		  String providerName = null;
		  
      Provider provider = null;
      Class providerClass = null;
      
      try {
        providerClass = Class.forName(providerClassName);
      } catch (ClassNotFoundException e) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Cannot add new provider", e);
				out.println("Cannot add specified the provider");
				return;
      }
      
      try{ 
			  int modifiers = providerClass.getModifiers();
			  int position = Security.getProviders().length + 1;  
			  
			  if((Modifier.isAbstract(modifiers)) || (Modifier.isPrivate(modifiers)) 
			      || (Modifier.isInterface(modifiers))){
			    throw new SecurityException("Provider " + providerClassName + " cannot be instantiated.");		    
			  }
			  
			  if (Provider.class.isAssignableFrom(providerClass)) {
			    provider = (Provider) providerClass.newInstance();
			  }
			  
			  providerName = provider.getName();
		    if (Security.getProvider(providerName) != null) {
          out.println("The specified provider is already installed.");
        } else {
          // provider is not found
   	      Restrictions.checkPermission(Restrictions.CRYPTOGRAPHY_PROVIDERS, Restrictions.RESTRICTION_CHANGE_PROVIDERS);
   	      Security.insertProviderAt(provider, position);   
        }
      } catch (ThreadDeath td) {
				throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable t) {
        LOCATION.traceThrowableT(Severity.DEBUG, "Cannot add new provider", t);
        out.println("Cannot add specified the provider");
			}
		} else {
    	out.println(HELP_MESSAGE);
		}
	}
	
	public String getGroup() {
		return "cryptography";
	}
	
	public String getHelpMessage() {
 		return HELP_MESSAGE;
	}
	
	public String getName() {
		return "add_crypto_provider";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
}