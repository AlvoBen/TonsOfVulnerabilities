package com.sap.engine.deployment.proxy;

import javax.enterprise.deploy.shared.ModuleType;

import com.sap.engine.lib.io.SerializableFile;
import com.sap.engine.deployment.SAPTarget;
import com.sap.engine.deployment.SAPTargetModuleID;
import com.sap.engine.deployment.exceptions.SAPTargetException;
import com.sap.engine.deployment.exceptions.SAPRemoteException;
import com.sap.engine.deployment.exceptions.SAPIllegalStateException;

/**
 * @author Mariela Todorova
 */
public interface ManagementProxy {

	public SAPTargetModuleID[] getRunningModules(ModuleType moduleType,
			SAPTarget[] targetList) throws SAPTargetException,
			SAPIllegalStateException, SAPRemoteException;

	public SAPTargetModuleID[] getNonRunningModules(ModuleType moduleType,
			SAPTarget[] targets) throws SAPTargetException,
			SAPIllegalStateException, SAPRemoteException;

	public SAPTargetModuleID[] getAvailableModules(ModuleType moduleType,
			SAPTarget[] targets) throws SAPTargetException,
			SAPIllegalStateException, SAPRemoteException;

	public SAPTargetModuleID[] determineTargetModules(String[] modules,
			SAPTarget[] targets) throws SAPRemoteException;

	public SerializableFile getClientJar(SAPTargetModuleID[] targetModuleIDs)
			throws SAPRemoteException;
}