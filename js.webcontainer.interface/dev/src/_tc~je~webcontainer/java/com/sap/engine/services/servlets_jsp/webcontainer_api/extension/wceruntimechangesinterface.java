/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.servlets_jsp.webcontainer_api.extension;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.services.servlets_jsp.webcontainer_api.exceptions.WCEConfigurationException;

/**
 * This interface, when implemented by a WCE Provider, will allow it to initiate
 * a so called WCE-runtime-changes-action on the WebContainer (similar to the
 * containers' runtime changes action but simplified) and to receive
 * his WCE app sub config opened for write at runtime and update it.
 * In order to WCE initiate runtime changes the WCE Provider should call the method
 * <code>makeWCERuntimeChanges</code> on its IApplicationManager instance,
 * which the WCE Provider can obtain by calling the getApplicationManager() method
 * on its IWebContainerExtensionContext instance.
 * 
 * @author Vera Buchkova
 * @version 7.10
 */
public interface WCERuntimeChangesInterface {
	/**
	 * Updates the passed configuration for the given web module when
	 * the runtime changes action has been initiated.
	 * @param  applicationName  full name of the application (vendor part included) 
	 *       of the web module for which the config is needed
	 * @param  webModuleName  the name of the web module for which the 
	 *       config is needed
	 * @param  wceWebModuleConfig  the WCE web module sub config for the given web
	 *       module
	 * @return  true if the WCE Provider has successful completed
	 *       his updates in the configuration and the transaction has to
	 *       be committed; false - otherwise and the transaction has to be
	 *       rolled back.
	 * @throws WCEConfigurationException when the runtime changes action failed
	 * 			for some reason; this exception will be wrapped and could be visible
	 *      as cause in the exception thrown from the IApplicationManager.makeWCERuntimeChanges method.
	 */
	public boolean updateWCEWebModuleConfig(String applicationName, String webModuleName, 
	                                       Configuration wceWebModuleConfig) throws WCEConfigurationException;
}
