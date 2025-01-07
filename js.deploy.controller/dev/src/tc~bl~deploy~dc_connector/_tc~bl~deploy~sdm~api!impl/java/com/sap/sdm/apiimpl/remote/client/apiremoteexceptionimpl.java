package com.sap.sdm.apiimpl.remote.client;

import com.sap.sdm.api.remote.RemoteException;

/**
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 * 
 */
public class APIRemoteExceptionImpl extends RemoteException {

	public APIRemoteExceptionImpl(String msg) {
		super(msg);
	}

	public APIRemoteExceptionImpl(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}
