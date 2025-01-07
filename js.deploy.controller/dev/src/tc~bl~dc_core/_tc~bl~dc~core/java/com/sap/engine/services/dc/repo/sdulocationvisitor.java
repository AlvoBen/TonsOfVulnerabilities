package com.sap.engine.services.dc.repo;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-20
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface SduLocationVisitor {

	public void visit(SdaLocation sdaLocation);

	public void visit(ScaLocation scaLocation);

}
