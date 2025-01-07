package com.sap.engine.services.dc.repo;

import java.io.File;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-19
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
public interface Repository {

	/**
	 * Loads all the deployed <code>Sdu</code>s.
	 * 
	 * @return <code>Collection</code> with all the <code>Sdu</code>s which have
	 *         been deployed. If there are no deployed <code>Sdu</code>s, the
	 *         operation returns an empty <code>Collection</code>.
	 * @throws RepositoryException
	 */
	public Set loadSdus(SduVisitor sduVisitor) throws RepositoryException;

	/**
	 * Loads all the deployed <code>Sdu</code>s.
	 * 
	 * @param cfgHandler
	 *            which has to be used from the loading mechanism
	 * @return <code>Collection</code> with all the <code>Sdu</code>s which have
	 *         been deployed. If there are no deployed <code>Sdu</code>s, the
	 *         operation returns an empty <code>Collection</code>.
	 * @throws RepositoryException
	 */
	public Set loadSdus(ConfigurationHandler cfgHandler, SduVisitor sduVisitor)
			throws RepositoryException;

	/**
	 * Loads the <code>Sdu</code> which ir match to the specified one
	 * 
	 * @param sduId
	 * @return <code>Sdu</code> which is found. If there is no such
	 *         <code>Sdu</code> the operation returns <code>null</code>.
	 * @throws RepositoryException
	 */
	public Sdu loadSdu(SduId sduId) throws RepositoryException;

	/**
	 * Loads the <code>Sdu</code> which ir match to the specified one
	 * 
	 * @param sduId
	 * @return <code>Sdu</code> which is found. If there is no such
	 *         <code>Sdu</code> the operation returns <code>null</code>.
	 * @throws RepositoryException
	 */
	public Sdu loadSdu(SduId sduId, ConfigurationHandler cfgHandler)
			throws RepositoryException;

	/**
	 * Loads the <code>Sdu</code> which properties match to the ones specified
	 * as arguments.
	 * 
	 * @param location
	 * @return <code>Sdu</code> which is found. If there is no such
	 *         <code>Sdu</code> the operation returns <code>null</code>.
	 * @throws RepositoryException
	 */
	public Sdu loadSdu(SduRepoLocation location) throws RepositoryException;
	
	/**
	 * Loads the <code>Sdu</code> which properties match to the ones specified
	 * as arguments, using the provided configuration handler..
	 * 
	 * @param location sdu location in the DC repository; cannot be <tt>null</tt>
	 * @param cfgHandler configuration handler to use; cannot be <tt>null</tt>
	 * @return <code>Sdu</code> which is found. If there is no such
	 *         <code>Sdu</code> the operation returns <code>null</code>.
	 * @throws RepositoryException
	 */
	public Sdu loadSdu(SduRepoLocation location, ConfigurationHandler cfgHandler) throws RepositoryException;
	
	

	/**
	 * Loads the <code>Sda</code> which properties match to the ones specified
	 * as arguments.
	 * 
	 * @param name
	 * @param vendor
	 * @return <code>Sda</code> which is found. If there is no such
	 *         <code>Sda</code> the operation returns <code>null</code>.
	 * @throws RepositoryException
	 */
	public Sda loadSda(String name, String vendor) throws RepositoryException;

	/**
	 * Loads the <code>Sda</code> which properties match to the ones specified
	 * as arguments.
	 * 
	 * @param name
	 * @param vendor
	 * @param location
	 * @param version
	 * @return <code>Sda</code> which is found. If there is no such
	 *         <code>Sda</code> the operation returns <code>null</code>.
	 * @throws RepositoryException
	 */
	public Sda loadSda(String name, String vendor, String location,
			Version version) throws RepositoryException;

	public File loadSdaArchive(String name, String vendor, String sessionId)
			throws RepositoryException, NullPointerException;

	public File loadScaArchive(String name, String vendor, String sessionId)
			throws RepositoryException, NullPointerException;

	/**
	 * Persists the given <code>Sdu</code> and sduAbsoluteFilePath.
	 * 
	 * @param sdu
	 * @param sduAbsoluteFilePath
	 * @throws RepositoryException
	 */
	public void persistSdu(Sdu sdu, String sduAbsoluteFilePath)
			throws RepositoryException;

	/**
	 * Persists the given <code>Sdu</code>s and its <code>sduAbsoluteFilePath</code>s
	 * 
	 * @param sdu_filePath map of Sdu and its Archive file path. If you want to put items 
	 * in any order you can use Map with ordered key list (for example LinkedHashMap)
	 * @throws RepositoryException
	 */
	public void persistSdu(Map<Sdu, String> sdu_path)
			throws RepositoryException;
	
	/**
	 * Persists the given <code>Sdu</code>s and its <code>sduAbsoluteFilePath</code>s
	 * 
	 * @param sdu_filePath map of Sdu and its Archive file path. If you want to put items 
	 * in any order you can use Map with ordered key list (for example LinkedHashMap)
	 * @param cfgHandler configuration handler to use; cannot be <tt>null</tt>
	 * @param conn connection to database to use; cannot be <tt>null</tt>
	 * @throws RepositoryException
	 */
	public void persistSdu(Map<Sdu, String> sdu_path, ConfigurationHandler cfgHandler, Connection conn)
			throws RepositoryException;
	
	
	/**
	 * Persists the given <code>Sdu</code> and sduAbsoluteFilePath.
	 * 
	 * @param sdu
	 * @param sduAbsoluteFilePath
	 * @param cfgHandlel
	 * @throws RepositoryException
	 */
	public void persistSdu(Sdu sdu, String sduAbsoluteFilePath,
			ConfigurationHandler cfgHandlel) throws RepositoryException;

	/**
	 * Persists the given <code>Sdu</code> and sduAbsoluteFilePath.
	 * 
	 * @param sdu
	 * @param sduAbsoluteFilePath
	 * @param cfgHandlel
	 * @param conn
	 * @throws RepositoryException
	 */
	public void persistSdu(Sdu sdu, String sduAbsoluteFilePath,
			ConfigurationHandler cfgHandlel, Connection conn)
			throws RepositoryException;

	/**
	 * Persists the given <code>SduRepoLocation</code>.
	 * 
	 * @param location
	 *            <code>SduRepoLocation</code>
	 * @throws RepositoryException
	 */
	public void persistSdu(SduRepoLocation location) throws RepositoryException;

	/**
	 * Persists the given <code>SduRepoLocation</code>.
	 * 
	 * @param sduRepoLocation
	 *            <code>SduRepoLocation</code>
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @param connection
	 *            <code>Connection</code>
	 * @throws RepositoryException
	 */
	public void persistSdu(SduRepoLocation sduRepoLocation,
			ConfigurationHandler cfgHandler, Connection connection)
			throws RepositoryException;

	/**
	 * Deletes the given <code>Sdu</code>.
	 * 
	 * @param sdu
	 * @throws RepositoryException
	 */
	public void delete(Sdu sdu) throws RepositoryException;

	public DeploymentsContainer createDeploymentsContainer(
			Set allSdus, ConfigurationHandler cfgHandler) throws RepositoryException;
	
	public DeploymentsContainer createDeploymentsContainerWithoutReferences(
			Set allSdus) throws RepositoryException;
	

	public DeploymentsContainer createDeploymentsContainer();
	
	public Set loadAllSduRepoLocations(ConfigurationHandler cfgHandler) throws RepositoryException ;

}
