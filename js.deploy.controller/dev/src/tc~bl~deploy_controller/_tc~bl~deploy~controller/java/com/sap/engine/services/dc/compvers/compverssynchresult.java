package com.sap.engine.services.dc.compvers;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-26
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface CompVersSynchResult {

	public int getTotal();

	public int getFailed();

	public int getSuccesses();

	public CompVersSynchStatus getCompVersSynchStatus();

	public String getResultText();

}
