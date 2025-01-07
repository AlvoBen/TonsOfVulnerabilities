package com.sap.engine.deployment.operations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.enterprise.deploy.shared.CommandType;
import javax.enterprise.deploy.shared.ModuleType;
import javax.enterprise.deploy.shared.StateType;
import javax.enterprise.deploy.spi.status.ProgressEvent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.Logger;
import com.sap.engine.deployment.SAPTarget;
import com.sap.engine.deployment.proxy.DeploymentProxy;
import com.sap.engine.deployment.proxy.ApplicationTuner;
import com.sap.engine.deployment.status.SAPDeploymentStatus;
import com.sap.engine.deployment.exceptions.SAPIOException;
import com.sap.engine.deployment.exceptions.SAPRemoteException;
import com.sap.engine.lib.deploy.sda.exceptions.DeployLibException;

/**
 * @author Mariela Todorova
 */
public class DistributeOperation extends Operation {
	private static final String COULD_NOT_DISTRIBUTE_ARCHIVE = "Could not distribute {0}";
	private static final String COULD_NOT_INITIALIZE_DISTRIBUTE_STREAM_OPERATION = "Could not initialize distribute stream operation";
	private static final String COULD_NOT_INITIALIZE_DISTRIBUTE_OPERATION = "Could not initialize distribute operation";
	private static final Location location = Location
			.getLocation(DistributeOperation.class);
	private SAPTarget[] targets = null;
	private File source = null;
	private Properties props = null;
	private ApplicationTuner tuner = null;

	public DistributeOperation(DeploymentProxy dProxy, SAPTarget[] target,
			File src, File plan) {
		super(dProxy);
		this.status = new SAPDeploymentStatus(CommandType.DISTRIBUTE);

		try {
			tuner = new ApplicationTuner(src, plan);
			this.source = tuner.getArchive();
			this.props = tuner.getProperties();
		} catch (SAPIOException ioe) {
			this.handleDistributeException(ioe,
					COULD_NOT_INITIALIZE_DISTRIBUTE_OPERATION);
			return;
		} catch (ParserConfigurationException e) {
			this.handleDistributeException(e,
					COULD_NOT_INITIALIZE_DISTRIBUTE_OPERATION);
			return;
		} catch (TransformerException e) {
			this.handleDistributeException(e,
					COULD_NOT_INITIALIZE_DISTRIBUTE_OPERATION);
			return;
		}

		this.targets = target;
		Logger.trace(location, Severity.DEBUG, "Distributing " + source
				+ " with properties " + props.toString());
	}

	public DistributeOperation(DeploymentProxy dProxy, SAPTarget[] target,
			InputStream src, InputStream plan) {
		super(dProxy);
		this.status = new SAPDeploymentStatus(CommandType.DISTRIBUTE);

		try {
			tuner = new ApplicationTuner(src, plan);
			this.source = tuner.getArchive();
			this.props = tuner.getProperties();
		} catch (SAPIOException ioe) {
			this.handleDistributeException(ioe,
					COULD_NOT_INITIALIZE_DISTRIBUTE_STREAM_OPERATION);
			return;
		} catch (ParserConfigurationException e) {
			this.handleDistributeException(e,
					COULD_NOT_INITIALIZE_DISTRIBUTE_STREAM_OPERATION);
			return;
		} catch (TransformerException e) {
			this.handleDistributeException(e,
					COULD_NOT_INITIALIZE_DISTRIBUTE_STREAM_OPERATION);
			return;
		}

		this.targets = target;
		Logger.trace(location, Severity.DEBUG, "Distributing " + source
				+ " with properties " + props.toString());
	}

	public DistributeOperation(DeploymentProxy dProxy, SAPTarget[] target,
			ModuleType mType, InputStream src, InputStream plan) {
		super(dProxy);
		this.status = new SAPDeploymentStatus(CommandType.DISTRIBUTE);

		try {
			tuner = new ApplicationTuner(mType, src, plan);
			this.source = tuner.getArchive();
			this.props = tuner.getProperties();
		} catch (SAPIOException ioe) {
			this.handleDistributeException(ioe,
					COULD_NOT_INITIALIZE_DISTRIBUTE_STREAM_OPERATION);
			return;
		} catch (ParserConfigurationException e) {
			this.handleDistributeException(e,
					COULD_NOT_INITIALIZE_DISTRIBUTE_STREAM_OPERATION);
			return;
		} catch (TransformerException e) {
			this.handleDistributeException(e,
					COULD_NOT_INITIALIZE_DISTRIBUTE_STREAM_OPERATION);
			return;
		}

		this.targets = target;
		Logger.trace(location, Severity.DEBUG, "Distributing " + mType + " "
				+ source + " with properties " + props.toString());
	}

	/**
	 * @see DistributeOperation#handleDistributeException(Throwable, String,
	 *      String[])
	 * @param e
	 * @param message
	 */
	private void handleDistributeException(Throwable e, String message) {
		this.handleDistributeException(e, message, null);
	}

	/**
	 * If an exception is thrown when working with an ApplicationTuner object,
	 * the method sets the status of the operation to FAILED, fired a new
	 * ProgressEvent and logs the exception.
	 * 
	 * @param e
	 * @param message
	 */
	private void handleDistributeException(Throwable e, String message,
			String[] pieces) {
		if (null == pieces) {
			Logger.logThrowable(location, Severity.ERROR, message, e);
		} else {
			Logger.logThrowable(location, Severity.ERROR, message, pieces, e);
		}
		this.status.setStateType(StateType.FAILED);
		this.status.setMessage(e.getMessage());
		this.fireProgressEvent(new ProgressEvent(this, null, status));

		if (tuner != null) {
			tuner.clear();
		}
	}

	public void run() {
		Logger.trace(location, Severity.PATH,
				"Distribute operation started for " + source);
		String[] result = null;

		try {
			result = proxy.distribute(targets, source, props);
			this.fireProgressEvent(new ProgressEvent(this, null, status));
			this.targetModules = proxy.determineTargetModules(result, targets);
		} catch (SAPRemoteException re) {
			this.handleDistributeException(re, COULD_NOT_DISTRIBUTE_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		} catch (DeployLibException dle) {
			this.handleDistributeException(dle, COULD_NOT_DISTRIBUTE_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		} catch (IOException ioe) {
			this.handleDistributeException(ioe, COULD_NOT_DISTRIBUTE_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		} catch (SAXException saxe) {
			this.handleDistributeException(saxe, COULD_NOT_DISTRIBUTE_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		} catch (OutOfMemoryError oom){
			throw oom;
		} catch (ThreadDeath td){
			throw td;
		} catch (Throwable th){
			this.handleDistributeException(th, COULD_NOT_DISTRIBUTE_ARCHIVE,
					new String[] { source.getAbsolutePath() });
			return;
		}

		this.status.setStateType(StateType.COMPLETED);
		Logger.trace(location, Severity.DEBUG,
				"Distribute operation completed for " + source);

		if (targetModules == null) {
			this.fireProgressEvent(new ProgressEvent(this, null, status));
		} else {
			for (int i = 0; i < targetModules.length; i++) {
				Logger.log(location, Severity.INFO, "Target module "
						+ targetModules[i] + " distributed successfully");
				this.fireProgressEvent(new ProgressEvent(this,
						targetModules[i], status));
			}
		}

		if (tuner != null) {
			tuner.clear();
		}
	}

}