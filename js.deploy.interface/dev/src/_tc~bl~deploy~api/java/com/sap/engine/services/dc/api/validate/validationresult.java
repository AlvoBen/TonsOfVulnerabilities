package com.sap.engine.services.dc.api.validate;

import com.sap.engine.services.dc.api.deploy.ValidationStatus;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Base interface for validation results.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2007</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2007-11-13</DD>
 * </DL>
 * 
 * @author Todor Atanasov
 * @version 1.0
 * @since 7.1
 */
public interface ValidationResult {
	/**
	 * Returns validation status.
	 * 
	 * @return validation status
	 * @see ValidationStatus
	 */
	public ValidationStatus getValidationStatus();

	/**
	 * Returns true if there is a deployItem/undeployItem for offline
	 * deploy/undeploy, otherwise false.
	 * 
	 * @return true if there is a deployItem/undeployItem for offline
	 *         deploy/undeploy, otherwise false
	 */
	public boolean isOfflinePhaseScheduled();
	 
	  /**
	   * Returns overall result description 
	   * @return description as string
	   */
	  public String getDescription();
}
