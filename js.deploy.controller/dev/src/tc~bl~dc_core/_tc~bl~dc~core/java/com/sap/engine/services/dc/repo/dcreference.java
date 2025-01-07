package com.sap.engine.services.dc.repo;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-17
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface DCReference extends Serializable {

	public String getName();

	public String getVendor();

	public String getScAlias();

	public void setScAlias(String scAlias);

	public String toString();

	public boolean equals(Object obj);

	public int hashCode();

}
