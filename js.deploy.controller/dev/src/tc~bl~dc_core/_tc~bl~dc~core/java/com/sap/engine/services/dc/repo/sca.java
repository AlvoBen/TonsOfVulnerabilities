package com.sap.engine.services.dc.repo;

import java.util.Set;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-17
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface Sca extends Sdu {

	public Set getSdaIds();

	public Set getOrigSdaIds();

	public void addSdaId(SdaId sdaId);

	public void removeSdaId(SdaId sdaId);

	public void addOrigSdaId(SdaId sdaId);

}
