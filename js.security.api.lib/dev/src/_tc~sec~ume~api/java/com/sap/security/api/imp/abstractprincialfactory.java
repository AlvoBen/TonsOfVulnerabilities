package com.sap.security.api.imp;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.sap.security.api.AttributeList;
import com.sap.security.api.IPrincipal;
import com.sap.security.api.IPrincipalFactory;
import com.sap.security.api.IPrincipalMaint;
import com.sap.security.api.IPrincipalMetaData;
import com.sap.security.api.IPrincipalSearchFilter;
import com.sap.security.api.IPrincipalSet;
import com.sap.security.api.ISearchResult;
import com.sap.security.api.NoSuchObjectException;
import com.sap.security.api.NoSuchPrincipalException;
import com.sap.security.api.PrincipalListener;
import com.sap.security.api.UMException;

/**
 * This abstract class should be extended by custom implementations
 * of IPrincipalFactory which then have to overwrite all methods. Extending this
 * abstract class will allow UME API extensions of IPrincipalFactory without
 * breaking builds of test implementations.
 * 
 */
public abstract class AbstractPrincialFactory implements IPrincipalFactory {

	public static final String VERSIONSTRING = "$Id: //engine/js.security.api.lib/dev/src/_tc~sec~ume~api/java/com/sap/security/api/imp/AbstractPrincialFactory.java#1 $ from $DateTime: 2008/09/17 17:07:41 $ ($Change: 217696 $)";

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#isPrincipalAttributeModifiable(com.sap.security.api.IPrincipal, java.lang.String, java.lang.String)
	 */
	public boolean isPrincipalAttributeModifiable(IPrincipal principal, String namespace, String attributename) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IConfigurable#setTxSupport(boolean)
	 */
	public void setTxSupport(boolean txSupport) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#registerListener(com.sap.security.api.PrincipalListener, int, boolean)
	 */
	public void registerListener(PrincipalListener objectListener,
			int modifier, boolean notifyAfterPhysicalCommitCompleted) {
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#deletePrincipals(java.lang.String[])
	 */
	public void deletePrincipals(String[] uniqueIDs) throws UMException {
		throw new UnsupportedOperationException();

	}
	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#simplePrincipalSearch(java.lang.String, java.lang.String, int, boolean, java.util.Map)
	 */
	public ISearchResult simplePrincipalSearch(String searchCriteria,
			String principalType, int mode, boolean caseSensitive,
			Map searchAttributes) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#getPrincipalType(java.lang.String)
	 */
	public String getPrincipalType(String uniqueId) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#getPrincipal(java.lang.String)
	 */
	public IPrincipal getPrincipal(String uniqueId) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#getPrincipal(java.lang.String, com.sap.security.api.AttributeList)
	 */
	public IPrincipal getPrincipal(
		String uniqueId,
		AttributeList populateAttributes)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#isPrincipalModifiable(java.lang.String)
	 */
	public boolean isPrincipalModifiable(String uniqueId) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#isPrincipalDeletable(java.lang.String)
	 */
	public boolean isPrincipalDeletable(String uniqueId) throws UMException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#isPrincipalAttributeModifiable(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean isPrincipalAttributeModifiable(
		String uniqueId,
		String namespace,
		String attributename)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#getPrincipals(java.lang.String[])
	 */
	public IPrincipal[] getPrincipals(String[] uniqueIDs)
		throws NoSuchPrincipalException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#getPrincipals(java.lang.String[], com.sap.security.api.AttributeList)
	 */
	public IPrincipal[] getPrincipals(
		String[] uniqueIDs,
		AttributeList populateAttributes)
		throws NoSuchPrincipalException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#newPrincipal(java.lang.String)
	 */
	public IPrincipalMaint newPrincipal(String principalTypeIdentifier) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#newPrincipalSet(java.lang.String)
	 */
	public IPrincipalSet newPrincipalSet(String principalTypeIdentifier) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#newPrincipalSet(com.sap.security.api.IPrincipalSet)
	 */
	public IPrincipalSet newPrincipalSet(IPrincipalSet copyFrom) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#newPrincipal(com.sap.security.api.IPrincipal)
	 */
	public IPrincipalMaint newPrincipal(IPrincipal copyFrom) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#deletePrincipal(java.lang.String)
	 */
	public void deletePrincipal(String uniqueID) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#commitPrincipals(com.sap.security.api.IPrincipalMaint[])
	 */
	public void commitPrincipals(IPrincipalMaint[] objects)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#rollbackPrincipals(com.sap.security.api.IPrincipalMaint[])
	 */
	public void rollbackPrincipals(IPrincipalMaint[] objects)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#searchPrincipals(com.sap.security.api.IPrincipalSearchFilter)
	 */
	public ISearchResult searchPrincipals(IPrincipalSearchFilter filter)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#savePrincipals(com.sap.security.api.IPrincipalMaint[])
	 */
	public void savePrincipals(IPrincipalMaint[] objects) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#newPrincipals(java.lang.String, int)
	 */
	public IPrincipalMaint[] newPrincipals(
		String principalTypeIdentifier,
		int num) {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#getMutablePrincipal(java.lang.String)
	 */
	public IPrincipalMaint getMutablePrincipal(String uniqueId)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#getPrincipalTypeIdentifier(java.lang.String)
	 */
	public String getPrincipalTypeIdentifier(String uniqueId)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#getPrincipalSearchFilter(boolean, java.lang.String)
	 */
	public IPrincipalSearchFilter getPrincipalSearchFilter(
		boolean orMode,
		String principalTypeIdentifier)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#addPrincipalToParent(java.lang.String, java.lang.String)
	 */
	public void addPrincipalToParent(
		String customObjectId,
		String parentPrincipalId)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#removePrincipalFromParent(java.lang.String, java.lang.String)
	 */
	public void removePrincipalFromParent(
		String customObjectId,
		String parentPrincipalId)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#registerListener(com.sap.security.api.PrincipalListener, int)
	 */
	public void registerListener(
		PrincipalListener objectListener,
		int modifier) {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#unregisterListener(com.sap.security.api.PrincipalListener)
	 */
	public void unregisterListener(PrincipalListener objectListener) {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#newPrincipalMetaData(java.lang.String, int)
	 */
	public IPrincipalMetaData newPrincipalMetaData(
		String principalTypeIdentifier,
		int principalType)
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#registerPrincipalMetaData(com.sap.security.api.IPrincipalMetaData)
	 */
	public void registerPrincipalMetaData(IPrincipalMetaData metadata)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#deregisterPrincipalMetaData(java.lang.String)
	 */
	public void deregisterPrincipalMetaData(String principalTypeIdentifier)
		throws UMException {
			throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#getAvailablePrincipalMetaData()
	 */
	public IPrincipalMetaData[] getAvailablePrincipalMetaData()
		throws UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#getPrincipalMetaData(java.lang.String)
	 */
	public IPrincipalMetaData getPrincipalMetaData(String principalTypeIdentifier)
		throws NoSuchObjectException, UMException {
			throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IPrincipalFactory#addDataSource(java.io.InputStream)
	 */
	public void addDataSource(InputStream configuration) throws UMException {
		throw new UnsupportedOperationException();

	}

	/* (non-Javadoc)
	 * @see com.sap.security.api.IConfigurable#initialize(java.util.Properties)
	 */
	public void initialize(Properties properties) throws UMException {
		throw new UnsupportedOperationException();

	}

}
