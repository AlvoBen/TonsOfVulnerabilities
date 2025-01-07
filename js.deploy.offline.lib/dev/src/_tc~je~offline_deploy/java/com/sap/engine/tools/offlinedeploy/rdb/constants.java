package com.sap.engine.tools.offlinedeploy.rdb;

/**
 * Interface containing all configuration path and file name constants.
 *
 * @author Dimitar Kostadinov
 * @version 710
 */
public interface Constants {
  //constants for logging
  public static final String KERNEL_DC_NAME = "kernel.sda";
  public static final String KERNEL_CSN_COMPONENT = "BC-JAS-COR";
  //configuration path constants
  public final static String CLUSTER_CONFIG = "cluster_config";
  public final static String GLOBALS = "globals";
  public final static String BIN = "bin";
  public final static String BOOTSTRAP = "bootstrap";
  public final static String NATIVE = "native";
  public final static String RUNTIME = "runtime";
  public final static String CLUSTERNODE_CONFIG = "clusternode_config";
  public final static String WORKERNODE = "workernode";
  public final static String APPS = "apps";
  public final static String UNDEPLOY = "undeploy";
  public final static String TEMPLATES = "templates";
  public final static String BASE = "base";
  public final static String STANDARD_INSTANCE = "standard_instance";
  public final static String CFG = "cfg";
  public final static String DEFAULT_TEMPLATES = "default";
  public final static String SYSTEM = "system";
  public final static String CUSTOM_GLOBAL = "custom_global";
  public final static String CUSTOM_TEMPLATES = "custom_templates";
  public final static String INSTANCES = "instances";
  public final static String CURRENT_INSTANCE = "current_instance";
  public final static String JVM_PARAMS_SUBCONFIGURATION = "jvm_params";
  public static final String SRC_ZIP = "src.zip";
  //configuration links constants
  public final static String CUSTOM_GLOBAL_LINK = CLUSTER_CONFIG + "/" + TEMPLATES + "/" + BASE + "/" + STANDARD_INSTANCE;
  public final static String CURRENT_INSTANCE_LINK = CLUSTER_CONFIG + "/" + SYSTEM + "/" + INSTANCES + "/ID${INSTANCE_ID}";
  //number of nodes & instance type for standard_instance template
  public final static String NUMBER_OF_NODES = "number_of_nodes";
  public final static String DEFAULT_NUMBER_OF_NODES_VALUE = "${CPU_COUNT}";
  public final static String INSTANCE_TYPE = "instance_type";
  public final static String DEFAULT_INSTANCE_TYPE_VALUE = "j2ee";
  //numbers of bin and native updates
  public static final String BIN_VERSION = "binVersion";
  public static final String OS_LIB_VERSION = "os_libVersion";
  //common data files
  public final static String SCREPOSITORY_FILE = "components.properties";
  public static final String NATIVE_DESCRIPTOR = "native.descriptor";
  public static final String UNDEPLOY_MAPPING = "mapping.txt";
  public static final String TEMPLATE_MAPPING = "templates.txt";
  //zip entries names constants
  public static final String MANIFEST = "META-INF/MANIFEST.MF";
  public static final String SAP_MANIFEST = "META-INF/SAP_MANIFEST.MF";
  public static final String SECURED_FILES = "META-INF/securedfiles.lst";
  public static final String SEARCH_RULES_XML = "META-INF/SearchRules.xml";
  public static final String TEMPLATE_ENTRY = "template.xml";
  //engine-kernel zip entries names -> corresponding to the name after last '/' (server/bin/boot --> boot) in the configuration
  public final static String[] KERNEL_BINARIES = new String[] {"server/bin/boot", "server/bin/system", "server/bin/kernel", "server/bin/core_lib"};
  public final static String[] KERNEL_CFG = new String[] {"server/cfg/kernel", "server/dtd"};
  //component types strings
  public final static String INTERFACE_BASE = "interfaces";
  public final static String LIBRARY_BASE = "ext";
  public final static String SERVICE_BASE = "services";
  //constants regarding component deployment
  public final static String SERVER = "server";
  public final static String PROVIDER = "provider";
  public final static String PROVIDER_XML = "provider.xml";
  public final static String PROPERTIES = "properties";
  public final static String PROPERTIES_XML = "properties.xml";
  public final static String PERSISTENT_BASE = "persistent";
  public final static String DESCRIPTORS_BASE = "descriptors";
  //constants for special kernel files
  public final static String LOG_CONFIGURATION_XML = "log-configuration.xml";
  public final static String CACHE_CONFIGURATION_XML = "cache-configuration.xml";
  public final static String JITEXCLUDE = ".hotspot_compiler";
  //forbidden chars string
  public final static String FORBIDDEN_NAME_SYMBOLS = "(%#?*><\"{}|\\^[]`;:@=&+,)$";
  public final static String ILLEGAL_CHARS = "/\\:*?\"<>|;,=%[]#&";
  //native path prefix
  public static final String OS_LIBS = "OS_libs/";
  //double name interfaces support list @see com.sap.engine.frame.container.monitor.InterfaceMonitor
  public static final String[] INTERFACE_NAMES_API = new String[] {"appcontext_api", "container_api", "cross_api", "csiv2_api", "ejbcomponent_api", "ejblocking_api",
          "ejbmonitor_api", "ejbormapping_api", "ejbserialization_api", "log_api", "naming_api", "security_api", "shell_api", "transactionext_api", "visual_administration_api", "webservices_api"};

}