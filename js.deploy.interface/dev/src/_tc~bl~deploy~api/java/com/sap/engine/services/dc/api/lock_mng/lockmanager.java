package com.sap.engine.services.dc.api.lock_mng;

import com.sap.engine.services.dc.api.APIException;
import com.sap.engine.services.dc.api.ConnectionException;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team</DD>
 * <DT><B>Description: </B></DT>
 * <DD>Serves to lock and unlock the engine for exact action.</DD>
 * <DT><B>Copyright: </B></DT>
 * <DD>Copyright (c) 2003</DD>
 * <DT><B>Company: </B></DT>
 * <DD>SAP AG</DD>
 * <DT><B>Date: </B></DT>
 * <DD>2005-4-26</DD>
 * </DL>
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.1
 * 
 */
public interface LockManager {
	/**
	 * Tries to lock the engine with the given action.
	 * 
	 * @param lockAction
	 * @throws AlreadyLockedException
	 *             in case the engine is already locked with the same
	 *             <code>LockAction</code>
	 * @throws LockException
	 * @throws ConnectionException
	 * @throws APIException
	 */
	public void lock(LockAction lockAction) throws AlreadyLockedException,
			LockException, ConnectionException, APIException;

	/**
	 * Tries to unlock the engine with the given action.
	 * 
	 * @param lockAction
	 * @throws LockNotFoundException
	 *             if engine is not locked with the given
	 *             <code>LockAction</code>
	 * @throws LockException
	 * @throws ConnectionException
	 * @throws APIException
	 */
	public void unlock(LockAction lockAction) throws LockNotFoundException,
			LockException, ConnectionException, APIException;

}