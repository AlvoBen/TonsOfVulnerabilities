package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentData;
import com.sap.engine.services.dc.cm.deploy.DeploymentException;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.tc.logging.Location;

public class WarningSafeDeplProcessor extends
		AbstractDeplStatusSafeDeplProcessor {

	private  final Location location = DCLog.getLocation(this.getClass());
	
	WarningSafeDeplProcessor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.deploy.impl.AbstractDeplStatusSafeDeplProcessor
	 * #process(com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem,
	 * java.util.Enumeration,
	 * com.sap.engine.services.dc.cm.deploy.DeploymentData,
	 * com.sap.engine.services.dc.repo.Repository)
	 */
	void process(final DeploymentBatchItem item,
			final DeploymentData deploymentData) throws DeploymentException {
		if (item.getSdu() == null) {
			DeploymentException de = new DeploymentException(
					"The safe mode deployment registration "
							+ "could not be performed for the component '"
							+ item
							+ "'. The deployment does not contain SDU. Solution: Please, perform "
							+ "the deployment process again.");
			de.setMessageID("ASJ.dpl_dc.003099");
			throw de;
		}

		if (location.bePath()) {
			tracePath(location,
					"Registering offline deployed component [{0}]",
					new Object[] { item });
		}
		
		notifyObservers(item, deploymentData);
		
		if (location.bePath()) {
			tracePath(location, "Component [{0}] has been registered",
					new Object[] { item.getBatchItemId() });
		}

	}

}
