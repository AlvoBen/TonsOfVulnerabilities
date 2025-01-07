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
package com.sap.engine.services.dc.api.explorer;

import com.sap.engine.services.dc.api.model.Sca;
import com.sap.engine.services.dc.api.model.ScaId;
import com.sap.engine.services.dc.api.model.Sda;
import com.sap.engine.services.dc.api.model.Sdu;

/**
 * <DL>
 * <DT><B>Title:</B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description:</B></DT>
 * <DD>Serves to find SDAs, SCAs and archives of both types that satisfy
 * specific criteria..</DD>
 * <DT><B>Usage:</B></DT>
 * <DD>RepositoryExplorerFactory repositoryExplorerFactory =
 * componentManager.getRepositoryExplorerFactory();</DD>
 * <DD>RepositoryExplorer repositoryExplorer =
 * repositoryExplorerFactory.createRepositoryExplorer();</DD>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company:</B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date:</B></DT>
 * <DD>2004-11-26</DD>
 * </DL>
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 * @see com.sap.engine.services.dc.api.deploy.DeployProcessor
 */
public interface RepositoryExplorer {
	/**
	 * Search for Sda in the repository regarding provided <code>name</code> and
	 * <code>vendor</code>.
	 * 
	 * @param name
	 *            of the SDA
	 * @param vendor
	 *            of the SDA
	 * @return either found SDA component or &quot;null&quot; if the component
	 *         is not deployed.
	 * @throws RepositoryExplorerException
	 */
	public Sda findSda(String name, String vendor)
			throws RepositoryExplorerException;

	/**
	 * Search for Sca in the repository regarding provided <code>name</code> and
	 * <code>vendor</code>.
	 * 
	 * @param name
	 *            of the SCA
	 * @param vendor
	 *            of the SCA
	 * @return either found SCA component or &quot;null&quot; if the component
	 *         is not deployed.
	 * @throws RepositoryExplorerException
	 */
	public Sca findSca(String name, String vendor)
			throws RepositoryExplorerException;

	/**
	 * Returns array containing all available in the repository <code>Sdu</code>
	 * s.
	 * 
	 * @return array containing all available in the repository <code>Sdu</code>
	 *         s.
	 * @throws RepositoryExplorerException
	 */
	public Sdu[] findAll() throws RepositoryExplorerException;

	/**
	 * Retrieves only the top level components from the repository. For top
	 * level components is considered all &quot;SCA&quot; components plus all
	 * &quot;SDA&quot; components which are not contained from any SCA.
	 * 
	 * @return array with all top level components.
	 * @throws RepositoryExplorerException
	 */
	public Sdu[] fetchTopLevelComponents() throws RepositoryExplorerException;

	/**
	 * Returns all contained &quot;SDA&quot; in the &quot;SCA&quot;.
	 * 
	 * @param scaId
	 *            the id of the containing SCA
	 * @return all contained &quot;SDA&quot; in the &quot;SCA&quot; with the
	 *         given <code>scaId</code>.
	 * @throws RepositoryExplorerException
	 */
	public Sda[] fetchChildren(ScaId scaId) throws RepositoryExplorerException;

	// public Sdu[] find(SearchCriteria searchCriteria) throws
	// RepositoryExploringException;
}
