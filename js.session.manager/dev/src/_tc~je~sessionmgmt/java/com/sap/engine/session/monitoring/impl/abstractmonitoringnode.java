package com.sap.engine.session.monitoring.impl;

import com.sap.engine.session.monitoring.MonitoringNode;

import java.util.HashMap;
import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * User: pavel-b
 * Date: 2006-8-14
 */
public abstract class AbstractMonitoringNode<T> implements MonitoringNode, Serializable {

  private static final long serialVersionUID = -1132143596241278491L;
  
  protected String path = null;


  protected String ID = null;
  protected String title = null;
  protected int destructionReason = -1;
  protected HashMap<String, Object> attributes = new HashMap<String, Object>();

  private transient WeakReference<T> referent;

  public AbstractMonitoringNode(String path) {
    this.path = path;
  }

  public AbstractMonitoringNode(String path, T referent) {
    this.path = path;
    this.referent = new WeakReference<T>(referent);
  }

  public String getID() {
    return ID;
  }

  public void setPath(String path){
    this.path = path;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Object getAttribute(String name) {
    return attributes.get(name);
  }

  public void setAttribute(String name, Object attribute) {
    attributes.put(name, attribute);
  }

  public void setDestructionReason(int reason) {
    destructionReason = reason;
  }

  public int getDestructionReason() {
    return destructionReason;
  }

  public void setReferent(T referent){
    this.referent = new WeakReference<T>(referent);
  }

  public T getReferent() {
    return referent != null ? referent.get() : null;
  }


}
