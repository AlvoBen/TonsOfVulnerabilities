/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session.persistent.memory;

import com.sap.engine.session.spi.persistent.Storage;
import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class MemoryStorage implements Storage  {
  public PersistentDomainModel getDomainModel(String context, String domain) throws PersistentStorageException {
    return new MemoryPersistentDomainModel();
  }
}
