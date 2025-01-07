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
 *@author Luchesar Cekov
 */
public class ContainerComparatorByCLPrioReverted 
	implements Comparator<ContainerInterface> {
	public static final ContainerComparatorByCLPrioReverted instance = 
		new ContainerComparatorByCLPrioReverted();

	private ContainerComparatorByCLPrioReverted() {/**/
	}

	public int compare(ContainerInterface aO1, ContainerInterface aO2) {
		return -ContainerComparatorByCLPrio.instance.compare(aO1, aO2);
	}

}
