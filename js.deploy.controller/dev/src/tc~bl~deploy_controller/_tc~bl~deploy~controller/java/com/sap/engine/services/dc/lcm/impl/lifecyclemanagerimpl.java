package com.sap.engine.services.dc.lcm.impl;

import static com.sap.engine.services.dc.util.PerformanceUtil.isBoostPerformanceDisabled;
import static com.sap.engine.services.dc.util.ThreadUtil.popTask;
import static com.sap.engine.services.dc.util.ThreadUtil.pushTask;
import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.rmi.RemoteException;

import javax.naming.NamingException;

import com.sap.engine.services.dc.event.EventMode;
import com.sap.engine.services.dc.event.LCEventListener;
import com.sap.engine.services.dc.lcm.LCMCompNotFoundException;
import com.sap.engine.services.dc.lcm.LCMException;
import com.sap.engine.services.dc.lcm.LCMResult;
import com.sap.engine.services.dc.lcm.LCMStatus;
import com.sap.engine.services.dc.lcm.LifeCycleManager;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.explorer.AbstractRemoteRepositoryExplorerFactory;
import com.sap.engine.services.dc.repo.explorer.RepositoryExplorer;
import com.sap.engine.services.dc.repo.explorer.RepositoryExploringException;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.engine.services.deploy.DeployService;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */

final class LifeCycleManagerImpl implements LifeCycleManager {
	
	private Location location = getLocation(this.getClass());
	
	private DeployService deployService;
	private LCMDSCallback dsCallback;
	private String[] currentParticipant;
	private static final String START = "start";
	private static final String STOP = "stop";

	// TODO: use the repository explorer

	LifeCycleManagerImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.LifeCycleManager#start(java.lang.String,
	 * java.lang.String)
	 */
	public LCMResult start(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException {
		try {
			if (isBoostPerformanceDisabled()) {
				pushTask(START, componentName, componentVendor);
			}

			if (location.bePath()) {
				tracePath(location, 
						"Starting component with name [{0}] and vendor [{1}]",
						new Object[] { componentName, componentVendor });
			}

			final Sda sda = findSda(componentName, componentVendor);

			final AbstractLCMProcessor lcmProcessor = LCMMapper.map(sda);

			return lcmProcessor.doStart(sda);
		} finally {
			if (isBoostPerformanceDisabled()) {
				popTask();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.LifeCycleManager#stop(java.lang.String,
	 * java.lang.String)
	 */
	public LCMResult stop(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException {
		try {
			if (isBoostPerformanceDisabled()) {
				pushTask(STOP, componentName, componentVendor);
			}

			final Sda sda = findSda(componentName, componentVendor);

			final AbstractLCMProcessor lcmProcessor = LCMMapper.map(sda);

			return lcmProcessor.doStop(sda);
		} finally {
			if (isBoostPerformanceDisabled()) {
				popTask();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.LifeCycleManager#getLCMStatus(java.lang
	 * .String, java.lang.String)
	 */
	public LCMStatus getLCMStatus(String componentName, String componentVendor)
			throws LCMCompNotFoundException, LCMException {
		final Sda sda = findSda(componentName, componentVendor);

		final AbstractLCMProcessor lcmProcessor = LCMMapper.map(sda);

		return lcmProcessor.getLCMStatus(sda);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.LifeCycleManager#getLCMStatuses(com.sap
	 * .engine.services.dc.repo.SdaId[])
	 */
	public LCMStatus[] getLCMStatuses(SdaId[] sdaIds) {
		int count = sdaIds != null ? sdaIds.length : 0;
		LCMStatus[] statuses = new LCMStatus[count];
		for (int i = 0; i < count; i++) {
			try {
				if (sdaIds[i] != null) {
					statuses[i] = getLCMStatus(sdaIds[i].getName(), sdaIds[i]
							.getVendor());
				} else {
					statuses[i] = LCMStatus.UNKNOWN;
				}
			} catch (LCMCompNotFoundException e) {
				statuses[i] = LCMStatus.UNKNOWN;
			} catch (LCMException e) {
				statuses[i] = LCMStatus.UNKNOWN;
			}
		}
		return statuses;
	}

	private Sda findSda(String name, String vendor)
			throws LCMCompNotFoundException {
		Sda sda = null;
		RepositoryExplorer repoExplorer;
		try {
			repoExplorer = AbstractRemoteRepositoryExplorerFactory
					.getInstance().createRepositoryExplorer();
			sda = repoExplorer.findSda(name, vendor);
		} catch (RepositoryExploringException e) {
			throw new LCMCompNotFoundException(
					"ASJ.dpl_dc.003320 Cannot find SDA with name '"
							+ name + "' and vendor '" + vendor + "'. Reason:"
							+ e.getLocalizedMessage(), e);
		}
		/*
		 * checkArg(name, "development component name"); checkArg(vendor,
		 * "development component vendor");
		 * 
		 * final String trimmedName = name.trim(); final String trimmedVendor =
		 * vendor.trim();
		 * 
		 * final String correctedName = getCorrectedValue(trimmedName); final
		 * String correctedVendor = getCorrectedValue(trimmedVendor);
		 * 
		 * checkCorrectedValue(trimmedName, correctedName,
		 * "development component name"); checkCorrectedValue(trimmedVendor,
		 * trimmedVendor, "development component vendor");
		 * 
		 * final SdaId sdaId =
		 * RepositoryComponentsFactory.getInstance().createSdaId(correctedName,
		 * correctedVendor);
		 * 
		 * final Sda sda = (Sda)
		 * RepositoryContainer.getDeploymentsContainer().getDeployment(sdaId);
		 */
		if (sda == null) {
			throw new LCMCompNotFoundException(
					"ASJ.dpl_dc.003321 The system did not find registered development "
							+ "component  with name '" + name
							+ "' and vendor '" + vendor + "'");
		}

		return sda;
	}

	/*
	 * private void checkArg(String argValue, String argName) throws
	 * NullPointerException, IllegalArgumentException { if (argValue == null) {
	 * throw new NullPointerException("The " + argName + " could not be null.");
	 * }
	 * 
	 * if ( argValue.trim().equals("") ) { throw new
	 * IllegalArgumentException("The " + argName +
	 * " could not be an empty string."); } }
	 * 
	 * private String getCorrectedValue(String value) { return
	 * ComponentPropsCorrector.getCorrected(value); }
	 * 
	 * private void checkCorrectedValue(String value, String correctedValue,
	 * String paramName) { if ( !value.equals(correctedValue) ) {
	 * DCLog.logWarning( DCLogConstants.REPO_WILL_BE_CORRECTED_IT_IS_, new
	 * Object[] { value, paramName, correctedValue}); } }
	 */
	public void addLCEventListener(LCEventListener listener, EventMode eventMode)
			throws LCMException {
		if (this.deployService == null) {
			this.deployService = getDeployService();
		}
		if (this.deployService == null) {
			throw new LCMException(
					"ASJ.dpl_dc.003322 Deploy Service refference is 'null'");
		}
		if (this.dsCallback == null) {
			this.dsCallback = new LCMDSCallback();
		}
		this.dsCallback.addLCEventListener(listener, eventMode);
		try {
			if (location.beDebug()) {
				traceDebug(
						location,
						"Try to register LCM Deploy Callback to the DeployService.");
			}
			this.deployService.registerDeployCallback(this.dsCallback,
					getCurrentParticipant());
			if (location.bePath()) {
				tracePath(location, 
						"LCM Deploy Callback registered successfully to the DeployService.");
			}
		} catch (RemoteException e) {
			throw new LCMException(
					"ASJ.dpl_dc.003323 An error occurred while register the LCM callback to the Deploy Service.",
					e);
		}
	}

	public void removeLCEventListener(LCEventListener listener)
			throws LCMException {
		if (this.dsCallback != null) {
			this.dsCallback.removeLCEventListener(listener);
			if (this.dsCallback.isEmpty() && this.deployService != null) {
				try {
					this.deployService.unregisterDeployCallback(
							this.dsCallback, getCurrentParticipant());
				} catch (RemoteException e) {
					throw new LCMException(
							"ASJ.dpl_dc.003324 An error occurred while unregister the LCM callback from the Deploy Service.",
							e);
				}
			}
		}
	}

	private synchronized String[] getCurrentParticipant() {
		if (this.currentParticipant == null) {
			this.currentParticipant = new String[] { ServiceConfigurer
					.getInstance().getClusterMonitor().getCurrentParticipant()
					.getName() };
		}
		return this.currentParticipant;
	}

	private DeployService getDeployService() throws LCMException {
		try {
			if (location.beDebug()) {
				traceDebug(location, "Lookup DeployService ...");
			}
			DeployService dsReff = ServiceConfigurer.getInstance()
					.getDeployService();
			if (location.bePath()) {
				tracePath( 
						location,
						"... DeployService looked up successfully.");
			}
			return dsReff;
		} catch (NamingException ne) {
			throw new LCMException(
					"ASJ.dpl_dc.003325 An error occurred while getting the Deploy Service from the LCM.",
					ne);
		}
	}
}
