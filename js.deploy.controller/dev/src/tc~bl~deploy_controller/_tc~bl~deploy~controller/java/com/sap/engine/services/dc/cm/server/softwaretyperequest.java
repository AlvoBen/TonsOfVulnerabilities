package com.sap.engine.services.dc.cm.server;

import org.w3c.dom.Document;

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
public interface SoftwareTypeRequest extends ServerServiceRequest {

	public static String DEFAULT_DEPLOY_REFERENCES_XML = "com/sap/engine/services/dc/cfg/deploy-references.xml";
	public static String DEFAULT_DEPLOY_REFERENCES_XSD = "com/sap/engine/services/dc/cfg/deploy-references.xsd";

	public Document getDeployReferencesDocument();

	public boolean isDefault();
}
