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
package com.sap.engine.services.jndi.implserver;

import java.rmi.RemoteException;

import javax.naming.Binding;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;

import com.sap.engine.services.jndi.cluster.DirObject;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.JNDIHandleEnumeration;
import com.sap.engine.services.jndi.persistent.JNDIPersistentRepository;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Server-side implementation of Server Naming Enumeration Remote interface
 *
 * @author Panayot Dobrikov
 * @author Petio Petev
 * @version 4.00
 */
class ServerNamingEnumImpl implements ServerNamingEnum {

	private final static Location LOG_LOCATION = Location.getLocation(ServerNamingEnumImpl.class);

  /**
   * Constant representing undefined type of return object
   */
  public static final byte TYPE_UNDEFINED = 0;
  /**
   * Constant representing Binding type of return object
   */
  public static final byte TYPE_BINDING = 1;
  /**
   * Constant representing NameClassPair type of returning object
   */
  public static final byte TYPE_NAMECLASSPAIR = 2;
  /**
   * Flag representing wether there are more elements in the enumeration
   */
  private boolean moreElements = true;
  /**
   * Flag representing the state wether the context which uses this class is a remote one
   */
  private boolean remote;

  /**
   * Object containing the next eventually returned Binding
   */
  private Binding nextBinding = null;
  /**
   * Object containing the next eventually returned NameClassPair
   */
  private NameClassPair nextNameClassPair = null;
  /**
   * The absolute name of the starting context in the namespace
   */
  private Name rootName = null;
  /**
   * Handle enumeration to follow
   */
  JNDIHandleEnumeration je = null;
  /**
   * Repository to take objects from
   */
  JNDIPersistentRepository persistent = null;
  /**
   * Type of return object
   */
  private byte type = TYPE_UNDEFINED;
  /**
   * Boolean flag showing wether all operations are allowed in naming or only lookup
   */
  private boolean onlyLookupAllowed = false;

  private boolean redirectableContext = false;

  /**
   * Constructor
   *
   * @param persistent The repository to use when enumerating
   * @param je Handle Enumeration to follow
   * @param type Type of result (NameClassPair or Binding)
   * @param rootName Initial value of this.rootName
   * @param onlyLookupAllowed showing wether all operations are allowed in naming or only lookup
   * @throws java.rmi.RemoteException When problems occured while doing remote operations
   */
    public ServerNamingEnumImpl(JNDIPersistentRepository persistent,
                                JNDIHandleEnumeration je,
                                byte type,
                                Name rootName,
                                boolean onlyLookupAllowed,
                                boolean remote,
                                boolean redirectable) throws java.rmi.RemoteException {

    this.remote = remote;
    this.onlyLookupAllowed = onlyLookupAllowed;
    this.je = je;
    this.type = type;
    this.persistent = persistent;
    this.rootName = rootName;
    this.redirectableContext = redirectable;
    try {
      next();
    } catch (javax.naming.NamingException ne) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", ne);
      RemoteException re = new RemoteException("Exception during list/listBindings operation.", ne);
      throw re;
    }
  }

  /**
   * Returns next object qualified to be enumerated, when local operation invoked, only qualify objects on machines with particular sid.
   *
   * @return Next object (Binding or NameClassPair)
   * @throws java.rmi.RemoteException When problems occured while doing remote operations
   * @throws javax.naming.NamingException When problems occured while doing operations with the naming
   */
  public Object next() throws java.rmi.RemoteException, javax.naming.NamingException {
    if (!moreElements) {
      throw new java.util.NoSuchElementException("There are no more elements in the naming enumeration.");
    }

    do {
      if (je.hasMoreElements()) {
        try {
          JNDIHandle jh = null;
          String objName = null;
          String className = null;
          Object object = null;
          jh = je.nextObject(); // next Object
          objName = persistent.getObjectName(jh);

          if (objName != null) {
            byte[] dirObj = persistent.readObject(jh);
            JNDIHandle linkedContainer = persistent.getLinkedContainer(jh);

            if (linkedContainer != null) { // Context
              if (redirectableContext) {
                object = new ServerContextRedirectableImpl(persistent.getNewConnection(), jh, linkedContainer, ((Name) (rootName.clone())).add(objName), remote);
                ((ServerContextRedirectableImpl) object).onlyLookUpAllowed = this.onlyLookupAllowed;
              } else {
                object = new ServerContextImpl(persistent.getNewConnection(), jh, linkedContainer, ((Name) (rootName.clone())).add(objName), remote);
                ((ServerContextImpl) object).onlyLookUpAllowed = this.onlyLookupAllowed;
              }
              className = "javax.naming.Context";
            } else {
              object = dirObj;
              className = object != null ? DirObject.getClassName((byte[]) object) : null;
            }

            Binding oldBinding = nextBinding;
            NameClassPair oldNameClassPair = nextNameClassPair;
            nextBinding = new Binding(objName, null, object, true);
            nextNameClassPair = new NameClassPair(objName, className, true); //last parameter is "isRelative"

            switch (type) {
              case TYPE_BINDING: {
                return oldBinding;
              }
              case TYPE_NAMECLASSPAIR: {
                return oldNameClassPair;
              }
              default: {
                return null;
              }
            }
          }
        } catch (NamingException e) {
          LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
          NamingException re = new NamingException("Exception during list/listBindings operation.");
          re.setRootCause(e);
          throw re;
        }
      } else {
        je.closeEnumeration();
        moreElements = false;

        switch (type) {
          case TYPE_BINDING: {
            return nextBinding;
          }
          case TYPE_NAMECLASSPAIR: {
            return nextNameClassPair;
          }
          default: {
            return null;
          }
        }
      }
    } while (true);
  }

  /**
   * Returns a flag representing if there are more objects.
   *
   * @return A flag representing whether there are more objects qualified.
   * @throws java.rmi.RemoteException When problems occured while doing remote operations
   */
  public boolean hasMore() throws java.rmi.RemoteException, javax.naming.NamingException {
    return moreElements;
  }

  /**
   * Close
   *
   * @throws java.rmi.RemoteException When problems occured while doing remote operations
   */
  public void close() throws java.rmi.RemoteException {
    je.closeEnumeration();
  }


}

