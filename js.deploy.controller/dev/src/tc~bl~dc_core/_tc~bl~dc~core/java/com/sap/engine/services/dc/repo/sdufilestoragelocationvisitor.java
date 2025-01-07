package com.sap.engine.services.dc.repo;

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
public interface SduFileStorageLocationVisitor {

	public void visit(SdaFileStorageLocation location);

	public void visit(ScaFileStorageLocation location);

}
