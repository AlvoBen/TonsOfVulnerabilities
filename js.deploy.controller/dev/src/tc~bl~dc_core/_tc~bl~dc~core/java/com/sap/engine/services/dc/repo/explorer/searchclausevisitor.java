package com.sap.engine.services.dc.repo.explorer;

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
public interface SearchClauseVisitor {

	public abstract void visit(SimpleSearchClause clause);

	public abstract void visit(AndSearchClause andClause);

	public abstract void visit(OrSearchClause orClause);

}
