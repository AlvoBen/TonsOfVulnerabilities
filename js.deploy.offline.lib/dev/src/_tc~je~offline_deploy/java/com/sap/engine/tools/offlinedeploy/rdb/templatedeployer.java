package com.sap.engine.tools.offlinedeploy.rdb;

import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.frame.core.configuration.addons.PropertyEntry;
import com.sap.engine.frame.core.configuration.addons.PropertySheet;
import com.sap.engine.lib.config.api.CommonClusterFactory;
import com.sap.engine.lib.config.api.ConfigurationLevel;
import com.sap.engine.lib.config.api.component.ApplicationHandler;
import com.sap.engine.lib.config.api.component.ComponentHandler;
import com.sap.engine.lib.config.api.component.ComponentProperties;
import com.sap.engine.lib.config.api.component.ManagerHandler;
import com.sap.engine.lib.config.api.exceptions.ClusterConfigurationException;
import com.sap.engine.lib.config.api.filters.ComponentFilter;
import com.sap.engine.lib.config.api.filters.FilterHandler;
import com.sap.engine.lib.config.api.jvm.ExtendedParameters;
import com.sap.engine.lib.config.api.jvm.JVMParameter;
import com.sap.engine.lib.config.api.jvm.JVMParametersHandler;
import com.sap.engine.lib.config.api.offline.OfflineClusterConfiguration;
import com.sap.engine.lib.config.api.shm.ShmConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This class is used to deploy components with software type j2ee-template.
 *
 * @version 710
 * @author Dimitar Kostadinov
 */
public class TemplateDeployer {

  /**
   * Undeploy template form default templates.
   *
   * @param handler - current configuration handler.
   * @param templateName - name of the template.
   * @throws ClusterConfigurationException if any errors occur.
   */
  static void removeTemplate(ConfigurationHandler handler, String templateName) throws ClusterConfigurationException {
    CommonClusterFactory clusterFactory = OfflineClusterConfiguration.getClusterFactory(handler);
    clusterFactory.deleteLevelEntry(CommonClusterFactory.LEVEL_DEFAULT_TEMPLATES, templateName);
  }

  /**
   * Deploys template SDA.
   *
   * @param handler - current configuration handler.
   * @param zipFile - template SDA.
   * @param parser - parse helper instance.
   * @return undeploy mapping info String[] {keyvendor$keyname, templatename}.
   * @throws IOException - if any I/O errors occur.
   * @throws SAXException - if template xml is not well formed.
   * @throws DeploymentException - if template definition is not valid.
   * @throws ClusterConfigurationException - if any config lib error occur.
   * @throws ConfigurationException - if any configuration error occur.
   */
  static String[] deployTemplate(ConfigurationHandler handler, ZipFile zipFile, DOMParseHelper parser) throws IOException, SAXException, DeploymentException, ClusterConfigurationException, ConfigurationException {
    ZipEntry template = zipFile.getEntry(Constants.TEMPLATE_ENTRY);
    if (template == null) {
      throw new DeploymentException("Template SDA [" + zipFile.getName() + "] does not contains template.xml entry");
    }
    Document document = parser.parse(template, zipFile);
    document.normalizeDocument();
    String templateName = deployTemplate(handler, document.getDocumentElement());
    String[] keyVendorKeyNamePair = Utils.getKeyVendorKeyName(zipFile);
    String[] result = new String[2];
    result[0] = keyVendorKeyNamePair[0] + '$' + keyVendorKeyNamePair[1];
    result[1] = templateName;
    return result;
  }

  /**
   * Creates template in DB using configuration library.
   *
   * <!ELEMENT template (template-description?, filters?, jvm-parameters?, configuration?)>
   * <!ELEMENT template-description (#PCDATA)>
   * <!ATTLIST template
	 *   name CDATA #REQUIRED
	 *   number_of_nodes CDATA #IMPLIED
	 *   instance_type (j2ee|jms) #IMPLIED
   * >
   *
   * @param handler - current configuration handler.
   * @param templateElement - representing the template.
   * @return name of the template for undeploy mapping.
   * @throws DeploymentException - if template definition is not valid.
   * @throws ClusterConfigurationException - if any config lib error occur.
   * @throws ConfigurationException - if any configuration error occur.
   */
  private static String deployTemplate(ConfigurationHandler handler, Element templateElement) throws DeploymentException, ClusterConfigurationException, ConfigurationException {
    CommonClusterFactory clusterFactory = OfflineClusterConfiguration.getClusterFactory(handler);
    String name;
    if (templateElement.hasAttribute("name")) {
      name = templateElement.getAttribute("name").trim();
    } else {
      throw new DeploymentException("Required template name attribute is missing");
    }
    //remove old template if exist
    clusterFactory.deleteLevelEntry(CommonClusterFactory.LEVEL_DEFAULT_TEMPLATES, name);
    //get template description
    String template_description = null;
    NodeList list = templateElement.getElementsByTagName("template-description");
    if (list.getLength() > 0) {
      template_description = Utils.getTextValue((Element) list.item(0));
    }
    //create template
    ConfigurationLevel template = clusterFactory.createLevelEntry(CommonClusterFactory.LEVEL_DEFAULT_TEMPLATES, name, null, template_description);
    //get template attributes
    if (templateElement.hasAttribute("number_of_nodes")) {
      String number_of_nodes = templateElement.getAttribute("number_of_nodes").trim();
      template.setNumberOfNodes(number_of_nodes);
    }
    if (templateElement.hasAttribute("instance_type")) {
      String instance_type = templateElement.getAttribute("instance_type").trim();
      if (!instance_type.equals("j2ee") && !instance_type.equals("jms")) {
        throw new DeploymentException("Template instance_type attribute [" + instance_type + "] is not valid");
      }
      template.setInstanceType(instance_type);
    }
    //iterate elements
    list = templateElement.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element) list.item(i);
        String tagName = child.getTagName();
        if (tagName.equals("filters")) {
          processFiltersTag(child, template.getFilters(false));
        } else if (tagName.equals("jvm-parameters")) {
          processJVMParamsTag(child, template);
        } else if (tagName.equals("configuration")) {
          processConfiguration(child, template);
        }
      }
    }
    return name;
  }

  /**
   * <!ELEMENT filters (filter+)>
   * <!ELEMENT filter EMPTY>
   * <!ATTLIST filter
   *   filter_action (start|stop|disable) #REQUIRED
   *   component_name CDATA #REQUIRED
   *   component_provider CDATA #REQUIRED
   *   component_type CDATA #REQUIRED
   * >
   */
  private static void processFiltersTag(Element filters, FilterHandler filterHandler) throws DeploymentException, ClusterConfigurationException {
    ArrayList<ComponentFilter> rules = new ArrayList<ComponentFilter>();
    NodeList list = filters.getElementsByTagName("filter");
    for (int i = 0; i < list.getLength(); i++) {
      Element filter = (Element) list.item(i);
      String filter_action = filter.getAttribute("filter_action").trim();
      int action;
      if (filter_action.equals("start")) {
        action = ComponentFilter.ACTION_START;
      } else if (filter_action.equals("stop")) {
        action = ComponentFilter.ACTION_STOP;
      } else if (filter_action.equals("disable")) {
        action = ComponentFilter.ACTION_DISABLE;
      } else {
        throw new DeploymentException("Filter filter_action attribute [" + filter_action + "] is not valid");
      }
      String component_name = filter.getAttribute("component_name").trim();
      String component_provider = filter.getAttribute("component_provider").trim();
      String component_type = filter.getAttribute("component_type").trim();
      int type;
      if (component_type.equals("library")) {
        type = ComponentFilter.COMPONENT_LIBRARY;
      } else if (component_type.equals("service")) {
        type = ComponentFilter.COMPONENT_SERVICE;
      } else if (component_type.equals("application")) {
        type = ComponentFilter.COMPONENT_APPLICATION;
      } else if (component_type.equals("*")) {
        type = ComponentFilter.COMPONENT_ALL;
      } else {
        throw new DeploymentException("Filter component_type attribute [" + component_type + "] is not valid");
      }
      ComponentFilter componentFilter = filterHandler.createRule(action, type, component_provider, component_name);
      rules.add(componentFilter);
    }
    if (rules.size() > 0) {
      filterHandler.setFilter(rules);
    }
  }

  /**
   * <!ELEMENT jvm-parameters (vm-vendor*, parameter*)>
   * <!ELEMENT vm-vendor (platform*, parameter*)>
   * <!ATTLIST vm-vendor name CDATA #REQUIRED>
   * <!ELEMENT platform (parameter+)>
   * <!ATTLIST platform name CDATA #REQUIRED>
   */
  private static void processJVMParamsTag(Element jvmParams, ConfigurationLevel template) throws ClusterConfigurationException, DeploymentException {
    NodeList list = jvmParams.getChildNodes();
    JVMParametersHandler root = null;
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element) list.item(i);
        if (child.getTagName().equals("vm-vendor")) {
          String vendorName = child.getAttribute("name").trim();
          NodeList vendorList = child.getChildNodes();
          JVMParametersHandler vendorRoot = null;
          for (int j = 0; j < vendorList.getLength(); j++) {
            if (vendorList.item(j).getNodeType() == Node.ELEMENT_NODE) {
              Element vendorChild = (Element) vendorList.item(j);
              if (vendorChild.getTagName().equals("platform")) {
                String platformName = vendorChild.getAttribute("name").trim();
                NodeList platformList = vendorChild.getElementsByTagName("parameter");
                if (platformList.getLength() > 0) {
                  JVMParametersHandler platformRoot = template.getJVMParameters(vendorName, platformName, false);
                  for (int k = 0; k < platformList.getLength(); k++) {
                    Element platformParam = (Element) platformList.item(k);
                    setJVMParameter(platformParam, platformRoot);
                  }
                }
              } else if (vendorChild.getTagName().equals("parameter")) {
                if (vendorRoot == null) vendorRoot = template.getJVMParameters(vendorName, null, false);
                setJVMParameter(vendorChild, vendorRoot);
              }
            }
          }
        } else if (child.getTagName().equals("parameter")) {
          if (root == null) root = template.getJVMParameters(null, null, false);
          setJVMParameter(child, root);
        }
      }
    }
  }

  /**
   * <!ELEMENT parameter (value?, description?)>
   * <!ATTLIST parameter
   *   name CDATA #REQUIRED
   *   type (system|memory|additional) #REQUIRED
   *   disabled (yes|no) #IMPLIED
   * >
   * <!ELEMENT value (#PCDATA)>
   * <!ELEMENT description (#PCDATA)>
   */
  private static void setJVMParameter(Element parameter, JVMParametersHandler handler) throws ClusterConfigurationException, DeploymentException {
    if (!parameter.hasAttribute("name") || !parameter.hasAttribute("type")) {
      throw new DeploymentException("Required JVM parameter attribute is missing");
    }
    String name = parameter.getAttribute("name").trim();
    String type = parameter.getAttribute("type").trim();
    String disabled = null;
    if (parameter.hasAttribute("disabled")) {
      disabled = parameter.getAttribute("disabled").trim();
    }
    String value = null;
    String description = null;
    NodeList list = parameter.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element child = (Element) list.item(i);
        if (child.getTagName().equals("value")) {
          value = Utils.getTextValue(child);
        } else if (child.getTagName().equals("description")) {
          description = Utils.getTextValue(child);
        }
      }
    }
    if (!type.equals("additional") && value == null) {
      throw new DeploymentException("Missing [" + name + "] JVM parameter value");
    }
    if (value == null) value = "";
    JVMParameter param;
    if (type.equals("memory")) {
      param = handler.getMemoryParametersHandler().getParameter(name);
      param.setValue(value);
    } else {
      ExtendedParameters extendedParameters;
      if (type.equals("system")) {
        extendedParameters = handler.getSystemParametersHandler();
      } else {//additional
        extendedParameters = handler.getAdditionalParametersHandler();
      }
      try {
        param = extendedParameters.getParameter(name);
        param.setValue(value);
      } catch (com.sap.engine.lib.config.api.exceptions.NameNotFoundException e) {
        extendedParameters.addParameter(name, value, description);
        param = extendedParameters.getParameter(name);
      }
    }
    if (description != null) param.setDescription(description);
    if (disabled != null) {
      if (disabled.equals("yes")) {
        param.disable();
      } else {
        param.enable();
      }
    }
  }

  /**
   * <!ELEMENT configuration (manager*, service*, application*, system-info?, shmSlot*)>
   * <!ELEMENT manager (property*, nested_property*)>
   * <!ATTLIST manager
   *   name CDATA #REQUIRED
   * >
   * <!ELEMENT service (property*, nested_property*)>
   * <!ATTLIST service
   *   name CDATA #REQUIRED
   *   provider CDATA #REQUIRED
   * >
   * <!ELEMENT system-info (property+)>
   * <!ELEMENT application (property*, nested_property*)>
   * <!ATTLIST application
   *   name CDATA #REQUIRED
   *   provider CDATA #REQUIRED
   * >
   * <!ELEMENT shmSlot EMPTY>
   * <!ATTLIST shmSlot
   *   name CDATA #REQUIRED
   *   size CDATA #REQUIRED
   * >
   */
  private static void processConfiguration(Element configuration, ConfigurationLevel template) throws ClusterConfigurationException, ConfigurationException {
    NodeList managers = configuration.getElementsByTagName("manager");
    if (managers.getLength() > 0) {
      ManagerHandler managerHandler = template.getManagerAccess();
      for (int i = 0; i < managers.getLength(); i++) {
        Element manager = (Element) managers.item(i);
        String managerName = manager.getAttribute("name").trim();
        ComponentProperties properties = managerHandler.getProperties(managerName, false);
        updateProperties(manager, properties);
      }
    }
    NodeList services = configuration.getElementsByTagName("service");
    if (services.getLength() > 0) {
      ComponentHandler componentHandler = template.getComponentAccess();
      for (int i = 0; i < services.getLength(); i++) {
        Element service = (Element) services.item(i);
        String serviceName = service.getAttribute("name").trim();
        String serviceProvider = service.getAttribute("provider").trim();
        String componentName = Utils.modifyComponentName(serviceName, serviceProvider);
        ComponentProperties properties = componentHandler.getServiceProperties(componentName, false);
        updateProperties(service, properties);
      }
    }
    NodeList applications = configuration.getElementsByTagName("application");
    if (applications.getLength() > 0) {
      ApplicationHandler applicationHandler = template.getApplicationAccess();
      for (int i = 0; i < applications.getLength(); i++) {
        Element application = (Element) applications.item(i);
        String applicationName = application.getAttribute("name").trim();
        String applicationProvider = application.getAttribute("provider").trim();
        ComponentProperties properties = applicationHandler.getApplicationProperties(applicationName, applicationProvider, false);
        updateProperties(application, properties);
      }
    }
    NodeList systemInfos = configuration.getElementsByTagName("system-info");
    if (systemInfos.getLength() > 0) {
      Element systemInfo = (Element) systemInfos.item(0);
      ComponentProperties properties = template.getSystemInfo(false);
      updateProperties(systemInfo, properties);
    }
    NodeList shmSlots = configuration.getElementsByTagName("shmSlot");
    if (shmSlots.getLength() > 0) {
      ShmConfiguration shmConfig = template.getShmAccess();
      for (int i = 0; i < shmSlots.getLength(); i++) {
        Element shmSlot = (Element) shmSlots.item(i);
        String shmSlotName = shmSlot.getAttribute("name").trim();
        String shmSlotSize = shmSlot.getAttribute("size").trim();
        shmConfig.setShmProperty(shmSlotName, shmSlotSize);
      }
    }
  }

  /**
   * <!ELEMENT nested_property (property*|nested_property*)>
   * <!ATTLIST nested_property name CDATA #REQUIRED>
   * <!ELEMENT property EMPTY>
   * <!ATTLIST property
   *         name CDATA #REQUIRED
   *         value CDATA #REQUIRED
   *         secure (true|false) #IMPLIED
   *         parameterized (true|false) #IMPLIED
   *         contains_link (true|false) #IMPLIED
   *         computed (true|false) #IMPLIED
   *         final (true|false) #IMPLIED
   *         disabled (true|false) #IMPLIED
   *         description CDATA #IMPLIED
   *         short_description CDATA #IMPLIED
   *         visibility (EXPERT|NOVICE|ANY) #IMPLIED
   * >
   */
  private static void updateProperties(Element rootElement, ComponentProperties properties) throws ClusterConfigurationException, ConfigurationException {
    NodeList list = rootElement.getChildNodes();
    PropertySheet propertySheet = properties.getPropertySheet();
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) list.item(i);
        if (element.getTagName().equals("property")) {
          String name = element.getAttribute("name").trim();
          String value = element.getAttribute("value").trim();
          int typeMask = 0;
          //make property type mask
          if (element.hasAttribute("secure") && element.getAttribute("secure").trim().equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_SECURE;
          if (element.hasAttribute("parameterized") && element.getAttribute("parameterized").trim().equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_PARAMETERIZED;
          if (element.hasAttribute("contains_link") && element.getAttribute("contains_link").trim().equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_CONTAINS_LINK;
          if (element.hasAttribute("computed") && element.getAttribute("computed").trim().equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_COMPUTED;
          if (element.hasAttribute("final") && element.getAttribute("final").trim().equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_FINAL;
          if (element.hasAttribute("disabled") && element.getAttribute("disabled").trim().equals("true"))
            typeMask |= PropertyEntry.ENTRY_TYPE_DISABLED;
          String description = element.getAttribute("description").trim();
          String shortDescription = element.getAttribute("short_description").trim();
          byte visibilityByte = PropertyEntry.VISIBILITY_ANY;
          if (element.hasAttribute("visibility")) {
            String visibility = element.getAttribute("visibility").trim();
            if (visibility.equals("ANY")) {
              visibilityByte = PropertyEntry.VISIBILITY_ANY;
            } else if (visibility.equals("EXPERT")) {
              visibilityByte = PropertyEntry.VISIBILITY_EXPERT;
            } else if (visibility.equals("NOVICE")) {
              visibilityByte = PropertyEntry.VISIBILITY_NOVICE;
            }
          }
          PropertyEntry pEntrey;
          try {
            pEntrey = propertySheet.getPropertyEntry(name);
            pEntrey.setDefault(value, typeMask);
            pEntrey.setDescription(description);
          } catch (NameNotFoundException e) {
            pEntrey = propertySheet.createPropertyEntry(name, value, description, typeMask);
          }
          pEntrey.setShortDescription(shortDescription);
          pEntrey.setVisibility(visibilityByte);
        } else if (element.getTagName().equals("nested_property")) {
          String name = element.getAttribute("name").trim();
          ComponentProperties componentProperties = properties.getNestedProperties(name);
          updateProperties(element, componentProperties);
        }
      }
    }
  }

}