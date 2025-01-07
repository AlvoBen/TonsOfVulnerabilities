/*
 * Created on 2004.8.5
 *
 */
package com.sap.engine.cache.core.impl;

import java.io.Serializable;

import com.sap.util.cache.EvictionPeriod;
import com.sap.util.cache.spi.policy.ElementAttributes;

/**
 * @author petio-p
 *
 */
public class ElementAttributesImpl implements ElementAttributes, Serializable {
  
  static final long serialVersionUID = -7971834937378596912L;
  
  private EvictionPeriod renewOn;
  
  private int attrSize;

  private long absEviction;

  private long lastAccess;

  private long creation;

  private long ttl;

  private int size;

  /**
   * @param size
   * @param ttl
   */
  public ElementAttributesImpl(int size, int attrSize, long ttl, long absEviction) {
    this.size = size;
    this.attrSize = attrSize;
    this.ttl = ttl;
    this.absEviction = absEviction;
    creation = System.currentTimeMillis();
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementAttributes#getSize()
   */
  public int getSize() {
    return size;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementAttributes#getCreationTime()
   */
  public long getCreationTime() {
    return creation;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementAttributes#getLastAccessTime()
   */
  public long getLastAccessTime() {
    return lastAccess;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfigurationInfo#getTimeToLive()
   */
  public long getTimeToLive() {
    return ttl;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfigurationInfo#getAbsEvictionTime()
   */
  public long getAbsEvictionTime() {
    return absEviction;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.policy.ElementAttributes#getAttributesSize()
   */
  public int getAttributesSize() {
    return attrSize;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.policy.ElementAttributes#setAttributesSize(int)
   */
  public void setAttributesSize(int attrSize) {
    this.attrSize = attrSize;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.policy.ElementAttributes#setCreationTime(long)
   */
  public void setCreationTime(long creation) {
    this.creation = creation;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.policy.ElementAttributes#setLastAccessTime(long)
   */
  public void setLastAccessTime(long lastAccess) {
    this.lastAccess = lastAccess;    
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.spi.policy.ElementAttributes#setSize(int)
   */
  public void setSize(int size) {
    this.size = size;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfiguration#setAbsEvictionTime(long)
   */
  public void setAbsEvictionTime(long absEviction) {
    this.absEviction = absEviction;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfiguration#setTimeToLive(long)
   */
  public void setTimeToLive(long ttl) {
    this.ttl = ttl;    
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfiguration#setEvictionPeriod(com.sap.util.cache.EvictionPeriod)
   */
  public void setEvictionPeriod(EvictionPeriod period) {
    this.renewOn = period;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfiguration#setAbsEvictionTime(long, com.sap.util.cache.EvictionPeriod)
   */
  public void setAbsEvictionTime(long period, EvictionPeriod renew) {
    this.absEviction = period;
    this.renewOn = renew;
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfiguration#setAbsEvictionTime(long, int, int, int)
   */
  public void setAbsEvictionTime(long arg0, int arg1, int arg2, int arg3) {
    this.absEviction = arg0;
    this.renewOn = new EvictionPeriod(arg1, arg2, arg3);
  }

  /* (non-Javadoc)
   * @see com.sap.util.cache.ElementConfigurationInfo#getEvictionPeriod()
   */
  public EvictionPeriod getEvictionPeriod() {
    return renewOn;
  }

}
