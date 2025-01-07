﻿package com.sap.engine.services.dc.cm.server.impl;

import com.sap.engine.services.dc.cm.server.RequestVisitor;
import com.sap.engine.services.dc.cm.server.RestartServerRequest;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-7
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class RestartServerRequestImpl implements RestartServerRequest {

	RestartServerRequestImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.server.ServerServiceRequest#accept(com.
	 * sap.engine.services.dc.cm.server.RequestVisitor)
	 */
	public void accept(RequestVisitor visitor) {
		visitor.visit(this);
	}
}
