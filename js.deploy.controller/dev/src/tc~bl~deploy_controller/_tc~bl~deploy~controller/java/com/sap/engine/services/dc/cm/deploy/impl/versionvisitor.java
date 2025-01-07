package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.VersionStatus;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.RepositoryContainer;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.Version;
import com.sap.engine.services.dc.repo.VersionHelper;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 *<pre>
 * Title:        J2EE Deployment Team
 * Description:  The class compares the component which is visiting with the
 * one which has been set before the visit. The second one is usually a 
 * component loaded from the repository.
 * In order to set the version status of the component which is currently visiting,
 * the visitor compares the versions of the two components.
 * After setting the right version status of the currently visiting deployment item,
 * the visitor applies to visiting deployment item the version handling rule which 
 * have been set by the time of the visitor creation. 
 * The result from the version handling rule applying is that the component 
 * deployment status could be &quot;admitted&quot; or &quot;already deployed&quot;. 
 *          
 * For SCAs:
 * lower/lowerchanged     samelower            all
 * L    C    depl?        L    C    depl?      L    C    depl?
 * =    &lt;    no           =    &lt;    no         =    &lt;    yes
 * =    =    no           =    =    yes        =    =    yes
 * =    &gt;    yes          =    &gt;    yes        =    &gt;    yes
 * &lt;&gt;   &lt;    yes          &lt;&gt;   &lt;    yes        &lt;&gt;   &lt;    yes
 * &lt;&gt;   =    yes          &lt;&gt;   =    yes        &lt;&gt;   =    yes
 * &lt;&gt;   &gt;    yes          &lt;&gt;   &gt;    yes        &lt;&gt;   &gt;    yes
 * 
 * For SDAs contained in SCAs: 
 *    -If the containing SCA will not be deployed, the SDA will also not be deployed.
 *    -If the containing SCA will be deployed/updated:
 * lower                  samelower            all                  lowerchanged
 * L    C    depl?        L    C    depl?      L    C    depl?      L    C    depl?
 * =    &lt;    no*          =    &lt;    no*        =    &lt;    yes        =    &lt;    yes
 * =    =    no*          =    =    yes        =    =    yes        =    =    no
 * =    &gt;    yes          =    &gt;    yes        =    &gt;    yes        =    &gt;    yes
 * &lt;&gt;   &lt;    yes          &lt;&gt;   &lt;    yes        &lt;&gt;   &lt;    yes        &lt;&gt;   &lt;    yes
 * &lt;&gt;   =    yes          &lt;&gt;   =    yes        &lt;&gt;   =    yes        &lt;&gt;   =    yes
 * &lt;&gt;   &gt;    yes          &lt;&gt;   &gt;    yes        &lt;&gt;   &gt;    yes        &lt;&gt;   &gt;    yes
 *     *Note: If SCA has the SAME version as already deployed and some of its SDAs are not deployed the SCA will be admitted for deployment.
 *     
 * Single patches (SDAs not contained in SCAs) :
 * lower/lowerchanged     samelower            all
 * L    C    depl?        L    C    depl?      L    C    depl?
 * =    &lt;    no           =    &lt;    no         =    &lt;    yes
 * =    =    no           =    =    yes        =    =    yes
 * =    &gt;    yes          =    &gt;    yes        =    &gt;    yes
 * &lt;&gt;   &lt;    yes          &lt;&gt;   &lt;    yes        &lt;&gt;   &lt;    yes
 * &lt;&gt;   =    yes          &lt;&gt;   =    yes        &lt;&gt;   =    yes
 * &lt;&gt;   &gt;    yes          &lt;&gt;   &gt;    yes        &lt;&gt;   &gt;    yes
 * 
 * Legend: L - location; C - key counter; 
 * 
 * Copyright:    Copyright (c) 2003
 * Company:      SAP AG
 * Date:         2004-7-26
 * </pre>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 * @see com.sap.engine.services.dc.cm.deploy.VersionStatus
 * @see com.sap.engine.services.dc.cm.deploy.ComponentVersionHandlingRule
 * @see com.sap.engine.services.dc.cm.deploy.DeploymentStatus
 * 
 */
final class VersionVisitor extends ValidationVisitor {

	private static final Location location = 
		DCLog.getLocation(VersionVisitor.class);
	
	private final ComponentVersionHandlingRule componentVersionHandlingRule;
	private final VersionHelper versionHelper;
	private CrcCheckVisitor crcCheckVisitor;

	private Collection alreadyDeployedBatchItems = new ArrayList();
	private Collection crcAlreadyDeployedBatchItems = new ArrayList();

	VersionVisitor(ComponentVersionHandlingRule componentVersionHandlingRule) {
		this.componentVersionHandlingRule = componentVersionHandlingRule;
		this.versionHelper = RepositoryComponentsFactory.getInstance()
				.createVersionHelper();
		this.crcCheckVisitor = new CrcCheckVisitor(componentVersionHandlingRule);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
	 */
	public void visit(DeploymentItem deploymentItem) {
		compare(deploymentItem, deploymentItem.getOldSdu());

		if (DeploymentStatus.ALREADY_DEPLOYED.equals(deploymentItem
				.getDeploymentStatus())) {
			this.alreadyDeployedBatchItems.add(deploymentItem);
		}else if(DeploymentStatus.ADMITTED.equals(deploymentItem
				.getDeploymentStatus())){
			deploymentItem.accept(crcCheckVisitor);
			if (DeploymentStatus.ALREADY_DEPLOYED.equals(deploymentItem
					.getDeploymentStatus())) {
				this.crcAlreadyDeployedBatchItems.add(deploymentItem);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
	 */
	public void visit(CompositeDeploymentItem compositeDeploymentItem) {
		compositeDeploymentItem.startTimeStatEntry("Check composite version",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Check composite version:" + compositeDeploymentItem.getSdu().getId();
	    Accounting.beginMeasure( tagName, VersionVisitor.class );
		try {

			compare(compositeDeploymentItem, compositeDeploymentItem
					.getOldSdu());

			if (DeploymentStatus.ADMITTED.equals(compositeDeploymentItem
					.getDeploymentStatus())
					|| (DeploymentStatus.ALREADY_DEPLOYED
							.equals(compositeDeploymentItem
									.getDeploymentStatus()) && VersionStatus.SAME
							.equals(compositeDeploymentItem.getVersionStatus()))) {

				compositeDeploymentItem.startTimeStatEntry(
						"Check contained SDAs versions",
						TimeStatisticsEntry.ENTRY_TYPE_OTHER);
				try {
					boolean isThereAdmittedItems = false;

					final Collection deploymentItems = compositeDeploymentItem
							.getDeploymentItems();
					for (Iterator iter = deploymentItems.iterator(); iter
							.hasNext();) {
						final DeploymentItem deploymentItem = (DeploymentItem) iter
								.next();

						compare(deploymentItem, RepositoryContainer
								.getDeploymentsContainer().getDeployment(
										deploymentItem.getSda().getId()));

						if (DeploymentStatus.ADMITTED
										.equals(deploymentItem
												.getDeploymentStatus())) {
							deploymentItem.accept(crcCheckVisitor);
						}
						
						if (!isThereAdmittedItems
								&& DeploymentStatus.ADMITTED
										.equals(deploymentItem
												.getDeploymentStatus())) {
							isThereAdmittedItems = true;
						}

					}

					if (DeploymentStatus.ALREADY_DEPLOYED
							.equals(compositeDeploymentItem
									.getDeploymentStatus())) {
						if (!isThereAdmittedItems) {
							this.alreadyDeployedBatchItems
									.add(compositeDeploymentItem);
						} else {
							compositeDeploymentItem
									.setDeploymentStatus(DeploymentStatus.ADMITTED);
						}
					}

					if (DeploymentStatus.ADMITTED
							.equals(compositeDeploymentItem
									.getDeploymentStatus())) {
						if (!isThereAvailabaleItems(compositeDeploymentItem)) {
							compositeDeploymentItem
									.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
						}
					}
				} finally {
					compositeDeploymentItem.finishTimeStatEntry();
				}
			} else if (DeploymentStatus.ALREADY_DEPLOYED
					.equals(compositeDeploymentItem.getDeploymentStatus())) {
				this.alreadyDeployedBatchItems.add(compositeDeploymentItem);
				final Collection deploymentItems = compositeDeploymentItem
						.getDeploymentItems();
				for (Iterator iter = deploymentItems.iterator(); iter.hasNext();) {
					final DeploymentItem deploymentItem = (DeploymentItem) iter
							.next();
					deploymentItem
							.setDeploymentStatus(DeploymentStatus.ALREADY_DEPLOYED);
					deploymentItem
							.setDescription("Containing SCA has already deployed because its version is newer that deployed one.");
				}
			}
		} finally {
			Accounting.endMeasure(tagName);
			compositeDeploymentItem.finishTimeStatEntry();
		}
	}

	private void compare(DeploymentBatchItem item, Sdu compareSdu) {
		if (DeploymentStatus.FILTERED.equals(item.getDeploymentStatus())) {
			return;
		}
		item.startTimeStatEntry("Check version:" + item.getSdu().getId(),
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Check version:" + item.getSdu().getId();
	    Accounting.beginMeasure( tagName, VersionVisitor.class );
		try {
			initDeploymentItemVersionStatus(item, compareSdu);

			applyVersionHandlingRule(item, compareSdu);
		} finally {
			Accounting.endMeasure(tagName);
			item.finishTimeStatEntry();
		}
	}

	private void initDeploymentItemVersionStatus(
			DeploymentBatchItem deploymentBatchItem, Sdu compareSdu) {
		if (compareSdu == null) {
			deploymentBatchItem.setVersionStatus(VersionStatus.NEW);
		} else {
			final Sdu batchSdu = deploymentBatchItem.getSdu();
			final Version comparableVersion = compareSdu.getVersion();
			final Version batchCompVersion = batchSdu.getVersion();
			if (this.versionHelper.isLower(comparableVersion, batchCompVersion)) {
				deploymentBatchItem.setVersionStatus(VersionStatus.HIGHER);
			} else if (this.versionHelper.isEquivalent(comparableVersion,
					batchCompVersion)) {
				deploymentBatchItem.setVersionStatus(VersionStatus.SAME);
			} else {
				deploymentBatchItem.setVersionStatus(VersionStatus.LOWER);
			}
		}
	}

	private static void setItemAsAlreadyDeployed(
			DeploymentBatchItem deploymentBatchItem, Sdu compareSdu) {
		deploymentBatchItem
				.setDeploymentStatus(DeploymentStatus.ALREADY_DEPLOYED);
		StringBuffer descr = new StringBuffer(100);
		descr.append("Already deployed component has version:").append(
				compareSdu.getVersion());
		deploymentBatchItem.addDescription(descr.toString());
		if (location.beDebug()) {
			traceDebug(location, 						"Due to Version check deployment status of deployment item [{0}] is set to [{1}].",
						new Object[] { deploymentBatchItem.getBatchItemId(),
								DeploymentStatus.ALREADY_DEPLOYED });
		}
	}

	private void applyVersionHandlingRule(
			DeploymentBatchItem deploymentBatchItem, Sdu compareSdu) {
		if (!deploymentBatchItem.getVersionStatus().equals(VersionStatus.NEW)) {
			final String comparableComponentLocation = compareSdu.getLocation();
			final String deploymentItemLocation = deploymentBatchItem.getSdu()
					.getLocation();
			if (!deploymentItemLocation
					.equalsIgnoreCase(comparableComponentLocation)) {
				// if the SL locations are different then the versions have not
				// to be compared
				// the versions are local for a concrete location
				if (location.beDebug()) {
					traceDebug(location, 
								"Due to Version check deployment status of deployment item [{0}] is set to [{1}] because its location [{2}] differs from the location [{3}] of the already deployed item.",
								new Object[] {
										deploymentBatchItem.getBatchItemId(),
										DeploymentStatus.ADMITTED,
										deploymentItemLocation,
										comparableComponentLocation });
				}
				deploymentBatchItem
						.setDeploymentStatus(DeploymentStatus.ADMITTED);
				return;
			}
		} else {
			// if the deployment item status is NEW, then there is no need any
			// kind
			// of version check to be done
			deploymentBatchItem.setDeploymentStatus(DeploymentStatus.ADMITTED);
			return;
		}

		if (this.componentVersionHandlingRule
				.equals(ComponentVersionHandlingRule.UPDATE_LOWER_VERSIONS_ONLY)) {
			if ((!deploymentBatchItem.getVersionStatus().equals(
					VersionStatus.HIGHER))
					&& (!deploymentBatchItem.getVersionStatus().equals(
							VersionStatus.NEW))) {
				setItemAsAlreadyDeployed(deploymentBatchItem, compareSdu);
			} else {
				deploymentBatchItem
						.setDeploymentStatus(DeploymentStatus.ADMITTED);
			}
		} else if (this.componentVersionHandlingRule
				.equals(ComponentVersionHandlingRule.UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY)) {
			if ((deploymentBatchItem instanceof DeploymentItem)
					&& ((DeploymentItem) deploymentBatchItem).getParentId() != null) {
				if ((!deploymentBatchItem.getVersionStatus().equals(
						VersionStatus.HIGHER))
						&& (!deploymentBatchItem.getVersionStatus().equals(
								VersionStatus.LOWER))
						&& (!deploymentBatchItem.getVersionStatus().equals(
								VersionStatus.NEW))) {
					setItemAsAlreadyDeployed(deploymentBatchItem, compareSdu);
				} else {
					deploymentBatchItem
							.setDeploymentStatus(DeploymentStatus.ADMITTED);
				}
			} else {
				if ((!deploymentBatchItem.getVersionStatus().equals(
						VersionStatus.HIGHER))
						&& (!deploymentBatchItem.getVersionStatus().equals(
								VersionStatus.NEW))) {
					setItemAsAlreadyDeployed(deploymentBatchItem, compareSdu);
				} else {
					deploymentBatchItem
							.setDeploymentStatus(DeploymentStatus.ADMITTED);
				}
			}
		} else if (this.componentVersionHandlingRule
				.equals(ComponentVersionHandlingRule.UPDATE_SAME_AND_LOWER_VERSIONS_ONLY)) {
			if (deploymentBatchItem.getVersionStatus().equals(
					VersionStatus.LOWER)) {
				setItemAsAlreadyDeployed(deploymentBatchItem, compareSdu);
			} else {
				deploymentBatchItem
						.setDeploymentStatus(DeploymentStatus.ADMITTED);
			}
		} else if (this.componentVersionHandlingRule
				.equals(ComponentVersionHandlingRule.UPDATE_ALL_VERSIONS)) {
			deploymentBatchItem.setDeploymentStatus(DeploymentStatus.ADMITTED);
		} else {
			throw new RuntimeException(
					"ASJ.dpl_dc.003098 The specified component version rule '"
							+ this.componentVersionHandlingRule
							+ "' is not supported!");
		}
	}

	public int getAlreadyDeployedBatchItemsSize() {
		return this.alreadyDeployedBatchItems.size();
	}
	
	public int getCRCAlreadyDeployedBatchItemsSize() {
		return this.crcAlreadyDeployedBatchItems.size();
	}

}
