/// taka li trqbwa da byde next i nextElement - prawilno l ise hwyrlq exceptiona ??
package com.sap.engine.services.jndi.implclient;

import javax.naming.directory.*;
import javax.naming.NamingEnumeration;
import javax.naming.Name;
import javax.naming.Binding;
import javax.naming.NameClassPair;
import java.util.Hashtable;
import java.util.Enumeration;

import com.sap.engine.services.jndi.implserver.ServerNamingEnum;
import com.sap.engine.services.jndi.implserver.ServerContextInface;
import com.sap.engine.services.jndi.cluster.DirObject;
import com.sap.engine.services.jndi.RemoteServiceReference;
import com.sap.engine.services.jndi.JNDIFrame;

import javax.naming.NamingException;
import java.util.NoSuchElementException;

import com.sap.exception.BaseRuntimeException;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

/**
 * Implements the NamingEnumeration
 *
 * @author Petio Petev, Panayot Dobrikov
 * @version 4.00
 */
public class ClientNamingEnum implements Enumeration, NamingEnumeration, java.io.Serializable {
	
	private final static Location LOG_LOCATION = Location.getLocation(ClientNamingEnum.class);
	
  /**
   * serial version UID
   */
  static final long serialVersionUID = -3548747230052206539L;
  /**
   * Server enumeration
   */
  ServerNamingEnum serverEnum = null; //$JL-SER$
  /**
   * Stores the client context
   */
  ClientContext context;
  /**
   * Name of the rott
   */
  Name rootName = null;
  /**
   * Stores the environment
   */
  Hashtable env = null;
  /**
   * Flags if this is server
   */
  boolean onServer = false;
  /**
   * Used to determine if the enumeration is already closed
   */
  boolean alreadyClosed = false;
  /**
   * The LoginContext for this principle
   */
  public LoginHelper loginContext = null; //$JL-SER$

  /**
   * Constructor
   *
   * @param context Client context to use
   * @param serverEnum The server side enumeration
   * @param rootName Name of the root
   * @param env Environment
   * @param onServer Runs on server flag
   */
  public ClientNamingEnum(ClientContext context, ServerNamingEnum serverEnum, Name rootName, Hashtable env, boolean onServer, LoginHelper loginCtx) {
    //System.out.println("  * ClientNamingEnum construcotr invoked ");
    this.context = context;
    this.serverEnum = serverEnum;
    this.rootName = rootName;
    this.env = env;
    this.onServer = onServer;
    this.loginContext = loginCtx;
  }

  /**
   * Closes the enumeration
   *
   * @throws javax.naming.NamingException Thrown if a problem occures
   */
  public void close() throws javax.naming.NamingException {
    if (!alreadyClosed) {
      try {
        serverEnum.close();
      } catch (Exception e) {
                LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      }
    }
  }

  /**
   * Determines if the enumeration has more elements
   *
   * @return "true" if there is more elements
   */
  public boolean hasMoreElements() {
    try {
      if (!serverEnum.hasMore()) {
        alreadyClosed = true;
        return false;
      } else {
        return true;
      }
    } catch (Exception ex) {
        	LOG_LOCATION.traceThrowableT(Severity.PATH, "", ex);
      //      ex.printStackTrace();
      return false;
    }
  }

  /**
   * Returns the next element in the enumeration
   *
   * @return The next element
   */
  public Object nextElement() {
    if (!hasMoreElements()) {
      throw new NoSuchElementException("There are no more elements in the naming enumeration.");
    }

    try {
      Object obj = next();
      return obj;
    } catch (javax.naming.NamingException e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during list/listBindings/search operation.", e);
      }
      RuntimeException re = new RuntimeException("Exception during list/listBindings/search operation.", e);
      throw re;
    }
  }

  /**
   * Determines if the enumeration has more elements
   *
   * @return "true" if there is more elements
   */
  public boolean hasMore() throws javax.naming.NamingException {
    try {
      if (!serverEnum.hasMore()) {
        alreadyClosed = true;
        return false;
      } else {
        return true;
      }
    } catch (java.rmi.RemoteException ex) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during list/listBindings/search operation.", ex);
      }
      NamingException ne = new NamingException("Exception during list/listBindings/search operation.");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  /**
   * Returns the next object in the enumeration
   *
   * @return The next object
   */
  public Object next() throws javax.naming.NamingException {
    try {
      if (!hasMore()) {
        throw new NoSuchElementException("There are no more elements in the naming enumeration.");
      }

      Object obj = serverEnum.next();

      if (obj instanceof SearchResult) {
        Object object = ((SearchResult) obj).getObject();
        String name = ((SearchResult) obj).getName();
        String classname = ((SearchResult) obj).getClassName();

        if (object instanceof ServerContextInface) {
          if (env != null) {
            object = new ClientContext((Hashtable) (env.clone()), (ServerContextInface) object, ((Name) (rootName.clone())).add(name), this.onServer, context.referenceFactory, loginContext);
          } else {
            object = new ClientContext(new Hashtable(), (ServerContextInface) object, ((Name) (rootName.clone())).add(name), this.onServer, context.referenceFactory, loginContext);
          }

          classname = object.getClass().getName();
        } else {
          if (object != null) {
            try {
              if (object instanceof byte[]) {
                object = context.deserializeDirObject((byte[]) object);
              }

              try {
                classname = ((DirObject) object).getClassName();
                object = ((DirObject) object).getObject();

                if (object instanceof RemoteServiceReference) {
                  RemoteServiceReference rSR = null;
                  try {
                    rSR = (RemoteServiceReference) object;
                    object = null;
                    object = rSR.getServiceInterface();
                    // local reference
                    if (object == null) {
                      if (rSR.isService()) {
                        object = JNDIFrame.containerContext.getObjectRegistry().getServiceInterface(name);
                      } else {
                        object = JNDIFrame.containerContext.getObjectRegistry().getProvidedInterface(name);
                      }
                    }
                  } catch (Exception e) {
                                        LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
                    try {
                      if (object == null) {
                        if (rSR.isService()) {
                          object = JNDIFrame.containerContext.getObjectRegistry().getServiceInterface(name);
                        } else {
                          object = JNDIFrame.containerContext.getObjectRegistry().getProvidedInterface(name);
                        }
                      }
                    } catch (Exception e2) {
                      if (LOG_LOCATION.beInfo()) {
                        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during lookup operation of object with name " + name + ", cannot resolve object reference.", e2);
                      }
                      object = null;
                    }
                  }
                }
                if (object != null) {
                  classname = object.getClass().getName();
                } else {
                  classname = null;
                }
//                if (object instanceof RemoteServiceReference) {
//                  try {
//                    object = ((RemoteServiceReference) object).getServiceInterface();
//                    classname = object.getClass().getName();
//                  } catch (ClassCastException e) {
//                    object = null;
//                    classname = null;
//                  }
//                }
              } catch (Exception e) {
                                LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
                object = null;
              }
            } catch (Exception e) {
                            LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
              object = null;
            }
          }
        }

        obj = new SearchResult(name, classname, object, ((SearchResult) obj).getAttributes(), ((SearchResult) obj).isRelative());
      } else if (obj instanceof Binding) {
        Object object = ((Binding) obj).getObject();
        String name = ((Binding) obj).getName();
        String classname = "";

        if (object instanceof ServerContextInface) {
          if (env != null) {
            object = new ClientContext((Hashtable) (env.clone()), (ServerContextInface) object, ((Name) (rootName.clone())).add(name), this.onServer, context.referenceFactory, loginContext);
          } else {
            object = new ClientContext(new Hashtable(), (ServerContextInface) object, ((Name) (rootName.clone())).add(name), this.onServer, context.referenceFactory, loginContext);
          }

          classname = object.getClass().getName();
        } else {
          try {
            if (object instanceof byte[]) {
              classname = DirObject.getClassName((byte[]) object);
              object = context.deserializeDirObject((byte[]) object);
            } else {
              classname = ((DirObject) object).getClassName();
            }

            object = ((DirObject) object).getObject();

            if (object instanceof RemoteServiceReference) {
              RemoteServiceReference rSR = null;
              try {
                rSR = (RemoteServiceReference) object;
                object = null;
                object = rSR.getServiceInterface();
                // local reference
                if (object == null) {
                  if (rSR.isService()) {
                    object = JNDIFrame.containerContext.getObjectRegistry().getServiceInterface(name);
                  } else {
                    object = JNDIFrame.containerContext.getObjectRegistry().getProvidedInterface(name);
                  }
                }
              } catch (Exception e) {
                                LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
                try {
                  if (object == null) {
                    if (rSR.isService()) {
                      object = JNDIFrame.containerContext.getObjectRegistry().getServiceInterface(name);
                    } else {
                      object = JNDIFrame.containerContext.getObjectRegistry().getProvidedInterface(name);
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
            if (object != null) {
              classname = object.getClass().getName();
            } else {
              classname = null;
            }
//            if (object instanceof RemoteServiceReference) {
//              object = ((RemoteServiceReference) object).getServiceInterface();
//              classname = object.getClass().getName();
//            }
          } catch (Exception e) {
                        LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
            object = null;
            classname = "null";
          }
        }

        obj = new Binding(name, classname, object, ((Binding) obj).isRelative());
      } else if (obj instanceof NameClassPair) {

      }

      return obj;
    } catch (java.rmi.RemoteException e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "RemoteException while trying to get next element of naming enumeration.", e);
      }
      NamingException ne = new NamingException("RemoteException while trying to get next element of naming enumeration.");
      ne.setRootCause(e);
      throw ne;
    }
  }

}

