package com.sap.security.api.imp;

import java.util.Date;
import java.util.Iterator;

import com.sap.security.api.IGroup;
import com.sap.security.api.UMException;

/**
 * This abstract class should be extended by custom implementations
 * of IGroup which then have to overwrite all methods. Extending this
 * abstract class will allow UME API extensions of IGroup without
 * breaking builds of test implementations.
 * 
 */
public abstract class AbstractGroup extends AbstractPrincipalSet implements IGroup {

	static final long serialVersionUID = 7248873663230020193L;
	
	public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/imp/AbstractGroup.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#addAttributeValue(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean addAttributeValue(String namespace, String name, String value) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#removeAttributeValue(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean removeAttributeValue(String namespace, String name, String value) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#getUserMembers(boolean)
	 */
	public Iterator getUserMembers(boolean getChildMembers) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#getGroupMembers(boolean)
	 */
	public Iterator getGroupMembers(boolean getChildMembers) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#isUserMember(String,boolean)
	 */
	public boolean isUserMember(String uniqueIdOfUser, boolean checkParents) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#isGroupMember(String,boolean)
	 */
	public boolean isGroupMember(
		String uniqueIdOfGroup,
		boolean checkParents) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#addUserMember(String)
	 */
	public boolean addUserMember(String uniqueIdOfUser) throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#addGroupMember(String)
	 */
	public boolean addGroupMember(String uniqueIdOfGroup) throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#removeUserMember(String)
	 */
	public boolean removeUserMember(String uniqueIdOfUser) throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#removeGroupMember(String)
	 */
	public boolean removeGroupMember(String uniqueIdOfGroup)
		throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#getRoles(boolean)
	 */
	public Iterator getRoles(boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#getParentGroups(boolean)
	 */
	public Iterator getParentGroups(boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#isMemberOfRole(java.lang.String, boolean)
	 */
	public boolean isMemberOfRole(String uniqueIdOfRole, boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#isMemberOfGroup(java.lang.String, boolean)
	 */
	public boolean isMemberOfGroup(String uniqueIdOfGroup, boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#addToGroup(java.lang.String)
	 */
	public void addToGroup(String uniqueIdOfGroup) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#removeFromGroup(java.lang.String)
	 */
	public void removeFromGroup(String uniqueIdOfGroup) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#addToRole(java.lang.String)
	 */
	public void addToRole(String uniqueIdOfRole) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#removeFromRole(java.lang.String)
	 */
	public void removeFromRole(String uniqueIdOfRole) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#getDescription()
	 */
	public String getDescription() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#setDescription(java.lang.String)
	 */
	public boolean setDescription(String description) throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroup#getUniqueName()
	 */
	public String getUniqueName() {
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
	 * @see com.sap.security.api.IPrincipalSet#addMember(java.lang.String)
	 */
	public boolean addMember(String newMember) throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalSet#removeMember(java.lang.String)
	 */
	public boolean removeMember(String oldMember) throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#setAttribute(java.lang.String, java.lang.String, java.lang.String[])
	 */
	public boolean setAttribute(
		String namespace,
		String name,
		String[] values) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#setBinaryAttribute(java.lang.String, java.lang.String, byte[])
	 */
	public boolean setBinaryAttribute(
		String namespace,
		String name,
		byte[] value) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#isModified()
	 */
	public boolean isModified() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#setDisplayName(java.lang.String)
	 */
	public boolean setDisplayName(String displayName) throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalMaint#commit()
	 */
	public void commit() throws UMException {
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

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getMessages(boolean)
	 */
	public Iterator getMessages(boolean clearPermanentMessages) {
		// TODO Auto-generated method stub
		return null;
	}

}
