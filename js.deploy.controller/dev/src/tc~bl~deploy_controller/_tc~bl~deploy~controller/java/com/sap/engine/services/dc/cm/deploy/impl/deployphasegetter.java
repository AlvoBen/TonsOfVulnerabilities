package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Set;

import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.server.Server;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.ServerService;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class DeployPhaseGetter {

	final PhaseGetter phaseGetter;

	static DeployPhaseGetter createInstance() throws DeploymentException {
		return new DeployPhaseGetter();
	}

	static DeployPhaseGetter createInstance(
			SoftwareTypeService softwareTypeService) throws DeploymentException {
		return new DeployPhaseGetter(softwareTypeService);
	}

	private DeployPhaseGetter() throws DeploymentException {
		this.phaseGetter = new PhaseGetter();
	}

	private DeployPhaseGetter(SoftwareTypeService softwareTypeService)
			throws DeploymentException {
		this.phaseGetter = new PhaseGetter(softwareTypeService);
	}

	DeployPhase getPhase(DeploymentBatchItem item) throws DeploymentException {
		item.accept(phaseGetter);
		phaseGetter.checkException();

		return phaseGetter.getDeploymentPhase();
	}

	private static class PhaseGetter implements DeploymentBatchItemVisitor {

		private DeployPhase deployPhase;
		private DeploymentException deploymentException;
		private final SoftwareTypeService softwareTypeService;

		private PhaseGetter() throws DeploymentException {
			softwareTypeService = getSoftwareTypeService();
		}

		private PhaseGetter(SoftwareTypeService softwareTypeService)
				throws DeploymentException {
			this.softwareTypeService = softwareTypeService;
		}

		DeployPhase getDeploymentPhase() {
			return deployPhase;
		}

		void checkException() throws DeploymentException {
			if (deploymentException != null) {
				throw deploymentException;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
		 */
		public void visit(DeploymentItem deploymentItem) {
			deploymentException = null;

			final Set offlineSoftwareTypes = softwareTypeService
					.getOfflineSoftwareTypes();
			final Set onlineSoftwareTypes = softwareTypeService
					.getOnlineSoftwareTypes();
			final Set postOnlineSoftwareTypes = softwareTypeService
					.getPostOnlineSoftwareTypes();

			if (postOnlineSoftwareTypes.contains(deploymentItem.getSda()
					.getSoftwareType())) {
				deployPhase = DeployPhase.POST_ONLINE;

			} else if (onlineSoftwareTypes.contains(deploymentItem.getSda()
					.getSoftwareType())) {
				deployPhase = DeployPhase.ONLINE;

			} else if (offlineSoftwareTypes.contains(deploymentItem.getSda()
					.getSoftwareType())) {
				deployPhase = DeployPhase.OFFLINE;

			} else {
				final String errMsg = "ASJ.dpl_dc.003055 An error occurred while getting the deploy phase. "
						+ "The specified deployment item has a Software Type ('"
						+ deploymentItem.getSda().getSoftwareType()
						+ "') which could not be "
						+ "recognized neither as 'online' nor as 'offline'.";
				deploymentException = new DeploymentException(errMsg);
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
		 * (com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem)
		 */
		public void visit(CompositeDeploymentItem deploymentItem) {
			deploymentException = null;

			deployPhase = DeployPhase.UNKNOWN;
		}

		private SoftwareTypeService getSoftwareTypeService()
				throws DeploymentException {
			final Server server = ServerFactory.getInstance().createServer();
			final ServerService serverService = server
					.getServerService(ServerFactory.getInstance()
							.createSoftwareTypeRequest());
			if (serverService == null
					|| !(serverService instanceof SoftwareTypeService)) {
				final String errMsg = "Received ServerService for determining online and "
						+ "offline software types that is no SoftwareTypeService.";
				DeploymentException de = new DeploymentException(errMsg);
				de.setMessageID("ASJ.dpl_dc.003056");
				throw de;
			}

			final SoftwareTypeService softwareTypeService = (SoftwareTypeService) serverService;
			return softwareTypeService;
		}

	}

}
