package com.sap.engine.services.dc.api;

import com.sap.engine.services.dc.api.deploy.DeployBatch;
import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.undeploy.UndeployBatch;
import com.sap.engine.services.dc.api.undeploy.UndeployItem;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Instanciate deploy and undeploy batch items.</DD>
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
public interface BatchFactory {
	/**
	 * Create a deploy batch.
	 * 
	 * @param deployItems
	 * @return deploy batch
	 */
	DeployBatch createDeployBatch(DeployItem[] deployItems);

	/**
	 * Create an undeploy batch.
	 * 
	 * @param undeployItems
	 * @return undeploy batch
	 */
	UndeployBatch createUndeployBatch(UndeployItem[] undeployItems);
}
