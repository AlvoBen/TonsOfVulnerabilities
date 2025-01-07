/*
 * Copyright (C) 2000 - 2005 by SAP AG, Walldorf,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.cm.validate;

import java.rmi.Remote;

import com.sap.engine.services.dc.cm.deploy.ValidationException;
import com.sap.engine.services.dc.cm.undeploy.GenericUndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.utils.filters.BatchFilter;

/**
 * Date: Dec 13, 2007
 * 
 * @author Todor Atanasov(i043963)
 */
public interface Validator extends Remote {
	public ValidateResult validate(ValidationBatch[] batchList, String sessionId)
			throws ValidationException;

	public UndeployValidationBatch createUndeployBatch(
			GenericUndeployItem[] undeployItems);

	public UndeployValidationBatch createUndeployBatch(
			UndeployItem[] undeployItems);
	
	public DeployValidationBatch createDeployBatch(String[] deployItemPaths,
			BatchFilter[] batchFilters);
}
