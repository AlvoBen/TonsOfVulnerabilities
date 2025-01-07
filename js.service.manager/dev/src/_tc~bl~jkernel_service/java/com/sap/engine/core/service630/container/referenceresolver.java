package com.sap.engine.core.service630.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.frame.ServiceRuntimeException;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.Reference;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.lib.config.api.filters.ComponentFilter;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

/**
 * This class is used for resolving references, found reference cycles, found common loaders areas and all tasks
 * related to component references and resolving
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public class ReferenceResolver {

  private Hashtable<String, InterfaceWrapper> interfaces;
  private Hashtable<String, LibraryWrapper> libraries;
  private Hashtable<String, ServiceWrapper> services;

  private ContainerEventRegistry containerEventRegistry;
  private MemoryContainer container;

  private ComponentFilter[] ruleArray;

  private static final Category CATEGORY = Category.SYS_SERVER;
  /** Category used for logging critical engine messages for AGS. */
  private static final Category CATEGORY_SERVER_CRITICAL = Category.getCategory(Category.SYS_SERVER, "Critical");
  private static final Location LOCATION = Location.getLocation(ReferenceResolver.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  ReferenceResolver(MemoryContainer container) {
    this.container = container;
    this.interfaces = container.getInterfaces();
    this.libraries = container.getLibraries();
    this.services = container.getServices();
    this.containerEventRegistry = container.getServiceContainer().getContainerEventRegistry();
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * This method resolve all deployed components. A component is resolved when all referenced components are deployed.
   * On this step reverse references are initialized. The reverse references are used for unloading and stopping.
   * (A) has ref to (C1), ..., (Cn) and components (R1), ..., (Rm) has reference to (A) ->
   * (A) has references [(C1), ..., (Cn)] and reverse references [(R1), ..., (Rm)]
   */
  void resolveComponentReferences() {
    //hold all unresolved components
    Set<ComponentWrapper> unresolved = new HashSet<ComponentWrapper>();
    //1. init declared interface providers
    initDeclaredInterfaceProviders();
    checkForMissingInterfaceProviders();
    //2. init reverse references
    initReverseReferences(interfaces, unresolved);
    initReverseReferences(libraries, unresolved);
    initReverseReferences(services, unresolved);
    //3. fill unresolved set
    if (!unresolved.isEmpty()) {
      ComponentWrapper[] missingReferences = unresolved.toArray(new ComponentWrapper[unresolved.size()]);
      for (ComponentWrapper missingReference : missingReferences) {
        fillUnresolvedSet(missingReference, unresolved);
      }
    }
    //4. resolve
    resolveComponents(interfaces, unresolved);
    resolveComponents(libraries, unresolved);
    resolveComponents(services, unresolved);
  }

  /**
   * Initialize interface providers for all components with deploy status.
   */
  private void initDeclaredInterfaceProviders() {
    for (ServiceWrapper service : services.values()) {
      initDeclaredInterfaceProvidersForService(service);
    }
  }

  private void initDeclaredInterfaceProvidersForService(ServiceWrapper service) {
    if (service.getStatus() == ComponentMonitor.STATUS_DEPLOYED) {
      if (service.getProvidedInterfaces() != null) {
        for (String iName : service.getProvidedInterfaces()) {
          InterfaceWrapper interfaceWrapper = interfaces.get(iName);
          if (interfaceWrapper != null) {
            interfaceWrapper.setServiceProviderName(service.getComponentName());
          } else {
            if (LOCATION.beWarning()) {
              LOCATION.warningT(ResourceUtils.formatString(ResourceUtils.INTERFACE_NOT_REGISTERED_AS_COMPONENT,
                      new Object[]{iName, service.getComponentName()}));
            }
          }
        }
      }
    }
  }

  /**
   * Missing interface providers can causes runtime errors
   */
  private void checkForMissingInterfaceProviders() {
    for (InterfaceWrapper interfaceWrapper : interfaces.values()) {
      if (interfaceWrapper.getStatus() == ComponentMonitor.STATUS_DEPLOYED) {
        if (interfaceWrapper.getProvider() == null) {
          if (LOCATION.beWarning()) {
            SimpleLogger.trace(Severity.WARNING, LOCATION, interfaceWrapper.getDcName(),
                               interfaceWrapper.getCSNComponent(), "ASJ.krn_srv.000064",
                               "Interface [{0}] without declared provider",
                               null, interfaceWrapper.getComponentName());
          }
        }
      }
    }
  }

  /**
   * This method init reverse references and add components with missing references to unresolved set.
   */
  private void initReverseReferences(Hashtable<String, ? extends ComponentWrapper> components, Set<ComponentWrapper> unresolved) {
    for (ComponentWrapper component : components.values()) {
      //if component is with status deployed init reverce references ->
      if (component.getStatus() == ComponentMonitor.STATUS_DEPLOYED) {
        for (ReferenceImpl reference : component.getReferenceSet()) {
          ComponentWrapper referencedComponent = reference.getReferencedComponent();
          if (referencedComponent == null) {
            if (!unresolved.contains(component)) {
              unresolved.add(component);
              sendComponentNotResolvedEvent(component);
            }
            if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
              SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
                  "ASJ.krn_srv.000046",
                  "Cannot resolve component [{0}] because referenced component [{1}] not found",
                  new Object[]{component.toString(), reference.toString()});
            }
          } else {
            ReferenceImpl reverseReference = new ReferenceImpl(container, component.getComponentName(), component.getByteType(), reference.getType());
            referencedComponent.getReverseReferenceSet().add(reverseReference);
          }
        }
      }
    }
  }

  private void fillUnresolvedSet(ComponentWrapper component, Set<ComponentWrapper> unresolved) {
    for (ReferenceImpl reverseRef : component.getReverseReferenceSet()) {
      ComponentWrapper reveseComponent = reverseRef.getReferencedComponent();
      if (!unresolved.contains(reveseComponent)) {
        //add in unresolved set and send event
        unresolved.add(reveseComponent);
        sendComponentNotResolvedEvent(reveseComponent);
        if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
          SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION,
              "ASJ.krn_srv.000047",
              "Cannot resolve component [{0}] because referenced component [{1}] not resolved",
              new Object[] {reveseComponent.toString(), component.toString()});
        }
        fillUnresolvedSet(reveseComponent, unresolved);
      }
    }
  }

  private void resolveComponents(Hashtable<String, ? extends ComponentWrapper> components, Set<ComponentWrapper> unresolved) {
    for (ComponentWrapper component : components.values()) {
      if (component.getStatus() == ComponentMonitor.STATUS_DEPLOYED && !unresolved.contains(component)) {
        component.setStatus(ComponentMonitor.STATUS_RESOLVED);
        sendComponentResolvedEvent(component);
      }
    }
  }

  private void sendComponentResolvedEvent(ComponentWrapper component) {
    ContainerEvent event = new ContainerEvent();
    event.method = ContainerEventListener.MASK_COMPONENT_RESOLVED;
    event.isAdmin = true;
    event.name = component.getComponentName();
    event.type = component.getByteType();
    containerEventRegistry.addContainerEvent(event);
  }

  private void sendComponentNotResolvedEvent(ComponentWrapper component) {
    ContainerEvent event = new ContainerEvent();
    event.method = ContainerEventListener.MASK_COMPONENT_NOT_RESOLVED;
    event.isAdmin = true;
    event.name = component.getComponentName();
    event.type = component.getByteType();
    containerEventRegistry.addContainerEvent(event);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Remove component from other components reverse references set.
   * Used when removing component from service container registry.
   */
  void removeFromReverseReferences(ComponentWrapper cw) {
    for (ReferenceImpl reference : cw.getReferenceSet()) {
      ComponentWrapper referencedComponent = reference.getReferencedComponent();
      if (referencedComponent != null) {//component could be null if removing one is not resolved!
        Set<ReferenceImpl> reverseSet = referencedComponent.getReverseReferenceSet();
        ReferenceImpl[] reverseReferences = reverseSet.toArray(new ReferenceImpl[reverseSet.size()]);
        for (ReferenceImpl ref : reverseReferences) {
          if (ref.getName().equals(cw.getComponentName()) && ref.getReferentType() == cw.getByteType()) {
            reverseSet.remove(ref);
          }
        }
      }
    }
  }

  /**
   * Resolve single component. Used for runtime deployment.
   * @return set of newly resolved components
   */
  Set<ComponentWrapper> resolveComponent(ComponentWrapper component) {
    Set<ComponentWrapper> unresolved = new HashSet<ComponentWrapper>();
    if (component.getByteType() == ComponentWrapper.TYPE_SERVICE) {
      //1. if current component is service and provides interfaces -> check for interface existences.
      initDeclaredInterfaceProvidersForService((ServiceWrapper) component);
    } else if (component.getByteType() == ComponentWrapper.TYPE_INTERFACE) {
      //2. if current component is interface search for service that provides it
      searchForServiceProvider((InterfaceWrapper) component);
    }
    //3. init reverse references to other components
    for (ReferenceImpl reference : component.getReferenceSet()) {
      ComponentWrapper referencedComponent = reference.getReferencedComponent();
      if (referencedComponent != null) {
        ReferenceImpl reverseReference = new ReferenceImpl(container, component.getComponentName(), component.getByteType(), reference.getType());
        referencedComponent.getReverseReferenceSet().add(reverseReference);
      } else {
        if (!unresolved.contains(component)) {
          unresolved.add(component);
          sendComponentNotResolvedEvent(component);
        }
        if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
          SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.krn_srv.000048", "Cannot resolve component [{0}] because referenced component [{1}] not found", new Object[]{component.toString(), reference.toString()});
        }
      }
    }
    //4. init component reverse references from other components with deployed status.
    initComponentReverseReferences(component, interfaces, unresolved);
    initComponentReverseReferences(component, libraries, unresolved);
    initComponentReverseReferences(component, services, unresolved);
    if (!unresolved.contains(component)) {
      ComponentWrapper[] missingReferences = unresolved.toArray(new ComponentWrapper[unresolved.size()]);
      for (ComponentWrapper missingReference : missingReferences) {
        fillUnresolvedSetRuntime(component, missingReference, unresolved);
      }
    }
    //5. resolve
    Set<ComponentWrapper> result = new HashSet<ComponentWrapper>();
    resolveSingleComponent(component, unresolved, result);
    return result;
  }

  private void resolveSingleComponent(ComponentWrapper component, Set<ComponentWrapper> unresolved, Set<ComponentWrapper> resolved) {
    if (component.getStatus() == ComponentMonitor.STATUS_DEPLOYED && !unresolved.contains(component)) {
      component.setStatus(ComponentMonitor.STATUS_RESOLVED);
      sendComponentResolvedEvent(component);
      resolved.add(component);
      for (ReferenceImpl reference : component.getReverseReferenceSet()) {
        ComponentWrapper reverseComponent = reference.getReferencedComponent();
        resolveSingleComponent(reverseComponent, unresolved, resolved);
      }
    }
  }

  private void searchForServiceProvider(InterfaceWrapper interfaceWrapper) {
    for (ServiceWrapper service : services.values()) {
      if (service.getStatus() == ComponentMonitor.STATUS_DEPLOYED) {
        if (service.getProvidedInterfaces() != null) {
          for (String iName : service.getProvidedInterfaces()) {
            if (iName.equals(interfaceWrapper.getComponentName())) {
              interfaceWrapper.setServiceProviderName(service.getComponentName());
            }
          }
        }
      }
    }
    if (interfaceWrapper.getProvider() == null) {
      if (LOCATION.beWarning()) {
        LOCATION.warningT(ResourceUtils.formatString(ResourceUtils.INTERFACE_WITHOUT_PROVIDER, new Object[] {interfaceWrapper.getComponentName()}));
      }
    }
  }

  private void initComponentReverseReferences(ComponentWrapper deployingComponent, Hashtable<String, ? extends ComponentWrapper> components, Set<ComponentWrapper> unresolved) {
    for (ComponentWrapper component : components.values()) {
      if (component.getStatus() == ComponentMonitor.STATUS_DEPLOYED) {//if component is with status deployed
        for (ReferenceImpl reference : component.getReferenceSet()) {
          ComponentWrapper referencedComponent = reference.getReferencedComponent();
          if (referencedComponent == null) {
            unresolved.add(component); //no evet is needed!
          } else if (deployingComponent.equals(referencedComponent)) {//add reverse ref
            ReferenceImpl reverseReference = new ReferenceImpl(container, component.getComponentName(), component.getByteType(), reference.getType());
            deployingComponent.getReverseReferenceSet().add(reverseReference);
          }
        }
      }
    }
  }

  private void fillUnresolvedSetRuntime(ComponentWrapper deployingComponent, ComponentWrapper component, Set<ComponentWrapper> unresolved) {
    for (ReferenceImpl reverseRef : component.getReverseReferenceSet()) {
      ComponentWrapper reveseComponent = reverseRef.getReferencedComponent();
      if (!unresolved.contains(reveseComponent)) {
        unresolved.add(reveseComponent);
        if (deployingComponent.equals(reveseComponent)) {
          //send event only for deploying component
          sendComponentNotResolvedEvent(reveseComponent);
          if (SimpleLogger.isWritable(Severity.ERROR, CATEGORY)) {
            SimpleLogger.log(Severity.ERROR, CATEGORY, LOCATION, "ASJ.krn_srv.000049", "Cannot resolve component [{0}] because referenced component [{1}] not resolved", new Object[] {reveseComponent.toString(), component.toString()});
          }
        }
        fillUnresolvedSetRuntime(deployingComponent, reveseComponent, unresolved);
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Check up whether service in core list exists and are loaded. The method is called on initial start.
   */
  void checkCoreComponentsExisting() {
    boolean exist = true;
    for (String aCORE_SERVICES : MemoryContainer.CORE_SERVICES) {
      ServiceWrapper service = services.get(aCORE_SERVICES);
      if (service == null) {
        exist = false;
        SimpleLogger.log(Severity.FATAL, CATEGORY_SERVER_CRITICAL, LOCATION, "ASJ.krn_srv.000050", "Core service [{0}] missing", aCORE_SERVICES);
      } else if (service.getStatus() != ComponentMonitor.STATUS_LOADED) {
        exist = false;
        SimpleLogger.log(Severity.FATAL, CATEGORY_SERVER_CRITICAL, LOCATION, "ASJ.krn_srv.000051", "Core service [{0}] not loaded", aCORE_SERVICES);
      }
    }
    if (!exist) {
      Framework.criticalShutdown(MemoryContainer.CORE_COMPONENTS_MISSING_OR_NOT_LOADED_EXIT_CODE, ResourceUtils.getString(ResourceUtils.CORE_COMPONENTS_MISSING_OR_NOT_LOADED));
    }
  }

  /**
   * Returns core services set. This method is invoked only once,
   * before getStartupServiceSet().
   */
  Set<ServiceWrapper> getCoreServiceSet() {
    Set<ServiceWrapper> result = new HashSet<ServiceWrapper>();
    Set<ServiceWrapper> allServices = new HashSet<ServiceWrapper>(services.values());
    for (String aCORE_SERVICES : MemoryContainer.CORE_SERVICES) {
      ServiceWrapper service = services.get(aCORE_SERVICES);
      addServiceToStartSet(service, result, allServices);
    }
    return result;
  }

  /**
   * Returns startup service set according declared filters
   * This method is invoked only once.
   */
  Set<ServiceWrapper> getStartupServiceSet() {
    Set<ServiceWrapper> result = new HashSet<ServiceWrapper>();
    //initialize filters
    try {
      ConfigurationLevel level = container.getPersistentContainer().getInstanceLevel();
      List allFilters = level.getFilters(true).getUpperLevelsFilters();
      List localFilters = level.getFilters(true).getFilter();
      allFilters.addAll(localFilters);
      ruleArray = new ComponentFilter[allFilters.size()];
      allFilters.toArray(ruleArray);
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Filters: " + Arrays.toString(ruleArray));
      }
      ruleArray = optimizeFilters(ruleArray);
      if (LOCATION.beDebug()) {
        LOCATION.debugT("Optimized filters: " + Arrays.toString(ruleArray));
      }
    } catch (ClusterConfigurationException e) {
      SimpleLogger.log(Severity.FATAL, CATEGORY_SERVER_CRITICAL, LOCATION, "ASJ.krn_srv.000052",
          "Cannot read component filters; possible DB problem or component filters are not well defined, check filters.txt in DB; detailed information: [{0}]", e.getMessage());
      SimpleLogger.traceThrowable(Severity.FATAL, LOCATION,
          "Cannot read component filters; possible DB problem or component " +
            "filters are not well defined, check filters.txt in DB; detailed " +
            "information: [" + e.toString() + "]", e);
      Framework.criticalShutdown(MemoryContainer.ERROR_READING_FILTERS_EXIT_CODE,
          "Cannot read component filters; possible DB problem or component " +
            "filters are not well defined, check filters.txt in DB; detailed " +
            "information: [" + e.toString() + "]");
    }
    if (ruleArray.length > 0) {
      //evaluate filters
      Set<ServiceWrapper> allServices = new HashSet<ServiceWrapper>(services.values());
      evaluateFilters(result, allServices);
    } else {
      SimpleLogger.log(Severity.FATAL, CATEGORY_SERVER_CRITICAL, LOCATION,
          "ASJ.krn_srv.000053",
          "Filters set is empty; component filters are not well defined and services cannot be started, check filters.txt in DB");
      Framework.criticalShutdown(MemoryContainer.FILTERS_EMPTY_EXIT_CODE,
          "Filters set is empty; component filters are not well defined and services cannot be started, check filters.txt in DB");
    }
    return result;
  }

  private void evaluateFilters(Set<ServiceWrapper> startupSet, Set<ServiceWrapper> services) {
    for (ComponentFilter filter : ruleArray) {
      if (filter.getAction() == ComponentFilter.ACTION_START) {
        //start rule
        for (ServiceWrapper serviceWrapper : services) {
          //apply rule to loaded services only!
          if (serviceWrapper.getStatus() == ComponentMonitor.STATUS_LOADED) {
            if (filter.matches(ComponentFilter.ACTION_START, ComponentFilter.COMPONENT_SERVICE,
                    serviceWrapper.getProviderName(), getMatchComponentName(serviceWrapper))) {
              addServiceToStartSet(serviceWrapper, startupSet, services);
            }
          }
        }
      } else if (filter.getAction() == ComponentFilter.ACTION_STOP) {
        //stop rule
        ServiceWrapper[] startArray = new ServiceWrapper[startupSet.size()];
        startupSet.toArray(startArray);
        for (ServiceWrapper serviceWrapper : startArray) {
          //when remove is procede a chain of component can be removed -> check whether component is in start set
          if (startupSet.contains(serviceWrapper)) {
            if (filter.matches(ComponentFilter.ACTION_STOP, ComponentFilter.COMPONENT_SERVICE,
                    serviceWrapper.getProviderName(), getMatchComponentName(serviceWrapper))) {
              removeServiceFromStartSet(serviceWrapper, startupSet, false);
            }
          }
        }
      } else if (filter.getAction() == ComponentFilter.ACTION_DISABLE) {
        //disable rule
        for (ServiceWrapper serviceWrapper : services) {
          if (filter.matches(ComponentFilter.ACTION_DISABLE, ComponentFilter.COMPONENT_SERVICE,
                  serviceWrapper.getProviderName(), getMatchComponentName(serviceWrapper))) {
            removeServiceFromStartSet(serviceWrapper, startupSet, true);
          }
        }
      }
    }
  }

  /**
   * Returns runtime name of the component, but in case of not sap provider the provider prefix from
   * runtime name is removed.
   *
   * @param service - service.
   * @return name for filter match.
   */
  private String getMatchComponentName(ServiceWrapper service) {
    if (ComponentWrapper.isSapProvider(service.getProviderName())) {
      return service.getComponentName();
    } else {
      return service.getComponentName().substring(service.getProviderName().length() + 1);
    }
  }

  /**
   * Applies filter rules array for newly resolved component and return set of services that must be started accordingly
   */
  Set<ServiceWrapper> applyFilters(Set<ComponentWrapper> newlyResolved) {
    Set<ServiceWrapper> result = new HashSet<ServiceWrapper>();
    Set<ServiceWrapper> services = new HashSet<ServiceWrapper>();
    for (ComponentWrapper component : newlyResolved) {
      if (component.getByteType() == ComponentWrapper.TYPE_SERVICE ) {
        services.add((ServiceWrapper) component);
      }
    }
    if (!services.isEmpty()) {
      evaluateFilters(result, services);
    }
    return result;
  }

  //todo move all rules optimization in config lib
  //optimize rule array
  private ComponentFilter[] optimizeFilters(ComponentFilter[] filters) {
    ComponentFilter[] result = filters;
    int pos = -1;
    for (int i = filters.length - 1; i > -1; i--) {
      int component = filters[i].getComponent();
      //only for services
      if (component == ComponentFilter.COMPONENT_ALL || component == ComponentFilter.COMPONENT_SERVICE) {
        String nameMask = filters[i].getNameMask();
        String vendorMask = filters[i].getVendorMask();
        if ((nameMask.equals("*") || nameMask.equals("")) && (vendorMask.equals("*") || (vendorMask.equals("")))) {
          //found
          pos = i;
          break;
        }
      }
    }
    if (pos > 0) {
      ArrayList<ComponentFilter> rules = new ArrayList<ComponentFilter>();
      //add disable rules
      for (int i = 0; i < pos; i++) {
        if (filters[i].getAction() == ComponentFilter.ACTION_DISABLE) {
          rules.add(filters[i]);
        }
      }
      //add the rest
      for (int i = pos; i < filters.length; i++) {
        rules.add(filters[i]);
      }
      result = new ComponentFilter[rules.size()];
      rules.toArray(result);
    }
    return result;
  }

  /**
   * Add service to startup set. All ref & reverse ref exists.
   */
  private void addServiceToStartSet(ServiceWrapper service, Set<ServiceWrapper> startupSet, Set<ServiceWrapper> services) {
    if (!service.isDisabled()) {
      //if service is not disabled -> all hard references are not disabled too
      startupSet.add(service);
      for (ReferenceImpl reference : service.getReferenceSet()) {
        if (reference.getType() == Reference.TYPE_HARD) {
          if (reference.getReferentType() == Reference.REFER_SERVICE) {
            ServiceWrapper referencedService = (ServiceWrapper) reference.getReferencedComponent();
            if (services.contains(referencedService)) {
              addServiceToStartSet(referencedService, startupSet, services);
            }
          } else if (reference.getReferentType() == Reference.REFER_INTERFACE) {
            InterfaceWrapper referencedInterface = (InterfaceWrapper) reference.getReferencedComponent();
            ServiceWrapper interfaceProvider = referencedInterface.getProvider();
            if (interfaceProvider != null && interfaceProvider.getStatus() == ComponentMonitor.STATUS_LOADED) {
              if (services.contains(interfaceProvider)) {
                addServiceToStartSet(interfaceProvider, startupSet, services);
              }
            } else {
              //interface provider can be <null> or not resolved in both cases remove service from startup set
              if (LOCATION.beWarning()) {
                if (interfaceProvider == null) {
                  LOCATION.warningT(ResourceUtils.formatString(ResourceUtils.CANT_ADD_TO_STARTUP_SET_NOT_DEPLOYED,
                          new Object[] {service, referencedInterface}));
                } else {
                  LOCATION.warningT(ResourceUtils.formatString(ResourceUtils.CANT_ADD_TO_STARTUP_SET_NOT_RESOLVED,
                          new Object[] {service, referencedInterface, interfaceProvider}));
                }
              }
              removeServiceFromStartSet(service, startupSet, false);
              break;
            }
          } else {
            //no sense to have hard ref to library -> nothing to do
          }
        }
      }
    }
  }

  /**
   * Remove from startup set and set disabled flag. Can't disable core service. All ref & reverse ref exists.
   */
  private void removeServiceFromStartSet(ServiceWrapper service, Set<ServiceWrapper> startupSet, boolean markDisabled) {
    if (markDisabled) {
      if (container.getCoreServiceSet().contains(service)) {
        if (SimpleLogger.isWritable(Severity.WARNING, CATEGORY)) {
          SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.krn_srv.000054", "Cannot disable core service [{0}]", service.getComponentName());
        }
        markDisabled = false;
      } else {
        service.setDisabled();
      }
    }
    startupSet.remove(service);
    for (ReferenceImpl reference : service.getReverseReferenceSet()) {
      if (reference.getType() == Reference.TYPE_HARD) {
        if (reference.getReferentType() == Reference.REFER_SERVICE) {
          ServiceWrapper reverseService = (ServiceWrapper) reference.getReferencedComponent();
          removeServiceFromStartSet(reverseService, startupSet, markDisabled);
        } else {
          //no sense to have hard ref from library or interface -> nothing to do
        }
      }
    }
    if (service.getProvidedInterfaces() != null) {
      for (String interfaceName : service.getProvidedInterfaces()) {
        InterfaceWrapper interfaceWrapper = interfaces.get(interfaceName);//interface exists because component is resolved
        for (ReferenceImpl reference : interfaceWrapper.getReverseReferenceSet()) {
          if (reference.getType() == Reference.TYPE_HARD && reference.getReferentType() == Reference.REFER_SERVICE) {
            ServiceWrapper reverseService = (ServiceWrapper) reference.getReferencedComponent();
            removeServiceFromStartSet(reverseService, startupSet, markDisabled);
          }
        }
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Check up for hard reference cycles. If cycle exists and the method is called on initial start call critical shutdown.
   * If exists and the method is called at runtime throws exception.
   */
  void checkForHardReferenceCycles(boolean runtime) throws ServiceRuntimeException {
    ArrayList<ComponentWrapper> resolved = getResolvedComponents();
    //prepare SCCAlgorithm
    ComponentWrapper component;
    ArrayList<ComponentWrapper> tmp = new ArrayList<ComponentWrapper>(); //use to hold tmp objects
    ComponentWrapper[] nodes = new ComponentWrapper[resolved.size()];
    ComponentWrapper[][] adjList = new ComponentWrapper[resolved.size()][];
    for (int i = 0; i < resolved.size(); i++) {
      tmp.clear();
      component = resolved.get(i);
      component.setNodeId(i);
      nodes[i] = component;
      for (ReferenceImpl reference : component.getReferenceSet()) {
        if (reference.getType() == Reference.TYPE_HARD) {
          ComponentWrapper referenceComponent = reference.getReferencedComponent();
          if (referenceComponent.getStatus() == ComponentMonitor.STATUS_RESOLVED) {
            //add only resolved - loaded has no impact and are out of the graph
            tmp.add(referenceComponent);
          }
        }
      }
      if (component.getByteType() == ComponentWrapper.TYPE_INTERFACE) {
        // Hard reference to interface; add provider service
        ComponentWrapper interfaceProvider = ((InterfaceWrapper)component).getProvider();
        if (interfaceProvider != null) {
          tmp.add(interfaceProvider);
        }
      }
      adjList[i] = tmp.toArray(new ComponentWrapper[tmp.size()]);
    }
    //SCCAlgorithm
    SCCAlgorithm sccAlgorithm = new SCCAlgorithm(nodes, adjList);
    //each strongly connected group has different color
    int[] areas = sccAlgorithm.strongComponents();
    Set<ComponentWrapper[]> cycles = new HashSet<ComponentWrapper[]>();
    int color;
    for (int i = 0; i < areas.length; i++) {
      color = areas[i];
      if (color != -1) {
        tmp.clear();
        for (int j = i; j < areas.length; j++) {
          if (areas[j] == color) {
            tmp.add(nodes[j]);
            areas[j] = -1;
          }
        }
        if (tmp.size() > 1) { //-> cycle found!
          cycles.add(tmp.toArray(new ComponentWrapper[tmp.size()]));
        }
      }
    }
    if (cycles.size() > 0) {
      String strCycles = getCyclesAsString(cycles);
      if (runtime) {
        if (SimpleLogger.isWritable(Severity.ERROR, LOCATION)) {
          SimpleLogger.trace(Severity.ERROR, LOCATION,
              "AS Java does not support cyclic runtime strong references: the following components form a cyclic references chain: [" + strCycles + "]");
        }
        throw new ServiceRuntimeException(LOCATION, new LocalizableTextFormatter(ResourceUtils.resourceAccessor,
              ResourceUtils.getKey(ResourceUtils.CYCLE_FOUND), new Object[] {strCycles}));
      } else {
        SimpleLogger.log(Severity.FATAL, CATEGORY_SERVER_CRITICAL, LOCATION, "ASJ.krn_srv.000055",
            "AS Java does not support cyclic runtime strong references: the following components form a cyclic references chain: [{0}]", strCycles);
        Framework.criticalShutdown(MemoryContainer.HARD_REF_CYCLE_DETECTED_EXIT_CODE,
            "AS Java does not support cyclic runtime strong references: the following components form a cyclic references chain: [" + strCycles + "]");
      }
    }
  }

  ArrayList<ComponentWrapper[]> findCommonLoaderAreas() {
    ArrayList<ComponentWrapper> resolved = getResolvedComponents();
    //prepare SCCAlgorithm
    ComponentWrapper component;
    ArrayList<ComponentWrapper> tmp = new ArrayList<ComponentWrapper>(); //use to hold tmp objects
    ComponentWrapper[] nodes = new ComponentWrapper[resolved.size()];
    ComponentWrapper[][] adjList = new ComponentWrapper[resolved.size()][];
    for (int i = 0; i < resolved.size(); i++) {
      tmp.clear();
      component = resolved.get(i);
      component.setNodeId(i);
      nodes[i] = component;
      for (ReferenceImpl reference : component.getReferenceSet()) {
        ComponentWrapper referenceComponent = reference.getReferencedComponent();
        if (referenceComponent.getStatus() == ComponentMonitor.STATUS_RESOLVED) {
          //add only resolved - loaded has no impact and are out of the graph
          tmp.add(referenceComponent);
        }
      }
      adjList[i] = tmp.toArray(new ComponentWrapper[tmp.size()]);
    }
    //SCCAlgorithm
    SCCAlgorithm sccAlgorithm = new SCCAlgorithm(nodes, adjList);
    //each strongly connected group has different color
    int[] areas = sccAlgorithm.strongComponents();
    ArrayList<ComponentWrapper[]> loaderAreas = new ArrayList<ComponentWrapper[]>();
    int color;
    for (int i = 0; i < areas.length; i++) {
      color = areas[i];
      if (color != -1) {
        tmp.clear();
        for (int j = i; j < areas.length; j++) {
          if (areas[j] == color) {
            tmp.add(nodes[j]);
            areas[j] = -1;
          }
        }
        loaderAreas.add(tmp.toArray(new ComponentWrapper[tmp.size()]));
      }
    }
    return loaderAreas;
  }

  /**
   * Check up for classload cycles and call critical shutdown if haltOnClassloadCycle=true.
   * The method is called on initial start.
   */
  void checkForClassloadCycles(ArrayList<ComponentWrapper[]> loaderAreas, boolean haltOnClassloadCycle) {
    String cycles = getCyclesAsString(loaderAreas);
    if (cycles != null) {
      if (haltOnClassloadCycle) {
        Framework.criticalShutdown(MemoryContainer.CLASSLOAD_CYCLE_DETECTED_EXIT_CODE,
            "AS Java does not support cyclic runtime references; the following components form a cyclic references chain: [" + cycles + "]");
      } else {
        if (SimpleLogger.isWritable(Severity.WARNING, CATEGORY)) {
          SimpleLogger.log(Severity.WARNING, CATEGORY, LOCATION, "ASJ.krn_srv.000056",
              "AS Java does not support cyclic runtime references; the following components form a cyclic references chain: [{0}]", cycles);
        }
      }
    }
  }

  private ArrayList<ComponentWrapper> getResolvedComponents() {
    ArrayList<ComponentWrapper> resolved = new ArrayList<ComponentWrapper>();
    for (ComponentWrapper component : interfaces.values()) {
      if (component.getStatus() == ComponentMonitor.STATUS_RESOLVED) {
        resolved.add(component);
      }
    }
    for (ComponentWrapper component : libraries.values()) {
      if (component.getStatus() == ComponentMonitor.STATUS_RESOLVED) {
        resolved.add(component);
      }
    }
    for (ComponentWrapper component : services.values()) {
      if (component.getStatus() == ComponentMonitor.STATUS_RESOLVED) {
        resolved.add(component);
      }
    }
    return resolved;
  }

  private String getCyclesAsString(Collection<ComponentWrapper[]> cycles) {
    StringBuilder buffer = new StringBuilder();
    boolean hasCycles = false;
    buffer.append("[");
    for (ComponentWrapper[] cycle : cycles) {
      if (cycle.length > 1) {
        hasCycles = true;
        buffer.append("{");
        for (int i = 0; i < cycle.length; i++) {
          ComponentWrapper component = cycle[i];
          buffer.append(component.toString());
          if (i != cycle.length - 1) {
            buffer.append(", ");
          }
        }
        buffer.append("}");
      }
    }
    buffer.append("].");
    String result = null;
    if (hasCycles) {
      result = buffer.toString();
    }
    return result;
  }

}