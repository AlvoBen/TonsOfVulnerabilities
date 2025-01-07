package com.sap.engine.services.dc.lcm.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import com.sap.engine.services.dc.lcm.LCMException;
import com.sap.engine.services.dc.lcm.LCMResult;
import com.sap.engine.services.dc.lcm.LCMResultStatus;
import com.sap.engine.services.dc.lcm.LCMStatus;
import com.sap.engine.services.dc.lcm.LCMStatusDetails;
import com.sap.engine.services.dc.lcm.LifeCycleManagerFactory;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.util.Constants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.engine.services.dc.util.logging.DCLogResourceAccessor;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.engine.services.deploy.container.op.util.StatusDescription;
import com.sap.engine.services.deploy.container.op.util.StatusFlagsEnum;
import com.sap.engine.services.deploy.exceptions.DisabledApplicationException;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-29
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class J2EELCMProcessor extends AbstractLCMProcessor {
	
	private Location location = DCLog.getLocation(this.getClass());

	private static final Map STATUSES_MAP = new HashMap();

	private static final Map STATUS_DETAILS_MAP = new HashMap();

	private final DeployService deployService;

	private static J2EELCMProcessor INSTANCE;

	static {

		STATUSES_MAP.put(DeployService.IMPLICIT_STOPPED_APP_STATUS, LCMStatus.IMPLICIT_STOPPED);
		STATUSES_MAP.put(DeployService.STARTED_APP_STATUS, LCMStatus.STARTED);
		STATUSES_MAP.put(DeployService.STARTING_APP_STATUS, LCMStatus.STARTING);
		STATUSES_MAP.put(DeployService.STOPPED_APP_STATUS, LCMStatus.STOPPED);
		STATUSES_MAP.put(DeployService.STOPPING_APP_STATUS, LCMStatus.STOPPING);
		STATUSES_MAP.put(DeployService.UNKNOWN_APP_STATUS, LCMStatus.UNKNOWN);
		STATUSES_MAP.put(DeployService.UPGRADING_APP_STATUS, LCMStatus.UPGRADING);
		STATUSES_MAP.put(DeployService.MARKED_FOR_REMOVAL, LCMStatus.MARKED_FOR_REMOVAL);

		STATUS_DETAILS_MAP.put(StatusFlagsEnum.NO_STOPPED_FLAG, LCMStatusDetails.NO_STOPPED_FLAG);
		STATUS_DETAILS_MAP.put(StatusFlagsEnum.STOPPED_ON_ERROR, LCMStatusDetails.STOPPED_ON_ERROR);
		STATUS_DETAILS_MAP.put(StatusFlagsEnum.STOPPED_OK, LCMStatusDetails.STOPPED_OK);
		STATUS_DETAILS_MAP.put(StatusFlagsEnum.IMPLICIT_STOPPED_ON_ERROR, LCMStatusDetails.IMPLICIT_STOPPED_ON_ERROR);
		STATUS_DETAILS_MAP.put(StatusFlagsEnum.IMPLICIT_STOPPED_OK, LCMStatusDetails.IMPLICIT_STOPPED_OK);

	}

	static J2EELCMProcessor getInstance() throws LCMException {
		if (INSTANCE == null) {
			INSTANCE = new J2EELCMProcessor();
		}

		return INSTANCE;
	}

	private J2EELCMProcessor() throws LCMException {
		this.deployService = getDeployService();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.impl.LifeCycleManagerImpl#doStart(com.
	 * sap.engine.services.dc.repo.Sda)
	 */
	protected LCMResult doStart(Sda sda) {
		if (location.bePath()) {
			tracePath(location, 
					"Performing Java EE start operation for development component [{0}]",
					new Object[] { sda });
		}

		final String dsCompId = getCompId(sda);
		// try {
		// final String status =
		// this.deployService.getApplicationStatus(dsCompId);
		// }
		// catch (Exception e) {
		// throw new
		//LCMException("The system not get the current status for the component "
		// + dsCompId, e);
		// }

		try {
			this.deployService.startApplicationAndWait(dsCompId);

			if (location.bePath()) {
				tracePath(location, 
						"Java EE start of development component [{0}] finished successfully",
						new Object[] { dsCompId });
			}

			return LifeCycleManagerFactory.getInstance().createLCMResult(
					LCMResultStatus.SUCCESS,
					"The component " + dsCompId + "was started successfully.");
		} catch (WarningException we) {
			DCLog
					.logInfo(location, 
							"ASJ.dpl_dc.005609",
							"Warning exception was returned while the [{0}] was starting. Warnings:[{1}][{2}]",
							new Object[] { dsCompId, Constants.EOL,
									this.concatStrings(we.getWarnings()) });

			return LifeCycleManagerFactory
					.getInstance()
					.createLCMResult(
							LCMResultStatus.WARNING,
							DCLogResourceAccessor
									.getInstance()
									.getMessageText(
											DCLogConstants.LCM_WARNING_EXC_IS_RETURNED_WHILE_STARTING,
											new Object[] {
													dsCompId,
													Constants.EOL,
													this.concatStrings(we
															.getWarnings()) }));
		} catch (Exception e) {
			if (e.getCause() instanceof DisabledApplicationException) {
				DCLog
						.logInfo(location, 
								"ASJ.dpl_dc.005607",
								"The [{0}] cannot be started, because it is 'disabled' in the zero admin template",
								new Object[] { dsCompId });

				return LifeCycleManagerFactory
						.getInstance()
						.createLCMResult(
								LCMResultStatus.ERROR,
								DCLogResourceAccessor
										.getInstance()
										.getMessageText(
												DCLogConstants.LCM_DISABLED_EXCEPTION_IS_RETURNED_WHILE_STARTING,
												new Object[] { dsCompId }));
			} else {
				DCLog
						.logInfo(location, 
								"ASJ.dpl_dc.005608",
								"Exception was returned while the [{0}] was starting. Warning/Exception :[{1}][{2}]",
								new Object[] { dsCompId, Constants.EOL,
										e.getMessage() });

				return LifeCycleManagerFactory
						.getInstance()
						.createLCMResult(
								LCMResultStatus.ERROR,
								DCLogResourceAccessor
										.getInstance()
										.getMessageText(
												DCLogConstants.LCM_EXCEPTION_IS_RETURNED_WHILE_STARTING,
												new Object[] { dsCompId,
														Constants.EOL,
														e.getMessage() }));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.impl.LifeCycleManagerImpl#doStop(com.sap
	 * .engine.services.dc.repo.Sda)
	 */
	protected LCMResult doStop(Sda sda) throws LCMException {
		if (location.bePath()) {
			tracePath(location, 
					"Performing Java EE stop operation for development component [{0}]",
					new Object[] { sda });
		}

		final String dsCompId = getCompId(sda);
		try {
			this.deployService.stopApplicationAndWait(dsCompId);

			return LifeCycleManagerFactory.getInstance().createLCMResult(
					LCMResultStatus.SUCCESS,
					"The component " + dsCompId + "was stopped successfully.");
		} catch (WarningException we) {
			return LifeCycleManagerFactory.getInstance().createLCMResult(
					LCMResultStatus.WARNING,
					"Warnings:\n" + this.concatStrings(we.getWarnings()));
		} catch (Exception e) {
			DCLog
					.logInfo(location, 
							"ASJ.dpl_dc.005611",
							"Exception was returned while the [{0}] was stopping. Warning/Exception :[{1}][{2}]",
							new Object[] { dsCompId, Constants.EOL,
									e.getMessage() });

			return LifeCycleManagerFactory
					.getInstance()
					.createLCMResult(
							LCMResultStatus.ERROR,
							DCLogResourceAccessor
									.getInstance()
									.getMessageText(
											DCLogConstants.LCM_EXCEPTION_IS_RETURNED_WHILE_STOPPING,
											new Object[] { dsCompId,
													Constants.EOL,
													e.getMessage() }));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.lcm.impl.AbstractLCMProcessor#getLCMStatus
	 * (com.sap.engine.services.dc.repo.Sda)
	 */
	protected LCMStatus getLCMStatus(Sda sda) throws LCMException {
		if (location.bePath()) {
			tracePath(location, 
					"Performing Java EE get status operation for development component [{0}]",
					new Object[] { sda });
		}

		final String dsCompId = getCompId(sda);

		try {
			final String status = this.deployService
					.getApplicationStatus(dsCompId);
			final StatusDescription dsStatusDescription = this.deployService
					.getApplicationStatusDescription(dsCompId);
			if (location.beDebug()) {
				traceDebug(location,
						"Java EE status is [{0}]",
						new Object[] { status });
			}

			final LCMStatus lcmStatus = (LCMStatus) STATUSES_MAP.get(status);
			final LCMStatusDetails lcmStatusDetails = (LCMStatusDetails) STATUS_DETAILS_MAP
					.get(dsStatusDescription.getStatusFlag());
			if (lcmStatus == null) {
				return LCMStatus.UNKNOWN;
			}
			if (lcmStatusDetails != null) {
				lcmStatusDetails.setDescription(dsStatusDescription
						.getDescription());
				lcmStatus.setLCMStatusDetails(lcmStatusDetails);
			}

			return lcmStatus;
		} catch (Exception e) {
			throw new LCMException(
					"ASJ.dpl_dc.003317 The system not get the status for the component "
							+ dsCompId, e);
		}
	}

	private DeployService getDeployService() throws LCMException {
		try {
			return ServiceConfigurer.getInstance().getDeployService();
		} catch (NamingException ne) {
			throw new LCMException(
					"ASJ.dpl_dc.003318 An error occurred while getting the Deploy Service from the LCM.",
					ne);
		}
	}

	protected String concatStrings(String[] arr) {
		final StringBuffer result = new StringBuffer("");
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				result.append(arr[i]).append("\n");
			}
		}

		return result.toString();
	}

}
