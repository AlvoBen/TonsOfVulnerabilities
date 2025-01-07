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

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.portlet.PreferencesValidator;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import com.sap.engine.lib.descriptors.portlet.PortletPreferencesType;
import com.sap.engine.lib.descriptors.portlet.PreferenceType;
import com.sap.engine.lib.descriptors.portlet.ValueType;
import com.sap.engine.services.portletcontainer.LogContext;

/**
 * The <code>PortletPreferencesImpl</code> class is an implementation of the
 * <code>PortletPreferencesImpl</code> interface that allows the portlet to
 * store configuration data. It is not the purpose of this interface to replace
 * general purpose databases.
 * <p/>
 * There are two different types of preferences:
 * <p/>
 * <ul>
 * <li>modifiable preferences - these preferences can be changed by the portlet
 * in any standard portlet mode (EDIT, HELP, VIEW). Per default every preference
 * is modifiable.
 * <li>read-only preferences - these preferences cannot be changed by the portlet
 * in any standard portlet mode, but may be changed by administrative modes.
 * Preferences are read-only, if the are defined in the deployment descriptor
 * with read-only set to true, or if the portlet container restricts write access.
 * </ul>
 * Changes are persisted when the store method is called. The store method can
 * only be invoked within the scope of a processAction call. Changes that are
 * not persisted are discarded when the processAction or render method ends.
 *
 * @author diyan-y
 * @version 7.10
 */
public class PortletPreferencesImpl implements javax.portlet.PortletPreferences {

  //TODO: test this: HOW WILL WE MAINTAIN THE INFORMATION IF THERE IS NO PREFERENCES IN PORTLET DD FOR THIS PORTLET?
  private static final String STORE_ERROR_MESSAGE = "Store method is not called from an Action request!";
  private static final String READ_ONLY_MESSAGE = "Portlet preference is read only.";
  private static final String NAME_IS_NULL = "The given name is null.";

  /**
   * The default portlet preferences.
   */
  private PreferenceType[] preferenceTypes = null;

  /**
   * The default preferences validator.
   * Single validator instance per portlet definition.
   */
  private PreferencesValidator preferencesValidator = null;

  private String portletName = null;
  private String portletApplicationName = null;
  
  /**
   * The default portlet preferences.
   */
  private Map modifiablePreferences = new HashMap();
  private Set readOnlyPreferences = new HashSet();

  /**
   * Creates new <code>PortletPreferences</code> object.
   * @param portletPreferencesType the <code>PortletPreferencesType</code> object
   * that contains the portlet preferences info loaded from the deployment descriptor.
   * @param preferencesValidator the PreferenveValidator object if such is 
   * specified for the preferences.
   * @param portletName the portlet name.
   * @param portletApplicationName the name of the application.
   */
  public PortletPreferencesImpl(PortletPreferencesType portletPreferencesType,
      PreferencesValidator preferencesValidator, String portletName, String portletApplicationName) {
    preferenceTypes = portletPreferencesType.getPreference();
    loadPreferences(preferenceTypes);
    this.preferencesValidator = preferencesValidator;
    this.portletName = portletName;
    this.portletApplicationName = portletApplicationName;
  }//end of constructor

  public PortletPreferencesImpl() {
  }//end of constructor

  /**
   * Returns true, if the value of this key cannot be modified by the user.
   * <p>
   * Modifiable preferences can be changed by the portlet in any standard portlet
   * mode (<code>EDIT, HELP, VIEW</code>). Per default every preference is 
   * modifiable.
   * <p>
   * Read-only preferences cannot be changed by the portlet in any standard portlet 
   * mode, but inside of custom modes it may be allowed changing them.
   * Preferences are read-only, if they are defined in the deployment descriptor 
   * with <code>read-only</code> set to <code>true</code>, or if the portlet 
   * container restricts write access.
   * @param key key for which the associated value is to be checked.
   * @return false, if the value of this key can be changed, or if the key is 
   * not known.
   * @exception IllegalArgumentException if <code>key</code> is <code>null</code>.
   */
  public boolean isReadOnly(String key) {
    if (key == null) {
      throw new IllegalArgumentException(NAME_IS_NULL);
    } else {
      return readOnlyPreferences.contains(key);
    }
  }//end of isReadOnly(String name)

  /**
   * Returns the first String value associated with the specified key of this 
   * preference. If there is one or more preference values associated with the 
   * given key it returns the first associated value. If there are no preference 
   * values associated with the given key, or the backing preference database is 
   * unavailable, it returns the given default value.
   * @param key key for which the associated value is to be returned.
   * @param def the value to be returned in the event that there is no value 
   * available associated with this <code>key</code>.
   * @return the value associated with <code>key</code>, or <code>def</code>
   * if no value is associated with <code>key</code>, or the backing store is 
   * inaccessible.
   * @exception IllegalArgumentException if <code>key</code> is <code>null</code>. 
   * (A <code>null</code> value for <code>def</code> <i>is</i> permitted.)
   */
  public String getValue(String key, String def) {
    if (key != null) {
      String[] values = (String[]) modifiablePreferences.get(key);
      if (values != null && values.length > 0) {
        return values[0];
      } else {
        return def;
      }
    } else {
      throw new IllegalArgumentException(NAME_IS_NULL);
    }
  }//end of getValue(String name, String defaultValue)

  /**
   * Returns the String array value associated with the specified key in this 
   * preference.
   * <p>Returns the specified default if there is no value
   * associated with the key, or if the backing store is inaccessible.
   * @param key key for which associated value is to be returned.
   * @param def the value to be returned in the event that this preference node 
   * has no value associated with <code>key</code> or the associated value cannot 
   * be interpreted as a String array, or the backing store is inaccessible.
   * @return the String array value associated with <code>key</code>, or 
   * <code>def</code> if the associated value does not exist.
   * @exception IllegalArgumentException if <code>key</code> is <code>null</code>.
   * (A <code>null</code> value for <code>def</code> <i>is</i> permitted.)
   */
  public String[] getValues(String key, String[] def) {
    if (key != null) {
      String[] values = (String[]) modifiablePreferences.get(key);
      if (values != null && values.length > 0) {
        return values;
      } else {
        return def;
      }
    } else {
      throw new IllegalArgumentException(NAME_IS_NULL);
    }
  }//end of getValues(String name, String[] defaultValues)

  /**
   * Associates the specified String value with the specified key in this
   * preference.
   * <p>
   * The key cannot be <code>null</code>, but <code>null</code> values
   * for the value parameter are allowed.
   * @param key key with which the specified value is to be associated.
   * @param value value to be associated with the specified key.
   * @exception  ReadOnlyException if this preference cannot be modified for 
   * this request.
   * @exception IllegalArgumentException if key is <code>null</code>,
   * or <code>key.length()</code> or <code>value.length</code> are to long. 
   * The maximum length for key and value are implementation specific.
   */
  public void setValue(String key, String value) throws ReadOnlyException {
    if (key != null) {
      if (!isReadOnly(key)) {
        String[] values = new String[1];
        values[0] = value;
        modifiablePreferences.put(key, values);
      } else {
        throw new ReadOnlyException(READ_ONLY_MESSAGE);
      }
    } else {
      throw new IllegalArgumentException(NAME_IS_NULL);
    }
  }//end of setValue(String name, String value)

  /**
   * Associates the specified String array value with the specified key in this
   * preference.
   * <p>
   * The key cannot be <code>null</code>, but <code>null</code> values
   * in the values parameter are allowed.
   * @param key key with which the  value is to be associated.
   * @param values values to be associated with key.
   * @exception  IllegalArgumentException if key is <code>null</code>, or
   * <code>key.length()</code> is to long or <code>value.size</code> is to large.  
   * The maximum length for key and maximum size for value are implementation 
   * specific.
   * @exception ReadOnlyException if this preference cannot be modified for this 
   * request
   */
  public void setValues(String key, String[] values) throws ReadOnlyException {
    if (key != null) {
      if (!isReadOnly(key)) {
        modifiablePreferences.put(key, values);
      } else {
        throw new ReadOnlyException(READ_ONLY_MESSAGE);
      }
    } else {
      throw new IllegalArgumentException(NAME_IS_NULL);
    }
  }//end of setValues(String name, String[] values)

  /**
   * Returns all of the keys that have an associated value,
   * or an empty <code>Enumeration</code> if no keys are
   * available.
   * @return an Enumeration of the keys that have an associated value,
   * or an empty <code>Enumeration</code> if no keys are available.
   */
  public Enumeration getNames() {
    return Collections.enumeration(modifiablePreferences.keySet());
  }//end of getNames()

  /** 
   * Returns a <code>Map</code> of the preferences.
   * <p>
   * The values in the returned <code>Map</code> are from type
   * String array (<code>String[]</code>).
   * <p>
   * If no preferences exist this method returns an empty <code>Map</code>.
   * @return an immutable <code>Map</code> containing preference names as 
   * keys and preference values as map values, or an empty <code>Map</code>
   * if no preference exist. The keys in the preference map are of type String. 
   * The values in the preference map are of type String array (<code>String[]</code>).
   */
  public Map getMap() {
    //If no preferences exist this method returns an empty Map.
    Map result = new HashMap(modifiablePreferences);
    for(Iterator it = modifiablePreferences.keySet().iterator(); it.hasNext(); ) {
      String name = (String) it.next();
      String[] values = (String[]) result.get(name);
      if (values != null) {
        String[] newValues = new String[values.length];
        System.arraycopy(values, 0, newValues, 0, values.length);
        result.put(name, newValues);
      }
    }
    return result;
  }//end of getMap()

  /**
   * Resets or removes the value associated with the specified key.
   * <p>
   * The given key will be reset to the stored default.
   * <p>
   * If there is no default available the key will be removed.
   * @param  key to reset.
   * @exception  IllegalArgumentException if key is <code>null</code>.
   * @exception  ReadOnlyException if this preference cannot be modified for 
   * this request.
   */
  public void reset(String key) throws ReadOnlyException {
    if (key != null) {
      if (!isReadOnly(key)) { //preferenceTypes
        if (modifiablePreferences.keySet().contains(key)) {
          boolean remove = true;
          for (int i = 0; i < preferenceTypes.length; i++) {
            String inName = preferenceTypes[i].getName().get_value();
            if (inName.equals(key)) {
              ValueType[] valueTypes = preferenceTypes[i].getValue();
              if (valueTypes != null) {
                Vector stringValues = new Vector();
                for (int j = 0; j < valueTypes.length; j++) {
                  stringValues.add(valueTypes[j].get_value());
                }
                modifiablePreferences.put(key, stringValues.toArray(new String[stringValues.size()]));
              } else {
                modifiablePreferences.put(key, null);
              }
              remove = false;
              break;
            }
          }
          if (remove) {
            modifiablePreferences.remove(key);
          }
        } //else: nothing to reset
      } else {
        throw new ReadOnlyException(READ_ONLY_MESSAGE);
      }
    } else {
      throw new IllegalArgumentException(NAME_IS_NULL);
    }
  }//end of reset(String name)

  /**
   * Current implementation do not support persistent storage.
   * @exception IOException if changes cannot be written into the backend store.
   * @exception  ValidatorException if the validation performed by the
   * associated validator fails.
   */
  public void store() throws IOException, ValidatorException {
    LogContext.getLocation(LogContext.LOCATION_REQUESTS).traceWarning("ASJ.portlet.000046", 
    		"Preferences of portlet [{0}] portlet app [{1}] will not be stored in persistent storage :" +
            "store method is not implemented in PortletContainer but in Portal!", new Object[]{portletName, portletApplicationName}, null, null);
  }//end of store()

  /**
   * Returns the <code>PreferencesValidator</code> object for this Preferences object.
   * @return the <code>PreferencesValidator</code> object for this Preferences object.
   */
  public PreferencesValidator getPreferencesValidator() {
    return preferencesValidator;
  }//end of getPreferencesValidator()

  //Set runtime info

  private void loadPreferences(PreferenceType[] portletPreferenceTypes) {
    for (int i = 0; portletPreferenceTypes != null && i < portletPreferenceTypes.length; i++) {
      String name = portletPreferenceTypes[i].getName().get_value();
      if (portletPreferenceTypes[i].getReadOnly() != null &&
          (new Boolean(portletPreferenceTypes[i].getReadOnly().getValue())).booleanValue()) {
        readOnlyPreferences.add(name);
      }
      ValueType[] valueTypes = portletPreferenceTypes[i].getValue();
      if (valueTypes != null) {
        Vector stringValues = new Vector();
        for (int j = 0; j < valueTypes.length; j++) {
          stringValues.add(valueTypes[j].get_value());
        }
        modifiablePreferences.put(name, stringValues.toArray(new String[stringValues.size()]));
      } else {
        modifiablePreferences.put(name, null);
      }
    }
  }
}//end of class
