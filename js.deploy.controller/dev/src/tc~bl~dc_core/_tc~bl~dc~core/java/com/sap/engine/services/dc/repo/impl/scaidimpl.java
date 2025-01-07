package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.SduIdVisitor;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-1
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class ScaIdImpl extends SduIdImpl implements ScaId {

	private static final long serialVersionUID = -2335053992129814744L;

	ScaIdImpl(String name, String vendor) {
		super(name, vendor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduId#accept(com.sap.engine.services.
	 * dc.repo.SduIdVisitor)
	 */
	public void accept(SduIdVisitor visitor) {
		visitor.visit(this);
	}

}
