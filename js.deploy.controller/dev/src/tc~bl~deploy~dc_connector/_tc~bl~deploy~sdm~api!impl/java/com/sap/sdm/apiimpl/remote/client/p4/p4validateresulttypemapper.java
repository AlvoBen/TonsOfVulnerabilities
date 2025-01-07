/*
 * Created on 2005-7-4 by radoslav-i
 */
package com.sap.sdm.apiimpl.remote.client.p4;

import com.sap.engine.services.dc.api.deploy.ValidationStatus;
import com.sap.sdm.api.remote.validateresults.Error;
import com.sap.sdm.api.remote.validateresults.Success;
import com.sap.sdm.api.remote.validateresults.ValidateResultType;

/**
 * @author radoslav-i
 */
final class P4ValidateResultTypeMapper {

	static ValidateResultType map(
			com.sap.engine.services.dc.api.deploy.ValidationResult dcValidationResult) {
		final ValidationStatus dcValidationStatus = dcValidationResult
				.getValidationStatus();

		if (ValidationStatus.SUCCESS.equals(dcValidationStatus)) {
			return new P4ValidateResultTypeImpl(new Success() {
			}).getType();
		} else if (ValidationStatus.ERROR.equals(dcValidationStatus)) {
			return new P4ValidateResultTypeImpl(new Error() {
			}).getType();
		} else {
			final String fatalErrMsg = "Unknown validate status'"
					+ dcValidationStatus + "' detected!";
			throw new IllegalStateException(fatalErrMsg);
		}
	}

	private P4ValidateResultTypeMapper() {
	}
}
