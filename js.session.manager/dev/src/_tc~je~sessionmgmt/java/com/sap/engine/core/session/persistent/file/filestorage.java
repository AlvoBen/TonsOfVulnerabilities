/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session.persistent.file;

import com.sap.engine.session.spi.persistent.Storage;
import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;
import com.sap.engine.core.session.StaticConfiguration;
import java.io.File;
/*
 * Author: i024157 /Georgi Stanev/ 
 */

public class FileStorage implements Storage {
  public static String workDir = StaticConfiguration.fileSystemRoot();

  public PersistentDomainModel getDomainModel(String context, String domain) throws PersistentStorageException {
    File parentDir = new File(workDir + context);

    if (!parentDir.exists()) {
      parentDir.mkdirs();
    }

    return new FilePersistentDomainModel(parentDir, domain);
  }
}
