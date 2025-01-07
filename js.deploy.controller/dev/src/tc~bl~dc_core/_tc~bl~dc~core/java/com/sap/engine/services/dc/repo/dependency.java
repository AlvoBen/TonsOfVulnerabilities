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
public interface Dependency extends Serializable {

	public String getName();

	public String getVendor();

	public DCReference getDCReference();

	public boolean isResolvableBy(Sdu sdu);

	public boolean isClassloading();

	public boolean isFunctional();

	public void setForChildren(boolean forChildren);

	public boolean isForChildren();

	public void setRelevantAtDesignTime(boolean relevantAtDesignTime);

	public boolean isRelevantAtDesignTime();

	public void setRelevantAtBuildTime(boolean relevantAtBuildTime);

	public boolean isRelevantAtBuildTime();

	public void setRelevantAtDeployTime(boolean relevantAtDeployTime);

	public boolean isRelevantAtDeployTime();

	public void setRelevantAtRuntimeTime(boolean relevantAtRuntimeTime);

	public boolean isRelevantAtRuntimeTime();

	public String getPpRef();

	public void setPpRef(String ppRef);

	public String toString();

	public boolean equals(Object obj);

	public int hashCode();

}
