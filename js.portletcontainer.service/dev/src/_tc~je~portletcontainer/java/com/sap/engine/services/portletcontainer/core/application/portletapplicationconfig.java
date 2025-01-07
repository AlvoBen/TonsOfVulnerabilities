/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.portletcontainer.core.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletMode;
import javax.portlet.PreferencesValidator;

import com.sap.engine.lib.descriptors.portlet.CustomPortletModeType;
import com.sap.engine.lib.descriptors.portlet.CustomWindowStateType;
import com.sap.engine.lib.descriptors.portlet.ExpirationCacheType;
import com.sap.engine.lib.descriptors.portlet.MimeTypeType;
import com.sap.engine.lib.descriptors.portlet.PortletAppType;
import com.sap.engine.lib.descriptors.portlet.PortletCollectionType;
import com.sap.engine.lib.descriptors.portlet.PortletModeType;
import com.sap.engine.lib.descriptors.portlet.PortletNameType;
import com.sap.engine.lib.descriptors.portlet.PortletPreferencesType;
import com.sap.engine.lib.descriptors.portlet.PortletType;
import com.sap.engine.lib.descriptors.portlet.SecurityConstraintType;
import com.sap.engine.lib.descriptors.portlet.SecurityRoleRefType;
import com.sap.engine.lib.descriptors.portlet.SupportsType;
import com.sap.engine.lib.descriptors.portlet.TransportGuaranteeType;
import com.sap.engine.lib.descriptors.portlet.UserAttributeType;
import com.sap.engine.lib.descriptors.portlet.UserDataConstraintType;
import com.sap.engine.lib.util.HashMapObjectObject;
import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.container.descriptor.PortletDeploymentDescriptor;
import com.sap.engine.services.portletcontainer.core.PortletPreferencesImpl;
import com.sap.engine.services.portletcontainer.core.UserAttributesMap;
import com.sap.tc.logging.Location;

/**
 * The <code>PortletApplicationConfig</code> class contains all portlet's
 * configuration objects loaded from the portlet deployment descriptor.
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletApplicationConfig {

  private Location currentLocation = Location.getLocation(getClass());
  
  //private PortletType[] portletTypes = null;
  private Hashtable portletTypes = new Hashtable();
  private CustomPortletModeType[] customPortletModeTypes = null;
  private CustomWindowStateType[] customWindowStateTypes = null;

  /** All the attribtues the portlet application has declared to use */
  private UserAttributeType[] userAttributeTypes = null;
  /** User attributes the portlets will use and that are supported by Portlet Container */
  private Set userAttributes = new HashSet();

  private SecurityConstraintType[] securityConstraintTypes = null;
  /**
   * Maps portlet names to their security constraints if any;
   * If no security constraints are declared for a portlet, it is not in the map. 
   */
  private HashMapObjectObject securityConstraintsMap = new HashMapObjectObject();
  private String version = "";
  private Hashtable portletsPreferences = new Hashtable();
  private HashMapObjectObject portletClasses = new HashMapObjectObject();
  private HashMapObjectObject portletArguments = new HashMapObjectObject();
  private HashMapObjectObject expirationCaches = new HashMapObjectObject();
  private String[][] allPortlets = null;
  private HashMapObjectObject securityRolesReferencs = new HashMapObjectObject(); //portlet - SecurityRoleRefType[]
  private Set preverencesValidatorClasses = new HashSet();

  /** Stores all supported portlet modes for each portlet regardles the mime-type */
  private HashMapObjectObject supportedPortletModes = new HashMapObjectObject();
  /** 
   * Stores supported portlet modes for each portlet per mime-type as declared in DD;
   * The map has portlet names as keys and HashMapObjectObject as values, where
   * the HashMapObjectObject values contain mime-type of String type as keys and
   * List-s of the declared supported portlet modes (PortletMode objects) as values. 
   */
  private HashMapObjectObject supportedPortletModesMaps = new HashMapObjectObject();
  
  //supported portlet modes for mime-type for each portlet(key=[portletName]; value=[hashmap[mime-type][modes]])
  private HashMapObjectObject supportedMimeTypes = new HashMapObjectObject();
  private Set supportedWindowStates = new HashSet();  

  /**
   * @directed directed
   */
  private PortletApplicationContext portletApplicationContext = null;

  /**
   * Creates new <code>PortletApplicationConfig</code> object to collect all
   * configuration objects the portlet has defined in its deployment descriptor.
   * @param portletApplicationContext the <code>PortletApplicationContext</code>
   * for the portlet application.
   */
  public PortletApplicationConfig(PortletApplicationContext portletApplicationContext) {
    this.portletApplicationContext = portletApplicationContext;
  }

  /**
   * Initializes the portlet configuration objects defined by the specified
   * deployment descriptor.
   * @param portletDD the <code>PortletDeploymentDescriptor</code> object that
   * represents the portlet descriptor.
   */
  public void init(PortletDeploymentDescriptor portletDD) {
    PortletAppType portletApp = portletDD.getPortletApp();
    version = portletApp.getVersion();
    initPortlets(portletApp);
    initCustomPortletModes(portletApp);
    initCustomWindowStates(portletApp);
    initUserAttributes(portletApp);
    initSecurityConstraints(portletApp);
    initSupportedPortletModes(portletApp);
    initSupportedWindowStates(portletApp);
  }//end of init(PortletDeploymentDescriptor portletDD)

  /**
   * Initializes each portlet defined in the portlet application deployment
   * descriptor.
   * @param portletApp the portlet application definition object.
   */
  private void initPortlets(PortletAppType portletApp) {
    //TODO
    PortletType[] allPortletTypes = portletApp.getPortlet();

    //init portlet preferences and preferences validators
    for (int i = 0; allPortletTypes != null && i < allPortletTypes.length; i++) {
      String portletName = allPortletTypes[i].getPortletName().get_value();
      PortletPreferencesType prefsType = allPortletTypes[i].getPortletPreferences();

      portletTypes.put(portletName, allPortletTypes[i]);
      if (prefsType != null) {
        portletsPreferences.put(portletName, prefsType);
        if (prefsType.getPreferencesValidator() != null) {
          preverencesValidatorClasses.add(prefsType.getPreferencesValidator());
        }
      }
      if (allPortletTypes[i].getSecurityRoleRef() != null) {
        securityRolesReferencs.put(allPortletTypes[i].getPortletName().get_value(), allPortletTypes[i].getSecurityRoleRef());
      }
    }

    //init portlet classes
    Enumeration portletNames = portletTypes.keys();
    while (portletNames.hasMoreElements()) {
      String portletName = (String)portletNames.nextElement();
      PortletType portletType = (PortletType)portletTypes.get(portletName);
      String portletClass = portletType.getPortletClass();
      portletClasses.put(portletName, portletClass);
    }

    //init expiration-cache
    portletNames = portletTypes.keys();
    while (portletNames.hasMoreElements()) {
      String portletName = (String)portletNames.nextElement();
      PortletType portletType = (PortletType)portletTypes.get(portletName);
      ExpirationCacheType expirationCache = portletType.getExpirationCache();
      if (expirationCache != null) {
        expirationCaches.put(portletName, new Integer(expirationCache.get_value()));
      }
    }

  }//end of initPortlets(PortletAppType portletApp)

  /**
   * Initializes the custom portlet modes supported by the portlet application.
   * @param portletApp the portlet application definition.
   */
  private void initCustomPortletModes(PortletAppType portletApp) {
    //TODO
    customPortletModeTypes = portletApp.getCustomPortletMode();
  }//end of initCustomPortletModes(PortletAppType portletApp)

  /**
   * Initializes the supported windows states by the portlet application.
   * @param portletApp the portlet application definition.
   */
  private void initSupportedWindowStates(PortletAppType portletApp) {
    CustomWindowStateType [] windowStates = customWindowStateTypes = portletApp.getCustomWindowState();
    for (int i = 0; windowStates != null && i < windowStates.length; i++) {
      supportedWindowStates.add(windowStates[i].getWindowState().get_value());
    }
  }

  /**
   * Returns the supported window states.
   * @return a <code>Set</code> of supported window states.
   */
  public Set getSupportedWindowStates() {
    return supportedWindowStates;
  }

  /**
   * Initializes the custom window states that the portlet application supports.
   * @param portletApp the portlet application definition.
   */
  private void initCustomWindowStates(PortletAppType portletApp) {
    //TODO
    customWindowStateTypes = portletApp.getCustomWindowState();
  }//end of initCustomWindowStates(PortletAppType portletApp)

  /**
   * Initializes the portlet modes that the portlet application suports.
   * @param portletApp the portlet application definition.
   */
  private void initSupportedPortletModes(PortletAppType portletApp) {
    PortletType[] allPortletTypes = portletApp.getPortlet();

    //init supported portlet modes for each portlet
    for (int i = 0; allPortletTypes != null && i < allPortletTypes.length; i++) {
      String portletName = allPortletTypes[i].getPortletName().get_value();
      SupportsType[] supportsTypes = allPortletTypes[i].getSupports();
      HashMapObjectObject mimeTypes = new HashMapObjectObject();
      Set modes = new HashSet();      
      HashMap typeModesMap = new HashMap();
      for (int j = 0; supportsTypes != null && j < supportsTypes.length; j++) {
        MimeTypeType mimeType = supportsTypes[j].getMimeType();
        List modesList = new ArrayList();
        PortletModeType[] portletModes = supportsTypes[j].getPortletMode();
        //mimeTypes.put(mimeType, portletModes);
        for (int k = 0; portletModes != null && k < portletModes.length; k++) {
          modes.add(portletModes[k].get_value());
          if (portletModes[k].get_value() != null && !"".equals(portletModes[k].get_value().trim())) {
            PortletMode current = new PortletMode(portletModes[k].get_value());
            if (!modesList.contains(current)) {
              modesList.add(current);
            }
          }          
        }
        mimeTypes.put(mimeType.get_value(), modes);
        typeModesMap.put(mimeType.get_value(), modesList);
      }
      supportedMimeTypes.put(portletName, mimeTypes);
      supportedPortletModes.put(portletName, modes);     
      supportedPortletModesMaps.put(portletName, typeModesMap);
    }
  }

  /**
   * Returns the portlet modes supported by the specified portlet.
   * The result is a union of the <portlet-mode> for all <mime-type> 
   * in all the <supports> for the given portlet.
   * @param portletName the portlet name.
   * @return an <code>Enumeration</code> of all supported portlet modes, or
   * <code>null</code>.
   */
  public Enumeration getSupportedPortletModes(String portletName) {
    //TODO: never used
    Set supportedModes = (Set)supportedPortletModes.get(portletName);
    if (supportedModes != null) {
      return Collections.enumeration(supportedModes);
    } else {
      return null;
    }
  }

  /**
   * Returns all supported portlet modes.
   * @return a HashMap of all supported modes.
   */
  public HashMapObjectObject getSupportedPortletModes() {
    return supportedPortletModes;
  }

  /**
   * Returns a list of <code>ContentType</code>s supported by the specified portlet.
   * @param portletName the portlet name.
   * @return a list of <code>ContentType<code>s supported by the specified portlet or null.
   */
  public Enumeration getMimeTypes(String portletName) {
    HashMapObjectObject mimeTypes = (HashMapObjectObject)supportedMimeTypes.get(portletName);
    if (mimeTypes != null) {
      return mimeTypes.keys();
    }
    return null;
  }

  /**
   * Loads user attributes the portles will use.
   * The attributes that are not supported by the Portlet Container are ignored.
   * @param portletApp
   */
  private void initUserAttributes(PortletAppType portletApp) {
    userAttributeTypes = portletApp.getUserAttribute();
    /*
     * PLT.17.1
     * A deployer must map the portlet application's logical user attributes to
     * the corresponding user attributes offered by the runtime environment.
     */
    PortletApplicationComponents comp = portletApplicationContext.getPortletComponents();
    UserAttributesMap attributesMap = comp.getUserAttribtuesMap();
    if (userAttributeTypes != null) {
      for (int i = 0; i < userAttributeTypes.length; i++) {
        if (attributesMap.isSupported(userAttributeTypes[i].getName().get_value())) {
          userAttributes.add(userAttributeTypes[i].getName().get_value());
        }
      }
    }
  }//end of initUserAttributes(PortletAppType portletApp)

  /**
   * Initializes the security constraints defined for the portlet application.
   * Note: unlike the servlets, there is NO authorization constraint establishing a 
   * requirement for authentication and names the permitted authorization roles. 
   * @param portletApp the portlet application definition.
   */
  private void initSecurityConstraints(PortletAppType portletApp) {
    securityConstraintTypes = portletApp.getSecurityConstraint();
    if (securityConstraintTypes != null) {
      for (int i = 0; i < securityConstraintTypes.length; i++) {
        SecurityConstraintType secConstraint = securityConstraintTypes[i];
        
        PortletCollectionType portlets = secConstraint.getPortletCollection(); //sequence of portlet names; at least 1
        
        UserDataConstraintType userDataConstraint = secConstraint.getUserDataConstraint();             
        String transportGuarantee = userDataConstraint.getTransportGuarantee().getValue();
        
        PortletNameType[] portletNames = portlets.getPortletName();
        if (portletNames != null) {
          for (int j = 0; j < portletNames.length; j++) {
            String portletName = portletNames[j].get_value();
            List transports = (List) securityConstraintsMap.get(portletName);
            if (transports == null) {
              transports = new ArrayList();
            }
            transports.add(transportGuarantee);
            securityConstraintsMap.put(portletName, transports);
          }
        } else {
          if (currentLocation.beDebug()) {
              LogContext.getLocation(LogContext.LOCATION_DEPLOY).traceDebug(
                  "Portlet names in security constraints [" +  
                  ((secConstraint != null) ? secConstraint.getDisplayName() : "") +
                  "] for portlet application [" +
                  ((portletApplicationContext != null) ? portletApplicationContext.getPortletApplicationName() : "") +
                  "] are null.");
          }
        }
      } //security constraints is empty - not possible?
    } //no security constraints
  }//end of initSecurityConstraints(PortletAppType portletApp)
  
  /**
   * Returns an unmodifiable list of the transport guarantee of user-data-constraints
   * for the given portlet name. 
   * @param portletName the portlet to get the constraints for
   * @return returns an unmodifiable list of the 
   */
  public List getTransportGuarantee(String portletName) {
    List result = (List) securityConstraintsMap.get(portletName);
    if (result == null) {
      result = Collections.EMPTY_LIST;
    }
    result = Collections.unmodifiableList(result);
    return result;
  }

  //TODO get methods

  /**
   * Returns the portlet definition for the specified portlet.
   * @param portletName the portlet name.
   * @return the portlet definition for the specified portlet.
   */
  public PortletType getPortletType(String portletName) {
    return (PortletType)portletTypes.get(portletName);
  }

  /**
   * Returns the portlet class name for the specified portlet.
   * @param portletName the portlet name.
   * @return the portlet class name for the specified portlet.
   */
  public String getPortletClass(String portletName) {
    return (String)portletClasses.get(portletName);
  }

  /**
   * Returns a <code>String</code> array of all the portlets defined in the
   * portlet application.
   * [i][0] - portlet name
   * [i][1] - portlet class
   * @return all portlets this portlet application contains.
   */
  public String [][] getAllPortlets() {
    return allPortlets;
  }
  //TODO private methods

  /**
   * Returns all portlet arguments.
   * @return portlet arguments.
   */
  public HashMapObjectObject getPortletArguments() {
    return portletArguments;
  }

  /**
   * Returns a <code>HashMap</code> of all portlet classes.
   * @return portlet classes.
   */
  public HashMapObjectObject getPortletClasses() {
    return portletClasses;
  }

  /**
   * Returns the security role name the roleName is linked to.
   * If the security-role-ref element does not define a role-link element, returns the roleName.
   * @param portletName	the portlet
   * @param roleName	the role-name
   * @return	the role-link value from the security-role-ref;
   * 	null if no such role-name has been found for this portlet
   */
  public String getLinkedSecurityRoleName(String portletName, String roleName) {
    String result = null;
    SecurityRoleRefType[] types = (SecurityRoleRefType[]) securityRolesReferencs.get(portletName);
    if (types != null) {
      for (int i = 0; i < types.length; i++) {
        if (types[i].getRoleName().equals(roleName)) {
          if (types[i].getRoleLink() != null) {
            result = types[i].getRoleLink().get_value();
          } else {
            //the security-role-ref element does not define a role-link element
            result = roleName;
          }
          break;
        }
      }
    }
    return result;
  }

  /**
   * Returns new object PortletPreferences containing default values from DD
   * and loaded preferences validator.
   * @param portletName
   * @return  new object PortletPreferences containing the default values from the DD.
   */
  public PortletPreferencesImpl getPortletsPreferences(String portletName) {
    PortletPreferencesImpl result = null;
    if (! portletsPreferences.containsKey(portletName) || portletsPreferences.get(portletName) == null) {
      result = new PortletPreferencesImpl(); //empty preferences object
    } else {
      PortletPreferencesType portletPreferencesType = (PortletPreferencesType) portletsPreferences.get(portletName);
      PreferencesValidator preferenceValidator = getPreferenceValidatorByClassName(portletPreferencesType.getPreferencesValidator());
      result = new PortletPreferencesImpl(portletPreferencesType, preferenceValidator, portletName, portletApplicationContext.getPortletApplicationName());
    }
    return result;
  }

  /**
   * Returns a <code>String</code> array of all portlet names in the portlet
   * application.
   * @return all defined portlet names.
   */
  public String[] getPortletsNames() {
    String[] result = new String[portletTypes.size()];
    Enumeration en = portletTypes.keys();
    for (int i = 0; i < result.length && en.hasMoreElements(); i++) {
      result[i] = (String) en.nextElement();
    }
    return result;
  }

  /**
   * Return the <code>PreferencesValidator</code> object for the specified portlet.
   * @param portletName the name of the portlet.
   * @return the <code>PreferencesValidator</code> instance.
   */
  public PreferencesValidator getPreferenceValidator(String portletName) {
    PortletPreferencesType portletPreferencesType = (PortletPreferencesType) portletsPreferences.get(portletName);
    if (portletPreferencesType == null || portletPreferencesType.getPreferencesValidator() == null) {
      return null;
    }
    return getPreferenceValidatorByClassName(portletPreferencesType.getPreferencesValidator());
  }


  /**
   * Gets the preference validator specified by the class name.
   * If not loaded attempts to load it.
   * @param preferencesValidatorClassName	a valid class name; may be null
   * @return the preference validator instance
   */
  public PreferencesValidator getPreferenceValidatorByClassName(String preferencesValidatorClassName) {
    if (preferencesValidatorClassName == null) {
      return null;
    }
    PortletApplicationComponents comp = portletApplicationContext.getPortletComponents();
    PreferencesValidator result = comp.getPreferencesValidator(preferencesValidatorClassName);
   return result;
  }//end of getPreferenceValidator

  /**
   * Returns all preferences validator classes.
   * @return the preverences validator classes.
   */
  public Set getPreverencesValidatorClasses() {
    return preverencesValidatorClasses;
  }

  /**
   * Returns the user attributes.
   * @return the userAttributes.
   */
  public Set getUserAttributes() {
    return userAttributes;
  }

  /**
   * Returns the expiration-cache value defined for specified portletName.
   * @param portletName name of the porlet
   * @return expiration-cache value defined for specified portletName, or
   * <code>null</code> if expiration cache is not defined.
   */
  public Integer getExpirationCache(String portletName) {
    return (Integer)expirationCaches.get(portletName);
  }
  
  /**
   * Returns a map containing mime-type-s of String type as keys and
   * List-s of the declared supported portlet modes (PortletMode objects) as values.
   * Note that VIEW mode is always supported even if not declared in <supports> in DD,
   * thus additional check for VIEW should be made when using this method.
   * @param portletName name of the portlet
   * @return java.util.Map object mapping the mime type to its supported portlet modes.
   */
  public Map getSupportedModesMap(String portletName) {
    return (Map) supportedPortletModesMaps.get(portletName);
  }

}//end of class
