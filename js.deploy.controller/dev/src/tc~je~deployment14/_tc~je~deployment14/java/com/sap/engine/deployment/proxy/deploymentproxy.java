package com.sap.engine.deployment.proxy;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.xml.sax.SAXException;

import com.sap.engine.deployment.SAPTarget;
import com.sap.engine.deployment.SAPTargetModuleID;
import com.sap.engine.deployment.exceptions.SAPIllegalStateException;
import com.sap.engine.deployment.exceptions.SAPRemoteException;
import com.sap.engine.lib.deploy.sda.exceptions.DeployLibException;
import com.sap.exception.standard.SAPUnsupportedOperationException;

/**
 * @author Mariela Todorova
 */
public interface DeploymentProxy extends ConnectionManager, ManagementProxy {

	public String[] distribute(SAPTarget[] targetList, File moduleArchive,
			Properties props) throws DeployLibException, IOException,
			SAXException;

	public void start(SAPTargetModuleID[] targetModuleIDs)
			throws SAPRemoteException;

	public void stop(SAPTargetModuleID[] targetModuleIDs)
			throws SAPRemoteException;

	public void undeploy(SAPTargetModuleID[] targetModuleIDs)
			throws SAPRemoteException;

	public String[] redeploy(SAPTargetModuleID[] targetModuleIDs,
			File moduleArchive, Properties props)
			throws SAPUnsupportedOperationException, SAPIllegalStateException,
			IOException, DeployLibException, SAXException;

}