/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.version;

import java.io.Serializable;
import java.util.Set;

/**
 * Provides information about the deployed components in the cluster.
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public interface ServerVersion extends Serializable {

	/**
	 * Gets the same information like the one retrieved from the telnet command
	 * VERSION without parameters.
	 * 
	 * @return <code>String<code>
	 */
	public String getInfo();

	/**
	 * Gets the same information like the one retrieved from the telnet command
	 * VERSION with parameter -MORE.
	 * 
	 * @return <code>String<code>
	 */
	public String getMoreInfo();

	/**
	 * In case more information is needed.
	 * 
	 * @return <code>Set<code>, which containes <code>Sdu<code> objects.
	 */
	public Set getSDUs();

}
