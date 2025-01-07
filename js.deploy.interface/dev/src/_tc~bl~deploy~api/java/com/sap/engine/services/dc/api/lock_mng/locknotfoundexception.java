package com.sap.engine.services.dc.api.lock_mng;

import com.sap.tc.logging.Location;

/**
 * <DL>
 * <DT><B>Title: </B></DT>
 * <DD>J2EE Deployment Team
 * <DT><B>Description: </B></DT>
 * <DD>The exception will be thrown by
 * {@link com.sap.engine.services.dc.api.lock_mng.LockManager#unlock(LockAction)}
 * if the engine is not locked with the given <code>LockAction</code></DD>
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
public class LockNotFoundException extends LockException {

	public LockNotFoundException(Location location, String patternKey,
			String[] parameters) {
		super(location, patternKey, parameters);
	}

	public LockNotFoundException(Location location, String patternKey,
			String[] parameters, Throwable cause) {
		super(location, patternKey, parameters, cause);
	}

}
