/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.spi.session;

import com.sap.engine.session.Session;
import com.sap.engine.session.spi.persistent.PersistentModel;



/**
 * This interface present the system view of the used session model.
 * The session is present form two parts management and persistent.
 * The management Model is an abstraction used for session management.
 * The Persistent Model present system view of the user data stored in 
 * the session.   
 * 
 * @author georgi-s
 *
 */
public interface SessionModel {
	/**
	 * Returns the management model of the session
	 * 
	 * @return the management model of this session
	 */
	MgmtModel mgmtModel();

	
	/**
	 * returns the Persistent Model of the session
	 *  
	 * @return the Persistent model of this session
	 */
	PersistentModel persistentModel();
	/**
	 * Returns the session that is present from the model
	 * 
	 * @return the session presents from the model 
	 */
	Session session();

}
