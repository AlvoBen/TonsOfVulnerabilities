package com.sap.engine.services.dc.repo.explorer;

import java.io.Serializable;

import com.sap.engine.services.dc.repo.Sdu;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-11-25
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface SearchClause extends Serializable {

	public boolean isAccepted(Sdu sdu);

	public void accept(SearchClauseVisitor visitor);

	/**
	 * 
	 * @deprecated
	 */
	public static final String NAME_KEY = "name";

	/**
	 * 
	 * @deprecated
	 */
	public static final String VENDOR_KEY = "vendor";

	/**
	 * 
	 * @deprecated
	 */
	public static final String LOCATION_KEY = "location";

	/**
	 * 
	 * @deprecated
	 */
	public static final String VERSION_KEY = "version";

	/**
	 * 
	 * @deprecated
	 */
	public String getKey();

	/**
	 * 
	 * @deprecated
	 */
	public String getValue();

	/**
	 * 
	 * @deprecated
	 */
	public SearchClauseTarget getTarget();

}
