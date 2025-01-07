package com.sap.engine.services.dc.repo;

import java.util.Collection;

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
public interface ScaLocation extends SduLocation {

	public Sca getSca();

	/**
	 * Returns the <code>SduLocation</code>s of possible grouped
	 * <code>Sdu</code>s.
	 * 
	 * @return a non-<code>null</code> <code>Collection</code> of
	 *         <code>SduLocation</code>s
	 */
	public Collection getGroupedLocations();

}
