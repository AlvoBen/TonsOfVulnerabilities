package com.sap.security.api.imp;

import java.util.Date;
import java.util.Iterator;

import com.sap.security.api.IPrincipal;
import com.sap.security.api.UMException;

/**
 * This abstract class should be extended by custom implementations
 * of IPrincipal which then have to overwrite all methods. Extending this
 * abstract class will allow UME API extensions of IPrincipal without
 * breaking builds of test implementations.
 * 
 */
public class AbstractPrincipal implements IPrincipal {

	static final long serialVersionUID = -8374182172142098954L;
	
	public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/imp/AbstractPrincipal.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getMessages(boolean)
	 */
	public Iterator getMessages(boolean clearPermanentMessages) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getUniqueID()
	 */
	public String getUniqueID() {
		throw new UnsupportedOperationException();		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#created()
	 */
	public Date created() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#lastModified()
	 */
	public Date lastModified() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getAttribute(java.lang.String, java.lang.String)
	 */
	public String[] getAttribute(String namespace, String name) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getBinaryAttribute(java.lang.String, java.lang.String)
	 */
	public byte[] getBinaryAttribute(String namespace, String name) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getAttributeNamespaces()
	 */
	public String[] getAttributeNamespaces() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getAttributeNames(java.lang.String)
	 */
	public String[] getAttributeNames(String namespace) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getDisplayName()
	 */
	public String getDisplayName() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#isExistenceChecked()
	 */
	public boolean isExistenceChecked() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#refresh()
	 */
	public void refresh() throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#isMutable()
	 */
	public boolean isMutable() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getAttributeType(java.lang.String, java.lang.String)
	 */
	public String getAttributeType(String namespace, String attributeName) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getParents(java.lang.String[], boolean)
	 */
	public Iterator getParents(
		String[] principalTypeIdentifiers,
		boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

}
