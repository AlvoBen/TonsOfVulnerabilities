package com.sap.engine.services.dc.cm.lock;

import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.services.dc.util.lock.LockData;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-6
 * 
 * @author Dimitar Dimitrov, Anton Georgiev
 * @version 1.0
 * @since 7.0
 * 
 */
public interface DCLockManager {

	/**
	 * Makes lock in the Enqueue Server and DB
	 * 
	 * @param lockAction
	 *            <code>LockAction</code>
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws DCAlreadyLockedException
	 *             in case the lock already exists.
	 * @throws DCLockException
	 *             in case the lock cannot be made.
	 */
	public void lockEnqueueAndDB(LockAction lockAction,
			ConfigurationHandler cfgHandler) throws DCAlreadyLockedException,
			DCLockException;

	/**
	 * Makes lock in the Enqueue Server
	 * 
	 * @param lockAction
	 *            <code>LockAction</code>
	 * @throws DCAlreadyLockedException
	 *             in case the lock already exists.
	 * @throws DCLockException
	 *             in case the lock cannot be made.
	 */
	public void lockEnqueue(LockAction lockAction)
			throws DCAlreadyLockedException, DCLockException;

	/**
	 * Makes lock in the Enqueue Server
	 * 
	 * @param lockAction
	 *            <code>LockAction</code>
	 * @param lockData
	 *            <code>LockData</code>
	 * @throws DCAlreadyLockedException
	 *             in case the lock already exists.
	 * @throws DCLockException
	 *             in case the lock cannot be made.
	 */
	public void lockEnqueue(LockAction lockAction, LockData lockData)
			throws DCAlreadyLockedException, DCLockException;

	/**
	 * Makes lock in the DB
	 * 
	 * @param lockAction
	 *            <code>LockAction</code>
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws DCAlreadyLockedException
	 *             in case the lock already exists.
	 * @throws DCLockException
	 *             in case the lock cannot be made.
	 */
	public void lockDB(LockAction lockAction, ConfigurationHandler cfgHandler)
			throws DCAlreadyLockedException, DCLockException;

	/**
	 * Removes lock from the DB and Enqueue Server
	 * 
	 * @param lockAction
	 *            <code>LockAction</code>
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws DCLockNotFoundException
	 *             in case there is no such lock made.
	 * @throws DCLockException
	 *             in case the lock cannot be removed.
	 */
	public void unlockDBAndEnqueue(LockAction lockAction,
			ConfigurationHandler cfgHandler) throws DCLockNotFoundException,
			DCLockException;

	/**
	 * Removes lock from the Enqueue Server
	 * 
	 * @param lockAction
	 *            <code>LockAction</code>
	 * @throws DCLockNotFoundException
	 *             in case there is no such lock made.
	 * @throws DCLockException
	 *             in case the lock cannot be removed.
	 */
	public void unlockEnqueue(LockAction lockAction)
			throws DCLockNotFoundException, DCLockException;

	/**
	 * Removes lock from the Enqueue Server
	 * 
	 * @param lockAction
	 *            <code>LockAction</code>
	 * @param lockData
	 *            <code>LockData</code>
	 * @throws DCLockNotFoundException
	 *             in case there is no such lock made.
	 * @throws DCLockException
	 *             in case the lock cannot be removed.
	 */
	public void unlockEnqueue(LockAction lockAction, LockData lockData)
			throws DCLockNotFoundException, DCLockException;

	/**
	 * Removes lock from the DB
	 * 
	 * @param lockAction
	 *            <code>LockAction</code>
	 * @param cfgHandler
	 *            <code>ConfigurationHandler</code>
	 * @throws DCLockNotFoundException
	 *             in case there is no such lock made.
	 * @throws DCLockException
	 *             in case the lock cannot be removed.
	 */
	public void unlockDB(LockAction lockAction, ConfigurationHandler cfgHandler)
			throws DCLockNotFoundException, DCLockException;

	/**
	 * Gets the lock action from the DB.
	 * 
	 * @return <code>LockAction</code> which specified for what purposes the
	 *         lock has been set or <code>null</code> if there is no lock set.
	 * @throws DCLockException
	 *             if it is not possible to read the lock action from DB.
	 */
	public LockAction getDBLockAction(ConfigurationHandler cfgHandler,
			LockActionLocation location) throws DCLockException;

	/**
	 * Makes lock in the Enqueue Server only for the current instance
	 * 
	 * @param lockAction
	 *            <code>LockAction</code>
	 * @throws DCAlreadyLockedException
	 *             in case the lock already exists.
	 * @throws DCLockException
	 *             in case the lock cannot be made.
	 */
	public void lockEnqueueForCurrentInstance(LockAction lockAction)
			throws DCAlreadyLockedException, DCLockException;

	/**
	 * Removes lock from the Enqueue Server if it exists for the current
	 * instance
	 * 
	 * @param lockAction
	 *            <code>LockAction</code>
	 * @throws DCLockNotFoundException
	 *             in case there is no such lock made.
	 * @throws DCLockException
	 *             in case the lock cannot be removed.
	 */
	public void unlockEnqueueForCurrentInstance(LockAction lockAction)
			throws DCLockNotFoundException, DCLockException;
}
