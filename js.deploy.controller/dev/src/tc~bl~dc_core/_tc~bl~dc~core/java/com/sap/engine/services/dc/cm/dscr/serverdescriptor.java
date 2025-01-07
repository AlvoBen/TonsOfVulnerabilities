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
package com.sap.engine.services.dc.cm.dscr;

import java.io.Serializable;

/**
 * Describes the update status of a server node.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 * @deprecated The interface will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */
public interface ServerDescriptor extends Serializable {

	/**
	 * Gets the item status.
	 * 
	 * @return <code>ItemStatus</code>
	 * @deprecated
	 */
	public ItemStatus getItemStatus();

	/**
	 * Unique cluster element identifier.
	 * 
	 * @return <code>int</code>
	 * @deprecated
	 */
	public int getClusterID();// TODO name?

	/**
	 * Unique instance identifier.
	 * 
	 * @return <code>int</code>
	 * @deprecated
	 */
	public int getInstanceID();

	/**
	 * Description of the <code>ItemStatus</code> on teh server.
	 * 
	 * @return <code>String</code>
	 * @deprecated
	 */
	public String getDescription();

}
