package com.sap.engine.core.service630.container;

import java.io.*;
import java.net.*;
import java.util.*;

import com.sap.engine.boot.loader.BytecodeModifier;
import com.sap.engine.core.Framework;
import com.sap.engine.core.Names;
import com.sap.engine.core.classload.ClassLoaderManager;
import com.sap.engine.frame.core.load.*;
import com.sap.engine.frame.core.load.gui.ComponentInfo;
import com.sap.engine.lib.util.HashMapObjectInt;

/**
 * Wrap LoadContext to convert loader name in case of common loader and check for some additional runtime restrictions
 *
 * @author Dimitar Kostadinov, Nikolai Dimitrov, Stefan Dimov, Hristo Iliev
 * @version 710
 * @see com.sap.engine.frame.core.load.LoadContext
 */
public class LoadContextWrapper implements LoadContext {

  //interface loader name prefix
  private final static String COMPONENT_INTERFACE = "interface:";
  //library loader name prefix
  private final static String COMPONENT_LIBRARY = "library:";
  //service loader name prefix
  private final static String COMPONENT_SERVICE = "service:";

  private MemoryContainer container;
  private ClassLoaderManager classLoaderManager;

  LoadContextWrapper(MemoryContainer container) {
    this.container = container;
    this.classLoaderManager = (ClassLoaderManager) Framework.getManager(Names.CLASSLOADER_MANAGER);
  }

  public ClassLoader createLocalClassLoader(ClassLoader[] parents, String[] paths, String name, BytecodeModifier modifier) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createLocalClassLoader(parents, paths, name, modifier);
  }

  public ClassLoader createLocalClassLoader(ClassLoader[] parents, URL[] paths, String name, BytecodeModifier modifier) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createLocalClassLoader(parents, paths, name, modifier);
  }

  public ClassLoader createClassLoader(ClassLoader[] parents, String[] paths, String name) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createClassLoader(parents, paths, name);
  }

  public ClassLoader createClassLoader(ClassLoader[] parents, URL[] paths, String name) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createClassLoader(parents, paths, name);
  }

  public ClassLoader createLocalClassLoader(ClassLoader[] parents, String[] paths, String name, String componentName, int componentType, BytecodeModifier modifier) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createLocalClassLoader(parents, paths, name, componentName, componentType, modifier);
  }

  public ClassLoader createLocalClassLoader(ClassLoader[] parents, URL[] paths, String name, String componentName, int componentType, BytecodeModifier modifier) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createLocalClassLoader(parents, paths, name, componentName, componentType, modifier);
  }

  public ClassLoader createClassLoader(ClassLoader[] parents, String[] paths, String name, String componentName, int componentType) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createClassLoader(parents, paths, name, componentName, componentType);
  }

  public ClassLoader createClassLoader(ClassLoader[] parents, URL[] paths, String name, String componentName, int componentType) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createClassLoader(parents, paths, name, componentName, componentType);
  }

  public ClassLoader createLocalClassLoader(ClassLoader[] parents, String[] paths, String name, String csnComponentName, String componentName, int componentType, BytecodeModifier modifier) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createLocalClassLoader(parents, paths, name, csnComponentName, componentName, componentType, modifier);
  }

  public ClassLoader createLocalClassLoader(ClassLoader[] parents, URL[] paths, String name, String csnComponentName, String componentName, int componentType, BytecodeModifier modifier) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createLocalClassLoader(parents, paths, name, csnComponentName, componentName, componentType, modifier);
  }

  public ClassLoader createClassLoader(ClassLoader[] parents, String[] paths, String name, String csnComponentName, String componentName, int componentType) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createClassLoader(parents, paths, name, csnComponentName, componentName, componentType);
  }

  public ClassLoader createClassLoader(ClassLoader[] parents, URL[] paths, String name, String csnComponentName, String componentName, int componentType) throws UnknownClassLoaderException, MalformedURLException {
    return classLoaderManager.createClassLoader(parents, paths, name, csnComponentName, componentName, componentType);
  }

  public String getName(ClassLoader loader) {
    return classLoaderManager.getName(loader);
  }

  public ClassLoader[] getClassLoaderParents(ClassLoader loader) {
    return classLoaderManager.getClassLoaderParents(loader);
  }

  public URL[] getClassLoaderURLs(ClassLoader loader) throws MalformedURLException {
    return classLoaderManager.getClassLoaderURLs(loader);
  }

  public String[] getReferences(ClassLoader loader) {
    return classLoaderManager.getReferences(loader);
  }

  public String[] getTransitiveReferences(ClassLoader loader) {
    return classLoaderManager.getTransitiveReferences(loader);
  }

  public String[] getTransitiveParents(ClassLoader loader) {
    return classLoaderManager.getTransitiveParents(loader);
  }

  public String[] getReferencesFrom(ClassLoader loader) {
    return classLoaderManager.getReferencesFrom(loader);
  }

  public String[] getResourceNames(ClassLoader loader) {
    return classLoaderManager.getResourceNames(loader);
  }

  public boolean register(ReferencedLoader loader) throws Exception {
    return classLoaderManager.register(loader);
  }

  public boolean register(ReferencedLoader loader, String componentName, int componentType) throws Exception {
    return classLoaderManager.register(loader, componentName, componentType);
  }

  public boolean unregister(ClassLoader loader) throws Exception {
    return classLoaderManager.unregister(loader);
  }

  public boolean registerReference(String from, String to) {
    //modify to name, from must be application
    return classLoaderManager.registerReference(from, modifyName(to));
  }

  public boolean unregisterReference(String from, String to) {
    //modify to name, from must be application
    return classLoaderManager.unregisterReference(from, modifyName(to));
  }

  public ClassLoader getClassLoader(String name) {
    name = modifyName(name);
    return classLoaderManager.getClassLoader(name);
  }

  public String[] getReferences(String name) {
    name = modifyName(name);
    return classLoaderManager.getReferences(name);
  }

  public String[] getParents(String name) {
    name = modifyName(name);
    return classLoaderManager.getParents(name);
  }

  public String[] getChildren(String name) {
    name = modifyName(name);
    return classLoaderManager.getChildren(name);
  }

  public String[] getTransitiveReferences(String name) {
    name = modifyName(name);
    return classLoaderManager.getTransitiveReferences(name);
  }

  public String[] getTransitiveParents(String loaderName) {
    loaderName = modifyName(loaderName);
    return classLoaderManager.getTransitiveParents(loaderName);
  }

  public String[] getReferencesFrom(String name) {
    name = modifyName(name);
    return classLoaderManager.getReferencesFrom(name);
  }

  public Enumeration listLoaders() {
    return classLoaderManager.listLoaders();
  }

  public HashMapObjectInt getLoadersCompTypesMapping() {
    return classLoaderManager.getLoadersCompTypesMapping();
  }

  public String[] getResourceNames(String loaderName) {
    loaderName = modifyName(loaderName);
    return classLoaderManager.getResourceNames(loaderName);
  }

  /**
   * Finds a resource by provided extension
   * <p/>
   * The method will iterate the resources of the class loaders and their transitive parents. Information about all
   * found files with the requested extension is returned.
   * <p/>
   * The method will return resources that are either directly found in the passed classloaders or found in the
   * transitive parents of the loaders.
   * <p/>
   * The result of the execution heaviliy depends on the classloading hierarchy and the classloaders passed to the
   * method. For example the order in which the resources are returned is determined by:
   * <ol>
   * <li>the order of the classloaders passed to the method</li>
   * <li>the references between the loaders</li>
   * </ol>
   * <p/>
   * This method may use a lot of file operations to find the needed resources. Therefore the method should not be
   * invoked often (for instance on every request to an application).
   *
   * @param classloaderNames Names of the class loaders that has to be iterated. Only registered loaders will be
   *                         iterated. The method will not throw an exception if the loader name is not registered
   * @param extension        Extension to search. No wildcards and separators should be used. Examples: "tld", "jpg".
   *                         Reduces the number of resources that will be searched.
   * @return <code>List</code> with <code>ClassLoaderResource</code> objects
   *         <p/>Returns empty <code>List</code> if no resources are found.
   * @throws NullPointerException Thrown if any of the parameters are null
   * @throws IOException          Thrown if there are problems during the scan of the resources
   */
  public List<ClassLoaderResource> getResources(List<String> classloaderNames, String extension) throws IOException {
    return classLoaderManager.getResources(classloaderNames, extension);
  }

  public String getLeakedClassLoaders(Properties properties) {
    return classLoaderManager.getLeakedClassLoaders(properties);
  }

  public ComponentInfo getLoaderWrapper(String loaderName) {
    loaderName = modifyName(loaderName);
    return classLoaderManager.getLoaderWrapper(loaderName);
  }

  public ComponentInfo[] getLoaderWrappers() {
    return classLoaderManager.getLoaderWrappers();
  }

  public ClassInfo getClassInfo(Class cls) {
    return classLoaderManager.getClassInfo(cls);
  }

  public ClassInfo getLoaderComponentInfo(ClassLoader loader) {
    return classLoaderManager.getLoaderComponentInfo(loader);
  }

  public ClassInfo getLoaderComponentInfo(String loaderName) {
    return classLoaderManager.getLoaderComponentInfo(loaderName);
  }

  public String getLoaderDetailedInfo(ClassLoader loader) {
    return classLoaderManager.getLoaderDetailedInfo(loader);
  }

  private String modifyName(String name) {
    String loaderName = name;
    ComponentWrapper component;
    String tmp;
    if (name.startsWith(COMPONENT_INTERFACE)) {
      tmp = name.substring(COMPONENT_INTERFACE.length());
      tmp = InterfaceWrapper.transformINameApiToIName(ComponentWrapper.convertComponentName(tmp));
      component = container.getInterfaces().get(tmp);
      if (component != null && component.getClassLoader() != null) {
        loaderName = classLoaderManager.getName(component.getClassLoader());
      }
    } else if (name.startsWith(COMPONENT_LIBRARY)) {
      tmp = name.substring(COMPONENT_LIBRARY.length());
      tmp = ComponentWrapper.convertComponentName(tmp);
      component = container.getLibraries().get(tmp);
      if (component != null && component.getClassLoader() != null) {
        loaderName = classLoaderManager.getName(component.getClassLoader());
      }
    } else if (name.startsWith(COMPONENT_SERVICE)) {
      tmp = name.substring(COMPONENT_SERVICE.length());
      tmp = ComponentWrapper.convertComponentName(tmp);
      component = container.getServices().get(tmp);
      if (component != null && component.getClassLoader() != null) {
        loaderName = classLoaderManager.getName(component.getClassLoader());
      }
    }
    return loaderName;
  }

}