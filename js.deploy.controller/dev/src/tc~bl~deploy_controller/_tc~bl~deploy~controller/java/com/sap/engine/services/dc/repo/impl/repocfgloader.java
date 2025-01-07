package com.sap.engine.services.dc.repo.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.dc.manage.PathsConfigurer;
import com.sap.engine.services.dc.repo.LocationConstants;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.SduFileStorageLocation;
import com.sap.engine.services.dc.repo.SduRepoLocation;
import com.sap.engine.services.dc.repo.SduVisitor;
import com.sap.engine.services.dc.util.CfgUtils;
import com.sap.engine.services.dc.util.ValidatorUtils;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.logging.DCLog.TimeWatcher;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-2
 * 
 * @author Dimitar Dimitrov, Anton Georgiev
 * @version 1.0
 * @since 7.0
 * 
 */
final class RepoCfgLoader extends AbstractRepoCfgMapper {
	
	private Location location = getLocation(this.getClass());

	private static final RepoCfgLoader INSTANCE = new RepoCfgLoader();

	static RepoCfgLoader getInstance() {
		return INSTANCE;
	}

	private RepoCfgLoader() {
	}

	Set loadAllSduRepoLocations(final ConfigurationHandler cfgHandler)
			throws RepositoryException {
		String[] sdaCfgNames = null;
		try {
			sdaCfgNames = getAllSubConfigurationNames(cfgHandler,
					LocationConstants.ROOT_REPO_DC);
		} catch (ConfigurationException ce) {
			throw handleLoadAllSdus(ce, LocationConstants.ROOT_REPO_DC);
		}
		String[] scaCfgNames = null;
		try {
			scaCfgNames = getAllSubConfigurationNames(cfgHandler,
					LocationConstants.ROOT_REPO_SC);
		} catch (ConfigurationException ce) {
			throw handleLoadAllSdus(ce, LocationConstants.ROOT_REPO_SC);
		}

		final Set<SduRepoLocation> result = new HashSet<SduRepoLocation>(
				sdaCfgNames.length + scaCfgNames.length);
		result.addAll(getSduRepoLocations(sdaCfgNames,
				LocationConstants.ROOT_REPO_DC));
 		result.addAll(getSduRepoLocations(scaCfgNames,
				LocationConstants.ROOT_REPO_SC));
		return result;
	}

	private Set<SduRepoLocation> getSduRepoLocations(
			final String[] sduCfgNames, final String sduIdRoot) {
		if (sduCfgNames == null) {
			return null;
		}

		final Set<SduRepoLocation> result = new HashSet<SduRepoLocation>(
				sduCfgNames.length);
		for (final String sduId : sduCfgNames) {
			result.add(SduRepoLocationBuilder.getInstance().build(sduIdRoot,
					sduId));
		}
		return result;
	}

	private String[] getAllSubConfigurationNames(
			final ConfigurationHandler cfgHandler, final String sduIdRoot)
			throws ConfigurationException, NullPointerException {
		final Configuration rootCfg = CfgUtils.openCfgRead(cfgHandler,
				sduIdRoot);
		try {
			return rootCfg.getAllSubConfigurationNames();
		} finally {
			cfgHandler.closeConfiguration(rootCfg);
		}
	}

	/**
	 * The operation loads all the <code>Sdu</code>s, both <code>Sda</code>s and
	 * <code>Sca</code>s.
	 * 
	 * @return <code>Set</code> with all the <code>Sdu</code>s.
	 * @throws RepositoryException
	 *             if the <code>Sdu</code>s could not be loaded.
	 */
	Set loadAllSdus(SduVisitor sduVisitor) throws NullPointerException,
			RepositoryException {

		return this.loadAllSdus(getCfgHandler(), sduVisitor);
	}

	/**
	 * The operation loads all the <code>Sdu</code>s, both <code>Sda</code>s and
	 * <code>Sca</code>s.
	 * 
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @return <code>Set</code> with all the <code>Sdu</code>s.
	 * @throws RepositoryException
	 *             if the <code>Sdu</code>s could not be loaded.
	 * @throws NullPointerException
	 *             if the <code>ConfigurationHandler</code> is null.
	 */
	Set loadAllSdus(ConfigurationHandler cfgHandler, SduVisitor sduVisitor)
			throws NullPointerException, RepositoryException {
		ValidatorUtils.validate(cfgHandler);

		final Set allSdus = new HashSet();

		loadAllSdas(cfgHandler, allSdus, sduVisitor);
		loadAllScas(cfgHandler, allSdus, sduVisitor);

		return allSdus;
	}

	/**
	 * The operation loads the <code>Sdu</code> corresponding to the specified
	 * <code>SduRepoLocation</code>. After the <code>Sdu</code> is loaded it is
	 * set to the <code>SduRepoLocation</code>, therefore after performing this
	 * operation one could get the loaded <code>Sdu</code> by calling
	 * <code>SduRepoLocation.getSdu()</code>.
	 * 
	 * @param location
	 *            <code>SduRepoLocation</code> specifies where the
	 *            <code>Sdu</code> is located into the repository.
	 * @throws RepositoryException
	 *             if the <code>Sdu</code> corresponding to the specified
	 *             <code>SduRepoLocation</code> could not be loaded.
	 * @throws NullPointerException
	 *             is the specified <code>SduRepoLocation</code> is null.
	 */
	void load(SduRepoLocation location) throws NullPointerException,
			RepositoryException {
		if (location.getConfiguration() == null) {
			load(location, getCfgHandler());
		} else {
			loadWithoutCfgHandler(location);
		}
	}

	/**
	 * The operation loads the <code>Sdu</code> corresponding to the specified
	 * <code>SduRepoLocation</code>. After the <code>Sdu</code> is loaded it is
	 * set to the <code>SduRepoLocation</code>, therefore after performing this
	 * operation one could get the loaded <code>Sdu</code> by calling
	 * <code>SduRepoLocation.getSdu()</code>.
	 * 
	 * @param location
	 *            <code>SduRepoLocation</code> specifies where the
	 *            <code>Sdu</code> is located into the repository.
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws RepositoryException
	 *             if the <code>Sdu</code> corresponding to the specified
	 *             <code>SduRepoLocation</code> could not be loaded.
	 * @throws NullPointerException
	 *             is the specified <code>SduRepoLocation</code> or
	 *             <code>ConfigurationHandler</code> are null.
	 */
	void load(SduRepoLocation location, ConfigurationHandler cfgHandler)
			throws NullPointerException, RepositoryException {
		ValidatorUtils.validate(location);
		ValidatorUtils.validate(cfgHandler);

		try {
			loadAction(location, cfgHandler);
		} catch (ConfigurationException ce) {
			throw handleLoadSduRepoLocation(ce, location);
		}
	}

	/**
	 * The operation loads the <code>File</code> corresponding to the specified
	 * <code>SduFileStorageLocation</code>. After the <code>File</code> is
	 * loaded ot is set to the <code>SduFileStorageLocation</code>, therefore
	 * after performing this operation one could get the loaded
	 * <code>File</code> by calling
	 * <code>SduFileStorageLocation.getSduFile()</code>.
	 * 
	 * @param location
	 *            <code>SduFileStorageLocation</code> specifies where the
	 *            <code>File</code> is located into the repository.
	 * @param sessionId
	 *            <code>String</code> used for creation of a directory where the
	 *            file will be stored.
	 * @throws RepositoryException
	 *             if the <code>File</code> corresponding to the specified
	 *             <code>SduFileStorageLocation</code> could not be loaded.
	 * @throws NullPointerException
	 *             is the specified <code>SduFileStorageLocation</code> is null.
	 * @return false if SAP_MANIFEST.MF is stored for given
	 *         <code>Sdu</Sdu>, otherwise true.
	 */
	boolean load(SduFileStorageLocation location, String sessionId)
			throws NullPointerException, RepositoryException {
		final ConfigurationHandler cfgHandler = getCfgHandler();

		return load(location, sessionId, cfgHandler);
	}

	/**
	 * The operation loads the <code>File</code> corresponding to the specified
	 * <code>SduFileStorageLocation</code>. After the <code>File</code> is
	 * loaded ot is set to the <code>SduFileStorageLocation</code>, therefore
	 * after performing this operation one could get the loaded
	 * <code>File</code> by calling
	 * <code>SduFileStorageLocation.getSduFile()</code>.
	 * 
	 * @param location
	 *            <code>SduFileStorageLocation</code> specifies where the
	 *            <code>File</code> is located into the repository.
	 * @param sessionId
	 *            <code>String</code> used for creation of a directory where the
	 *            file will be stored.
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws RepositoryException
	 *             if the <code>File</code> corresponding to the specified
	 *             <code>SduFileStorageLocation</code> could not be loaded.
	 * @throws NullPointerException
	 *             is the specified <code>SduFileStorageLocation</code> or
	 *             <code>ConfigurationHandler</code> are null.
	 * @return false if SAP_MANIFEST.MF is stored for given
	 *         <code>Sdu</Sdu>, othewise true.
	 */
	boolean load(SduFileStorageLocation location, String sessionId,
			ConfigurationHandler cfgHandler) throws NullPointerException,
			RepositoryException {
		ValidatorUtils.validate(location);
		ValidatorUtils.validate(cfgHandler);

		final String storeDir = PathsConfigurer.getInstance()
				.getStorageDirName(sessionId);

		try {
			SduFileStorageLocationLoadVisitor loaderVisitor = new SduFileStorageLocationLoadVisitor(
					cfgHandler, storeDir);
			location.accept(loaderVisitor);
			loaderVisitor.getException();
			return loaderVisitor.isArchiveStored();
		} catch (IllegalArgumentException iae) {
			throw handleLoadSduFileStorageLocation(iae, storeDir, location);
		} catch (IOException ioe) {
			throw handleLoadSduFileStorageLocation(ioe, storeDir, location);
		} catch (ConfigurationException ce) {
			throw handleLoadSduFileStorageLocation(ce, storeDir, location);
		}
	}

	void loadAllSdus(ConfigurationHandler cfgHandler, String sduIdRoot,
			Set allSdus, SduVisitor sduVisitor) throws ConfigurationException,
			NullPointerException {

		TimeWatcher tw = TimeWatcher.getInstance();
		try {
			final String subNames[] = getAllSubConfigurationNames(cfgHandler,
					sduIdRoot);

			String sduId = null;
			SduRepoLocation location;
			if (subNames != null) {
				for (int i = 0; i < subNames.length; i++) {
					sduId = subNames[i];
					location = SduRepoLocationBuilder.getInstance().build(
							sduIdRoot, sduId);
					loadAction(location, cfgHandler);
					allSdus.add(location.getSdu());
					if (sduVisitor != null) {
						location.getSdu().accept(sduVisitor);
					}
				}
			}
		} finally {
			if (location.beDebug()) {
				traceDebug(location, "{0} loaded : {2}", new Object[] {
						sduIdRoot, tw.getElapsedTimeAsString() });
			}
		}
	}

	private void loadAllSdas(ConfigurationHandler cfgHandler, Set allSdus,
			SduVisitor sduVisitor) throws NullPointerException,
			RepositoryException {
		String sduRoot = null;
		try {
			sduRoot = LocationConstants.ROOT_REPO_DC;
			loadAllSdus(cfgHandler, sduRoot, allSdus, sduVisitor);
		} catch (ConfigurationException ce) {
			throw handleLoadAllSdus(ce, sduRoot);
		}
	}

	private void loadAllScas(ConfigurationHandler cfgHandler, Set allSdus,
			SduVisitor sduVisitor) throws NullPointerException,
			RepositoryException {
		String sduRoot = null;
		try {
			sduRoot = LocationConstants.ROOT_REPO_SC;
			loadAllSdus(cfgHandler, sduRoot, allSdus, sduVisitor);
		} catch (ConfigurationException ce) {
			throw handleLoadAllSdus(ce, sduRoot);
		}
	}

	private RepositoryException handleLoadSduFileStorageLocation(Exception ex,
			String storeDir, SduFileStorageLocation location) {
		return new RepositoryException(
				DCExceptionConstants.REPO_CANNOT_LOAD_TO, new String[] {
						storeDir, location.getLocation() }, ex);
	}

	private RepositoryException handleLoadSduRepoLocation(Exception ex,
			SduRepoLocation location) {
		return new RepositoryException(DCExceptionConstants.REPO_CANNOT_LOAD,
				new String[] { location.getLocation() }, ex);
	}

	private RepositoryException handleLoadAllSdus(Exception ex, String cfg) {
		return new RepositoryException(
				DCExceptionConstants.REPO_CANNOT_LOAD_ALL,
				new String[] { cfg }, ex);
	}

	private void loadWithoutCfgHandler(SduRepoLocation location)
			throws NullPointerException, RepositoryException {
		ValidatorUtils.validate(location);

		try {
			loadAction(location, null);
		} catch (ConfigurationException ce) {
			throw handleLoadSduRepoLocation(ce, location);
		}
	}

	void loadAction(SduRepoLocation location, ConfigurationHandler cfgHandler)
			throws ConfigurationException {
		SduRepoLocationLoaderVisitor loaderVisitor = new SduRepoLocationLoaderVisitor(
				cfgHandler);
		location.accept(loaderVisitor);
		loaderVisitor.getException();
	}

}
