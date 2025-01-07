﻿/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Oct 5, 2005
 */
package com.sap.engine.services.dc.api.cmd.params;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.cmd.AbstractCommand;
import com.sap.engine.services.dc.api.cmd.Command;
import com.sap.engine.services.dc.api.params.Param;
import com.sap.engine.services.dc.api.params.ParamAlreadyExistsException;
import com.sap.engine.services.dc.api.params.ParamsException;
import com.sap.engine.services.dc.api.params.ParamsProcessor;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Oct 5, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class AddParamsCommand extends AbstractCommand {
	private final Properties paramsList = new Properties();

	private int addParamsToList(String filePath) {
		File file = new File(filePath);
		String canonicalPath = getCanonicalFilePath(file);
		if (!file.exists()) {
			addDescription("File '" + canonicalPath + " does not exist.", true);
			return Command.CODE_ERROR_OCCURRED;
		}
		if (!file.canRead()) {
			addDescription("File '" + canonicalPath + " cannot read.", true);
			return Command.CODE_ERROR_OCCURRED;
		}
		this.paramsList.clear();
		try {
			this.paramsList.load(new FileInputStream(file));
			if (this.paramsList.isEmpty()) {
				addDescription("Property file '" + canonicalPath
						+ "' is empty.", true);
				return Command.CODE_ERROR_OCCURRED;
			} else {
				return Command.CODE_SUCCESS;
			}
		} catch (FileNotFoundException e) {
			addDescription("file with parameter '" + getCanonicalFilePath(file)
					+ "' not found.", true);
			return Command.CODE_CRITICAL_ERROR;
		} catch (IOException e) {
			addDescription(
					"Error occurred while reading the content of property file '"
							+ getCanonicalFilePath(file) + "'. Reason: "
							+ e.getLocalizedMessage(), true);
			return Command.CODE_CRITICAL_ERROR;
		}
	}

	protected int performOperation(Properties params) {
		try {
			ParamsProcessor paramsProcessor = getClient().getComponentManager()
					.getParamsProcessor();
			Set set = params.entrySet();
			Param[] apiParams = new Param[params.size()];
			int i = 0;
			for (Iterator iter = set.iterator(); iter.hasNext(); i++) {
				Map.Entry element = (Map.Entry) iter.next();
				apiParams[i] = paramsProcessor.createParam((String) element
						.getKey(), (String) element.getValue());
				super.daLog().logInfo(
						"ASJ.dpl_api.001279",
						"Create parameter [{0}] with value [{1}]",
						new Object[] { apiParams[i].getName(),
								apiParams[i].getValue() });
			}
			paramsProcessor.addParams(apiParams);
			return Command.CODE_SUCCESS;
		} catch (ConnectionException e) {
			addDescription("ConnectionException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (ParamAlreadyExistsException pae) {
			addDescription(pae.getMessage(), true);
			super.daLog().logThrowable(pae);
			return Command.CODE_ERROR_OCCURRED;
		} catch (ParamsException e) {
			addDescription("ParamsException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.cmd.AbstractCommand#processOption(java
	 * .lang.String, java.lang.String)
	 */
	protected int processOption(String key, String value) {
		if ("-f".equals(key) || "--file".equals(key)) {
			return addParamsToList(value);
		} else {
			addDescription("Unknown option '" + key + "', value '" + value
					+ "'", false);
			return Command.CODE_SUCCESS_WITH_WARNINGS;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.cmd.AbstractCommand#internalExec()
	 */
	protected int executeCommand() {
		if (this.paramsList.isEmpty()) {
			usage();
			return Command.CODE_ERROR_OCCURRED;
		} else {
			return performOperation(this.paramsList);
		}
	}

}
