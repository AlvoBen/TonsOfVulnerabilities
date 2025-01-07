package com.sap.security.api.imp;

import java.util.Iterator;

import com.sap.security.api.IPrincipalSet;
import com.sap.security.api.UMException;

/**
 * This abstract class should be extended by custom implementations
 * of IPrincipalSet which then have to overwrite all methods. Extending this
 * abstract class will allow UME API extensions of IPrincipalSet without
 * breaking builds of test implementations.
 * 
 */

public class AbstractPrincipalSet extends AbstractPrincipal implements
		IPrincipalSet {

	static final long serialVersionUID = -6691729429737803269L;
	
	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#addAttributeValue(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean addAttributeValue(String namespace, String name, String value) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#commit()
	 */
	public void commit() throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#isModified()
	 */
	public boolean isModified() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#removeAttributeValue(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean removeAttributeValue(String namespace, String name, String value) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#rollback()
	 */
	public void rollback() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#save()
	 */
	public void save() throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#setAttribute(java.lang.String, java.lang.String, java.lang.String[])
	 */
	public boolean setAttribute(String namespace, String name, String[] values) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#setBinaryAttribute(java.lang.String, java.lang.String, byte[])
	 */
	public boolean setBinaryAttribute(String namespace, String name, byte[] value) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#setDisplayName(java.lang.String)
	 */
	public boolean setDisplayName(String displayName) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalSet#addMember(java.lang.String)
	 */
	public boolean addMember(String newMember) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalSet#getMembers(boolean)
	 */
	public Iterator getMembers(boolean getChildMembers) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalSet#isMember(java.lang.String, boolean)
	 */
	public boolean isMember(String uniqueIdOfPrincipal, boolean recursive) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalSet#removeMember(java.lang.String)
	 */
	public boolean removeMember(String oldMember) throws UMException {
		throw new UnsupportedOperationException();
	}

}
