/*
 * Created on 2005-7-6 by radoslav-i
 */
package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.sdm.api.remote.validateresults.ValidateResultType;

/**
 * @author radoslav-i
 */
class P4ValidateResultTypeImpl implements ValidateResultType {

	private ValidateResultType type = null;

	P4ValidateResultTypeImpl(ValidateResultType type) {
		AssertionCheck.checkForNullArg(getClass(), "<init>", type);

		this.type = type;
	}

	public ValidateResultType getType() {
		return type;
	}

}
