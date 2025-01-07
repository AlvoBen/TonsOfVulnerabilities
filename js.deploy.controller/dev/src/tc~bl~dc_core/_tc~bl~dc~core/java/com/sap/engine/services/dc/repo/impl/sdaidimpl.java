package com.sap.engine.services.dc.repo.impl;

import com.sap.engine.services.dc.repo.SdaId;
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
public final class SdaIdImpl extends SduIdImpl implements SdaId {

	private static final long serialVersionUID = 6326250964749115913L;

	public SdaIdImpl(final String name, final String vendor) {
		super(name, vendor);
	}

	public SdaIdImpl(final String vendorAndName) {
		super(vendorAndName);
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
