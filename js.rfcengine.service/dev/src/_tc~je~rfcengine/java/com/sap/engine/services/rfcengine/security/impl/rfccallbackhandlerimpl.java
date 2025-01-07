/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rfcengine.security.impl;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.lib.security.http.HttpCallback;
import com.sap.engine.services.rfcengine.security.RFCCallbackHandler;

/**
 * @author Silvia Petrova
 *
 * 
 */
public class RFCCallbackHandlerImpl implements RFCCallbackHandler {

	/**
	 * The incoming SSO Ticket
	 */
	private String ticket;

	public RFCCallbackHandlerImpl(String _ticket) {
		this.ticket = _ticket;
	}

	/**
	 * Retrieve the information requested in the provided Callbacks.
	 */
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
	  for (int i = 0; i < callbacks.length; i++) {
	  	setCallback(callbacks[i]);
	  }
	}
	
	private void setCallback(Callback callback) {
		if(callback instanceof HttpGetterCallback) {
			HttpGetterCallback c = (HttpGetterCallback)callback;
			if( ticket != null && 
				(c.getType() == HttpCallback.HEADER) &&
				("MYSAPSSO2".equals(c.getName())) )
					c.setValue(ticket); 
		}
	}
}
