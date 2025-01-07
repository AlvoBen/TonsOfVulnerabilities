package com.sap.engine.services.rmi_p4;

/**
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public class P4RemoteObjectInfo extends RemoteObjectInfo {

  static final long serialVersionUID = 3495888118242973035L;

  public transient Skeleton skeleton;
  transient String skeletonClassName;

}

