package com.sap.engine.services.dc.cm.deploy.impl;

import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.deploy.CompositeDeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.ServerMode;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.repo.SoftwareType;

public class EngineMode2SWTypeVisitor extends ValidationVisitor {

	private final SoftwareTypeService softwareTypeService;
	private final ServerMode mode;

	EngineMode2SWTypeVisitor() {

		this.softwareTypeService = (SoftwareTypeService) ServerFactory
				.getInstance()
				.createServer()
				.getServerService(
						ServerFactory.getInstance().createSoftwareTypeRequest());
		this.mode = ServiceConfigurer.getInstance().getServerMode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.DeploymentBatchItemVisitor#visit
	 * (com.sap.engine.services.dc.cm.deploy.DeploymentItem)
	 */
	public void visit(DeploymentItem deploymentItem) {
		if (this.mode.equals(ServerMode.SAFE)) {
			// post online items cannot be deployed in safe mode
			SoftwareType type = deploymentItem.getSda().getSoftwareType();
			if (!softwareTypeService.getSafeModeSoftwareTypes().contains(type)) {
				deploymentItem
						.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
				deploymentItem.addDescription("Software type '" + type
						+ "' cannot be deployed in safe mode.");
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
		compositeDeploymentItem.startTimeStatEntry(
				"Check contained components vs engine mode",
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		final String tagName = "Check contained components vs engine mode:" + compositeDeploymentItem.getSdu().getId();
		Accounting.beginMeasure(tagName, EngineMode2SWTypeVisitor.class);
		try {
			final Collection deploymentItems = compositeDeploymentItem
					.getDeploymentItems();
			for (Iterator iter = deploymentItems.iterator(); iter.hasNext();) {
				final DeploymentItem deploymentItem = (DeploymentItem) iter
						.next();
				if (DeploymentStatus.ADMITTED.equals(deploymentItem
						.getDeploymentStatus())) {
					visit(deploymentItem);
				}

			}
			if (!isThereAvailabaleItems(compositeDeploymentItem)) {
				compositeDeploymentItem
						.setDeploymentStatus(DeploymentStatus.PREREQUISITE_VIOLATED);
			}

		} finally {
			Accounting.endMeasure(tagName);
			compositeDeploymentItem.finishTimeStatEntry();
		}
	}

}
