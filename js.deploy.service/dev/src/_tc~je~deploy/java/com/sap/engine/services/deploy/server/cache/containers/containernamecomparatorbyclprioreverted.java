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
 * The comparator used by deployment info to order the class files in class
 * load path. The class files of the containers with lower priority are in the
 * beginning of the class path order. The classes with the same canonical name
 * which are at the beginning of the order, will override these which are after
 * them in the order.
 * 
 *@author Luchesar Cekov
 */
public class ContainerNameComparatorByCLPrioReverted 
	implements Comparator<String> {
	public static final ContainerNameComparatorByCLPrioReverted instance = 
		new ContainerNameComparatorByCLPrioReverted();

	private ContainerNameComparatorByCLPrioReverted() {/**/
	}

	public int compare(String aO1, String aO2) {
		final Containers containers = Containers.getInstance();
		final ContainerInterface container1 = containers.getContainer(aO1);
		final ContainerInterface container2 = containers.getContainer(aO2);

		return ContainerComparatorByCLPrioReverted.instance.compare(
			container1, container2);
	}
}
