package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.services.dc.repo.Repository;
import com.sap.engine.services.dc.repo.RepositoryFactory;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-21
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public class RepositoryFactoryImpl extends RepositoryFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.RepositoryFactory#createRepository()
	 */
	public Repository createRepository() {
		return new RepositoryImpl();
	}
}
