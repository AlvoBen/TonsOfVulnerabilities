/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.repo.version;

import java.util.HashSet;
import java.util.Set;

import com.sap.engine.services.dc.repo.Sca;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SduVisitor;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
class ScaFilterSduVisitor implements SduVisitor {

	final Set scas = new HashSet();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduVisitor#visit(com.sap.engine.services
	 * .dc.repo.Sda)
	 */
	public void visit(Sda sda) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.SduVisitor#visit(com.sap.engine.services
	 * .dc.repo.Sca)
	 */
	public void visit(Sca sca) {
		scas.add(sca);
	}

	public Set getScas() {
		return scas;
	}

}
