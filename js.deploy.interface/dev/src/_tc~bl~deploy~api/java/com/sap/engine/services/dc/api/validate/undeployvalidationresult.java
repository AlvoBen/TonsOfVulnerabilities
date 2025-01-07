package com.sap.engine.services.dc.api.validate;

import com.sap.engine.services.dc.api.undeploy.UndeployItem;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Interface for undeploy batch validation result.</DD>
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
 * @since 7.0
 */
public interface UndeployValidationResult extends ValidationResult {
	/**
	 * Returns array with undeployItems which are sorted in order to be
	 * undeployed.
	 * 
	 * @return array with undeployItems
	 */
	public UndeployItem[] getSortedUndeploymentItems();

	/**
	 * Returns array with the given for validating undeployItems.
	 * 
	 * @return array of undeployItems
	 */
	public UndeployItem[] getUndeploymentItems();

}
