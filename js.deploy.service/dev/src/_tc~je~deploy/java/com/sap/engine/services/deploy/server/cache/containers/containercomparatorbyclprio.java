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
public class ContainerComparatorByCLPrio implements Comparator<ContainerInterface> {
	public static final ContainerComparatorByCLPrio instance = new ContainerComparatorByCLPrio();

	private ContainerComparatorByCLPrio() {/**/
	}

	public int compare(final ContainerInterface container1,
		final ContainerInterface container2) {
		if (container1 == null && container2 != null) {
			return 1;
		} else if (container1 == null && container2 == null) {
			return 0;
		} else if (container1 != null && container2 == null) {
			return -1;
		}

		int prio1 = container1.getContainerInfo().getClassLoadPriority();
		int prio2 = container2.getContainerInfo().getClassLoadPriority();

		return prio1 - prio2;
	}
}
