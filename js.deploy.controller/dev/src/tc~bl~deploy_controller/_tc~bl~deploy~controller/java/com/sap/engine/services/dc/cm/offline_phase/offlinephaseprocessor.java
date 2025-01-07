package com.sap.engine.services.dc.cm.offline_phase;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-22
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface OfflinePhaseProcessor {

	public void process() throws OfflinePhaseProcessException;

}
