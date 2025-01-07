package com.sap.engine.services.dc.repo;

import com.sap.engine.frame.core.configuration.Configuration;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-2
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface SduRepoLocation {

	public String getLocation();

	public Configuration getConfiguration();

	public Sdu getSdu();

	public void setSdu(Sdu sdu);

	public void accept(SduRepoLocationVisitor visitor);

}
