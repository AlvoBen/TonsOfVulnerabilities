package com.sap.engine.services.dc.cm.undeploy;

import java.io.Serializable;

import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.Version;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface GenericUndeployItem extends Serializable {

	public UndeployItemId getId();

	public String getName();

	public String getVendor();

	public String getLocation();

	public Version getVersion();

	public UndeployItemStatus getUndeployItemStatus();

	public void setUndeployItemStatus(UndeployItemStatus undeployItemStatus);

	public String getDescription();

	public void setDescription(String description);

	public String toString();

	public boolean equals(Object obj);

	public int hashCode();
	
	public void accept(UndeployItemVisitor visitor);
	
	public Sdu getSdu();
	
	public void addUndeployItemObserver(UndeployItemObserver observer);
	
	public void removeUndeployItemObserver(UndeployItemObserver observer);
	

}
