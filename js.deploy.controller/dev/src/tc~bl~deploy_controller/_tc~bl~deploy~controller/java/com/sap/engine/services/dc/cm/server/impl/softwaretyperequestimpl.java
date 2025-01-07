package com.sap.engine.services.dc.cm.server.impl;

import org.w3c.dom.Document;

import com.sap.engine.services.dc.cm.server.RequestVisitor;
import com.sap.engine.services.dc.cm.server.SoftwareTypeRequest;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SoftwareTypeRequestImpl implements SoftwareTypeRequest {

	private final Document deployReferencesDocument;
	private final boolean isDefault;

	SoftwareTypeRequestImpl(final Document configurationDocument) {
		this(configurationDocument, false);
	}

	SoftwareTypeRequestImpl(final Document configurationDocument,
			final boolean isDefault) {
		this.deployReferencesDocument = configurationDocument;
		this.isDefault = isDefault;
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

	public Document getDeployReferencesDocument() {
		return this.deployReferencesDocument;
	}

	public boolean isDefault() {
		return this.isDefault;
	}
}
