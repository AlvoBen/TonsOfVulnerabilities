package com.sap.engine.services.dc.cm.lock.impl;

import com.sap.engine.frame.core.configuration.Configuration;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.locking.LockException;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.services.dc.cm.lock.DCAlreadyLockedException;
import com.sap.engine.services.dc.cm.lock.DCLockException;
import com.sap.engine.services.dc.cm.lock.DCLockManager;
import com.sap.engine.services.dc.cm.lock.DCLockNotFoundException;
import com.sap.engine.services.dc.cm.lock.LockAction;
import com.sap.engine.services.dc.cm.lock.LockActionBuilder;
import com.sap.engine.services.dc.cm.lock.LockActionLocation;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.lock.DCEnqueueLock;
import com.sap.engine.services.dc.util.lock.LockData;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.tc.logging.Location;

/**
 * Implements the <code>DCLockManager</code> interface.
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
final class DCLockManagerImpl implements DCLockManager {
	
	private Location location = DCLog.getLocation(this.getClass());

	DCLockManagerImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.lock.DCLockManager#lockEnqueueAndDB(com
	 * .sap.engine.services.dc.cm.lock.LockAction)
	 */
	public void lockEnqueueAndDB(LockAction lockAction,
			ConfigurationHandler cfgHandler) throws DCAlreadyLockedException,
			DCLockException {
		lockEnqueue(lockAction);
		try {
			lockDB(lockAction, cfgHandler);
		} catch (DCLockException dcle) {
			try {
				unlockEnqueue(lockAction);
			} catch (DCLockException dcle1) {// $JL-EXC$
				// @Anton this must not be shown
			}
			throw dcle;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.lock.DCLockManager#lockEnqueue(com.sap.
	 * engine.services.dc.cm.lock.LockAction)
	 */
	public void lockEnqueue(LockAction lockAction)
			throws DCAlreadyLockedException, DCLockException {
		lockEnqueue(lockAction, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.lock.DCLockManager#lockEnqueue(com.sap.
	 * engine.services.dc.cm.lock.LockAction,
	 * com.sap.engine.services.dc.util.lock.LockData)
	 */
	public void lockEnqueue(LockAction lockAction, LockData lockData)
			throws DCAlreadyLockedException, DCLockException {

		if (location.beInfo()) {
			DCLog.traceInfo(location, 
					"Locking the enqueue for operation [{0}] ...",
					new String[] { lockAction.getName() });
		}

		final LockActionLocation location = LockActionBuilder.getInstance()
				.build(lockAction);
		try {
			DCEnqueueLock.getInstance().lock4Parallel(location.getLocation(),
					lockData);
		} catch (LockException e) {
			throw handleDCAlreadyLockedException("lock", "Enqueue", location, e
					.getCollisionUserName(), e);
		} catch (TechnicalLockException e) {
			throw handleDCLockException("lock", "Enqueue", location
					.getLocation(), location.getLockAction().getName(), e);
		} catch (IllegalArgumentException e) {
			throw handleDCLockException("lock", "Enqueue", location
					.getLocation(), location.getLockAction().getName(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.lock.DCLockManager#lockDB(com.sap.engine
	 * .services.dc.cm.lock.LockAction,
	 * com.sap.engine.frame.core.configuration.ConfigurationHandler)
	 */
	public void lockDB(LockAction lockAction, ConfigurationHandler cfgHandler)
			throws DCAlreadyLockedException, DCLockException {

		if (location.bePath()) {
			DCLog.tracePath(location, "Locking the DB ...");
		}

		final LockActionLocation location = LockActionBuilder.getInstance()
				.build(lockAction);
		try {
			try {
				lockDBSingleThread(location, cfgHandler);
				cfgHandler.commit();
			} catch (ConfigurationException ce) {
				cfgHandler.rollback();
				throw ce;
			} finally {
				cfgHandler.closeAllConfigurations();
			}
		} catch (NameAlreadyExistsException e) {
			final LockActionLocation oldLALocation = LockActionBuilder
					.getInstance().build(location.getLocation());
			oldLALocation.setLockAction(getDBLockAction(cfgHandler,
					oldLALocation));
			throw handleDCAlreadyLockedException("lock", "DB", location,
					getName(oldLALocation), e);
		} catch (ConfigurationException e) {
			throw handleDCLockException("lock", "DB", location.getLocation(),
					location.getLockAction().getName(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.lock.DCLockManager#unlockEnqueueAndDB()
	 */
	public void unlockDBAndEnqueue(LockAction lockAction,
			ConfigurationHandler cfgHandler) throws DCLockNotFoundException,
			DCLockException {
		unlockDB(lockAction, cfgHandler);
		try {
			unlockEnqueue(lockAction);
		} catch (DCLockException dcle) {
			try {
				lockDB(lockAction, cfgHandler);
			} catch (DCLockException dcle1) {// $JL-EXC$
				// @Anton this must not be shown
			}
			throw dcle;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.lock.DCLockManager#unlockEnqueue()
	 */
	public void unlockEnqueue(LockAction lockAction)
			throws DCLockNotFoundException, DCLockException {
		unlockEnqueue(lockAction, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.lock.DCLockManager#unlockEnqueue(com.sap
	 * .engine.services.dc.cm.lock.LockAction,
	 * com.sap.engine.services.dc.util.lock.LockData)
	 */
	public void unlockEnqueue(LockAction lockAction, LockData lockData)
			throws DCLockNotFoundException, DCLockException {

		if (location.beInfo()) {
			DCLog.traceInfo(location, 
					"Unlocking the enqueue for operation {0}.",
					new String[] { lockAction.getName() });
		}
		
		final LockActionLocation location = LockActionBuilder.getInstance()
				.build(lockAction);
		try {
			DCEnqueueLock.getInstance().unlock4Parallel(location.getLocation(),
					lockData);
		} catch (TechnicalLockException e) {
			throw handleDCLockNotFoundException("Enqueue", location
					.getLocation(), location.getLockAction().getName(), e);
		} catch (IllegalArgumentException e) {
			throw handleDCLockException("unlock", "Enqueue", location
					.getLocation(), location.getLockAction().getName(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.lock.DCLockManager#unlockDB()
	 */
	public void unlockDB(LockAction lockAction, ConfigurationHandler cfgHandler)
			throws DCLockNotFoundException, DCLockException {

		if (location.bePath()) {
			DCLog.tracePath(location, "Unlocking the DB ...");
		}

		final LockActionLocation location = LockActionBuilder.getInstance()
				.build(lockAction);
		try {
			try {
				unlockDBSingleThread(location, cfgHandler);
				cfgHandler.commit();
			} catch (DCLockException le) {
				cfgHandler.rollback();
				throw le;
			} catch (ConfigurationException ce) {
				cfgHandler.rollback();
				throw ce;
			} finally {
				cfgHandler.closeAllConfigurations();
			}
		} catch (NameNotFoundException e) {
			throw handleDCLockNotFoundException("DB", location.getLocation(),
					location.getLockAction().getName(), e);
		} catch (ConfigurationException e) {
			throw handleDCLockException("unlock", "DB", location.getLocation(),
					location.getLockAction().getName(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.lock.DCLockManager#getDBLockAction()
	 */
	public LockAction getDBLockAction(ConfigurationHandler cfgHandler,
			LockActionLocation location) throws DCLockException {
		try {
			try {
				return getSingleThreadDBLockAction(location, cfgHandler);
			} finally {
				cfgHandler.closeAllConfigurations();
			}
		} catch (ConfigurationException e) {
			throw handleDCLockException("get", "DB", location.getLocation(),
					"{?}", e);
		}
	}

	private LockAction getSingleThreadDBLockAction(LockActionLocation location,
			ConfigurationHandler cfgHandler) throws ConfigurationException,
			DCLockException {

		try {
			final Configuration singleCfg = cfgHandler.openConfiguration(
					location.getLocation(), ConfigurationHandler.READ_ACCESS);
			final String lockActionName = (String) singleCfg
					.getConfigEntry(LockLocationConstants.TYPE);
			final LockAction dbLockAction = LockAction
					.getLockActionByName(lockActionName);
			location.setLockAction(dbLockAction);
			validateLockActionReadFromDB(location);
		} catch (NameNotFoundException nnfe) {
			location.setLockAction(null);
		}
		return location.getLockAction();
	}

	private void unlockDBSingleThread(LockActionLocation location,
			ConfigurationHandler cfgHandler) throws ConfigurationException,
			DCLockException {
		final Configuration singleCfg = cfgHandler.openConfiguration(location
				.getLocation(), ConfigurationHandler.WRITE_ACCESS);
		final String lockActionName = (String) singleCfg
				.getConfigEntry(LockLocationConstants.TYPE);
		final LockAction dbLockAction = LockAction
				.getLockActionByName(lockActionName);
		location.setLockAction(dbLockAction);
		validateLockActionReadFromDB(location);
		if (!dbLockAction.equals(location.getLockAction())) {
			throw handleDCLockException("unlock", "DB", location.getLocation(),
					location.getLockAction().getName(),
					"The LockAction in DB is " + dbLockAction.getName()
							+ ", but the given one is "
							+ location.getLockAction().getName());
		}
		singleCfg.deleteConfiguration();
	}

	private void lockDBSingleThread(LockActionLocation location,
			ConfigurationHandler cfgHandler) throws ConfigurationException {
		final Configuration singleCfg = cfgHandler
				.createSubConfiguration(location.getLocation());
		singleCfg.addConfigEntry(LockLocationConstants.TYPE, location
				.getLockAction().getName());
	}

	private void validateLockActionReadFromDB(LockActionLocation dbLocation)
			throws DCLockException {
		if (dbLocation.getLockAction() == null) {
			throw handleDCLockException("unlock", "DB", dbLocation
					.getLocation(), NA, "The LockAction in DB is null.");
		}
	}

	private DCAlreadyLockedException handleDCAlreadyLockedException(
			String what, String where, LockActionLocation newLALocation,
			String oldName, Exception ex) {
		final String newName = getName(newLALocation);
		DCAlreadyLockedException dale = new DCAlreadyLockedException(
				"Cannot perform '" + what
						+ "' operation in '" + where + "' for argument '"
						+ newLALocation.getLocation() + "' and user '"
						+ newName + "', because the argument '"
						+ newLALocation.getLocation() + "' and user '"
						+ oldName + "' is already locked.", ex);
		dale.setMessageID("ASJ.dpl_dc.003103");
		return dale;
	}

	private DCLockNotFoundException handleDCLockNotFoundException(String where,
			String argument, String user, Exception ex) {
		DCLockNotFoundException dlnfe = new DCLockNotFoundException(
				"Cannot perform '" + "unlock"
						+ "' operation in '" + where + "' for argument '"
						+ argument + "' and user '" + user
						+ "', because it is not locked.", ex);
		dlnfe.setMessageID("ASJ.dpl_dc.003104"); 
		return dlnfe;
	}

	private DCLockException handleDCLockException(String what, String where,
			String argument, String user, Exception ex) {
		 DCLockException dle = new DCLockException("Cannot perform '"
				+ what + "' operation in '" + where + "' for argument '"
				+ argument + "' and user '" + user + "'.", ex);
		 dle.setMessageID("ASJ.dpl_dc.003105");
		 return dle;
	}

	private DCLockException handleDCLockException(String what, String where,
			String argument, String user, String because) {
		 DCLockException dle = new DCLockException("Cannot perform '"
				+ what + "' operation in '" + where + "' for argument '"
				+ argument + "' and user '" + user + "'. " + because);
		 dle.setMessageID("ASJ.dpl_dc.003106");
		 return dle;

	}

	private final static String NA = "N/A";

	private String getName(LockActionLocation location) {
		String name = NA;
		if (location.getLockAction() != null) {
			name = location.getLockAction().getName();
		}
		return name;
	}

	public void lockEnqueueForCurrentInstance(LockAction lockAction)
			throws DCAlreadyLockedException, DCLockException {

		if (location.beInfo()) {
			DCLog.traceInfo(location, 
					"Locking the enqueue for operation [{0}] ...",
					new String[] { lockAction.getName() });
		}

		final LockActionLocation location = LockActionBuilder.getInstance()
				.buildForInstance(
						lockAction,
						ServiceConfigurer.getInstance().getClusterMonitor()
								.getCurrentParticipant().getGroupId());
		try {
			DCEnqueueLock.getInstance().lock4Parallel(location.getLocation(),
					null);
		} catch (LockException e) {
			throw handleDCAlreadyLockedException("lock", "Enqueue", location, e
					.getCollisionUserName(), e);
		} catch (TechnicalLockException e) {
			throw handleDCLockException("lock", "Enqueue", location
					.getLocation(), location.getLockAction().getName(), e);
		} catch (IllegalArgumentException e) {
			throw handleDCLockException("lock", "Enqueue", location
					.getLocation(), location.getLockAction().getName(), e);
		}

	}

	public void unlockEnqueueForCurrentInstance(LockAction lockAction)
			throws DCLockNotFoundException, DCLockException {

		if (location.beInfo()) {
			DCLog.traceInfo(location, 
					"Unlocking the enqueue for operation {0}.",
					new String[] { lockAction.getName() });
		}

		final LockActionLocation location = LockActionBuilder.getInstance()
				.buildForInstance(
						lockAction,
						ServiceConfigurer.getInstance().getClusterMonitor()
								.getCurrentParticipant().getGroupId());
		try {
			DCEnqueueLock.getInstance().unlock4Parallel(location.getLocation(),
					null);
		} catch (TechnicalLockException e) {
			throw handleDCLockNotFoundException("Enqueue", location
					.getLocation(), location.getLockAction().getName(), e);
		} catch (IllegalArgumentException e) {
			throw handleDCLockException("unlock", "Enqueue", location
					.getLocation(), location.getLockAction().getName(), e);
		}
	}

}
