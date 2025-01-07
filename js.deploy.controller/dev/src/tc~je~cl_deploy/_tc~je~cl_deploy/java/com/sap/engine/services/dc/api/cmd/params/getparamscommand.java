/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on Oct 18, 2005
 */
package com.sap.engine.services.dc.api.cmd.params;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.cmd.AbstractCommand;
import com.sap.engine.services.dc.api.cmd.Command;
import com.sap.engine.services.dc.api.params.Param;
import com.sap.engine.services.dc.api.params.ParamsException;
import com.sap.engine.services.dc.api.params.ParamsProcessor;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright (c) 2005, SAP-AG Date: Oct 18, 2005
 * 
 * @author Boris Savov(i030791)
 * @version 1.0
 * @since 7.1
 * 
 */
public class GetParamsCommand extends AbstractCommand {
	private File outputFile = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.api.cmd.AbstractCommand#processOption(java
	 * .lang.String, java.lang.String)
	 */
	protected int processOption(String key, String value) {
		if ("-o".equals(key) || "--outFile".equals(key)) {
			this.outputFile = new File(value);
			try {
				if (this.outputFile.exists()) {
					if (!this.outputFile.canWrite()) {
						addDescription("Cannot write to existing file '"
								+ super.getCanonicalFilePath(this.outputFile)
								+ "'", true);
						return Command.CODE_ERROR_OCCURRED;
					}
				} else if (!this.outputFile.createNewFile()) {
					addDescription(
							"Cannot write to file '"
									+ super
											.getCanonicalFilePath(this.outputFile)
									+ "'", true);
					return Command.CODE_ERROR_OCCURRED;
				}
			} catch (IOException e) {
				addDescription("Exception during creating file '"
						+ super.getCanonicalFilePath(this.outputFile) + "'",
						true);
				super.daLog().logThrowable(e);
				return Command.CODE_ERROR_OCCURRED;
			} finally {
				try {
					this.outputFile.delete();
				} catch (SecurityException e) {
					// $JL-EXC$
				}
			}
			return Command.CODE_SUCCESS;
		} else {
			addDescription("Unknown option '" + key + "', value '" + value
					+ "'", false);
			return Command.CODE_SUCCESS_WITH_WARNINGS;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.api.cmd.AbstractCommand#executeCommand()
	 */
	protected int executeCommand() {
		try {
			ParamsProcessor paramsProcessor = getClient().getComponentManager()
					.getParamsProcessor();
			Param[] params = paramsProcessor.getAllParams();
			FileOutputStream fos = null;
			try {
				Properties props = new Properties();
				for (int i = 0; i < params.length; i++) {
					props
							.setProperty(params[i].getName(), params[i]
									.getValue());
				}
				fos = new FileOutputStream(this.outputFile);
				props.store(fos, "");
			} catch (FileNotFoundException e1) {
				addDescription("FileNotFoundException:" + e1.getMessage(), true);
				super.daLog().logThrowable(e1);
				return Command.CODE_ERROR_OCCURRED;
			} catch (IOException e) {
				addDescription("FileNotFoundException:" + e.getMessage(), true);
				super.daLog().logThrowable(e);
				return Command.CODE_ERROR_OCCURRED;
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e2) {
						// $JL-EXC$
					}
				}
			}
			return Command.CODE_SUCCESS;
		} catch (ConnectionException e) {
			addDescription("ConnectionException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		} catch (ParamsException e) {
			addDescription("ParamsException:" + e.getMessage(), true);
			super.daLog().logThrowable(e);
			return Command.CODE_ERROR_OCCURRED;
		}
	}
}
