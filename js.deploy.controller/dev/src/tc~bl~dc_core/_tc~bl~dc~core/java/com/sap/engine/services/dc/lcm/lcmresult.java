package com.sap.engine.services.dc.lcm;

import java.io.Serializable;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2005-3-27
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public interface LCMResult extends Serializable {

	public LCMResultStatus getLCMResultStatus();

	public String getDescription();

	public String toString();

}
