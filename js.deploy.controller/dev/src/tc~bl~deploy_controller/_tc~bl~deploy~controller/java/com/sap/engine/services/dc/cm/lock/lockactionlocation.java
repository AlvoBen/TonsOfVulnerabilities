/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.dc.cm.lock;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
public abstract class LockActionLocation {

	public abstract String getLocation();

	public abstract LockAction getLockAction();

	public abstract void setLockAction(LockAction action);

}
