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

import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.shell.Command;
import com.sap.engine.interfaces.shell.Environment;


/**
	*	Displays all cryptography providers and their current priority positions.
	*
	*	@author Diana Berberova
	*	@version 1.0
	* 
	*/
public class ListCryptoProvidersCommand implements Command{

  private SecurityContext security = null;
	private PrintWriter out = null;
	final String HELP_MESSAGE = "Displays all cryptography providers and their current " + 
			"priority positions.\nUsage: LIST_ CRYPTO_PROVIDERS "; 

	
	public ListCryptoProvidersCommand(com.sap.engine.interfaces.security.SecurityContext root) {

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
		
		if (params.length == 0) {
		  Provider[] providers = null;
			try {
	      providers = Security.getProviders();
	      for (int i = 0; i < providers.length; i++) {
					out.println(String.valueOf(i+1) + ". " + (providers[i].getName()));
	      }
			} catch (ThreadDeath td) {
			  throw td;
			} catch (OutOfMemoryError aome) {
				throw aome;
			} catch (Throwable t) {
				t.printStackTrace();
		  	out.println("Cannot load the list of cryptography providers");
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
		return "list_crypto_providers";
	}
	
  public String[] getSupportedShellProviderNames() {
    return null;
  }
  
}
