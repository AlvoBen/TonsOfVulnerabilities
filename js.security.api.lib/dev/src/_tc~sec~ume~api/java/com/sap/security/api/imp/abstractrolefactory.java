package com.sap.security.api.imp;

import java.util.Properties;

import com.sap.security.api.AttributeList;
import com.sap.security.api.IRole;
import com.sap.security.api.IRoleFactory;
import com.sap.security.api.IRoleSearchFilter;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.RoleListener;
import com.sap.security.api.UMException;

/**
 * This abstract class should be extended by custom implementations
 * of IRoleFactory which then have to overwrite all methods. Extending this
 * abstract class will allow UME API extensions of IRoleFactory without
 * breaking builds of test implementations.
 * 
 */
public abstract class AbstractRoleFactory implements IRoleFactory {

	public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/imp/AbstractRoleFactory.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	/* (non-Javadoc)
	 * @see com.sap.security.api.IConfigurable#setTxSupport(boolean)
	 */
	public void setTxSupport(boolean txSupport) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#registerListener(com.sap.security.api.RoleListener, int, boolean)
	 */
	public void registerListener(RoleListener roleListener, int modifier,
			boolean notifyAfterPhysicalCommitCompleted) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getRole(java.lang.String)
	 */
	public IRole getRole(String uniqueID) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getRole(java.lang.String, com.sap.security.api.AttributeList)
	 */
	public IRole getRole(String uniqueID, AttributeList populateAttributes)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#deleteRole(java.lang.String)
	 */
	public void deleteRole(String uniqueID) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#searchRoles(com.sap.security.api.IRoleSearchFilter)
	 */
	public ISearchResult searchRoles(IRoleSearchFilter filter)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getRoleSearchFilter()
	 */
	public IRoleSearchFilter getRoleSearchFilter() throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#newRole(java.lang.String)
	 */
	public IRole newRole(String uniqueName) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getRoleByUniqueName(java.lang.String)
	 */
	public IRole getRoleByUniqueName(String uniqueName) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getRoles(java.lang.String[])
	 */
	public IRole[] getRoles(String[] uniqueIDs) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getRoles(java.lang.String[], com.sap.security.api.AttributeList)
	 */
	public IRole[] getRoles(
		String[] uniqueIDs,
		AttributeList populateAttributes)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getMutableRole(java.lang.String)
	 */
	public IRole getMutableRole(String uniqueID) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getMaxRoleDescriptionLength()
	 */
	public int getMaxRoleDescriptionLength() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#registerListener(com.sap.security.api.RoleListener, int)
	 */
	public void registerListener(RoleListener roleListener, int modifier) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#unregisterListener(com.sap.security.api.RoleListener)
	 */
	public void unregisterListener(RoleListener roleListener) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getUsersOfRole(java.lang.String, boolean)
	 */
	public String[] getUsersOfRole(String uniqueIdOfRole, boolean recursive) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getGroupsOfRole(java.lang.String, boolean)
	 */
	public String[] getGroupsOfRole(String uniqueIdOfRole, boolean recursive) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getRolesOfUser(java.lang.String, boolean)
	 */
	public String[] getRolesOfUser(String uniqueIdOfUser, boolean recursive) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#getRolesOfGroup(java.lang.String, boolean)
	 */
	public String[] getRolesOfGroup(
		String uniqueIdOfGroup,
		boolean recursive) {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#addUserToRole(java.lang.String, java.lang.String)
	 */
	public void addUserToRole(String uniqueIdOfUser, String uniqueIdOfRole)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#addGroupToRole(java.lang.String, java.lang.String)
	 */
	public void addGroupToRole(String uniqueIdOfGroup, String uniqueIdOfRole)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#removeUserFromRole(java.lang.String, java.lang.String)
	 */
	public void removeUserFromRole(
		String uniqueIdOfUser,
		String uniqueIdOfRole)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IRoleFactory#removeGroupFromRole(java.lang.String, java.lang.String)
	 */
	public void removeGroupFromRole(
		String uniqueIdOfGroup,
		String uniqueIdOfRole)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IConfigurable#initialize(java.util.Properties)
	 */
	public void initialize(Properties properties) throws UMException {
		throw new UnsupportedOperationException();

	}

}
