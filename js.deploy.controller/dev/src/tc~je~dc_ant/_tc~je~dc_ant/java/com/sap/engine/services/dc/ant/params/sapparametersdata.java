/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.ant.params;

import java.util.Collection;

import com.sap.engine.services.dc.ant.SAPJ2EEEngine;

/**
 * 
 * This class represents parameters data passed from the parameters task.
 * 
 * @author Dimitar Dimitrov
 * @author Boris Savov( i030791 )
 * @author Todor Stoitsev
 * @version 1.0
 * @since 7.1
 * 
 */
public final class SAPParametersData {

	private final Collection params;

	private final SAPJ2EEEngine engine;

	private boolean remove;

	private final String toString;

	SAPParametersData(SAPJ2EEEngine engine, Collection params, boolean remove) {
		this.engine = engine;
		this.params = params;
		this.toString = this.generateToString();
		this.remove = remove;
	}

	/**
	 * @return Returns the parameters.
	 */
	public Collection getParams() {
		return this.params;
	}

	/**
	 * @return Returns the engine.
	 */
	public SAPJ2EEEngine getEngine() {
		return this.engine;
	}

	/**
	 * @return Returns the remove option.
	 */
	public boolean isRemove() {
		return this.remove;
	}

	private String generateToString() {
		return "\nengine: '" + this.engine + "'\nparams: '" + this.params
				+ "'\nremove: '" + this.remove + "'";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.toString;
	}

}
