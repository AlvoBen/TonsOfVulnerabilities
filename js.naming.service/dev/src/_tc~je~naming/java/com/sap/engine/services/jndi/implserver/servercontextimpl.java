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

import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.naming.CompositeName;
import javax.naming.ContextNotEmptyException;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.NoPermissionException;
import javax.naming.NotContextException;
import javax.naming.directory.Attribute;
import javax.naming.directory.AttributeModificationException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.rmi.PortableRemoteObject;

import com.sap.engine.lib.util.HashMapObjectObject;
import com.sap.engine.services.jndi.Constants;
import com.sap.engine.services.jndi.cluster.DirObject;
import com.sap.engine.services.jndi.cluster.SecurityBase;
import com.sap.engine.services.jndi.persistent.JNDIHandle;
import com.sap.engine.services.jndi.persistent.JNDIHandleEnumeration;
import com.sap.engine.services.jndi.persistent.JNDILogConstants;
import com.sap.engine.services.jndi.persistent.JNDIPersistentRepository;
import com.sap.engine.services.jndi.persistent.Serializator;
import com.sap.engine.services.jndi.persistent.exceptions720.JNDIException;
import com.sap.engine.services.jndi.persistent.exceptions.NameNotFoundException;
import com.sap.engine.services.jndi.persistentimpl.memory.JNDIHandleImpl;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * @author Petio Petev, Elitsa Pancheva
 * @version 4.00
 */
public class ServerContextImpl extends AbstractServerContextImpl {

  private final static Location LOG_LOCATION = Location.getLocation(ServerContextImpl.class);

  /**
   * Persistent Repository interface
   */
  public JNDIPersistentRepository persistent = null;
  /**
   * Reference to parent Object
   */
  protected JNDIHandle parentObject = null;
  // private byte[] parentObjectArray = null;
  /**
   * Reference to coresponding container in Repository
   */
  protected JNDIHandle rootContainer = null;
  /**
   * Name, representing the name of the context, relative to root context
   */
  protected Name rootName = null;
  /**
   * Name to byte array cache
   */
  public static int numberOfBindings = 0;
  /**
   * String used in getLastContainer and methods, representing tha last
   * component of a name at runtime
   */
  private String lastCompoundComponentVariable = null;
  /**
   * Whether Context is disposed
   */
  private boolean disposed = false;
  /**
   * Whether Context is a remote one
   */
  private boolean remote = false;

  /**
   * Boolean flag showing wether all operations are allowed in naming or only
   * lookup
   */
  public boolean onlyLookUpAllowed = false;

  protected boolean redirectableContext = false;

  private static HashMapObjectObject keys = new HashMapObjectObject();

  /**
   * Constructor
   *
   * @param remote Denotes if the context will be used remotely
   * @throws RemoteException Thrown if a problem in initializing was encountered
   */
  public ServerContextImpl(boolean remote) throws RemoteException {
    super(remote);
    this.remote = remote;
  }
  
  /**
   * Constructor
   *
   * @param persistent JNDIPersistentRepository object - connection to the repository
   * @param parentObject -
   * JNDIHandle of object linked to the root container
   * @param container -
   * JNDIHandle of the root container
   * @param remote Denotes if the context will be used remotely
   * @throws RemoteException Thrown if a problem in initializing was encountered
   */
  public ServerContextImpl(JNDIPersistentRepository persistent, JNDIHandle parentObject, JNDIHandle container, boolean remote, boolean onlyLookUpOperation) throws RemoteException {
    super(remote);
    this.remote = remote;
    this.onlyLookUpAllowed = onlyLookUpOperation;
    this.persistent = persistent;
    this.parentObject = parentObject;
    this.rootContainer = container;
  }

  /**
   * Constructor
   *
   * @param persistent JNDIPersistentRepository object - connection to the repository
   * @param parentObject JNDIHandle of object linked to the root container
   * @param container JNDIHandle of the root container
   * @param name Initial value of ((rootName == null)?(rootName = new
   * CompositeName("")):rootName)
   * @param remote Denotes if the context will be used remotely
   * @throws RemoteException Thrown if a problem in initializing was encountered
   */
  public ServerContextImpl(JNDIPersistentRepository persistent, JNDIHandle parentObject, JNDIHandle container, Name name, boolean remote) throws RemoteException {
    super(remote);
    this.remote = remote;
    this.persistent = persistent;
    this.parentObject = parentObject;
    this.rootContainer = container;
    rootName = name;
  }

  /**
   * Get the context's name
   *
   * @return The Context's name
   * @throws javax.naming.NamingException Thrown if problems occured in determining the name of the
   * context
   */
  public Name getCtxName() throws javax.naming.NamingException {
    try {
      return new CompositeName(persistent.getObjectName(parentObject));
    } catch (NamingException de) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", de);
      NamingException ne = new NamingException("Data Exception while trying to determine the name of the context.");
      if (!(de instanceof JNDIException)) ne.setRootCause(de);
      throw ne;
    }
  }

  /**
   * Finds JNDIHandle of the container, represented in the before last
   * component in the name, processes composite names.
   *
   * @param container the container to start from
   * @param name Name to process
   * @return handle to last container in the name
   * @throws javax.naming.NamingException Thrown if problems occured in determining the name of the
   * context
   */
  protected JNDIHandle getLastContainer(JNDIHandle container, Name name) throws javax.naming.NamingException {
    try {
      JNDIHandle tempContainer = null;
      JNDIHandle containerCandidate = null;
      int count = name.size() - 1;

      if (count == 0) {
        String fullName = container.getContainerName();
        int index = fullName.lastIndexOf("/");
        if (index != -1 && index != 0) {
          containerCandidate = new JNDIHandleImpl(fullName.substring(0, index), fullName.substring(index + 1));
          tempContainer = persistent.getLinkedContainer(containerCandidate);
        }
      } else {
        StringBuilder suffix = null;
        if (rootName == null) {
          suffix = new StringBuilder(count * 16);
        } else {
          suffix = new StringBuilder((rootName.size() + count) * 16);
        }

        for (int i = 0; i < count - 1; i++) {
          suffix.append("/").append(name.get(i));
        }

        containerCandidate = new JNDIHandleImpl(suffix.insert(0, container.getContainerName()).toString(), name.get(count - 1));
        tempContainer = persistent.getLinkedContainer(containerCandidate);
      }

      if (tempContainer == null) {
        tempContainer = container;
        containerCandidate = null;
        for (int componentIndex = 0; componentIndex < count; componentIndex++) {
          String compositeString = name.get(componentIndex);
          containerCandidate = persistent.findObject(tempContainer, compositeString);

          if (containerCandidate == null) {
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.infoT("Path to object does not exist at " + compositeString + ", the whole lookup name is " + name + ".");
            }
            if(!remote) {
              com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException ne = new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException(JNDILogConstants.PATH_TO_OBJECT_DOES_NOT_EXISTS + compositeString + ", the whole lookup name is " + name + ".");
              if (!Constants.NON_SERIALIZABLE_REBIND_OLD_BEHAVIOUR) {
                ne.missingPathComponent = true;
              }
              throw ne;              
            } else {
              NameNotFoundException nnfe = new NameNotFoundException(NameNotFoundException.PATH_TO_OBJECT_DOES_NOT_EXISTS, new Object[]{compositeString, name});
              if (!Constants.NON_SERIALIZABLE_REBIND_OLD_BEHAVIOUR) {
                nnfe.missingPathComponent = true;
              }
              throw nnfe;
            }
          }

          tempContainer = persistent.getLinkedContainer(containerCandidate);

          if (tempContainer == null) {
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.infoT("Intermediate context " + compositeString + " not found, the whole lookup name is " + name + ".");
            }
            throw new NamingException("Intermediate context " + compositeString + " not found, the whole lookup name is " + name + ".");
          }
        }
      }
      try {
        lastCompoundComponentVariable = name.get(count);
      } catch (Exception e) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Incorrect name " + name + " is passed to operation.", e);
        }
        NamingException ne = new NamingException("Incorrect name " + name + " is passed to operation.");
        ne.setRootCause(e);
        throw ne;
      }
      return tempContainer;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception in processing name parameter " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception in processing name parameter " + name + ".");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Finds JNDIHandle of the context object, represented in the before last
   * component in the name, processes composite names.
   *
   * @param container the container to start from
   * @param name Name to process
   * @return handle to last context in the name
   * @throws javax.naming.NamingException Thrown if problems occured in determining the name of the
   * context
   */
  protected JNDIHandle getLastContextObject(JNDIHandle container, Name name) throws javax.naming.NamingException {
    try {
      JNDIHandle tempContainer = null;
      JNDIHandle containerCandidate = null;
      int count = name.size() - 1;

      if (count == 0) {
        String fullName = container.getContainerName();
        int index = fullName.lastIndexOf("/");
        if (index != -1 && index != 0) {
          containerCandidate = new JNDIHandleImpl(fullName.substring(0, index), fullName.substring(index + 1));
          tempContainer = persistent.getLinkedContainer(containerCandidate);
        }
      } else {
        StringBuilder suffix = null;
        if (rootName == null) {
          suffix = new StringBuilder(count * 16);
        } else {
          suffix = new StringBuilder((rootName.size() + count) * 16);
        }

        for (int i = 0; i < count - 1; i++) {
          suffix.append("/").append(name.get(i));
        }
        containerCandidate = new JNDIHandleImpl(suffix.insert(0, container.getContainerName()).toString(), name.get(count - 1));
        tempContainer = persistent.getLinkedContainer(containerCandidate);
      }

      if (tempContainer == null) {
        tempContainer = container;
        containerCandidate = null;
        for (int componentIndex = 0; componentIndex < count; componentIndex++) {
          String compositeString = name.get(componentIndex);
          containerCandidate = persistent.findObject(tempContainer, compositeString);

          if (containerCandidate == null) {
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.infoT("Path to object does not exist at " + compositeString + ", the whole lookup name is " + name + ".");
            }
            if(!remote) {
              com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException ne = new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException(JNDILogConstants.PATH_TO_OBJECT_DOES_NOT_EXISTS + compositeString + ", the whole lookup name is " + name + ".");
              if (!Constants.NON_SERIALIZABLE_REBIND_OLD_BEHAVIOUR) {
                ne.missingPathComponent = true;
              }
              throw ne;   
            } else {
              NameNotFoundException nnfe = new NameNotFoundException(NameNotFoundException.PATH_TO_OBJECT_DOES_NOT_EXISTS, new Object[]{compositeString, name});
              if (!Constants.NON_SERIALIZABLE_REBIND_OLD_BEHAVIOUR) {
                nnfe.missingPathComponent = true;
              }
              throw nnfe;
            }
          }

          tempContainer = persistent.getLinkedContainer(containerCandidate);

          if (tempContainer == null) {
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.infoT("Intermediate context " + compositeString + " not found, the whole lookup name is " + name + ".");
            }
            throw new NamingException("Intermediate context " + compositeString + " not found, the whole lookup name is " + name + ".");
          }
        }
      }
      try {
        lastCompoundComponentVariable = name.get(count);
      } catch (Exception e) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Incorrect name " + name + " is passed to operation.", e);
        }
        NamingException ne = new NamingException("Incorrect name " + name + " is passed to operation.");
        ne.setRootCause(e);
        throw ne;
      }
      return containerCandidate;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception in processing name parameter " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception in processing name parameter " + name + ".");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }


  /**
   * @param name The name to be bound
   * @param objectData The object that the name will be bound to
   * @param type The type of the operation
   * @param lastSerializationType
   * @throws javax.naming.NamingException Thrown when NamingException occurs
   * @throws RemoteException Thrown when RemoteException occurs
   */
  public void bind(Name name, byte[] objectData, short type, boolean lastSerializationType) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform bind operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform bind operation.");
      }
    }

    try {
      if (name.size() == 0) { // empty name
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Cannot bind an empty name.");
        }
        throw new InvalidNameException("Cannot bind an empty name.");
      }

      // is the path valid
      JNDIHandle lastContext = getLastContextObject(rootContainer, name);

      JNDIHandle lastContainer = rootContainer;
      if (lastContext != null) {
        lastContainer = persistent.getLinkedContainer(lastContext);
      }

      // synchronize the operation using container id
      synchronized (getLockObject(lastContainer.getContainerName())) {

        JNDIHandle object = persistent.findObject(lastContainer, lastCompoundComponentVariable);

        if (object == null) { // object doesn't exist

          // check whether the context is replicated
          if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
//                    	if (lastContext != null && (persistent.getObjectType(lastContext) != Constants.REPLICATED_OPERATION)) {
//                    		System.out.println("<ServerContext> bind getObjectType(lastContext) -> " + persistent.getObjectType(lastContext));
//                    		throw new NamingException("\r\nID007115 : Attempt to bind a global (replicated) object in local (nonreplicated) context");
//                    	}
          } else {
            if (lastContext != null && (persistent.getObjectType(lastContext) == Constants.REPLICATED_OPERATION)) {
              if (LOG_LOCATION.beInfo()) {
                LOG_LOCATION.infoT("Attempt to bind a local (non-replicated) object " + name + " in a global (replicated) context.");
              }
              throw new NamingException("Attempt to bind a local (non-replicated) object " + name + " in a global (replicated) context.");
            }
          }

          persistent.bindObject(lastContainer, lastCompoundComponentVariable, objectData, type);
          increaseNumberOfBindings();
        } else { // object exists!
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("Object with name " + lastCompoundComponentVariable + " is already bound.");
          }
          throw new NameAlreadyBoundException("Object with name " + lastCompoundComponentVariable + " is already bound.");
        }
      }
    } catch (NoPermissionException np) {
      throw np;
    } catch (InvalidNameException in) {
      throw in;
    } catch (NameAlreadyBoundException nab) {
      throw nab;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (JNDIException jde) {
      if (jde.getExceptionType() == JNDIException.NAME_ALREADY_BOUND) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Object with name " + lastCompoundComponentVariable + " is already bound.");
        }
        NameAlreadyBoundException nabe = new NameAlreadyBoundException("Object with name " + lastCompoundComponentVariable + " is already bound.");
        throw nabe;
      }
    } catch (NamingException jde) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", jde);
      NamingException ne = new NamingException("Exception during bind operation of object with name " + name + ".");
      ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * @param name The name to be rebound
   * @param objectData The object that the name will be bound to
   * @param type The type of the operation
   * @param lastSerializationType
   * @throws javax.naming.NamingException Thrown when NamingException occurs
   * @throws RemoteException Thrown when RemoteException occurs
   */
  public void rebind(Name name, byte[] objectData, short type, boolean lastSerializationType) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform rebind operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform rebind operation.");
      }
    }

    try {

      JNDIHandle lastContext = getLastContextObject(rootContainer, name);

      JNDIHandle lastContainer = rootContainer;

      if (lastContext != null) {
        lastContainer = persistent.getLinkedContainer(lastContext);
      }

      // synchronize the operation using container id
      synchronized (getLockObject(lastContainer.getContainerName())) {

        JNDIHandle object = persistent.findObject(lastContainer, lastCompoundComponentVariable);

        if (object != null) {
          if (persistent.getLinkedContainer(object) != null) { // context
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.infoT("Attempt to rebind context with name " + name + ".");
            }
            throw new NamingException("Attempt to rebind context with name " + name + ".");
          } else { // object
            if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
              short objectType = persistent.getObjectType(object);
              if (objectType == Constants.REPLICATED_OPERATION || objectType == Constants.REMOTE_REPLICATED_OPERATION || objectType == Constants.NOT_DEFINED_OBJECT) {
                persistent.unbindObject(object, type);
                decreaseNumberOfBindings();
              } else {
                if (LOG_LOCATION.beInfo()) {
                  LOG_LOCATION.infoT("Attempt to rebind a global (replicated) object over a local (non-replicated) object with name " + name + ".");
                }
                throw new NamingException("Attempt to rebind a global (replicated) object over a local (non-replicated) object with name " + name + ".");
              }
            } else { //check if a non replicated operation is going to be applied to a replicated object
              if (persistent.getObjectType(object) == Constants.REPLICATED_OPERATION || persistent.getObjectType(object) == Constants.REMOTE_REPLICATED_OPERATION) {
                if (LOG_LOCATION.beInfo()) {
                  LOG_LOCATION.infoT("Attempt to rebind a local (non-replicated) object over a global (replicated) object with name " + name + ".");
                }
                throw new NamingException("Attempt to rebind a local (non-replicated) object over a global (replicated) object with name " + name + ".");
              } else {
                persistent.unbindObject(object, type);
                decreaseNumberOfBindings();
              }
            }
          }
        } else { // no object with this name -> bind
          // check whether the context is replicated
          if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
            // if (lastContext != null &&
            // (persistent.getObjectType(lastContext) !=
            // Constants.REPLICATED_OPERATION)) {
            // throw new NamingException("\r\nID007126 : Attempt to
            // rebind global (replicated) object over a local (non
            // replicated) object");
            // }
          } else {
            if (lastContext != null && (persistent.getObjectType(lastContext) == Constants.REPLICATED_OPERATION)) {
              if (LOG_LOCATION.beInfo()) {
                LOG_LOCATION.infoT("Attempt to rebind a local (non-replicated) object in a global (replicated) context, the object name is " + name + ".");
              }
              throw new NamingException("Attempt to rebind a local (non-replicated) object in a global (replicated) context, the object name is " + name + ".");
            }
          }
        }

        object = persistent.bindObject(lastContainer, lastCompoundComponentVariable, objectData, type);
        increaseNumberOfBindings();
      }
    } catch (NoPermissionException np) {
      throw np;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (JNDIException jde) {
      if (jde.getExceptionType() == JNDIException.NAME_ALREADY_BOUND) {
        if (LOG_LOCATION.bePath()) {
          LOG_LOCATION.pathT("Exception during rebind operation of object with name " + name + ".");
          LOG_LOCATION.traceThrowableT(Severity.PATH, "Object with name " + lastCompoundComponentVariable + " is already bound.", jde);
        }
        rebind(name, objectData, type, lastSerializationType);
      }
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during rebind operation of object with name " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during rebind operation of object with name " + name + ".");
      ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * If name is empty name, returns instance of this context. When the name
   * leads to subcontext, returns instance of its context. Both instances may
   * be accessed concurrently
   *
   * @param name The name of the object to be looked up
   * @param type The type of the operation
   * @return The looked up object
   * @throws javax.naming.NamingException Thrown if problem is encountered when looking up the name
   * @throws RemoteException Thrown if problem is encountered when looking up the name
   */
  public Object lookup(Name name, short type) throws javax.naming.NamingException, RemoteException {
    try {
      if (name.size() == 0) { // empty name
        /* returns new server Context impl, reusing from pool */
        ServerContextImpl server = new ServerContextImpl(remote);
        server.reuse(persistent.getNewConnection(),
            parentObject,
            rootContainer,
            (Name) (((rootName == null) ? (rootName = new CompositeName("")) : rootName).clone()),
            onlyLookUpAllowed);
        return server;
      }
      // is the path valid
      JNDIHandle lastContainer = getLastContainer(rootContainer, name);
      if (lastContainer == null) {
        lastContainer = this.rootContainer;
      }

      // synchronize the operation using container id
      synchronized (getLockObject(lastContainer.getContainerName())) {

        /* GET LINKED CONTAINER AND LOOKUP */
        JNDIHandle object = persistent.findObject(lastContainer, lastCompoundComponentVariable);
        if (object == null) { // object doesn't exist
          // // check if the object is looked up in root conext, it
          // might be service, and must try to look it up in sap.com
          // context
          // if (lastContainer.getContainerID() ==
          // rootContainer.getContainerID()) {
          // lastContainer = persistent.findObject(rootContainer,
          // Constants.DEFAULT_SERVICE_PROVIDER_NAME);
          // if (lastContainer != null) {
          // lastContainer =
          // persistent.getLinkedContainer(lastContainer);
          // object = persistent.findObject(lastContainer,
          // lastCompoundComponentVariable);
          // if (object != null) {
          // JNDIHandle objectContainer =
          // persistent.getLinkedContainer(object);
          // if (objectContainer == null) {
          // return persistent.readObject(object);
          // }
          // }
          // }
          // }
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("Object not found in lookup of " + lastCompoundComponentVariable + ".");
          }
          if(!remote) {
            throw new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException(JNDILogConstants.NAME_NOT_FOUND_IN_LOOKUP + lastCompoundComponentVariable + ".");
          } else {
            throw new NameNotFoundException(NameNotFoundException.NAME_NOT_FOUND_IN_LOOKUP, new Object[]{lastCompoundComponentVariable});
          }
        } else {

          JNDIHandle objectContainer = persistent.getLinkedContainer(object);

          if (objectContainer != null) {
            // if the object is container, return new instance
            ServerContextImpl server = new ServerContextImpl(remote);
            server.reuse(persistent.getNewConnection(),
                object,
                objectContainer,
                ((Name) (((rootName == null) ? (rootName = new CompositeName("")) : rootName).clone())).addAll(name),
                onlyLookUpAllowed);
            return server;
          }

          byte[] result = persistent.readObject(object);

          return result;
        }
      }
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during lookup operation of object with name " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during lookup operation of object with name " + name + ".");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * If the object with that name does not exist, doesn't throw any exception
   * but exits the method.
   *
   * @param name The name of the object to be unbound
   * @param type The type of the operation
   * @throws javax.naming.NamingException If problem occurs while unbinding the object
   * @throws RemoteException If problem occurs while unbinding the object
   */
  public void unbind(Name name, short type) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform unbind operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform unbind operation.");
      }
    }

    try {

      // is the path valid
      JNDIHandle lastContainer = getLastContainer(rootContainer, name);
      if (lastContainer == null) {
        lastContainer = this.rootContainer;
      }

      // synchronize the operation using container id

      synchronized (getLockObject(lastContainer.getContainerName())) {

        JNDIHandle object = persistent.findObject(lastContainer, lastCompoundComponentVariable);

        if (object == null) { // object not found - just exit without an exception
          return;
        } else {
          if (persistent.getLinkedContainer(object) != null) {
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.infoT("Cannot unbind context with name " + name + ". Reason: the unbind operation can be used only for objects.");
            }
            throw new NamingException("Cannot unbind context with name " + name + ". Reason: the unbind operation can be used only for objects.");
          } else {
            // is not context - its normal object
            // check whether the object is replicated in case the unbind
            // operation is replicated
            if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
              short objectType = persistent.getObjectType(object);
              if (objectType == Constants.REPLICATED_OPERATION || objectType == Constants.REMOTE_REPLICATED_OPERATION || objectType == Constants.NOT_DEFINED_OBJECT) {
                persistent.unbindObject(object, type);
                decreaseNumberOfBindings();
              } else {
                if (LOG_LOCATION.beInfo()) {
                  LOG_LOCATION.infoT("Attempt for a global (replicated) unbind over a local (non-replicated) object with name " + name + ".");
                }
                throw new NamingException("Attempt for a global (replicated) unbind over a local (non-replicated) object with name " + name + ".");
              }
            } else { //check if a non replicated operation is going to be applied to a replicated object
              if (persistent.getObjectType(object) == Constants.REPLICATED_OPERATION || persistent.getObjectType(object) == Constants.REMOTE_REPLICATED_OPERATION) {
                if (LOG_LOCATION.beInfo()) {
                  LOG_LOCATION.infoT("Attempt for a local (non-replicated) unbind over a global (replicated) object with name " + name + ".");
                }
                if (LOG_LOCATION.bePath()) {
                  LOG_LOCATION.traceThrowableT(Severity.PATH, "The thread dump of the caller that makes the local unbind over global object follows.", new Exception());
                }

                persistent.unbindObject(object, Constants.NOT_REPLICATED_OPERATION);
                decreaseNumberOfBindings();
              } else {
                persistent.unbindObject(object, type);
                decreaseNumberOfBindings();
              }
            }
          }
        }
      }
    } catch (NoPermissionException np) {
      throw np;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during unbind operation of object with name " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during unbind operation of object with name " + name + ".");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * @param oldName The old name of the object
   * @param newName The new name of the object
   * @param type Type of operation
   * @throws javax.naming.NamingException Thrown when a NamingException occurs while renaming
   * @throws RemoteException Thrown when a RemoteException occurs while renaming
   */
  public void rename(Name oldName, Name newName, short type) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform rename operation of name " + oldName + ".");
        }
        throw new NoPermissionException("No permission to perform rename operation.");
      }
    }

    try {

      if (newName.size() == 0 || oldName.size() == 0) { // empty name
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Incorrect usage of the rename operation. Reason: at least one of the specified name parameters is an empty string. The new name is " + newName + ", the old name is " + oldName + ".", new Object[]{,});
        }
        throw new InvalidNameException("Incorrect usage of the rename operation. Reason: at least one of the specified name parameters is an empty string. The new name is " + newName + ", the old name is " + oldName + ".");
      }

      if (oldName.equals(newName)) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Cannot rename " + oldName.get(newName.size() - 1) + " to " + newName.get(newName.size() - 1) + ". An object is already bound to the new name.", new Object[]{,});
        }
        throw new NameAlreadyBoundException("Cannot rename " + oldName.get(newName.size() - 1) + " to " + newName.get(newName.size() - 1) + ". An object is already bound to the new name.");
      }

      // is the path valid
      JNDIHandle oldLastContainer = getLastContainer(rootContainer, oldName);
      if (oldLastContainer == null) {
        oldLastContainer = this.rootContainer;
      }
      String oldNameLastComponent = lastCompoundComponentVariable;
      JNDIHandle newLastContainer = getLastContainer(rootContainer, newName);

      // synchronize the operation using container id
      synchronized (getLockObject(oldLastContainer.getContainerName())) {

        if (newLastContainer == null) {
          newLastContainer = rootContainer;
        }

        JNDIHandle object = persistent.findObject(oldLastContainer, oldNameLastComponent);

        if (object == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("Rename operation failed. Object " + oldNameLastComponent + "does not exist.");
          }
          if(!remote) {
            throw new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException("Rename operation failed. Object " + oldNameLastComponent + " does not exist.");
          } else {
            throw new NameNotFoundException(NameNotFoundException.NAME_NOT_FOUND_IN_RENAME, new Object[]{oldNameLastComponent});
          }
        }

        if (persistent.findObject(newLastContainer, lastCompoundComponentVariable) != null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("Cannot rename " + oldName.get(newName.size() - 1) + " to " + lastCompoundComponentVariable + ". There is object already bound to the new name.");
          }
          throw new NameAlreadyBoundException("Cannot rename " + oldName.get(newName.size() - 1) + " to " + lastCompoundComponentVariable + ". There is object already bound to the new name.");
        }

        if (oldLastContainer.getContainerName().equals(newLastContainer.getContainerName())) {
          // check whether object which have to be renamed is
          // replicated in case the rename operation is replicated
          if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
            short objectType = persistent.getObjectType(object);
            if (objectType == Constants.REPLICATED_OPERATION || objectType == Constants.REMOTE_REPLICATED_OPERATION || objectType == Constants.NOT_DEFINED_OBJECT) {
              persistent.renameObject(oldLastContainer, oldNameLastComponent, lastCompoundComponentVariable, type);
              return;
            } else {
              if (LOG_LOCATION.beInfo()) {
                LOG_LOCATION.infoT("Attempt for a global (replicated) rename operation over a local (non-replicated) object with name " + oldName.get(newName.size() - 1) + ".");
              }
              throw new NamingException("Attempt for a global (replicated) rename operation over a local (non-replicated) object with name " + oldName.get(newName.size() - 1) + ".");
            }
          } else { //check if a non replicated operation is going to be applied to a replicated object
            if (persistent.getObjectType(object) == Constants.REPLICATED_OPERATION || persistent.getObjectType(object) == Constants.REMOTE_REPLICATED_OPERATION) {
              if (LOG_LOCATION.beInfo()) {
                LOG_LOCATION.infoT("Attempt for a local (non-replicated) rename operation over a global (replicated) object with name " + oldName.get(newName.size() - 1) + ".");
              }
              throw new NamingException("Attempt for a local (non-replicated) rename operation over a global (replicated) object with name " + oldName.get(newName.size() - 1) + ".");
            } else {
              persistent.renameObject(oldLastContainer, oldNameLastComponent, lastCompoundComponentVariable, type);
              return;
            }
          }
        } else {
          // move object
          // check whether the object is replicated in case the rename
          // operation is replicated
          if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
            // check whether the new context is replicated in case the rename
            // operation is replicated
            // if (newLastContext != null && persistent.getObjectType(newLastContext) !=
            // Constants.REPLICATED_OPERATION) {
            // throw new NamingException("\r\nID007145 : Attempt for a global
            // (replicated) rename/move operation in a local (non replicated) context");
            // }

            short objectType = persistent.getObjectType(object);
            if (objectType == Constants.REPLICATED_OPERATION || objectType == Constants.REMOTE_REPLICATED_OPERATION || objectType == Constants.NOT_DEFINED_OBJECT) {
              persistent.moveObject(oldLastContainer, oldNameLastComponent, newLastContainer, lastCompoundComponentVariable, type);
              return;
            } else {
              if (LOG_LOCATION.beInfo()) {
                LOG_LOCATION.infoT("Attempt for a global (replicated) rename operation over a local (non-replicated) object with name " + oldName.get(newName.size() - 1) + ".");
              }
              throw new NamingException("Attempt for a global (replicated) rename operation over a local (non-replicated) object with name " + oldName.get(newName.size() - 1) + ".");
            }
          } else { //check if a non replicated operation is going to be applied to a replicated object
            if (persistent.getObjectType(object) == Constants.REPLICATED_OPERATION || persistent.getObjectType(object) == Constants.REMOTE_REPLICATED_OPERATION) {
              if (LOG_LOCATION.beInfo()) {
                LOG_LOCATION.infoT("Attempt for a local (non-replicated) rename operation over a global (replicated) object with name " + oldName.get(newName.size() - 1) + ".");
              }
              throw new NamingException("Attempt for a local (non-replicated) rename operation over a global (replicated) object with name " + oldName.get(newName.size() - 1) + ".");
            } else {
              persistent.moveObject(oldLastContainer, oldNameLastComponent, newLastContainer, lastCompoundComponentVariable, type);
              return;
            }
          }
        }
      }
    } catch (NoPermissionException np) {
      throw np;
    } catch (InvalidNameException in) {
      throw in;
    } catch (NameAlreadyBoundException nab) {
      throw nab;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (JNDIException jde) { 
      if (jde.getExceptionType() == JNDIException.NAME_ALREADY_BOUND) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Cannot rename " + oldName.get(newName.size() - 1) + " to " + lastCompoundComponentVariable + ". There is object already bound to the new name.", jde);
        }
        throw new NameAlreadyBoundException("Cannot rename " + oldName.get(newName.size() - 1) + " to " + lastCompoundComponentVariable + ". There is object already bound to the new name.");
      }
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during rename operation of object with old name " + oldName + " and new name " + newName + ".", jde);
      }
      NamingException ne = new NamingException("Exception during rename operation of object with old name " + oldName + " and new name " + newName + ".");
      ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Creates subContext. The last component of name points to a context.
   *
   * @param name The name of the newly created context
   * @param attr The attributes to put on the new context
   * @param type Type of operation
   * @return An instance of the newly created context
   * @throws javax.naming.NamingException If NamingException is thrown while creating the context.
   * @throws RemoteException If RemoteException is thrown while creating the context.
   */
  public ServerContextInface createSubcontext(Name name, Attributes attr, short type) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform createSubcontext operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform createSubcontext operation.");
      }
    }
    try {
      if (name.size() == 0) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Cannot create a context over the root context.");
        }
        throw new NameAlreadyBoundException("Cannot create a context over the root context.");
      }

      JNDIHandle lastContainer = getLastContainer(this.rootContainer, name);

      if (lastContainer == null) {
        lastContainer = this.rootContainer;
      }

      // synchronize the operation using container id
      synchronized (getLockObject(lastContainer.getContainerName())) {

        JNDIHandle contextObject = persistent.findObject(lastContainer, lastCompoundComponentVariable);

        if (contextObject != null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("createSubcontext operation failed; context " + lastCompoundComponentVariable + " already exists.");
          }
          throw new NameAlreadyBoundException("createSubcontext operation failed; context " + lastCompoundComponentVariable + " already exists.");
        }

        Properties properties = new Properties();
        try {
          if (lastContainer != null) {
            properties = (Properties) (Serializator.toObject(persistent.readContainer(lastContainer)));
          }
        } catch (Exception e) {
          // Excluding this catch block from JLIN $JL-EXC$ since there's no
          // need to log this exception
          // Please do not remove this comment!
          // DO NOTHING - No properties!
          properties = new Properties();
        }

        //params: object's container name; object name; containerData; objectData; operation type;
        JNDIHandle newContainer = persistent.createSubcontext(persistent.getContainerName(lastContainer), lastCompoundComponentVariable, Serializator.toByteArray(properties), DirObject.getNewDirObject(attr, null), type);

        /* returns new server Context impl, reusing from pool */
        ServerContextImpl newContext = new ServerContextImpl(remote);
        newContext.reuse(persistent.getNewConnection(),
            new JNDIHandleImpl(persistent.getContainerName(lastContainer), lastCompoundComponentVariable),
            newContainer,
            ((Name) (((rootName == null) ? (rootName = new CompositeName("")) : rootName).clone())).addAll(name),
            onlyLookUpAllowed);

        return newContext;
      }
    } catch (NoPermissionException np) {
      throw np;
    } catch (NameAlreadyBoundException nab) {
      throw nab;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (JNDIException jde) {
      if (jde.getExceptionType() == JNDIException.NAME_ALREADY_BOUND) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "createSubcontext operation failed; context " + lastCompoundComponentVariable + " already exists.", jde);
        }
        throw new NameAlreadyBoundException("createSubcontext operation failed; context " + lastCompoundComponentVariable + " already exists.");
      } //if JNDIException is caught then its exception type may not be equal to NAME_ALREADY_BOUND if the parent context is not found
      throw jde;
      
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during createSubcontext operation of context with name " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during createSubcontext operation of context with name " + name + ".");
      ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Destroys subContext. The last component of name points to a context. If
   * the context doesn't exist, does nothing. If the pointed context is not
   * empty, throws ContextNotEmptyException. If any of intermediate context
   * does not exist, throws NameNotFoundException. If the object is not a
   * context, throws NotContextException.
   *
   * @param name The name of the context to be destroyed
   * @param type Type of operation
   * @throws javax.naming.NamingException If NamingException is thrown while destroying the context.
   * @throws RemoteException If RemoteException is thrown while destroying the context.
   */
  public void destroySubcontext(Name name, short type) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform destroySubcontext operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform destroySubcontext operation.");
      }
    }

    try {

      if (name.size() == 0) { // empty name
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Cannot destroy the current context.");
        }
        throw new NamingException("Cannot destroy the current context.");
      }

      // is the path valid
      JNDIHandle lastContainer = getLastContainer(rootContainer, name);
      if (lastContainer == null) {
        lastContainer = this.rootContainer;
      }

      // synchronize the operation using container id
      synchronized (getLockObject(lastContainer.getContainerName())) {
        // get the named object handle
        JNDIHandle lastContainerObject = persistent.findObject(lastContainer, lastCompoundComponentVariable);

        if (lastContainerObject == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("destroySubcontext operation failed; context " + lastCompoundComponentVariable + " not found.");
          }
          if(!remote) {
            throw new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException("destroySubcontext operation failed; context " + lastCompoundComponentVariable + " not found.");
          } else {
            throw new NameNotFoundException(NameNotFoundException.NAME_NOT_FOUND_IN_DESTROY_SUBCONTEXT, new Object[]{lastCompoundComponentVariable});
          }
          
        }

        JNDIHandle toDeleteContainer = persistent.getLinkedContainer(lastContainerObject);

        if (toDeleteContainer == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("destroySbcontext operation failed; " + lastCompoundComponentVariable + " either is not a context, or is a foreign context.");
          }
          throw new NotContextException("destroySbcontext operation failed; " + lastCompoundComponentVariable + " either is not a context, or is a foreign context.");
        } else {
          // check if the container is empty
          // synchronize the operation using container id
          String cName = toDeleteContainer.getContainerName();
          synchronized (getLockObject(cName)) {
            try {
              JNDIHandleEnumeration en = persistent.listObjects(toDeleteContainer, "*");
              try {
                if (en.hasMoreElements()) {
                  // print out the contents of the container (debug only)
                  if (LOG_LOCATION.bePath()) {
                    while (en.hasMoreElements()) {
                      LOG_LOCATION.pathT("Cannot destroy not empty context[" + lastCompoundComponentVariable + "]. Objects left in this context are: ");
                      LOG_LOCATION.pathT("  -> " + persistent.getObjectName(en.nextObject()));
                    }
                  }

                  if (LOG_LOCATION.beInfo()) {
                    LOG_LOCATION.infoT("destroySubcontext operation failed; context " + lastCompoundComponentVariable + " is not empty.");
                  }
                  throw new ContextNotEmptyException("destroySubcontext operation failed; context " + lastCompoundComponentVariable + " is not empty.");
                }
              } finally {
                en.closeEnumeration();
              }

              //chech whether the object is replicated in case the destroy context operation is replicated
              if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
                short objectType = persistent.getObjectType(lastContainerObject);
                if (objectType == Constants.REPLICATED_OPERATION || objectType == Constants.REMOTE_REPLICATED_OPERATION || objectType == Constants.NOT_DEFINED_OBJECT) {
                  persistent.destroySubcontext(toDeleteContainer.getContainerName(), lastContainerObject, type);
                } else {
                  if (LOG_LOCATION.beInfo()) {
                    LOG_LOCATION.infoT("Attempt for a global (replicated) destroySubcontext operation over a local (non-replicated) context with name " + lastCompoundComponentVariable + ".");
                  }
                  throw new NamingException("Attempt for a global (replicated) destroySubcontext operation over a local (non-replicated) context with name " + lastCompoundComponentVariable + ".");
                }
              } else {
                persistent.destroySubcontext(toDeleteContainer.getContainerName(), lastContainerObject, type);
              }
            } finally {
              unlockObject(cName);
            }
          }
        }
      }
    } catch (NoPermissionException np) {
      throw np;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (NotContextException nc) {
      throw nc;
    } catch (ContextNotEmptyException cne) {
      throw cne;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during destroySubcontext operation of context with name " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during destroySubcontext operation of context with name " + name + ".");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Returns enumeration of Bindings in the Context
   *
   * @param name The name of the context to be listed
   * @param type Type of operation
   * @return Enumeration of the Binding-s, according to objects in the context
   * @throws javax.naming.NamingException If NamingException is thrown while listing the context.
   * @throws RemoteException If RemoteException is thrown while listing the context.
   */
  public ServerNamingEnum listBindings(Name name, short type, boolean onserver) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform listBindings operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform listBindings operation.");
      }
    }

    try {
      JNDIHandle lastContainer;

      if (name.size() == 0) {
        lastContainer = this.rootContainer;
      } else {

        lastContainer = getLastContainer(rootContainer, name);
        if (lastContainer == null) {
          lastContainer = this.rootContainer;
        }
        JNDIHandle object = persistent.findObject(lastContainer, lastCompoundComponentVariable);

        if (object == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("listBindings operation failed; path to object " + lastCompoundComponentVariable + " does not exist.");
          }
          if(!remote) {
            throw new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException("listBindings operation failed; path to object " + lastCompoundComponentVariable + " does not exist.");
          } else {
            throw new NameNotFoundException(NameNotFoundException.PATH_TO_OBJECT_DOESNOT_EXISTS_IN_LISTBINDINGS, new Object[]{lastCompoundComponentVariable});
          }
        }

        lastContainer = persistent.getLinkedContainer(object);

        if (lastContainer == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("listBindings operation failed; " + lastCompoundComponentVariable + " either is not a context, or is a foreign context.");
          }
          throw new NotContextException("listBindings operation failed; " + lastCompoundComponentVariable + " either is not a context, or is a foreign context.");
        }

      }

      // synchronize the operation using container id
      synchronized (getLockObject(lastContainer.getContainerName())) {
        JNDIHandleEnumeration jhe = persistent.listObjects(lastContainer, "*");
        return new ServerNamingEnumImpl(persistent.getNewConnection(), jhe, ServerNamingEnumImpl.TYPE_BINDING, ((rootName == null) ? (rootName = new CompositeName("")) : rootName), onlyLookUpAllowed, remote, redirectableContext);
      }
    } catch (NoPermissionException np) {
      throw np;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (NotContextException nc) {
      throw nc;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during listBindings operation of context with name " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during listBindings operation of context with name " + name + ".");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Returns enumeration of ClassNamePairs in the Context
   *
   * @param name The name of the context to be listed
   * @param type Type of operation
   * @return Enumeration of the NameClassPair-s, according to objects in the
   *         context
   * @throws javax.naming.NamingException If NamingException is thrown while listing the context.
   * @throws RemoteException If RemoteException is thrown while listing the context.
   */
  public ServerNamingEnum list(Name name, short type, boolean onserver) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform list operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform list operation.");
      }
    }

    try {
      JNDIHandle lastContainer = null;

      if (name.size() == 0) {
        lastContainer = this.rootContainer;
      } else {
        lastContainer = getLastContainer(rootContainer, name);
        if (lastContainer == null) {
          lastContainer = this.rootContainer;
        }
        JNDIHandle object = persistent.findObject(lastContainer, lastCompoundComponentVariable);

        if (object == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("list operation failed; path to object " + lastCompoundComponentVariable + " does not exist.");
          }
          if(!remote) {
            throw new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException("list operation failed; path to object " + lastCompoundComponentVariable + " does not exist.");
          } else {
            throw new NameNotFoundException(NameNotFoundException.PATH_TO_OBJECT_DOESNOT_EXISTS_IN_LIST, new Object[]{lastCompoundComponentVariable});
          }
        }

        lastContainer = persistent.getLinkedContainer(object);

        if (lastContainer == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("list operation failed; " + lastCompoundComponentVariable + " either is not a context, or is a foreign context.");
          }
          throw new NotContextException("list operation failed; " + lastCompoundComponentVariable + " either is not a context, or is a foreign context.");
        }
      }

      // synchronize the operation using container id
      synchronized (getLockObject(lastContainer.getContainerName())) {
        JNDIHandleEnumeration jhe = persistent.listObjects(lastContainer, "*");
        return new ServerNamingEnumImpl(persistent.getNewConnection(), jhe, ServerNamingEnumImpl.TYPE_NAMECLASSPAIR, ((rootName == null) ? (rootName = new CompositeName("")) : rootName), onlyLookUpAllowed, remote, redirectableContext);
      }
    } catch (NoPermissionException np) {
      throw np;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (NotContextException nc) {
      throw nc;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during list operation of context with name " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during list operation of context with name " + name + ".");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * @param name The name of the object to take attributes from
   * @param type The type of the operation
   * @return The attributes of the object named by name
   * @throws javax.naming.NamingException If NamingException is thrown while getting the attributes
   * of the object.
   * @throws RemoteException If RemoteException is thrown while getting the attributes
   * of the object.
   */
  public Attributes getAttributes(Name name, short type) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform getAttributes operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform getAttributes operation.");
      }
    }

    if (name.size() == 0) {
      // return the Attributes of the root context
      Attributes attrToReturn;
      try {
        attrToReturn = DirObject.getAttributes(persistent.readObject(parentObject));
      } catch (NamingException jde) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during getAttributes operation of object/context with name " + name + ".", jde);
        }
        NamingException ne = new NamingException("Exception during getAttributes operation of object/context with name " + name + ".");
        if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
        throw ne;
      }

      if (attrToReturn == null) {
        attrToReturn = new BasicAttributes();
      }

      return attrToReturn;
    }

    try {

      JNDIHandle lastContainer = getLastContainer(rootContainer, name);
      if (lastContainer == null) {
        lastContainer = this.rootContainer;
      }

      // synchronize the operation using container id
      synchronized (getLockObject(lastContainer.getContainerName())) {

        JNDIHandle object = persistent.findObject(lastContainer, lastCompoundComponentVariable);

        if (object == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("getAttributes operation failed; object " + lastCompoundComponentVariable + " not found.");
          }
          if(!remote) {
            throw new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException("getAttributes operation failed; object " + lastCompoundComponentVariable + " not found.");
          } else {
            throw new NameNotFoundException(NameNotFoundException.OBJECT_DOESNOT_EXISTS_IN_GET_ATRIBUTES, new Object[]{lastCompoundComponentVariable});
          }
        }

        Attributes attrToReturn = DirObject.getAttributes(persistent.readObject(object));

        if (attrToReturn == null) {
          attrToReturn = new BasicAttributes();
        }

        return attrToReturn;
      }
    } catch (NoPermissionException np) {
      throw np;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during getAttributes operation of object/context with name " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during getAttributes operation of object/context with name " + name + ".");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Returns attributes with the selected IDs
   *
   * @param name The name of the object to take attributes from
   * @param attrIDs The set of attributes to return in result
   * @param type The type of the operation
   * @return The attributes of the object named by name
   * @throws javax.naming.NamingException If NamingException is thrown while getting the attributes
   * of the object.
   * @throws RemoteException If RemoteException is thrown while getting the attributes
   * of the object.
   */
  public Attributes getAttributes(Name name, String[] attrIDs, short type) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform getAttributes operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform getAttributes operation.");
      }
    }

    try {

      Attributes attrsToReturn = new BasicAttributes();

      // get all attributes
      Attributes attrs = getAttributes(name, type);

      if (attrIDs == null) {
        // all
        attrsToReturn = attrs;
      } else if (attrIDs.length == 0) {
        // none
      } else {
        // some
        for (int i = 0; i < attrIDs.length; i++) {
          String tempID = attrIDs[i];
          Attribute attribute = attrs.get(tempID);

          if (attribute != null) {
            attrsToReturn.put(attribute);
          }
        }
      }

      return attrsToReturn;
    } catch (Exception jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during getAttributes operation of object/context with name " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during getAttributes operation of object/context with name " + name + ".");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Modifies by modify operation
   *
   * @param name The name bound to object whose attributes are to be modified
   * @param mod_op The modifying operation
   * @param attrs The attributes to use when modifying operation is applied
   * @param type Type of operation
   * @throws AttributeModificationException When Exception occurs while modifying the attributes
   * @throws javax.naming.NamingException When there is a problem encountered while performing
   * naming operations
   * @throws RemoteException When there is a problem encountered while performing
   * remote operations
   */
  public void modifyAttributes(Name name, int mod_op, Attributes attrs, short type) throws javax.naming.directory.AttributeModificationException, javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform modifyAttributes operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform modifyAttributes operation.");
      }
    }

    try {

      if (attrs == null) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Modification attributes cannot be null.");
        }
        throw new AttributeModificationException("Modification attributes cannot be null.");
      }

      JNDIHandle lastContainer = getLastContainer(rootContainer, name);
      if (lastContainer == null) {
        lastContainer = this.rootContainer;
      }

      // synchronize the operation using container id
      synchronized (getLockObject(lastContainer.getContainerName())) {

        JNDIHandle object = persistent.findObject(lastContainer, lastCompoundComponentVariable);

        if (object == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("modifyAttributes failed; object " + lastCompoundComponentVariable + " not found.");
          }
          if(!remote) {
            throw new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException("modifyAttributes failed; object " + lastCompoundComponentVariable + " not found.");
          } else {
            throw new NameNotFoundException(NameNotFoundException.OBJECT_DOESNOT_EXISTS_IN_MODIFY_ATRIBUTES, new Object[]{lastCompoundComponentVariable});
          }
        }

        // ClusterObject clusterObject =
        // contextClusterObject.read(persistent.readObject(object));
        byte[] data = persistent.readObject(object);
        Attributes attrToModify = DirObject.getAttributes(data);

        if (attrToModify == null) {
          attrToModify = new BasicAttributes();
        }

        ModifyAttributes.modAttr(attrToModify, mod_op, attrs);
        data = DirObject.setAttributes(data, attrToModify);

        // check whether the object is replicated in case the modifyAttributes
        // operation is replicated
        if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
          short objectType = persistent.getObjectType(object);
          if (objectType == Constants.REPLICATED_OPERATION || objectType == Constants.REMOTE_REPLICATED_OPERATION || objectType == Constants.NOT_DEFINED_OBJECT) {
            persistent.rebindObject(object, data, type);
            return;
          } else {
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.infoT("Attempt for a global (replicated) modifyAttributes operation over local (non-replicated) object " + lastCompoundComponentVariable + ".");
            }
            throw new NamingException("Attempt for a global (replicated) modifyAttributes operation over local (non-replicated) object " + lastCompoundComponentVariable + ".");
          }
        } else { //check if a non replicated operation is going to be applied to a replicated object
          if (persistent.getObjectType(object) == Constants.REPLICATED_OPERATION || persistent.getObjectType(object) == Constants.REMOTE_REPLICATED_OPERATION) {
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.infoT("Attempt for a local (non-replicated) modifyAttributes operation over global (replicated) object " + lastCompoundComponentVariable + ".");
            }
            throw new NamingException("Attempt for a local (non-replicated) modifyAttributes operation over global (replicated) object " + lastCompoundComponentVariable + ".");
          } else {
            persistent.rebindObject(object, data, type);
            return;
          }
        }
      }
    } catch (NoPermissionException np) {
      throw np;
    } catch (javax.naming.directory.AttributeModificationException ame) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during modifyAttributes operation of object/context with name " + name + ".", ame);
      }
      AttributeModificationException amex = new AttributeModificationException("AttributeModificationException in modifyAttributes operation.");
      amex.setRootCause(ame);
      throw amex;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (JNDIException jde) { // rethrow the exception as a naming exception
      if (jde.getExceptionType() == JNDIException.NAME_ALREADY_BOUND) {
        modifyAttributes(name, mod_op, attrs, type);
      }
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during modifyAttributes operation of object/context with name " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during modifyAttributes operation of object/context with name " + name + ".");
      ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Modifies by array ModificationItems
   *
   * @param name The name bound to object whose attributes are to be modified
   * @param mods Array of ModificationItem-s, which determine the modifying
   * operations
   * @param type Type of operation
   * @throws AttributeModificationException When Exception occurs while modifying the attributes
   * @throws javax.naming.NamingException When there is a problem encountered while performing
   * naming operations
   * @throws RemoteException When there is a problem encountered while performing
   * remote operations
   */
  public void modifyAttributes(Name name, ModificationItem[] mods, short type) throws javax.naming.directory.AttributeModificationException, javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform modifyAttributes operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform modifyAttributes operation.");
      }
    }

    try {

      if (mods == null) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("ModificationItemArray is null in modifyAttributes operation.");
        }
        throw new AttributeModificationException("Modification attributes cannot be null.");
      }

      JNDIHandle lastContainer = getLastContainer(rootContainer, name);
      if (lastContainer == null) {
        lastContainer = this.rootContainer;
      }

      // synchronize the operation using container id
      synchronized (getLockObject(lastContainer.getContainerName())) {

        JNDIHandle object = persistent.findObject(lastContainer, lastCompoundComponentVariable);

        if (object == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("modifyAttributes failed; object " + lastCompoundComponentVariable + " not found.");
          }
          if(!remote) {
            throw new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException("modifyAttributes failed; object " + lastCompoundComponentVariable + " not found.");
          } else {
            throw new NameNotFoundException(NameNotFoundException.OBJECT_DOESNOT_EXISTS_IN_MODIFY_ATRIBUTES, new Object[]{lastCompoundComponentVariable});
          }
        }

        byte[] dobj = persistent.readObject(object);
        Attributes attrToModify = DirObject.getAttributes(dobj);

        if (attrToModify == null) {
          attrToModify = new BasicAttributes();
        }

        ModifyAttributes.modAttr(attrToModify, mods);
        dobj = DirObject.setAttributes(dobj, attrToModify);

        //chech whether the object is replicated in case the modifyAttributes operation is replicated
        if (type == Constants.REPLICATED_OPERATION || type == Constants.REMOTE_REPLICATED_OPERATION) {
          short objectType = persistent.getObjectType(object);
          if (objectType == Constants.REPLICATED_OPERATION || objectType == Constants.REMOTE_REPLICATED_OPERATION || objectType == Constants.NOT_DEFINED_OBJECT) {
            persistent.rebindObject(object, dobj, type);
            return;
          } else {
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.infoT("Attempt for a global (replicated) modifyAttributes operation over local (non-replicated) object " + lastCompoundComponentVariable + ".");
            }
            throw new NamingException("Attempt for a global (replicated) modifyAttributes operation over local (non-replicated) object " + lastCompoundComponentVariable + ".");
          }
        } else { //check if a non replicated operation is going to be applied to a replicated object
          if (persistent.getObjectType(object) == Constants.REPLICATED_OPERATION || persistent.getObjectType(object) == Constants.REMOTE_REPLICATED_OPERATION) {
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.infoT("Attempt for a local (non-replicated) modifyAttributes operation over global (replicated) object " + lastCompoundComponentVariable + ".");
            }
            throw new NamingException("Attempt for a local (non-replicated) modifyAttributes operation over global (replicated) object " + lastCompoundComponentVariable + ".");
          } else {
            persistent.rebindObject(object, dobj, type);
            return;
          }
        }
      }
    } catch (NoPermissionException np) {
      throw np;
    } catch (javax.naming.directory.AttributeModificationException ame) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during modifyAttributes operation of object/context with name " + name + ".", ame);
      }
      AttributeModificationException amex = new AttributeModificationException("AttributeModificationException in modifyAttributes operation.");
      amex.setRootCause(ame);
      throw amex;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (JNDIException jde) {
      // rethrow the exception as a naming exception
      if (jde.getExceptionType() == JNDIException.NAME_ALREADY_BOUND) {
        modifyAttributes(name, mods, type);
      }
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during modifyAttributes operation of object/context with name " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during modifyAttributes operation of object/context with name " + name + ".");
      ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * The enumeration skips the objects with attributes that do not match with
   * matchingAttributes. <p/> Objects in the enumeration are SearchResults
   * with attributes, got by filtering each object's attribute with
   * attributesToReturn
   *
   * @param name The name of the context or object, the search process will be
   * applied to
   * @param matchingAttributes The attributes an object has to match to be added to resulting
   * enumeration
   * @param attributesToReturn The selection of the attributes to return in result
   * @param type Type of operation
   * @return Enumeration of the SearchResult-s according to the qualified
   *         objects
   * @throws javax.naming.NamingException When there was a problem in naming operations
   * @throws RemoteException When there was a problem in remote operations
   */
  public ServerNamingEnum search(Name name, Attributes matchingAttributes, String[] attributesToReturn, short type) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform search operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform search operation.");
      }
    }

    try {

      JNDIHandle lastContainer = null;
      JNDIHandleEnumeration jhe = null;

      if (name.size() == 0) {
        lastContainer = this.rootContainer;
      } else {
        lastContainer = getLastContainer(rootContainer, name);

        JNDIHandle object = persistent.findObject(lastContainer, lastCompoundComponentVariable);

        if (object == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("Search operation failed. Name " + lastCompoundComponentVariable + " bound to any object or context does not exist.");
          }
          if(!remote) {
            throw new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException("Search operation failed. Name " + lastCompoundComponentVariable + " bound to any object or context does not exist.");
          } else {
            throw new NameNotFoundException(NameNotFoundException.OBJECT_DOESNOT_EXISTS_IN_SEARCH, new Object[]{lastCompoundComponentVariable});
          }
        }

        lastContainer = persistent.getLinkedContainer(object);

        if (lastContainer == null) {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("Attempt to search over binding " + lastCompoundComponentVariable + ". The search operation can be used only for contexts.");
          }
          throw new NotContextException("Attempt to search over binding " + lastCompoundComponentVariable + ". The search operation can be used only for contexts.");
        }
      }

      // synchronize the operation using container id
      synchronized (getLockObject(lastContainer.getContainerName())) {
        jhe = persistent.listObjects(lastContainer, "*");
      }
      // persistent.commit();

      if (matchingAttributes == null) {
        matchingAttributes = new BasicAttributes();
      }

      return new ServerNamingSearchEnumImpl(persistent.getNewConnection(), jhe, matchingAttributes, attributesToReturn, ((rootName == null) ? (rootName = new CompositeName("")) : rootName), remote, onlyLookUpAllowed, redirectableContext);
    } catch (NoPermissionException np) {
      throw np;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (NotContextException nc) {
      throw nc;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during search operation on " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during search operation.");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Performs search according to RFC 2254's string filter and search
   * controls. The enumeration skips the not-qualified objects
   *
   * @param name The name of the context or object, the search process will be
   * applied to
   * @param filterExpr The string filter according to RFC 2254
   * @param filterArgs The arguments passed to the filter to complete it
   * @param cons The searchcontrols containg information on which and how much
   * of the objects will qualify, and the type of the SearchResult
   * @param type Operation type
   * @return Enumeration of the SearchResult-s according to the qualified
   *         objects
   * @throws javax.naming.NamingException When there was a problem in naming operations
   * @throws RemoteException When there was a problem in remote operations
   */
  public ServerNamingEnum search(Name name, String filterExpr, Object filterArgs[], SearchControls cons, short type) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform search operation of name " + name + ".");
        }
        throw new NoPermissionException("No permission to perform search operation.");
      }
    }

    try {

      if (filterExpr == null) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Cannot search " + name + "; list operation is not allowed.");
        }
        throw new NamingException("Cannot search " + name + "; list operation is not allowed.");
      }

      JNDIHandle lastContainer = null;
      JNDIHandle parentHandle;
      byte[] dobj = null;

      if (name.size() == 0) {
        lastContainer = rootContainer;
        parentHandle = parentObject;

        if (cons.getSearchScope() == SearchControls.OBJECT_SCOPE) {
          return new ServerNamingOneObjectEnumImpl(persistent.getNewConnection(), parentHandle, filterExpr, filterArgs, cons, ((rootName == null) ? (rootName = new CompositeName("")) : rootName), remote, onlyLookUpAllowed, redirectableContext);
        }
      } else {
        lastContainer = getLastContainer(rootContainer, name);

        // synchronize the operation using container id
        synchronized (getLockObject(lastContainer.getContainerName())) {

          JNDIHandle object = persistent.findObject(lastContainer, lastCompoundComponentVariable);

          if (object == null) {
            if (LOG_LOCATION.beInfo()) {
              LOG_LOCATION.infoT("Search operation failed. Name " + lastCompoundComponentVariable + " bound to any object or context does not exist.");
            }
            if(!remote) {
              throw new com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException("Search operation failed. Name " + lastCompoundComponentVariable + " bound to any object or context does not exist.");
            } else {
              throw new NameNotFoundException(NameNotFoundException.OBJECT_DOESNOT_EXISTS_IN_SEARCH, new Object[]{lastCompoundComponentVariable});
            }
          }

          if (cons.getSearchScope() == SearchControls.OBJECT_SCOPE) {
            return new ServerNamingOneObjectEnumImpl(persistent.getNewConnection(), object, filterExpr, filterArgs, cons, ((rootName == null) ? (rootName = new CompositeName("")) : rootName), remote, onlyLookUpAllowed, redirectableContext);
          }

          dobj = persistent.readObject(object);
          parentHandle = object;
          lastContainer = persistent.getLinkedContainer(object);

          if (lastContainer == null) { // if object is not a context
            if (cons.getSearchScope() != SearchControls.ONELEVEL_SCOPE) {
              return new ServerNamingOneObjectEnumImpl(persistent.getNewConnection(), object, filterExpr, filterArgs, cons, ((rootName == null) ? (rootName = new CompositeName("")) : rootName), remote, onlyLookUpAllowed, redirectableContext);
            } else {
              if (LOG_LOCATION.beInfo()) {
                LOG_LOCATION.infoT("Attempt to search over binding " + lastCompoundComponentVariable + ". The search operation can be used only for contexts.");
              }
              throw new NotContextException("Attempt to search over binding " + lastCompoundComponentVariable + ". The search operation can be used only for contexts.");
            }
          }
        }
      }
      return new ServerNamingSearchFilterEnumImpl(persistent.getNewConnection(), parentHandle, lastContainer, dobj, filterExpr, filterArgs, cons, ((rootName == null) ? (rootName = new CompositeName("")) : rootName), remote, onlyLookUpAllowed, redirectableContext);
    } catch (NoPermissionException np) {
      throw np;
    } catch (NameNotFoundException nnf) {
      throw nnf;
    } catch (com.sap.engine.services.jndi.persistent.exceptions720.NameNotFoundException nnf) {
      throw nnf;
    } catch (NotContextException nc) {
      throw nc;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during search operation on " + name + ".", jde);
      }
      NamingException ne = new NamingException("Exception during search operation.");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Returns the environment of the context.
   *
   * @param type Type of the operation
   * @return The environment of the context
   * @throws javax.naming.NamingException When there was a problem in naming operations
   * @throws RemoteException When there was a problem in remote operations
   */
  public Properties getEnvironment(short type) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform getEnvironment operation.");
        }
        throw new NoPermissionException("No permission to perform getEnvironment operation.");
      }
    }

    try {

      byte[] tempb = persistent.readContainer(rootContainer);
      Properties temp;

      if (tempb != null) {
        temp = (Properties) Serializator.toObject(tempb);
      } else {
        temp = new Properties();
      }

      return temp;
    } catch (NoPermissionException np) { 
      throw np;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during getEnvironment operation.", jde);
      }
      NamingException ne = new NamingException("Exception during getEnvironment operation.");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Removes a property from the environment
   *
   * @param propName The nonnull property to remove.
   * @param type Type of the operation
   * @return The value of the property before removing.
   * @throws javax.naming.NamingException When there was a problem in naming operations
   * @throws RemoteException When there was a problem in remote operations
   */
  public String removeFromEnvironment(String propName, short type) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform removeFromEnvironment operation.");
        }
        throw new NoPermissionException("No permission to perform removeFromEnvironment operation.");
      }
    }

    try {

      byte[] tempb = persistent.readContainer(rootContainer);
      Properties temp;

      if (tempb != null) {
        temp = (Properties) Serializator.toObject(tempb);
      } else {
        temp = new Properties();
      }

      String toReturn = temp.getProperty(propName);
      temp.remove(propName);
      persistent.modifyContainer(rootContainer, Serializator.toByteArray(temp), false);
      return toReturn;
    } catch (NoPermissionException np) {
      throw np;
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during removeFromEnvironment operation.", jde);
      }
      NamingException ne = new NamingException("Exception during removeFromEnvironment operation.");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Adds a property to the environment
   *
   * @param propName The nonnull property to remove.
   * @param propVal The nonnul value of the property.
   * @param type Type of the operation
   * @return The value of the property prior adding the new value.
   * @throws javax.naming.NamingException When there was a problem in naming operations
   * @throws RemoteException When there was a problem in remote operations
   */
  public String addToEnvironment(String propName, String propVal, short type) throws javax.naming.NamingException, RemoteException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform addToEnvironment operation.");
        }
        throw new NoPermissionException("No permission to perform addToEnvironment operation.");
      }
    }

    try {

      byte[] tempb = persistent.readContainer(rootContainer);
      Properties temp;

      if (tempb != null) {
        temp = (Properties) Serializator.toObject(tempb);
      } else {
        temp = new Properties();
      }

      String toReturn = temp.getProperty(propName);
      temp.setProperty(propName, propVal);
      persistent.modifyContainer(rootContainer, Serializator.toByteArray(temp), false);
      return toReturn;
    } catch (NoPermissionException np) {
      throw np;
    } catch (JNDIException jde) {
      //if (jde.getExceptionType() == JNDIException.NAME_ALREADY_BOUND) {
      //} 
      return addToEnvironment(propName, propVal, type); // the condition above is always true
    } catch (NamingException jde) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during addToEnvironment operation.", jde);
      }
      NamingException ne = new NamingException("Exception during addToEnvironment operation.");
      ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Returns the flag whether the implementation has the ability to dynamiclly
   * listen to afterwards created handles
   *
   * @return Always true as dynaming listening is not implemented
   * @throws javax.naming.NamingException When there was a problem in naming operations
   * @throws RemoteException When there was a problem in remote operations
   */
  public boolean targetMustExist() throws javax.naming.NamingException, RemoteException {
    return true;
  }

  /**
   * Close the repository
   *
   * @throws RemoteException When there was a problem in naming operations
   * @throws javax.naming.NamingException When there was a problem in remote operations
   */
  public void close() throws java.rmi.RemoteException, javax.naming.NamingException {
    if (this.disposed == false) {
      try {
        PortableRemoteObject.unexportObject(this);
        this.disposed = true;
      } catch (Exception e) {
        // Excluding this catch block from JLIN $JL-EXC$ since there's no need to log this exception
        // Please do not remove this comment!
        // will ignore this exception; the object is exported in p4 but not exported in iiop
        this.disposed = true;
      }
    }
  }

  /**
   * Reuses a ServerContext
   *
   * @param jp The repository to use in new context
   * @param parentObject Handle to parent object
   * @param container Handle the root container
   * @param name ((rootName == null)?(rootName = new CompositeName("")):rootName)
   */
  public void reuse(JNDIPersistentRepository jp, JNDIHandle parentObject, JNDIHandle container, Name name, boolean onlyLookupAllowed) {
    this.persistent = jp;
    this.parentObject = parentObject;
    this.rootContainer = container;
    this.disposed = false;
    this.rootName = name;
    this.onlyLookUpAllowed = onlyLookupAllowed;
  }

  /**
   * Prints the naming using repository's printTree()
   *
   * @throws RemoteException When there was a problem in naming operations
   * @throws javax.naming.NamingException When there was a problem in remote operations
   */
  public void print(PrintStream outStrm) throws java.rmi.RemoteException, javax.naming.NamingException {
    if (!SecurityBase.WITHOUT_SECURITY) {
      if (onlyLookUpAllowed) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("No permission to perform list operation.");
        }
        throw new NoPermissionException("No permission to perform list operation.");
      }
    }

    try {

      persistent.printTree(outStrm);
    } catch (NoPermissionException np) {
      throw np;
    } catch (NamingException jde) {
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during print operation.", jde);
      }
      NamingException ne = new NamingException("Exception during print operation.");
      if (!(jde instanceof JNDIException)) ne.setRootCause(jde);
      throw ne;
    }
  }

  /**
   * Allow specific operation for specific user (denoted by sid)
   */
  public void allowOperation(String userName, String permissionName, boolean isGroup) throws javax.naming.NamingException, java.rmi.RemoteException {
// try {
// SecurityBase.allowOperation(userName, permissionName, isGroup);
// } catch (JNDIException jde) {
// if (jde.getExceptionType() == JNDIException.NAME_ALREADY_BOUND) {
// allowOperation(userName, permissionName, isGroup);
// } else {
// if (JNDIFrame.log.toLogInfoInLocation()) {
// JNDIFrame.log.logInfo(NamingException.CAN_NOT_ALLOW_OPERATION, new Object[]
// {userName, permissionName});
// JNDIFrame.log.logCatching(jde);
// }
// NamingException ne = new
// NamingException(NamingException.CAN_NOT_ALLOW_OPERATION, new Object[]
// {userName, permissionName}, jde);
// throw ne;
// }
// }
  }

  /**
   * Deny specific operation for specific user (denoted by sid)
   */
  public void denyOperation(String userName, String permissionName, boolean isGroup) throws javax.naming.NamingException, java.rmi.RemoteException {
// try {
// SecurityBase.denyOperation(userName, permissionName, isGroup);
// } catch (JNDIException jde) {
// if (jde.getExceptionType() == JNDIException.NAME_ALREADY_BOUND) {
// allowOperation(userName, permissionName, isGroup);
// } else {
// if (JNDIFrame.log.toLogInfoInLocation()) {
// JNDIFrame.log.logInfo(NamingException.CAN_NOT_DENY_OPERATION, new Object[]
// {userName, permissionName});
// JNDIFrame.log.logCatching(jde);
// }
// NamingException ne = new
// NamingException(NamingException.CAN_NOT_DENY_OPERATION, new Object[]
// {userName, permissionName}, jde);
// throw ne;
// }
// }
  }

  public static synchronized void increaseNumberOfBindings() {
    numberOfBindings++;
  }

  public static synchronized void decreaseNumberOfBindings() {
    numberOfBindings--;
  }

  public static synchronized int getNumberOfBindings() {
    return numberOfBindings;
  }

  public static String getLockObject(String id) {
    synchronized (keys) {
      String result = (String) keys.get(id);
      if (result == null) {
        result = new String(id);
        keys.put(id, result);
        return result;
      } else {
        return result;
      }
    }
  }

  public static void unlockObject(String id) {
    synchronized (keys) {
      keys.remove(id);
    }
  }

}

