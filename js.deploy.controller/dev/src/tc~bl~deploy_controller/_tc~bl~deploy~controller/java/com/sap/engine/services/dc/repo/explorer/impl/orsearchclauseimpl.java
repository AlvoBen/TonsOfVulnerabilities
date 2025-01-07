package com.sap.engine.services.dc.repo.explorer.impl;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.explorer.OrSearchClause;
import com.sap.engine.services.dc.repo.explorer.SearchClause;
import com.sap.engine.services.dc.repo.explorer.SearchClauseTarget;
import com.sap.engine.services.dc.repo.explorer.SearchClauseVisitor;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class OrSearchClauseImpl implements OrSearchClause {

	// $JL-SER$
	private final Set searchClauses;

	OrSearchClauseImpl() {
		this.searchClauses = new LinkedHashSet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.explorer.OrSearchClause#addSearchClause
	 * (com.sap.engine.services.dc.repo.explorer.SearchClause)
	 */
	public void addSearchClause(SearchClause clause) {
		this.searchClauses.add(clause);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.explorer.SearchClause#isAccepted(com.
	 * sap.engine.services.dc.repo.Sdu)
	 */
	public boolean isAccepted(Sdu sdu) {
		for (Iterator iter = this.searchClauses.iterator(); iter.hasNext();) {
			final SearchClause clause = (SearchClause) iter.next();
			if (clause.isAccepted(sdu)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.explorer.SearchClause#accept(com.sap.
	 * engine.services.dc.repo.explorer.SearchClauseVisitor)
	 */
	public void accept(SearchClauseVisitor visitor) {
		visitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.explorer.SearchClause#getKey()
	 */
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.explorer.SearchClause#getValue()
	 */
	public String getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.explorer.SearchClause#getTarget()
	 */
	public SearchClauseTarget getTarget() {
		// TODO Auto-generated method stub
		return null;
	}

}
