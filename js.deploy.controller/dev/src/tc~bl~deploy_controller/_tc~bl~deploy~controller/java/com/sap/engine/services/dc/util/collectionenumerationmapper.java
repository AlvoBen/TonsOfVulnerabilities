package com.sap.engine.services.dc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-20
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public final class CollectionEnumerationMapper {

	public static Enumeration map(Collection collection) {

		final Object[] collectionData = collection.toArray();
		final int collectionSize = collectionData.length;

		return new Enumeration() {
			private int count = 0;

			public boolean hasMoreElements() {
				return count < collectionSize;
			}

			public synchronized Object nextElement() {
				if (count < collectionSize) {
					return collectionData[count++];
				}

				throw new NoSuchElementException(
						"ASJ.dpl_dc.003392 Collection Enumeration");
			}
		};
	}

	public static Collection map(Enumeration enum1) {
		final Collection collection = new ArrayList();
		while (enum1.hasMoreElements()) {
			collection.add(enum1.nextElement());
		}

		return collection;
	}

	public static Enumeration<DeploymentBatchItem> map(
			final Collection<DeploymentBatchItem> collection,
			final Set<DeploymentStatus> acceptedStatuses) {

		Collection<DeploymentBatchItem> itemsFilteredByStatus = new ArrayList<DeploymentBatchItem>(
				collection.size());

		Iterator<DeploymentBatchItem> iter = collection.iterator();

		while (iter.hasNext()) {

			DeploymentBatchItem item = iter.next();
			if (acceptedStatuses.contains(item.getDeploymentStatus())) {
				itemsFilteredByStatus.add(item);
			}
		}

		return map(itemsFilteredByStatus);

	}

	private CollectionEnumerationMapper() {
	}

}
