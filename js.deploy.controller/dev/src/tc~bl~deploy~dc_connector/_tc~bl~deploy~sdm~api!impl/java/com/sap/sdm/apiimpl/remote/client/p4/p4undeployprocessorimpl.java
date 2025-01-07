package com.sap.sdm.apiimpl.remote.client.p4;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.undeploy.UndeployException;
import com.sap.engine.services.dc.api.undeploy.UndeployProcessor;
import com.sap.engine.services.dc.api.undeploy.UndeployResultNotFoundException;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.UnDeployItem;
import com.sap.sdm.api.remote.UnDeployProcessor;
import com.sap.sdm.apiimpl.remote.client.APIRemoteExceptionImpl;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-7-8
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
class P4UnDeployProcessorImpl implements UnDeployProcessor {

	private final com.sap.engine.services.dc.api.Client dcClient;

	static UnDeployProcessor createInstance(
			com.sap.engine.services.dc.api.Client dcClient) {
		return new P4UnDeployProcessorImpl(dcClient);
	}

	private P4UnDeployProcessorImpl(
			com.sap.engine.services.dc.api.Client dcClient) {
		this.dcClient = dcClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.UnDeployProcessor#undeploy(com.sap.sdm.api.remote
	 * .UnDeployItem[])
	 */
	public void undeploy(UnDeployItem[] undeployItems) throws RemoteException {
		final UndeployProcessor undeployProcessor;
		try {
			undeployProcessor = this.dcClient.getComponentManager()
					.getUndeployProcessor();
		} catch (ConnectionException apice) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while getting an UndeployProcessor "
							+ "from the Deploy Controller API.", apice);
		} catch (UndeployException apiue) {
			throw new APIRemoteExceptionImpl(
					"An error occurred while getting an UndeployProcessor "
							+ "from the Deploy Controller API.", apiue);
		}

		final Map dcSdmUndeployItemsMap = buildDcSdmDeployItemsMap(
				undeployProcessor, undeployItems);

		com.sap.engine.services.dc.api.undeploy.UndeployItem[] dcUndeployItems = (com.sap.engine.services.dc.api.undeploy.UndeployItem[]) dcSdmUndeployItemsMap
				.keySet()
				.toArray(
						new com.sap.engine.services.dc.api.undeploy.UndeployItem[dcSdmUndeployItemsMap
								.keySet().size()]);

		try {
			undeployProcessor.undeploy(dcUndeployItems);
		} catch (ConnectionException apice) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while performing undeployment "
							+ "via the Deploy Controller API.", apice);
		} catch (com.sap.engine.services.dc.api.lock_mng.AlreadyLockedException le) {
			throw new APIRemoteExceptionImpl(
					"Lock exception via the Deploy Controller "
							+ "API. Probably other un/deployment "
							+ "is performed at the moment.", le);
		} catch (UndeployResultNotFoundException apiurnfe) {
			throw new APIRemoteExceptionImpl(
					"The result of the undeployment could not "
							+ "be get from the server.", apiurnfe);
		} catch (UndeployException apiue) {
			throw new APIRemoteExceptionImpl(
					"An error occurred while performing undeployment "
							+ "via the Deploy Controller API.", apiue);
		}

		buildResult(dcSdmUndeployItemsMap);
	}

	private void buildResult(Map dcSdmUndeployItemsMap) {
		for (Iterator iter = dcSdmUndeployItemsMap.entrySet().iterator(); iter
				.hasNext();) {
			final Map.Entry mapEntry = (Map.Entry) iter.next();
			com.sap.engine.services.dc.api.undeploy.UndeployItem dcUndeployItem = (com.sap.engine.services.dc.api.undeploy.UndeployItem) mapEntry
					.getKey();
			final P4UnDeployItemImpl sdmUndeployItem = (P4UnDeployItemImpl) mapEntry
					.getValue();

			sdmUndeployItem.setUnDeployResult(P4UnDeployResultMapper
					.map(dcUndeployItem));
			sdmUndeployItem.setDeploymentID(dcUndeployItem.getName() + "_"
					+ dcUndeployItem.getVendor());
		}
	}

	private Map buildDcSdmDeployItemsMap(UndeployProcessor undeployProcessor,
			UnDeployItem[] undeployItems) {
		final Map dcSdmUndeployItemsMap = new HashMap();

		for (int i = 0; i < undeployItems.length; i++) {
			final P4UnDeployItemImpl sdmUndeployItem = (P4UnDeployItemImpl) undeployItems[i];
			com.sap.engine.services.dc.api.undeploy.UndeployItem dcUndeployItem = undeployProcessor
					.createUndeployItem(sdmUndeployItem.getComponentName(),
							sdmUndeployItem.getComponentVendor(),
							sdmUndeployItem.getComponentLocation(),
							sdmUndeployItem.getComponentVersion());

			dcSdmUndeployItemsMap.put(dcUndeployItem, undeployItems[i]);
		}

		return dcSdmUndeployItemsMap;
	}

}
