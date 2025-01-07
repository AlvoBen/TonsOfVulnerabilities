package com.sap.engine.core.session;

import com.sap.engine.session.spi.persistent.Storage;
import com.sap.engine.session.runtime.SessionFailoverMode;
import com.sap.engine.session.runtime.OnRequestFailoverMode;
import com.sap.engine.session.runtime.OnThreadEndFailoverMode;
import com.sap.engine.session.mgmt.ConfigurationEntry;
import com.sap.engine.session.mgmt.SessionConfigurator;
import com.sap.engine.session.mgmt.EntryBuilder;
import com.sap.engine.session.failover.FailoverConfig;
import com.sap.engine.core.session.persistent.file.FileStorage;
import com.sap.engine.core.session.persistent.db.DBStorage;
import com.sap.engine.core.session.persistent.memory.MemoryStorage;
import com.sap.engine.core.session.persistent.sharedmemory.ShMemoryStorage;

import java.util.StringTokenizer;

public class ConfigurationEntryBuilder implements EntryBuilder {
  public static final String MODE_KEY = "mode";
  public static final String STORAGE_KEY = "storage";
  public static final String STORAGE_FILE = "file";
  public static final String STORAGE_SHARED_MEMORY = "shared_memory";
  public static final String STORAGE_MEMORY = "memory";
  public static final String STORAGE_DB = "db";
  public static final String STORAGE_NONE = "none";
  public static final String MODE_ON_REQUEST = "on_request";
  public static final String MODE_ON_THREAD_END = "on_thread_end";
  public static final String MODE_ON_SHUTDOWN = "on_shutdown";
  public static String defaultMode = MODE_ON_REQUEST;
  public static String defaultStorage = STORAGE_NONE;

  public static ConfigurationEntry buildEntry(String domain, String encodedProps) {
    String mode = null;
    String str_storage = null;
    Storage storage;
    SessionFailoverMode failoverMode;
    StringTokenizer tokenizer = new StringTokenizer(encodedProps, ",");
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      int index = token.indexOf(':');
      if (index > -1) {
        try {
          String key = token.substring(0, index).trim().toLowerCase();
          String value = token.substring(index + 1).trim().toLowerCase();
          if (key.equals(MODE_KEY)) {
            mode = value;
          } else if (key.equals(STORAGE_KEY)) {
            str_storage = value;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    storage = buildStorageForType(str_storage);
    failoverMode = buildFailoverModeForType(mode);
    if (failoverMode != null || storage != null) {
      return new ConfigurationEntry(domain, storage, failoverMode);
    }
    return null;
  }

  public static Storage buildStorageForType(String storageType) {
    Storage storage = null;
    if (storageType != null) {
      storageType = storageType.trim().toLowerCase();
      if (storageType.equals(STORAGE_FILE)) {
        storage = new FileStorage();
      } else if (storageType.equals(STORAGE_DB)) {
        storage = new DBStorage();
      } else if (storageType.equals(STORAGE_SHARED_MEMORY)) {
        storage = new ShMemoryStorage();
      } else if (storageType.equals(STORAGE_MEMORY)) {
        storage = new MemoryStorage();
      }
    }
    return storage;
  }

  public static SessionFailoverMode buildFailoverModeForType(String modeType) {
    SessionFailoverMode mode = null;
    if (modeType != null) {
      modeType = modeType.trim().toLowerCase();
      if (modeType.equals(MODE_ON_REQUEST)) {
        mode = new OnRequestFailoverMode();
      } else if (modeType.equals(MODE_ON_THREAD_END)) {
        mode = new OnThreadEndFailoverMode();
      } else if (modeType.equals(MODE_ON_SHUTDOWN)) {
        // ??
      }
    }
    return mode;
  }

  public SessionFailoverMode buildFailoverModeForType(int modeType) {
    SessionFailoverMode mode = null;
    if (modeType == FailoverConfig.FAILOVER_CONFIGURATION_ON_REQUEST) {
      mode = new OnRequestFailoverMode();
    } else if (modeType == FailoverConfig.FAILOVER_CONFIGURATION_ON_APP_STOP) {
      // todo:
    } else if (modeType == FailoverConfig.FAILOVER_CONFIGURATION_ON_ATTRIBUTE) {
      // todo:
    }
    return mode;
  }

  public Storage buildStorageForType(int storageType) {
    Storage storage = null;
    if (storageType == FailoverConfig.INSTANCE_LOCAL || storageType == FailoverConfig.VM_LOCAL) {
      storage = buildStorageForType(defaultStorage);
    } else if (storageType == FailoverConfig.CLUSTER_WIDE) {
      storage = buildStorageForType(STORAGE_DB);
    }
    return storage;
  }

  public static void buildConfigurationEntries(String encodedEntries) {
    StringTokenizer tokenizer = new StringTokenizer(encodedEntries, ";");
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken().trim();
      int index = token.indexOf(' ');
      if (index > -1) {
        try {
          String key = token.substring(0, index);
          String props = token.substring(index + 1);
          ConfigurationEntry entry = ConfigurationEntryBuilder.buildEntry(key, props);
          if (entry != null) {
            SessionConfigurator.addConfigurationEntry(key, entry);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
