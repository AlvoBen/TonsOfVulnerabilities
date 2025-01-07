package com.sap.engine.services.dc.cm.undeploy.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.cm.undeploy.ScaUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemVisitor;
import com.sap.engine.services.dc.repo.Dependency;
import com.sap.engine.services.dc.repo.Sda;

/**
 * The operation finds all the <code>UndeployItem</code>s from the specified
 * <code>Collection</code>, to which the specified <code>undeployItem</code>
 * has dependencies. The resolving is based on the SDA's dependencies. The
 * operation sets for an item both the items on which depends and the items
 * which depend on it.
 * 
 */

public class InitDependenciesVisitor implements UndeployItemVisitor{
	
	Collection<GenericUndeployItem> admittedUndeployItems;
	
	InitDependenciesVisitor(Collection<GenericUndeployItem> admittedUndeployItems) {
		this.admittedUndeployItems = admittedUndeployItems;
	}

	public void visit(UndeployItem undeployItem) {
		final Sda sda = undeployItem.getSda();
		final Set<Dependency> dependencies = sda.getDependencies();
		for (Iterator<Dependency> iter = dependencies.iterator(); iter.hasNext();) {
			final Dependency dependency = iter.next();

			final GenericUndeployItem resUndeployItem = findResolvingUndeployItem(
					dependency, admittedUndeployItems);
			if (resUndeployItem != null) {
				if (resUndeployItem instanceof UndeployItem){
					UndeployItem resSdaUndeployItem = (UndeployItem)resUndeployItem;
					undeployItem.addDepending(resSdaUndeployItem);
					resSdaUndeployItem.addDependingOnThis(undeployItem);
				}else{
					throw new RuntimeException("SDA cannot depend on SCA.");
				}
			}
		}
	}

	public void visit(ScaUndeployItem undeployItem) {
		// do nothing as the current logic does not allowed undeployment of non empty SCA 
	}

	

	private GenericUndeployItem findResolvingUndeployItem(Dependency dependency,
			Collection<GenericUndeployItem> admittedUndeployItems) {
		for (Iterator<GenericUndeployItem> iter = admittedUndeployItems.iterator(); iter.hasNext();) {
			final GenericUndeployItem undeployItem = iter.next();
			if (dependency.isResolvableBy(undeployItem.getSdu())) {
				return undeployItem;
			}
		}

		return null;
	}

	
}
