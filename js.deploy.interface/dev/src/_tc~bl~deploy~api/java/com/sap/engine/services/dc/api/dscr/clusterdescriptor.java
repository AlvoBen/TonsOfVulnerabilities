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
 * Describes the update status of the cluster.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 * @deprecated The interface will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */
public interface ClusterDescriptor {

	/**
	 * Gets the <code>InstanceDescriptor</code>s for instances in the cluster.
	 * 
	 * @return <code>Set<InstanceDescriptor></code>
	 * @deprecated
	 */
	public Set getInstanceDescriptors();

	/**
	 * Gets the cluster status.
	 * 
	 * @return <code>ClusterStatus</code>
	 * @deprecated
	 */
	public ClusterStatus getClusterStatus();

}
