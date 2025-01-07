/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.api.params.impl;

import java.rmi.Remote;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.impl.IRemoteReferenceHandler;
import com.sap.engine.services.dc.api.params.Param;
import com.sap.engine.services.dc.api.params.ParamAlreadyExistsException;
import com.sap.engine.services.dc.api.params.ParamNotFoundException;
import com.sap.engine.services.dc.api.params.ParamsException;
import com.sap.engine.services.dc.api.params.ParamsProcessor;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.api.util.DALog;
import com.sap.engine.services.dc.api.util.DAUtils;
import com.sap.engine.services.dc.api.util.ServiceTimeWatcher;
import com.sap.engine.services.dc.api.util.exception.APIExceptionConstants;
import com.sap.engine.services.dc.cm.CMException;
import com.sap.engine.services.dc.cm.params.ParamManager;
import com.sap.engine.services.dc.cm.params.RemoteParamsFactory;
import com.sap.engine.services.dc.cm.params.RemoteParamsFactoryException;
import com.sap.engine.services.rmi_p4.P4ObjectBroker;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-9
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
final class ParamsProcessorImpl implements ParamsProcessor,
		IRemoteReferenceHandler {
	private final Session session;
	private RemoteParamsFactory remoteParamsFactory;
	private ParamManager paramManager;
	private final DALog daLog;

	// remote references to be handled within an instance of this class
	private Set remoteRefs = new HashSet();

	ParamsProcessorImpl(Session session) throws ConnectionException,
			ParamsException {
		this.daLog = session.getLog();
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		if (daLog.isDebugTraceable()) {
			this.daLog.traceDebug("[ B E G I N ParamsProcessor],timerId=[{0}]",
					new Object[] { new Long(serviceTimeWatcher.getId()) });
		}
		this.session = session;
		// add the instance as a remote reference handler to the session
		this.session.addRemoteReferenceHandler(this);
		try {
			this.remoteParamsFactory = this.session.createCM()
					.getRemoteParamsFactory();
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug(
						"remoteParamsFactory got successfully,time:=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
			if (this.remoteParamsFactory == null) {
				throw new ParamsException(
						this.daLog.getLocation(),
						APIExceptionConstants.PARAMS_CANNOT_CREATE_REMOTE_FACTORY,
						null);
			}
			// register the reference to the obtained remote object
			registerRemoteReference(remoteParamsFactory);
			this.paramManager = this.remoteParamsFactory.createParamManager();
			// register the reference to the obtained remote object
			registerRemoteReference(paramManager);
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug(
						"paramManager created successfully,time:=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
		} catch (com.sap.engine.services.dc.cm.params.RemoteParamsFactoryException rpfe) {
			String exceptionName = DAUtils.getThrowableClassName(rpfe);
			this.daLog.traceError("ASJ.dpl_api.001103",
					"Failed to create ParamsProcessor [{0}],cause=[{1}]",
					new Object[] { exceptionName, rpfe.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_REMOTEPARAMSFACTORY_EXCEPTION,
					new String[] { exceptionName, rpfe.getMessage() }, rpfe);
		} catch (CMException cme) {
			String exceptionName = DAUtils.getThrowableClassName(cme);
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug(
						"Failed to create ParamsProcessor [{0}],cause=[{1}]",
						new Object[] { exceptionName, cme.getMessage() });
			}
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_CM_EXCEPTION, new String[] {
							exceptionName, cme.getMessage() }, cme);
		} finally {
			if (daLog.isDebugTraceable()) {
				this.daLog.traceDebug("[ E N D ParamsProcessor],timer=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
		}
	}

	public Param createParam(String paramName, String paramValue) {
		return new ParamImpl(paramName, paramValue);
	}

	public void addParam(Param param) throws ParamAlreadyExistsException,
			ParamsException {
		this.daLog.logInfo("ASJ.dpl_api.001104",
				"+++++ Starting  A D D  P A R A M  action +++++");
		if (param == null) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1119] Parameter can not be null.");
		}
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		if (daLog.isDebugTraceable()) {
			this.daLog.traceDebug("[ B E G I N addParam],timerId=[{0}]",
					new Object[] { new Long(serviceTimeWatcher.getId()) });
		}
		try {
			this.paramManager.addParam(this.remoteParamsFactory.createParam(
					param.getName(), param.getValue()));
			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug("parameter :[{0}] added successfully",
						new Object[] { param });
			}
		} catch (com.sap.engine.services.dc.cm.params.ParamAlreadyExistsException e) {
			this.daLog.logError("ASJ.dpl_api.001105",
					"Parameter Already Exists. name [{0}],cause=[{1}]",
					new Object[] { param.getName(), e.getMessage() });
			throw new ParamAlreadyExistsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMALREADYEXISTS_EXCEPTION,
					new String[] { param.getName() }, e);
		} catch (com.sap.engine.services.dc.cm.params.ParamsException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001106",
							"ParamsException during add parameter operation [{0}],Parameter [{1}],cause=[{2}]",
							new Object[] { exceptionName, param.getName(),
									e.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMS_EXCEPTION_WITH_INFO,
					new String[] { exceptionName, param.getName(),
							e.getMessage() }, e);
		} catch (RemoteParamsFactoryException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001107",
							"Remote Factory Exception during add parameter operation [{0}],Parameter [{1}],cause=[{2}]",
							new Object[] { exceptionName, param.getName(),
									e.getMessage() });
			throw new ParamsException(
					this.daLog.getLocation(),
					APIExceptionConstants.DC_REMOTEPARAMSFACTORY_EXCEPTION_WITH_INFO,
					new String[] { exceptionName, param.getName(),
							e.getMessage() }, e);
		} finally {
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001108",
						"[ E N D addParam],timer=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
			this.daLog.logInfo("ASJ.dpl_api.001109",
					"+++++ End  A D D  P A R A M  action +++++");
		}
	}

	public void addParams(Param[] params) throws ParamAlreadyExistsException,
			ParamsException {
		if (params == null || params.length == 0) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1125] Parameters can not be null or empty array.");
		}
		this.daLog.logInfo("ASJ.dpl_api.001110",
				"+++++ Starting  A D D  P A R A M S  action +++++");
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		if (daLog.isInfoTraceable()) {
			this.daLog.traceInfo("ASJ.dpl_api.001111",
					"[ B E G I N addParams],timerId=[{0}]",
					new Object[] { new Long(serviceTimeWatcher.getId()) });
		}
		try {
			Hashtable mapParams = new Hashtable(params.length);
			for (int i = 0; i < params.length; i++) {
				if (params[i] == null) {
					throw new IllegalArgumentException(
							"[ERROR CODE DPL.DCAPI.1126] Parameter #" + i
									+ " is null.");
				}
				mapParams.put(params[i].getName(), params[i].getValue());
			}
			this.paramManager.addParams(this.remoteParamsFactory
					.createParams(mapParams));
			for (int i = 0; i < params.length; i++) {
				this.daLog
						.logInfo(
								"ASJ.dpl_api.001112",
								"parameter : name [{0}] , value [{1}] added successfully",
								new Object[] { params[i].getName(),
										params[i].getValue() });
			}
		} catch (com.sap.engine.services.dc.cm.params.ParamAlreadyExistsException e) {
			this.daLog.logError("ASJ.dpl_api.001113",
					"Parameter Already Exists.Cause=[{0}]", new Object[] { e
							.getMessage() });
			throw new ParamAlreadyExistsException(
					this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMALREADYEXISTS_EXCEPTION_WITH_INFO,
					new String[] { e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.params.ParamsException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001114",
							"ParamsException during add parameters operation [{0}].Cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMS_EXCEPTION, new String[] {
							exceptionName, e.getMessage() }, e);
		} catch (RemoteParamsFactoryException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001115",
							"Remote Factory Exception during add parameters operation [{0}].Cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_REMOTEPARAMSFACTORY_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} finally {
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001116",
						"[ E N D addParams],timer=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
			this.daLog.logInfo("ASJ.dpl_api.001117",
					"+++++ End  A D D  P A R A M S action +++++");
		}
	}

	public Param[] getAllParams() throws ParamsException {
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		this.daLog.logInfo("ASJ.dpl_api.001118",
				"+++++ Starting  G E T  A L L  P A R A M S  action +++++");
		if (daLog.isInfoTraceable()) {
			this.daLog.traceInfo("ASJ.dpl_api.001119",
					"[ B E G I N getAllParams],timerId=[{0}]",
					new Object[] { new Long(serviceTimeWatcher.getId()) });
		}
		try {
			com.sap.engine.services.dc.cm.params.Param[] remoteParams = this.paramManager
					.getAllParams();
			Param[] params = new Param[remoteParams != null ? remoteParams.length
					: 0];
			if (remoteParams != null) {
				for (int i = 0; i < remoteParams.length; i++) {
					params[i] = new ParamImpl(remoteParams[i].getName(),
							remoteParams[i].getValue());
					this.daLog
							.logInfo("ASJ.dpl_api.001120",
									"parameter name [{0}], value [{1}]",
									new Object[] { params[i].getName(),
											params[i].getValue() });
				}
			}
			return params;
		} catch (com.sap.engine.services.dc.cm.params.ParamsException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001121",
							"ParamsException during get all parameters operation [{0}].Cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMS_EXCEPTION, new String[] {
							exceptionName, e.getMessage() }, e);
		} finally {
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001122",
						"[ E N D getAllParams],timer=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
			this.daLog.logInfo("ASJ.dpl_api.001123",
					"+++++ End G E T  A L L  P A R A M S  action +++++");
		}
	}

	public Param getParamByName(String paramName)
			throws ParamNotFoundException, ParamsException {
		if (paramName == null || paramName.length() == 0) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1129] Parameter name can not be null or empty.");
		}
		this.daLog.logInfo("ASJ.dpl_api.001124",
				"+++++ Starting  G E T  P A R A M  B Y  N A M E  action +++++");
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		if (daLog.isInfoTraceable()) {
			this.daLog.traceInfo("ASJ.dpl_api.001125",
					"[ B E G I N getParamByName],timerId=[{0}]",
					new Object[] { new Long(serviceTimeWatcher.getId()) });
		}
		try {
			com.sap.engine.services.dc.cm.params.Param remoteParam = this.paramManager
					.getParamByName(paramName);
			Param param = new ParamImpl(remoteParam.getName(), remoteParam
					.getValue());
			this.daLog.logInfo("ASJ.dpl_api.001126",
					"get parameter name [{0}], value [{1}]", new Object[] {
							param.getName(), param.getValue() });
			return param;
		} catch (com.sap.engine.services.dc.cm.params.ParamNotFoundException e) {
			this.daLog.logError("ASJ.dpl_api.001127",
					"Parameter [{0}] not found. Cause=[{1}]", new Object[] {
							paramName, e.getMessage() });
			throw new ParamNotFoundException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMNOTFOUND_EXCEPTION_WITH_INFO,
					new String[] { paramName, e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.params.ParamsException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001128",
							"ParamsException during get parameter [{0}] operation [{1}].Cause=[{2}]",
							new Object[] { paramName, exceptionName,
									e.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMS_EXCEPTION_WITH_INFO,
					new String[] { exceptionName, paramName, e.getMessage() },
					e);
		} finally {
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001129",
						"[ E N D getParamByName],timer=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
			this.daLog.logInfo("ASJ.dpl_api.001130",
					"+++++ End  G E T  P A R A M  B Y  N A M E  action +++++");
		}
	}

	public void removeParam(Param param) throws ParamNotFoundException,
			ParamsException {
		if (param == null) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1131] Parameter can not be null.");
		}
		this.daLog.logInfo("ASJ.dpl_api.001131",
				"+++++ Starting  R E M O V E  P A R A M  action +++++");
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		if (daLog.isInfoTraceable()) {
			this.daLog.traceInfo("ASJ.dpl_api.001132",
					"[ B E G I N removeParam],timerId=[{0}]",
					new Object[] { new Long(serviceTimeWatcher.getId()) });
		}
		try {
			this.paramManager.removeParam(this.remoteParamsFactory.createParam(
					param.getName(), param.getValue()));
			if (daLog.isDebugLoggable()) {
				this.daLog.logDebug("parameter [{0}] removed successfully",
						new Object[] { param.getName() });
			}
		} catch (com.sap.engine.services.dc.cm.params.ParamNotFoundException e) {
			this.daLog.logError("ASJ.dpl_api.001133",
					"Parameter [{0}] not found for remove . Cause=[{1}]",
					new Object[] { param.getName(), e.getMessage() });
			throw new ParamNotFoundException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMNOTFOUND_EXCEPTION_WITH_INFO,
					new String[] { param.getName(), e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.params.ParamsException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001134",
							"ParamsException during remove parameter [{0}] operation [{1}].Cause=[{2}]",
							new Object[] { param.getName(), exceptionName,
									e.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMS_EXCEPTION_WITH_INFO,
					new String[] { exceptionName, param.getName(),
							e.getMessage() }, e);
		} catch (RemoteParamsFactoryException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001135",
							"Remote Factory Exception during remove parameter [{0}] operation [{1}].Cause=[{2}]",
							new Object[] { param.getName(), exceptionName,
									e.getMessage() });
			throw new ParamsException(
					this.daLog.getLocation(),
					APIExceptionConstants.DC_REMOTEPARAMSFACTORY_EXCEPTION_WITH_INFO,
					new String[] { exceptionName, param.getName(),
							e.getMessage() }, e);
		} finally {
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001136",
						"[ E N D removeParam],timer=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
			this.daLog.logInfo("ASJ.dpl_api.001137",
					"+++++ End  R E M O V E  P A R A M  action +++++");
		}
	}

	public void removeParams(Param[] params) throws ParamNotFoundException,
			ParamsException {
		if (params == null || params.length == 0) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1132] Parameters can not be null or empty array.");
		}
		this.daLog.logInfo("ASJ.dpl_api.001283",
				"+++++ Starting  R E M O V E  P A R A M S  action +++++");
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		if (daLog.isInfoTraceable()) {
			this.daLog.traceInfo("ASJ.dpl_api.001138",
					"[ B E G I N removeParams],timerId=[{0}]",
					new Object[] { new Long(serviceTimeWatcher.getId()) });
		}
		com.sap.engine.services.dc.cm.params.Param[] remoteParams = new com.sap.engine.services.dc.cm.params.Param[params.length];
		try {
			for (int i = 0; i < params.length; i++) {
				if (params[i] == null) {
					throw new IllegalArgumentException(
							"[ERROR CODE DPL.DCAPI.1133] Parameter #" + i
									+ " is null.");
				}
				remoteParams[i] = this.remoteParamsFactory.createParam(
						params[i].getName(), params[i].getValue());
			}
			this.paramManager.removeParams(remoteParams);
			if (daLog.isDebugLoggable()) {
				for (int i = 0; i < params.length; i++) {
					this.daLog.logDebug("parameter [{0}] removed successfully",
							new Object[] { params[i].getName() });
				}
			}
		} catch (com.sap.engine.services.dc.cm.params.ParamNotFoundException e) {
			this.daLog.logError("ASJ.dpl_api.001139",
					"Parameter not found for remove. Cause=[{0}]",
					new Object[] { e.getMessage() });
			throw new ParamNotFoundException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMNOTFOUND_EXCEPTION,
					new String[] { e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.params.ParamsException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001140",
							"ParamsException during remove parameters operation [{0}].Cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMS_EXCEPTION, new String[] {
							exceptionName, e.getMessage() }, e);
		} catch (RemoteParamsFactoryException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001141",
							"Remote Factory Exception during remove parameters operation [{0}].Cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_REMOTEPARAMSFACTORY_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} finally {
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001142",
						"[ E N D removeParams],timer=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
			this.daLog.logInfo("ASJ.dpl_api.001143",
					"+++++ End  R E M O V E  P A R A M S  action +++++");
		}
	}

	public void updateParam(Param param) throws ParamNotFoundException,
			ParamsException {
		if (param == null) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1135] Parameter can not be null.");
		}
		this.daLog.logInfo("ASJ.dpl_api.001144",
				"+++++ Starting  U P D A T E  P A R A M  action +++++");
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		if (daLog.isInfoTraceable()) {
			this.daLog.traceInfo("ASJ.dpl_api.001145", "timerId=[{0}]",
					new Object[] { new Long(serviceTimeWatcher.getId()) });
		}
		try {
			this.paramManager.updateParam(this.remoteParamsFactory.createParam(
					param.getName(), param.getValue()));
			if (daLog.isDebugLoggable()) {
				this.daLog
						.logDebug(
								"parameter name [{0}], value [{1}] updated successfully",
								new Object[] { param.getName(),
										param.getValue() });
			}
		} catch (com.sap.engine.services.dc.cm.params.ParamNotFoundException e) {
			this.daLog.logError("ASJ.dpl_api.001146",
					"Parameter [{0}] not found for update. Cause=[{1}]",
					new Object[] { param.getName(), e.getMessage() });
			throw new ParamNotFoundException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMNOTFOUND_EXCEPTION_WITH_INFO,
					new String[] { param.getName(), e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.params.ParamsException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001147",
							"ParamsException during update parameter [{0}] operation [{1}].Cause=[{2}]",
							new Object[] { param.getName(), exceptionName,
									e.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMS_EXCEPTION_WITH_INFO,
					new String[] { exceptionName, param.getName(),
							e.getMessage() }, e);
		} catch (RemoteParamsFactoryException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001148",
							"Remote Factory Exception during update parameter [{0}] operation [{1}].Cause=[{2}]",
							new Object[] { param.getName(), exceptionName,
									e.getMessage() });
			throw new ParamsException(
					this.daLog.getLocation(),
					APIExceptionConstants.DC_REMOTEPARAMSFACTORY_EXCEPTION_WITH_INFO,
					new String[] { exceptionName, param.getName(),
							e.getMessage() }, e);
		} finally {
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001149",
						"[ E N D updateParam],timer=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
			this.daLog.logInfo("ASJ.dpl_api.001150",
					"+++++ End  U P D A T E  P A R A M  action +++++");
		}
	}

	public void updateParams(Param[] params) throws ParamNotFoundException,
			ParamsException {
		if (params == null || params.length == 0) {
			throw new IllegalArgumentException(
					"[ERROR CODE DPL.DCAPI.1136] Parameters can not be null or empty array.");
		}
		this.daLog.logInfo("ASJ.dpl_api.001151",
				"+++++ Starting  U P D A T E  P A R A M S  action +++++");
		ServiceTimeWatcher serviceTimeWatcher = new ServiceTimeWatcher();
		if (daLog.isInfoTraceable()) {
			this.daLog.traceInfo("ASJ.dpl_api.001152",
					"[ B E G I N updateParams],timerId=[{0}]",
					new Object[] { new Long(serviceTimeWatcher.getId()) });
		}
		com.sap.engine.services.dc.cm.params.Param[] remoteParams = new com.sap.engine.services.dc.cm.params.Param[params.length];
		try {
			for (int i = 0; i < params.length; i++) {
				if (params[i] == null) {
					throw new IllegalArgumentException(
							"[ERROR CODE DPL.DCAPI.1137] Parameter #" + i
									+ " is null.");
				}
				remoteParams[i] = this.remoteParamsFactory.createParam(
						params[i].getName(), params[i].getValue());
			}
			this.paramManager.updateParams(remoteParams);
			if (daLog.isDebugLoggable()) {
				for (int i = 0; i < params.length; i++) {
					this.daLog
							.logDebug(
									"parameter name [{0}], value [{1}] updated successfully",
									new Object[] { params[i].getName(),
											params[i].getValue() });
				}
			}
		} catch (com.sap.engine.services.dc.cm.params.ParamNotFoundException e) {
			this.daLog.logError("ASJ.dpl_api.001153",
					"Parameter not found for update. Cause=[{0}]",
					new Object[] { e.getMessage() });
			throw new ParamNotFoundException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMNOTFOUND_EXCEPTION,
					new String[] { e.getMessage() }, e);
		} catch (com.sap.engine.services.dc.cm.params.ParamsException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001154",
							"ParamsException during update parameters 'operation [{0}].Cause=[{1}]",
							new Object[] { e.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_PARAMS_EXCEPTION, new String[] {
							exceptionName, e.getMessage() }, e);
		} catch (RemoteParamsFactoryException e) {
			String exceptionName = DAUtils.getThrowableClassName(e);
			this.daLog
					.logError(
							"ASJ.dpl_api.001155",
							"Remote Factory Exception during update parameters operation [{0}].Cause=[{1}]",
							new Object[] { exceptionName, e.getMessage() });
			throw new ParamsException(this.daLog.getLocation(),
					APIExceptionConstants.DC_REMOTEPARAMSFACTORY_EXCEPTION,
					new String[] { exceptionName, e.getMessage() }, e);
		} finally {
			if (daLog.isInfoTraceable()) {
				this.daLog.traceInfo("ASJ.dpl_api.001157",
						"[ E N D updateParams],timer=[{0}]",
						new Object[] { serviceTimeWatcher
								.getTotalElapsedTimeAsString() });
			}
			this.daLog.logInfo("ASJ.dpl_api.001158",
					"+++++ End  U P D A T E  P A R A M S  action +++++");
		}
	}

	public String toString() {
		return "ParamsProcessorImpl[session=" + this.session + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.impl.IRemoteReferenceHandler#
	 * registerRemoteReference(Remote)
	 */
	public void registerRemoteReference(Remote remote) {
		remoteRefs.add(remote);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.api.impl.IRemoteReferenceHandler#
	 * releaseRemoteReferences()
	 */
	public void releaseRemoteReferences() {
		// try to release the remote references
		P4ObjectBroker broker = P4ObjectBroker.getBroker();
		if (broker == null) {
			this.daLog
					.logDebug(
							"ASJ.dpl_api.001159",
							"The P4ObjectBroker is null while trying to release remote references. The release operation is aborted!");
		} else {
			Iterator iter = this.remoteRefs.iterator();
			while (iter.hasNext()) {
				Remote remoteRef = (Remote) iter.next();
				// separate error handling for each resource
				// to release as much remote refs. as possible
				try {
					broker.release(remoteRef);
				} catch (Exception e) {
					this.daLog
							.logThrowable(
									"ASJ.dpl_api.001160",
									"An exception occured while trying to release remote reference for object [{0}]",
									e, new Object[] { remoteRef });
				}
			}
		}
		remoteRefs.clear();
	}
}
