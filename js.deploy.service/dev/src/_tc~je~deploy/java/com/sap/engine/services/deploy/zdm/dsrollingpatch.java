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
package com.sap.engine.services.deploy.zdm;

import com.sap.engine.services.deploy.server.dpl_info.module.ApplicationName;
import com.sap.engine.services.deploy.zdm.utils.ApplicationComponent;

/**
 * Enables the &quot zero down time &quot concept based on the rolling approach
 * for online applications at instance level, which is the smallest entity in
 * the cluster. The responsibility for synchronizing the whole cluster is
 * delegated to the upper layer, which uses this interface.
 * 
 * @author Anton Georgiev
 * @version 1.00
 * @since 7.10
 * @deprecated The interface will only be used for proofing the concept in the
 *             prototyping phase. It will not be shipped to external customers
 *             and is not considered as public interface, without reviewing it.
 */
public interface DSRollingPatch extends java.rmi.Remote {

	/**
	 * Updates the application in the database and in the instance.
	 * 
	 * @param applicationComponent
	 *            , which is an <code>ApplicationComponent</code> and describes
	 *            the new application version.
	 * @return a <code>DSRollingResult</code>, which describes how the state of
	 *         the instance was changed as a result of calling this method.
	 *         Based on the result the method caller is responsible to decide is
	 *         the new state of the instance acceptable or not. It cannot be
	 *         null.
	 * @throws DSRollingException
	 *             can be thrown only if the update process cannot start and the
	 *             state of the instance before invoking the method and after
	 *             throwing the exception is the same, otherwise a
	 *             <code>DSRollingResult</code> will be returned.
	 * @deprecated
	 */
	public DSRollingResult updateInstanceAndDB(
			ApplicationComponent applicationComponent)
			throws DSRollingException;

	/**
	 * Synchronizes the application in the instance with the database.
	 * 
	 * @param applicationName
	 *            , which is an <code>ApplicationName</code> with name and
	 *            vendor.
	 * @return a <code>DSRollingResult</code>, which describes how the state of
	 *         the instance was changed as a result of calling this method.
	 *         Based on the result the method caller is responsible to decide is
	 *         the new state of the instance acceptable or not. It cannot be
	 *         null.
	 * @throws DSRollingException
	 *             can be thrown only if the update process cannot start and the
	 *             state of the instance before invoking the method and after
	 *             throwing the exception is the same, otherwise a
	 *             <code>DSRollingResult</code> will be returned.
	 * @deprecated
	 */
	public DSRollingResult syncInstanceWithDB(ApplicationName applicationName)
			throws DSRollingException;

}
