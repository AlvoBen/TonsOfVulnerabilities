/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.jmx.modelhelper;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.InvalidKeyException;

import com.sap.exception.standard.SAPIllegalArgumentException;
import com.sap.tc.logging.Location;

/**
 * An implementation of CompositeData that allows to set/change the values.
 */
public class ChangeableCompositeData implements CompositeData, Serializable {
  private static final long serialVersionUID = 1355154076781122134L;
  private static final Location LOCATION = Location.getLocation(ChangeableCompositeData.class);

  private final CompositeType type;
  private final HashMap data = new HashMap();

  /**
   * @param compositeType
   */
  public ChangeableCompositeData(CompositeType compositeType) {
    if (compositeType == null) {
      throw new SAPIllegalArgumentException(LOCATION, SAPIllegalArgumentException.PARAMETER_NULL, new Object[] { "compositeType" }); //$NON-NLS-1$
    }
    this.type = compositeType;
    Set keys = type.keySet();
    for (Iterator iter = keys.iterator(); iter.hasNext();) {
      data.put(iter.next(), null);
    }
  }

  /**
   * @param compositeData
   */
  public ChangeableCompositeData(CompositeData compositeData) {
    this.type = compositeData.getCompositeType();
    Set keys = type.keySet();
    String[] names = (String[]) keys.toArray(new String[keys.size()]);
    Object[] values = compositeData.getAll(names);
    for (int i = 0; i < names.length; i++) {
      data.put(names[i], values[i]);
    }
  }

  /**
   * @param key
   * @param value
   * @return
   */
  public Object set(String key, Object value) {
    if ((key == null) || (key.trim().equals(""))) { //$NON-NLS-1$
      throw new IllegalArgumentException("Argument key cannot be a null or empty String.");
    }
    if (!type.containsKey(key.trim())) {
      throw new InvalidKeyException("Argument key=\"" + key.trim() + "\" is not an existing item name for this CompositeData instance.");
    }
    return data.put(key, value);
  }

  /**
   * @see javax.management.openmbean.CompositeData#getCompositeType()
   */
  public CompositeType getCompositeType() {
    return type;
  }

  /**
   * @see javax.management.openmbean.CompositeData#get(java.lang.String)
   */
  public Object get(String key) {
    if ((key == null) || (key.trim().equals(""))) { //$NON-NLS-1$
      throw new IllegalArgumentException("Argument key cannot be a null or empty String.");
    }
    if (!data.containsKey(key.trim())) {
      throw new InvalidKeyException("Argument key=\"" + key.trim() + "\" is not an existing item name for this CompositeData instance.");
    }
    return data.get(key.trim());
  }

  /**
   * @see javax.management.openmbean.CompositeData#getAll(java.lang.String[])
   */
  public Object[] getAll(String[] keys) {
    if ((keys == null) || (keys.length == 0)) {
      return new Object[0];
    }
    Object[] results = new Object[keys.length];
    for (int i = 0; i < keys.length; i++) {
      results[i] = this.get(keys[i]);
    }
    return results;
  }

  /**
   * @see javax.management.openmbean.CompositeData#containsKey(java.lang.String)
   */
  public boolean containsKey(String key) {
    if ((key == null) || (key.trim().equals(""))) { //$NON-NLS-1$
      return false;
    }
    return data.containsKey(key);
  }

  /**
   * @see javax.management.openmbean.CompositeData#containsValue(java.lang.Object)
   */
  public boolean containsValue(Object value) {
    return data.containsValue(value);
  }

  /**
   * @see javax.management.openmbean.CompositeData#values()
   */
  public Collection values() {
    return Collections.unmodifiableCollection(data.values());
  }

  /**
   * Compares the specified <var>obj</var> parameter with this <code>ChangeableCompositeData</code> instance for equality. 
   * <p>
   * Returns <tt>true</tt> if and only if all of the following statements are true:
   * <ul>
   * <li><var>obj</var> is non null,</li>
   * <li><var>obj</var> also implements the <code>CompositeData</code> interface,</li>
   * <li>their composite types are equal</li>
   * <li>their contents, i.e. (name, value) pairs are equal. </li>
   * </ul>
   * This ensures that this <tt>equals</tt> method works properly for <var>obj</var> parameters which are
   * different implementations of the <code>CompositeData</code> interface, with the restrictions mentioned in the
   * {@link java.util.Collection#equals(Object) equals}
   * method of the <tt>java.util.Collection</tt> interface.
   * <br>&nbsp;
   * @param  obj  the object to be compared for equality with this <code>ChangeableCompositeData</code> instance;
   * 
   * @return  <code>true</code> if the specified object is equal to this <code>ChangeableCompositeData</code> instance.
   */
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    CompositeData other;
    try {
      other = (CompositeData) obj;
    }
    catch (ClassCastException e) {
      return false;
    }

    if (!this.getCompositeType().equals(other.getCompositeType())) {
      return false;
    }

    Map.Entry entry;
    boolean ok;
    for (Iterator iter = data.entrySet().iterator(); iter.hasNext();) {
      entry = (Map.Entry) iter.next();
      ok = (entry.getValue() == null ? other.get((String) entry.getKey()) == null : entry.getValue().equals(other.get((String) entry.getKey())));
      if (!ok) {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns the hash code value for this <code>ChangeableCompositeData</code> instance. 
   * <p>
   * The hash code of a <code>ChangeableCompositeData</code> instance is the sum of the hash codes
   * of all elements of information used in <code>equals</code> comparisons 
   * (ie: its <i>composite type</i> and all the item values). 
   * <p>
   * This ensures that <code> t1.equals(t2) </code> implies that <code> t1.hashCode()==t2.hashCode() </code> 
   * for any two <code>ChangeableCompositeData</code> instances <code>t1</code> and <code>t2</code>, 
   * as required by the general contract of the method
   * {@link Object#hashCode() Object.hashCode()}.
   * <p>
   * However, note that another instance of a class implementing the <code>CompositeData</code> interface
   * may be equal to this <code>ChangeableCompositeData</code> instance as defined by {@link #equals}, 
   * but may have a different hash code if it is calculated differently.
   *
   * @return  the hash code value for this <code>ChangeableCompositeData</code> instance
   */
  public int hashCode() {

    int result = 0;
    result += type.hashCode();
    Map.Entry entry;
    for (Iterator iter = data.entrySet().iterator(); iter.hasNext();) {
      entry = (Map.Entry) iter.next();
      result += (entry.getValue() == null ? 0 : entry.getValue().hashCode());
    }
    return result;
  }

  /**
   * Returns a string representation of this <code>ChangeableCompositeData</code> instance. 
   * <p>
   * The string representation consists of the name of this class, 
   * the string representation of the composite type of this instance, and the string representation of the contents
   * (ie list the itemName=itemValue mappings).
   * 
   * @return  a string representation of this <code>ChangeableCompositeData</code> instance
   */
  public String toString() {

    return new StringBuffer()
      .append(this.getClass().getName())
      .append("(compositeType=")
      .append(type.toString())
      .append(",contents=")
      .append(data.toString())
      .append(")")
      .toString();
  }

}
