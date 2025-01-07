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
package com.sap.engine.services.dc.api.explorer.impl;

import com.sap.engine.services.dc.api.explorer.RepositoryExplorerException;
import com.sap.engine.services.dc.api.explorer.SearchClause;
import com.sap.engine.services.dc.api.explorer.SearchClauseTarget;
import com.sap.engine.services.dc.api.session.Session;
import com.sap.engine.services.dc.repo.explorer.RemoteRepositoryExplorerFactory;

/**
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-26
 * 
 * @author Boris Savov
 * @version 1.0
 * @since 7.0
 */
final class SearchClauseImpl implements SearchClause {

	private final String key;
	private final String value;
	private final Session session;
	// private final com.sap.engine.services.dc.repo.explorer.SearchClause
	// remoteSearchClause;
	private final SearchClauseTarget searchClauseTarget;

	SearchClauseImpl(Session session, String key, String value,
			RemoteRepositoryExplorerFactory remoteRepoExplorerFactory)
			throws RepositoryExplorerException {
		this(session, key, value, null, remoteRepoExplorerFactory);
	}

	SearchClauseImpl(Session session, String key, String value,
			SearchClauseTarget searchClauseTarget,
			RemoteRepositoryExplorerFactory remoteRepoExplorerFactory)
			throws RepositoryExplorerException {
		this.key = key;
		this.value = value;
		this.searchClauseTarget = searchClauseTarget;
		this.session = session;
		/*
		 * try { if(searchClauseTarget!=null){ remoteSearchClause =
		 * remoteRepoExplorerFactory.createSearchClause( key,value,
		 * RepositoryExplorerMapper.mapSearchClauseTarget(searchClauseTarget));
		 * } else { remoteSearchClause =
		 * remoteRepoExplorerFactory.createSearchClause(key,value); } } catch
		 * (com
		 * .sap.engine.services.dc.repo.explorer.RepositoryExploringException e)
		 * { DALog.traceError("SearchClauseImpl::ctor,cause="+e.getMessage());
		 * throw new RepositoryExploringException(e.getMessage(), e); }
		 */
	}

	public String getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}

	public SearchClauseTarget getTarget() {
		return this.searchClauseTarget;
	}
	/*
	 * com.sap.engine.services.dc.repo.explorer.SearchClause
	 * getRemoteSearchClause(){ return remoteSearchClause; }
	 */
}