package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;

import com.sap.engine.services.dc.repo.SduId;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-26
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface BatchItemId extends Serializable {

	public SduId getSduId();

	public int getIdCount();

	public void setIdCount(int idCount);

	public String toString();

	public boolean equals(Object obj);

	public int hashCode();

}
