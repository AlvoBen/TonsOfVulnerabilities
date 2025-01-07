package com.sap.engine.core.service630.container;

import com.sap.engine.core.classload.ClassLoaderManager;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.frame.core.load.UnknownClassLoaderException;
import com.sap.engine.frame.core.load.LoadContext;
import com.sap.engine.lib.time.SystemTime;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.*;
import java.net.MalformedURLException;

/**
 * This class creates component class loaders.
 *
 * @author Dimitar Kostadinov, Nikolai Dimitriv
 * @version 710
 */
public class LoadContainer {

  private ClassLoaderManager loadManager;
  private ClassLoader frameLoader;

  private ContainerEventRegistry containerEventRegistry;

  private static final Location location = Location.getLocation(LoadContainer.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  LoadContainer(MemoryContainer memoryContainer) {
    containerEventRegistry = memoryContainer.getServiceContainer().getContainerEventRegistry();
    loadManager = (ClassLoaderManager) Framework.getManager(Names.CLASSLOADER_MANAGER);
    frameLoader = Framework.getFrameLoader();
  }

  /**
   * Component class loaders are created here. Each element from the array list represents a loader area with participants.
   * The list is sorting topologically and after that the loaders are created.
   */
  void createClassLoaders(final ArrayList<ComponentWrapper[]> loaderAreas) {
    //1. topologic sort according resolved references
    if (location.beDebug()) {
      printLoadAreaInfo(loaderAreas, "Load Areas");
    }
    ArrayList<ComponentWrapper[]> notSorted = (ArrayList<ComponentWrapper[]>) loaderAreas.clone();
    ArrayList<ComponentWrapper[]> topologicSorted = new ArrayList<ComponentWrapper[]>();
    Set<ComponentWrapper> sortedNodes = new HashSet<ComponentWrapper>();
    while (!notSorted.isEmpty()) {
      ComponentWrapper[][] loaders = notSorted.toArray(new ComponentWrapper[notSorted.size()][]);
      for (ComponentWrapper[] loaderArea : loaders) {
        if (loaderArea.length == 1) {
          if (getNotCreatedParentsCount(loaderArea[0], sortedNodes) == 0) {
            sortedNodes.add(loaderArea[0]);
            topologicSorted.add(loaderArea);
            notSorted.remove(loaderArea);
          }
        } else {
          //common area case:
          if (getNotCreatedParentsCount(loaderArea, sortedNodes) == 0) {
            for (ComponentWrapper component : loaderArea) {
              sortedNodes.add(component);
            }
            topologicSorted.add(loaderArea);
            notSorted.remove(loaderArea);
          }
        }
      }
    }
    if (location.beDebug()) {
      printLoadAreaInfo(topologicSorted, "Topologic Sort Load Areas");
    }
    //2. create loaders
    ArrayList<ClassLoader> parents = new ArrayList<ClassLoader>();
    ArrayList<String> classPaths = new ArrayList<String>();
    ClassLoader componentLoader;
    long loadersRegisterTime = 0;
    for (ComponentWrapper[] loaderArea : topologicSorted) {
      String loaderName = getLoaderName(loaderArea); //generate loader name
      long beginMicros = SystemTime.currentTimeMicros();
      try {
        //clear
        parents.clear();
        classPaths.clear();
        parents.add(frameLoader); //add frame loader as first parent
        if (loaderArea.length == 1) {
          //use new register method
          ComponentWrapper component = loaderArea[0];
          Set<ComponentWrapper> loaderParticipants = new HashSet<ComponentWrapper>();
          loaderParticipants.add(component);
          if (component.getClassLoader() == null) {
            for (ReferenceImpl reference : component.getReferenceSet()) {
              parents.add(reference.getReferencedComponent().getClassLoader());
            }
            for (String jar : component.getJars()) {
              classPaths.add(jar);
            }
            String componentName = component.getComponentName();
            int type = getLoadType(component.getByteType());
            componentLoader = loadManager.createClassLoader(parents.toArray(new ClassLoader[parents.size()]), classPaths.toArray(new String[classPaths.size()]), loaderName, component.getCSNComponent(), componentName, type);
            if (location.beDebug()) {
              location.debugT("createClassLoader : " + loaderName);
            }
            component.setClassLoader(componentLoader);
          } //else core_lib loader is initialized in the constructor
          component.setLoaderParticipant(loaderParticipants);
        } else {
          //common area case:
          Set<ComponentWrapper> tmp = new HashSet<ComponentWrapper>();
          Set<ComponentWrapper> loaderParticipants = new HashSet<ComponentWrapper>();
          //tmp array holds area components and already used (for the area) components
          for (ComponentWrapper component : loaderArea) {
            tmp.add(component);
            loaderParticipants.add(component);
          }
          for (ComponentWrapper component : loaderArea) {
            for (ReferenceImpl reference : component.getReferenceSet()) {
              ComponentWrapper cmp = reference.getReferencedComponent();
              if (!tmp.contains(cmp)) {
                tmp.add(cmp);
                parents.add(cmp.getClassLoader());
              }
            }
            for (String jar : component.getJars()) {
              classPaths.add(jar);
            }
          }
          componentLoader = loadManager.createClassLoader(parents.toArray(new ClassLoader[parents.size()]), classPaths.toArray(new String[classPaths.size()]), loaderName);
          if (location.beDebug()) {
            location.debugT("createClassLoader : " + loaderName);
          }
          for (ComponentWrapper component : loaderArea) {
            component.setClassLoader(componentLoader);
            component.setLoaderParticipant(loaderParticipants);
          }
        }
      } catch (UnknownClassLoaderException e) {
        if (location.beError()) {
          location.traceThrowableT(Severity.ERROR, ResourceUtils.formatString(ResourceUtils.ERROR_REGISTERING_LOADER, new Object[] {"LoadContainer.createClassLoaders"}), e);
        }
      } catch (MalformedURLException e) {
        if (location.beError()) {
          location.traceThrowableT(Severity.ERROR, ResourceUtils.formatString(ResourceUtils.ERROR_REGISTERING_LOADER, new Object[] {"LoadContainer.createClassLoaders"}), e);
        }
      }
      long endMicros = SystemTime.currentTimeMicros();
      loadersRegisterTime += (endMicros - beginMicros);
    }
    //3. fire component loaded event
    for (ComponentWrapper[] componentArray : loaderAreas) {
      for (ComponentWrapper component : componentArray) {
        sendComponentLoadedEvent(component);
      }
    }
    if (location.beDebug()) {
      location.debugT("Registering loaders for " + loadersRegisterTime/1000 + " ms.");
    }
    loadManager.setRuntimeMode();
  }

  private void printLoadAreaInfo(ArrayList<ComponentWrapper[]> loadArea, String header) {
    StringBuilder buffer = new StringBuilder();
    buffer.append(header);
    buffer.append(" : [");
    for (ComponentWrapper[] components : loadArea) {
      buffer.append('{');
      for (int i = 0; i < components.length; i++) {
        ComponentWrapper component = components[i];
        buffer.append(component.toString());
        if (i != components.length - 1) {
          buffer.append(';');
        }
      }
      buffer.append('}');
    }
    buffer.append(']');
    location.debugT(buffer.toString());
  }

  private int getLoadType(byte componentType) {
    switch (componentType) {
      case (ComponentWrapper.TYPE_INTERFACE) : return LoadContext.COMP_TYPE_INTERFACE;
      case (ComponentWrapper.TYPE_LIBRARY) : return LoadContext.COMP_TYPE_LIBRARY;
      case (ComponentWrapper.TYPE_SERVICE) : return LoadContext.COMP_TYPE_SERVICE;
      default : return -1;
    }
  }

  //calculate not created parents loader count
  private int getNotCreatedParentsCount(ComponentWrapper component, Set<ComponentWrapper> sortedNodes) {
    int result = 0;
    for (ReferenceImpl reference : component.getReferenceSet()) {
      ComponentWrapper referenceComponent = reference.getReferencedComponent();
      if (referenceComponent.getStatus() == ComponentMonitor.STATUS_RESOLVED) {
        if (!sortedNodes.contains(referenceComponent)) {//sortedNodes set contains already removed vertex
          result++;
        }
      }
    }
    return result;
  }

  //calculate not created parents loader count for common area case
  private int getNotCreatedParentsCount(ComponentWrapper[] components, Set<ComponentWrapper> sortedNodes) {
    int result = 0;
    //1. init area set:
    Set<ComponentWrapper> commonArea = new HashSet<ComponentWrapper>();
    for (ComponentWrapper component : components) {
      commonArea.add(component);
    }
    //2. calculate
    Set<ComponentWrapper> set = new HashSet<ComponentWrapper>();
    for (ComponentWrapper component : components) {
      for (ReferenceImpl reference : component.getReferenceSet()) {
        ComponentWrapper referenceComponent = reference.getReferencedComponent();
        if (!commonArea.contains(referenceComponent)) {
          //exclude refs in common area
          if (!set.contains(referenceComponent)) {
            //exclude duplicated ref
            set.add(referenceComponent);
            if (referenceComponent.getStatus() == ComponentMonitor.STATUS_RESOLVED) {
              if (!sortedNodes.contains(referenceComponent)) {//sortedNodes set contains already removed vertex
                result++;
              }
            }
          }
        }
      }
    }
    return result;
  }

  //return the loader name for set area
  //common loader names must be sorted (component as well)
  private String getLoaderName(ComponentWrapper[] components) {
    if (components.length == 1) {
      ComponentWrapper component = components[0];
      return component.getType() + ":" + component.getComponentName();
    } else {
      //common area case:
      StringBuilder buffer = new StringBuilder();
      String[] nameArray = new String[components.length];
      for (int i = 0; i < components.length; i++) {
        ComponentWrapper component = components[i];
        nameArray[i] = component.getType() + ":" + component.getComponentName();
      }
      quickSort(0, nameArray.length - 1, nameArray, components);
      buffer.append("common:");
      buffer.append(nameArray[0]);
      for (int i = 1; i < nameArray.length; i++) {
        buffer.append(";");
        buffer.append(nameArray[i]);
      }
      return buffer.toString();
    }
  }

  private void quickSort(int left, int right, String[] array, ComponentWrapper[] cmp) {
    String tmp;
    ComponentWrapper tmpCmp;
    int i;
    int j;
    if (right > left) {
      String s1 = array[right];
      i = left - 1;
      j = right;
      while (true) {
        while (array[++i].compareTo(s1) < 0) {
          //nothing to do
        }
        while (j > 0) {
          if (array[--j].compareTo(s1) <= 0) {
            break;
          }
        }
        if (i >= j) break;
        tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
        tmpCmp = cmp[i];
        cmp[i] = cmp[j];
        cmp[j] = tmpCmp;
      }
      tmp = array[i];
      array[i] = array[right];
      array[right] = tmp;
      tmpCmp = cmp[i];
      cmp[i] = cmp[right];
      cmp[right] = tmpCmp;
      quickSort(left, i - 1, array, cmp);
      quickSort(i + 1, right, array, cmp);
    }
  }

  private void sendComponentLoadedEvent(ComponentWrapper component) {
    //if component classloader creation fails it will be null and the status will remains resolved
    if (component.getClassLoader() != null) {
      component.setStatus(ComponentMonitor.STATUS_LOADED);
      ContainerEvent event = new ContainerEvent();
      event.method = ContainerEventListener.MASK_COMPONENT_LOADED;
      event.isAdmin = true;
      event.name = component.getComponentName();
      event.type = component.getByteType();
      containerEventRegistry.addContainerEvent(event);
    } else {
      ContainerEvent event = new ContainerEvent();
      event.method = ContainerEventListener.MASK_COMPONENT_NOT_LOADED;
      event.isAdmin = true;
      event.name = component.getComponentName();
      event.type = component.getByteType();
      containerEventRegistry.addContainerEvent(event);
    }
  }

}