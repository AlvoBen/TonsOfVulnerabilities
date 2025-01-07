/*
 * ReferencesTransaction.java
 *
 * Created on April 17, 2002, 3:36 PM
 */
package com.sap.engine.services.deploy.server.library;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.locking.LockingConstants;
import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.DTransaction;
import com.sap.engine.services.deploy.server.DeployConstants;
import com.sap.engine.services.deploy.server.DeployServiceContext;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.TransactionStatistics;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.editor.DIWriter;
import com.sap.engine.services.deploy.server.editor.EditorFactory;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.remote.RemoteCaller;
import com.sap.engine.services.deploy.server.utils.concurrent.ConflictingOperationLockException;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSet;
import com.sap.engine.services.deploy.server.utils.concurrent.LockSetNotAcquiredException;
import com.sap.engine.services.deploy.server.utils.concurrent.eval.SingleNodeLockEvaluator;
import com.sap.tc.logging.Location;


/**
 * 
 * @author Radoslav Tsiklovski, Rumiana Angelova
 * @version 6.30
 */
public abstract class ReferencesTransaction implements DTransaction {
	
	private static final Location location = 
		Location.getLocation(ReferencesTransaction.class);
	
	protected DTransaction childTransaction = null;

	private LockSet lockSet;
	private boolean lockNeeded;
	private boolean okFinished = false;
	private String transactionType = null;
	private char lockType = LockingConstants.MODE_EXCLUSIVE_NONCUMULATIVE;
	private ConfigurationHandler handler = null;
	private long beginTime = 0;
	private String moduleID = null;
	private final TransactionStatistics currentStatistics;
	private TransactionStatistics[] remoteStatistics;
	private Date timeOfLastChange = null;

	protected final DeployServiceContext ctx;
	protected DeploymentInfo info = null;

	public ReferencesTransaction(final String fromApp,
		final DeployServiceContext ctx, final String transType)
		throws DeploymentException {
		this.ctx = ctx;
		transactionType = transType;
		if (fromApp == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.MISSING_PARAMETERS,
				getTransactionType(), "from which application");
			sde.setMessageID("ASJ.dpl_ds.005024");
			throw sde;
		}
		setModuleID(fromApp);
		currentStatistics = new TransactionStatistics(
			ctx.getClusterMonitorHelper().getCurrentServerId());
		this.info = ctx.getTxCommunicator().getApplicationInfo(getModuleID());
		if (info == null) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.NOT_DEPLOYED,
				getModuleID(), getTransactionType());
			sde.setMessageID("ASJ.dpl_ds.005005");
			throw sde;
		}
	}

	public void notNeeded() {
		// Empty default method.		
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DTransaction#lock()
	 */
	public void lock() throws LockSetNotAcquiredException,
			InterruptedException, ConflictingOperationLockException {
		final Component component = 
			new Component(getModuleID(), Component.Type.APPLICATION);
		lockSet = ctx.getLockManager().lock(
			new SingleNodeLockEvaluator(getTransactionType(), component,
				isEnqueueLockNeeded() ? getLockType() : 0,
				PropManager.getInstance().getTimeout4LocalLock()));
	}

	public Component getComponent() {
		return new Component(getModuleID(), Component.Type.APPLICATION);
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.deploy.server.DTransaction#unlock()
	 */
	public void unlock() {
		ctx.getLockManager().unlock(lockSet);
	}

	public void setModuleID(String id) throws DeploymentException {
		if (id.indexOf("/") != -1) {
			if (id.indexOf("/") == (id.length() - 1)) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.NOT_SPECIFIED_APP_NAME,
					id, transactionType);
				sde.setMessageID("ASJ.dpl_ds.005012");
				throw sde;
			}
			moduleID = id;
		} else {
			moduleID = "sap.com/" + id;
		}
	}

	public String getModuleID() {
		return this.moduleID;
	}

	public String getSoftwareType() {
		return null;
	}

	public byte getModuleType() {
		return DeployConstants.REF_TYPE;
	}

	public String getModuleTypeAsString() {
		return DeployConstants.RESOURCE_TYPE_APPLICATION;
	}

	public String getTransactionType() {
		return this.transactionType;
	}

	public DTransaction getChildTransaction() {
		return this.childTransaction;
	}

	public void makeAllPhasesLocal() throws DeploymentException,
			ComponentNotDeployedException {
		try {
			beginLocal();
		} catch (DeploymentException dex) {
			rollbackLocal();
			throw dex;
		} catch (ComponentNotDeployedException cnde) {
			rollbackLocal();
			throw cnde;
		}

		try {
			prepareLocal();
		} catch (DeploymentException dex) {
			rollbackPrepareLocal();
			throw dex;
		}
		commitLocal();
	}

	public void makeAllPhases() throws DeploymentException,
			ComponentNotDeployedException {
		try {
			this.begin();
		} catch (DeploymentException rex) {
			this.rollback();
			throw rex;
		} catch (ComponentNotDeployedException cnde) {
			this.rollback();
			throw cnde;
		}
		try {
			this.prepare();
		} catch (DeploymentException rex) {
			this.rollback();
			throw rex;
		}
		this.commit();
		notifyRemotely();
	}

	private void notifyRemotely() throws DeploymentException {
		final Map<String, Object> dict = prepareNotification();
		remoteStatistics = ctx.getRemoteCaller().notifyRemotely(
			dict, ctx.getClusterMonitorHelper().findEligibleReceivers(), true);
	}

	protected Map<String, Object> prepareNotification() {
		final Map<String, Object> dict = new Hashtable<String, Object>();
		dict.put(RemoteCaller.COMMAND, getTransactionType());
		dict.put(RemoteCaller.APP_NAME, getModuleID());
		return dict;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.DTransaction#setClusterWideLockNeeded
	 * (boolean)
	 */
	public void setEnqueueLockNeeded(final boolean lockNeeded) {
		this.lockNeeded = lockNeeded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.DTransaction#isClusterWideLockNeeded
	 * ()
	 */
	public boolean isEnqueueLockNeeded() {
		return lockNeeded;
	}

	public void setBeginTime(long begin) {
		this.beginTime = begin;
	}

	public long getBeginTime() {
		return this.beginTime;
	}

	public void begin() throws DeploymentException,
			ComponentNotDeployedException {
		// to do set beckup ????
		if (location.bePath()) {
			DSLog.tracePath(
					location,
					"Begin [{0}] for application [{1}]",
					getTransactionType(), 
					getModuleID());
		}
		ConfigurationHandlerFactory factory = PropManager.getInstance()
				.getConfigurationHandlerFactory();
		Configuration config = null;
		if (factory != null) {
			try {
				handler = factory.getConfigurationHandler();
			} catch (ConfigurationException ce) {
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.CANNOT_GET_HANDLER_AT_BEGINNING,
						new String[] { getTransactionType(), getModuleID() },
						ce);
				sde.setMessageID("ASJ.dpl_ds.005009");
				sde.setDcNameForObjectCaller(factory);
				throw sde;
			}
		} else {
			DSLog.traceWarning(
					location,
					"ASJ.dpl_ds.000254",
					"Configuration manager is not available - can not open configuration.");
		}
		if (handler != null) {
			try {
				config = handler.openConfiguration(
					"apps/" + this.getModuleID(),
					ConfigurationHandler.WRITE_ACCESS);
			} catch (ConfigurationException cex) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_OPEN_CONFIGURATION,
					new String[] { "apps/" + this.getModuleID(),
						this.getModuleID(), this.getTransactionType() },
					cex);
				sde.setMessageID("ASJ.dpl_ds.005011");
				sde.setDcNameForObjectCaller(handler);
				throw sde;
			}
			try {
				ReferenceObject[] newRefsInDB = null;
				if (getTransactionType().equals(DeployConstants.makeRefs)) {
					newRefsInDB = addRefs();
				} else if (getTransactionType().equals(
					DeployConstants.removeRefs)) {
					newRefsInDB = removeRefs();
				}
				final DIWriter diWriter = EditorFactory.getInstance()
					.getDIWriter(info.getVersion());
				diWriter.modifyReferences(config, newRefsInDB);
			} catch (ConfigurationException cex) {
				DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006382",
					"Error in configuration on modify references", cex);
			}
			try {
				handler.commit();
				beginLocal();
			} catch (ConfigurationException e) {
				ServerDeploymentException sde = new ServerDeploymentException(
					ExceptionConstants.CANNOT_COMMIT_HANDLER, new String[] {
						getTransactionType(), getModuleID() }, e);
				sde.setMessageID("ASJ.dpl_ds.005026");
				sde.setDcNameForObjectCaller(handler);
				throw sde;
			} finally {
				try {
					handler.closeAllConfigurations();
				} catch (ConfigurationException cex) {
					DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006383",
						"Exception on closing configurations in begin",
						cex);
				}
				handler = null;
			}
		}
	}

	protected abstract ReferenceObject[] addRefs();

	protected abstract ReferenceObject[] removeRefs();

	public void prepare() throws DeploymentException {
		if (location.bePath()) {
			DSLog.tracePath(location, "Prepare [{0}] for application [{1}]",
				getTransactionType(), getModuleID());
		}
	}

	public void prepareLocal() throws DeploymentException {
		if (location.bePath()) {
			DSLog.tracePath(location, "Prepare local [{0}] for application [{1}]",
				getTransactionType(), getModuleID());
		}
	}

	public void rollback() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Rollback [{0}] for application [{1}]",
				getTransactionType(), getModuleID());
		}
		if (handler != null) {
			try {
				handler.rollback();
			} catch (ConfigurationException cex) {
				DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006384",
					"Error in rollback configuration handler", cex);
			} finally {
				try {
					handler.closeAllConfigurations();
				} catch (ConfigurationException cex) {
					DSLog.logErrorThrowable(location, "ASJ.dpl_ds.006385",
						"Exception on close configurations in rollback",
						cex);
				}
			}
		}
	}

	public void rollbackLocal() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Rollback local [{0}] for application [{1}]",
				getTransactionType(), getModuleID());
		}
	}

	public void rollbackPrepare() {
		if (location.bePath()) {
			DSLog.tracePath(location, "Rollback prepare [{0}] for application [{1}]",
				getTransactionType(), getModuleID());
		}
	}

	public void rollbackPrepareLocal() {
		if (location.bePath()) {
			DSLog.tracePath(location,
				"Rollback prepare local [{0}] for application [{1}]",
				getTransactionType(), getModuleID());
		}
	}

	public TransactionStatistics[] getStatistics() {
		if (remoteStatistics == null) {
			return new TransactionStatistics[] { currentStatistics };
		}
		TransactionStatistics[] temp = 
			new TransactionStatistics[remoteStatistics.length + 1];
		System.arraycopy(
			remoteStatistics, 0, temp, 0, remoteStatistics.length);
			temp[remoteStatistics.length] = currentStatistics;
			return temp;
		}

	public TransactionStatistics getCurrentStatistics() {
		return this.currentStatistics;
	}

	public boolean isNeeded() {
		return true;
	}

	public boolean isSuccessfullyFinished() {
		return isNeeded() ? okFinished : true;
	}

	protected void setSuccessfullyFinished(boolean ok) {
		this.okFinished = ok;
	}

	public char getLockType() {
		return lockType;
	}

	public void setLockType(char lockType) {
		this.lockType = lockType;
	}


	public void addWarning(String w) {
		currentStatistics.addWarning(w);
	}

	protected void generateWarningMessage(Object[] successful, Object[] failed) {
		if ((successful == null || successful.length == 0) &&
			(failed == null || failed.length == 0)) {
			return;
		}
		StringBuffer warning = new StringBuffer();
		warning.append("\nThe opperation "
			+ getTransactionType() + " with application " + getModuleID() +
			" finished. Its changes will be applied after the application" +
			" is restarted manually. ");

		if (successful != null && successful.length != 0) {
			warning.append(getTransactionType() + " was successful for [");
			for (int i = 0; i < successful.length; i++) {
				warning.append(successful[i]
						+ ((i + 1 < successful.length) ? ", " : ""));
			}
			warning.append("]");
		}

		if (failed != null && failed.length != 0) {
			warning.append(getTransactionType() + " failed for [");
			for (int i = 0; i < failed.length; i++) {
				warning.append(failed[i]
						+ ((i + 1 < failed.length) ? ", " : ""));
			}
			warning.append("]");
		}
		if (location.beWarning()) {
			DSLog.traceWarning(location, "ASJ.dpl_ds.000261", "{0}", warning.toString());
		}
		addWarning(warning.toString());
	}

	public boolean isTrackable() {
		return true;
	}

	public Date getTimeOfLastChange() {
		return timeOfLastChange;
	}

	public void setTimeOfLastChange(Date lastChange) {
		timeOfLastChange = lastChange;
	}
}