/*
 * Copyright (C) 2000 - 2005 by SAP AG, Walldorf,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.deploy.impl;

import com.sap.engine.services.dc.api.deploy.DeployBatch;
import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.DeploySettings;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD></DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2007</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2008-01-07</DD>
 * </DL>
 * 
 * @author Todor Atanasov
 * @version 1.0
 * @since 7.1
 */
public class DeployBatchImpl implements DeployBatch {
	private DeployItem[] deployItems;
	private DeploySettings deploySettings = new DeploySettingsImpl();

	public DeployBatchImpl(DeployItem[] deployItems) {
		this.deployItems = deployItems;
	}

	public DeployItem[] getDeployItems() {
		return this.deployItems;
	}

	public DeploySettings getDeploySettings() {
		return deploySettings;
	}

	public void setDeploySettings(DeploySettings deploySettings) {
		this.deploySettings = deploySettings;
	}
}
