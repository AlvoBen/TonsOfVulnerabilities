/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.validate.jlin.impl;

import java.io.IOException;
import java.util.Set;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.jlinee.lib.ApplicationComponentInfo;
import com.sap.engine.jlinee.lib.ApplicationInfo;
import com.sap.engine.jlinee.lib.JLinEEResult;
import com.sap.engine.jlinee.lib.JLinEEValidator;
import com.sap.engine.jlinee.lib.JLinEEValidatorFactory;
import com.sap.engine.jlinee.lib.ResultsDescriptor;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.Convertor;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.engine.services.deploy.server.validate.jlin.AppJLinInfo;
import com.sap.engine.services.deploy.server.validate.jlin.JLinExecException;
import com.sap.engine.services.deploy.server.validate.jlin.JLinPlunin;
import com.sap.engine.services.deploy.server.validate.jlin.JLinValidationException;
import com.sap.tc.jtools.jtci.exceptions.ExecutionException;
import com.sap.tc.logging.Location;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class JLinPluninImpl extends JLinPlunin {
	
	private static final Location location = 
		Location.getLocation(JLinPluninImpl.class);
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.deploy.server.jlin.JLinPlunin#exec(com.sap.engine
	 * .services.deploy.server.jlin.AppJLinInfo)
	 */
	@Override
	public Set<String> exec(AppJLinInfo appJlinInfo) throws JLinExecException,
			JLinValidationException {
		ValidateUtils.nullValidator(appJlinInfo, "AppJLinInfo");
		if (location.beDebug()) {
			DSLog.traceDebug(location, "{0}", appJlinInfo);
		}

		ApplicationInfo appInfo = null;
		final ISubstVarResolverImpl sVarResolver;
		final String tagName = "jlin validate:" + appJlinInfo.getAppName();
		try {
			Accounting.beginMeasure(tagName,
					ApplicationInfo.class);
			try {
				appInfo = new ApplicationInfo(appJlinInfo.getAppName(),
						appJlinInfo.getEarFile(), appJlinInfo.getFileLocator(),
					appJlinInfo.getApplicationComponentInfos().keySet()
								.toArray(new ApplicationComponentInfo[0]),
						appJlinInfo.getCLResourcesOrCLs());
				appInfo.setAnnotations(appJlinInfo.getAnnotations());
				sVarResolver = ISubstVarResolverImpl.getInstance();
			} catch (IOException ioEx) {
				JLinValidationException jlve = new JLinValidationException(
						ExceptionConstants.VAL_APP, new String[] {
								appJlinInfo.getAppName(),
								appJlinInfo.getOperation() }, ioEx);
				jlve.setMessageID("ASJ.dpl_ds.005400");
				throw jlve;
			} catch (ConfigurationException e) {
				JLinValidationException jlve = new JLinValidationException(
						ExceptionConstants.VAL_APP, new String[] {
								appJlinInfo.getAppName(),
								appJlinInfo.getOperation() }, e);
				jlve.setMessageID("ASJ.dpl_ds.005400");
				throw jlve;
			}

			final JLinEEValidator jlValidator = JLinEEValidatorFactory
					.getInstance();

			try {
				final ResultsDescriptor resDscr = jlValidator
						.validateApplication(appInfo, sVarResolver, PropManager
								.getInstance().getExcludedJLinEETests());
				if (location.beDebug()) {
					DSLog
							.traceDebug(
									location, 
									"The result returned from JLinEE validation of application [{0}] is [{1}]",
									appInfo.getApplicationName(),
									(resDscr != null ? resDscr.getAllMessages()
											: "NULL"));
				}
				if (resDscr != null) {
					final JLinEEResult[] warnings = resDscr.getWarnings();
					if (warnings != null && warnings.length > 0) {
						DSLog
								.logWarningWithFaultyDcName(
													location,
													appInfo.getApplicationName(),
													"ASJ.dpl_ds.003009",
													"JLinEE validation of application [{0}] returned result {1}.",
													appInfo.getApplicationName(), resDscr
															.getAllMessages());
					}

					final JLinEEResult errors[] = resDscr.getErrors();
					if (errors != null && errors.length > 0) {
						throw new JLinValidationException(
								ExceptionConstants.VAL_APP_RESULT,
								new String[] { getErrors(errors, appInfo
										.getApplicationName()) });
					}

					return Convertor.cObject2String(warnings);
				}
			} catch (ExecutionException execEx) {
				JLinExecException jlee = new JLinExecException(
						ExceptionConstants.VAL_APP, new String[] {
								appJlinInfo.getAppName(),
								appJlinInfo.getOperation() }, execEx);
				jlee.setMessageID("ASJ.dpl_ds.005400");
				throw jlee;
			}
		} finally {
			Accounting.endMeasure(tagName);
		}
		return null;
	}

	private String getErrors(JLinEEResult errors[], String appName) {
		final StringBuilder sb = new StringBuilder(
				"JLinEE reported following erros for " + appName
						+ " application." + CAConstants.EOL + "ERRORS:"
						+ CAConstants.EOL);
		for (JLinEEResult error : errors) {
			sb.append(" * ");
			sb.append(error.toString());
		}
		return sb.toString();
	}
}