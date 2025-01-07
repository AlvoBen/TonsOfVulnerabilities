package com.sap.engine.services.dc.api.deploy;

import com.sap.engine.services.dc.api.Batch;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Interface for deploy batch item.</DD>
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
public interface DeployBatch extends Batch {

	/**
	 * Returns the deploy items in tha batch.
	 * 
	 * @return deploy items in the batch
	 */
	public DeployItem[] getDeployItems();

	/**
	 * Returns the deploy settings of the batch.
	 * 
	 * @return
	 */
	public DeploySettings getDeploySettings();

	/**
	 * Specifies the deploy settings of the batch
	 * 
	 * @param deploySettings
	 *            of the batch
	 */
	public void setDeploySettings(DeploySettings deploySettings);
}
