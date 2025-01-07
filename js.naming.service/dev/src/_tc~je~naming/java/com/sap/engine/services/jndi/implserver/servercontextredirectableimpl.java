/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */

package com.sap.engine.services.jndi.implserver;

import com.sap.engine.interfaces.cross.Redirectable;
import com.sap.engine.services.jndi.persistent.JNDIPersistentRepository;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.Constants;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import java.rmi.RemoteException;

/*
 * 
 * @author Elitsa-P
 * @version 6.30
 */
public class ServerContextRedirectableImpl extends ServerContextImpl implements Redirectable {


  /**
   * Constructor
   *
   * @param remote Denotes if the context will be used remotely
   * @throws java.rmi.RemoteException Thrown if a problem in initializing was encountered
   */
  public ServerContextRedirectableImpl(boolean remote) throws RemoteException {
    super(remote);
    redirectableContext = true;
  }

  /**
   * Constructor
   *
   * @param persistent JNDIPersistentRepository object - connection to the repository
   * @param parentObject - JNDIHandle of object linked to the root container
   * @param container - JNDIHandle of the root container
   * @param remote Denotes if the context will be used remotely
   * @throws java.rmi.RemoteException Thrown if a problem in initializing was encountered
   */
  public ServerContextRedirectableImpl(JNDIPersistentRepository persistent, JNDIHandle parentObject, JNDIHandle container, boolean remote, boolean onlyLookUpOperation) throws RemoteException {
    super(persistent, parentObject, container, remote, onlyLookUpOperation);
    redirectableContext = true;
  }

  /**
   * Constructor
   *
   * @param persistent JNDIPersistentRepository object - connection to the repository
   * @param parentObject JNDIHandle of object linked to the root container
   * @param container JNDIHandle of the root container
   * @param name Initial value of ((rootName == null)?(rootName = new CompositeName("")):rootName)
   * @param remote Denotes if the context will be used remotely
   * @throws java.rmi.RemoteException Thrown if a problem in initializing was encountered
   */
  public ServerContextRedirectableImpl(JNDIPersistentRepository persistent, JNDIHandle parentObject, JNDIHandle container, Name name, boolean remote) throws RemoteException {
    super(persistent, parentObject, container, name, remote);
    redirectableContext = true;
  }

  /**
   * Context lookup
   *
   * @param name Name to lookup
   * @return Object requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Object lookup(Name name, short type) throws javax.naming.NamingException, RemoteException {
    Object obj = null;
    obj = super.lookup(name, type);
    //if context will return an ServerContextImpl
    if (obj instanceof ServerContextInface) {
      ServerContextImpl returnedContext = (ServerContextImpl) obj;
      obj = new ServerContextRedirectableImpl(true); //always remote operation
      ((ServerContextRedirectableImpl)obj).reuse(returnedContext.persistent,
                                                returnedContext.parentObject,
                                                returnedContext.rootContainer,
                                                returnedContext.rootName,
                                                returnedContext.onlyLookUpAllowed);
    }
    return obj;
  }

  /**
   * DirContext createSubcontext
   *
   * @param name Name to create
   * @param attr Attributes to be rebound with
   * @return DirContext created
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public ServerContextInface createSubcontext(Name name, Attributes attr, short type) throws javax.naming.NamingException, RemoteException {
    ServerContextImpl returnedContext = (ServerContextImpl) super.createSubcontext(name, attr, type);
    ServerContextRedirectableImpl obj = new ServerContextRedirectableImpl(true); //always remote operation
    obj.reuse(returnedContext.persistent,
                returnedContext.parentObject,
                returnedContext.rootContainer,
                returnedContext.rootName,
                returnedContext.onlyLookUpAllowed);
    return obj;
  }


  /**
   * Context getNameInNameSpace
   *
   * @return Requested name
   */

  public String getIdentifier() {
    return Constants.OBJECT_FACTORY_REGISTRATION_NAME;
  }

}
