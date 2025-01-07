/* 
 * Created on Feb 21, 2007
 */
package com.sap.engine.services.dc.api.cmd.report;

import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.DeployResult;
import com.sap.engine.services.dc.api.deploy.DeployResultStatus;
import com.sap.engine.services.dc.api.event.DeploymentListener;

public interface DeployReporter {
	/**
	 * Returns listener to handle deployment events
	 * 
	 * @return
	 */
	public DeploymentListener getDeploymentListener();

	/**
	 * Interprets the deployment result in a way defined by the implementation
	 * 
	 * @param result
	 *            The result to process
	 */
	public void processDeployResult(DeployResult result)
			throws ReporterException;

	/**
	 * Interprets set of deployment items
	 * 
	 * @param items
	 */
	public void processDeployItems(DeployItem[] items,
			DeployResultStatus status, String statusDescription)
			throws ReporterException;
}
