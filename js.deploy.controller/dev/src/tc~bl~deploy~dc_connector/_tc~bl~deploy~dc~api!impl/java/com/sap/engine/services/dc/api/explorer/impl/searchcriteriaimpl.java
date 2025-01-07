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

import java.util.ArrayList;

import com.sap.engine.services.dc.api.explorer.RepositoryExplorerException;
import com.sap.engine.services.dc.api.explorer.SearchClause;
import com.sap.engine.services.dc.api.explorer.SearchCriteria;
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
final class SearchCriteriaImpl implements SearchCriteria {
	// private final com.sap.engine.services.dc.repo.explorer.SearchCriteria
	// remoteSearchCriteria;
	private final ArrayList clauses;

	SearchCriteriaImpl(RemoteRepositoryExplorerFactory remoteRepoExplorerFactory)
			throws RepositoryExplorerException {
		this.clauses = new ArrayList();
		/*
		 * try { remoteSearchCriteria =
		 * remoteRepoExplorerFactory.createSearchCriteria(); } catch
		 * (com.sap.engine
		 * .services.dc.repo.explorer.RepositoryExploringException e) {
		 * DALog.traceError(""+e.getMessage()); throw new
		 * RepositoryExploringException(e.getMessage(),e); }
		 */
	}

	public SearchClause[] getClauses() {
		SearchClause[] ret = new SearchClause[this.clauses.size()];
		ret = (SearchClause[]) this.clauses.toArray(ret);
		return ret;
	}

	public void addClause(SearchClause clause) {
		if (clause instanceof SearchClauseImpl) {
			// remoteSearchCriteria.addClause(((SearchClauseImpl)clause).
			// getRemoteSearchClause());
			this.clauses.add(clause);
		} else {
			throw new ClassCastException(
					"[ERROR CODE DPL.DCAPI.1054] Argument SearchClause must be created by Factory.");
		}
	}

	/*
	 * com.sap.engine.services.dc.repo.explorer.SearchCriteria
	 * getRemoteSearchCriteria(){ return remoteSearchCriteria; }
	 */
}
