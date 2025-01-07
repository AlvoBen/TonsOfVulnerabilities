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
public interface AccessibleEnterpriseJavaBeanDescriptor extends EnterpriseJavaBeanDescriptor {
	com.sap.engine.lib.descriptors.j2ee.HomeType getHome();

	com.sap.engine.lib.descriptors.j2ee.RemoteType getRemote();

	com.sap.engine.lib.descriptors.j2ee.LocalHomeType getLocalHome();

	com.sap.engine.lib.descriptors.j2ee.LocalType getLocal();
	
	com.sap.engine.lib.descriptors.j2ee.SecurityRoleRefType[] getSecurityRoleRef();
}
