package com.sap.engine.core.service630.container;

import java.io.File;
import java.io.InputStream;

import com.sap.engine.core.Names;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.component.PersistentEntryContainer;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.lib.config.api.exceptions.NameNotFoundException;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This class is implements PersistentContainer.
 * @see com.sap.engine.frame.state.PersistentContainer
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class PersistentHelperImpl implements com.sap.engine.frame.state.PersistentContainer {

  final static String containerType = "persistent";
  private String serviceName;
  private PersistentContainer persistentContainer;

  private static final Location LOCATION = Location.getLocation(PersistentHelperImpl.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  private static final Category CATEGORY = Category.SYS_SERVER;

  public PersistentHelperImpl(String name, ServiceContainerImpl container) {
    serviceName = name;
    persistentContainer = container.getMemoryContainer().getPersistentContainer();
  }

  public InputStream getPersistentEntryStream(String name, boolean global) throws ServiceException {
    InputStream result;
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Service " + serviceName + " request " + ((global) ? "global" : "local") + " file " + name);
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      result = pec.getPersistentEntryAsStream(name);
    } catch (NameNotFoundException e) {
      // $JL-EXC$
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Missing file " + name);
      }
      result = null;
    } catch (ClusterConfigurationException e) {
      Object[] params = new Object[] {name, serviceName, containerType};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000035",
            "Error reading [{0}] file entry from [{1}] component [{2}] container",
            params);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.ERROR_READING_FROM_PERSISTENT_CONTAINER), params), e);
    }
    return result;
  }

  public File getPersistentEntryFile(String name, boolean global) throws ServiceException {
    if (LOCATION.beWarning()) {
      String message = ResourceUtils.formatString(ResourceUtils.GET_PERSISTENT_FILE_DEPRECATED, new Object[] {serviceName, LOCATION.getName()});
      LOCATION.warningT(message);
      if (LOCATION.beDebug()) {
        LOCATION.traceThrowableT(Severity.DEBUG, message, new Exception());
      }
    }
    File result;
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      result = pec.getPersistentEntryAsFile(name);
    } catch (NameNotFoundException e) {
      // $JL-EXC$
      result = null;
    } catch (ClusterConfigurationException e) {
      Object[] params = new Object[] {name, serviceName, containerType};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000036",
            "Error reading [{0}] file entry from [{1}] component [{2}] container",
            params);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.ERROR_READING_FROM_PERSISTENT_CONTAINER), params), e);
    }
    return result;
  }

  public void setPersistentEntryStream(String name, InputStream stream, boolean global) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Service " + serviceName + " update " + ((global) ? "global" : "local") + " file " + name + " : " + stream);
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      pec.setPersistentEntryFromStream(name, false, stream);
    } catch (ClusterConfigurationException e) {
      Object[] params = new Object[] {name, serviceName, containerType};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000037",
            "Error storing [{0}] file entry in [{1}] component [{2}] container",
            params);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.ERROR_STORING_IN_PERSISTENT_CONTAINER), params), e);
    }
  }

  public void setPersistentEntryFile(String name, File file, boolean global) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Service " + serviceName + " update " + ((global) ? "global" : "local") + " file " + name + " : " + file);
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      pec.setPersistentEntryFromFile(name, false, file);
    } catch (ClusterConfigurationException e) {
      Object[] params = new Object[] {name, serviceName, containerType};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000038",
            "Error storing [{0}] file entry in [{1}] component [{2}] container",
            params);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.ERROR_STORING_IN_PERSISTENT_CONTAINER), params), e);
    }
  }

  public void setSecuredPersistentEntryStream(String name, InputStream stream, boolean global) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Service " + serviceName + " update secured " + ((global) ? "global" : "local") + " file " + name + " : " + stream);
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      pec.setPersistentEntryFromStream(name, true, stream);
    } catch (ClusterConfigurationException e) {
      Object[] params = new Object[] {name, serviceName, containerType};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000039",
            "Error storing [{0}] file entry in [{1}] component [{2}] container",
            params);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.ERROR_STORING_IN_PERSISTENT_CONTAINER), params), e);
    }
  }

  public void setSecuredPersistentEntryFile(String name, File file, boolean global) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Service " + serviceName + " update secured " + ((global) ? "global" : "local") + " file " + name + " : " + file);
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      pec.setPersistentEntryFromFile(name, true, file);
    } catch (ClusterConfigurationException e) {
      Object[] params = new Object[] {name, serviceName, containerType};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000040",
            "Error storing [{0}] file entry in [{1}] component [{2}] container",
            params);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.ERROR_STORING_IN_PERSISTENT_CONTAINER), params), e);
    }
  }

  public void removePersistentEntry(String name, boolean global) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Service " + serviceName + " remove " + ((global) ? "global" : "local") + " file " + name);
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      pec.removePersistentEntry(name);
    } catch (ClusterConfigurationException e) {
      Object[] params = new Object[] {name, serviceName, containerType};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000041",
            "Error removing [{0}] file envy from [{1}] component [{2}] container",
            params);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.ERROR_REMOVING_FROM_PERSISTENT_CONTAINER), params), e);
    }
  }

  public String[] listPersistentEntryNames(boolean global) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Service " + serviceName + " list persistent entries");
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      return pec.listPersistentEntryNames();
    } catch (ClusterConfigurationException e) {
      Object[] params = new Object[] {serviceName, containerType};
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000042",
            "Error listing file entry from [{0}] component [{1}] container",
            params);
      }
      throw new ServiceException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.ERROR_LISTING_PERSISTENT_CONTAINER), params), e);
    }
  }

  public void setMigrationVersion(int version) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Service " + serviceName + " set migration version " + version);
    }
    try {
      ConfigurationLevel level = persistentContainer.getCustomGlobalLevel();
      level.getComponentAccess().setServiceMigrationVersion(serviceName, version);
    } catch (ClusterConfigurationException e) {
      throw new ServiceException(LOCATION, e);
    }
  }

  public int getMigrationVersion() {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Service " + serviceName + " gets migration version");
    }
    try {
      ConfigurationLevel level = persistentContainer.getCustomGlobalLevel();
      return level.getComponentAccess().getServiceMigrationVersion(serviceName);
    } catch (ClusterConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000043",
            "Error getting migration version for service [{0}]",
            new Object[] {serviceName});
      }
      if(SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "Error getting migration version for service [" + serviceName +
              "]", e);
      }
      return 0;
    }
  }

  private PersistentEntryContainer getPersistentEntryContainer(boolean global) throws ClusterConfigurationException {
    ConfigurationLevel level;
    if (global) {
      level = persistentContainer.getCustomGlobalLevel();
    } else {
      level = persistentContainer.getInstanceLevel();
    }
    return level.getComponentAccess().getServicePersistentContainer(serviceName);
  }

}