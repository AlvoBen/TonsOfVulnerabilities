package com.sap.engine.core.service630.container;

import java.io.File;
import java.io.InputStream;

import com.sap.engine.core.Names;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.frame.ServiceException;
import com.sap.engine.frame.container.monitor.DescriptorContainer;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.component.ComponentHandler;
import com.sap.engine.lib.config.api.component.PersistentEntryContainer;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.lib.config.api.exceptions.NameNotFoundException;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This class is implements DescriptorContainer.
 * @see com.sap.engine.frame.container.monitor.DescriptorContainer
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class DescriptorHelperImpl implements DescriptorContainer {

  final static String containerType = "descriptor";

  private String componentName;
  private byte componentType;
  private PersistentContainer persistentContainer;

  private static final Location LOCATION = Location.getLocation(DescriptorHelperImpl.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);
  private static final Category CATEGORY = Category.SYS_SERVER;

  public DescriptorHelperImpl(String name, byte type, ServiceContainerImpl container) {
    componentName = name;
    if (type == ComponentWrapper.TYPE_INTERFACE) {
      componentType = ComponentHandler.TYPE_INTERFACE;
    } else if (type == ComponentWrapper.TYPE_LIBRARY) {
      componentType = ComponentHandler.TYPE_LIBRARY;
    } else { //service
      componentType = ComponentHandler.TYPE_SERVICE;
    }
    persistentContainer = container.getMemoryContainer().getPersistentContainer();
  }

  public InputStream getPersistentEntryStream(String name, boolean global) {
    InputStream result = null;
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Request " + ((global) ? "global" : "local") + " file " + name + " from component " + componentName);
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
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000002",
            "Error reading [{0}] file entry from [{1}] component [{2}] container",
            new Object[] {name, componentName, containerType});
      }
      if(SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "Error reading [" + name + "] file entry from [" + componentName +
              "] component [" + containerType + "] container", e);
      }
    }
    return result;
  }

  public File getPersistentEntryFile(String name, boolean global) {
    if (LOCATION.beWarning()) {
      String message = ResourceUtils.formatString(ResourceUtils.GET_PERSISTENT_FILE_DEPRECATED, new Object[] {componentName, LOCATION.getName()});
      LOCATION.warningT(message);
      if (LOCATION.beDebug()) {
        LOCATION.traceThrowableT(Severity.DEBUG, message, new Exception());
      }
    }
    File result = null;
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      result = pec.getPersistentEntryAsFile(name);
    } catch (NameNotFoundException e) {
      //$JL-EXC$
      result = null;
    } catch (ClusterConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000003",
            "Error reading [{0}] file entry from [{1}] component [{2}] container",
            new Object[] {name, componentName, containerType});
      }
      if(SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "Error reading [" + name + "] file entry from [" + componentName +
              "] component [" + containerType + "] container", e);
      }
    }
    return result;
  }

  public void setPersistentEntryStream(String name, InputStream stream, boolean global) {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Update " + ((global) ? "global" : "local") + " file " + name + " : " + stream + " from component " + componentName);
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      pec.setPersistentEntryFromStream(name, false, stream);
    } catch (ClusterConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000004",
            "Error storing [{0}] file entry in [{1}] component [{2}] container",
            new Object[] {name, componentName, containerType});
      }
      if(SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "Error storing [" + name + "] file entry in [" + componentName +
              "] component [" + containerType + "] container", e);
      }
    }
  }

  public void setPersistentEntryFile(String name, File file, boolean global) {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Update " + ((global) ? "global" : "local") + " file " + name + " : " + file + " from component " + componentName);
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      pec.setPersistentEntryFromFile(name, false, file);
    } catch (ClusterConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000005",
            "Error storing [{0}] file entry in [{1}] component [{2}] container",
            new Object [] {name, componentName, containerType});
      }
      if(SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "Error storing [" + name + "] file entry in [" + componentName +
              "] component [" + containerType + "] container", e);
      }
    }
  }

  public void removePersistentEntry(String name, boolean global) throws ServiceException {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Remove " + ((global) ? "global" : "local") + " file " + name + " from component " + componentName);
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      pec.removePersistentEntry(name);
    } catch (ClusterConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000006",
            "Error removing [{0}] file envy from [{1}] component [{2}] container",
            new Object [] {name, componentName, containerType});
      }
      if(SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "Error removing [" + name + "] file envy from [" + componentName +
              "] component [" + containerType + "] container", e);
      }
    }
  }

  public InputStream getSecuredPersistentEntryStream(String name, boolean global) {
    return getPersistentEntryStream(name, global);
  }

  public File getSecuredPersistentEntryFile(String name, boolean global) {
    return getPersistentEntryFile(name, global);
  }

  public void setSecuredPersistentEntryStream(String name, InputStream stream, boolean global) {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Update " + ((global) ? "global" : "local") + " secured file " + name + " : " + stream + " from component " + componentName);
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      pec.setPersistentEntryFromStream(name, true, stream);
    } catch (ClusterConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000007",
            "Error storing [{0}] file entry in [{1}] component [{2}] container",
            new Object [] {name, componentName, containerType});
      }
      if(SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "Error storing [" + name + "] file entry in [" + componentName +
              "] component [" + containerType + "] container", e);
      }
    }
  }

  public void setSecuredPersistentEntryFile(String name, File file, boolean global) {
    if (LOCATION.beDebug()) {
      LOCATION.debugT("Update " + ((global) ? "global" : "local") + " secured file " + name + " : " + file + " from component " + componentName);
    }
    try {
      PersistentEntryContainer pec = getPersistentEntryContainer(global);
      pec.setPersistentEntryFromFile(name, true, file);
    } catch (ClusterConfigurationException e) {
      if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
        SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
            "ASJ.krn_srv.000008",
            "Error storing [{0}] file entry in [{1}] component [{2}] container",
            new Object[] {name, componentName, containerType});
      }
      if(SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
        SimpleLogger.traceThrowable(Severity.ERROR, LOCATION,
            "Error storing [" + name + "] file entry in [" + componentName +
              "] component [" + containerType + "] container", e);
      }
    }
  }

  public void removeSecuredPersistentEntry(String name, boolean global) throws ServiceException {
    removePersistentEntry(name, global);
  }

  private PersistentEntryContainer getPersistentEntryContainer(boolean global) throws ClusterConfigurationException {
    ConfigurationLevel level;
    if (global) {
      level = persistentContainer.getCustomGlobalLevel();
    } else {
      level = persistentContainer.getInstanceLevel();
    }
    return level.getComponentAccess().getDescriptorContainer(componentName, componentType);
  }

}