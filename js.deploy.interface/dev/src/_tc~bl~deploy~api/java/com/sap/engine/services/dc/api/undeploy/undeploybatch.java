package com.sap.engine.services.dc.api.undeploy;

import com.sap.engine.services.dc.api.Batch;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Interface for undeploy batch item.</DD>
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
public interface UndeployBatch extends Batch {
	/**
	 * Returns the undeploy items from the batch
	 * 
	 * @return undeploy items
	 */
	public UndeployItem[] getUndeployItems();

	/**
	 * Returns the undeploy settings of the batch
	 * 
	 * @return undeploy settings
	 */
	public UndeploySettings getUndeploySettings();

	/**
	 * Specifies new undeploy settings
	 * 
	 * @param undeploy
	 *            settings
	 */
	public void setUndeploySettings(UndeploySettings undeploySettings);
}
