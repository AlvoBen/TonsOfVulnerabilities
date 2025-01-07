package com.sap.engine.frame.container.monitor;

import com.sap.engine.frame.container.event.ContainerEventListener;

/**
 * Interface representing a reference between components
 *
 * @author Dimitar Kostadinov
 */
public interface Reference {

  /**
   * reference to interface
   */
  public final static byte REFER_INTERFACE = ContainerEventListener.INTERFACE_TYPE;

  /**
   * reference to library
   */
  public final static byte REFER_LIBRARY = ContainerEventListener.LIBRARY_TYPE;

  /**
   * reference to service
   */
  public final static byte REFER_SERVICE = ContainerEventListener.SERVICE_TYPE;

  /**
   * Not used
   */
  public final static byte TYPE_NOTIFY = 10;

  /**
   * Representing classload reference
   */
  public final static byte TYPE_SOFT = 11;

  /**
   * Representing classload + functional reference
   */
  public final static byte TYPE_HARD = 12;


  /**
   * The component name (register name in service container).
   *
   * @return component name
   */
  public String getName();

  /**
   * Returns the type of the pointed component. The type can be interface library or service (REFER_INTERFACE,
   * REFER_LIBRARY or REFER_SERVICE)
   *
   * @return type of the pointed component
   */
  public byte getReferentType();

  /**
   * Returns reference type. Type can be weak (TYPE_SOFT) or hard (strong) type (TYPE_HARD)
   *
   * @return type
   */
  public byte getType();

  /**
   * Component name (from provider.xml reference tag)
   *
   * @return component name
   */
  public String getComponentName();

  /**
   * Provider name (from provider.xml reference tag - provider-name attribute)
   *
   * @return provider name
   */
  public String getProviderName();

}