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

import com.sap.engine.core.Names;
import com.sap.tc.logging.Location;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public interface Storage {

  public static final Location loc = Location.getLocation("com.sap.engine.session.persistent", Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  PersistentDomainModel getDomainModel(String context, String domain) throws PersistentStorageException;

}
