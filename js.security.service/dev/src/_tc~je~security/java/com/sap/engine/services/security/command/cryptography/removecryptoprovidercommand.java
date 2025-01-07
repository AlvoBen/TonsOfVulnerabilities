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
import java.security.Security;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;
import com.sap.engine.services.security.restriction.Restrictions;

/**
	*	Removes the specified  provider from the list of cryptography providers.
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class RemoveCryptoProviderCommand implements Command{

  private PrintWriter out = null;
  private final String HELP_MESSAGE = "Removes the specified  provider from the list of cryptography providers.\n" + 
  			"Usage: REMOVE_ CRYPTO_PROVIDER <providerName>\nArguments:\n\t<providerName>\t-" + 
  			" The name of the provider to remove from the list"; 

    
	public RemoveCryptoProviderCommand(com.sap.engine.interfaces.security.SecurityContext root) {
		
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
		  String providerName = params[0];
		  
			if (Security.getProvider(providerName) != null) {
		    try {
		      Restrictions.checkPermission(Restrictions.CRYPTOGRAPHY_PROVIDERS, Restrictions.RESTRICTION_CHANGE_PROVIDERS);
					Security.removeProvider(providerName);
		    } catch (ThreadDeath td) {
					throw td;
				} catch (OutOfMemoryError aome) {
					throw aome;
				} catch (Throwable t) {
					t.printStackTrace();
					out.println("Cannot remove the specified provider.");
				}
			} else {
				out.println("The specified provider does not exist. Cannot be removed.");
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
		return "remove_crypto_provider";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }  
}