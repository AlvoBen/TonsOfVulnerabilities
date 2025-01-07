/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.dscr;

import java.util.Set;

/**
 * Describes the updated status of an instance.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 * @deprecated The interface will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */
public interface InstanceDescriptor {

	/**
	 * Gets the <code>ServerDescriptor</code>s for server nodes in the instance.
	 * 
	 * @return <code>Set<ServerDescriptor></code>
	 * @deprecated
	 */
	public Set getServerDescriptors();

	/**
	 * Gets the instance sattus.
	 * 
	 * @return <code>InstanceStatus</code>
	 * @deprecated
	 */
	public InstanceStatus getInstanceStatus();

	/**
	 * Unique instance identifier.
	 * 
	 * @return <code>int</code>
	 * @deprecated
	 */
	public int getInstanceID();// TODO name?

	/**
	 * Gets the <code>TestInfo</code> for this instance.
	 * 
	 * @return <code>TestInfo</code>
	 * @deprecated
	 */
	public TestInfo getTestInfo();

	/**
	 * Gets state description of this instance.
	 * 
	 * @return <code>String</code>
	 * @deprecated
	 */
	public String getDescription();

}
