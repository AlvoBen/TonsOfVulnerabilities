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
import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PreferencesValidator;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

/**
 * Wraps the original PortletPreferences object received by the Portal in order
 * to do validation checks and use store() method implemeted by the portal.
 * 
 * @author diyan-y
 * @version 7.10
 */
public class PortletPreferencesWrapper implements PortletPreferences {

  /**
   * Defines that this portlet preferences ara created for invokation of render method.
   */
  public static final boolean RENDER = false;
  
  /**
   * Defines that this portlet preferences ara created for invokation of processAction method.
   */
  public static final boolean ACTION = true;
  
  private PortletPreferences portletPreferences = null;
  private PreferencesValidator preferencesValidator = null;
  /** Allows to store */
  private boolean modifiable = false;
  
  private static final String STORE_ERROR_MESSAGE = "Store method is not called from an Action request!";
  private static final String READ_ONLY_MESSAGE = "Portlet preference is read only.";
  private static final String NAME_IS_NULL = "The given name is null.";

  /**
   * Creates a new implementation of the Portletpreferences interfaced on top of
   * the specified portletPreferences object.
   *  
   * @param portletPreferences the wrapped PortletPreferences object.
   * @param preferencesValidator the PreferenveValidator object if such is 
   * specified for the preferences.
   * @param invokationScope defines the scope in which the PortletpreferencesWrapper is created.
   * the default values for this parameter are:
   * <ul>
   * <li>PortletPreferencesWrapper.RENDER - the PortletPreferencesWrapper object is created 
   * to be used in the scope Portlet.render() method. In this case the invokation of store() 
   * method will throw IllegalStateException. 
   * <li>PortletPreferencesWrapper.ACTION - the PortletPreferencesWrapper object is created 
   * to be used in the scope Portlet.processAction() method.
   * </ul>
   */
  public PortletPreferencesWrapper(PortletPreferences portletPreferences, 
      PreferencesValidator preferencesValidator, boolean invokationScope) {
    this.portletPreferences = portletPreferences;
    this.preferencesValidator = preferencesValidator;
    if (invokationScope) {
      modifiable = true;
    }
  }
  
  /**
   * Returns true, if the value of this key cannot be modified by the user.
   * @param key key for which the associated value is to be checked.
   * @return  false, if the value of this key can be changed, or if the key is not known.
   * @exception IllegalArgumentException if <code>key</code> is <code>null</code>.
   */
  public boolean isReadOnly(String key) {
    if (key == null) {
      throw new IllegalArgumentException(NAME_IS_NULL);
    }
    return portletPreferences.isReadOnly(key);
  }

  /**
   * Returns the first String value associated with the specified key of this preference.
   * If there is one or more preference values associated with the given key 
   * it returns the first associated value.
   * If there are no preference values associated with the given key, or the 
   * backing preference database is unavailable, it returns the given 
   * default value.
   * @param key key for which the associated value is to be returned.
   * @param defaultValue the value to be returned in the event that there is no value 
   * available associated with this <code>key</code>.
   * @return the value associated with <code>key</code>, or <code>defaultValue</code>
   * if no value is associated with <code>key</code>, or the backing store is inaccessible.
   * @exception IllegalArgumentException if <code>key</code> is <code>null</code>. 
   * (A <code>null</code> value for <code>defaultValue</code> <i>is</i> permitted.)
   * 
   * @see #getValues(String, String[])
   */
  public String getValue(String key, String defaultValue) {
    if (key == null) {
      throw new IllegalArgumentException(NAME_IS_NULL);  
    }
    return portletPreferences.getValue(key, defaultValue);
  }

  /**
   * Returns the String array value associated with the specified key in this 
   * preference.
   * <p>Returns the specified default if there is no value
   * associated with the key, or if the backing store is inaccessible.
   * @param key key for which associated value is to be returned.
   * @param defaultValues the value to be returned in the event that this
   * preference node has no value associated with <code>key</code> or the associated 
   * value cannot be interpreted as a String array, or the backing store is inaccessible.
   * @return the String array value associated with <code>key</code>, or 
   * <code>defaultValue</code> if the associated value does not exist.
   * @exception IllegalArgumentException if <code>key</code> is <code>null</code>.  
   * (A <code>null</code> value for <code>defaultValue</code> <i>is</i> permitted.)
   *
   * @see #getValue(String,String)
   */
  public String[] getValues(String key, String[] defaultValues) {
    if (key == null) {
      throw new IllegalArgumentException(NAME_IS_NULL);
    }
    return portletPreferences.getValues(key, defaultValues);
  }

  /**
   * Associates the specified String value with the specified key in this
   * preference.
   * <p>
   * The key cannot be <code>null</code>, but <code>null</code> values
   * for the value parameter are allowed.
   * @param key key with which the specified value is to be associated.
   * @param value value to be associated with the specified key.
   * @throws  ReadOnlyException if this preference cannot be modified for this request
   * @exception IllegalArgumentException if key is <code>null</code>,
   * or <code>key.length()</code> or <code>value.length</code> are to long. 
   *
   * @see #setValues(String, String[])
   */
  public void setValue(String key, String value) throws ReadOnlyException {
    if (key == null) {
      throw new IllegalArgumentException(NAME_IS_NULL);
    }
    if (isReadOnly(key)) {
      throw new ReadOnlyException(READ_ONLY_MESSAGE);
    }
    portletPreferences.setValue(key, value);
  }

  /**
   * Associates the specified String array value with the specified key in this
   * preference.
   * <p>
   * The key cannot be <code>null</code>, but <code>null</code> values
   * in the values parameter are allowed.
   * @param key key with which the  value is to be associated.
   * @param values values to be associated with key.
   * @exception IllegalArgumentException if key is <code>null</code>, or
   * <code>key.length()</code> is to long or <code>value.size</code> is to large.
   * @throws ReadOnlyException if this preference cannot be modified for this request.
   *
   * @see #setValue(String,String)
   */
  public void setValues(String key, String[] values) throws ReadOnlyException {
    if (key == null) {
      throw new IllegalArgumentException(NAME_IS_NULL);
    }
    if (isReadOnly(key)) {
      throw new ReadOnlyException(READ_ONLY_MESSAGE);
    }
    portletPreferences.setValues(key, values);
  }

  /**
   * Returns all of the keys that have an associated value,
   * or an empty <code>Enumeration</code> if no keys are
   * available.
   * @return an Enumeration of the keys that have an associated value,
   * or an empty <code>Enumeration</code> if no keys are available.
   */
  public Enumeration getNames() {
    return portletPreferences.getNames();
  }

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
    return portletPreferences.getMap();
  }

  /**
   * Resets or removes the value associated with the specified key.
   * <p>
   * If this implementation supports stored defaults, and there is such
   * a default for the specified preference, the given key will be 
   * reset to the stored default.
   * <p>
   * If there is no default available the key will be removed.
   * @param  key to reset
   * @exception  IllegalArgumentException if key is <code>null</code>.
   * @exception  ReadOnlyException if this preference cannot be modified for this request.
   */  
  public void reset(String key) throws ReadOnlyException {
    if (key == null) {
      throw new IllegalArgumentException(NAME_IS_NULL);
    }
    if (isReadOnly(key)) {
      throw new ReadOnlyException(READ_ONLY_MESSAGE);
    }
    portletPreferences.reset(key);
  }

  /**
   * Commits all changes made to the preferences via the 
   * <code>set</code> methods in the persistent store.
   * <P>
   * If this call returns succesfull, all changes are made
   * persistent. If this call fails, no changes are made
   * in the persistent store. This call is an atomic operation 
   * regardless of how many preference attributes have been modified.
   * <P>
   * All changes made to preferences not followed by a call 
   * to the <code>store</code> method are discarded when the 
   * portlet finishes the <code>processAction</code> method.
   * <P>
   * If a validator is defined for this preferences in the
   * deployment descriptor, this validator is called before
   * the actual store is performed to check wether the given
   * preferences are vaild. If this check fails a 
   * <code>ValidatorException</code> is thrown.
   *
   * @exception IOException if changes cannot be written into the backend store.
   * @exception ValidatorException if the validation performed by the
   * associated validator fails.
   * @exception IllegalStateException if this method is called inside a render call.
   *
   * @see  PreferencesValidator
   */
  public void store() throws IOException, ValidatorException {
    if (!modifiable) {
      throw new IllegalStateException(STORE_ERROR_MESSAGE);
    }
    //  validation if a validator is given
    if (preferencesValidator != null) {
      preferencesValidator.validate(this);
    }
    synchronized (this) {
      portletPreferences.store();
    }

  }

}
