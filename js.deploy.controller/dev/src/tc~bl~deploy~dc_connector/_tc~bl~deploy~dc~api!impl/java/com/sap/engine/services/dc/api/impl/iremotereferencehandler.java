/*
 * Copyright (c) 2005 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * Created on August 3, 2006
 */
package com.sap.engine.services.dc.api.impl;

import java.rmi.Remote;

/**
 * This interface declares common behaviour for all instances that should handle
 * references to remote objects. The references should be released when the
 * client session is ended so that remote objects on server are eligible for
 * garbage collection.
 * 
 * @author Todor Stoitsev
 */
public interface IRemoteReferenceHandler {

	/**
	 * The method should be used for registering a newly detected reference to a
	 * remote object.
	 * 
	 * @param remote
	 */
	public void registerRemoteReference(Remote remote);

	/**
	 * The method should be called when the client session is ended to release
	 * the registered references to remote objects.
	 */
	public void releaseRemoteReferences();
}
