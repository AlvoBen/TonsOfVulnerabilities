package com.sap.engine.services.dc.cm.params.impl;

import java.rmi.RemoteException;

import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.services.dc.cm.params.Param;
import com.sap.engine.services.dc.cm.params.ParamAlreadyExistsException;
import com.sap.engine.services.dc.cm.params.ParamManager;
import com.sap.engine.services.dc.cm.params.ParamNotFoundException;
import com.sap.engine.services.dc.cm.params.ParamsException;
import com.sap.engine.services.dc.cm.params.ParamsFactory;
import com.sap.engine.services.dc.cm.params.ParamsFactoryException;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public class RemoteParamManagerImpl implements ParamManager {

	final ParamManager pManager;

	RemoteParamManagerImpl() throws RemoteException {
		this(null);
	}

	RemoteParamManagerImpl(ConfigurationHandlerFactory cfgHandlerFactory)
			throws RemoteException {
		super();
		try {
			pManager = ParamsFactory.getInstance().createParamManager(
					cfgHandlerFactory);
		} catch (ParamsFactoryException pfe) {
			throw new RemoteException("ASJ.dpl_dc.003457"
					+ pfe.getMessage(), pfe);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#addParam(com.sap.engine
	 * .services.dc.cm.params.Param)
	 */
	public void addParam(Param param) throws ParamAlreadyExistsException,
			ParamsException {
		pManager.addParam(param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#addParams(com.sap.engine
	 * .services.dc.cm.params.Param[])
	 */
	public void addParams(Param[] params) throws ParamAlreadyExistsException,
			ParamsException {
		pManager.addParams(params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#removeParam(com.sap
	 * .engine.services.dc.cm.params.Param)
	 */
	public void removeParam(Param param) throws ParamNotFoundException,
			ParamsException {
		pManager.removeParam(param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#removeParams(com.sap
	 * .engine.services.dc.cm.params.Param[])
	 */
	public void removeParams(Param[] params) throws ParamNotFoundException,
			ParamsException {
		pManager.removeParams(params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#updateParam(com.sap
	 * .engine.services.dc.cm.params.Param)
	 */
	public void updateParam(Param param) throws ParamNotFoundException,
			ParamsException {
		pManager.updateParam(param);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#updateParams(com.sap
	 * .engine.services.dc.cm.params.Param[])
	 */
	public void updateParams(Param[] params) throws ParamNotFoundException,
			ParamsException {
		pManager.updateParams(params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.params.ParamManager#getAllParams()
	 */
	public Param[] getAllParams() throws ParamsException {
		return pManager.getAllParams();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#getParamByName(java
	 * .lang.String)
	 */
	public Param getParamByName(String paramName)
			throws ParamNotFoundException, ParamsException {
		return pManager.getParamByName(paramName);
	}

}
