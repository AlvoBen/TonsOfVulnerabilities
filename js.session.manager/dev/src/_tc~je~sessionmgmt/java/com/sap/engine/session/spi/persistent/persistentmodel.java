/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.spi.persistent;

import java.io.NotSerializableException;
import java.util.Iterator;

import com.sap.engine.session.Session;

/**
 * @author georgi-s
 *
 */
public interface PersistentModel {
	
	String instaceKey();
	
	String context();
	
	String domainName();
	
	String sessionId();
		
	long expirationTime();
	
	Session session();
		
	Iterator getData();
	
	void addUnit(String key, Object value) throws NotSerializableException;
	
	Object getUnit(String key);
	
	void removeUnit(String key); 
	
	
}
