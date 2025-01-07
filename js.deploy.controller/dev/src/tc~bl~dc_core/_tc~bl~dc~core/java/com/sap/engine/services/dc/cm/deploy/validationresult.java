package com.sap.engine.services.dc.cm.deploy;

import java.io.Serializable;
import java.util.Collection;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-13
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface ValidationResult extends Serializable {

	public abstract ValidationStatus getValidationStatus();

	public abstract boolean isOfflinePhaseScheduled();

	public abstract Collection getSortedDeploymentBatchItems();

	public abstract Collection getDeploymentBatchItems();

}
