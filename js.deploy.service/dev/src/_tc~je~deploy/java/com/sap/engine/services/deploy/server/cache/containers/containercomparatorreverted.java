/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.cache.containers;

import java.util.Comparator;

import com.sap.engine.services.deploy.container.ContainerInterface;

/**
 * @author Luchesar Cekov
 */
public class ContainerComparatorReverted implements Comparator<ContainerInterface> {
	public static final ContainerComparatorReverted instance = new ContainerComparatorReverted();

	private ContainerComparatorReverted() {/**/
	}

	public int compare(ContainerInterface container1, 
		ContainerInterface container2) {
		return -ContainerComparator.instance.compare(container1, container2);
	}
}
