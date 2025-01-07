package com.sap.engine.services.dc.repo.explorer;

import java.rmi.Remote;

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
public interface RemoteRepositoryExplorerFactory extends Remote {

	public RepositoryExplorer createRepositoryExplorer()
			throws RepositoryExploringException;

	public SearchCriteria createSearchCriteria()
			throws RepositoryExploringException;

	public SearchClause createSearchClause(String key, String value)
			throws RepositoryExploringException;

	public SearchClause createSearchClause(String key, String value,
			SearchClauseTarget target) throws RepositoryExploringException;

}
