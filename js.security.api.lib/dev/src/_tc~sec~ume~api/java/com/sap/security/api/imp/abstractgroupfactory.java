package com.sap.security.api.imp;

import java.util.Properties;

import com.sap.security.api.AttributeList;
import com.sap.security.api.GroupListener;
import com.sap.security.api.IGroup;
import com.sap.security.api.IGroupFactory;
import com.sap.security.api.IGroupSearchFilter;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.NoSuchGroupException;
import com.sap.security.api.UMException;

/**
 * This abstract class should be extended by custom implementations
 * of IGroupFactory which then have to overwrite all methods. Extending this
 * abstract class will allow UME API extensions of IGroupFactory without
 * breaking builds of test implementations.
 * 
 */
public abstract class AbstractGroupFactory implements IGroupFactory {

	/* (non-Javadoc)
	 * @see com.sap.security.api.IConfigurable#setTxSupport(boolean)
	 */
	public void setTxSupport(boolean txSupport) {
		throw new UnsupportedOperationException();
	}
	
	public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/imp/AbstractGroupFactory.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#registerListener(com.sap.security.api.GroupListener, int, boolean)
	 */
	public void registerListener(GroupListener groupListener, int modifier,
			boolean notifyAfterPhysicalCommitCompleted) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#getGroup(java.lang.String)
	 */
	public IGroup getGroup(String uniqueID) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#getGroup(java.lang.String, com.sap.security.api.AttributeList)
	 */
	public IGroup getGroup(String uniqueID, AttributeList populateAttributes)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#newGroup(java.lang.String)
	 */
	public IGroup newGroup(String uniqueName) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#getGroupByUniqueName(java.lang.String)
	 */
	public IGroup getGroupByUniqueName(String uniqueName) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#deleteGroup(java.lang.String)
	 */
	public void deleteGroup(String uniqueID) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#searchGroups(com.sap.security.api.IGroupSearchFilter)
	 */
	public ISearchResult searchGroups(IGroupSearchFilter filter)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#getGroups(java.lang.String[])
	 */
	public IGroup[] getGroups(String[] uniqueIDs)
		throws NoSuchGroupException, UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#getGroups(java.lang.String[], com.sap.security.api.AttributeList)
	 */
	public IGroup[] getGroups(
		String[] uniqueIDs,
		AttributeList populateAttributes)
		throws NoSuchGroupException, UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#getMutableGroup(java.lang.String)
	 */
	public IGroup getMutableGroup(String uniqueID)
		throws NoSuchGroupException, UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#registerListener(com.sap.security.api.GroupListener, int)
	 */
	public void registerListener(GroupListener groupListener, int modifier) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#unregisterListener(com.sap.security.api.GroupListener)
	 */
	public void unregisterListener(GroupListener groupListener) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#getParentGroups(java.lang.String, boolean)
	 */
	public String[] getParentGroups(String uniqueIdOfGroup, boolean recursive)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#getChildGroups(java.lang.String, boolean)
	 */
	public String[] getChildGroups(String uniqueIdOfGroup, boolean recursive)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#addGroupToParent(java.lang.String, java.lang.String)
	 */
	public void addGroupToParent(
		String uniqueIdOfGroup,
		String uniqueIdOfParentGroup)
		throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#addUserToGroup(java.lang.String, java.lang.String)
	 */
	public void addUserToGroup(String uniqueIdOfUser, String uniqueIdOfGroup)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#removeGroupFromParent(java.lang.String, java.lang.String)
	 */
	public void removeGroupFromParent(
		String uniqueIdOfGroup,
		String uniqueIdOfParentGroup)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#removeUserFromGroup(java.lang.String, java.lang.String)
	 */
	public void removeUserFromGroup(
		String uniqueIdOfUser,
		String uniqueIdOfGroup)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#getGroupSearchFilter()
	 */
	public IGroupSearchFilter getGroupSearchFilter() throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#invalidateCacheEntryByUniqueName(java.lang.String)
	 */
	public void invalidateCacheEntryByUniqueName(String uniqueName)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IGroupFactory#invalidateCacheEntry(java.lang.String)
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
