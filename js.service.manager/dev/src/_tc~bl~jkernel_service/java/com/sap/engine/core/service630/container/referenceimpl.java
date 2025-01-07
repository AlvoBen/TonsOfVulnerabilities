package com.sap.engine.core.service630.container;

import com.sap.engine.frame.container.monitor.Reference;

/**
 * This class represents a reference between components
 *
 * @author Dimitar Kosatdinov
 * @version 710
 */
public class ReferenceImpl implements Reference {

  private String name;
  private byte referentType;
  private byte type;

  private String providerName = null;
  private String componentName = null;

  private MemoryContainer container;

  //use for references
  public ReferenceImpl(MemoryContainer container, String componentName, String providerName, byte referentType, byte type) {
    this.container = container;
    this.componentName = componentName;
    this.providerName = providerName.equals("") ? ComponentWrapper.SAP_PROVIDERS[0] : providerName;
    this.referentType = referentType;
    this.type = type;
    this.name = ComponentWrapper.getRuntimeName(componentName, providerName, referentType);
  }

  //use for reverse references
  public ReferenceImpl(MemoryContainer container, String runtimeName, byte referentType, byte type) {
    this.container = container;
    this.componentName = runtimeName;
    this.providerName = ComponentWrapper.SAP_PROVIDERS[0];
    this.referentType = referentType;
    this.type = type;
    this.name = runtimeName;
  }

  public String getName() {
    return name;
  }

  public byte getReferentType() {
    return referentType;
  }

  public byte getType() {
    return type;
  }

  public String getComponentName() {
    return componentName;
  }

  public String getProviderName() {
    return providerName;
  }

  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof ReferenceImpl) {
      ReferenceImpl r = (ReferenceImpl) object;
      return name.equals(r.getName()) && referentType == r.getReferentType() && type == r.getType();
    }
    return false;
  }

  public int hashCode() {
    return (name + referentType + type).hashCode();
  }

  public String toString() {
    String tmp = "Reference " + ((type == TYPE_HARD) ? "<strong>" : "<weak>");
    switch (referentType) {
      case REFER_INTERFACE : return tmp + " to interface " + name;
      case REFER_LIBRARY : return tmp + " to library " + name;
      case REFER_SERVICE : return tmp + " to service " + name;
      default : return tmp + " to " + name;
    }
  }

  ComponentWrapper getReferencedComponent() {
    switch (referentType) {
      case REFER_INTERFACE : return container.getInterfaces().get(InterfaceWrapper.transformINameApiToIName(name));
      case REFER_LIBRARY : return container.getLibraries().get(name);
      case REFER_SERVICE : return container.getServices().get(name);
      default : return null;
    }
  }

}