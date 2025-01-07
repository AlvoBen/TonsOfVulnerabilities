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

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.security.restriction.Restrictions;


/**
	*	Changes the priority of a cryptography provider. 
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class MoveCryptoProviderCommand implements Command{

  private PrintWriter out = null;
	private final String HELP_MESSAGE = "Changes the priority of the current provider by moving it to the specified position. " +
			"The other providers are moved down or up according to the position.\n" + 
			"Usage: MOVE_ CRYPTO_ PROVIDER <providerName> <position>\nArguments:\n\t<providerName>\t-" + 
			" The name of the provider to be moved\n\t<position>\t-" + 
			" Position in the list to which the provider will be moved." ; 

  
	public MoveCryptoProviderCommand(com.sap.engine.interfaces.security.SecurityContext root) {
		
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
			String providerName = params[0];
			Provider provider = null;
			Provider[] providers = null;
			int oldPosition = 0;
			int newPosition = 0;
			
			try {
				newPosition = Integer.parseInt(params[1]);
			}	catch (NumberFormatException e) {
		    	out.println(HELP_MESSAGE);
		    	return;
		  }
			
			try {
				if (newPosition > 0 && newPosition <= Security.getProviders().length){
				  providers = Security.getProviders();
				  
				  for (int i = 0; i < providers.length; i++) {
				    if (providerName.equalsIgnoreCase(providers[i].getName())) {
				      provider = providers[i];
				      oldPosition = i + 1;
				      break;
				    }
				  }
				  
					if (oldPosition > 0) {
					  if (oldPosition != newPosition) {
					    try {
					      Restrictions.checkPermission(Restrictions.CRYPTOGRAPHY_PROVIDERS, Restrictions.RESTRICTION_CHANGE_PROVIDERS);
							  Security.removeProvider(providerName);
							  Security.insertProviderAt(provider, newPosition);
					    } catch (Throwable e) {
					      throw e;
					    }
						}	else {
				    	out.println("The specified position is the same as the current position of the provider");
						}
					} else {
			    	out.println("The specified provider does not exist. Cannot be moved.");
					} 
				}	else {
		    	out.println("The specified position is not a valid position in the cryptography providers list");
				}
			} catch (ThreadDeath td) {
				throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable t) {
				out.println("Cannot move the provider to specified position.");
        t.printStackTrace();
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
		return "move_crypto_provider";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
}
