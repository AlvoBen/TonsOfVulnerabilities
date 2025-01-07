package com.sap.security.api.imp;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Iterator;

import com.sap.security.api.IUser;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.InvalidPasswordException;
import com.sap.security.api.UMException;

/**
 * This abstract class should be extended by custom implementations
 * of IUserAccount which then have to overwrite all methods. Extending this
 * abstract class will allow UME API extensions of IUserAccount without
 * breaking builds of test implementations.
 * 
 */
public abstract class AbstractUserAccount extends AbstractPrincipal implements IUserAccount {

	static final long serialVersionUID = -3610843882781267416L;
	
	public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/imp/AbstractUserAccount.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipal#getMessages(boolean)
	 */
	public Iterator getMessages(boolean clearPermanentMessages) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#isPasswordLocked()
	 */
	public boolean isPasswordLocked() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#isUserAccountLocked()
	 */
	public boolean isUserAccountLocked() {
		throw new UnsupportedOperationException();
	}

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
	 * @see com.sap.security.api.IUserAccount#checkPasswordExtended(java.lang.String)
	 */
	public int checkPasswordExtended(String pass) throws UMException {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#isPasswordDisabled()
	 */
	public boolean isPasswordDisabled() {
		throw new UnsupportedOperationException();
	}
	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setPasswordDisabled()
	 */
	public void setPasswordDisabled() {
		throw new UnsupportedOperationException();
	}
	

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getLogonUid()
	 */
	public String getLogonUid() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getValidFromDate()
	 */
	public Date getValidFromDate() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setValidFromDate(java.util.Date)
	 */
	public void setValidFromDate(Date date) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getValidToDate()
	 */
	public Date getValidToDate() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getAssignedUser()
	 */
	public IUser getAssignedUser() throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setValidToDate(java.util.Date)
	 */
	public void setValidToDate(Date date) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#isLocked()
	 */
	public boolean isLocked() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setLocked(boolean, int)
	 */
	public void setLocked(boolean lock, int reason) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getLockReason()
	 */
	public int getLockReason() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getLastFailedLogonDate()
	 */
	public Date getLastFailedLogonDate() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setLastFailedLogonDate(java.util.Date)
	 */
	public void setLastFailedLogonDate(Date timeStamp) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getFailedLogonAttempts()
	 */
	public int getFailedLogonAttempts() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setFailedLogonAttempts(int)
	 */
	public void setFailedLogonAttempts(int i) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#incrementFailedLogonAttempts()
	 */
	public void incrementFailedLogonAttempts() {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#resetFailedLogonAttempts()
	 */
	public void resetFailedLogonAttempts() {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getLastSuccessfulLogonDate()
	 */
	public Date getLastSuccessfulLogonDate() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setLastSuccessfulLogonDate(java.util.Date)
	 */
	public void setLastSuccessfulLogonDate(Date timeStamp) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getSuccessfulLogonCounts()
	 */
	public int getSuccessfulLogonCounts() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#incrementSuccessfulLogonCounts()
	 */
	public void incrementSuccessfulLogonCounts() {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setSuccessfulLogonCounts(int)
	 */
	public void setSuccessfulLogonCounts(int i) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#isPasswordChangeRequired()
	 */
	public boolean isPasswordChangeRequired() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getLastPasswordChangedDate()
	 */
	public Date getLastPasswordChangedDate() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setPasswordChangeRequired(boolean)
	 */
	public void setPasswordChangeRequired(boolean chng) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setPassword(java.lang.String)
	 */
	public void setPassword(String pass) throws InvalidPasswordException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setPassword(java.lang.String, java.lang.String)
	 */
	public void setPassword(String oldpass, String newpass)
		throws InvalidPasswordException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getCertificates()
	 */
	public X509Certificate[] getCertificates()
		throws CertificateException, UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setCertificates(java.security.cert.X509Certificate[])
	 */
	public void setCertificates(X509Certificate[] certificate)
		throws CertificateException, UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#deleteCertificates(java.security.cert.X509Certificate[])
	 */
	public void deleteCertificates(X509Certificate[] certificate)
		throws CertificateException, UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#checkPassword(java.lang.String)
	 */
	public boolean checkPassword(String pass) {
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
	 * @see com.sap.security.api.IUserAccount#lockDate()
	 */
	public Date lockDate() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getHashedPassword()
	 */
	public String getHashedPassword() throws UMException {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#setLastLogoutDate(java.util.Date)
	 */
	public void setLastLogoutDate(Date timeStamp) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getPreviousSuccessfulLogonDate()
	 */
	public Date getPreviousSuccessfulLogonDate() {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getRoles(boolean)
	 */
	public Iterator getRoles(boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getParentGroups(boolean)
	 */
	public Iterator getParentGroups(boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#isMemberOfRole(java.lang.String, boolean)
	 */
	public boolean isMemberOfRole(String roleId, boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#isMemberOfGroup(java.lang.String, boolean)
	 */
	public boolean isMemberOfGroup(String uniqueIdOfGroup, boolean recursive) {
		throw new UnsupportedOperationException();
		
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#addToGroup(java.lang.String)
	 */
	public void addToGroup(String uniqueIdOfGroup) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#removeFromGroup(java.lang.String)
	 */
	public void removeFromGroup(String uniqueIdOfGroup) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#addToRole(java.lang.String)
	 */
	public void addToRole(String uniqueIdOfRole) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#removeFromRole(java.lang.String)
	 */
	public void removeFromRole(String uniqueIdOfRole) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccount#getAssignedUserID()
	 */
	public String getAssignedUserID() {
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
