package com.sap.engine.services.dc.repo.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.File;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.dc.repo.DeploymentsContainer;
import com.sap.engine.services.dc.repo.Repository;
import com.sap.engine.services.dc.repo.RepositoryComponentsFactory;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.ScaId;
import com.sap.engine.services.dc.repo.Sda;
import com.sap.engine.services.dc.repo.SdaId;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduFileStorageLocation;
import com.sap.engine.services.dc.repo.SduId;
import com.sap.engine.services.dc.repo.SduNotStoredInRepositoryException;
import com.sap.engine.services.dc.repo.SduRepoLocation;
import com.sap.engine.services.dc.repo.SduVisitor;
import com.sap.engine.services.dc.repo.Version;
import com.sap.engine.services.dc.repo.VersionHelper;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-21
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class RepositoryImpl implements Repository {
	
	private Location location = DCLog.getLocation(this.getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Repository#loadSdus()
	 */
	public Set loadSdus(SduVisitor sduVisitor) throws RepositoryException {
		return RepoCfgLoader.getInstance().loadAllSdus(sduVisitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#loadSdus(com.sap.engine.frame
	 * .core.configuration.ConfigurationHandler)
	 */
	public Set loadSdus(ConfigurationHandler cfgHandler, SduVisitor sduVisitor)
			throws RepositoryException {
		return RepoCfgLoader.getInstance().loadAllSdus(cfgHandler, sduVisitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#loadSdu(com.sap.engine.services
	 * .dc.repo.SduRepoLocation)
	 */
	public Sdu loadSdu(SduRepoLocation location) throws RepositoryException {
		RepoCfgLoader.getInstance().load(location);
		if (location.getSdu() != null) {
			return location.getSdu();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#loadSdu(com.sap.engine.services
	 * .dc.repo.SduRepoLocation, com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public Sdu loadSdu(SduRepoLocation location, ConfigurationHandler cfgHandler)
			throws RepositoryException {
		RepoCfgLoader.getInstance().load(location, cfgHandler);
		if (location.getSdu() != null) {
			return location.getSdu();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#loadSdu(com.sap.engine.services
	 * .dc.repo.SduId)
	 */
	public Sdu loadSdu(SduId id) throws RepositoryException {
		final SduRepoLocation sduRepoLocation = SduRepoLocationBuilder
				.getInstance().build(id);
		RepoCfgLoader.getInstance().load(sduRepoLocation);

		return sduRepoLocation.getSdu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#loadSdu(com.sap.engine.services
	 * .dc.repo.SduId,
	 * com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public Sdu loadSdu(SduId sduId, ConfigurationHandler cfgHandler)
			throws RepositoryException {
		final SduRepoLocation sduRepoLocation = SduRepoLocationBuilder
				.getInstance().build(sduId);
		RepoCfgLoader.getInstance().load(sduRepoLocation, cfgHandler);

		return sduRepoLocation.getSdu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Repository#loadSda(java.lang.String,
	 * java.lang.String)
	 */
	public Sda loadSda(String name, String vendor) throws RepositoryException {
		final SdaId sdaId = RepositoryComponentsFactory.getInstance()
				.createSdaId(name, vendor);
		final SduRepoLocation sdaRepoLocation = SduRepoLocationBuilder
				.getInstance().build(sdaId);
		RepoCfgLoader.getInstance().load(sdaRepoLocation);

		return (Sda) sdaRepoLocation.getSdu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.repo.Repository#loadSda(java.lang.String,
	 * java.lang.String, java.lang.String,
	 * com.sap.engine.services.dc.repo.Version)
	 */
	public Sda loadSda(String name, String vendor, String location,
			Version version) throws RepositoryException {
		final SdaId sdaId = RepositoryComponentsFactory.getInstance()
				.createSdaId(name, vendor);
		final SduRepoLocation sdaRepoLocation = SduRepoLocationBuilder
				.getInstance().build(sdaId);
		RepoCfgLoader.getInstance().load(sdaRepoLocation);
		final Sda sda = (Sda) sdaRepoLocation.getSdu();
		final VersionHelper versionHelper = RepositoryComponentsFactory
				.getInstance().createVersionHelper();

		if (sda != null && sda.getLocation().equalsIgnoreCase(location)
				&& versionHelper.isEquivalent(sda.getVersion(), version)) {
			return sda;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#loadSdaArchive(java.lang.String
	 * , java.lang.String, java.lang.String)
	 */
	public File loadSdaArchive(String name, String vendor, String sessionId)
			throws RepositoryException, NullPointerException {
		final SdaId sdaId = RepositoryComponentsFactory.getInstance()
				.createSdaId(name, vendor);
		final SduFileStorageLocation location = RepositoryComponentsFactory
				.getInstance().createSduStorageLocation(sdaId);

		final boolean isLoadedArchiveOk = RepoCfgLoader.getInstance().load(
				location, sessionId);

		if (!isLoadedArchiveOk) {
			throw new SduNotStoredInRepositoryException(
					DCExceptionConstants.REPO_CANNOT_LOAD_ARCHIVE_AS_NOT_STORED,
					new String[] { location.toString() });
		}

		return location.getSduFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#loadScaArchive(java.lang.String
	 * , java.lang.String, java.lang.String)
	 */
	public File loadScaArchive(String name, String vendor, String sessionId)
			throws RepositoryException, NullPointerException {
		final ScaId scaId = RepositoryComponentsFactory.getInstance()
				.createScaId(name, vendor);
		final SduFileStorageLocation location = RepositoryComponentsFactory
				.getInstance().createSduStorageLocation(scaId);

		RepoCfgLoader.getInstance().load(location, sessionId);

		return location.getSduFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#persistSdu(com.sap.engine.
	 * services.dc.repo.Sdu, java.lang.String)
	 */
	public void persistSdu(Sdu sdu, String sduAbsoluteFilePath)
			throws RepositoryException {
		final SduRepoLocation sduRepoLocation = SduRepoLocationBuilder
				.getInstance().build(sdu);
		SduFileStorageLocation sduFileStorageLocation = SduFileStorageLocationBuilder
				.getInstance()
				.build(sdu.getId(), new File(sduAbsoluteFilePath));

		RepoCfgPersistor.getInstance().persist(sduRepoLocation,
				sduFileStorageLocation);
	}

	
	
	
	public void persistSdu(Map<Sdu, String> sdu_path)
			throws RepositoryException {
		Map<SduRepoLocation, SduFileStorageLocation> sdu_pathLoc = 
			new LinkedHashMap<SduRepoLocation, SduFileStorageLocation>(sdu_path.size()); 
		for(Sdu sdu : sdu_path.keySet()){
			SduRepoLocation sduRepoLocation = SduRepoLocationBuilder
			 .getInstance().build(sdu);
			SduFileStorageLocation sduFileStorageLocation = SduFileStorageLocationBuilder
			 .getInstance().build(sdu.getId(), new File(sdu_path.get(sdu)));
			sdu_pathLoc.put(sduRepoLocation, sduFileStorageLocation);			
		}
		
		RepoCfgPersistor.getInstance().persist(sdu_pathLoc);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#persistSdu(com.sap.engine.
	 * services.dc.repo.Sdu, java.lang.String,
	 * com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public void persistSdu(Sdu sdu, String sduAbsoluteFilePath,
			ConfigurationHandler cfgHandlel) throws RepositoryException {
		persistSdu(sdu, sduAbsoluteFilePath, cfgHandlel, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#persistSdu(com.sap.engine.
	 * services.dc.repo.Sdu, java.lang.String,
	 * com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public void persistSdu(Sdu sdu, String sduAbsoluteFilePath,
			ConfigurationHandler cfgHandler, Connection conn)
			throws RepositoryException {
		if (location.beDebug()) {
			traceDebug(location, "Starting to persist SDU [{0}]",
					new Object[] { sdu.getId() });
		}

		final SduRepoLocation sduRepoLocation = SduRepoLocationBuilder
				.getInstance().build(sdu);
		SduFileStorageLocation sduFileStorageLocation = SduFileStorageLocationBuilder
				.getInstance()
				.build(sdu.getId(), new File(sduAbsoluteFilePath));

		if (location.beDebug()) {
			traceDebug(location, "Persisting SDU data into DB");
		}
		RepoCfgPersistor.getInstance().persist(sduRepoLocation,
				sduFileStorageLocation, cfgHandler, conn);

		if (location.bePath()) {
			tracePath(
					location,
					"SDU [{0}] was persisted into repository",
					new Object[] { sdu.getId() });
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#persistSdu(com.sap.engine.
	 * services.dc.repo.SduRepoLocation)
	 */
	public void persistSdu(SduRepoLocation location) throws RepositoryException {
		RepoCfgPersistor.getInstance().persist(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#persistSdu(com.sap.engine.
	 * services.dc.repo.SduRepoLocation,
	 * com.sap.engine.frame.core.configuration.ConfigurationHandler, )
	 */
	public void persistSdu(SduRepoLocation sduRepoLocation,
			ConfigurationHandler cfgHandler, Connection connection)
			throws RepositoryException {
		RepoCfgPersistor.getInstance().persist(sduRepoLocation, cfgHandler,
				connection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#delete(com.sap.engine.services
	 * .dc.repo.Sdu)
	 */
	public void delete(Sdu sdu) throws RepositoryException {
		final SduRepoLocation sduRepoLocation = SduRepoLocationBuilder
				.getInstance().build(sdu);
		SduFileStorageLocation sduFileStorageLocation = SduFileStorageLocationBuilder
				.getInstance().build(sdu.getId());

		RepoCfgPersistor.getInstance().delete(sduRepoLocation,
				sduFileStorageLocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#delete(com.sap.engine.services
	 * .dc.repo.SduRepoLocation)
	 */
	public void delete(SduRepoLocation location) throws RepositoryException {
		RepoCfgPersistor.getInstance().delete(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.repo.Repository#createDeploymentsContainer
	 * (com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public DeploymentsContainer createDeploymentsContainer(
			final Set allSdus, ConfigurationHandler cfgHandler) throws RepositoryException {
		DCLog.TimeWatcher timeWatcher = DCLog.TimeWatcher.getInstance();

		final DeploymentsContainer deploymentsContainer = new DeploymentsContainerImpl();
		
		deploymentsContainer.init(allSdus);
		if (location.bePath()) {
			tracePath(location, 
					"DeploymentsContainer was initialized : [{0}]. Total time elapsed: [{1}]",
					new Object[] { timeWatcher.getElapsedTimeAsString(),
							timeWatcher.getTotalElapsedTimeAsString() });
		}

		return deploymentsContainer;
	}

	public DeploymentsContainer createDeploymentsContainerWithoutReferences(
			Set allSdus) throws RepositoryException {
		DCLog.TimeWatcher timeWatcher = DCLog.TimeWatcher.getInstance();

		final DeploymentsContainer deploymentsContainer = new DeploymentsContainerImplWithoutReferences();
		
		deploymentsContainer.init(allSdus);
		if (location.bePath()) {
			tracePath(location, 
					"DeploymentsContainerWithoutReferences was initialized : [{0}]. Total time elapsed: [{1}]",
					new Object[] { timeWatcher.getElapsedTimeAsString(),
							timeWatcher.getTotalElapsedTimeAsString() });
		}

		return deploymentsContainer;
	}

	public DeploymentsContainer createDeploymentsContainer() {
		return new DeploymentsContainerImpl();
	}
	
	public Set loadAllSduRepoLocations(ConfigurationHandler cfgHandler) 
	throws RepositoryException {
		return RepoCfgLoader.getInstance().loadAllSduRepoLocations(cfgHandler);
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.services.dc.repo.Repository#persistSdu(java.util.Map,
	 * 	com.sap.engine.frame.core.configuration.ConfigurationHandler, java.sql.Connection)
	 */
	public void persistSdu(Map<Sdu, String> sdu_path,
			ConfigurationHandler cfgHandler, Connection conn)
			throws RepositoryException {
		for(Sdu sdu : sdu_path.keySet()){
			persistSdu(sdu, sdu_path.get(sdu), cfgHandler, conn);		
		}
	}
	
	
}
