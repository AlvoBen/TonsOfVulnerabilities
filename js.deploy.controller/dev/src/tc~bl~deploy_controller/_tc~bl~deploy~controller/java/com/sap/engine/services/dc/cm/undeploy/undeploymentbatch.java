package com.sap.engine.services.dc.cm.undeploy;

import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-1
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface UndeploymentBatch {

	public List<GenericUndeployItem> getOrderedUndeployItems();

	public void addOrderedUndeployItems(List<GenericUndeployItem> orderedUndeployItems);

	public Enumeration<GenericUndeployItem> getAdmittedUndeployItems();

	public Set<GenericUndeployItem> getUndeployItems();

	public void addUndeployItem(GenericUndeployItem undeployItem);

	public void addUndeployItems(Collection<GenericUndeployItem> undeployItems);

	public void removeUndeployItem(GenericUndeployItem undeployItem);

	public void removeUndeployItems(Collection<GenericUndeployItem> undeployItems);

	public void clear();

}
