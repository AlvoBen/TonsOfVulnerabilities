package com.sap.engine.services.dc.util.logging;

import static com.sap.engine.services.dc.cm.utils.ResultUtils.logSummary4Deploy;
import static com.sap.engine.services.dc.cm.utils.ResultUtils.logSummary4Undeploy;
import static com.sap.engine.services.dc.util.logging.DCLog.logError;
import static com.sap.engine.services.dc.util.logging.DCLog.logInfo;

import java.util.Arrays;
import java.util.Collection;

import com.sap.engine.services.dc.cm.deploy.DeployResult;
import com.sap.engine.services.dc.cm.deploy.Deployer;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.ValidationException;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.cm.params.Param;
import com.sap.engine.services.dc.cm.params.ParamAlreadyExistsException;
import com.sap.engine.services.dc.cm.params.ParamNotFoundException;
import com.sap.engine.services.dc.cm.params.ParamsException;
import com.sap.engine.services.dc.cm.params.ParamsFactory;
import com.sap.engine.services.dc.cm.params.ParamsFactoryException;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployResult;
import com.sap.engine.services.dc.cm.undeploy.Undeployer;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentException;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.deploy.DeployService;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

/**
 * Tracks all manual write operation done using Telnet or Mbeans
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class DCChangeLog {
	
	private static Location location = DCLog.getLocation(DCChangeLog.class);

	private static final String CH_DPL_CATEGORY = "Deploy";

	private static Category categoryChDpl = Category.getCategory(
			Category.SYS_CHANGES, CH_DPL_CATEGORY);

	private static final String deploy = "deploy";
	private static final String addParams = "addParams";
	private static final String updateParams = "updateParams";
	private static final String removeParams = "removeParams";
	private static final String undeploy = "undeploy";

	private void processDplResult(boolean isOk, String operation, String what,
			String via, String details) {
		if (isOk) {
			logInfoChDplOK(operation, what, via, details);
		} else {
			logErrorChDplNotOK(operation, what, via, details);
		}
	}

	private void logInfoChDplOK(String operation, String what, String via,
			String details) {
		logInfoChDpl(
				"ASJ.dpl_dc.000561",
				"Operation [{0}] over [{1}], started via [{2}] request, finished with success.[{3}]",
				new Object[] { operation, what, via, details });
	}

	private void logErrorChDplNotOK(String operation, String what, String via,
			String details) {
		logErrorChDpl(
				"ASJ.dpl_dc.000562",
				"Operation [{0}] over [{1}], started via [{2}] request, finished with error.[{3}]",
				new Object[] { operation, what, via, details });
	}

	// *************** WRITE *************** //

	public DeployResult deploy(Deployer deployer,
			String[] archiveFilePathNames, String sessionId, String via)
			throws ValidationException, DeploymentException, DCLockException {
		boolean isOk = false;
		Collection dbItems4Result = null;
		try {
			final DeployResult dr = deployer.deploy(archiveFilePathNames,
					sessionId);
			dbItems4Result = dr.getDeploymentItems();
			isOk = true;
			return dr;
		} catch (DeploymentException dEx) {
			dbItems4Result = dEx.getDeploymentBatchItems();
			throw dEx;
		} finally {
			processDplResult(isOk, deploy, Arrays
					.toString(archiveFilePathNames), via,
					logSummary4Deploy(dbItems4Result));
		}
	}

	public void addParams(Param[] params, String via)
			throws ParamAlreadyExistsException, ParamsException,
			ParamsFactoryException {
		boolean isOk = false;
		try {
			ParamsFactory.getInstance().createParamManager().addParams(params);
			isOk = true;
		} finally {
			processDplResult(isOk, addParams, Arrays.toString(params), via,
					Constants.EMPTY);
		}
	}

	public void updateParams(Param[] params, String via)
			throws ParamNotFoundException, ParamsException,
			ParamsFactoryException {
		boolean isOk = false;
		try {
			ParamsFactory.getInstance().createParamManager().updateParams(
					params);
			isOk = true;
		} finally {
			processDplResult(isOk, updateParams, Arrays.toString(params), via,
					Constants.EMPTY);
		}
	}

	public void removeParams(Param[] params, String via)
			throws ParamNotFoundException, ParamsException,
			ParamsFactoryException {
		boolean isOk = false;
		try {
			ParamsFactory.getInstance().createParamManager().removeParams(
					params);
			isOk = true;
		} finally {
			processDplResult(isOk, removeParams, Arrays.toString(params), via,
					Constants.EMPTY);
		}
	}

	public UndeployResult undeploy(Undeployer undeployer,
			GenericUndeployItem[] undeployItems, String sessionId, String via)
			throws UndeploymentException, DCLockException {
		boolean isOk = false;
		Collection uItems4Result = null;
		try {
			final UndeployResult ur = undeployer.undeploy(undeployItems,
					sessionId);
			uItems4Result = ur.getUndeployItems();
			isOk = true;
			return ur;
		} catch (UndeploymentException uEx) {
			uItems4Result = uEx.getUndeployItems();
			throw uEx;
		} finally {
			processDplResult(isOk, undeploy, Arrays.toString(undeployItems),
					via, logSummary4Undeploy(uItems4Result));
		}
	}

	public void remove(DeployService deploy, String applicationName, String via)
			throws java.rmi.RemoteException {
		boolean isOk = false;
		try {
			deploy.remove(applicationName);
			isOk = true;
		} finally {
			processDplResult(isOk, undeploy, applicationName, via,
					Constants.EMPTY);
		}
	}

	// ************** help ************** //

	// Logs messages with severity Severity.INFO in case of deploy service issue
	private static void logInfoChDpl(String messageID, String message,
			Object... args) {
		logInfo(location, categoryChDpl, messageID, message, args);
	}

	// Logs messages with severity Severity.ERROR in case of deploy service
	// issue
	private static void logErrorChDpl(String messageID, String message,
			Object... args) {
		logError(location, categoryChDpl, messageID, message, args);
	}

}
