package com.sap.engine.services.dc.api;

import com.sap.engine.services.dc.api.deploy.DeploySettings;
import com.sap.engine.services.dc.api.undeploy.UndeploySettings;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Factory for deploy and undeploy settings.</DD>
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
public interface SettingsFactory {
	/**
	 * Create Deploy Settings object.
	 * 
	 * @return DeploySettings object
	 */
	DeploySettings createDeploySettings();

	/**
	 * Create Undeploy Settings object.
	 * 
	 * @return UndeploySettings object
	 */
	UndeploySettings createUndeploySettings();
}
