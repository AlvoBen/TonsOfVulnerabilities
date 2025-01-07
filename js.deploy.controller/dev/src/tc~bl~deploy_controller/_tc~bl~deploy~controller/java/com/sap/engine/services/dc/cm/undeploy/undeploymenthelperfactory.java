package com.sap.engine.services.dc.cm.undeploy;

import java.util.Collection;
import java.util.Set;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.utils.measurement.DataMeasurements;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public abstract class UndeploymentHelperFactory {

	private static final String FACTORY_IMPL = "com.sap.engine.services.dc.cm.undeploy.impl.UndeploymentHelperFactoryImpl";
	private final static String EOL = System.getProperty("line.separator");

	private static UndeploymentHelperFactory INSTANCE;

	protected UndeploymentHelperFactory() {
	}

	/**
	 * @return the object reference for the factory. The class is implemented as
	 *         a Singleton.
	 */
	public static synchronized UndeploymentHelperFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = createFactory();
		}
		return INSTANCE;
	}

	private static UndeploymentHelperFactory createFactory() {
		try {
			final Class classFactory = Class.forName(FACTORY_IMPL);
			return (UndeploymentHelperFactory) classFactory.newInstance();
		} catch (Exception e) {
			final String errMsg = "ASJ.dpl_dc.003247 An error occurred while creating an instance of "
					+ "class UndeploymentDataFactory! " + EOL + e.getMessage();

			throw new RuntimeException(errMsg);
		}
	}

	/**
	 * @return a new <code>UndeploymentData</code>.
	 */
	public abstract UndeploymentData createUndeploymentData(
			Collection<GenericUndeployItem> sortedUndeployItems,
			UndeploymentBatch undeploymentBatch, String sessionId,
			Collection<UndeploymentObserver> undeploymentObservers,
			ErrorStrategy undeploymentErrorStrategy,
			UndeploymentStrategy undeploymentStrategy,
			UndeployWorkflowStrategy workflowStrategy,
			UndeployParallelismStrategy undeployParallelismStrategy,
			UndeployListenersList undeployListenersList,
			DataMeasurements dataMeasurements, String userUniqueId,
			String callerHost);

	public abstract SafeModeUndeployer createSafeModeUndeployer(
			UndeploymentData undeploymentData) throws UndeploymentException;

	public abstract SdaUndeployItemId createSdaUndeployItemId(String name,
			String vendor);

	public abstract ScaUndeployItemId createScaUndeployItemId(String name,
			String vendor);

	public abstract UndeploymentBatch createUndeploymentBatch(
			Set<GenericUndeployItem> undeployItems);

	public abstract UndeployItem createSdaUndeployItem(SdaUndeployItemId uiId);

	public abstract ScaUndeployItem createScaUndeployItem(ScaUndeployItemId uiId);
}
