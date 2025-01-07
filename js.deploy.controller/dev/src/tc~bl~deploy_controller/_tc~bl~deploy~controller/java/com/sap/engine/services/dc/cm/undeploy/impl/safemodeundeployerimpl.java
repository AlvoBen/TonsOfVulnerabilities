package com.sap.engine.services.dc.cm.undeploy.impl;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.lock.LockAction;
import com.sap.engine.services.dc.cm.server.Server;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.cm.server.spi.OnlineOfflineSoftwareType;
import com.sap.engine.services.dc.cm.server.spi.RestartServerService;
import com.sap.engine.services.dc.cm.server.spi.RestartServerService.RestartServerServiceException;
import com.sap.engine.services.dc.cm.undeploy.SafeModeUndeployer;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentData;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentException;
import com.sap.engine.services.dc.cm.undeploy.UndeploymentObserver;
import com.sap.engine.services.dc.cm.undeploy.storage.UndeploymentDataStorageFactory;
import com.sap.engine.services.dc.cm.undeploy.storage.UndeploymentDataStorageManager;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.repo.Repository;
import com.sap.engine.services.dc.repo.RepositoryFactory;
import com.sap.engine.services.dc.util.CollectionEnumerationMapper;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-23
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @version 1.0
 * @since 7.0
 * 
 */
final class SafeModeUndeployerImpl implements SafeModeUndeployer {
	
	private Location location = DCLog.getLocation(this.getClass());

	private UndeploymentData undeploymentData;
	private UndeploymentDataStorageManager undeplDataStorageManager;

	SafeModeUndeployerImpl(UndeploymentData undeploymentData)
			throws UndeploymentException {
		init(undeploymentData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.SafeModeUndeployer#undeployOfflineData
	 * ()
	 */
	public void undeployOfflineData() throws UndeploymentException {
		doProcessUndeploymentData(this.undeploymentData,
				OnlineOfflineSoftwareType.OFFLINE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.SafeModeUndeployer#undeployOnlineData
	 * ()
	 */
	public void undeployOnlineData() throws UndeploymentException {
		doProcessUndeploymentData(this.undeploymentData,
				OnlineOfflineSoftwareType.ONLINE);

		doPostProcess(this.undeploymentData);
	}

	private void init(UndeploymentData _undeploymentData)
			throws UndeploymentException {
		if (_undeploymentData == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003206 The loaded undeployment data is null. The system "
							+ "could not perform the safe mode undeployment.");
		}
		this.undeploymentData = _undeploymentData;
		attachUndeplObservers(_undeploymentData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.undeploy.SafeModeUndeployer#clearData()
	 */
	public void clearData() {
		this.undeploymentData.clear();
	}

	private synchronized UndeploymentDataStorageManager getStorageManager() {
		if (this.undeplDataStorageManager == null) {
			this.undeplDataStorageManager = UndeploymentDataStorageFactory
					.getInstance().createUndeploymentDataStorageManager(
							ServiceConfigurer.getInstance()
									.getConfigurationHandlerFactory());
		}

		return this.undeplDataStorageManager;
	}

	private void attachUndeplObservers(UndeploymentData _undeploymentData) {
		final Set undeployObservers = InternalUndeplObserversInitializer
				.getInstance().initUndeployObservers();
		for (Iterator iter = undeployObservers.iterator(); iter.hasNext();) {
			final UndeploymentObserver observer = (UndeploymentObserver) iter
					.next();
			_undeploymentData.addUndeploymentObserver(observer);
		}
	}

	private void doProcessUndeploymentData(UndeploymentData _undeploymentData,
			OnlineOfflineSoftwareType onOffSoftwareType)
			throws UndeploymentException {
		final Collection sortedUndeplItems = _undeploymentData
				.getSortedUndeploymentBatchItem();
		final Enumeration undeploymentEnum = getUndeploymentEnumerator(
				_undeploymentData, CollectionEnumerationMapper
						.map(sortedUndeplItems), onOffSoftwareType);
		final Repository repo = RepositoryFactory.getInstance()
				.createRepository();

		try {
			UndeployPhase currentUndeployPhase = UndeployPhase.ONLINE;
			while (undeploymentEnum.hasMoreElements()) {
				final GenericUndeployItem item = (GenericUndeployItem) undeploymentEnum
						.nextElement();

				if (!OnlineOfflineSoftwareType.OFFLINE
						.equals(onOffSoftwareType)) {
					final UndeployPhase undeployPhase = UndeployPhaseGetter
							.getInstance().getPhase(item);
					// the following "if" statements are not merged because of
					// readability
					if (!UndeployPhase.UNKNOWN.equals(undeployPhase)) {
						if (!currentUndeployPhase.equals(undeployPhase)) {
							if (currentUndeployPhase
									.equals(UndeployPhase.OFFLINE)) {
								// this means that until now there were offline
								// undeployments performed,
								// but now there is an online one, so we have to
								// stop here and to
								// perform all the offline logic. After the
								// restart the system has to
								// continue with the undeployment of the current
								// item
								break;
							} else {
								currentUndeployPhase = UndeployPhase.OFFLINE;
							}
						}
					}
				}

				final AbstractUndeplStatusSafeUndeplProcessor undeplStatusDeplProcessor = UndeplStatusSafeUndeplProcessorMapper
						.getInstance().map(item.getUndeployItemStatus());
				if (undeplStatusDeplProcessor == null) {
					// do nothing if the item has status different than the ones
					// mapped in the
					// class UndeplStatusSafeUndeplProcessorMapper
					continue;
				} else {
					undeplStatusDeplProcessor.process(item, undeploymentEnum,
							_undeploymentData, repo);
				}
			}
		} catch (EnumRuntimeException ere) {
			UndeploymentException ue = new UndeploymentException(
					"An error occurred while enumerating the undeploy components.",
					ere);
			ue.setMessageID("ASJ.dpl_dc.003207");
			throw ue;
		}
	}

	private Enumeration getUndeploymentEnumerator(
			UndeploymentData _undeploymentData,
			Enumeration admittedUndeplItemsEnum,
			OnlineOfflineSoftwareType onOffSoftwareType) {
		final ErrorStrategy errorStrategy = _undeploymentData
				.getUndeploymentErrorStrategy();
		final Set acceptedStatuses = new HashSet();
		acceptedStatuses.add(UndeployItemStatus.OFFLINE_ADMITTED);
		acceptedStatuses.add(UndeployItemStatus.WARNING);
		acceptedStatuses.add(UndeployItemStatus.OFFLINE_WARNING);
		acceptedStatuses.add(UndeployItemStatus.OFFLINE_SUCCESS);
		acceptedStatuses.add(UndeployItemStatus.SUCCESS);

		if (errorStrategy.equals(ErrorStrategy.ON_ERROR_SKIP_DEPENDING)) {
			return new PostUndeplEnumOnErrorSkipDep(admittedUndeplItemsEnum,
					acceptedStatuses, this.getStorageManager(),
					_undeploymentData.getSessionId(), onOffSoftwareType);
		} else {
			return new PostUndeplEnumOnErrorStop(admittedUndeplItemsEnum,
					acceptedStatuses, this.getStorageManager(),
					_undeploymentData.getSessionId(), onOffSoftwareType);
		}
	}

	private void doPostProcess(UndeploymentData _undeploymentData)
			throws UndeploymentException {
		final Collection undplItems = _undeploymentData
				.getSortedUndeploymentBatchItem();
		for (Iterator iter = undplItems.iterator(); iter.hasNext();) {
			final GenericUndeployItem undeployItem = (GenericUndeployItem) iter.next();

			if (UndeployItemStatus.OFFLINE_ADMITTED.equals(undeployItem
					.getUndeployItemStatus())) {
				DCLog
						.logInfo(location, 
								"ASJ.dpl_dc.002526",
								"Cluster will be restarted again due to 'offline' -> 'online components dependencies");
				restartServer( _undeploymentData.getSessionId() );
			}
		}
	}

	private void restartServer(String sessionId) throws UndeploymentException {
		DCLog.logInfo(location, "ASJ.dpl_dc.002527",
				"Restarting Application Server Java ...");

		final RestartServerService restartService = getRestartServerService();
		try {
			restartService.restartInSafeMode(LockAction.UNDEPLOY, false, sessionId);
		} catch (RestartServerServiceException rsse) {
			UndeploymentException ue = new UndeploymentException(
					"An error occurred while restarting the server.",
					rsse);
			ue.setMessageID("ASJ.dpl_dc.003208");
			throw ue;
		}
	}

	private synchronized RestartServerService getRestartServerService()
			throws UndeploymentException {
		final Server server = ServerFactory.getInstance().createServer();
		final ServerService serverService = server
				.getServerService(ServerFactory.getInstance()
						.createRestartServerRequest());

		if (serverService == null
				|| !(serverService instanceof RestartServerService)) {
			final String errMsg = "Received ServerService for restarting the server "
					+ "which is not of type RestartServerService.";
			UndeploymentException ue = new UndeploymentException(errMsg);
			ue.setMessageID("ASJ.dpl_dc.003209");
			throw ue;
		}

		return (RestartServerService) serverService;
	}

}
