package com.sap.engine.core.session.persistent.sharedmemory;

import com.sap.engine.session.spi.persistent.Storage;
import com.sap.engine.session.spi.persistent.PersistentDomainModel;
import com.sap.engine.session.spi.persistent.PersistentStorageException;

public class ShMemoryStorage implements Storage {

  public PersistentDomainModel getDomainModel(String _contextName, String _domainName) throws PersistentStorageException {
    String contextName = _contextName.replace('/', '#');
    String domainName = _domainName.replace('/', '#');
    return new ShMemoryPersistentDomainModel(contextName + "#" + domainName);
  }
}
