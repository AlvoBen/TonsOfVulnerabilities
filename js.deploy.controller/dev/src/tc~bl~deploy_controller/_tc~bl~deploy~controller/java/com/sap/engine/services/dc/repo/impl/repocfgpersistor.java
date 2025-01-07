package com.sap.engine.services.dc.repo.impl;

import java.io.IOException;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.dc.repo.RepositoryException;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduFileStorageLocation;
import com.sap.engine.services.dc.repo.SduRepoLocation;
import com.sap.engine.services.dc.util.ValidatorUtils;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.dc.util.readers.sdu_reader.SduReaderException;

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
final class RepoCfgPersistor extends AbstractRepoCfgMapper {

	private static final RepoCfgPersistor INSTANCE = new RepoCfgPersistor();

	static RepoCfgPersistor getInstance() {
		return INSTANCE;
	}

	private RepoCfgPersistor() {
	}

	/**
	 * The operation persists the <code>Sdu</code> corresponding to the
	 * specified <code>SduRepoLocation</code> and the <code>File</code>
	 * corresponding to the specified <code>SduFileStorageLocation</code> into
	 * the repository. If there is already persisted file located on the
	 * specified <code>SduFileStorageLocation</code>, then the operation deletes
	 * it and then persists the new one.
	 * 
	 * @param location
	 *            <code>SduRepoLocation</code> specifies where the
	 *            <code>Sdu</code> is located into the repository.
	 * @param location
	 *            <code>SduFileStorageLocation</code> specifies where the
	 *            <code>File</code> is located into the repository.
	 * @throws RepositoryException
	 *             if the <code>Sdu</code> corresponding to the specified
	 *             <code>SduRepoLocation</code> or the <code>File</code>
	 *             corresponding to the specified
	 *             <code>SduFileStorageLocation</code> could not be persisted.
	 * @throws NullPointerException
	 *             if the specified <code>SduRepoLocation</code> or
	 *             <code>SduFileStorageLocation</code> are null.
	 * @throws IllegalArgumentException
	 *             if the <code>File</code> in
	 *             <code>SduFileStorageLocation</code> does not exist or is
	 *             directory.
	 */
	void persist(SduRepoLocation sduRepoLocation,
			SduFileStorageLocation sduFileStorageLocation)
			throws NullPointerException, IllegalArgumentException,
			RepositoryException {
		final ConfigurationHandler cfgHandler = this.getCfgHandler();
		Connection conn = null;

		try {
			conn = this.getDBConnection();
			persist(sduRepoLocation, sduFileStorageLocation, cfgHandler, conn);
		} finally {
			closeDBConnection(conn);
		}
	}

	
	/**
	 * The operation persists the <code>Sdu</code>s corresponding to the
	 * specified <code>SduRepoLocation</code>s and the <code>File</code>s
	 * corresponding to the specified <code>SduFileStorageLocation</code>s into
	 * the repository. If there is already persisted file located on the
	 * specified <code>SduFileStorageLocation</code>, then the operation deletes
	 * it and then persists the new one.
	 * 
	 * @param sdu_pathLoc 
	 *            map of <code>SduRepoLocation</code>s 
	 *            and <code>SduFileStorageLocation</code>s.
	 * @throws RepositoryException
	 *             if the <code>Sdu</code> corresponding to the specified
	 *             <code>SduRepoLocation</code> or the <code>File</code>
	 *             corresponding to the specified
	 *             <code>SduFileStorageLocation</code> could not be persisted.
	 * @throws NullPointerException
	 *             if the specified <code>SduRepoLocation</code> or
	 *             <code>SduFileStorageLocation</code> are null.
	 * @throws IllegalArgumentException
	 *             if the <code>File</code> in
	 *             <code>SduFileStorageLocation</code> does not exist or is
	 *             directory.
	 */
	void persist(Map<SduRepoLocation, SduFileStorageLocation> sdu_pathLoc)
			throws NullPointerException, IllegalArgumentException,
			RepositoryException {
		final ConfigurationHandler cfgHandler = this.getCfgHandler();
		Connection conn = null;
		try {
			conn = this.getDBConnection();
			ValidatorUtils.validate(cfgHandler);
			try {
				for(SduRepoLocation sduRepoLocation : sdu_pathLoc.keySet()){
					persistSduRepoLocation(sduRepoLocation, cfgHandler, conn);
					persistSduFileStorageLocation(sdu_pathLoc.get(sduRepoLocation),
							sduRepoLocation.getSdu(), cfgHandler);	
				}
				commit(cfgHandler);
			} catch (RepositoryException re) {
				rollback(cfgHandler);
				throw re;
			} finally {
				closeAllConfigurations(cfgHandler);
			}
		} finally {
			closeDBConnection(conn);
		}
	}
	
	
	/**
	 * The operation persists the <code>Sdu</code> corresponding to the
	 * specified <code>SduRepoLocation</code> into the repository.
	 * 
	 * @param location
	 *            <code>SduRepoLocation</code> specifies where the
	 *            <code>Sdu</code> is located into the repository.
	 * @throws RepositoryException
	 *             if the <code>Sdu</code> corresponding to the specified
	 *             <code>SduRepoLocation</code> could not be persisted.
	 * @throws NullPointerException
	 *             if the specified <code>SduRepoLocation</code> is null.
	 */
	void persist(SduRepoLocation sduRepoLocation) throws NullPointerException,
			RepositoryException {
		ValidatorUtils.validate(sduRepoLocation);

		Connection conn = null;
		try {
			conn = this.getDBConnection();

			persist(sduRepoLocation, this.getCfgHandler(), conn);
			// if (sduRepoLocation.getConfiguration() == null) {
			// persist(sduRepoLocation, this.getCfgHandler(), conn);
			// } else {
			// persistWithoutCfgHandler(sduRepoLocation, conn);
			// }
		} finally {
			closeDBConnection(conn);
		}
	}

	/**
	 * The operation persists the <code>Sdu</code> corresponding to the
	 * specified <code>SduRepoLocation</code> and the <code>File</code>
	 * corresponding to the specified <code>SduFileStorageLocation</code> into
	 * the repository. If there is already persisted file located on the
	 * specified <code>SduFileStorageLocation</code>, then the operation deletes
	 * it and then persists the new one.
	 * 
	 * @param location
	 *            <code>SduRepoLocation</code> specifies where the
	 *            <code>Sdu</code> is located into the repository.
	 * @param location
	 *            <code>SduFileStorageLocation</code> specifies where the
	 *            <code>File</code> is located into the repository.
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws RepositoryException
	 *             if the <code>Sdu</code> corresponding to the specified
	 *             <code>SduRepoLocation</code> or the <code>File</code>
	 *             corresponding to the specified
	 *             <code>SduFileStorageLocation</code> could not be persisted.
	 * @throws NullPointerException
	 *             if the specified <code>SduRepoLocation</code> or
	 *             <code>SduFileStorageLocation</code> or
	 *             <code>ConfigurationHandler</code> are null.
	 * @throws IllegalArgumentException
	 *             if the <code>File</code> in
	 *             <code>SduFileStorageLocation</code> does not exist or is
	 *             directory.
	 */
	void persist(SduRepoLocation sduRepoLocation,
			SduFileStorageLocation sduFileStorageLocation,
			ConfigurationHandler cfgHandler, Connection conn)
			throws NullPointerException, IllegalArgumentException,
			RepositoryException {
		ValidatorUtils.validate(cfgHandler);

		try {
			persistSduRepoLocation(sduRepoLocation, cfgHandler, conn);
			persistSduFileStorageLocation(sduFileStorageLocation,
					sduRepoLocation.getSdu(), cfgHandler);

			commit(cfgHandler);
		} catch (RepositoryException re) {
			rollback(cfgHandler);

			throw re;
		} finally {
			closeAllConfigurations(cfgHandler);
		}
	}

	/**
	 * The operation persists the <code>Sdu</code> corresponding to the
	 * specified <code>SduRepoLocation</code> into the repository.
	 * 
	 * @param location
	 *            <code>SduRepoLocation</code> specifies where the
	 *            <code>Sdu</code> is located into the repository.
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws RepositoryException
	 *             if the <code>Sdu</code> corresponding to the specified
	 *             <code>SduRepoLocation</code> could not be persisted.
	 * @throws NullPointerException
	 *             if the specified <code>SduRepoLocation</code> or
	 *             <code>ConfigurationHandler</code> are null.
	 */
	void persist(SduRepoLocation sduRepoLocation,
			ConfigurationHandler cfgHandler, Connection conn)
			throws NullPointerException, RepositoryException {
		ValidatorUtils.validate(cfgHandler);

		try {
			persistSduRepoLocation(sduRepoLocation, cfgHandler, conn);

			commit(cfgHandler);
		} catch (RepositoryException re) {
			rollback(cfgHandler);
			throw re;
		} finally {
			closeAllConfigurations(cfgHandler);
		}
	}

	/**
	 * The operation deletes the specified <code>SduRepoLocation</code> and
	 * <code>SduFileStorageLocation</code> from the repository.
	 * 
	 * @param location
	 *            <code>SduRepoLocation</code> specifies what has to be deleted
	 *            from the repository. Updates the described references in the
	 *            <code>Sdu</code> and refered <code>Sdu</code>.
	 * @param location
	 *            <code>SduFileStorageLocation</code> specifies what has to be
	 *            deleted from the repository.
	 * @throws RepositoryException
	 *             if the <code>SduRepoLocation</code> or
	 *             <code>SduFileStorageLocation</code> could not be deleted.
	 * @throws NullPointerException
	 *             if the specified <code>SduRepoLocation</code> or
	 *             <code>SduFileStorageLocation</code> are null.
	 */
	void delete(SduRepoLocation sduRepoLocation,
			SduFileStorageLocation sduFileStorageLocation)
			throws NullPointerException, RepositoryException {
		final ConfigurationHandler cfgHandler = getCfgHandler();

		delete(sduRepoLocation, sduFileStorageLocation, cfgHandler);
	}

	/**
	 * The operation deletes the specified <code>SduRepoLocation</code> from the
	 * repository.
	 * 
	 * @param location
	 *            <code>SduRepoLocation</code> specifies what has to be deleted
	 *            from the repository. Updates the described references in the
	 *            <code>Sdu</code> and refered <code>Sdu</code>.
	 * @throws RepositoryException
	 *             if the <code>SduRepoLocation</code> could not be deleted.
	 * @throws NullPointerException
	 *             if the specified <code>SduRepoLocation</code>is null.
	 */
	void delete(SduRepoLocation sduRepoLocation) throws NullPointerException,
			RepositoryException {
		final ConfigurationHandler cfgHandler = getCfgHandler();

		delete(sduRepoLocation, cfgHandler);
	}

	/**
	 * The operation deletes the specified <code>SduRepoLocation</code> and
	 * <code>SduFileStorageLocation</code> from the repository.
	 * 
	 * @param sduRepoLocation
	 *            <code>SduRepoLocation</code> specifies what has to be deleted
	 *            from the repository. Updates the described references in the
	 *            <code>Sdu</code> and refered <code>Sdu</code>.
	 * @param sduFileStorageLocation
	 *            <code>SduFileStorageLocation</code> specifies what has to be
	 *            deleted from the repository.
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws RepositoryException
	 *             if the <code>SduRepoLocation</code> or
	 *             <code>SduFileStorageLocation</code> could not be deleted.
	 * @throws NullPointerException
	 *             if the specified <code>SduRepoLocation</code>,
	 *             <code>SduFileStorageLocation</code> or
	 *             <code>ConfigurationHandler</code> are null.
	 */
	void delete(SduRepoLocation sduRepoLocation,
			SduFileStorageLocation sduFileStorageLocation,
			ConfigurationHandler cfgHandler) throws NullPointerException,
			RepositoryException {
		ValidatorUtils.validate(sduRepoLocation);
		ValidatorUtils.validate(sduFileStorageLocation);
		ValidatorUtils.validate(cfgHandler);

		Connection conn = null;

		try {
			conn = this.getDBConnection();

			deleteSduRepoLocation(sduRepoLocation, cfgHandler, conn);
			deleteSduFileStorageLocation(sduFileStorageLocation, cfgHandler);

			commit(cfgHandler);
		} catch (RepositoryException re) {
			rollback(cfgHandler);

			throw re;
		} finally {
			closeAllConfigurations(cfgHandler);
			closeDBConnection(conn);
		}
	}

	/**
	 * The operation deletes the specified <code>SduRepoLocation</code> from the
	 * repository.
	 * 
	 * @param sduRepoLocation
	 *            <code>SduRepoLocation</code> specifies what has to be deleted
	 *            from the repository. Updates the described references in the
	 *            <code>Sdu</code> and refered <code>Sdu</code>.
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws RepositoryException
	 *             if the <code>SduRepoLocation</code> could not be deleted.
	 * @throws NullPointerException
	 *             if the specified <code>SduRepoLocation</code> or
	 *             <code>ConfigurationHandler</code> are null.
	 */
	void delete(SduRepoLocation sduRepoLocation, ConfigurationHandler cfgHandler)
			throws NullPointerException, RepositoryException {
		ValidatorUtils.validate(sduRepoLocation);
		ValidatorUtils.validate(cfgHandler);

		Connection conn = null;

		try {
			conn = this.getDBConnection();

			deleteSduRepoLocation(sduRepoLocation, cfgHandler, conn);

			commit(cfgHandler);
		} catch (RepositoryException re) {
			rollback(cfgHandler);

			throw re;
		} finally {
			closeAllConfigurations(cfgHandler);
			closeDBConnection(conn);
		}
	}

	void deleteSduFileStorageLocation(SduFileStorageLocation location,
			ConfigurationHandler cfgHandler) throws NullPointerException,
			RepositoryException {
		deleteCfgIfExists(cfgHandler, location.getLocation());
	}

	private void deleteSduRepoLocation(SduRepoLocation location,
			ConfigurationHandler cfgHandler, Connection conn)
			throws NullPointerException, RepositoryException {
		final SduRepoLocationDeleteVisitor deleteVisitor = new SduRepoLocationDeleteVisitor(
				cfgHandler, conn);

		location.accept(deleteVisitor);
		try {
			deleteVisitor.getException();
		} catch (Exception e) {
			throw new RepositoryException(
					DCExceptionConstants.REPO_CANNOT_DELETE,
					new String[] { location.toString() }, e);
		}
	}

	// Persists SduRepoLocation
	private void persistSduRepoLocation(SduRepoLocation location,
			ConfigurationHandler cfgHandler, Connection conn)
			throws NullPointerException, RepositoryException {
		ValidatorUtils.validate(location);

		try {
			SduRepoLocationPersistVisitor persistorVisitor = new SduRepoLocationPersistVisitor(
					cfgHandler, conn);
			location.accept(persistorVisitor);
			persistorVisitor.getException();
		} catch (Exception e) {
			throw handleSduPersistException(e, location.getSdu().getId()
					.toString(), location.getLocation());
		}
	}

	// private void persistWithoutCfgHandler(SduRepoLocation sduRepoLocation,
	// Connection conn)
	// throws NullPointerException, RepositoryException {
	// final ConfigurationHandler cfgHandler = null;
	//    
	// try {
	// persistSduRepoLocation(sduRepoLocation, cfgHandler, conn);
	//      
	// commit(cfgHandler);
	// }
	// catch(RepositoryException re) {
	// rollback(cfgHandler);
	//      
	// throw re;
	// }
	// finally {
	// closeAllConfigurations(cfgHandler);
	// closeDBConnection(conn);
	// }
	// }

	// Persists SduFileStorageLocation
	private void persistSduFileStorageLocation(SduFileStorageLocation location,
			Sdu sdu, ConfigurationHandler cfgHandler)
			throws NullPointerException, IllegalArgumentException,
			RepositoryException {
		ValidatorUtils.validate(location);

		try {
			SduFileStorageLocationPersistVisitor persistorVisitor = new SduFileStorageLocationPersistVisitor(
					this, cfgHandler);
			persistorVisitor.persistSduFileStorage(location, sdu);
			persistorVisitor.getException();
		} catch (SduReaderException sre) {
			throw handleSduPersistException(sre, location.getSduFile()
					.getName(), location.getLocation());
		} catch (ConfigurationException ce) {
			throw handleSduPersistException(ce,
					location.getSduFile().getName(), location.getLocation());
		} catch (IOException ioe) {
			throw handleSduPersistException(ioe, location.getSduFile()
					.getName(), location.getLocation());
		}
	}

	private RepositoryException handleSduPersistException(Exception ex,
			String what, String from) {
		return new RepositoryException(
				DCExceptionConstants.REPO_CANNOT_PERSIST, new String[] { what,
						from }, ex);
	}
}
