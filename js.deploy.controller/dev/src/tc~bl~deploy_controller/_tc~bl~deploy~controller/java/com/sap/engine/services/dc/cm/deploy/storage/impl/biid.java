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
package com.sap.engine.services.dc.cm.deploy.storage.impl;

import com.sap.engine.services.dc.cm.deploy.BatchItemId;

/**
 * Wrapps the <code>BatchItemId</code>.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
class BiId {

	private final BatchItemId biId;
	private final int hashCode;
	private final String toString;

	BiId(BatchItemId biId) {
		this.biId = biId;
		this.hashCode = 23 + biId.hashCode() + biId.getIdCount();
		this.toString = biId.toString() + "_" + biId.getIdCount();
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		final BiId biIdWrapper = (BiId) obj;

		if (!this.biId.equals(biIdWrapper.getBiId())) {
			return false;
		}

		if (this.biId.getIdCount() != biIdWrapper.getBiId().getIdCount()) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		return this.hashCode;
	}

	public String toString() {
		return this.toString;
	}

	public BatchItemId getBiId() {
		return biId;
	}

}
