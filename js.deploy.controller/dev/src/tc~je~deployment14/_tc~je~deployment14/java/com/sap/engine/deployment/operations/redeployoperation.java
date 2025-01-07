package com.sap.engine.deployment.operations;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.Logger;
import com.sap.engine.deployment.SAPTargetModuleID;
import com.sap.engine.deployment.proxy.DeploymentProxy;
import com.sap.engine.deployment.proxy.ApplicationTuner;
import com.sap.engine.deployment.status.SAPDeploymentStatus;
import com.sap.engine.deployment.exceptions.SAPIOException;
import com.sap.engine.deployment.exceptions.SAPIllegalStateException;
import com.sap.engine.deployment.exceptions.SAPRemoteException;
import com.sap.engine.lib.deploy.sda.exceptions.DeployLibException;
import com.sap.exception.standard.SAPUnsupportedOperationException;

/**
 * @author Mariela Todorova
 */
public class RedeployOperation extends Operation {
	private static final String COULD_NOT_REDEPLOY_ARCHIVE = "Could not redeploy {0}";
	private static final String COULD_NOT_INITIALIZE_REDEPLOY_STREAM_OPERATION = "Could not initialize redeploy stream operation";
	private static final String COULD_NOT_INITIALIZE_REDEPLOY_OPERATION = "Could not initialize redeploy operation";
	private static final Location location = Location
			.getLocation(RedeployOperation.class);
	private File source = null;
	private Properties props = null;
	private ApplicationTuner tuner = null;

	public RedeployOperation(DeploymentProxy dProxy,
			SAPTargetModuleID[] modules, File src, File plan) {
		super(dProxy);
		this.targetModules = modules;
		this.status = new SAPDeploymentStatus(CommandType.REDEPLOY);

		try {
			tuner = new ApplicationTuner(src, plan);
			this.source = tuner.getArchive();
			this.props = tuner.getProperties();
		} catch (SAPIOException ioe) {
			this.handleRedeployException(ioe, COULD_NOT_INITIALIZE_REDEPLOY_OPERATION);
			return;
		} catch (ParserConfigurationException e) {
			this.handleRedeployException(e, COULD_NOT_INITIALIZE_REDEPLOY_OPERATION);
			return;
		} catch (TransformerException e) {
			this.handleRedeployException(e, COULD_NOT_INITIALIZE_REDEPLOY_OPERATION);
			return;
		}

		Logger.trace(location, Severity.DEBUG, "Redeploying " + source
				+ " with properties " + props.toString());
	}

	public RedeployOperation(DeploymentProxy dProxy,
			SAPTargetModuleID[] modules, InputStream src, InputStream plan) {
		super(dProxy);
		this.targetModules = modules;
		this.status = new SAPDeploymentStatus(CommandType.REDEPLOY);

		try {
			tuner = new ApplicationTuner(src, plan);
			this.source = tuner.getArchive();
			this.props = tuner.getProperties();
		} catch (SAPIOException ioe) {
			this.handleRedeployException(ioe,
					COULD_NOT_INITIALIZE_REDEPLOY_STREAM_OPERATION);
			return;
		} catch (ParserConfigurationException e) {
			this.handleRedeployException(e,
					COULD_NOT_INITIALIZE_REDEPLOY_STREAM_OPERATION);
			return;
		} catch (TransformerException e) {
			this.handleRedeployException(e,
					COULD_NOT_INITIALIZE_REDEPLOY_STREAM_OPERATION);
			return;
		}

		Logger.trace(location, Severity.DEBUG, "Redeploying " + source
				+ " with properties " + props.toString());
	}

	/**
	 * @see RedeployOperation#handleRedeployException(Throwable, String,
	 *      String[])
	 * @param e
	 * @param message
	 */
	private void handleRedeployException(Throwable e, String message) {
		this.handleRedeployException(e, message, null);
	}

	/**
	 * If an exception is thrown when working with an
	 * <code>ApplicationTuner</code> object, the method sets the status of the
	 * operation to FAILED, fired new <code>ProgressEvent</code>s and logs
	 * the exception.
	 * 
	 * @param e
	 * @param message
	 */
	private void handleRedeployException(Throwable e, String message,
			String[] pieces) {
		if (null == pieces) {
			Logger.logThrowable(location, Severity.ERROR, message, e);
		} else {
			Logger.logThrowable(location, Severity.ERROR, message, pieces, e);
		}
		this.status.setStateType(StateType.FAILED);
		this.status.setMessage(e.getMessage());

		for (int i = 0; i < targetModules.length; i++) {
			this.fireProgressEvent(new ProgressEvent(this, targetModules[i],
					status));
		}

		if (tuner != null) {
			tuner.clear();
		}
	}

	public void run() {
		Logger.trace(location, Severity.PATH, "Redeploy operation started for "
				+ source);

		if (targetModules == null || targetModules.length == 0) {
			// should not happen - already checked!
			return;
		}

		String[] result = null;
		ArrayList list = new ArrayList();

		try {
			for (int i = 0; i < targetModules.length; i++) {
				if (targetModules[i].getParentTargetModuleID() != null) {
					Logger.trace(location, Severity.DEBUG,
							"Skipping non-root target module "
									+ targetModules[i].toString());
					continue;
				}

				list.add(targetModules[i]);
			}

			Logger.trace(location, Severity.DEBUG, "Modules to redeploy: "
					+ list.toString());
			result = proxy.redeploy((SAPTargetModuleID[]) list
					.toArray(new SAPTargetModuleID[0]), source, props);

			for (int i = 0; i < list.size(); i++) {
				this.fireProgressEvent(new ProgressEvent(this,
						(SAPTargetModuleID) list.get(i), status));
			}

			this.targetModules = proxy.determineTargetModules(result, proxy
					.getTargets());
		} catch (SAPRemoteException re) {
			this.handleRedeployException(re, COULD_NOT_REDEPLOY_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		} catch (SAPUnsupportedOperationException uoe) {
			this.handleRedeployException(uoe, COULD_NOT_REDEPLOY_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		} catch (SAPIllegalStateException iae) {
			this.handleRedeployException(iae, COULD_NOT_REDEPLOY_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		} catch (IOException ioe) {
			this.handleRedeployException(ioe, COULD_NOT_REDEPLOY_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		} catch (DeployLibException sle) {
			this.handleRedeployException(sle, COULD_NOT_REDEPLOY_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		} catch (SAXException saxe) {
			this.handleRedeployException(saxe, COULD_NOT_REDEPLOY_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		} catch (OutOfMemoryError oom){
			throw oom;
		} catch (ThreadDeath td){
			throw td;
		} catch (Throwable th){
			this.handleRedeployException(th, COULD_NOT_REDEPLOY_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		}


		this.status.setStateType(StateType.COMPLETED);
		Logger.trace(location, Severity.DEBUG,
				"Redeploy operation completed for " + source);

		if (targetModules == null) {
			this.fireProgressEvent(new ProgressEvent(this, null, status));
		} else {
			for (int i = 0; i < targetModules.length; i++) {
				Logger.log(location, Severity.INFO, "Target module "
						+ targetModules[i] + " redeployed successfully");
				this.fireProgressEvent(new ProgressEvent(this,
						targetModules[i], status));
			}
		}

		if (tuner != null) {
			tuner.clear();
		}
	}

}