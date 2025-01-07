package com.sap.engine.services.dc.repo;

import java.io.File;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-24
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface SduFileStorageLocation {

	public File getSduFile();

	public void setSduFile(File sduFile);

	public String getLocation();

	/**
	 * Accepts a concrete <code>SduFileStorageLocationVisitor</code> visitor.
	 * 
	 * @param visitor
	 *            specifies the concrete
	 *            <code>SduFileStorageLocationVisitor</code> visitor.
	 */
	public void accept(SduFileStorageLocationVisitor visitor);

}
