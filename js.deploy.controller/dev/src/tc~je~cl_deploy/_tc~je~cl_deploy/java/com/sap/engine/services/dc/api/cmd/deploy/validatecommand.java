/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Oct 17, 2005
 */
package com.sap.engine.services.dc.api.cmd.deploy;

import java.util.Iterator;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.ComponentManager;
import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.ErrorStrategyAction;
import com.sap.engine.services.dc.api.cmd.Command;
import com.sap.engine.services.dc.api.deploy.DeployException;
import com.sap.engine.services.dc.api.deploy.DeployItem;
import com.sap.engine.services.dc.api.deploy.DeployProcessor;
import com.sap.engine.services.dc.api.deploy.DeployWorkflowStrategy;
import com.sap.engine.services.dc.api.deploy.EngineTimeoutException;
import com.sap.engine.services.dc.api.deploy.LifeCycleDeployStrategy;
import com.sap.engine.services.dc.api.deploy.TransportException;
import com.sap.engine.services.dc.api.deploy.ValidationException;
import com.sap.engine.services.dc.api.deploy.ValidationResult;
import com.sap.engine.services.dc.api.deploy.ValidationStatus;
import com.sap.engine.services.dc.api.filters.BatchFilterFactory;
import com.sap.engine.services.dc.api.lock_mng.AlreadyLockedException;
import com.sap.engine.services.dc.api.model.ModelFactory;
import com.sap.engine.services.dc.api.model.SoftwareType;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Oct 17, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class ValidateCommand extends DeployCommand {

	protected int performOperation() {
		try {
			ComponentManager componentManager = getClient()
					.getComponentManager();
			DeployProcessor deployer = componentManager.getDeployProcessor();

			this.fileList.trimToSize();
			DeployItem[] deployItems = new DeployItem[this.fileList.size()];
			this.daLog().logInfo("ASJ.dpl_api.001277", "Items to validate:");
			int i = 0;
			for (Iterator iter = this.fileList.iterator(); iter.hasNext(); i++) {
				String element = (String) iter.next();
				deployItems[i] = deployer.createDeployItem(element);
				this.daLog().logInfo("ASJ.dpl_api.001278", "{0}",
						new Object[] { deployItems[i].toString() });
			}

			if (this.softwareFilters != null && this.softwareFilters.size() > 0) {
				BatchFilterFactory filterFactory = componentManager
						.getBatchFilterFactory();
				ModelFactory modelFactory = componentManager.getModelFactory();
				for (Iterator iter = this.softwareFilters.iterator(); iter
						.hasNext();) {
					String filter = (String) iter.next();
					SoftwareType softwareType = modelFactory
							.createSoftwareType(filter, filter);
					deployer.addBatchFilter(filterFactory
							.createSoftwareTypeBatchFilter(softwareType));
				}
			}

			deployer.setComponentVersionHandlingRule(this.updateStrategy);
			deployer.setDeployWorkflowStrategy(DeployWorkflowStrategy.NORMAL);
			deployer.setErrorStrategy(
					ErrorStrategyAction.PREREQUISITES_CHECK_ACTION,
					this.errorStrategy);
			deployer.setErrorStrategy(ErrorStrategyAction.DEPLOYMENT_ACTION,
					this.errorStrategy);
			deployer.setLifeCycleDeployStrategy(LifeCycleDeployStrategy.BULK);
			if (this.timeout > 0) {
				deployer.setCustomServerTimeout(this.timeout);
			}

			ValidationResult validationResult = null;
			try {
				validationResult = deployer.validate(deployItems);
				if (validationResult == null) {
					addDescription("Validation result could not be received.",
							true);
					return Command.CODE_ERROR_OCCURRED;
				} else {
					ValidationStatus validationStatus = validationResult
							.getValidationStatus();
					if (ValidationStatus.SUCCESS.equals(validationStatus)) {
						return Command.CODE_SUCCESS;
					} else if (ValidationStatus.ERROR.equals(validationStatus)) {
						addDescription(validationResult.toString(), true);
						return Command.CODE_ERROR_OCCURRED;
					} else {
						addDescription("Unknown result status '"
								+ validationStatus + "'", true);
						return Command.CODE_CRITICAL_ERROR;
					}
				}
			} catch (TransportException te) {
				addDescription(
						"Some exception occurred while uploading the files to the server. Reason:"
								+ te.getLocalizedMessage(), true);
				super.daLog().logThrowable(te);
				return Command.CODE_ERROR_OCCURRED;
			} catch (ValidationException ve) {
				addDescription("Validation exception occurred. Reason:"
						+ ve.getLocalizedMessage(), true);
				super.daLog().logThrowable(ve);
				return Command.CODE_ERROR_OCCURRED;
			} catch (EngineTimeoutException ete) {
				addDescription("Engine timeout. Reason:"
						+ ete.getLocalizedMessage(), true);
				super.daLog().logThrowable(ete);
				return Command.CODE_ERROR_ENGINE_TIMEOUT;
			} catch (DeployException de) {
				addDescription(
						"Exception occurred during the validation. Reason:"
								+ de.getLocalizedMessage(), true);
				super.daLog().logThrowable(de);
				return Command.CODE_ERROR_OCCURRED;
			} catch (AlreadyLockedException al) {
				addDescription(
						"DC is already locked. Probably other deployment or validation is performing at the moment. Reason:"
								+ al.getLocalizedMessage(), true);
				super.daLog().logThrowable(al);
				return Command.CODE_ERROR_OCCURRED;
			} catch (APIException ae) {
				addDescription(
						"General validation exception. For more info please refer to the trace file. Reason:"
								+ ae.getLocalizedMessage(), true);
				super.daLog().logThrowable(ae);
				return Command.CODE_ERROR_OCCURRED;
			}
		} catch (ConnectionException e) {
			addDescription("ConnectionException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (DeployException e) {
			addDescription("DeployException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		}
	}
}
