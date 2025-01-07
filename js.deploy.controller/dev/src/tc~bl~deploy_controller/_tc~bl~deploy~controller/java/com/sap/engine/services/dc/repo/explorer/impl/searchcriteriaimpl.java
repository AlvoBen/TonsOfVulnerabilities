package com.sap.engine.services.dc.repo.explorer.impl;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.services.dc.repo.explorer.SearchClause;
import com.sap.engine.services.dc.repo.explorer.SearchCriteria;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-26
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SearchCriteriaImpl implements SearchCriteria {

	private static final long serialVersionUID = -4845107253480764434L;

	private final Set clauses;// $JL-SER$

	SearchCriteriaImpl() {
		this.clauses = new HashSet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.explorer.SearchCriteria#getClauses()
	 */
	public Set getClauses() {
		return this.clauses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.explorer.SearchCriteria#addClause(com
	 * .sap.engine.services.dc.repo.explorer.SearchClause)
	 */
	public void addClause(SearchClause clause) {
		this.clauses.add(clause);
	}

}
