package com.sap.engine.services.security.exceptions;

import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.locking.TechnicalLockException;
import com.sap.engine.lib.security.SecurityConfigurationLockedException;

public class StorageLockedException extends StorageException implements SecurityConfigurationLockedException {

  private static final long serialVersionUID = 1L;

  public StorageLockedException(String message) {
    super(message);
  }

  public StorageLockedException(String message, ConfigurationLockedException e) {
    super(message, e);
  }

  public StorageLockedException(String message, TechnicalLockException e) {
    super(message, e);
  }

}