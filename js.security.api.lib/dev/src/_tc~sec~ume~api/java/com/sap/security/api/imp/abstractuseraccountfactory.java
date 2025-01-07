package com.sap.security.api.imp;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;

import com.sap.security.api.AttributeList;
import com.sap.security.api.AuthenticationFailedException;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.IUserAccountFactory;
import com.sap.security.api.IUserAccountSearchFilter;
import com.sap.security.api.UMException;
import com.sap.security.api.UserAccountListener;
import com.sap.security.api.UserLockedException;
import com.sap.security.api.ticket.TicketException;

/**
 * This abstract class should be extended by custom implementations
 * of IUserAccountFactory which then have to overwrite all methods. Extending this
 * abstract class will allow UME API extensions of IUserAccountFactory without
 * breaking builds of test implementations.
 * 
 */
public abstract class AbstractUserAccountFactory
	implements IUserAccountFactory {

	public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/imp/AbstractUserAccountFactory.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	/* (non-Javadoc)
	 * @see com.sap.security.api.IConfigurable#setTxSupport(boolean)
	 */
	public void setTxSupport(boolean txSupport) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#registerListener(com.sap.security.api.UserAccountListener, int, boolean)
	 */
	public void registerListener(UserAccountListener userAccountListener,
			int modifier, boolean notifyAfterPhysicalCommitCompleted) {
		throw new UnsupportedOperationException();
	}
	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#newUserAccount(java.lang.String, java.lang.String)
	 */
	public IUserAccount newUserAccount(String logonid, String uniqueIdOfUser)
		throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#newUserAccount(java.lang.String)
	 */
	public IUserAccount newUserAccount(String logonid) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#getUserAccountByLogonId(java.lang.String)
	 */
	public IUserAccount getUserAccountByLogonId(String logonid)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#getUserAccountByLogonId(java.lang.String, com.sap.security.api.AttributeList)
	 */
	public IUserAccount getUserAccountByLogonId(
		String logonid,
		AttributeList populateAttributes)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#getUserAccount(java.lang.String)
	 */
	public IUserAccount getUserAccount(String uniqueId) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#getUserAccount(java.lang.String, com.sap.security.api.AttributeList)
	 */
	public IUserAccount getUserAccount(
		String uniqueId,
		AttributeList populateAttributes)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#getUserAccount(java.security.cert.X509Certificate)
	 */
	public IUserAccount getUserAccount(X509Certificate cert)
		throws CertificateException, UMException, TicketException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#getAuthenticatedUserAccount(java.util.Map)
	 */
	public IUserAccount getAuthenticatedUserAccount(Map credentials)
		throws
			UMException,
			AuthenticationFailedException,
			UserLockedException,
			TicketException,
			CertificateException {
				throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#getUserAccount(java.util.Map)
	 */
	public IUserAccount getUserAccount(Map credentials)
		throws UMException, CertificateException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#deleteUserAccount(java.lang.String)
	 */
	public void deleteUserAccount(String uniqueId) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#search(com.sap.security.api.IUserAccountSearchFilter)
	 */
	public ISearchResult search(IUserAccountSearchFilter filter)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#getUserAccounts(java.lang.String)
	 */
	public IUserAccount[] getUserAccounts(String uniqueIdOfUser)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#getUserAccounts(java.lang.String, com.sap.security.api.AttributeList)
	 */
	public IUserAccount[] getUserAccounts(
		String uniqueIdOfUser,
		AttributeList attributeList)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#getMutableUserAccount(java.lang.String)
	 */
	public IUserAccount getMutableUserAccount(String uniqueId)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#getUserAccountSearchFilter()
	 */
	public IUserAccountSearchFilter getUserAccountSearchFilter()
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#registerListener(com.sap.security.api.UserAccountListener, int)
	 */
	public void registerListener(
		UserAccountListener userAccountListener,
		int modifier) {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#unregisterListener(com.sap.security.api.UserAccountListener)
	 */
	public void unregisterListener(UserAccountListener userAccountListener) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#invalidateCacheEntryByLogonId(java.lang.String)
	 */
	public void invalidateCacheEntryByLogonId(String logonid)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserAccountFactory#invalidateCacheEntry(java.lang.String)
	 */
	public void invalidateCacheEntry(String uniqueid) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IConfigurable#initialize(java.util.Properties)
	 */
	public void initialize(Properties properties) throws UMException {
		throw new UnsupportedOperationException();

	}

}
