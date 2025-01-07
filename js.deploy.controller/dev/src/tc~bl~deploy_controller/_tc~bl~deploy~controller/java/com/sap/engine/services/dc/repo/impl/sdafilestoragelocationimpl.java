package com.sap.engine.services.dc.repo.impl;

import java.io.File;

import com.sap.engine.services.dc.repo.SdaFileStorageLocation;
import com.sap.engine.services.dc.repo.SduFileStorageLocationVisitor;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-1
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SdaFileStorageLocationImpl extends SduFileStorageLocationImpl
		implements SdaFileStorageLocation {

	public SdaFileStorageLocationImpl(String sdaFileStorageLocation) {
		this(sdaFileStorageLocation, null);
	}

	SdaFileStorageLocationImpl(String sdaFileStorageLocation, File sduFile) {
		super(sdaFileStorageLocation, sduFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduFileStorageLocation#accept(com.sap
	 * .engine.services.dc.repo.SduFileStorageLocationVisitor)
	 */
	public void accept(SduFileStorageLocationVisitor visitor) {
		visitor.visit(this);
	}

}
