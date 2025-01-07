package com.sap.engine.services.dc.repo.explorer;

import java.io.Serializable;
import java.util.Set;

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
public interface SearchCriteria extends Serializable {

	public Set getClauses();

	public void addClause(SearchClause clause);

}
