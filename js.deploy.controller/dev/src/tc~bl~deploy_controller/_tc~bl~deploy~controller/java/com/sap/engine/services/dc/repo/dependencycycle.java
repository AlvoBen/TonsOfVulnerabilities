package com.sap.engine.services.dc.repo;

import java.io.Serializable;
import java.util.Collection;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-15
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public interface DependencyCycle extends Serializable {

	/**
	 * @return <code>Collection</code> with components which form the cyclic
	 *         dependency.
	 */
	public Collection getItems();

}
