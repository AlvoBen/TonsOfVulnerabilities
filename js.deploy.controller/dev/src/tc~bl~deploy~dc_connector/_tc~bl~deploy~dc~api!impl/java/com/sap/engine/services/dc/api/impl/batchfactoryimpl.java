package com.sap.engine.services.dc.api.impl;

import com.sap.engine.services.dc.api.BatchFactory;
import com.sap.engine.services.dc.api.deploy.DeployBatch;
import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.impl.DeployBatchImpl;
import com.sap.engine.services.dc.api.undeploy.UndeployBatch;
import com.sap.engine.services.dc.api.undeploy.UndeployItem;
import com.sap.engine.services.dc.api.undeploy.impl.UndeployBatchImpl;

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
public class BatchFactoryImpl implements BatchFactory {

	private static BatchFactoryImpl instance = null;

	private BatchFactoryImpl() {
	};

	public synchronized static final BatchFactoryImpl getInstance() {
		if (instance == null) {
			instance = new BatchFactoryImpl();
		}

		return instance;
	}

	public DeployBatch createDeployBatch(DeployItem[] deployItems) {
		return new DeployBatchImpl(deployItems);
	}

	public UndeployBatch createUndeployBatch(UndeployItem[] undeployItems) {
		return new UndeployBatchImpl(undeployItems);
	}

}
