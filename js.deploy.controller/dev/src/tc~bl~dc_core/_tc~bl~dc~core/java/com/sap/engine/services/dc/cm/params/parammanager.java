package com.sap.engine.services.dc.cm.params;

import java.rmi.Remote;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-4
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface ParamManager extends Remote {

	public void addParam(Param param) throws ParamAlreadyExistsException,
			ParamsException;

	public void addParams(Param[] params) throws ParamAlreadyExistsException,
			ParamsException;

	public void removeParam(Param param) throws ParamNotFoundException,
			ParamsException;

	public void removeParams(Param[] params) throws ParamNotFoundException,
			ParamsException;

	public void updateParam(Param param) throws ParamNotFoundException,
			ParamsException;

	public void updateParams(Param[] params) throws ParamNotFoundException,
			ParamsException;

	public Param[] getAllParams() throws ParamsException;

	public Param getParamByName(String paramName)
			throws ParamNotFoundException, ParamsException;

}
