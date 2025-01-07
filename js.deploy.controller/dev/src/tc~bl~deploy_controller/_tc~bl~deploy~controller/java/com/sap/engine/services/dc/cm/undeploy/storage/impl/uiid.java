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
package com.sap.engine.services.dc.cm.undeploy.storage.impl;

import com.sap.engine.services.dc.cm.undeploy.UndeployItemId;

/**
 * Wrapps the <code>UndeployItemId</code>.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
class UiId {

	private final UndeployItemId uiId;
	private final int hashCode;
	private final String toString;

	UiId(UndeployItemId uiId) {
		this.uiId = uiId;
		this.hashCode = 23 + uiId.hashCode() + uiId.getIdCount();
		this.toString = uiId.toString() + "_" + uiId.getIdCount();
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		final UiId uiIdWrapper = (UiId) obj;

		if (!this.uiId.equals(uiIdWrapper.getUiId())) {
			return false;
		}

		if (this.uiId.getIdCount() != uiIdWrapper.getUiId().getIdCount()) {
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

	public UndeployItemId getUiId() {
		return uiId;
	}

}
