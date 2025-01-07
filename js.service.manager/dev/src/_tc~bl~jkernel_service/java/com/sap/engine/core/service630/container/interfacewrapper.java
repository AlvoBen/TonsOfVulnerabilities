package com.sap.engine.core.service630.container;

import com.sap.engine.frame.container.monitor.InterfaceMonitor;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.core.Names;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.tc.logging.Location;
import com.sap.localization.LocalizableTextFormatter;

import java.util.Properties;
import java.util.Set;
import java.util.HashSet;

/**
 * Implements ComponentMonitor.
 *
 * @see com.sap.engine.frame.container.monitor.InterfaceMonitor
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class InterfaceWrapper extends ComponentWrapper implements InterfaceMonitor {

  private final static Set<String> INTERFACE_NAMES_API_SET = new HashSet<String>();
  private final static Set<String> INTERFACE_NAMES_SET = new HashSet<String>();

  static {
    for (int i = 0; i < InterfaceMonitor.INTERFACE_NAMES_API.length; i++) {
      INTERFACE_NAMES_API_SET.add(InterfaceMonitor.INTERFACE_NAMES_API[i]);
      INTERFACE_NAMES_SET.add(InterfaceMonitor.INTERFACE_NAMES[i]);
    }
  }

  public static String transformINameApiToIName(String interfaceName) {
    if (INTERFACE_NAMES_API_SET.contains(interfaceName)) {
      interfaceName = interfaceName.substring(0, interfaceName.length() - 4);
    }
    return interfaceName;
  }

  public static String transformINameToINameApi(String interfaceName) {
    if (INTERFACE_NAMES_SET.contains(interfaceName)) {
      interfaceName = interfaceName + "_api";
    }
    return interfaceName;
  }

  public static boolean existInInterfaceApiList(String name) {
    return INTERFACE_NAMES_SET.contains(name);
  }

  //name of the service that provides this interface
  private String serviceProviderName;

  private static final Location location = Location.getLocation(InterfaceWrapper.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  InterfaceWrapper(MemoryContainer memoryContainer, Properties properties) {
    super(memoryContainer, properties);
  }

  String getType() {
    return "interface";
  }

  byte getByteType() {
    return TYPE_INTERFACE;
  }

  public String getProvidingServiceName() {
    return serviceProviderName;
  }

  void setServiceProviderName(String name) {
    if (serviceProviderName == null) {
      serviceProviderName = name;
    } else {
      if (!serviceProviderName.equals(name)) {
        //this method can be invoked during online deployment.
        //exception must be thrown only if the names are not equals
        throw new ServiceRuntimeException(location, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
                ResourceUtils.getKey(ResourceUtils.PROVIDER_ALREADY_SET), new Object[] {componentName, serviceProviderName, name}));
      }
    }
  }

  ServiceWrapper getProvider() {
    ServiceWrapper result = null;
    if (serviceProviderName != null) {
      result = memoryContainer.getServices().get(serviceProviderName);
    }
    return result;
  }

}