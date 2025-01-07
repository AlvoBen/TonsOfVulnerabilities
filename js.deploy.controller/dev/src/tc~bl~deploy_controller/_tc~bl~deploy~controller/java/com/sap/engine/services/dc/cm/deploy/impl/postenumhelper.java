package com.sap.engine.services.dc.cm.deploy.impl;

import com.sap.engine.services.dc.util.logging.DCLog;

import java.util.Set;

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.server.Server;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.OnlineOfflineSoftwareType;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.repo.SoftwareType;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-4-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
final class PostEnumHelper {

	private static SoftwareTypeService softwareTypeService;

	private static final Location location = 
		DCLog.getLocation(SoftwareTypeService.class);
	
	private PostEnumHelper() {
	}

	static boolean isNext(DeploymentBatchItem deplBatchItem,
			OnlineOfflineSoftwareType onOffSoftwareType)
			throws EnumRuntimeException {
		final EnumRulesChecker enumRulesChecker = new EnumRulesChecker();
		enumRulesChecker.setOnlineOfflineSoftwareType(onOffSoftwareType);
		deplBatchItem.accept(enumRulesChecker);

		if (!enumRulesChecker.isSoftwareTypeAdmitted()) {
			if (location.beDebug()) {
				DCLog.traceDebug(location, 
						"The item [{0}] is not admitted for processing. Only items with software types corresponding to [{1}] are admitted.",
						new Object[] { deplBatchItem, onOffSoftwareType });
			}
			return false;
		}

		if (!enumRulesChecker.isDeploymentStatusAdmitted()) {
			if (location.beDebug()) {
				DCLog.traceDebug(location, 
						"The item [{0}] is not admitted for processing in this [{1}] step, because of its deployment status [{2}].",
						new Object[] { deplBatchItem.getBatchItemId(),
								onOffSoftwareType,
								deplBatchItem.getDeploymentStatus() });
			}
			return false;
		}

		if (!enumRulesChecker.isComponentTypeAdmitted()) {
			if (location.beDebug()) {
				DCLog.traceDebug(location, 
						"The item [{0}] is not admitted for processing in this [{1}] step, because of its component type (SC or DC).",
						new Object[] { deplBatchItem.getBatchItemId(),
								onOffSoftwareType });
			}
			return false;
		}

		return true;
	}

	private static synchronized SoftwareTypeService getSoftwareTypeService() {
		if (softwareTypeService == null) {
			final Server server = ServerFactory.getInstance().createServer();
			softwareTypeService = (SoftwareTypeService) server
					.getServerService(ServerFactory.getInstance()
							.createSoftwareTypeRequest());
		}

		return softwareTypeService;
	}

	private static class EnumRulesChecker implements DeploymentBatchItemVisitor {

		private boolean softwareTypeAdmitted = false;
		private OnlineOfflineSoftwareType onOffSoftwareType;

		private boolean deploymentStatusAdmitted;
		// private boolean onlineDeploymentStarted;
		// private boolean offlineDeploymentStarted;

		private boolean componentTypeAdmitted;

		EnumRulesChecker() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
		 */
		public void visit(DeploymentItem deploymentItem) {
			checkSoftwareType(deploymentItem);
			checkDeploymentStatus(deploymentItem);
			componentTypeAdmitted = true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
		 */
		public void visit(CompositeDeploymentItem deploymentItem) {
			softwareTypeAdmitted = true;
			checkDeploymentStatus(deploymentItem); // deploymentStatusAdmitted =
			// true;

			if (OnlineOfflineSoftwareType.OFFLINE.equals(onOffSoftwareType)) {
				componentTypeAdmitted = false;
			} else {
				componentTypeAdmitted = true;
			}
		}

		void setOnlineOfflineSoftwareType(
				OnlineOfflineSoftwareType _onOffSoftwareType) {
			onOffSoftwareType = _onOffSoftwareType;
		}

		boolean isSoftwareTypeAdmitted() {
			return softwareTypeAdmitted;
		}

		boolean isDeploymentStatusAdmitted() {
			return deploymentStatusAdmitted;
		}

		boolean isComponentTypeAdmitted() {
			return componentTypeAdmitted;
		}

		private void checkSoftwareType(DeploymentItem deploymentItem) {
			final SoftwareType swtType = deploymentItem.getSda()
					.getSoftwareType();
			if (OnlineOfflineSoftwareType.OFFLINE.equals(onOffSoftwareType)) {
				final Set admittedSwtTypes = getSoftwareTypeService()
						.getOfflineSoftwareTypes();
				softwareTypeAdmitted = admittedSwtTypes.contains(swtType);
			} else if (OnlineOfflineSoftwareType.ONLINE
					.equals(onOffSoftwareType)) {
				final Set admittedSwtTypes = getSoftwareTypeService()
						.getOnlineSoftwareTypes();
				softwareTypeAdmitted = admittedSwtTypes.contains(swtType);
			} else if (OnlineOfflineSoftwareType.POST_ONLINE
					.equals(onOffSoftwareType)) {
				final Set admittedSwtTypes = getSoftwareTypeService()
						.getPostOnlineSoftwareTypes();
				softwareTypeAdmitted = admittedSwtTypes.contains(swtType);
			} else {
				throw new IllegalStateException(
						"The software type: "
								+ swtType
								+ " does not belong to either offline, online or post online types.");
			}
		}

		private void checkDeploymentStatus(CompositeDeploymentItem item) {
			// during this phase we have to perform the deployment of the SCAs
			// from the offline phase and
			// and the ones from the online phase
			if (OnlineOfflineSoftwareType.ONLINE.equals(onOffSoftwareType)
					|| OnlineOfflineSoftwareType.POST_ONLINE
							.equals(onOffSoftwareType)) {

				if ((DeploymentStatus.ADMITTED
						.equals(item.getDeploymentStatus())
						|| DeploymentStatus.OFFLINE_ADMITTED.equals(item
								.getDeploymentStatus())
						|| DeploymentStatus.OFFLINE_ABORTED.equals(item
								.getDeploymentStatus())
						|| DeploymentStatus.OFFLINE_WARNING.equals(item
								.getDeploymentStatus())
						|| DeploymentStatus.OFFLINE_SUCCESS.equals(item
								.getDeploymentStatus())) 
					&& (!item.getAdmittedDeploymentItems().hasMoreElements())) {
					deploymentStatusAdmitted = true;
				} else {
					deploymentStatusAdmitted = false;
				}

			} else {
				deploymentStatusAdmitted = false;
			}
		}

		private void checkDeploymentStatus(DeploymentItem deploymentItem) {
			if (OnlineOfflineSoftwareType.OFFLINE.equals(onOffSoftwareType)) {
				if (DeploymentStatus.OFFLINE_SUCCESS.equals(deploymentItem
						.getDeploymentStatus())
						|| DeploymentStatus.OFFLINE_WARNING
								.equals(deploymentItem.getDeploymentStatus())
						|| DeploymentStatus.OFFLINE_ABORTED
								.equals(deploymentItem.getDeploymentStatus())) {
					deploymentStatusAdmitted = true;
				} else {
					deploymentStatusAdmitted = false;
				}
			} else {
				// if ( DeploymentStatus.OFFLINE_ADMITTED.equals(
				// deploymentItem.getDeploymentStatus() ) ) {
				// if ( !onlineDeploymentStarted ) {
				// deploymentStatusAdmitted = false;
				// }
				// else {
				// // the system admits "offline" deployments on this part of
				// the logic
				// // only after "online" ones have been done
				// deploymentStatusAdmitted = true;
				// offlineDeploymentStarted = true;
				//
				// // deploymentStatusAdmitted = false;
				// }
				// }
				// else if ( DeploymentStatus.ADMITTED.equals(
				// deploymentItem.getDeploymentStatus() ) ) {
				// onlineDeploymentStarted = true;
				// deploymentStatusAdmitted = true;
				// }
				if (DeploymentStatus.ADMITTED.equals(deploymentItem
						.getDeploymentStatus())
						|| DeploymentStatus.OFFLINE_ADMITTED
								.equals(deploymentItem.getDeploymentStatus())) {
					deploymentStatusAdmitted = true;
				} else {
					deploymentStatusAdmitted = false;
				}
			}
		}

	}

}
