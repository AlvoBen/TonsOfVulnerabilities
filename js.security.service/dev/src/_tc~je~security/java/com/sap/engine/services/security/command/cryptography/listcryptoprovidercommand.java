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

import java.io.*;
import java.security.Provider;
import java.security.Security;

import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;


/**
	*	Displays information about cryptography provider.
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class ListCryptoProviderCommand implements Command{
	
  private PrintWriter out = null;
	private final String HELP_MESSAGE = "Displays information about the specified cryptography provider.\n" + 
			"Usage: LIST_CRYPTO_PROVIDER <providerName>\nArguments:\n\t<providerName>\t-" + 
			" The name of the cryptography provider"; 
	
	public ListCryptoProviderCommand(com.sap.engine.interfaces.security.SecurityContext root) {
		
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
      Provider[] providers = null;
		  String providerName = params[0];

			try {
	      providers = Security.getProviders();
			  for (int i = 0; i < providers.length; i++) {
			    if (providerName.equalsIgnoreCase(providers[i].getName())) {
						out.println(String.valueOf(i + 1) + ". " + providerName.toString() 
						    + ": " + providers[i].getInfo());
			      return;
			    }
			  }
	    	out.println("The specified provider does not exist.");
			} catch (ThreadDeath td) {
				throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable t) {
		  	out.println("Cannot load information for the specified provider");
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
		return "list_crypto_provider";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }
}
