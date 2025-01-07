package com.sap.engine.services.dc.repo.explorer;

import java.rmi.Remote;

import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.Sdu;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-25
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface RepositoryExplorer extends Remote {

	public Sda findSda(String name, String vendor)
			throws RepositoryExploringException;

	public Sca findSca(String name, String vendor)
			throws RepositoryExploringException;

	public Sdu[] findAll() throws RepositoryExploringException;

	public Sdu[] find(SearchCriteria searchCriteria)
			throws RepositoryExploringException;

}
