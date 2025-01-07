package com.sap.engine.services.dc.repo.explorer;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface SimpleSearchClause extends SearchClause {

	public static final String NAME_KEY = "name";
	public static final String VENDOR_KEY = "vendor";
	public static final String LOCATION_KEY = "location";
	public static final String VERSION_KEY = "version";
	public static final String SOFTWARE_TYPE_KEY = "st";

	public String getKey();

	public String getValue();

	public SearchClauseTarget getTarget();

}
