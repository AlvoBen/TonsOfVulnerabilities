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
package com.sap.engine.services.jndi;

/**
 * The initial context factory provides InQMy Context Implementation.
 * Class implements javax.naming.spi.InitialContextFactory
 * @see javax.naming.spi.InitialContextFactory
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */

import java.security.*;
import java.util.*;
import javax.naming.spi.*;
import javax.naming.Context;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.lib.security.HomeHandlePermission;
import com.sap.engine.lib.security.LoginExceptionDetails;
import com.sap.engine.lib.security.VirtualPermission;
import com.sap.engine.services.jndi.implclient.ClientContext;
import com.sap.engine.services.jndi.implclient.OffsetClientContext;
import com.sap.engine.services.jndi.implclient.P4ReferenceFactory;
import com.sap.engine.services.jndi.implclient.LoginHelper;
import com.sap.engine.services.jndi.implserver.ServerContextInface;
import com.sap.engine.services.jndi.implserver.ServerContextImpl;
import com.sap.engine.services.jndi.persistent.RemoteSerializator;
import com.sap.engine.services.jndi.persistent.SerializatorFactory;

import javax.naming.NamingException;
import javax.naming.NoPermissionException;

import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.interfaces.cross.CrossObjectBroker;
import com.sap.engine.interfaces.cross.Destination;
import com.sap.engine.interfaces.cross.RemoteBroker;
import com.sap.engine.interfaces.cross.RemoteEnvironment;
import com.sap.engine.interfaces.security.SecurityContextObject;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class InitialContextFactoryImpl implements InitialContextFactory {

  private final static Location LOG_LOCATION = Location.getLocation(InitialContextFactoryImpl.class);


  /*
  * static properties to use if the consecutive InitialContext is made with no PROVIDER_URL specified
  */
  static Properties systemProperties = new Properties();
  /*
  * Protocol delimiter to recognize if the provider URL has protocol specified or not.
  */
  static final String PROTOCOL_DELIMITER = "://";

  /**
   * Returns new initial context, as defined in the specification
   *
   * @param environment Enviroment properties
   * @return Context
   * @throws javax.naming.NamingException
   */
  public Context getInitialContext(Hashtable environment) throws javax.naming.NamingException {
    String componentName = null;
    String appClientName = null;
    Hashtable env = (environment == null) ? SystemProperties.getProperties() : ((Hashtable) environment.clone());
    boolean beaLoggedIn = false;
    boolean clear_cache = false;
    // clear_cache property is used to clean naming cache
    if (env.get("clear_cache") != null && env.get("clear_cache").equals("true")) {
      clear_cache = true;
    }

    if ((SystemProperties.getProperty("server") != null) && (SystemProperties.getProperty("memory") == null)) {
      env.put("server", "true");
    }

    if ((env.get("server") != null) && JNDIFrame.isAppContextAvailable() && JNDIFrame.threadSystem.getThreadContext() != null) {
      componentName = JNDIFrame.getAppContextProvider().getComponentName();
      if (componentName != null) {
        componentName = componentName + ":" + JNDIFrame.getAppContextProvider().getApplicationName();
      } else {
        // component name is null, probably the call is from library container => check id jndi access forbidden
        if (JNDIFrame.getAppContextProvider().isJndiAccessForbidden()) {
          NamingException ne = new NamingException("Obtaining InitialContext from application library is forbidden. Application name is " + JNDIFrame.getAppContextProvider().getApplicationName() + ".");
          if (LOG_LOCATION.bePath()) {
            LOG_LOCATION.traceThrowableT(Severity.PATH, "Obtaining InitialContext from application library is forbidden. Application name is " + JNDIFrame.getAppContextProvider().getApplicationName() + ".", ne);
          }
          throw ne;
        }
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("The component name cannot be retrieved in a getInitialContext operation although the thread is an application thread and the appcontext interface is registered.");
        }
      }
    }

    try {
      if ((env.get("server") != null) && (env.get("force_remote") == null)) {
        // server side client
        String factoryName = (String) env.get(Context.INITIAL_CONTEXT_FACTORY);

        if (factoryName != null && (!"com.sap.engine.services.jndi.InitialContextFactoryImpl".equals(factoryName))) {
          if (componentName != null) {
            ClassLoader loader = null;
            int lindex = componentName.lastIndexOf(':');

            if (lindex != -1) {
              loader = JNDIFrame.getClassLoader(componentName.substring(lindex + 1));
            } else {
              loader = JNDIFrame.getClassLoader("service:naming");
            }

            Class factoryClass = null;
            try {
              factoryClass = Class.forName(factoryName, true, loader);
            } catch (Exception e) {
              factoryClass = Class.forName(factoryName, true, JNDIFrame.getClassLoader("service:naming"));
            }
            InitialContextFactory loadedFactory = (InitialContextFactory) factoryClass.newInstance();
            return loadedFactory.getInitialContext(environment);
          } else {
            return ((InitialContextFactory) (Class.forName(factoryName, true, JNDIFrame.getClassLoader("service:naming"))).newInstance()).getInitialContext(environment);
          }
        }

        // will check for security context object
        if (JNDIFrame.threadSystem == null) {
          throw new NamingException("The JNDI Registry Service is not started.");
        }
        ThreadContext threadContext = JNDIFrame.threadSystem.getThreadContext();
        LoginContext loginContext = null;
        LoginHelper loginHelper = new LoginHelper();
        if (threadContext != null) { // if the thread is system => no authentication is required
          SecurityContextObject contextObject = ((SecurityContextObject) threadContext.getContextObject("security"));
          if (contextObject != null) {
            if (contextObject.getSession() != null) {
              if (contextObject.getSession().getAuthenticationConfiguration() == null) { // anonimous user => will try to login
                loginHelper.serverSideLogin(env);
              } else {
                if (LOG_LOCATION.bePath()) {
                  Principal principle = contextObject.getSession().getPrincipal();
                  LOG_LOCATION.pathT("Trying to get InitialContext from an already authenticated client -> no authentication is required. Client information: component name: " + componentName + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((principle != null) ? principle.getName() : "null") + ".");
                }
              }
            } else {
              if (LOG_LOCATION.bePath()) {
                LOG_LOCATION.pathT("No SecuritySession is available in the Thread when trying to get InitialContext -> no authentication can be made. Client information: component name: " + componentName + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ".");
              }
            }
          } else {
            if (LOG_LOCATION.bePath()) {
              LOG_LOCATION.pathT("No SecurityContextObject is set in the Thread when trying to get InitialContext -> no authentication can be made. Client information: component name: " + componentName + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ".");
            }
          }
        } else {
          if (LOG_LOCATION.bePath()) {
            LOG_LOCATION.pathT("Trying to get InitialContext from a system thread -> no authentication is required. Client information: component name: " + componentName + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ".");
          }
        }

        if (env.get("domain") == null || !((String) env.get("domain")).equalsIgnoreCase("true")) {
          if (componentName != null) {
            ServerContextImpl sc = ((ServerContextImpl) NamingManager.getNamingManager().getProxy().getNewServerContext(false, false));
            return (new OffsetClientContext(env, sc, true, null, componentName, true, loginHelper));
          }
        } else if (componentName != null) {
          ServerContextImpl sc = ((ServerContextImpl) NamingManager.getNamingManager().getProxy().getNewServerContext(false, true));
          return (new OffsetClientContext(env, sc, true, null, componentName, false, loginHelper));
        }
        ServerContextImpl sc = ((ServerContextImpl) NamingManager.getNamingManager().getProxy().getNewServerContext(false, false));
        return new ClientContext(env, sc, true, null, loginHelper);

      } else { // remote client
        appClientName = null;

        if (env.get("appclient") == null) {
          VirtualPermission permission = new VirtualPermission(null);
          try {
            AccessController.checkPermission(permission);
          } catch (Exception e) {
            // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here
            // Please do not remove this comment!
            appClientName = null;
          }
          appClientName = permission.getComponentName();

          if (appClientName != null) {
            if (appClientName.startsWith("@a@p@p")) {
              appClientName = appClientName.substring(6);
              // additional
            } else if (appClientName.equals("@e@j@b")) {
              try {
                AccessController.checkPermission(new HomeHandlePermission());
                appClientName = null;
                beaLoggedIn = true;
              } catch (Exception ex) {
                LOG_LOCATION.traceThrowableT(Severity.PATH, "", ex);
                appClientName = null; //here i dont know what to do.Somethimg like default client context??
              }
              // additional end
            } else {
              appClientName = null;
            }
          }
        }
      }
    } catch (Exception e) { // propably applet client
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception caught when trying to get InitialContext. Exception is: " + e.toString() + " Client information: component name: " + componentName + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ".", e);
      }
      NamingException ne = new NamingException("Error getting the server-side naming service functionality during getInitialContext operation.");
      ne.setRootCause(e);
      throw ne;
    }

    if (environment.get("java.naming.corba.orb") != null) {
      Object COSNamingfactory = null;
      try {
        COSNamingfactory = Class.forName("com.sap.engine.services.jndi.CosNamingInitialContextFactoryImpl").newInstance();
      } catch (Exception e) {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception caught when trying to get InitialContext. Exception is: " + e.toString() + " Client information: component name: " + componentName + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ".", e);
        }
        NamingException ne = new NamingException("Error getting the server-side naming service functionality during getInitialContext operation.");
        ne.setRootCause(e);
        throw ne;
      }
      return ((InitialContextFactory) COSNamingfactory).getInitialContext(environment);
    }

//---------------- Remote client ----------------------------------------------
    // Merge static props with the environment ones
    String conn_type = null;
    if ((conn_type = (String) env.get("TransportLayerQueue")) == null) {
      String temp = systemProperties.getProperty("TransportLayerQueue");
      if (temp != null) {
        conn_type = temp;
        env.put("TransportLayerQueue", temp);
      } else {
        temp = SystemProperties.getProperty("TransportLayerQueue");
        if (temp != null) {
          conn_type = temp;
          env.put("TransportLayerQueue", temp);
        }
      }
    }

    String provider_url = null;
    if ((provider_url = (String) env.get(Context.PROVIDER_URL)) == null) {
      String temp = systemProperties.getProperty(Context.PROVIDER_URL);
      if (temp != null) {
        provider_url = temp;
        env.put(Context.PROVIDER_URL, temp);
      }
    }

    if (provider_url == null) {
      // no way to connect to any name server if the provider_url is not specified => throw Exception
      NamingException ne = new NamingException("No InitialContext can be created because the java.naming.provider.url property is no specified in the jndi environment.");
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", ne);
      }
      throw ne;
    }

    // check if the provider url has protocol specified and add P4:// (as default protocol) if not
    if (provider_url.indexOf(PROTOCOL_DELIMITER) == -1) {
      provider_url = RemoteEnvironment.P4 + PROTOCOL_DELIMITER + provider_url;
    }

    try {
      // set ClientSerializationFactory to use in serialization/deserialization process during bind/lookup
      if (RemoteSerializator.serializatorFactory == null) {
        Class factory = Class.forName("com.sap.engine.services.jndi.persistent.ClientSerializatorFactory");
        RemoteSerializator.serializatorFactory = (SerializatorFactory) factory.newInstance();
      }

      // check if user and pass present in the environemnt and if not try to get them from SystemProperties
      // CTS is configured this way (to set the user and pass in the system properties)
      String user = (String) env.get(Context.SECURITY_PRINCIPAL);
      String pass = (String) env.get(Context.SECURITY_CREDENTIALS);

      if (user == null && pass == null) {
        user = SystemProperties.getProperty("_" + Context.SECURITY_PRINCIPAL);
        pass = SystemProperties.getProperty("_" + Context.SECURITY_CREDENTIALS);
        if (user != null || pass != null) {
          env.put(Context.SECURITY_PRINCIPAL, user);
          env.put(Context.SECURITY_CREDENTIALS, pass);
          if (LOG_LOCATION.bePath()) {
            LOG_LOCATION.pathT("Security credentials and principal will be obtained from System.properties. Client information: component name: " + componentName + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ". Provider_URL: " + env.get(Context.PROVIDER_URL) + ".");
          }
        }
      }

      // Get RemoteBroker
      Destination destination = CrossObjectBroker.getDestination(provider_url, getProperties(env));

      // if remote broker is already set in the environemnt use it instead of getting a new one
      RemoteBroker broker = null;
      if ((broker = (RemoteBroker) env.get(Constants.P4_OBJECT_BROKER)) == null) {
        broker = destination.getRemoteBroker();
      }

      //check security credentials
      LoginHelper loginContext = new LoginHelper();
      Throwable exceptionCause = null;
      JNDIProxy proxy = null;
      boolean tryAgain = true;

      if (!beaLoggedIn) {
        do {
          try {
            if (env.get("force_remote") != null && env.get("server") != null && env.get("server").equals("true")) {
              // server side client that wants to get naming from different cluster
              loginContext.serverSideLogin(env, broker);
            } else {
              // remote client
              loginContext.clientSideLogin(env, broker);
            }
            // login is successful => get jndi proxy
            proxy = (JNDIProxy) broker.resolveInitialReference("naming", JNDIProxy.class);
            // set p4 ObjectBroker to use in case the same environment is used for creation of InitialContext
            env.put(Constants.P4_OBJECT_BROKER, broker);
            tryAgain = false;
          } catch (LoginException le) {
            exceptionCause = le;
            while (null != exceptionCause) {
              if (exceptionCause instanceof LoginExceptionDetails) {
                break;
              } else {
                exceptionCause = exceptionCause.getCause();
              }
            }

            if (exceptionCause instanceof LoginExceptionDetails && ((LoginExceptionDetails) exceptionCause).getExceptionCause() < 0) {
              // if the reason for the failier is p4 connection
              broker = handleConnectionPropblem(le, destination, env, componentName, broker);
            } else {
              // user problems
              handleUserProblem(le, env, componentName);
              tryAgain = false;
            }
          } catch (Exception e) {
            broker = handleConnectionPropblem(e, destination, env, componentName, broker);
          }
        } while (tryAgain);
      }

      // connection is made => set the connection info in the static properties
      if (provider_url != null) {
        systemProperties.setProperty(Context.PROVIDER_URL, provider_url);
      }
      // remove cacheing the connection type to allow the user to use the default p4 connection type if it is not
      // explicitly stated in jndi env instead of connecting the stub to the last used connection type.
//      if (conn_type != null) {
//        systemProperties.setProperty("TransportLayerQueue", conn_type);
//      }

      // get server context from the remote jndi proxy object
      ServerContextInface serverContextInface = proxy.getNewServerContext(true, true);
      // all remote clients will get replicated context in order to enable the redirectable support (failover)
      if (env.get("Replicate") == null) {
        env.put("Replicate", "true");
      }

      if (appClientName == null) {
        if (componentName != null) {
          return (new OffsetClientContext(env, serverContextInface, false, null, componentName, false, loginContext));
        } else {
          return new ClientContext(env, serverContextInface, false, new P4ReferenceFactory(), loginContext);
        }
      } else {
        return (ClientContext) (new ClientContext(env, serverContextInface, false, new P4ReferenceFactory(), loginContext).lookup("appclients/" + appClientName));
      }
    } catch (javax.naming.NamingException ne) {
      // NamingException is thrown by the naming itself during login or resolve initial reference => just rethrow it
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.traceThrowableT(Severity.PATH, "", ne);
      }
      throw ne;
    } catch (LoginException le) {
      //connection or user problem => check and throw the corresponding naming exception
      Throwable exceptionCause = le;
      while (null != exceptionCause) {
        if (exceptionCause instanceof LoginExceptionDetails) {
          break;
        } else {
          exceptionCause = exceptionCause.getCause();
        }
      }

      if (exceptionCause instanceof LoginExceptionDetails && ((LoginExceptionDetails) exceptionCause).getExceptionCause() < 0) {
        // connection problem
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during get InitialContext operation. Exception is: " + (le.toString() + "#LoginExceptionDetails.getExceptionCause()=" + ((LoginExceptionDetails) exceptionCause).getExceptionCause()) + ". Cannot establish connection to the remote server. Client information: component name: " + ((componentName != null) ? componentName : "N/A") + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ". Provider_URL: " + ((env.get(Context.PROVIDER_URL) != null) ? env.get(Context.PROVIDER_URL) : "N/A") + ". Verify that the java.naming.provider.url property is correctly set and there is running server process in the specified cluster.", le);
        }
        NamingException ne = new NamingException("Exception during getInitialContext operation. Cannot establish connection to the remote server with host and port:" + ((env.get(Context.PROVIDER_URL) != null) ? env.get(Context.PROVIDER_URL) : "N/A") + ". Verify that the java.naming.provider.url property is correctly set and there is running server process in the specified cluster.");
        ne.setRootCause(le);
        throw ne;
      } else {
        // user problems
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during getInitialContext operation. Exception is: " + (le.toString() + "#LoginExceptionDetails.getExceptionCause()=" + ((LoginExceptionDetails) exceptionCause).getExceptionCause()) + ". Wrong security principal/credentials. Client information: component name: " + ((componentName != null) ? componentName : "N/A") + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ". Provider_URL: " + ((env.get(Context.PROVIDER_URL) != null) ? env.get(Context.PROVIDER_URL) : "N/A") + ". Verify that the values of the java.naming.security.principal and java.naming.security.credentials properties are correct.", le);
        }
        JNDIFrame.log.logCatching(le);
        NoPermissionException npe = new NoPermissionException("Exception during getInitialContext operation. Wrong security principal/credentials.");
        npe.setRootCause(le);
        throw npe;
      }
    } catch (Exception e) {
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception caught when trying to get InitialContext. Exception is: " + e.toString() + " Client information: component name: " + ((componentName != null) ? componentName : "N/A") + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ". Provider URL: " + ((env.get(Context.PROVIDER_URL) != null) ? env.get(Context.PROVIDER_URL) : "N/A") + ".", e);
      }
      NamingException ne = new NamingException("Exception while trying to get InitialContext.");
      ne.setRootCause(e);
      throw ne;
    }
  }

  private Properties getProperties(Hashtable environment) {
    Properties properties = new Properties();
    for (Enumeration e = environment.keys(); e.hasMoreElements();) {
      String key = (String) e.nextElement();
      properties.put(key, environment.get(key));
    }
    return properties;
  }

  private RemoteBroker handleConnectionPropblem(Exception ex, Destination destination, Hashtable env, String componentName, RemoteBroker remote) throws NamingException {
    try {
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.traceThrowableT(Severity.PATH, "Connection to instance cannot be made during getInitialContext operation using remote broker " + remote + ". Naming service will try to connect to another instance if available.", ex);
      }

      return destination.getNextAvailableBroker();
    } catch (Exception e) {
      // no next broker (connection) is available => rethrow the exception
      if (LOG_LOCATION.bePath()) {
        LOG_LOCATION.traceThrowableT(Severity.PATH, "Cannot connect to any instance => throw exception to the caller.", e);
      }
      if (LOG_LOCATION.beInfo()) {
        LOG_LOCATION.infoT("Exception during get InitialContext operation. Exception is: " + ex.toString() + ". Cannot establish connection to the remote server. Client information: component name: " + ((componentName != null) ? componentName : "N/A") + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ". Provider_URL: " + ((env.get(Context.PROVIDER_URL) != null) ? env.get(Context.PROVIDER_URL) : "N/A") + ". Verify that the java.naming.provider.url property is correct and there are running dispatcher and server in the cluster specified.");
      }
      NamingException ne = new NamingException("Exception during getInitialContext operation. Cannot establish connection to the remote server.");
      ne.setRootCause(ex);
      throw ne;
    }
  }

  private void handleUserProblem(LoginException le, Hashtable env, String componentName) throws javax.naming.NamingException {
    if (LOG_LOCATION.beInfo()) {
      LOG_LOCATION.traceThrowableT(Severity.INFO, "Exception during getInitialContext operation. Exception is: " + le.toString() + ". Wrong security principal/credentials. Client information: component name: " + ((componentName != null) ? componentName : "N/A") + ". Initial context factory used: " + ((env.get(Context.INITIAL_CONTEXT_FACTORY) != null) ? env.get(Context.INITIAL_CONTEXT_FACTORY) : "N/A") + ". Security Principal: " + ((env.get(Context.SECURITY_PRINCIPAL) != null) ? env.get(Context.SECURITY_PRINCIPAL) : "N/A") + ". Provider_URL: " + ((env.get(Context.PROVIDER_URL) != null) ? env.get(Context.PROVIDER_URL) : "N/A") + ". Verify that the values of the java.naming.security.principal and java.naming.security.credentials properties are correct.", le);
    }
    JNDIFrame.log.logCatching(le);
    NoPermissionException npe = new NoPermissionException("Exception during getInitialContext operation. Wrong security principal/credentials.");
    npe.setRootCause(le);
    throw npe;
  }


}
