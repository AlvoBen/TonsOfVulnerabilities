package com.sap.engine.services.dc.repo.impl;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.frame.core.configuration.ChangeEvent;
import com.sap.engine.frame.core.configuration.ConfigurationChangedListener;
import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.repo.Repository;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.RepositoryFactory;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduRepoLocation;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.engine.services.dc.util.logging.DCLogResourceAccessor;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-19
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
abstract class AbstractRepoCfgListener implements ConfigurationChangedListener {
	
	private Location location = DCLog.getLocation(this.getClass());

	AbstractRepoCfgListener() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.frame.core.configuration.ConfigurationChangedListener#
	 * configurationChanged(com.sap.engine.frame.core.configuration.ChangeEvent)
	 */
	public void configurationChanged(ChangeEvent changeEvent) {
		final ChangeEvent[] detailedChangeEvents = changeEvent
				.getDetailedChangeEvents();
		if (detailedChangeEvents != null && detailedChangeEvents.length > 0) {
			final Set processedAdmittedPaths = new HashSet();

			for (int i = 0; i < detailedChangeEvents.length; i++) {
				final String admittedPath = getCfgEventPathAdmitted(detailedChangeEvents[i]
						.getPath());
				if (admittedPath == null
						|| processedAdmittedPaths.contains(admittedPath)) {
					continue;
				}

				processedAdmittedPaths.add(admittedPath);

				final int eventAction = detailedChangeEvents[i].getAction();
				switch (eventAction) {
				case ChangeEvent.ACTION_CREATED:
					createdAction(admittedPath);
					break;

				case ChangeEvent.ACTION_MODIFIED:
					modifiedAction(admittedPath);
					break;

				case ChangeEvent.ACTION_DELETED:
					deletedAction(admittedPath);
					break;

				default:
					break;
				}
			}
		}
	}

	protected abstract SduRepoLocation getSduRepoLocation(
			String admittedEventPath);

	protected abstract String getRepoSduRoot();

	private void deletedAction(String admittedEventPath) {
		final Sdu sdu = getDeploymentsContainerSdu(admittedEventPath);
		if (sdu != null) {
			RepositoryContainer.getDeploymentsContainer().removeDeployment(sdu);
		}
	}

	private void modifiedAction(String admittedEventPath) {
		final SduRepoLocation sdu_repo_location = getSduRepoLocation(admittedEventPath);

		final Repository repo = RepositoryFactory.getInstance()
				.createRepository();
		final Sdu sdu;
		try {
			sdu = repo.loadSdu(sdu_repo_location);
		} catch (RepositoryException re) {
			final String errMsg = "ASJ.dpl_dc.003340 An error occurred while loading the SDU corresponding to the "
					+ "repository location '"
					+ sdu_repo_location
					+ "'. The impact is that the just modified "
					+ "SDU will not be registered into the runtime Repository Container.";
			DCLog.logErrorThrowable(location, null, errMsg, re);
			throw new IllegalStateException(errMsg);
		}

		if (sdu == null) {
			DCLog
					.logError(location, 
							"ASJ.dpl_dc.005003",
							"No information about SDU corresponding to repository location [{0}]. The result is that the just modified SDU will not be registered into the runtime Repository Container.",
							new Object[] { sdu_repo_location });
			throw new IllegalStateException(
					DCLogResourceAccessor
							.getInstance()
							.getMessageText(
									DCLogConstants.NO_INFO_ABOUT_SDU_FOR_LOCATION__IMPACT_MODIFIED,
									new Object[] { sdu_repo_location }));
		}

		RepositoryContainer.getDeploymentsContainer().modifyDeployment(sdu);
	}

	private void createdAction(String admittedEventPath) {
		final SduRepoLocation sdu_repo_location = getSduRepoLocation(admittedEventPath);
		Sdu sdu = RepositoryContainer.getDeploymentsContainer().getDeployment(
				sdu_repo_location);
		if (sdu != null) {
			RepositoryContainer.getDeploymentsContainer().removeDeployment(sdu);
		}
		final Repository repo = RepositoryFactory.getInstance()
				.createRepository();
		try {
			sdu = repo.loadSdu(sdu_repo_location);
		} catch (RepositoryException re) {
			final String errMsg = "ASJ.dpl_dc.003342 An error occurred while loading the SDU corresponding to the "
					+ "repository location '"
					+ sdu_repo_location
					+ "'. The impact is that the just added "
					+ "SDU will not be registered into the runtime Repository Container.";
			DCLog.logErrorThrowable(location, null, errMsg, re);
			throw new IllegalStateException(errMsg);
		}

		if (sdu == null) {
			DCLog
					.logError(location, 
							"ASJ.dpl_dc.005004",
							"[o information about SDU corresponding to repository location [{0}]. The result is that the just added SDU will not be registered into the runtime Repository Container.",
							new Object[] { sdu_repo_location });
			throw new IllegalStateException(
					DCLogResourceAccessor
							.getInstance()
							.getMessageText(
									DCLogConstants.NO_INFO_ABOUT_SDU_FOR_LOCATION__IMPACT_ADD,
									new Object[] { sdu_repo_location }));
		}

		RepositoryContainer.getDeploymentsContainer().addDeployment(sdu);
	}

	private Sdu getDeploymentsContainerSdu(String admittedEventPath) {
		final SduRepoLocation location = getSduRepoLocation(admittedEventPath);

		return RepositoryContainer.getDeploymentsContainer().getDeployment(
				location);
	}

	/**
	 * Gets the path which is admitted for change (add, remove or modify), if
	 * there is.
	 * 
	 * @param cfgEventPath
	 *            the full path which cpecifies which cfg has been changed.
	 * @return <code>String</code> which is the admitted for change and is part
	 *         of the specified or <code>cfgEventPath</code>. <code>null</code>
	 *         if the specified configuration event path could not be admitted
	 *         for change.
	 */
	private String getCfgEventPathAdmitted(String cfgEventPath) {
		if (getRepoSduRoot().equals(cfgEventPath)) {
			return null;
		}

		final String repoSduRoot = getRepoSduRoot()
				+ LocationConstants.PATH_SEPARATOR;
		if (!cfgEventPath.startsWith(repoSduRoot)) {
			return null;
		}

		final int idx = cfgEventPath.indexOf(LocationConstants.PATH_SEPARATOR,
				repoSduRoot.length());
		if (idx == -1) {
			return repoSduRoot + cfgEventPath.substring(repoSduRoot.length());
		} else {
			return repoSduRoot
					+ cfgEventPath.substring(repoSduRoot.length(), idx);
		}
	}

}
