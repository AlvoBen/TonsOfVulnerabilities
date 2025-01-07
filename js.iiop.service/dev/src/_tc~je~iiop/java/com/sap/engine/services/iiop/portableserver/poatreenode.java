/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */


package com.sap.engine.services.iiop.PortableServer;


import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;

import java.util.Vector;



public abstract class POATreeNode extends LocalObject implements org.omg.PortableServer.POA {

  public static final String ROOT_PAO_NAME ="RootPOA";

  private transient ORB orb;

  //POA attributes
  private String the_name;
  private POATreeNode the_parent;

  private Vector poa_childs = new Vector();

  /**
   * Constructs a Root POA instance.
   *
   * @param   orb   The Root POA owner.
   */
  POATreeNode(ORB orb) {
    this.orb = orb;
    the_name = ROOT_PAO_NAME;
    the_parent = null;
  }

  /**
   * Construct a POA instance with specified name and parent
   * @param   name    POA name
   * @param   parent
   * @throws  AdapterAlreadyExists
   */
  protected POATreeNode(String name, POATreeNode parent) throws  AdapterAlreadyExists {
    if (parent == null) {
     throw new AdapterAlreadyExists("RootPOA");
    }
    this.the_name =  name;
    this.the_parent = parent;
    this.orb = parent.orb;
    parent.addChild(this);
  }

  /**
   *
   * @return orb the instance of the ORB currently associated with this POA
   */
  public ORB orb() {
    return orb;
  }

 //POAs attributes
  public org.omg.PortableServer.POA the_parent() {
    return the_parent;
  }

  public String the_name() {
    return the_name;
  }

  public POA[] the_children() {
    return (POA[])poa_childs.toArray(new POAImpl[0]);
  }

  private synchronized void addChild(POATreeNode child) throws AdapterAlreadyExists {
    POA p = findChild(child.the_name());
    if(p == null){
      poa_childs.add(child);
    }else{
      throw new AdapterAlreadyExists(child.the_name() + " already exists as a child of " + this.the_name());
    }
  }

  protected POA findChild(String name) {
    poa_childs.elements();
    POA poa;
    String poa_name;
    for(int i = 0; i < poa_childs.size();i++){
      poa = (POAImpl)poa_childs.elementAt(i);
      poa_name = poa.the_name();
      if(poa_name.equals(name)){
        return poa;
      }
    }
    return null;
  }

  public void destroy(boolean etherealize_objects, boolean wait_for_completion) {
  }

}
