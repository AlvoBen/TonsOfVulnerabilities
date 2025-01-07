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
package com.sap.engine.services.dc.api.params;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>provides base functionality to operate with parameters.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2004-11-9</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public interface ParamsProcessor {
	/**
	 * creates new parameter
	 * 
	 * @param paramName
	 *            parameters name
	 * @param paramValue
	 *            parameters value
	 * @return new created parameter
	 */
	public Param createParam(String paramName, String paramValue);

	/**
	 * adds parameter to repository
	 * 
	 * @param param
	 *            a parameter which should be added to the repository
	 * @throws ParamAlreadyExistsException
	 *             in case of try to add parameter which already exists in
	 *             repository
	 * @throws ParamsException
	 */
	public void addParam(Param param) throws ParamAlreadyExistsException,
			ParamsException;

	/**
	 * adds parameter set to repository
	 * 
	 * @param params
	 *            parameters array to add to the repository
	 * @throws ParamAlreadyExistsException
	 *             in case of try to add parameter which already exists in
	 *             repository
	 * @throws ParamsException
	 */
	public void addParams(Param[] params) throws ParamAlreadyExistsException,
			ParamsException;

	/**
	 * retrieve array with all parameters in the repository
	 * 
	 * @return list of parameters in repository
	 * @throws ParamsException
	 */
	public Param[] getAllParams() throws ParamsException;

	/**
	 * retrieves named parameter from the resository
	 * 
	 * @param paramName
	 *            parameter name
	 * @return retrieved parameter from the repository
	 * @throws ParamNotFoundException
	 *             in case if there is no such parameter in repository
	 * @throws ParamsException
	 */
	public Param getParamByName(String paramName)
			throws ParamNotFoundException, ParamsException;

	/**
	 * removes named parameter from the repository
	 * 
	 * @param param
	 *            parameter to remove
	 * @throws ParamNotFoundException
	 *             in case if there is no such parameter in repository
	 * @throws ParamsException
	 */
	public void removeParam(Param param) throws ParamNotFoundException,
			ParamsException;

	/**
	 * removes list of parameters
	 * 
	 * @param params
	 *            list of parameters which should be removed
	 * @throws ParamNotFoundException
	 *             in case if there is no available at least one of the
	 *             parameters from the given array in repository
	 * @throws ParamsException
	 */
	public void removeParams(Param[] params) throws ParamNotFoundException,
			ParamsException;

	/**
	 * updates parameter
	 * 
	 * @param param
	 *            parameter to update
	 * @throws ParamNotFoundException
	 *             in case if there is no such parameter in repository
	 * @throws ParamsException
	 */
	public void updateParam(Param param) throws ParamNotFoundException,
			ParamsException;

	/**
	 * updates list of parameters
	 * 
	 * @param params
	 *            list of parameters to update
	 * @throws ParamNotFoundException
	 *             in case if there is no available at least one of the
	 *             parameters from the given array in repository
	 * @throws ParamsException
	 */
	public void updateParams(Param[] params) throws ParamNotFoundException,
			ParamsException;
}