package com.sap.sdm.apiimpl.remote.client.p4;

import java.io.File;
import java.io.IOException;

import com.sap.sdm.api.remote.ComponentVersionHandlingRule;
import com.sap.sdm.api.remote.DeployItem;
import com.sap.sdm.api.remote.ErrorHandlingRule;
import com.sap.sdm.api.remote.HelperFactory;
import com.sap.sdm.api.remote.Param;
import com.sap.sdm.api.remote.ParamType;
import com.sap.sdm.api.remote.PrerequisiteErrorHandlingRule;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.api.remote.ServerType;
import com.sap.sdm.api.remote.TargetSystem;
import com.sap.sdm.api.remote.UnDeployItem;

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
class P4HelperFactoryImpl implements HelperFactory {

	private static P4HelperFactoryImpl INSTANCE = new P4HelperFactoryImpl();

	private P4HelperFactoryImpl() {
	}

	static P4HelperFactoryImpl getInstance() {
		return INSTANCE;
	}

	public Param createP4InternalParam(String name, Object value)
			throws RemoteException {
		AssertionCheck.checkForNullArgs(getClass(), "createParam",
				new Object[] { name });

		return new P4ParamImpl(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.HelperFactory#createParam(com.sap.sdm.api.remote
	 * .ParamType, java.lang.String, java.lang.String, java.lang.Object,
	 * boolean)
	 */
	public Param createParam(ParamType type, String name, String displayName,
			Object value, boolean shallBeHidden) throws RemoteException {
		AssertionCheck.checkForNullArgs(getClass(), "createParam",
				new Object[] { type, name, displayName });

		return new P4ParamImpl(type, name, displayName, value, shallBeHidden);
	}

	/**
	 * @see com.sap.sdm.api.remote.HelperFactory#createParam(com.sap.sdm.api.remote.ParamType,
	 *      java.lang.String, java.lang.Object, boolean)
	 * @deprecated
	 */
	public Param createParam(ParamType type, String name, Object value,
			boolean shallBeHidden) throws RemoteException {
		AssertionCheck.checkForNullArgs(getClass(), "createParam",
				new Object[] { type, name });

		return new P4ParamImpl(type, name, name, value, shallBeHidden);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.HelperFactory#createType(int)
	 */
	public ParamType createType(int typeAsInt) throws RemoteException {
		return new P4ParamTypeImpl(typeAsInt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.HelperFactory#createTargetSystem(java.lang.String,
	 * com.sap.sdm.api.remote.ServerType, java.lang.String)
	 */
	public TargetSystem createTargetSystem(String id, ServerType type,
			String description) throws RemoteException {
		throw new UnsupportedOperationException(
				"The operation is no more supported "
						+ "as all the target systems are moved as Java EE Containers "
						+ "within the SAP Application Server Java!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.HelperFactory#createDeployItem(java.io.File)
	 */
	public DeployItem createDeployItem(File archive) throws RemoteException,
			IOException {
		AssertionCheck.checkForNullArg(getClass(), "createDeployItem", archive);

		return new P4DeployItemImpl(archive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.HelperFactory#createComponentVersionHandlingRule
	 * (int)
	 */
	public ComponentVersionHandlingRule createComponentVersionHandlingRule(
			int ruleAsInt) throws RemoteException {
		return P4ComponentVersionHandlingRuleImpl
				.getComponentVersionHandlingRuleByInt(ruleAsInt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.HelperFactory#createUndeployItem(java.lang.String,
	 * java.lang.String)
	 */
	public UnDeployItem createUndeployItem(String vendor, String name)
			throws RemoteException {
		AssertionCheck
				.checkForNullArg(getClass(), "createUndeployItem", vendor);
		AssertionCheck.checkForNullArg(getClass(), "createUndeployItem", name);

		return new P4UnDeployItemImpl(vendor, name, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.HelperFactory#createUndeployItem(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public UnDeployItem createUndeployItem(String vendor, String name,
			String location, String counter) throws RemoteException {
		AssertionCheck
				.checkForNullArg(getClass(), "createUndeployItem", vendor);
		AssertionCheck.checkForNullArg(getClass(), "createUndeployItem", name);
		AssertionCheck.checkForNullArg(getClass(), "createUndeployItem",
				location);
		AssertionCheck.checkForNullArg(getClass(), "createUndeployItem",
				counter);

		return new P4UnDeployItemImpl(vendor, name, location, counter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.HelperFactory#createErrorHandlingRule(int)
	 */
	public ErrorHandlingRule createErrorHandlingRule(int ruleAsInt)
			throws RemoteException {
		return P4ErrorHandlingRuleImpl.getErrorHandlingRuleByInt(ruleAsInt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.HelperFactory#createPrerequisiteErrorHandlingRule
	 * (int)
	 */
	public PrerequisiteErrorHandlingRule createPrerequisiteErrorHandlingRule(
			int ruleAsInt) {
		return P4PrerequisiteErrorHandlingRuleImpl
				.getPrerequisiteErrorHandlingRuleByInt(ruleAsInt);
	}

}
