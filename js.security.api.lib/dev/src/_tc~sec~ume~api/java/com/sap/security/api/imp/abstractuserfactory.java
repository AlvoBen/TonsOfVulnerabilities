package com.sap.security.api.imp;

import java.util.Properties;

import com.sap.security.api.AttributeList;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.IUser;
import com.sap.security.api.IUserAccount;
import com.sap.security.api.IUserAccountSearchFilter;
import com.sap.security.api.IUserFactory;
import com.sap.security.api.IUserMaint;
import com.sap.security.api.IUserSearchFilter;
import com.sap.security.api.UMException;
import com.sap.security.api.UserListener;

/**
 * This abstract class should be extended by custom implementations
 * of IUserFactory which then have to overwrite all methods. Extending this
 * abstract class will allow UME API extensions of IUserFactory without
 * breaking builds of test implementations.
 * 
 */
public abstract class AbstractUserFactory implements IUserFactory {

		public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/imp/AbstractUserFactory.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#isUserCreationPossible()
		 */
		public boolean isUserCreationPossible() throws UMException {
			throw new UnsupportedOperationException();
		}

	/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUserByPersonID(java.lang.String, com.sap.security.api.AttributeList)
		 */
		public IUser getUserByPersonID(String personid, AttributeList attributeList) throws UMException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUserByPersonID(java.lang.String)
		 */
		public IUser getUserByPersonID(String personid) throws UMException {
			throw new UnsupportedOperationException();
		}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IConfigurable#setTxSupport(boolean)
	 */
	public void setTxSupport(boolean txSupport) {
		throw new UnsupportedOperationException();
	}
	
		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#registerListener(com.sap.security.api.UserListener, int, boolean)
		 */
		public void registerListener(UserListener userListener, int modifier,
				boolean notifyAfterPhysicalCommitCompleted) {
			throw new UnsupportedOperationException();
		}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IUserFactory#getUserByLogonAlias(java.lang.String, com.sap.security.api.AttributeList)
	 */
	public IUser getUserByLogonAlias(String logonalias, AttributeList attrlist)
			throws UMException {
		throw new UnsupportedOperationException();
	}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUniqueIDs()
		 */
		public ISearchResult getUniqueIDs() throws UMException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUser(java.lang.String)
		 */
		public IUser getUser(String uniqueID) throws UMException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUser(java.lang.String, com.sap.security.api.AttributeList)
		 */
		public IUser getUser(String uniqueID, AttributeList populateAttributes)
			throws UMException {
				throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUserByUniqueName(java.lang.String)
		 */
		public IUser getUserByUniqueName(String uniqueName) throws UMException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUserByUniqueName(java.lang.String, com.sap.security.api.AttributeList)
		 */
		public IUser getUserByUniqueName(
			String uniqueName,
			AttributeList attributeList)
			throws UMException {
				throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUsers(java.lang.String[])
		 */
		public IUser[] getUsers(String[] uniqueIDs) throws UMException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUsers(java.lang.String[], com.sap.security.api.AttributeList)
		 */
		public IUser[] getUsers(
			String[] uniqueIDs,
			AttributeList populateAttributes)
			throws UMException {
				throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#newUser(java.lang.String)
		 */
		public IUserMaint newUser(String uniqueName) throws UMException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#newUser(java.lang.String, com.sap.security.api.IUser)
		 */
		public IUserMaint newUser(String uniqueName, IUser copyFrom)
			throws UMException {
				throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#deleteUser(java.lang.String)
		 */
		public void deleteUser(String uniqueID) throws UMException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#searchUsers(com.sap.security.api.IUserSearchFilter)
		 */
		public ISearchResult searchUsers(IUserSearchFilter filter)
			throws UMException {
				throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#searchUsers(com.sap.security.api.IUserSearchFilter, com.sap.security.api.IUserAccountSearchFilter)
		 */
		public ISearchResult searchUsers(
			IUserSearchFilter ufilter,
			IUserAccountSearchFilter uafilter)
			throws UMException {
				throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUserByLogonID(java.lang.String)
		 */
		public IUser getUserByLogonID(String logonid) throws UMException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUserByLogonID(java.lang.String, com.sap.security.api.AttributeList)
		 */
		public IUser getUserByLogonID(String logonid, AttributeList attributeList)
			throws UMException {
				throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#newUsers(java.lang.String[])
		 */
		public IUserMaint[] newUsers(String[] uniqueNames) throws UMException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getMutableUser(java.lang.String)
		 */
		public IUserMaint getMutableUser(String uniqueId) throws UMException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#getUserSearchFilter()
		 */
		public IUserSearchFilter getUserSearchFilter() throws UMException {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#registerListener(com.sap.security.api.UserListener, int)
		 */
		public void registerListener(UserListener userListener, int modifier) {
			throw new UnsupportedOperationException();

		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#unregisterListener(com.sap.security.api.UserListener)
		 */
		public void unregisterListener(UserListener userListener) {
			throw new UnsupportedOperationException();

		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#invalidateCacheEntryByLogonId(java.lang.String)
		 */
		public void invalidateCacheEntryByLogonId(String logonid)
			throws UMException {
				throw new UnsupportedOperationException();

		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#invalidateCacheEntryByUniqueName(java.lang.String)
		 */
		public void invalidateCacheEntryByUniqueName(String uniqueName)
			throws UMException {
				throw new UnsupportedOperationException();

		}

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#invalidateCacheEntry(java.lang.String)
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

		/* (non-Javadoc)
		 * @see com.sap.security.api.IUserFactory#commitUser(com.sap.security.api.IUserMaint, com.sap.security.api.IUserAccount)
		 */
		public void commitUser(IUserMaint user, IUserAccount account) throws UMException {
			throw new UnsupportedOperationException();
		}
		
}
