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
package com.sap.engine.session;

import com.sap.engine.session.trace.Locations;
import com.sap.tc.logging.Severity;

/**
 * @author georgi-s 
 */
public class SessionNotFoundException extends CreateException {
	
	public SessionNotFoundException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SessionNotFoundException(String message) {
		super(message);
    if (Locations.SESSION_LOC.beDebug()){
      Locations.SESSION_LOC.traceThrowableT(Severity.DEBUG,"", this);
    }    
	}

	public SessionNotFoundException(String message, Throwable cause) {
		super(message, cause);

	}

	public SessionNotFoundException(Throwable cause) {
		super(cause);
	}
}
