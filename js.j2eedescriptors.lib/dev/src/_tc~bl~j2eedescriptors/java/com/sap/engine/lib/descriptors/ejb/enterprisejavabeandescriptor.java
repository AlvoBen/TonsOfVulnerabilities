/*
 * Created on 2005-3-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.lib.descriptors.ejb;

/**
 * @author hristo-s
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface EnterpriseJavaBeanDescriptor {
	  com.sap.engine.lib.descriptors.j2ee.DescriptionType[] getDescription();

	  com.sap.engine.lib.descriptors.j2ee.DisplayNameType[] getDisplayName();

	  com.sap.engine.lib.descriptors.j2ee.IconType[] getIcon();

	  com.sap.engine.lib.descriptors.ejb.EjbNameType getEjbName();
	  
	  com.sap.engine.lib.descriptors.ejb.EjbClassType getEjbClass();

	  com.sap.engine.lib.descriptors.j2ee.EnvEntryType[] getEnvEntry();

	  com.sap.engine.lib.descriptors.j2ee.EjbRefType[] getEjbRef();
	 
	  com.sap.engine.lib.descriptors.j2ee.EjbLocalRefType[] getEjbLocalRef();

	  com.sap.engine.lib.descriptors.j2ee.ServiceRefType[] getServiceRef();

      com.sap.engine.lib.descriptors.j2ee.ResourceRefType[] getResourceRef();
	  
	  com.sap.engine.lib.descriptors.j2ee.ResourceEnvRefType[] getResourceEnvRef();

	  com.sap.engine.lib.descriptors.j2ee.MessageDestinationRefType[] getMessageDestinationRef();

	  com.sap.engine.lib.descriptors.ejb.SecurityIdentityType getSecurityIdentity();
}
