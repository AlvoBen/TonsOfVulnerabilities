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
package com.sap.engine.services.dc.api.model;

import java.util.Set;

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Factory providing mechanism for creating Sda, Sca and Dependancy objects.
 * </DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date:</B></DT>
 * <DD>2004-11-11</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
public interface ModelFactory {
	/**
	 * creates a single <code>Sda</code> regarding provided arguments
	 * 
	 * @param name
	 *            key name
	 * @param vendor
	 *            key vendor
	 * @param location
	 *            sda location
	 * @param version
	 *            sda version
	 * @param softwareType
	 *            sda software type
	 * @param componentElementXML
	 *            component element xml
	 * @param csnComponent
	 *            csn component
	 * @param dependencies
	 *            set with the <code>Dependency</code> objects
	 * @param scaId
	 *            the Sca which contains the SDA which has to be created
	 * @return
	 */
	public Sda createSda(String name, String vendor, String location,
			Version version, SoftwareType softwareType,
			String componentElementXML, String csnComponent, Set dependencies,
			Set dependingFrom, ScaId scaId);

	/**
	 * creates a single <code>Sca</code> regarding provided arguments
	 * 
	 * @param name
	 *            key name
	 * @param vendor
	 *            key vendor
	 * @param location
	 *            sda location
	 * @param version
	 *            sda version
	 * @param componentElementXML
	 *            component element xml
	 * @param csnComponent
	 *            csn component
	 * @param sdas
	 *            set with <cide>Sda</code> objects included in this sca
	 * @return
	 */
	public Sca createSca(String name, String vendor, String location,
			Version version, String componentElementXML, String csnComponent,
			Set sdas, Set originalSdas, Set notDeployedSdas);

	/**
	 * creates a single <code>Dependency</code> regarding provided arguments.
	 * 
	 * @param name
	 *            sda name
	 * @param vendor
	 *            sda vendor name
	 * @return
	 */
	public Dependency createDependency(String name, String vendor);

	/**
	 * Creates components <code>SoftwareType</code>.
	 * 
	 * @param name
	 * @param description
	 * @return
	 */
	public SoftwareType createSoftwareType(String name, String description);

	/**
	 * Creates components <code>SoftwareType</code>.
	 * 
	 * @param name
	 * @param subTypeName
	 * @param description
	 * @return
	 */
	public SoftwareType createSoftwareType(String name, String subTypeName,
			String description);

	/**
	 * Creates component version. The method is for internal purposes and should
	 * not be used in general.
	 * 
	 * @param versionString
	 * @return
	 */
	public Version createVersion(String versionString);

	/**
	 * Creates <code>SdaId</code>
	 * 
	 * @param name
	 *            component key name
	 * @param vendor
	 *            component vendor
	 * @return new <code>SdaId</code> instance.
	 */
	public SdaId createSdaId(String name, String vendor);

	/**
	 * Creates <code>ScaId<code>
	 * 
	 * @param name
	 *            component key name
	 * @param vendor
	 *            component vendor
	 * @return new <code>ScaId<code> instance.
	 */
	public ScaId createScaId(String name, String vendor);
}
