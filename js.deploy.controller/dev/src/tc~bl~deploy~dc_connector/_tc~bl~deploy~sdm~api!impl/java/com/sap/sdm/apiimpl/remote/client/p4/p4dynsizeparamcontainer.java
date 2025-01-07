package com.sap.sdm.apiimpl.remote.client.p4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.sap.engine.services.dc.api.ConnectionException;
import com.sap.engine.services.dc.api.params.ParamAlreadyExistsException;
import com.sap.engine.services.dc.api.params.ParamNotFoundException;
import com.sap.engine.services.dc.api.params.ParamsException;
import com.sap.engine.services.dc.api.params.ParamsProcessor;
import com.sap.sdm.api.remote.DynSizeParamContainer;
import com.sap.sdm.api.remote.Param;
import com.sap.sdm.api.remote.RemoteException;
import com.sap.sdm.apiimpl.remote.client.APIRemoteExceptionImpl;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-14
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class P4DynSizeParamContainer implements DynSizeParamContainer {

	private final com.sap.engine.services.dc.api.Client dcClient;
	private Collection tempParams;
	private Collection addedParams;
	private Collection removedParams;

	P4DynSizeParamContainer(com.sap.engine.services.dc.api.Client dcClient) {
		this.dcClient = dcClient;

		this.addedParams = new ArrayList();
		this.removedParams = new ArrayList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DynSizeParamContainer#canAddParam(com.sap.sdm.
	 * api.remote.Param)
	 */
	public boolean canAddParam(Param param) throws RemoteException {
		initParams();

		return !contains(param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DynSizeParamContainer#addParam(com.sap.sdm.api
	 * .remote.Param)
	 */
	public void addParam(Param param) throws RemoteException {
		AssertionCheck.checkForNullArg(getClass(), "addParam", param);

		initParams();

		if (!canAddParam(param)) {
			final String errText = "P4DynSizeParamContainer.addParam(): Cannot add param "
					+ param.getName();
			throw new IllegalArgumentException(errText);
		}

		final ParamsProcessor paramsProcessor;
		try {
			paramsProcessor = this.dcClient.getComponentManager()
					.getParamsProcessor();
		} catch (ConnectionException apice) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while getting the Params Processor "
							+ "from the Deploy Controller API.", apice);
		} catch (ParamsException apie) {
			throw new APIRemoteExceptionImpl(
					"An error occurred while getting the Params Processor "
							+ "from the Deploy Controller API.", apie);
		}

		final com.sap.engine.services.dc.api.params.Param dcParam = paramsProcessor
				.createParam(param.getName(), param.getValueString());

		this.tempParams.add(dcParam);
		this.addedParams.add(dcParam);
		this.removedParams.remove(dcParam);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DynSizeParamContainer#canRemoveParam(com.sap.sdm
	 * .api.remote.Param)
	 */
	public boolean canRemoveParam(Param param) throws RemoteException {
		AssertionCheck.checkForNullArg(getClass(), "canRemoveParam", param);

		initParams();

		if (!this.contains(param)) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DynSizeParamContainer#removeParam(com.sap.sdm.
	 * api.remote.Param)
	 */
	public void removeParam(Param param) throws RemoteException {
		AssertionCheck.checkForNullArg(getClass(), "removeParam", param);

		initParams();

		if (!canRemoveParam(param)) {
			String errText = "P4DynSizeParamContainer.removeParam(): Cannot remove param "
					+ param.getName();
			throw new IllegalArgumentException(errText);
		}

		final ParamsProcessor paramsProcessor;
		try {
			paramsProcessor = this.dcClient.getComponentManager()
					.getParamsProcessor();
		} catch (ConnectionException apice) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while getting the Params Processor "
							+ "from the Deploy Controller API.", apice);
		} catch (ParamsException apipe) {
			throw new APIRemoteExceptionImpl(
					"An error occurred while getting the Params Processor "
							+ "from the Deploy Controller API.", apipe);
		}

		final com.sap.engine.services.dc.api.params.Param dcParam = paramsProcessor
				.createParam(param.getName(), param.getValueString());

		this.tempParams.remove(dcParam);
		this.removedParams.add(dcParam);
		this.addedParams.remove(dcParam);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DynSizeParamContainer#canRemoveParamByName(java
	 * .lang.String)
	 */
	public boolean canRemoveParamByName(String name) throws RemoteException {
		AssertionCheck
				.checkForNullArg(getClass(), "canRemoveParamByName", name);

		initParams();

		if (!this.contains(name)) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.DynSizeParamContainer#removeParamByName(java.lang
	 * .String)
	 */
	public void removeParamByName(String paramName) throws RemoteException {
		AssertionCheck.checkForNullArg(getClass(), "removeParamByName",
				paramName);

		initParams();

		if (!canRemoveParamByName(paramName)) {
			final String errText = "P4DynSizeParamContainer.removeParamByName(): Cannot remove param "
					+ paramName;
			throw new IllegalArgumentException(errText);
		}

		com.sap.engine.services.dc.api.params.Param param4Remove = null;
		for (Iterator iter = this.tempParams.iterator(); iter.hasNext();) {
			final com.sap.engine.services.dc.api.params.Param dcParam = (com.sap.engine.services.dc.api.params.Param) iter
					.next();
			if (dcParam.getName().equals(paramName)) {
				param4Remove = dcParam;
				break;
			}
		}

		if (param4Remove != null) {
			this.tempParams.remove(param4Remove);
			this.removedParams.add(param4Remove);
			this.addedParams.remove(param4Remove);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.AbstractParamContainer#getParams()
	 */
	public Param[] getParams() throws RemoteException {
		initParams();

		return getMapedParams();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.api.remote.AbstractParamContainer#getParamByName(java.lang
	 * .String)
	 */
	public Param getParamByName(String name) throws RemoteException {
		AssertionCheck.checkForNullArg(getClass(), "getParamByName", name);

		initParams();

		if (this.tempParams.isEmpty()) {
			return null;
		}

		for (Iterator iter = this.tempParams.iterator(); iter.hasNext();) {
			final com.sap.engine.services.dc.api.params.Param dcParam = (com.sap.engine.services.dc.api.params.Param) iter
					.next();
			if (dcParam.getName().equals(name)) {
				return P4HelperFactoryImpl.getInstance().createP4InternalParam(
						dcParam.getName(), dcParam.getValue());
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.api.remote.AbstractParamContainer#save()
	 */
	public void save() throws RemoteException {
		final ParamsProcessor paramsProcessor;
		try {
			paramsProcessor = this.dcClient.getComponentManager()
					.getParamsProcessor();
		} catch (ConnectionException apie) {
			throw new APIRemoteExceptionImpl(
					"Connection error occurred while getting the Params Processor "
							+ "from the Deploy Controller API.", apie);
		} catch (ParamsException apipe) {
			throw new APIRemoteExceptionImpl(
					"An error occurred while getting the Params Processor "
							+ "from the Deploy Controller API.", apipe);
		}

		if (!this.removedParams.isEmpty()) {
			try {
				paramsProcessor
						.removeParams((com.sap.engine.services.dc.api.params.Param[]) this.removedParams
								.toArray(new com.sap.engine.services.dc.api.params.Param[this.removedParams
										.size()]));

				removedParams = new ArrayList();
			} catch (ParamNotFoundException pnfe) {
				throw new APIRemoteExceptionImpl(
						"An error occurred while removing the specified "
								+ "parameters via the Deploy Controller API. At least one the parameters is not found.",
						pnfe);
			} catch (ParamsException pe) {
				throw new APIRemoteExceptionImpl(
						"An error occurred while removing the specified "
								+ "parameters via the Deploy Controller API.",
						pe);
			}
		}

		if (!this.addedParams.isEmpty()) {
			try {
				paramsProcessor
						.addParams((com.sap.engine.services.dc.api.params.Param[]) this.addedParams
								.toArray(new com.sap.engine.services.dc.api.params.Param[this.addedParams
										.size()]));
				addedParams = new ArrayList();
			} catch (ParamAlreadyExistsException paee) {
				throw new APIRemoteExceptionImpl(
						"An error occurred while adding the specified "
								+ "parameters via the Deploy Controller API. At least one of the specified parameters "
								+ "already exists.", paee);
			} catch (ParamsException pe) {
				throw new APIRemoteExceptionImpl(
						"An error occurred while adding the specified "
								+ "parameters via the Deploy Controller API.",
						pe);
			}
		}
	}

	private boolean contains(final Param param) throws RemoteException {
		for (Iterator iter = this.tempParams.iterator(); iter.hasNext();) {
			final com.sap.engine.services.dc.api.params.Param dcParam = (com.sap.engine.services.dc.api.params.Param) iter
					.next();
			if (dcParam.getName().equals(param.getName())) {
				return true;
			}
		}

		return false;
	}

	private boolean contains(final String paramName) throws RemoteException {
		for (Iterator iter = this.tempParams.iterator(); iter.hasNext();) {
			final com.sap.engine.services.dc.api.params.Param dcParam = (com.sap.engine.services.dc.api.params.Param) iter
					.next();
			if (dcParam.getName().equals(paramName)) {
				return true;
			}
		}

		return false;
	}

	private synchronized void initParams() throws RemoteException {
		if (this.tempParams == null) {
			this.tempParams = new ArrayList();

			final ParamsProcessor paramsProcessor;
			try {
				paramsProcessor = this.dcClient.getComponentManager()
						.getParamsProcessor();
			} catch (ConnectionException apice) {
				throw new APIRemoteExceptionImpl(
						"Connection error occurred while getting the Params Processor "
								+ "from the Deploy Controller API.", apice);
			} catch (ParamsException apie) {
				throw new APIRemoteExceptionImpl(
						"An error occurred while getting the Params Processor "
								+ "from the Deploy Controller API.", apie);
			}

			final com.sap.engine.services.dc.api.params.Param[] allParams;
			try {
				allParams = paramsProcessor.getAllParams();
			} catch (ParamsException pe) {
				throw new APIRemoteExceptionImpl(
						"An error occurred while getting all the "
								+ "parameters from the Deploy Controller API.",
						pe);
			}

			for (int i = 0; i < allParams.length; i++) {
				this.tempParams.add(allParams[i]);
			}
		}
	}

	private Param[] getMapedParams() throws RemoteException {
		final Param[] params = new Param[this.tempParams.size()];
		int idx = 0;
		for (Iterator iter = this.tempParams.iterator(); iter.hasNext();) {
			final com.sap.engine.services.dc.api.params.Param dcParam = (com.sap.engine.services.dc.api.params.Param) iter
					.next();
			params[idx++] = P4HelperFactoryImpl.getInstance()
					.createP4InternalParam(dcParam.getName(),
							dcParam.getValue());
		}

		return params;
	}

}
