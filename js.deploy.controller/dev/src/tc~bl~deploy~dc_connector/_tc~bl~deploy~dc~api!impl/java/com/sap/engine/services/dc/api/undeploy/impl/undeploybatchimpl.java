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
package com.sap.engine.services.dc.api.undeploy.impl;

import com.sap.engine.services.dc.api.undeploy.UndeployBatch;
import com.sap.engine.services.dc.api.undeploy.UndeployItem;
import com.sap.engine.services.dc.api.undeploy.UndeploySettings;

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
public class UndeployBatchImpl implements UndeployBatch {
	private UndeployItem[] undeployItems;
	private UndeploySettings undeploySettings = new UndeploySettingsImpl();

	public UndeployBatchImpl(UndeployItem[] undeployItems) {
		this.undeployItems = undeployItems;
	}

	public UndeployItem[] getUndeployItems() {
		return this.undeployItems;
	}

	public UndeploySettings getUndeploySettings() {
		return undeploySettings;
	}

	public void setUndeploySettings(UndeploySettings undeploySettings) {
		this.undeploySettings = undeploySettings;
	}
}
