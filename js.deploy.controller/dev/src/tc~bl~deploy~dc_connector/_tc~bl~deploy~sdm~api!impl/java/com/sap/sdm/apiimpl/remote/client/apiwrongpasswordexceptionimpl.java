package com.sap.sdm.apiimpl.remote.client;

import com.sap.sdm.api.remote.WrongPasswordException;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
class APIWrongPasswordExceptionImpl extends WrongPasswordException {

	APIWrongPasswordExceptionImpl(String msg) {
		super(msg);
	}
}
