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
package com.sap.engine.services.deploy.server.utils;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import com.sap.engine.services.deploy.container.ContainerInterface;
import com.sap.engine.services.deploy.exceptions.ServerDeploymentException;
import com.sap.engine.services.deploy.server.ExceptionConstants;
import com.sap.engine.services.deploy.server.cache.containers.Containers;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.Resource;

/**
 * Should be used for object validation.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public final class ValidateUtils {

	public static void nullValidator(Object obj, String name)
			throws NullPointerException {
		if (obj == null) {
			throw new NullPointerException(
					"ASJ.dpl_ds.006103 The given '" + name
							+ "' is NULL.");
		}
	}

	public static void missingDCinDIValidator(DeploymentInfo dInfo,
		String operation, ContainerInterface concernedContainers[])
		throws ServerDeploymentException {
		nullValidator(dInfo, "DeploymentInfo");
		final Hashtable cName_cData = dInfo.getCNameAndCData();
		if (cName_cData == null || cName_cData.size() == 0) {
			ServerDeploymentException sde = new ServerDeploymentException(
				ExceptionConstants.UNEXPECTED_EXCEPTION_OCCURRED,
				new String[] { "validating application " +
					dInfo.getApplicationName() + "." + DSConstants.EOL_TAB +
					"The " + dInfo.getApplicationName() + 
					" application was processed from " +
					Convertor.toString(concernedContainers, "") +
					" containers, but none of them returned information about deployed components." +
					DSConstants.EOL_TAB	+ "The registered containers in this moment were " +
					Convertor.toString(Containers.getInstance().getNames(), "")	+ 
					"."	+ DSConstants.EOL_TAB + "Possible reasons : " +
					DSConstants.EOL_TAB	+ "1.Empty or incorrect application, which is not recognized by registered containers." +
					DSConstants.EOL_TAB	+ "2.An AS Java service, which is providing a container, is stopped or not deployed." +
					DSConstants.EOL_TAB	+ "3.The containers, which processed it, are not implemented correctly, because the application was deployed or started initially" +
					", but containers didn't return information about deployed components in the application deployment info" });
			sde.setMessageID("ASJ.dpl_ds.005082");
			throw sde;
		}
		final Enumeration cnEnum = cName_cData.keys();
		String cName = null;
		ContainerData cData = null;
		Collection<Resource> dComps = null;
		while (cnEnum.hasMoreElements()) {
			cName = (String) cnEnum.nextElement();
			cData = (ContainerData) cName_cData.get(cName);
			dComps = cData.getProvidedResources();
			if (dComps == null || dComps.size() == 0) {
				ServerDeploymentException sde = new ServerDeploymentException(
						ExceptionConstants.DI_NO_DEPL_COMP, new String[] {
								dInfo.getApplicationName(), operation, cName });
				sde.setMessageID("ASJ.dpl_ds.005205");
				throw sde;
			}
		}
	}
}