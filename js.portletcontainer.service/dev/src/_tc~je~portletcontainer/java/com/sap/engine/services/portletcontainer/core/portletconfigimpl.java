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
package com.sap.engine.services.portletcontainer.core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;

import com.sap.engine.lib.descriptors.portlet.InitParamType;
import com.sap.engine.lib.descriptors.portlet.PortletInfoType;
import com.sap.engine.lib.descriptors.portlet.PortletType;
import com.sap.engine.lib.descriptors.portlet.ResourceBundleType;
import com.sap.engine.lib.descriptors.portlet.SupportedLocaleType;
import com.sap.engine.services.portletcontainer.LogContext;
import com.sap.engine.services.portletcontainer.core.application.DefaultResourceBundle;
import com.sap.tc.logging.Location;
/**
 * The <code>PortletConfigImpl</code> class is an implemetation of the <code>
 * PortletConfig</code> interface which provides the portlet with its 
 * configuration. The configuration holds information about the portlet that is
 * valid for all users. The configuration is retrieved from the portlet definition
 * in the deployment descriptor. The portlet can only read the configuration data. 
 *
 * The configuration information contains the portlet name, the portlet initialization
 * parameters, the portlet resource bundle and the portlet application context. 
 *
 * @author Diyan Yordanov
 * @version 7.10
 */
public class PortletConfigImpl implements PortletConfig {
  
  /**
   * The <code>PortletContext</code> of the portlet application the portlet is in.
   */
  private PortletContext portletContext = null;
  
  /**
   * The portlet definition from the deployment descriptor.
   */
  private PortletType portletType = null;
  
  /**
   * Init parameters from <code>init-param</code> tag.
   */
  private Hashtable initParameters = new Hashtable();
  
  private Location currentLocation = Location.getLocation(getClass());
  /**
   * Containes Resourse Bundles for different supported Locales
   */
  private Hashtable resourceBundles = new Hashtable();
  /**
   * Contains Locale objects for locales supported by the portlet
   */
  private Vector locales = new Vector();
  
  /**
   * The application classloader.
   */
  private ClassLoader classLoader = null;
  private String title = null;
  private String shortTitle = null;
  private String keywords = null;
  
  /**
   * Creates new <code>PortletConfigImpl</code> object.
   * @param portletContext the <code>PortletContext</code> of the portlet 
   * application the portlet is in.
   * @param portletType the <code>PortletType</code> object that contains the 
   * portlet configuration info loaded from the deployment descriptor.
   * @param classLoader the application classloader.
   */
  public PortletConfigImpl(PortletContext portletContext, PortletType portletType, ClassLoader classLoader) {
    this.portletContext = portletContext;
    this.portletType = portletType;
    this.classLoader = classLoader;
    initPortletInitParams(portletType);
    initResourceBundle(portletType);
  }
  
  /**
   * Returns the name of the portlet.
   * <P>
   * The name may be provided via server administration, assigned in the
   * portlet application deployment descriptor with the <code>portlet-name</code>
   * tag.
   * @return the portlet name.
   */
  public String getPortletName() {
    return portletType.getPortletName().get_value();
  }

  /**
   * Returns the <code>PortletContext</code> of the portlet application 
   * the portlet is in.
   * @return a <code>PortletContext</code> object, used by the caller to interact 
   * with its portlet container
   * @see PortletContext
   */
  public PortletContext getPortletContext() {
    return portletContext;
  }

  /**
   * Gets the resource bundle for the given locale based on the
   * resource bundle defined in the deployment descriptor
   * with <code>resource-bundle</code> tag or the inlined resources
   * defined in the deployment descriptor.
   * @param locale the locale for which to retrieve the resource bundle.
   * @return the resource bundle for the given locale.
   * @exception	IllegalArgumentException if the locale is <code>null</code>.
   */
  public ResourceBundle getResourceBundle(Locale locale) {
  	if (locale == null) {
  		throw new IllegalArgumentException("The Locale parameter is null: unable to return resource bundle for unspecified locale.");
  	}
    ResourceBundle resourceBundle = (ResourceBundle)resourceBundles.get(locale);
    if (resourceBundle == null) {
      resourceBundle = advancedSearch(locale);
    }
    return resourceBundle; 
  }

  /**
   * Returns a String containing the value of the named initialization parameter, 
   * or null if the parameter does not exist.
   * @param name a <code>String</code> specifying the name of the initialization parameter.
   * @return a <code>String</code> containing the value of the initialization parameter.
   * @exception	IllegalArgumentException if name is <code>null</code>.
   */
  public String getInitParameter(String name) {
    // TODO return the initialization parameter value found in the portlet
    // definition in the deployment descriptor.
    if (name == null) {
      // spec.
      throw new IllegalArgumentException("Parameter name == null");
    }
   
    return (String)initParameters.get(name);
  }

  /**
   * Returns the names of the portlet initialization parameters as an 
   * <code>Enumeration</code> of String objects, or an empty <code>Enumeration</code> 
   * if the portlet has no initialization parameters.    
   * @return an <code>Enumeration</code> of <code>String</code>	objects containing 
   * the names of the portlet	initialization parameters, or an empty 
   * <code>Enumeration</code> if the portlet has no initialization parameters. 
   */
  public Enumeration getInitParameterNames() {
    // TODO return the initialization parameter names found in the portlet
    // definition in the deployment descriptor.
    return initParameters.keys();
  }
  
  /**
   * Loads the init parameters of the portlet specified in the deployment descriptor.
   * @param portletType the portlet definition from the deployment descriptor.
   */
  private void initPortletInitParams(PortletType portletType) {
    InitParamType[] initParams = portletType.getInitParam();
    String portletName = portletType.getPortletName().get_value();
    //Hashtable paramsH = new Hashtable();
    if (initParams != null) {
      for (int i = 0; i< initParams.length; i++) {
        if (initParams[i] != null) {
          if (initParams[i].getName() == null || initParams[i].getName().get_value() == null
              || initParams[i].getValue() == null || initParams[i].getValue().get_value() == null) {
            LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logWarning(currentLocation, "ASJ.portlet.000044", "Incorrect parameters of the portlet [{0}]. Parameter name or value is null.", new Object[]{portletName}, null, null);
          } else {
            //paramsH.put(initParams[i].getName().get_value(), initParams[i].getValue().get_value());
            initParameters.put(initParams[i].getName().get_value(), initParams[i].getValue().get_value());
          }
        }
      }
      //portletsInitParameters.put(portletName, paramsH);
    }
  }
  
  /**
   * Loads the resource bundles for the specified portlet.
   * @param portletType the portlet definition from the deployment descriptor.
   */
  private void initResourceBundle(PortletType portletType) {
    SupportedLocaleType [] supportedLocaleTypes = portletType.getSupportedLocale();
    for (int i = 0; i < supportedLocaleTypes.length; i++) {
      Locale locale = new Locale(supportedLocaleTypes[i].get_value(), "");
      locales.add(locale);
    }
    if (portletType.getChoiceGroup1().isSetPortletInfo()) {
      loadPortletInfo(portletType.getChoiceGroup1().getPortletInfo());
      if (locales.isEmpty()) {
        getDefaultLocale();
      }
      loadDefaultResourceBundles();
    } else if (portletType.getChoiceGroup1().isSetSequenceGroup1()) {
      ResourceBundleType resourceBundleType = portletType.getChoiceGroup1().getSequenceGroup1().getResourceBundle();
      String resourceBundleClass = resourceBundleType.get_value();
      loadPortletInfo(portletType.getChoiceGroup1().getSequenceGroup1().getPortletInfo());
      loadResourceBundles(resourceBundleClass);
    }
  }

  /**
   * Loads a default resource bundle if such is not specified in the descriptor.
   */
  private void loadDefaultResourceBundles() {
    Iterator iter = locales.iterator();
    while (iter.hasNext()) {
      Locale locale = (Locale)iter.next();
      DefaultResourceBundle bundle = new DefaultResourceBundle(title, shortTitle, keywords);
      resourceBundles.put(locale, bundle);
    }
    if (locales.isEmpty()) {
      Locale locale = getDefaultLocale();
      DefaultResourceBundle bundle = new DefaultResourceBundle(title, shortTitle, keywords);
      resourceBundles.put(locale, bundle);
    }
  }

  /**
   * Returns the default locale.
   * @return the default locale - "en".
   */
  private Locale getDefaultLocale() {
    Locale defaultLocale = new Locale("en","");
    locales.add(defaultLocale);
    return defaultLocale;
  }

  /**
   * Loads the portlet info defined in the <code>portlet-info</code> tag.
   * @param portletInfo the content of the <code>portlet-info</code> tag.
   */
  private void loadPortletInfo(PortletInfoType portletInfo) {
    if (portletInfo != null) {
      this.title = portletInfo.getTitle().get_value();
      if (portletInfo.getShortTitle() != null) {
        this.shortTitle = portletInfo.getShortTitle().get_value();
      }      
      if (portletInfo.getKeywords() != null) {
        this.keywords = portletInfo.getKeywords().get_value();
      }
    }
  }

  /**
   * Loads the resource bundle by the specified class.
   * @param resourceBundleClass the resource bundle class name.
   */
  private void loadResourceBundles(String resourceBundleClass) {
    //If the root resource bundle does not contain the initial portlet info,
    //and these values are defined inline, they must be add as resources to the root resource bundle
    //locales aways contains a default locale
    Iterator iter = locales.iterator();
    while (iter.hasNext()) {
      Locale locale = (Locale)iter.next();
      ResourceBundle bundle = null;
      bundle = loadResourceBundle(locale, resourceBundleClass);
      bundle = addInlineValues(bundle);
      if (bundle != null) {
        resourceBundles.put(locale, bundle);
      }
    }
    if (locales.isEmpty()) {
      Locale locale = getDefaultLocale();
      ResourceBundle bundle = loadResourceBundle(locale, resourceBundleClass);
      if (bundle == null) {
        bundle = new DefaultResourceBundle(title, shortTitle, keywords);
      } else {
        bundle = addInlineValues(bundle);
      }      
      if (bundle != null) {
        resourceBundles.put(locale, bundle);
      }
    }
  }

  /**
   * Adds the inline value if missing in the resource bundle.
   * @param bundle the resource bundle.
   * @return the initialized resource bundle.
   */
  private ResourceBundle addInlineValues(ResourceBundle bundle) {
    String newTitle = null;
    String newShortTitle = null;
    String newKeywords = null;
    try {
      if (bundle.getString(DefaultResourceBundle.TITLE) != null) {
        newTitle = null;
      }
    } catch (MissingResourceException e1) {
      newTitle = title;
    }
    try {
      if (bundle.getString(DefaultResourceBundle.SHORT_TITLE) != null) {
        newShortTitle = null;
      }
    } catch (MissingResourceException e1) {
      if (shortTitle != null) {
        newShortTitle = shortTitle;
      }      
    }
    try {
      if (bundle.getString(DefaultResourceBundle.KEYWORDS) != null) {
        newKeywords = null;
      }
    } catch (MissingResourceException e1) {
      if (keywords != null) {
        newKeywords = keywords;
      }
    }
    if (newTitle != null || newShortTitle != null || newKeywords != null) {
      bundle = new DefaultResourceBundle(newTitle, newShortTitle, newKeywords, bundle);
    }
    return bundle;
  }

  /**
   * Loads the resource bundle for the specified locale.
   * @param locale the Locale.
   * @param resourceBundleClass the resource bundle class name.
   * @return a resource bundle instance for the specified locale.
   */
  private ResourceBundle loadResourceBundle(Locale locale, String resourceBundleClass) {
    ResourceBundle resourceBundle = null;
    try {
      resourceBundle = ResourceBundle.getBundle(resourceBundleClass, locale, classLoader);
    } catch (MissingResourceException x) {
      LogContext.getCategory(LogContext.CATEGORY_REQUESTS).logError(currentLocation, "ASJ.portlet.000045", "Cannot load Resource Bundle for class [{0}].", new Object[]{resourceBundleClass}, x, null, null);
      return null;
    }
    return resourceBundle;
  }

  /**
   * Tries to match the specified locale to the set of supported locales and 
   * search for ResourceBundle with it.
   * @param locale locale to be searched.
   * @return a resource bundle instance for the specified locale
   */
  private ResourceBundle advancedSearch(Locale locale) {
    String variant = locale.getVariant();
    if (variant != null && variant.length() > 0) {
        locale = new Locale(locale.getLanguage(), locale.getCountry());                                
    }

    if (!locales.contains(locale)) {
      String country = locale.getCountry();
      if (country != null && country.length() > 0) {
        locale = new Locale(locale.getLanguage(), "");
      }
    }

    if (!locales.contains(locale)) {
      locale = getDefaultLocale();
    }

    return (ResourceBundle)resourceBundles.get(locale);
  }

}
