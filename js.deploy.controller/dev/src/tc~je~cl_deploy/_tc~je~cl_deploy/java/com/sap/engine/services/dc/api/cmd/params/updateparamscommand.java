/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Oct 19, 2005
 */
package com.sap.engine.services.dc.api.cmd.params;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.cmd.Command;
import com.sap.engine.services.dc.api.params.Param;
import com.sap.engine.services.dc.api.params.ParamNotFoundException;
import com.sap.engine.services.dc.api.params.ParamsException;
import com.sap.engine.services.dc.api.params.ParamsProcessor;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Oct 19, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class UpdateParamsCommand extends AddParamsCommand {

	protected int performOperation(Properties params) {
		ParamsProcessor paramsProcessor;
		try {
			paramsProcessor = getClient().getComponentManager()
					.getParamsProcessor();
			Set set = params.entrySet();
			Param[] apiParams = new Param[params.size()];
			int i = 0;
			for (Iterator iter = set.iterator(); iter.hasNext(); i++) {
				Map.Entry element = (Map.Entry) iter.next();
				apiParams[i] = paramsProcessor.createParam((String) element
						.getKey(), (String) element.getValue());
				super.daLog().logInfo(
						"ASJ.dpl_api.001281",
						"Create parameter [{0}] with value [{1}]",
						new Object[] { apiParams[i].getName(),
								apiParams[i].getValue() });
			}
			paramsProcessor.updateParams(apiParams);
			return Command.CODE_SUCCESS;
		} catch (ConnectionException e) {
			addDescription("ConnectionException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (ParamNotFoundException e) {
			addDescription(
					"At least one parameter not found:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (ParamsException e) {
			addDescription("ParamsException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		}
	}
}
