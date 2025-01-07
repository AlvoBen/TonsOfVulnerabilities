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
package com.sap.engine.services.jndi.implclient;

import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.*;
import javax.naming.Name;
import javax.naming.CompositeName;
import javax.naming.Reference;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Referenceable;
import javax.naming.NamingEnumeration;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.rmi.*;

import com.sap.engine.lib.util.ConcurrentArrayObject;
import com.sap.engine.lib.util.ConcurrentHashMapObjectObject;
import com.sap.engine.services.jndi.Constants;
import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.engine.services.jndi.RemoteServiceReference;
import com.sap.engine.services.jndi.cluster.DirObject;
import com.sap.engine.services.jndi.implserver.ModifyAttributes;
import com.sap.engine.services.jndi.implserver.SearchFilter;
import com.sap.engine.services.jndi.implserver.ServerContextInface;
import com.sap.engine.services.jndi.implserver.ServerNamingEnum;
import com.sap.engine.services.jndi.persistent.*;

import javax.naming.ContextNotEmptyException;
import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.OperationNotSupportedException;

import com.sap.engine.services.jndi.persistent.exceptions720.JNDIException;
import com.sap.engine.services.rmi_p4.P4ConnectionException;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

//import com.inqmy.frame.container.log.LogContext;

/**
 * A service provider that implements a composite namespace via RMI.
 *
 * @author Panayot Dobrikov
 * @author Petio Petev, Elitsa Pancheva
 * @version 4.00
 */
public class ClientContext implements DirContext, java.io.Serializable {

  private final static Location LOG_LOCATION = Location.getLocation(ClientContext.class);

  /**
   * serial version UID
   */
  static final long serialVersionUID = 2116043629778868483L;

  private static final boolean DUMP_REFERENCES = false;
  /**
   * Local Environment
   */
  private Hashtable localEnvironment;
  public ClassLoader commonLoader = null; //$JL-SER$

  /**
   * Name, representing the name of the context, relative to root context
   */
  protected Name fullNameInSpace = new CompositeName();
  /**
   * Pointer to the Remote ServerContextInface
   */
  protected ServerContextInface remoteContext; //$JL-SER$
  /**
   * Contains the user's wanted altServerID for local operation during runtime
   */
  //  protected String altServerID = null;
  private static ConcurrentHashMapObjectObject referenceHash;
  public ConcurrentHashMapObjectObject remoteReferenceHash = null;
  //  private boolean destructive = false;
  //  private byte[] destructionData = null;
  public RemoteObjectReferenceFactory referenceFactory = null; //$JL-SER$
  /**
   * Contains the syntax properties of the context
   */
  static private ConcurrentHashMapObjectObject syntax = new ConcurrentHashMapObjectObject();

  /**
   * Block for initialization
   */
  static {
    syntax.put("jndi.syntax.direction", "left_to_right");
    syntax.put("jndi.syntax.ignorecase", "false");
    syntax.put("jndi.syntax.separator", "/");

    if (referenceHash == null) {
      referenceHash = new ConcurrentHashMapObjectObject();
    }
  }

  /**
   * Flags if it runs on server
   */
  private boolean runOnServer = false;
  /**
   * Flag if this is the last object
   */
  private boolean lastobj = true;
  /**
   * The LoginContext for this principle
   */
  public LoginHelper loginContext = null; //$JL-SER$

  /**
   * Protected method that parses a String to a Name
   *
   * @param name Name as a string
   * @return Composite name
   * @throws NamingException Thrown if a name cannot be composed
   */
  protected static Name parse(String name) throws javax.naming.NamingException {
    try {
      Schema sch = new Schema(name);
      return sch;
    } catch (javax.naming.NamingException ne) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Incorrect name parameter " + name + " in naming operation.", ne);
      }
      NamingException neEx = new NamingException("Incorrect name parameter " + name + " in naming operation.");
      neEx.setRootCause(ne);
      throw neEx;
    }
  }

  /**
   * Constructor
   *
   * @param environment Initial environment of the context
   * @param remoteContext The remote ServerContextInface
   * @param name The initial fullNameInSpace value
   * @param runOnServer Constructor invoked from server ?
   * @throws NamingException Thrown if initialization fails
   */
  public ClientContext(Hashtable environment, ServerContextInface remoteContext, Name name, boolean runOnServer, RemoteObjectReferenceFactory refrenceFactory, LoginHelper loginContext) throws javax.naming.NamingException {
    this.runOnServer = runOnServer;
    this.localEnvironment = (environment != null) ? (Hashtable) (environment.clone()) : null;
    this.remoteContext = remoteContext;
    fullNameInSpace = name;
    this.referenceFactory = refrenceFactory;
    this.loginContext = loginContext;
  }

  /**
   * Constructor
   *
   * @param environment Initial environment of the context
   * @param remoteContext The remote ServerContextInface
   * @param runOnServer Constructor invoked from server ?
   * @throws NamingException Thrown if initialization fails
   */
  public ClientContext(Hashtable environment, ServerContextInface remoteContext, boolean runOnServer, RemoteObjectReferenceFactory refrenceFactory, LoginHelper loginCtx) throws javax.naming.NamingException {
    this.runOnServer = runOnServer;
    this.localEnvironment = (environment != null) ? (Hashtable) (environment.clone()) : null;
    this.remoteContext = remoteContext;
    //    fullNameInSpace = new CompositeName("");
    fullNameInSpace = null;
    this.referenceFactory = refrenceFactory;
    this.loginContext = loginCtx;
  }

  /**
   * If the name is something like "a/b/c/" change it to a/b/c
   *
   * @param name The name to be transformed
   * @return Transformed name
   */
  protected Name transformName(Name name) {
    if (name.size() == 0) {
      return name;
    } else {
      while ((name.size() > 0) && (name.get(name.size() - 1).trim().equals(""))) {
        try {
          name.remove(name.size() - 1);
        } catch (Exception ex) {
          LOG_LOCATION.traceThrowableT(Severity.PATH, "", ex);
          break;
        }
      }

      return name;
    }
  }

  /**
   * Check for operationType of operation (the first component of the name)
   *
   * @param name The name to be used to detirmine the operationType of the operation
   * @return The operationType of the operation
   */
  protected short checkOperation(Name name) {
    transformName(name);
    try {

      if (name.size() == 0) {
        return Constants.NOT_REPLICATED_OPERATION;
      }

      String tempString = name.get(0).trim();

      if (tempString.length() == 0) {
        name.remove(0);
        return Constants.NOT_REPLICATED_OPERATION;
      }

      String toReplicate = (String) localEnvironment.get("Replicate");

      if (toReplicate != null && toReplicate.equals("true")) {
        //System.out.println("locEnv - " + localEnvironment.get("REPLICATE"));
        return Constants.REPLICATED_OPERATION;
      }

      if (tempString.equals("^") || tempString.equals("+")) {
        name.remove(0);
        return Constants.NOT_REPLICATED_OPERATION;
      }
    } catch (javax.naming.InvalidNameException e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Incorrect name parameter " + name + " in naming operation.", e);
      }
    }
    return Constants.NOT_REPLICATED_OPERATION;
  }

  /**
   * Verification for correct name - check whether has empty component of Name
   *
   * @param name The name to be checked
   * @throws NamingException Thrown if invalid name is passed
   */
  protected void verifyName(Name name) throws javax.naming.NamingException {
    if ((name.size() > 0) && (name.get(0).trim().equals(""))) {
      try {
        name.remove(0);
      } catch (javax.naming.InvalidNameException ine) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Incorrect name parameter " + name + " in naming operation.", ine);
        }
        InvalidNameException ineEx = new InvalidNameException("Incorrect name parameter " + name + " in naming operation.");
        ineEx.setRootCause(ine);
        throw ineEx;
      }
    }

    for (int i = 0; i < name.size(); i++) {
      if ((name.get(i)).trim().equals("")) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Incorrect name parameter " + name + " in naming operation.");
        }
        throw new InvalidNameException("Incorrect name parameter " + name + " in naming operation.");
      }
    }
  }

  /**
   * Context lookup using RMI Context
   *
   * @param name The name to be looked up
   * @return The looked up object
   * @throws NamingException Thrown if NamingException occurs
   */
  public Object lookup(Name name) throws javax.naming.NamingException {
    //    try {
    if (Schema.class.isAssignableFrom(name.getClass())) {
      if (((Schema) name).isCname) {
        //          System.out.println("CosNaming");

        return lookupFromCosNaming(((Schema) name).getName());
      }
    }
    //    } catch (javax.naming.NamingException ne) {
    //      throw new NamingException(NamingException.CANNOT_LOOKUP, new Object[] {name}, ne);
    //    }

    name = transformName(name);
    verifyName(name);
    try {
      if (remoteContext == null) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No connection with the server.");
        }
        throw new NamingException("No connection with the server.");
      }

      // first lookup in LROTable
      DirObject obj = null;
      String fullPath = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);
      obj = (DirObject) RemoteSerializator.LROTable.get(fullPath);

      if (obj != null) {
        return obj.getObject();
      }

      short operationType = checkOperation(name);
      // get byte[] here
      Object object = null;
      object = remoteContext.lookup(name, operationType);

      //      } catch (NamingException e) {
      //        System.out.println(" .............Exception in Lookup............ "  + name);
      //        e.printStackTrace();
      //        throw e;
      //      }
      // check whether is instance of Context
      if (object instanceof ServerContextInface) {
        if (localEnvironment != null) {
          if (Constants.KEEP_ABSOLUTE_NAME) {
            Name fullName = (Name) (((fullNameInSpace == null) ? (fullNameInSpace = new CompositeName("")) : fullNameInSpace).clone());
            if (name.size() != 0) {
              fullName.addAll(name);
            }

            object = new ClientContext((Hashtable) (localEnvironment.clone()), (ServerContextInface) object, fullName, this.runOnServer, this.referenceFactory, this.loginContext);
          } else {
            object = new ClientContext((Hashtable) (localEnvironment.clone()), (ServerContextInface) object, this.runOnServer, this.referenceFactory, this.loginContext);
          }
        } else {
          if (Constants.KEEP_ABSOLUTE_NAME) {
            Name fullName = (Name) (((fullNameInSpace == null) ? (fullNameInSpace = new CompositeName("")) : fullNameInSpace).clone());

            if (name.size() != 0) {
              fullName.addAll(name);
            }

            object = new ClientContext(new Hashtable(), (ServerContextInface) object, fullName, this.runOnServer, this.referenceFactory, this.loginContext);
          } else {
            object = new ClientContext(new Hashtable(), (ServerContextInface) object, this.runOnServer, this.referenceFactory, this.loginContext);
          }
        }

        return object;
      }

      // deserialization
      if (object != null) {
        try {
          byte[] data = (byte[]) object;
          DirObject dobj = deserializeDirObject(data);

          if (dobj != null) {
            object = dobj.getObject();
          } else {
            object = null;
          }
        } catch (Exception e) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during lookup operation of object with name " + name + ".", e);
          }
          NamingException ne = new NamingException("Exception during lookup operation of object with name " + name + ".");
          ne.setRootCause(e);
          throw ne;
        }

        if (object instanceof Reference) {
          Object temp = null;
          try {

            if (localEnvironment.get("server") != null) {
              Thread thisThread = Thread.currentThread();
              ClassLoader oldClassLoader = thisThread.getContextClassLoader();
              //check if a classloader name is set in the Reference
              String classLoaderName = ((Reference) object).getFactoryClassLocation();
              ClassLoader loader = JNDIFrame.getClassLoader(classLoaderName != null ? classLoaderName : "");

              if (classLoaderName != null && loader != null) { //try to use it
                thisThread.setContextClassLoader(loader);

                try {
                  temp = NamingManager.getObjectInstance(object, name, this, localEnvironment);

                  if (temp != null && temp == object) { // first loader failed to load the ObjectFactory
                    // try with the original loader
                    thisThread.setContextClassLoader(oldClassLoader);
                    temp = NamingManager.getObjectInstance(object, name, this, localEnvironment);

                    if (temp != null && temp == object) { // second loader failed to load the factory
                      // try with naming classloader
                      ClassLoader namingClassLoader = this.getClass().getClassLoader();
                      thisThread.setContextClassLoader(namingClassLoader);
                      temp = NamingManager.getObjectInstance(object, name, this, localEnvironment);

                      if (temp != null && temp == object) { // last loader failed to load the factory => log WARNING and return the Reference as it is
                        if (LOG_LOCATION.beInfo()) {
                          LOG_LOCATION.infoT("Reference cannot be resolved. Name: " + name + ", loader name: " + ((Reference) object).getFactoryClassLocation() + ", ObjectFactory name: " + ((Reference) object).getFactoryClassName() + ".");
                        }
                        if (LOG_LOCATION.beInfo()) {
                          LOG_LOCATION.infoT("Reference cannot be resolved. Name: " + name + ", loader name: " + ((Reference) object).getFactoryClassLocation() + ", ObjectFactory name: " + ((Reference) object).getFactoryClassName() + ".");
                        }
                      } else {
                        if (temp == null) {
                          if (LOG_LOCATION.beInfo()) {
                            LOG_LOCATION.infoT("null is returned after resolving the Reference. Name: " + name + ", loader name: " + thisThread.getContextClassLoader() + ", ObjectFactory name: " + ((Reference) object).getFactoryClassName() + ".");
                          }
                        }
                        object = temp;
                      }
                    } else {
                      if (temp == null) {
                        if (LOG_LOCATION.beInfo()) {
                          LOG_LOCATION.infoT("null is returned after resolving the Reference. Name: " + name + ", loader name: " + thisThread.getContextClassLoader() + ", ObjectFactory name: " + ((Reference) object).getFactoryClassName() + ".");
                        }
                      }
                      object = temp;
                    }
                  } else {
                    if (temp == null) {
                      if (LOG_LOCATION.beInfo()) {
                        LOG_LOCATION.infoT("null is returned after resolving the Reference. Name: " + name + ", loader name: " + thisThread.getContextClassLoader() + ", ObjectFactory name: " + ((Reference) object).getFactoryClassName() + ".");
                      }
                    }
                    object = temp;
                  }
                } finally {
                  thisThread.setContextClassLoader(oldClassLoader);
                }

              } else { //no loader is set in the Reference
                try {
                  // try to resolve reference with the client's class loader
                  temp = NamingManager.getObjectInstance(object, name, this, localEnvironment);

                  if (temp != null && temp == object) { // the ObjectFactory can not be loaded
                    // try to resolve reference with the naming class loader
                    ClassLoader newClassLoader = this.getClass().getClassLoader();

                    if (DUMP_REFERENCES) {
                      System.out.println("======================================================================");
                      System.out.println("THIS THREAD        : " + thisThread);
                      System.out.println("----------------------------------------------------------------------");
                      System.out.println("FACTORY NAME       : " + ((Reference) object).getFactoryClassName());
                      System.out.println("FACTORY LOCATION   : " + ((Reference) object).getFactoryClassLocation());
                      System.out.println("----------------------------------------------------------------------");
                      System.out.println("NEW CLASS LOADER   : " + newClassLoader);
                      System.out.println("OLD CLASS LOADER   : " + oldClassLoader);
                      System.out.println("----------------------------------------------------------------------");
                    } //$JL-SYS_OUT_ERR$

                    thisThread.setContextClassLoader(newClassLoader);
                    temp = NamingManager.getObjectInstance(object, name, this, localEnvironment);

                    if (DUMP_REFERENCES) {
                      System.out.println("OBJECT CLASS NAME  : " + object.getClass().getName());
                      System.out.println("OBJECT TOSTRING    : " + object.toString());
                      System.out.println("OBJECT CLASS LOADER: " + object.getClass().getClassLoader());
                      System.out.println("======================================================================");
                    } //$JL-SYS_OUT_ERR$

                    if (temp != null && temp == object) { // last loader failed to load the factory => log WARNING and return the Reference as it is
                      if (LOG_LOCATION.beInfo()) {
                        LOG_LOCATION.infoT("Reference cannot be resolved. Name: " + name + ", loader name: " + ((Reference) object).getFactoryClassLocation() + ", ObjectFactory name: " + ((Reference) object).getFactoryClassName() + ".");
                      }
                      if (LOG_LOCATION.beInfo()) {
                        LOG_LOCATION.infoT("Reference cannot be resolved. Name: " + name + ", loader name: " + ((Reference) object).getFactoryClassLocation() + ", ObjectFactory name: " + ((Reference) object).getFactoryClassName() + ".");
                      }
                    } else {
                      if (temp == null) {
                        if (LOG_LOCATION.beInfo()) {
                          LOG_LOCATION.infoT("null is returned after resolving the Reference. Name: " + name + ", loader name: " + thisThread.getContextClassLoader() + ", ObjectFactory name: " + ((Reference) object).getFactoryClassName() + ".");
                        }
                      }
                      object = temp;
                    }
                  } else {
                    if (temp == null) {
                      if (LOG_LOCATION.beInfo()) {
                        LOG_LOCATION.infoT("null is returned after resolving the Reference. Name: " + name + ", loader name: " + thisThread.getContextClassLoader() + ", ObjectFactory name: " + ((Reference) object).getFactoryClassName() + ".");
                      }
                    }
                    object = temp;
                  }
                } finally {
                  thisThread.setContextClassLoader(oldClassLoader);
                }
              }
              //              object = lookup(((javax.naming.Reference)object).getClassName());
            } else {
              object = NamingManager.getObjectInstance(object, name, this, localEnvironment);
            }
          } catch (Exception e) {
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during lookup operation of object with name " + name + ", cannot resolve object reference.", e);
            }
            NamingException ne = new NamingException("Exception during lookup operation of object with name " + name + ", cannot resolve object reference.");
            ne.setRootCause(e);
            throw ne;
          }
        }

        if (object instanceof RemoteServiceReference) {
          RemoteServiceReference rSR = null;
          try {
            rSR = (RemoteServiceReference) object;
            object = null;
            object = rSR.getServiceInterface();
            // local reference
            if (object == null) {
              if ((localEnvironment.get("server") != null) && (localEnvironment.get("force_remote") == null)) {
                if (rSR.isService()) {
                  object = JNDIFrame.containerContext.getObjectRegistry().getServiceInterface(name.get(name.size() - 1));
                } else {
                  object = JNDIFrame.containerContext.getObjectRegistry().getProvidedInterface(name.get(name.size() - 1));
                }
              }
            }
          } catch (Exception e) {
            LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
            try {
              if (object == null) {
                if ((localEnvironment.get("server") != null) && (localEnvironment.get("force_remote") == null)) {
                  if (rSR.isService()) {
                    object = JNDIFrame.containerContext.getObjectRegistry().getServiceInterface(name.get(name.size() - 1));
                  } else {
                    object = JNDIFrame.containerContext.getObjectRegistry().getProvidedInterface(name.get(name.size() - 1));
                  }
                }
              }
            } catch (Exception e2) {
              if (LOG_LOCATION.beInfo()) {
                LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during lookup operation of object with name " + name + ", cannot resolve object reference.", e);
              }
              object = null;
            }
          }
        }
      }

      Object result = null;
      try {
        result = NamingManager.getObjectInstance(object, name, this, localEnvironment);
      } catch (Exception e) {
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      }
      if (result == null) {
        try {
          result = DirectoryManager.getObjectInstance(object, name, this, localEnvironment);
        } catch (Exception e) {
          LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
        }
      }

      return result != null ? result : object;

    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing lookup operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("Connection with the server lost while performing lookup operation of name " + name + ".");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing lookup operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("Connection with the server lost while performing lookup operation of name " + name + ".");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform lookup operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform lookup operation of name " + name + ".");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during lookup operation of object with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during lookup operation of object with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * Context lookup using RMI Context
   *
   * @param name The name to be looked up.
   * @return The looked up object
   * @throws NamingException Thrown if NamingException occurs
   */
  public Object lookup(String name) throws javax.naming.NamingException {
    try {
      return this.lookup(parse(name));
    } catch (javax.naming.NamingException e) {
      // Excluding this catch block from JLIN $JL-EXC$ since this exception has been already logged at a lower level.
      // Please do not remove this comment!
      javax.naming.NamingException ne = e;
      try {
        return this.lookup(parse("java:" + name));
      } catch (javax.naming.NamingException e1) {
        String ex = e1.getExplanation();
        String formater1 = JNDILogConstants.PATH_TO_OBJECT_DOES_NOT_EXISTS + "java:";
        formater1 = formater1.substring(0, formater1.indexOf("java:") + 5); // remove the ending of this error message

        String formater2 = JNDILogConstants.NAME_NOT_FOUND_IN_LOOKUP + "java:";
        formater2 = formater2.substring(0, formater2.indexOf("java:") + 5); // remove the ending of this error message

        if (ex.indexOf(formater1) != -1 || ex.indexOf(formater2) != -1) {
          throw ne;
        }
        throw e1;
      }
    }
  }

  private Object lookupFromCosNaming(String name) throws javax.naming.NamingException {
    //    System.out.println("\n -----GG>>Lookup from COSNaming:" + name);
    if (LOG_LOCATION.bePath()) {
      LOG_LOCATION.pathT("Name " + name + " will be looked up from the cosnaming.");
    }
    String protokol = "";
    try {
      //      System.out.println("\n -----GG>>indexOf(':') +1 :" + (name.indexOf(':') + 1));
      //      System.out.println("\n -----GG>>name.ndexOf(':', (name.indexOf(':') + 1)) :" + name.indexOf(':', (name.lastIndexOf(':') + 1)));
      protokol = name.substring(name.indexOf(':') + 1, name.indexOf(':', (name.indexOf(':') + 1)));
      //      System.out.println("\n -----GG>>Protokol Name:" + protokol);
    } catch (Exception e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during lookup operation of object with name " + name + ".", e);
      }
    }

    if ((!protokol.equals("iiop")) && (!protokol.equals(""))) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.infoT("Incorrect CORBA name: " + name + ".");
      }
      throw new NamingException("Incorrect CORBA name: " + name + ".");
    }

    Object obj = null;
    try {
      String url = name.substring(name.lastIndexOf('@') + 1, name.lastIndexOf('#'));
      String objName = name.substring(name.lastIndexOf('#') + 1);
      Hashtable prop = new Hashtable(5);
      prop.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.CosNamingInitialContextFactoryImpl");
      prop.put(Context.PROVIDER_URL, url);
      Context ctx = new InitialContext(prop);
      obj = ctx.lookup(objName);
    } catch (javax.naming.NamingException ne) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during lookup operation of object with name " + name + ".", ne);
      }
      NamingException neEx = new NamingException("Exception during lookup operation of object with name " + name + ".");
      neEx.setRootCause(ne);
      throw ne;
    }
    return obj;
  }

  /**
   * DirContext bind using RMI Context
   *
   * @param name The name to be bound to the object
   * @param object The object, the name will be bind to
   * @param attributes The attributes which will be set to the object
   * @throws NamingException Thrown if NamingException occurs
   */
  public void bind(String name, Object object, Attributes attributes) throws javax.naming.NamingException {
    bind(parse(name), object, attributes);
  }

  /**
   * DirContext bind using RMI Context
   *
   * @param name The name to be bound to the object
   * @param object The object, the name will be bind to
   * @param attributes The attributes which will be set to the object
   * @throws NamingException Thrown if NamingException occurs
   */
  public void bind(Name name, Object object, Attributes attributes) throws javax.naming.NamingException {
    name = transformName(name);

    // verify name for correctness
    if (name.size() == 0) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.infoT("Cannot bind an empty name.");
      }
      throw new InvalidNameException("Cannot bind an empty name.");
    }

    verifyName(name);

    Object temp = prepareStateToBind(name, object, attributes);
    if (temp instanceof DirStateFactory.Result) {
      object = ((DirStateFactory.Result) temp).getObject();
      attributes = ((DirStateFactory.Result) temp).getAttributes();
    } else {
      object = temp;
    }

    short operationType = checkOperation(name);
    try {
      if (object instanceof Referenceable) {
        Object ref = ((Referenceable) object).getReference();

        if (ref != null) {
          object = ref;
        }
      }

      if (object instanceof PortableRemoteObject || object instanceof UnicastRemoteObject || object instanceof Remote) {
        if (operationType == Constants.REPLICATED_OPERATION) {
          operationType = Constants.REMOTE_REPLICATED_OPERATION;
        }
      }

      if (remoteContext == null) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No connection with the server.");
        }
        throw new NamingException("No connection with the server.");
      }

      // serialization
      if (attributes == null) {
        attributes = new BasicAttributes();
      }

      DirObject dobj = new DirObject(attributes, object);
      byte[] data = null;
      try {
        data = serializeDirObject(dobj);

        if (data == null) { // object is not serializable
          String fullName = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);

          if (!RemoteSerializator.LROTable.containsKey(fullName)) {
            try {
              remoteContext.lookup(name, operationType);
              //System.out.println("object found : " + fullName + " : " + o);
            } catch (javax.naming.NamingException ex) {
              if (ex instanceof com.sap.engine.services.jndi.persistent.exceptions.NameNotFoundException) {
                if (((com.sap.engine.services.jndi.persistent.exceptions.NameNotFoundException) ex).missingPathComponent) {
                  throw ex; //exceptions different from Object not found
                }
              } else {
                if (ex instanceof com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException) {
                  if (((com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException) ex).missingPathComponent) {
                    throw ex; //exceptions different from Object not found
                  }
                } else {
                  throw ex; //exceptions different from Object not found
                }
              }

              RemoteSerializator.LROTable.put(fullName, dobj);
              if (LOG_LOCATION.bePath()) {
                LOG_LOCATION.pathT("Object with name " + fullName + " will be put in the LROTable as a result of bind operation.");
              }
              // System.out.println("<BIND> put name:" + fullName in LROTable");
              return;
            }
            if (LOG_LOCATION.bePath()) {
              LOG_LOCATION.pathT("Object with name " + name + " is already bound.");
            }
            throw new NameAlreadyBoundException("Object with name " + name + " is already bound.");
          } else {
            if (LOG_LOCATION.bePath()) {
              LOG_LOCATION.pathT("Object with name " + name + " is already bound.");
            }
            throw new NameAlreadyBoundException("Object with name " + name + " is already bound.");
          }
        } else { //object is serializable, first check in LROTable if object with such a name already exists
          String fullName = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);

          if (RemoteSerializator.LROTable.containsKey(fullName)) {
            if (LOG_LOCATION.bePath()) {
              LOG_LOCATION.pathT("Object with name " + name + " is already bound.");
            }
            throw new NameAlreadyBoundException("Object with name " + name + " is already bound.");
          }
        }
      } catch (P4ConnectionException pce) {
        throw pce;
      } catch (ServerException nsoe) {
        throw nsoe;
      } catch (javax.naming.NamingException ex) {
        throw ex;
      } catch (Exception e) { // if serializeDirObject(dirObject) throws
        // exception dirObject will be put in LROTable
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
        String fullName = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);

        if (!RemoteSerializator.LROTable.containsKey(fullName)) {
          try {
            remoteContext.lookup(parse(fullName), operationType);
            //System.out.println("object found : " + fullName + " : " + o);
          } catch (P4ConnectionException pce) {
            throw pce;
          } catch (ServerException nsoe) {
            throw nsoe;
          } catch (javax.naming.NamingException ex) {
            if (ex instanceof com.sap.engine.services.jndi.persistent.exceptions.NameNotFoundException) {
              if (((com.sap.engine.services.jndi.persistent.exceptions.NameNotFoundException) ex).missingPathComponent) {
                throw ex; //exceptions different from Object not found
              }
            } else {
              if (ex instanceof com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException) {
                if (((com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException) ex).missingPathComponent) {
                  throw ex;
                }
              } else {
                if (!Constants.NON_SERIALIZABLE_REBIND_OLD_BEHAVIOUR) {
                  throw ex; //exceptions different from Object not found
                }
              }
            }
            RemoteSerializator.LROTable.put(fullName, dobj);
            if (LOG_LOCATION.bePath()) {
              LOG_LOCATION.pathT("Object with name " + fullName + " will be put in the LROTable as a result of bind operation.");
            }
            //            System.out.println("<BIND2> put name:" + fullName + " in LROTable");
            return;
          }

          if (LOG_LOCATION.bePath()) {
            LOG_LOCATION.pathT("Object with name " + name + " is already bound.");
          }
          throw new NameAlreadyBoundException("Object with name " + name + " is already bound.");
        } else {
          if (LOG_LOCATION.bePath()) {
            LOG_LOCATION.pathT("Object with name " + name + " is already bound.");
          }
          throw new NameAlreadyBoundException("Object with name " + name + " is already bound.");
        }
      }
      remoteContext.bind(name, data, operationType, lastobj);

      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Bind operation finished successfully over object/context with name " + name + ".");
      }

      if (lastobj) {
        if (remoteReferenceHash != null) {
          addReference(name, remoteReferenceHash);
        }
      }
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing bind operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing bind operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (com.sap.engine.services.rmi_p4.P4RuntimeException ex) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", ex);
      NamingException ne = new NamingException("Protocol exception. Cannot perform the operation.");
      ne.setRootCause(ex);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform bind operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform bind operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during bind operation of object with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during bind operation of object with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * DirContext bind using RMI Context
   *
   * @param name The name to be bound to the object
   * @param object The object, the name will be bind to
   * @throws NamingException Thrown if NamingException occurs
   */
  public void bind(String name, Object object) throws javax.naming.NamingException {
    bind(name, object, null);
  }

  /**
   * DirContext bind using RMI Context
   *
   * @param name The name to be bound to the object
   * @param object The object, the name will be bind to
   * @throws NamingException Thrown if NamingException occurs
   */
  public void bind(Name name, Object object) throws javax.naming.NamingException {
    bind(name, object, null);
  }

  /**
   * Context rebind using RMI Context
   *
   * @param name The name to be bound to the new object
   * @param object The new object, the name will be bind to
   * @throws NamingException Thrown if NamingException occurs
   */
  public void rebind(Name name, Object object) throws javax.naming.NamingException {
    rebind(name, object, null);
  }

  /**
   * Context rebind using RMI Context
   *
   * @param name The name to be bound to the new object
   * @param object The new object, the name will be bind to
   * @throws NamingException Thrown if NamingException occurs
   */
  public void rebind(String name, Object object) throws javax.naming.NamingException {
    rebind(parse(name), object, null);
  }

  /**
   * DirContext rebind using RMI Context
   *
   * @param name The name to be bound to the new object
   * @param object The new object, the name will be bind to
   * @param attributes The attributes which will be set to the object
   * @throws NamingException Thrown if NamingException occurs
   */
  public void rebind(Name name, Object object, Attributes attributes) throws javax.naming.NamingException {
    //Binds a name to an object, along with associated attributes, overwriting any existing binding. If
    //attrs is null and obj is a DirContext, the attributes from obj are used. If attrs is null and obj is
    //not a DirContext, any existing attributes associated with the object already bound in the directory
    //remain unchanged. If attrs is non-null, any existing attributes associated with the object already
    //bound in the directory are removed and attrs is associated with the named object. If obj is a
    //DirContext and attrs is non-null, the attributes of obj are ignored.
    name = transformName(name);

    // verify name for correctness
    if (name.size() == 0) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.infoT("Cannot rebind an empty name.");
      }
      throw new InvalidNameException("Cannot rebind an empty name.");
    }

    verifyName(name);

    Object temp = prepareStateToBind(name, object, attributes);
    if (temp instanceof DirStateFactory.Result) {
      object = ((DirStateFactory.Result) temp).getObject();
      attributes = ((DirStateFactory.Result) temp).getAttributes();
    } else {
      object = temp;
    }

    short operationType = checkOperation(name);
    try {
      if (object instanceof Referenceable) {
        Object ref = ((Referenceable) object).getReference();

        if (ref != null) {
          object = ref;
        }
      }

      if (object instanceof PortableRemoteObject || object instanceof UnicastRemoteObject || object instanceof Remote) {
        if (operationType == Constants.REPLICATED_OPERATION) {
          operationType = Constants.REMOTE_REPLICATED_OPERATION;
        }

        //        System.out.println("Remote Object : operationtype = " + operationType);
      }

      if (remoteContext == null) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No connection with the server.");
        }
        throw new NamingException("No connection with the server.");
      }

      //try to remove the object from LROTable
      String fullName = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);
      RemoteSerializator.LROTable.remove(fullName);
      DirObject dobj = new DirObject(attributes, object);

      if (object != null) {
        dobj.setClassName(object.getClass().getName());
      } else {
        dobj.setClassName("null");
      }
      byte[] data = null;
      try {
        data = serializeDirObject(dobj);

        if (data == null) { //the object is not serializable
          Object o = null;
          try {
            o = remoteContext.lookup(name, operationType);
            //System.out.println("object found : " + name + " : " + o);
            remoteContext.unbind(name, operationType);
          } catch (javax.naming.NameNotFoundException ex) {
            // Excluding this catch block from JLIN $JL-EXC$ since there is no need to log this exception.
            // Please do not remove this comment!
            if (ex instanceof com.sap.engine.services.jndi.persistent.exceptions.NameNotFoundException) {
              if (((com.sap.engine.services.jndi.persistent.exceptions.NameNotFoundException) ex).missingPathComponent) {
                throw ex;
              }
            } else {
              if (ex instanceof com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException) {
                if (((com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException) ex).missingPathComponent) {
                  throw ex; //exceptions different from Object not found
                }
              } else {
                if (!Constants.NON_SERIALIZABLE_REBIND_OLD_BEHAVIOUR) {
                  throw ex; //exceptions different from Object not found
                }
              }
            }
          }
          RemoteSerializator.LROTable.put(fullName, dobj);
          if (LOG_LOCATION.bePath()) {
            LOG_LOCATION.pathT("Object with name " + fullName + " will be put in the LROTable as a result of rebind operation.");
          }
          //          System.out.println("<REBIND> put name:" + fullName + " in LROTable");
          return;
        }
      } catch (P4ConnectionException pce) {
        throw pce;
      } catch (ServerException nsoe) {
        throw nsoe;
      } catch (Exception e) { // if serializeDirObject(dirObject) throws
        // exception dirObject will be put in LROTable
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
        try {
          remoteContext.lookup(parse(fullName), operationType);
          remoteContext.unbind(name, operationType);
          RemoteSerializator.LROTable.put(fullName, dobj);
          if (LOG_LOCATION.bePath()) {
            LOG_LOCATION.pathT("Object with name " + fullName + " will be put in the LROTable as a result of rebind operation.");
          }
          //            System.out.println("<REBIND2> put name:" + fullName + " in LROTable");
        } catch (P4ConnectionException pce) {
          throw pce;
        } catch (ServerException nsoe) {
          throw nsoe;
        } catch (javax.naming.NamingException ex) {
          LOG_LOCATION.traceThrowableT(Severity.PATH, "", ex);
          if (ex instanceof com.sap.engine.services.jndi.persistent.exceptions.NameNotFoundException) {
            if (((com.sap.engine.services.jndi.persistent.exceptions.NameNotFoundException) ex).missingPathComponent) {
              throw ex;
            }
          } else {
            if (ex instanceof com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException) {
              if (((com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException) ex).missingPathComponent) {
                throw ex; //exceptions different from Object not found
              }
            } else {
              if (!Constants.NON_SERIALIZABLE_REBIND_OLD_BEHAVIOUR) {
                throw ex; //exceptions different from Object not found
              }
            }
          }
          RemoteSerializator.LROTable.put(fullName, dobj);
          if (LOG_LOCATION.bePath()) {
            LOG_LOCATION.pathT("Object with name " + fullName + " will be put in the LROTable as a result of bind operation.");
          }
          return;
        }
      }
      removeReference(name);
      remoteContext.rebind(name, data, operationType, lastobj);

      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Rebind operation finished successfully over object/context with name " + name + ".");
      }

      if (lastobj) {
        if (remoteReferenceHash != null) {
          addReference(name, remoteReferenceHash);
        }
      }
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing rebind operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing rebind operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (com.sap.engine.services.rmi_p4.P4RuntimeException ex) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", ex);
      NamingException ne = new NamingException("Protocol exception. Cannot perform the operation.");
      ne.setRootCause(ex);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform rebind operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform rebind operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during rebind operation of object with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during rebind operation of object with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * DirContext rebind using RMI Context
   *
   * @param name The name to be bound to the new object
   * @param object The new object, the name will be bind to
   * @param attributes The attributes which will be set to the object
   * @throws NamingException Thrown if NamingException occurs
   */
  public void rebind(String name, Object object, Attributes attributes) throws javax.naming.NamingException {
    rebind(parse(name), object, attributes);
  }

  /**
   * Context unbind using RMI Context
   *
   * @param name The name to be unbound
   * @throws NamingException Thrown if NamingException occurs
   */
  public void unbind(Name name) throws javax.naming.NamingException {
    name = transformName(name);

    // verify name for correctness
    if (name.size() == 0) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.infoT("Cannot unbind empty name.");
      }
      throw new InvalidNameException("Cannot unbind empty name.");
    }

    verifyName(name);
    try {
      if (remoteContext == null) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No connection with the server.");
        }
        throw new NamingException("No connection with the server.");
      }

      // tries to remove it first from the LROTable
      DirObject obj = null;
      String fullPath = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);
      obj = (DirObject) RemoteSerializator.LROTable.remove(fullPath);

      if (obj != null) {
        if (LOG_LOCATION.bePath()) {
          LOG_LOCATION.pathT("Object with name " + fullPath + " will be removed from the LROTable as a result of an unbind operation.");
        }
        return;
      }

      short operationType = checkOperation(name);
      removeReference(name);
      remoteContext.unbind(name, operationType);
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Unbind operation finished successfully over object/context with name " + name + ".");
      }
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing unbind operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing unbind operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform unbind operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform unbind operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during unbind operation of object with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during unbind operation of object with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * Context unbind using RMI Context
   *
   * @param name The name to be unbound
   * @throws NamingException Thrown if NamingException occurs
   */
  public void unbind(String name) throws javax.naming.NamingException {
    unbind(parse(name));
  }

  /**
   * Context rename using RMI Context
   *
   * @param oldName The old name of the object
   * @param newName The new name of the object
   * @throws NamingException Thrown if NamingException occurs
   */
  public void rename(Name oldName, Name newName) throws javax.naming.NamingException {
    oldName = transformName(oldName);
    newName = transformName(newName);
    // verify name for correctness
    verifyName(newName);
    verifyName(oldName);
    try {
      if (remoteContext == null) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No connection with the server.");
        }
        throw new NamingException("No connection with the server.");
      }

      checkOperation(newName);
      short operationType = checkOperation(oldName);

      // check if the object is in LROTable
      DirObject obj = null;
      String fullPath = (getNameInNamespace().equals("")) ? (oldName.toString()) : (getNameInNamespace() + "/" + oldName);
      obj = (DirObject) RemoteSerializator.LROTable.remove(fullPath);

      if (obj != null) {
        if (LOG_LOCATION.bePath()) {
          LOG_LOCATION.pathT("Object with name " + fullPath + " will be removed from the LROTable as a result of an unbind operation.");
        }
        String newPath = (getNameInNamespace().equals("")) ? (newName.toString()) : (getNameInNamespace() + "/" + newName);
        RemoteSerializator.LROTable.put(newPath, obj);
        if (LOG_LOCATION.bePath()) {
          LOG_LOCATION.pathT("Object with name " + newPath + " will be put in the LROTable as a result of rename operation.");
        }
        return;
      }

      Object reference = removeReference(oldName);

      if (reference != null) {
        if (reference != null) {
          addReference(newName, reference);
        }
      }

      remoteContext.rename(oldName, newName, operationType);
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Unbind operation finished successfully over object/context with name " + oldName + ".");
        LOG_LOCATION.pathT("Rename operation finished successfully over object/context with name " + newName + ".");
      }
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing rename operation of name " + newName + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing rename operation of name " + newName + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform rename operation of name " + newName + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform rename operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during rename operation of object with old name " + oldName + " and new name " + newName + ".", ex);
      }
      NamingException ne = new NamingException("Exception during rename operation of object with old name " + oldName + " and new name " + newName + ".");
      throw ne;
    }
  }

  /**
   * Context rename using RMI Context
   *
   * @param oldName The old name of the object
   * @param newName The new name of the object
   * @throws NamingException Thrown if NamingException occurs
   */
  public void rename(String oldName, String newName) throws javax.naming.NamingException {
    rename(parse(oldName), parse(newName));
  }

  /**
   * Context list using RMI Context
   *
   * @param name The name of context to be listed
   * @return The enumeration, containing the ClassNamePair-s of the listed objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration list(Name name) throws javax.naming.NamingException {
    name = transformName(name);
    try {
      short operationType = checkOperation(name);
      return new ClientNamingEnum(this, new FullClientNamingEnum(remoteContext.list(name, operationType, runOnServer), listLROTable(name)), ((fullNameInSpace == null) ? (fullNameInSpace = new CompositeName("")) : fullNameInSpace), localEnvironment, runOnServer, loginContext);
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing list operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing list operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform list operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform list operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during list operation of context with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during list operation of context with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  private ConcurrentArrayObject listBindingsLROTable(Name name) {
    ConcurrentArrayObject bindings = new ConcurrentArrayObject();
    DirObject obj = null;
    String objectName = "";
    String fullPath = "";
    try {
      fullPath = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);

      if (fullPath.endsWith("/")) {
        fullPath = fullPath.substring(0, fullPath.length() - 1);
      }

      //System.out.println("path to find : " + fullPath);
    } catch (javax.naming.NamingException ne) {
      JNDIFrame.log.logCatching(ne);
      fullPath = "";
    }
    Object[] keys = RemoteSerializator.LROTable.getAllKeys();

    for (int i = 0; i < keys.length; i++) {
      int index = ((String) keys[i]).lastIndexOf('/');

      if (index != -1) {
        String context = ((String) keys[i]).substring(0, index);

        //System.out.println("current context is : " + context);
        if (fullPath.equals(context)) {
          //System.out.println("~~~~~~~~~~EQUALS~~~~~~~~~");
          objectName = ((String) keys[i]).substring(((String) keys[i]).lastIndexOf('/') + 1);
          obj = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
          bindings.add(new Binding(objectName, obj));
        }
      } else {
        if (fullPath.equals("") || fullPath.equals("/")) {
          obj = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
          bindings.add(new Binding((String) keys[i], obj));
        }
      }
    }

    //System.out.println("bindings.size = " + bindings.size());
    return bindings;
  }

  private ConcurrentArrayObject listLROTable(Name name) {
    ConcurrentArrayObject bindings = new ConcurrentArrayObject();
    DirObject obj = null;
    String objectName = "";
    String fullPath = "";
    try {
      fullPath = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);

      if (fullPath.endsWith("/")) {
        fullPath = fullPath.substring(0, fullPath.length() - 1);
      }

      //System.out.println("list path to find : " + fullPath);
    } catch (javax.naming.NamingException ne) {
      // Excluding this catch block from JLIN $JL-EXC$ since this exception has been already logged at a lower level.
      // Please do not remove this comment!
      fullPath = "";
    }
    Object[] keys = RemoteSerializator.LROTable.getAllKeys();

    for (int i = 0; i < keys.length; i++) {
      //System.out.println("keys -> " + keys[i]);
      int index = ((String) keys[i]).lastIndexOf('/');

      if (index != -1) {
        String context = ((String) keys[i]).substring(0, index);

        //System.out.println("current context is : " + context);
        if (fullPath.equals(context)) {
          // System.out.println("~~~~~~~~~~EQUALS~~~~~~~~~");
          objectName = ((String) keys[i]).substring(((String) keys[i]).lastIndexOf('/') + 1);
          obj = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
          bindings.add(new NameClassPair(objectName, obj.getClassName()));
        }
      } else {
        if (fullPath.equals("") || fullPath.equals("/")) {
          obj = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
          bindings.add(new NameClassPair((String) keys[i], obj.getClassName()));
        }
      }
    }

    // System.out.println("bindings.size = " + bindings.size());
    return bindings;
  }

  private ConcurrentArrayObject searchLROTable(Name name, Attributes matchingAttributes, String[] attrsToReturn) {
    //    System.out.println("in search LRO");
    ConcurrentArrayObject searchResult = new ConcurrentArrayObject();
    ConcurrentArrayObject dobjs = new ConcurrentArrayObject();
    DirObject object = null;
    String fullPath = "";
    try {
      fullPath = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);
    } catch (javax.naming.NamingException ne) {
      // Excluding this catch block from JLIN $JL-EXC$ since this exception has been already logged at a lower level.
      // Please do not remove this comment!
      fullPath = "";
    }

    if (fullPath.endsWith("/")) {
      fullPath = fullPath.substring(0, fullPath.length() - 1);
    }

    //System.out.println("Path is " + fullPath);
    Object[] keys = RemoteSerializator.LROTable.getAllKeys();
    String objectName = "";

    if ((fullPath.endsWith("/")) && (fullPath.length() > 0)) {
      for (int i = 0; i < keys.length; i++) {
        if (fullPath.equals(keys[i])) {
          object = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
          objectName = ((String) keys[i]).substring(((String) keys[i]).lastIndexOf('/') + 1);
          dobjs.add(new Binding(objectName, object));
        }
      }
    } else {
      for (int i = 0; i < keys.length; i++) {
        int index = ((String) keys[i]).lastIndexOf('/');

        if (index != -1) {
          if (fullPath.equals(((String) keys[i]).substring(0, index))) {
            objectName = ((String) keys[i]).substring(((String) keys[i]).lastIndexOf('/') + 1);
            object = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
            dobjs.add(new Binding(objectName, object));
          }
        } else {
          if (fullPath.equals("") || fullPath.equals("/")) {
            object = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
            dobjs.add(new Binding((String) keys[i], object));
          }
        }
      }
    }

    //System.out.println("dobjs.size() = " + dobjs.size());
    if (dobjs.size() == 0) {
      return searchResult;
    }

    Attributes attr = null;

    for (int i = 0; i < dobjs.size(); i++) {
      attr = ((DirObject) ((Binding) dobjs.get(i)).getObject()).getAttributes();
      objectName = ((Binding) dobjs.get(i)).getName();

      //System.out.println("object name - " + objectName);
      if (attr == null) {
        attr = new BasicAttributes();
      }

      try {
        if (ModifyAttributes.matchingAttributes(attr, matchingAttributes)) {
          // the attributes matched
          BasicAttributes selectedAttr = new BasicAttributes();

          // if attrsToReturn is null, return all attributes, by specification
          if (attrsToReturn != null) {
            // if is not null, select the attributes to return
            for (int n = 0; n < attrsToReturn.length; n++) {
              selectedAttr.put(attr.get(attrsToReturn[n]));
            }

            searchResult.add(new SearchResult(objectName, null, ((DirObject) ((Binding) dobjs.get(i)).getObject()), selectedAttr));
          } else {
            searchResult.add(new SearchResult(objectName, null, ((DirObject) ((Binding) dobjs.get(i)).getObject()), attr));
          }
        }
      } catch (Exception ex) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during search operation on name " + name + ".", ex);
        }
      }
    }

    //System.out.println("searchResult.size() -> " + searchResult.size());
    return searchResult;
  }

  private ConcurrentArrayObject searchLROTable(Name name, String filter, Object filterArgs[], SearchControls cons) {
    //System.out.println("in search LRO");
    ConcurrentArrayObject searchResult = new ConcurrentArrayObject();
    ConcurrentArrayObject dobjs = new ConcurrentArrayObject();
    DirObject obj = null;
    String objectName = "";
    String fullPath = "";
    try {
      fullPath = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);
    } catch (javax.naming.NamingException ne) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", ne);
      fullPath = "";
    }

    if (fullPath.endsWith("/")) {
      fullPath = fullPath.substring(0, fullPath.length() - 1);
    }

    //System.out.println("path to find : " + fullPath);
    Object[] keys = RemoteSerializator.LROTable.getAllKeys();

    if (cons.getSearchScope() == SearchControls.OBJECT_SCOPE) { //ako e object name
      for (int i = 0; i < keys.length; i++) {
        if (fullPath.equals(keys[i])) {
          obj = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
          objectName = ((String) keys[i]).substring(((String) keys[i]).lastIndexOf('/') + 1);
          dobjs.add(new Binding(objectName, obj));
        }
      }
    } else { // context name
      if (cons.getSearchScope() == SearchControls.ONELEVEL_SCOPE) {
        for (int i = 0; i < keys.length; i++) {
          int index = ((String) keys[i]).lastIndexOf('/');

          if (index != -1) {
            String context = ((String) keys[i]).substring(0, index);

            if (fullPath.equals(context)) {
              objectName = ((String) keys[i]).substring(((String) keys[i]).lastIndexOf('/') + 1);
              obj = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
              dobjs.add(new Binding(objectName, obj));
            }
          } else { // ako e root context name
            if (fullPath.equals("") || fullPath.equals("/")) {
              if (((String) keys[i]).indexOf("/") == -1) {
                obj = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
                dobjs.add(new Binding((String) keys[i], obj));
              }
            }
          }
        }
      } else { // SUBSTREE_SCOPE
        for (int i = 0; i < keys.length; i++) {
          if (((String) keys[i]).indexOf(fullPath) == 0) {
            objectName = ((String) keys[i]).substring(((String) keys[i]).lastIndexOf('/') + 1);
            obj = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
            dobjs.add(new Binding(objectName, obj));
          } else if (fullPath.equals("") || fullPath.equals("/")) {
            obj = (DirObject) RemoteSerializator.LROTable.get(keys[i]);
            dobjs.add(new Binding((String) keys[i], obj));
          }
        }
      }
    }

    //System.out.println("dobjs.size() = " + dobjs.size());
    if (dobjs.size() == 0) {
      return searchResult;
    }

    Attributes attr = null;
    SearchFilter searchFilter = null;
    String className = "";
    try {
      if (filterArgs != null) {
        String formattedFilter = SearchFilter.format(filter, filterArgs);
        searchFilter = new SearchFilter(formattedFilter);
      } else {
        searchFilter = new SearchFilter(filter);
      }
    } catch (javax.naming.NamingException ex) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during search operation on name " + name + ".", ex);
      }
    }

    for (int i = 0; i < dobjs.size(); i++) {
      attr = ((DirObject) ((Binding) dobjs.get(i)).getObject()).getAttributes();
      objectName = ((Binding) dobjs.get(i)).getName();

      //System.out.println("object name - " + objectName);
      if (attr == null) {
        attr = new BasicAttributes();
      }

      className = ((DirObject) ((Binding) dobjs.get(i)).getObject()).getClassName();
      try {
        if (searchFilter.check(attr)) {
          BasicAttributes selectedAttr = new BasicAttributes();
          String[] attrsToReturn = cons.getReturningAttributes();

          if (attrsToReturn != null) {
            for (int j = 0; j < attrsToReturn.length; j++) {
              selectedAttr.put(attr.get(attrsToReturn[i]));
            }

            if (cons.getReturningObjFlag()) {
              searchResult.add(new SearchResult(objectName, className, dobjs.get(i), selectedAttr));
            } else {
              searchResult.add(new SearchResult(objectName, className, null, selectedAttr));
            }
          } else {
            if (cons.getReturningObjFlag()) {
              searchResult.add(new SearchResult(objectName, className, dobjs.get(i), attr));
            } else {
              searchResult.add(new SearchResult(objectName, className, null, attr));
            }
          }
        }
      } catch (javax.naming.NamingException ex) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during search operation on name " + name + ".", ex);
        }
      }
    }

    //System.out.println("searchResult.size() -> " + searchResult.size());
    return searchResult;
  }

  /**
   * Context list using RMI Context
   *
   * @param name The name of context to be listed
   * @return The enumeration, containing the ClassNamePair-s of the listed objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration list(String name) throws javax.naming.NamingException {
    return list(parse(name));
  }

  /**
   * Context listBindings using RMI Context
   *
   * @param name The name of context to be listed
   * @return The enumeration, containing the Binding-s of the listed objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration listBindings(Name name) throws javax.naming.NamingException {
    name = transformName(name);
    try {
      short operationType = checkOperation(name);
      return new ClientNamingEnum(this, new FullClientNamingEnum(remoteContext.listBindings(name, operationType, runOnServer), listBindingsLROTable(name)), ((fullNameInSpace == null) ? (fullNameInSpace = new CompositeName("")) : fullNameInSpace), localEnvironment, runOnServer, loginContext);
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing listBindings operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing listBindings operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform listBindings operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform listBindings operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during listBindings operation of context with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during listBindings operation of context with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * Context listBindings using RMI Context
   *
   * @param name The name of context to be listed
   * @return The enumeration, containing the Binding-s of the listed objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration listBindings(String name) throws javax.naming.NamingException {
    return listBindings(parse(name));
  }

  /**
   * Context destroySubcontext using RMI Context
   *
   * @param name The name of the context to be destroyed
   * @throws NamingException Thrown if NamingException occurs
   */
  public void destroySubcontext(Name name) throws javax.naming.NamingException {

    name = transformName(name);
    verifyName(name);
    // check the backword compatibility constant first
    if (!Constants.DESTROY_CONTEXT_OLD_BEHAVIOUR) {
      //  check for objects in LROTable first
      if (isNameExistsInLROTable(name)) {
        // context is not empty => cannot destroy it => throw exception
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("destroySubcontext operation failed; context " + name + " is not empty.");
        }
        throw new ContextNotEmptyException("destroySubcontext operation failed; context " + name + " is not empty.");
      }
    }

    try {
      short operationType = checkOperation(name); //replicated or not
      remoteContext.destroySubcontext(name, operationType);
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.pathT("Destroy subcontext operation finished successfully over object/context with name " + name + ".");
      }
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing destroysubcontext operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing destroysubcontext operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform destroySubcontext operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform destroySubcontext operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during destroySubcontext operation of context with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during destroySubcontext operation of context with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * Context destroySubcontext using RMI Context
   *
   * @param name The name of the context to be destroyed
   * @throws NamingException Thrown if NamingException occurs
   */
  public void destroySubcontext(String name) throws javax.naming.NamingException {
    destroySubcontext(parse(name));
  }

  private boolean isNameExistsInLROTable(Name name) {
    String fullPath = "";
    try {
      fullPath = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);
      // this is done to ensure that only objects in this context will be searched
      // added as fix to a problem with context names such that the one name starts with the other
      if (!fullPath.endsWith("/")) {
        fullPath = fullPath + "/";
      }
    } catch (javax.naming.NamingException ne) {
      JNDIFrame.log.logCatching(ne);
      // Excluding this catch block from JLIN $JL-EXC$ since this exception has been already logged at a lower level.
      // Please do not remove this comment!
      fullPath = "";
    }

    Object[] keys = RemoteSerializator.LROTable.getAllKeys();

    for (int i = 0; i < keys.length; i++) {
      if (((String) keys[i]).startsWith(fullPath)) {
        if (LOG_LOCATION.bePath()) {
          LOG_LOCATION.pathT("Object with name " + keys[i] + " still exists in context " + fullPath + ".");
        }
        return true;
      }
    }
    return false;
  }

  /**
   * DirContext createSubcontext using RMI Context
   *
   * @param name The name of the context to be created
   * @param attributes Attributes to be set on the newly created context
   * @return An instance of the newly created context
   * @throws NamingException Thrown if NamingException occurs
   */
  public DirContext createSubcontext(Name name, Attributes attributes) throws javax.naming.NamingException {

    name = transformName(name);
    verifyName(name);
    try {
      short operationType = checkOperation(name); //replicated or not

      if (Constants.KEEP_ABSOLUTE_NAME) {
        try {
          Name newFullName = ((Name) (((fullNameInSpace == null) ? (fullNameInSpace = new CompositeName("")) : fullNameInSpace).clone())).addAll(name);
          ClientContext cl = new ClientContext(localEnvironment, remoteContext.createSubcontext(name, attributes, operationType), newFullName, runOnServer, this.referenceFactory, this.loginContext);
          if (LOG_LOCATION.bePath()) {
            LOG_LOCATION.pathT("Create subcontext operation finished successfully over object/context with name " + newFullName + ".");
          }
          return cl;
        } catch (javax.naming.InvalidNameException ex) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "Incorrect name parameter " + name + ". The name must be a CompositeName or a String.", ex);
          }
          InvalidNameException ine = new InvalidNameException("Incorrect name parameter " + name + ". The name must be a CompositeName or a String.");
          ine.setRootCause(ex);
          throw ine;
        }
      } else {
        ClientContext cl = new ClientContext(localEnvironment, remoteContext.createSubcontext(name, attributes, operationType), runOnServer, this.referenceFactory, this.loginContext);
        if (LOG_LOCATION.bePath()) {
          LOG_LOCATION.pathT("Create subcontext operation finished successfully over object/context with name " + name + ".");
        }
        return cl;
      }
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing createSubcontext operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing createSubcontext operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform createSubcontext operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform createSubcontext operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during createSubcontext operation of context with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during createSubcontext operation of context with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * DirContext createSubcontext using RMI Context
   *
   * @param name The name of the context to be created
   * @param attributes Attributes to be set on the newly created context
   * @return An instance of the newly created context
   * @throws NamingException Thrown if NamingException occurs
   */
  public DirContext createSubcontext(String name, Attributes attributes) throws javax.naming.NamingException {
    return createSubcontext(parse(name), attributes);
  }

  /**
   * Context createSubcontext using RMI Context
   *
   * @param name The name of the context to be created
   * @return An instance of the newly created context
   * @throws NamingException Thrown if NamingException occurs
   */
  public Context createSubcontext(Name name) throws javax.naming.NamingException {
    return createSubcontext(name, null);
  }

  /**
   * Context createSubcontext using RMI Context
   *
   * @param name The name of the context to be created
   * @return An instance of the newly created context
   * @throws NamingException Thrown if NamingException occurs
   */
  public Context createSubcontext(String name) throws javax.naming.NamingException {
    return createSubcontext(parse(name), null);
  }

  /**
   * Context lookupLink using RMI Context
   *
   * @param name The name of the object to be looked up
   * @return The looked up object
   * @throws NamingException Thrown if NamingException occurs
   */
  public Object lookupLink(String name) throws javax.naming.NamingException {
    return lookup(parse(name));
  }

  /**
   * Context lookupLink using RMI Context
   *
   * @param name The name of the object to be looked up
   * @return The looked up object
   * @throws NamingException Thrown if NamingException occurs
   */
  public Object lookupLink(Name name) throws javax.naming.NamingException {
    return lookup(name);
  }

  /**
   * DirContext getAttributes using RMI Context
   *
   * @param name The name of the object, which attributes are to be returned
   * @return The attributes of the object, the name is bound to
   * @throws NamingException Thrown if NamingException occurs
   */
  public Attributes getAttributes(Name name) throws javax.naming.NamingException {
    // System.out.println("* client object GET_ATTRIBUTES1 invoked");
    name = transformName(name);
    verifyName(name);
    short operationType = checkOperation(name);
    try {
      //check first in LROTable
      DirObject obj = null;
      String fullPath = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);
      //System.out.println("full name = " + fullPath);
      obj = (DirObject) RemoteSerializator.LROTable.get(fullPath);

      // System.out.println("object - " + obj);
      if (obj != null) {
        Attributes attrToReturn = obj.getAttributes();

        if (attrToReturn == null) {
          attrToReturn = new BasicAttributes();
        }

        return attrToReturn;
      }

      return remoteContext.getAttributes(name, operationType);
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing getAttributes operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing getAttributes operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform getAttributes operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform getAttributes operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during getAttributes operation of object/context with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during getAttributes operation of object/context with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * DirContext getAttributes using RMI Context
   *
   * @param name The name of the object, which attributes are to be returned
   * @param attrIDs The IDs of the attributes which will be returned
   * @return The attributes of the object, the name is bound to
   * @throws NamingException Thrown if NamingException occurs
   */
  public Attributes getAttributes(Name name, String[] attrIDs) throws javax.naming.NamingException {
    //System.out.println(" * client object GET_ATTRIBUTES2 invoked");
    name = transformName(name);
    verifyName(name);
    short operationType = checkOperation(name);
    try {
      //check first in LROTable
      DirObject obj = null;
      String fullPath = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);
      //System.out.println("fullpath = " + fullPath);
      obj = (DirObject) RemoteSerializator.LROTable.get(fullPath);

      if (obj != null) {
        Attributes attrsToReturn = new BasicAttributes();
        // get all attributes
        Attributes attrs = obj.getAttributes();

        if (attrIDs == null) {
          attrsToReturn = attrs;
        } else if (attrIDs.length == 0) {

        } else {
          for (int i = 0; i < attrIDs.length; i++) {
            String tempID = attrIDs[i];
            Attribute attribute = attrs.get(tempID);

            if (attribute != null) {
              attrsToReturn.put(attribute);
            }
          }
        }

        return attrsToReturn;
      }

      return remoteContext.getAttributes(name, attrIDs, operationType);
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing getAttributes operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing getAttributes operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform getAttributes operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform getAttributes operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during getAttributes operation of object/context with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during getAttributes operation of object/context with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * DirContext getAttributes using RMI Context
   *
   * @param name The name of the object, which attributes are to be returned
   * @return The attributes of the object, the name is bound to
   * @throws NamingException Thrown if NamingException occurs
   */
  public Attributes getAttributes(String name) throws javax.naming.NamingException {
    return getAttributes(parse(name));
  }

  /**
   * DirContext getAttributes using RMI Context
   *
   * @param name The name of the object, which attributes are to be returned
   * @param attributeIDs The IDs of the attributes which will be returned
   * @return The attributes of the object, the name is bound to
   * @throws NamingException Thrown if NamingException occurs
   */
  public Attributes getAttributes(String name, String[] attributeIDs) throws javax.naming.NamingException {
    if (attributeIDs == null) {
      return getAttributes(parse(name));
    }

    return getAttributes(parse(name), attributeIDs);
  }

  /**
   * DirContext modifyAttributes using RMI Context
   *
   * @param name The name of the object, whose attributes will be modified
   * @param modificationOption The operationType of the modification
   * @param attributes The attributes, used to modify object's attributes
   * @throws NamingException Thrown if NamingException occurs
   */
  public void modifyAttributes(Name name, int modificationOption, Attributes attributes) throws javax.naming.NamingException {

    name = transformName(name);
    verifyName(name);
    short operationType = checkOperation(name);
    try {
      //check first in LROTable
      DirObject obj = null;
      String fullPath = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);
      obj = (DirObject) RemoteSerializator.LROTable.remove(fullPath);

      //System.out.println("MA : full path = " + fullPath + " , obj = " + obj);
      if (obj != null) {
        Attributes attrToModify = obj.getAttributes();

        if (attrToModify == null) {
          attrToModify = new BasicAttributes();
        }

        ModifyAttributes.modAttr(attrToModify, modificationOption, attributes);
        obj.setAttributes(attrToModify);
        RemoteSerializator.LROTable.put(fullPath, obj);
        return;
      }

      remoteContext.modifyAttributes(name, modificationOption, attributes, operationType);
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing modifyAttributes operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing modifyAttributes operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform modifyAttributes operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform modifyAttributes operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during modifyAttributes operation of object/context with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during modifyAttributes operation of object/context with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * DirContext modifyAttributes using RMI Context
   *
   * @param name The name of the object, whose attributes will be modified
   * @param modificationItems Array of ModificationItem-s, used to modify the object's attributes
   * @throws NamingException Thrown if NamingException occurs
   */
  public void modifyAttributes(Name name, ModificationItem[] modificationItems) throws javax.naming.NamingException {
    name = transformName(name);
    verifyName(name);
    short operationType = checkOperation(name);
    try {
      //check first in LROTable
      DirObject obj = null;
      String fullPath = (getNameInNamespace().equals("")) ? (name.toString()) : (getNameInNamespace() + "/" + name);
      obj = (DirObject) RemoteSerializator.LROTable.remove(fullPath);

      //System.out.println("MA : full path = " + fullPath + " , obj = " + obj);
      if (obj != null) {
        Attributes attrToModify = obj.getAttributes();

        if (attrToModify == null) {
          attrToModify = new BasicAttributes();
        }

        ModifyAttributes.modAttr(attrToModify, modificationItems);
        obj.setAttributes(attrToModify);
        RemoteSerializator.LROTable.put(fullPath, obj);
        return;
      }

      remoteContext.modifyAttributes(name, modificationItems, operationType);
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing modifyAttributes operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing modifyAttributes operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform modifyAttributes operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform modifyAttributes operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during modifyAttributes operation of object/context with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during modifyAttributes operation of object/context with name " + name + ".");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * DirContext modifyAttributes using RMI Context
   *
   * @param name The name of the object, whose attributes will be modified
   * @param modificationOption The operationType of the modification
   * @param attrs The attributes, used to modify object's attributes
   * @throws NamingException Thrown if NamingException occurs
   */
  public void modifyAttributes(String name, int modificationOption, Attributes attrs) throws javax.naming.NamingException {
    modifyAttributes(parse(name), modificationOption, attrs);
  }

  /**
   * DirContext modifyAttributes using RMI Context
   *
   * @param name The name of the object, whose attributes will be modified
   * @param modificationItems Array of ModificationItem-s, used to modify the object's attributes
   * @throws NamingException Thrown if NamingException occurs
   */
  public void modifyAttributes(String name, ModificationItem[] modificationItems) throws javax.naming.NamingException {
    modifyAttributes(parse(name), modificationItems);
  }

  /**
   * DirContext search using RMI Context
   *
   * @param name The name of the context or object to be passed to the search method
   * @param matchingAttributes The attributes to be matched for each object
   * @return Enumeration of SearchResult-s of the qualified objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration search(Name name, Attributes matchingAttributes) throws javax.naming.NamingException {
    return search(name, matchingAttributes, null);
  }

  /**
   * DirContext search using RMI Context
   *
   * @param name The name of the context or object to be passed to the search method
   * @param matchingAttributes The attributes to be matched for each object
   * @return Enumeration of SearchResult-s of the qualified objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration search(String name, Attributes matchingAttributes) throws javax.naming.NamingException {
    return search(parse(name), matchingAttributes, null);
  }

  /**
   * DirContext search using RMI Context
   *
   * @param name The name of the context or object to be passed to the search method
   * @param matchingAttributes The attributes to be matched for each object
   * @param attributesToReturn The selection of attributes which will be returned
   * @return Enumeration of SearchResult-s of the qualified objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws javax.naming.NamingException {
    try {
      short operationType = checkOperation(name);
      //      ServerNamingEnum sne = remoteContext.search(name, matchingAttributes, attributesToReturn, altServerID, operationType);
      //      while (sne.hasMore()) {
      //        System.out.println("sne -> " + sne.next());
      //      }
      return new ClientNamingEnum(this, new FullClientNamingEnum(remoteContext.search(name, matchingAttributes, attributesToReturn, operationType), searchLROTable(name, matchingAttributes, attributesToReturn)),
                    ((fullNameInSpace == null) ? (fullNameInSpace = new CompositeName("")) : fullNameInSpace), localEnvironment, runOnServer, loginContext);
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing search operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing search operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform search operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform search operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during search operation with name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during search operation.");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * DirContext search using RMI Context
   *
   * @param name The name of the context or object to be passed to the search method
   * @param matchingAttributes The attributes to be matched for each object
   * @param attributesToReturn The selection of attributes which will be returned
   * @return Enumeration of SearchResult-s of the qualified objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration search(String name, Attributes matchingAttributes, String attributesToReturn[]) throws javax.naming.NamingException {
    return search(parse(name), matchingAttributes, attributesToReturn);
  }

  /**
   * DirContext search using RMI Context (filterExpr - RFC 2254)
   *
   * @param name The name of the context or object to be passed to the search method
   * @param filterExpression The string filterExpression according to RFC 2254
   * @param filterArguments The arguments to complete the filterExpression string if needed
   * @param searchControls The SearchControls variable, containing information on how the search will be processed
   * @return Enumeration of SearchResult-s of the qualified objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration search(Name name, String filterExpression, Object filterArguments[], SearchControls searchControls) throws javax.naming.NamingException {
    try {
      short operationType = checkOperation(name);
      ServerNamingEnum sne = null;
      try {
        sne = remoteContext.search(name, filterExpression, filterArguments, searchControls, operationType);
      } catch (javax.naming.NameNotFoundException ex) {
        JNDIFrame.log.logCatching(ex);
        sne = null;
      }

      return new ClientNamingEnum(this, new FullClientNamingEnum(sne, searchLROTable(name, filterExpression, filterArguments, searchControls)), ((fullNameInSpace == null) ? (fullNameInSpace = new CompositeName("")) : fullNameInSpace), localEnvironment, this.runOnServer, loginContext);
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing search operation of name " + name + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing search operation of name " + name + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform search operation of name " + name + ".", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform search operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during search operation wint name " + name + ".", ex);
      }
      NamingException ne = new NamingException("Exception during search operation.");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * DirContext search using RMI Context (filterExpr - RFC 2254)
   *
   * @param name The name of the context or object to be passed to the search method
   * @param filterExpression The string filterExpression according to RFC 2254
   * @param filterArguments The arguments to complete the filterExpression string if needed
   * @param searchControls The SearchControls variable, containing information on how the search will be processed
   * @return Enumeration of SearchResult-s of the qualified objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration search(String name, String filterExpression, Object filterArguments[], SearchControls searchControls) throws javax.naming.NamingException {
    return search(parse(name), filterExpression, filterArguments, searchControls);
  }

  /**
   * DirContext search using RMI Context (filterExpr - RFC 2254)
   *
   * @param name The name of the context or object to be passed to the search method
   * @param filterExpression The string filterExpression according to RFC 2254
   * @param searchControls The SearchControls variable, containing information on how the search will be processed
   * @return Enumeration of SearchResult-s of the qualified objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration search(Name name, String filterExpression, SearchControls searchControls) throws javax.naming.NamingException {
    return search(name, filterExpression, null, searchControls);
  }

  /**
   * DirContext search using RMI Context (filterExpr - RFC 2254)
   *
   * @param name The name of the context or object to be passed to the search method
   * @param filterExpression The string filterExpression according to RFC 2254
   * @param searchControls The SearchControls variable, containing information on how the search will be processed
   * @return Enumeration of SearchResult-s of the qualified objects
   * @throws NamingException Thrown if NamingException occurs
   */
  public NamingEnumeration search(String name, String filterExpression, SearchControls searchControls) throws javax.naming.NamingException {
    return search(parse(name), filterExpression, null, searchControls);
  }

  /**
   * DirContext getSchemaClassDefinition using RMI Context (not implemented)
   *
   * @param name Name of a context or object
   * @return Not implemented !
   * @throws NamingException Thrown if NamingException occurs
   */
  public DirContext getSchemaClassDefinition(Name name) throws javax.naming.NamingException {
    throw new OperationNotSupportedException("Schemas addToEnvironment not supported in this version.");
  }

  /**
   * DirContext getSchemaClassDefinition using RMI Context (not implemented)
   *
   * @param name Name of a context or object
   * @return Not implemented !
   * @throws NamingException Thrown if NamingException occurs
   */
  public DirContext getSchemaClassDefinition(String name) throws javax.naming.NamingException {
    throw new OperationNotSupportedException("Schemas addToEnvironment not supported in this version.");
  }

  /**
   * DirContext getSchema using RMI Context (not implemented)
   *
   * @param name Name of a context or object
   * @return Not implemented !
   * @throws NamingException Thrown if NamingException occurs
   */
  public DirContext getSchema(Name name) throws javax.naming.NamingException {
    throw new OperationNotSupportedException("Schemas addToEnvironment not supported in this version.");
  }

  /**
   * DirContext getSchema using RMI Context (not implemented)
   *
   * @param name Name of a context or object
   * @return Not implemented !
   * @throws NamingException Thrown if NamingException occurs
   */
  public DirContext getSchema(String name) throws javax.naming.NamingException {
    throw new OperationNotSupportedException("Schemas addToEnvironment not supported in this version.");
  }

  /**
   * Context getNameParser
   *
   * @param name Name of a context or object
   * @return Name parser for the specified name
   * @throws NamingException Thrown if NamingException occurs
   */
  public NameParser getNameParser(String name) throws javax.naming.NamingException {
    return new ClientNameParser();
  }

  /**
   * Context getNameParser
   *
   * @param name Name of a context or object
   * @return Name parser for the specified name
   * @throws NamingException Thrown if NamingException occurs
   */
  public NameParser getNameParser(Name name) throws javax.naming.NamingException {
    return new ClientNameParser();
  }

  /**
   * Composes a prefix infront of a name
   *
   * @param name The given name
   * @param prefix The prefix to add to the given name
   * @return The resulted name
   * @throws NamingException Thrown if NamingException occurs
   */
  public String composeName(String name, String prefix) throws javax.naming.NamingException {
    Name result = composeName(new CompositeName(name), new CompositeName(prefix));
    return result.toString();
  }

  /**
   * Composes a prefix infront of a name
   *
   * @param name The given name
   * @param prefix The prefix to add to the given name
   * @return The resulted name
   * @throws NamingException Thrown if NamingException occurs
   */
  public Name composeName(Name name, Name prefix) throws javax.naming.NamingException {
    Name result = (Name) (prefix.clone());
    result.addAll(name);
    return result;
  }

  /**
   * Context addToEnvironment
   *
   * @param propertyName the name of the environment property to add; may not be null
   * @param propertyVal the value of the property to add; may not be null
   * @return The previous value of the property, or null if the property was not in the environment before
   * @throws NamingException Thrown if NamingException occurs
   */
  public Object addToEnvironment(String propertyName, Object propertyVal) throws javax.naming.NamingException {
    try {
      if (remoteContext == null) {
        throw new NamingException("No connection with the server.");
      }

      if (propertyName.equals("jndi.syntax.direction") | propertyName.equals("jndi.syntax.separator") | propertyName.equals("jndi.syntax.ignorecase") | propertyName
          .equals("jndi.syntax.escape") | propertyName.equals("jndi.syntax.beginquote") | propertyName.equals("jndi.syntax.endquote") | propertyName
          .equals("jndi.syntax.beginquote2") | propertyName.equals("jndi.syntax.endquote2") | propertyName.equals("jndi.syntax.trimblanks") | propertyName
          .equals("jndi.syntax.ava") | propertyName.equals("jndi.syntax.typeval")) {
        remoteContext.addToEnvironment(propertyName, (String) propertyVal, (short) 0);
      }

      if (localEnvironment != null) {
        return localEnvironment.put(propertyName, propertyVal);
      } else {
        localEnvironment = new Hashtable();
        return localEnvironment.put(propertyName, propertyVal);
      }
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing addToEnvironment operation of name " + propertyName + ".", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Connection with the server lost while performing addToEnvironment operation of name " + propertyName + ".", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform addToEnvironment operation.", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform addToEnvironment operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during addToEnvironment operation.", ex);
      }
      NamingException ne = new NamingException("Exception during addToEnvironment operation.");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * Context removeFromEnvironment
   *
   * @param propertyName the name of the environment property to add; may not be null
   * @return The previous value of the property, or null if the property was not in the environment before
   * @throws NamingException Thrown if NamingException occurs
   */
  public Object removeFromEnvironment(String propertyName) throws javax.naming.NamingException {
    if (localEnvironment != null) {
      return localEnvironment.remove(propertyName);
    } else {
      return null;
    }
  }

  /**
   * Context getEnvironment
   *
   * @return The environment of the context
   * @throws NamingException Thrown if NamingException occurs
   */
  public Hashtable getEnvironment() throws javax.naming.NamingException {
    if (localEnvironment == null) {
      localEnvironment = new Hashtable();
    }

    return localEnvironment;
  }

  /**
   * RMI object close
   *
   * @throws NamingException If a problem was encountered while closing the connection.
   */
  public void close() throws javax.naming.NamingException {
    try {
      remoteContext.close();
      remoteContext = null;
      if (loginContext != null) {
        loginContext.logout();
      }

    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "No connection with the server.", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "No connection with the server.", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (java.rmi.RemoteException ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform close operation.", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform close operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during close operation.", ex);
      }
      NamingException ne = new NamingException("Exception during close operation.");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  //  public void finalize() {
  //    try {
  //      if (remoteContext != null) {
  //        close();
  //      }
  //    } catch (NamingException ne) {
  //    }
  //  }
  /**
   * Prints the three of the naming database structure
   */
  public void print(PrintStream outStrm) throws javax.naming.NamingException {
    try {
      remoteContext.print(outStrm);
    } catch (P4ConnectionException pce) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "No connection with the server.", pce);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(pce);
      throw ne;
    } catch (ServerException nsoe) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "No connection with the server.", nsoe);
      }
      NamingException ne = new NamingException("No connection with the server.");
      ne.setRootCause(nsoe);
      throw ne;
    } catch (Exception ex) {
      String m = ex.getMessage();

      if (m != null) {
        if (m.indexOf("SecurityException") != -1) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.traceThrowableT(Severity.INFO, "No permission to perform print operation.", ex);
          }
          NoPermissionException npe = new NoPermissionException("No permission to perform print operation.");
          npe.setRootCause(ex);
          throw npe;
        }
      }

      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during print operation.", ex);
      }
      NamingException ne = new NamingException("Exception during print operation.");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  // added in jndi 1.2
  /**
   * Gets the name of the context in the namespace
   *
   * @return The name of this context.
   * @throws NamingException Thrown if NamingException occurs
   */
  public String getNameInNamespace() throws javax.naming.NamingException {
    return ((fullNameInSpace == null) ? (fullNameInSpace = new CompositeName("")) : fullNameInSpace).toString();
  }

  /**
   * Prints the three of the naming database structure
   *
   * @param outS OutputStream to write to
   * @param classes Specifies if class names and content are outputted
   * @throws NamingException Thrown if NamingException occurs
   */
  public void lsn(PrintStream outS, boolean classes) throws javax.naming.NamingException {
    outS.println("");
    outS.println("Naming Tree: ");
    outS.println("************ ");

    if (classes) {
      prntTree2(2, ((Context) (lookup(""))), outS);
    } else {
      prntTree(2, ((Context) (lookup(""))), outS);
    }

    outS.println("");
  }

  /**
   * Prints the three of the naming database structure
   *
   * @param offset The horizontal position to start printing from
   * @param where The context to recurse in
   * @param outStrm OutputStream to write to
   * @throws NamingException Thrown if NamingException occurs
   */
  public void prntTree(int offset, Context where, PrintStream outStrm) throws javax.naming.NamingException {
    NamingEnumeration je = where.list("");

    while (je.hasMoreElements()) {
      NameClassPair nameClassPair = (NameClassPair) je.nextElement();
      String name = nameClassPair.getName();
      String className = nameClassPair.getClassName();

      if ((className != null) & (className.equals("javax.naming.Context"))) {
        for (int j = 0; j < offset; j++) {
          outStrm.print(" ");
        }

        outStrm.println("* " + name + " [Context]");
        prntTree(offset + 2, (Context) where.lookup(name), outStrm);
      } else {
        for (int j = 0; j < offset; j++) {
          outStrm.print(" ");
        }

        outStrm.println(name);
      }
    }
  }

  /**
   * Prints the three of the naming database structure
   *
   * @param offset The horizontal position to start printing from
   * @param where The context to recurse in
   * @param outStrm OutputStream to write to
   * @throws NamingException Thrown if NamingException occurs
   */
  public void prntTree2(int offset, Context where, PrintStream outStrm) throws javax.naming.NamingException {
    NamingEnumeration je = where.listBindings("");

    while (je.hasMoreElements()) {
      Binding binding = (Binding) je.nextElement();
      String name = binding.getName();
      String className = binding.getClassName();

      if ((className != null) && (binding.getObject() instanceof Context)) {
        for (int j = 0; j < offset; j++) {
          outStrm.print(" ");
        }

        outStrm.println("* " + name + " [Context]");
        prntTree2(offset + 2, (Context) where.lookup(name), outStrm);
      } else {
        for (int j = 0; j < offset; j++) {
          outStrm.print(" ");
        }

        outStrm.println("+ " + name);

        for (int j = 0; j < offset + 2; j++) {
          outStrm.print(" ");
        }

        outStrm.println("[Class Name]   " + className);

        for (int j = 0; j < offset + 2; j++) {
          outStrm.print(" ");
        }

        outStrm.println("[Object Value] " + binding.getObject());
      }
    }
  }

  /**
   * Serialization for DirObject
   *
   * @param dirObject DirObject to serialize.
   * @return Serialized  (byte array) DirObject.
   * @throws Exception Thrown if DirObject can not be serialized.
   */
  protected byte[] serializeDirObject(DirObject dirObject) throws Exception {
    Attributes attributes = dirObject.getAttributes();
    String classname = dirObject.getClassName();
    Object obj = dirObject.getObject();
    //    short objType = dirObject.getType();
    byte[] result = null;
    byte[] tempAttr = null;
    byte[] tempClassName = null;
    byte[] tempObject = null;
    int offs = 0;
    int resOffs = 0;
    try {
      if (attributes == null) {
        attributes = new BasicAttributes();
      }

      tempAttr = Serializator.toByteArray(attributes);
      tempClassName = classname.getBytes();
      try {
        tempObject = serializeObject(obj);
      } catch (Exception e) {
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
        return null;
      }
      result = new byte[4 + tempAttr.length + 4 + tempClassName.length + 4 + tempObject.length];
      //      result[0] = (byte) (0x00FF & objType);
      //      result[1] = (byte) (0x00FF & (objType >> 8));
      offs = tempAttr.length;
      result[0] = (byte) (0x000000FF & offs);
      result[1] = (byte) (0x000000FF & (offs >> 8));
      result[2] = (byte) (0x000000FF & (offs >> 16));
      result[3] = (byte) (0x000000FF & (offs >> 24));
      System.arraycopy(tempAttr, 0, result, 4, offs);
      resOffs = offs + 4;
      offs = tempClassName.length;
      result[resOffs] = (byte) (0x000000FF & offs);
      result[resOffs + 1] = (byte) (0x000000FF & (offs >> 8));
      result[resOffs + 2] = (byte) (0x000000FF & (offs >> 16));
      result[resOffs + 3] = (byte) (0x000000FF & (offs >> 24));
      resOffs += 4;
      System.arraycopy(tempClassName, 0, result, resOffs, offs);
      resOffs += offs;
      offs = tempObject.length;
      result[resOffs] = (byte) (0x000000FF & offs);
      result[resOffs + 1] = (byte) (0x000000FF & (offs >> 8));
      result[resOffs + 2] = (byte) (0x000000FF & (offs >> 16));
      result[resOffs + 3] = (byte) (0x000000FF & (offs >> 24));
      resOffs += 4;
      System.arraycopy(tempObject, 0, result, resOffs, offs);
    } catch (Exception e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      throw new JNDIException("Error during s object serialization.", e);
    }
    return result;
  }

  /**
   * Deserialization for DirObject
   *
   * @param arr Byte array to deserialize.
   * @return Deserialized byte array (DirObject).
   * @throws Exception Thrown if DirObject can not be deserialized.
   */
  protected DirObject deserializeDirObject(byte[] arr) throws JNDIException {
    Attributes attributes;
    //    short objType;
    DirObject result = new DirObject();
    int resOffs = 0;
    int lngth = 0;
    //    objType = (short) (((arr[1] << 8) & 0x0000FF00) | ((arr[0] << 0) & 0x000000FF));
    //    result.setType(objType);
    try {
      lngth = (((arr[3] << 24) & 0xFF000000) | ((arr[2] << 16) & 0x00FF0000) | ((arr[1] << 8) & 0x0000FF00) | ((arr[0]) & 0x000000FF));
      resOffs = lngth + 4;
      attributes = (Attributes) (Serializator.toObject(arr, 4, lngth, false));
      result.setAttributes(attributes);
      lngth = (((arr[resOffs + 3] << 24) & 0xFF000000) | ((arr[resOffs + 2] << 16) & 0x00FF0000) | ((arr[resOffs + 1] << 8) & 0x0000FF00) | ((arr[resOffs]) & 0x000000FF));
      resOffs += 4;
      result.setClassName(new String(arr, resOffs, lngth));
      resOffs += lngth;
      lngth = (((arr[resOffs + 3] << 24) & 0xFF000000) | ((arr[resOffs + 2] << 16) & 0x00FF0000) | ((arr[resOffs + 1] << 8) & 0x0000FF00) | ((arr[resOffs]) & 0x000000FF));
      //      if (objType == Constants.LOCAL_REMOTE_OBJECT) {
      //        Object deserialized = deserializeObject(temp);
      ////        Object fromTable = RemoteSerializator.LROTable.get(deserialized);
      //        Object fromTable = null;
      //        if(deserialized != null){
      //          fromTable = RemoteSerializator.LROTable.get(deserialized);
      //        }
      //        deserialized = fromTable == null ? deserialized : fromTable;
      //        result.setObject(deserialized);
      //      } else {
      result.setObject(deserializeObject(arr, resOffs + 4, lngth));

      //      }
      if (lastobj) {
        try {
          result.setClassName(((result.getObject()).getClass()).getName());
        } catch (Exception e) {
          LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
          result.setClassName("null");
        }
      }

      return result;
    } catch (Exception e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      throw new JNDIException("Error during object deserialization. Root cause is: " + e + ".", e);
    }
  }

  /**
   * Serialization for Object
   *
   * @param object Object to serialize.
   * @return Serialized (byte array) object.
   * @throws Exception Thrown if Object can not be serialized.
   */
  public byte[] serializeObject(Object object) throws Exception {
    try {
      lastobj = false;
      byte[] bytearr = RemoteSerializator.toByteArray(object, this);
      return bytearr;
    } catch (Exception e) {
      throw new JNDIException("Error during s object serialization.", e);
    }
  }

  /**
   * Deserialization for Object
   *
   * @param data Byte array to deserialize.
   * @return Deserialized Object.
   * @throws Exception Thrown if DirObject can not be deserialized.
   */
  public Object deserializeObject(byte[] data, int offset, int length) throws Exception {
    try {
      lastobj = false;
      Object obj = RemoteSerializator.toObject(data, offset, length, this);
      commonLoader = null;
      return obj;
    } catch (Exception e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      throw e;
    }
  }

  /**
   * Sets last object flag.
   *
   * @param lo Last object ?
   */
  public void setLastObj(boolean lo) {
    lastobj = lo;
  }

  //  public boolean isDestructive() {
  //    return destructive;
  //  }
  //
  //  private void prepareDestruction(Name name, String altServerID, short operationType) {
  //    try {
  //      Object object = remoteContext.lookup(name, altServerID, operationType);
  //      destructionData = (byte[]) object;
  //    } catch (Exception e) {
  //    }
  //  }
  //
  //  private void commitDestruction() {
  //    if (destructionData != null) {
  //      destructive = true;
  //      try {
  //        deserializeDirObject(destructionData);
  //        destructionData = null;
  //      } catch (Exception e) {
  //        e.printStackTrace();
  //      }
  //      destructive = false;
  //    }
  //  }
  private Object removeReference(Name name) {
    try {
      if (fullNameInSpace == null) {
        fullNameInSpace = new CompositeName();
      }

      Name full = ((Name) ((fullNameInSpace).clone())).addAll(name);
      return referenceHash.remove(full);
    } catch (javax.naming.InvalidNameException e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      return null;
    }
  }

  private void addReference(Name name, Object value) {
    try {
      if (fullNameInSpace == null) {
        fullNameInSpace = new CompositeName();
      }

      Name full = ((Name) ((fullNameInSpace).clone())).addAll(name);
      referenceHash.put(full, value);
      remoteReferenceHash = null;
    } catch (javax.naming.InvalidNameException e) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      return;
    }
  }

  protected Object prepareStateToBind(Name name, Object obj, Attributes attr) {
    Object result = null;

    try {
      if (attr != null) {
        result = DirectoryManager.getStateToBind(obj, name, this, localEnvironment, attr);
      } else {
        result = NamingManager.getStateToBind(obj, name, this, localEnvironment);
      }
    } catch (javax.naming.NamingException ne) {
			LOG_LOCATION.traceThrowableT(Severity.PATH, "", ne);
    }
    return result != null ? result : obj;

  }

  public ServerContextInface getRemoteContext() {
    return this.remoteContext;
  }


}
