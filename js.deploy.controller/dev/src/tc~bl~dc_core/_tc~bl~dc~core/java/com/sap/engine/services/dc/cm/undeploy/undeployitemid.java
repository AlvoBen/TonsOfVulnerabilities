package com.sap.engine.services.dc.cm.undeploy;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-30
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface UndeployItemId extends Serializable {

	public String getName();

	public String getVendor();

	public int getIdCount();

	public void setIdCount(int idCount);

	public String toString();

	public boolean equals(Object obj);

	public int hashCode();

}
