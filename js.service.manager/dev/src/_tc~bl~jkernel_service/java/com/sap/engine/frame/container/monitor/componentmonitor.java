package com.sap.engine.frame.container.monitor;

import java.io.InputStream;

/**
 * General interface for component monitoring
 *
 * @author Dimitar Kostadinov
 */
public interface ComponentMonitor {

  /**
   * Not used
   */
  public final static byte STATUS_MISSING = 1;

  /**
   * State after creating a ComponentWrapper object.
   */
  public final static byte STATUS_DEPLOYED = 2;

  /**
   * All references to other components could be resolved, e.g.
   * the components exist. This also means that all components
   * that this component depends on are also resolved.
   */
  public final static byte STATUS_RESOLVED = 3;

  /**
   * The component has a classloader associated with it
   */
  public final static byte STATUS_LOADED = 4;

  /**
   * For services only: the service is started
   */
  public final static byte STATUS_ACTIVE = 5;

  /**
   * Returns the runtime component name
   *
   * @return runtime name
   */
  public String getComponentName();

  /**
   * Returns the component provider name
   *
   * @return provider
   */
  public String getProviderName();

  /**
   * Returns display name (described in provider.xml) used from GUI
   *
   * @return display name
   */
  public String getDisplayName();

  /**
   * Returns description (from provider.xml) for this component, used from GUI
   *
   * @return description
   */
  public String getDescription();

  /**
   * Returns major version (described in provider.xml)
   *
   * @return major version
   */
  public String getMajorVersion();

  /**
   * Returns minor version (described in provider.xml)
   *
   * @return minor version
   */
  public String getMinorVersion();

  /**
   * Returns micro version (described in provider.xml)
   *
   * @return micro version
   */
  public String getMicroVersion();

  /**
   * Returns group name (described in provider.xml)
   *
   * @return group name
   */
  public String getGroupName();

  /**
   * Returns component references (described in provider.xml)
   *
   * @see com.sap.engine.frame.container.monitor.Reference
   *
   * @return component references
   */
  public Reference[] getReferences();

  /**
   * Returns String array with component jar names described in provider.xml
   *
   * @return component jars
   */
  public String[] getJars();

  /**
   * Returns component status. The status can be STATUS_DEPLOYED, STATUS_RESOLVED, STATUS_LOADED, STATUS_ACTIVE
   *
   * @return current status
   */
  public byte getStatus();

  /**
   * Returns component descriptors container.
   *
   * @see com.sap.engine.frame.container.monitor.DescriptorContainer
   *
   * @return descriptor container
   */
  public DescriptorContainer getDescriptorContainer();

  /**
   * Returns SAP_MANIFEST.MF for this component
   *
   * @return SAP_MANIFEST.MF or null if not exists
   */
  public InputStream getSAPManifest();

}

