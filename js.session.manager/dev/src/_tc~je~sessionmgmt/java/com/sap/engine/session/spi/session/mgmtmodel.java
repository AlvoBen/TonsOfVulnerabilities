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

import com.sap.engine.session.SessionDomain;

/**
 * @author georgi-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface MgmtModel {
		
		String sessionId();
		
		SessionDomain domain();
		
		long lastAccessedTime();
		
		long maxInactivInterval();
				 
}
