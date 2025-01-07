package com.sap.engine.core.service630.container;

import java.io.*;
import java.util.*;
import java.math.BigInteger;

import com.sap.engine.core.Names;
import com.sap.engine.frame.container.monitor.ComponentMonitor;
import com.sap.engine.frame.container.monitor.DescriptorContainer;
import com.sap.engine.frame.container.monitor.Reference;
import com.sap.engine.frame.container.event.ContainerEventListener;
import com.sap.engine.boot.loader.ClassLoaderFactory;
import com.sap.engine.lib.io.hash.HashUtils;
import com.sap.engine.core.service630.ResourceUtils;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Implements ComponentMonitor.
 *
 * @see com.sap.engine.frame.container.monitor.ComponentMonitor
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public abstract class ComponentWrapper implements ComponentMonitor {

  //////////////////////////////////////////// STATIC //////////////////////////////////////////////////////////////////

  static final String[] SAP_PROVIDERS = new String[] {"engine.sap.com", "sap.com"};

  public static boolean isSapProvider(String providerName) {
    for (String aSAP_PROVIDERS : SAP_PROVIDERS) {
      if (providerName.equalsIgnoreCase(aSAP_PROVIDERS)) return true;
    }
    return false;
  }

  public static String convertComponentName(String name) {
    if (name.indexOf('/') != -1) {
      for (String aSAP_PROVIDERS : SAP_PROVIDERS) {
        if (name.startsWith(aSAP_PROVIDERS + '/')) {
          name = name.substring(aSAP_PROVIDERS.length() + 1);
          break;
        }
      }
      name = name.replace('/', '~');
    }
    return name;
  }

  public static String getRuntimeName(String componentName, String providerName) {
    String result;
    for (String aSAP_PROVIDERS : SAP_PROVIDERS) {
      if (componentName.startsWith(aSAP_PROVIDERS + '/')) {
        componentName = componentName.substring(aSAP_PROVIDERS.length() + 1);
        break;
      }
    }
    componentName = componentName.replace('/', '~');
    result = componentName;
    if (providerName == null || providerName.equals("")) {
      providerName = SAP_PROVIDERS[0];
    }
    if (!isSapProvider(providerName)) {
      providerName = providerName.replace('/', '~');
      result = providerName + '~' + result;
    }
    return result;
  }

  public static String getRuntimeName(String componentName, String providerName, byte type) {
    if (type == ComponentWrapper.TYPE_INTERFACE) {
      componentName = InterfaceWrapper.transformINameApiToIName(componentName);
    }
    return getRuntimeName(componentName, providerName);
  }

  //type constants from Reference interface
  static final byte TYPE_INTERFACE = ContainerEventListener.INTERFACE_TYPE;
  static final byte TYPE_LIBRARY = ContainerEventListener.LIBRARY_TYPE;
  static final byte TYPE_SERVICE = ContainerEventListener.SERVICE_TYPE;

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  protected MemoryContainer memoryContainer;

  protected byte currentStatus;

  protected String componentName;
  protected String providerName;
  protected String displayName;
  protected String groupName;
  protected String description;
  protected String majorVersion;
  protected String minorVersion;
  protected String microVersion;
  protected String csnComponent;
  protected String dcName;

  protected ClassLoader loader;
  protected String[] jars;

  protected ArrayList<ReferenceImpl> referenceSet;
  protected Set<ReferenceImpl> reverseReferenceSet;

  protected int nodeId;

  private String componentBinDir;
  private DescriptorContainer descriptorContainer = null;

  //Contains a single or a set of compoenent wrappers in case of common classloaders
  private Set<ComponentWrapper> loaderParticipant;

  //hash for service management
  private String hash = null;

  private boolean isDisabled = false;

  private static final Location location = Location.getLocation(ComponentWrapper.class.getName(), Names.KERNEL_DC_NAME, Names.SERVICE_MANAGER_CSN_COMPONENT);

  ComponentWrapper(MemoryContainer memoryContainer, Properties componentProperties) {
    this.memoryContainer = memoryContainer;
    this.referenceSet = new ArrayList<ReferenceImpl>();
    this.reverseReferenceSet = new HashSet<ReferenceImpl>();
    init(componentProperties);
    this.currentStatus = ComponentMonitor.STATUS_DEPLOYED;
  }

  //used for core_lib
  ComponentWrapper(MemoryContainer memoryContainer, String []jars) {
    this.memoryContainer = memoryContainer;
    this.referenceSet = new ArrayList<ReferenceImpl>();
    this.reverseReferenceSet = new HashSet<ReferenceImpl>();
    loader = getClass().getClassLoader().getParent();
    displayName = ClassLoaderFactory.CORE_LIBRARY_NAME;
    componentName = ClassLoaderFactory.CORE_LIBRARY_NAME;
    majorVersion = "7";
    minorVersion = "10";
    microVersion = "1";
    providerName = SAP_PROVIDERS[0];
    this.jars = jars;
    this.currentStatus = ComponentMonitor.STATUS_DEPLOYED;
  }

  ///////////////////////////////////////////////// INITIALIZE /////////////////////////////////////////////////////////

  private void init(Properties props) {
    displayName = props.getProperty("display-name");
    componentName = props.getProperty("component-name");
    description = props.getProperty("description");
    majorVersion = props.getProperty("major-version", "7");
    minorVersion = props.getProperty("minor-version", "10");
    microVersion = props.getProperty("micro-version", "1");
    providerName = props.getProperty("provider-name");
    groupName = props.getProperty("group-name");
    csnComponent = props.getProperty("csn-component");
    dcName = props.getProperty("dc-name");
    //init runtime name
    boolean isNameChanged = false;
    if (getByteType() == ComponentWrapper.TYPE_INTERFACE) {
      String tmp = InterfaceWrapper.transformINameApiToIName(componentName);
      isNameChanged = !tmp.equals(componentName);
      componentName = tmp;
    }
    componentName = getRuntimeName(componentName, providerName);
    //and bin dir
    componentBinDir = initComponentBinDir(isNameChanged);
    initReferences(props);
    initJars(props);
    parseProperties(props);
  }

  protected void parseProperties(Properties props) {
  }

  private void initReferences(Properties props) {
    String property = props.getProperty("references");
    if (property != null) {
      int count = Integer.parseInt(props.getProperty("references"));
      for (int i = 0; i < count; i++) {
        String name = props.getProperty("reference_name_" + i);
        byte referentType;
        byte type;
        String providerName;
        String tmp = props.getProperty("reference_type_" + i);
        if (tmp.equals("interface")) {
          referentType = Reference.REFER_INTERFACE;
        } else if (tmp.equals("library")) {
          referentType = Reference.REFER_LIBRARY;
        } else {
          referentType = Reference.REFER_SERVICE;
        }
        tmp = props.getProperty("reference_strength_" + i);
        if (tmp.equals("notify")) {
          //use weak instead of notify
          type = Reference.TYPE_SOFT;
        } else if (tmp.equals("weak")) {
          type = Reference.TYPE_SOFT;
        } else {
          type = Reference.TYPE_HARD;
        }
        providerName = props.getProperty("reference_provider-name_" + i);
        ReferenceImpl ref = new ReferenceImpl(memoryContainer, name, providerName, referentType, type);
        if (componentName.equals(ref.getName()) && getByteType() == referentType) {
          //avoid reference to itself
          if (location.beWarning()) {
            location.warningT(ResourceUtils.formatString(ResourceUtils.FORBIDDEN_REFERENCE, new Object[] {toString(), ref.toString()}));
          }
        } else if (!referenceSet.contains(ref)) {
          //avoid repeated references.
          referenceSet.add(ref);
        }
      }
    }
  }

  private void initJars(Properties props) {
    //todo - remove default value 0, when the xml validation is turned on
    int count = Integer.parseInt(props.getProperty("jars", "0"));
    jars = new String[count];
    for (int i = 0; i < count; i++) {
      jars[i] = props.getProperty("jar-name_" + i);
      jars[i] = jars[i].replace('/', File.separatorChar);
      if (jars[i].charAt(0) == File.separatorChar) {
        jars[i] = componentBinDir + jars[i];
      } else {
        jars[i] = componentBinDir + File.separatorChar + jars[i];
      }
    }
  }

  //////////////////////////////////// COMPONENT MONITOR ///////////////////////////////////////////////////////////////

  public String getComponentName() {
    return componentName;
  }

  public String getProviderName() {
    return providerName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public String getMajorVersion() {
    return majorVersion;
  }

  public String getMinorVersion() {
    return minorVersion;
  }

  public String getMicroVersion() {
    return microVersion;
  }

  public String getGroupName() {
    return groupName;
  }

  public Reference[] getReferences() {
    return referenceSet.toArray(new Reference[referenceSet.size()]);
  }

  public String[] getJars() {
    return jars;
  }

  public byte getStatus() {
    return currentStatus;
  }

  public synchronized DescriptorContainer getDescriptorContainer() {
    if (descriptorContainer == null) {
      descriptorContainer = new DescriptorHelperImpl(componentName, getByteType(), memoryContainer.getServiceContainer());
    }
    return descriptorContainer;
  }

  public InputStream getSAPManifest() {
    DescriptorContainer descriptor = getDescriptorContainer();
    return descriptor.getPersistentEntryStream("SAP_MANIFEST.MF", true);
  }

  public String toString() {
    return getType() + ":" + providerName + "/" + componentName;
  }

  /////////////////////////////////// INTERNAL /////////////////////////////////////////////////////////////////////////

  String getCSNComponent() {
    return csnComponent;
  }

  String getDcName() {
    return dcName;
  }
  
  void setStatus(byte status) {
    this.currentStatus = status;
  }

  void setClassLoader(ClassLoader loader) {
    this.loader = loader;
  }

  ClassLoader getClassLoader() {
    return loader;
  }

  void setLoaderParticipant(Set<ComponentWrapper> loaderParticipant) {
    this.loaderParticipant = loaderParticipant;
  }

  Set<ComponentWrapper> getLoaderParticipant() {
    return loaderParticipant;
  }

  void setNodeId(int id) {
    nodeId = id;
  }

  int getNodeId() {
    return nodeId;
  }

  ArrayList<ReferenceImpl> getReferenceSet() {
    return referenceSet;
  }

  Set<ReferenceImpl> getReverseReferenceSet() {
    return reverseReferenceSet;
  }

  void setDisabled() {
    isDisabled = true;
  }

  boolean isDisabled() {
    return isDisabled;
  }

  /////////////////////////////////// ABSTRACTS ////////////////////////////////////////////////////////////////////////

  abstract String getType();

  abstract byte getByteType();

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  synchronized String getHash() {
    if (hash == null) {
      hash = calculateHashes();
    }
    return hash;
  }

  private String calculateHashes() {
    File componentDir = new File(componentBinDir);
    String result = "0";
    if (componentDir.exists()) {
      try {
        byte[] md5byte = HashUtils.generateDirHash(componentDir);
        result = new BigInteger(md5byte).toString(16);
      } catch (IOException e) {
        location.traceThrowableT(Severity.ERROR, ResourceUtils.formatString(ResourceUtils.ERROR_CALCULATING_HASH, new Object[] {componentName}), e);
      }
    }
    return result;
  }

  //initialize component binary root
  private String initComponentBinDir(boolean isNameChanged) {
    switch (getByteType()) {
      case TYPE_INTERFACE : return ServiceContainerImpl.INTERFACE_BIN_DIR + File.separatorChar + ((isNameChanged) ?
              InterfaceWrapper.transformINameToINameApi(componentName) : componentName);
      case TYPE_LIBRARY : return ServiceContainerImpl.LIBRARY_BIN_DIR + File.separatorChar + componentName;
      case TYPE_SERVICE : return ServiceContainerImpl.SERVICE_BIN_DIR + File.separatorChar + componentName;
      default : return null;
    }
  }

}