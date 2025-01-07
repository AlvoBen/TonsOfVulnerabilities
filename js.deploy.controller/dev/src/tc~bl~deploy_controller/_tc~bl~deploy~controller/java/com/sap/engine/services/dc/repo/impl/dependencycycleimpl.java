package com.sap.engine.services.dc.repo.impl;

import java.util.Collection;

import com.sap.engine.services.dc.repo.DependencyCycle;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-15
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
class DependencyCycleImpl implements DependencyCycle {

	private static final long serialVersionUID = 2399896093933075906L;

	private final Collection items;// $JL-SER$

	DependencyCycleImpl(Collection items) {
		this.items = items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repository.DependencyCycle#getComponents()
	 */
	public Collection getItems() {
		return items;
	}

}
