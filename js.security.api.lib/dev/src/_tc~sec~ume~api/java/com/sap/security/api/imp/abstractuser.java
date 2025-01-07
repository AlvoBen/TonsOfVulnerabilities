package com.sap.security.api.imp;

import java.security.AccessControlException;
import java.security.Permission;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import com.sap.i18n.cpbase.I18nFormatterFactory;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.IUserFactory;
import com.sap.security.api.UMException;

/**
 * This abstract class should be extended by custom implementations
 * of IUser which then have to overwrite all methods. Extending this
 * abstract class will allow UME API extensions of IUser without
 * breaking builds of test implementations.
 * 
 */
public abstract class AbstractUser extends AbstractPrincipal implements IUser {

	static final long serialVersionUID = 376492732631526005L;
	
	public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/imp/AbstractUser.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getMessages(boolean)
	 */
	public Iterator getMessages(boolean clearPermanentMessages) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getPersonID()
	 */
	public String getPersonID() throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getUid()
	 */
	public String getUid() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getJobTitle()
	 */
	public String getJobTitle() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getDepartment()
	 */
	public String getDepartment() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getTitle()
	 */
	public String getTitle() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getSalutation()
	 */
	public String getSalutation() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getFirstName()
	 */
	public String getFirstName() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getLastName()
	 */
	public String getLastName() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getStreet()
	 */
	public String getStreet() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getCity()
	 */
	public String getCity() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getZip()
	 */
	public String getZip() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getState()
	 */
	public String getState() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getCountry()
	 */
	public String getCountry() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getLocale()
	 */
	public Locale getLocale() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getTimeZone()
	 */
	public TimeZone getTimeZone() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getCurrency()
	 */
	public String getCurrency() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getTelephone()
	 */
	public String getTelephone() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getCellPhone()
	 */
	public String getCellPhone() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getFax()
	 */
	public String getFax() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getEmail()
	 */
	public String getEmail() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#isCompanyUser()
	 */
	public boolean isCompanyUser() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getCompany()
	 */
	public String getCompany() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getUserFactory()
	 */
	public IUserFactory getUserFactory() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#hasPermission(java.security.Permission)
	 */
	public boolean hasPermission(Permission permission) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#checkPermission(java.security.Permission)
	 */
	public void checkPermission(Permission permission)
		throws AccessControlException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#hasPermission(java.lang.String, java.security.Permission)
	 */
	public boolean hasPermission(String contextID, Permission permission) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#checkPermission(java.lang.String, java.security.Permission)
	 */
	public void checkPermission(String contextID, Permission permission)
		throws AccessControlException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getUserAccounts()
	 */
	public IUserAccount[] getUserAccounts() throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getUserAccountUniqueIDs()
	 */
	public Iterator getUserAccountUniqueIDs() throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getAccessibilityLevel()
	 */
	public int getAccessibilityLevel() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getRoles(boolean)
	 */
	public Iterator getRoles(boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getParentGroups(boolean)
	 */
	public Iterator getParentGroups(boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#isMemberOfRole(java.lang.String, boolean)
	 */
	public boolean isMemberOfRole(String uniqueIdOfRole, boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#isMemberOfGroup(java.lang.String, boolean)
	 */
	public boolean isMemberOfGroup(String uniqueIdOfGroup, boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getUniqueName()
	 */
	public String getUniqueName() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#getTransientAttribute(java.lang.String, java.lang.String)
	 */
	public Object getTransientAttribute(String namespace, String name) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUser#setTransientAttribute(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public boolean setTransientAttribute(
		String namespace,
		String name,
		Object o) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	public String getName() {
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
     * @see com.sap.security.api.IUser#getI18nFormatterFactory()
     */
	public I18nFormatterFactory getI18nFormatterFactory() throws UMException {
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
