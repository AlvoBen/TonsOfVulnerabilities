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

import java.util.*;
import java.net.InetAddress;

import org.omg.CORBA.ORB;

import javax.naming.spi.InitialContextFactory;
//import com.inqmy.frame.ThreadContext;
import com.sap.engine.services.jndi.cosnaming.CosNamingContext;

import javax.naming.NamingException;

import com.sap.engine.boot.SystemProperties;

import javax.naming.Context;

/**
 * The initial context factory provides InQMy Context Implementation.
 * Class implements javax.naming.spi.InitialContextFactory
 *
 * @author Pavel Zlatarev
 * @version 4.00
 * @see javax.naming.spi.InitialContextFactory
 */
public class CosNamingInitialContextFactoryImpl implements InitialContextFactory {

  public static final String DEFAULT_INIT_ORB_PORT = "3333";
  public static final String IIOP_PREFIX = "iiop://";
  private static Object monitor = new Object();

  /**
   * Returns new initial context, as defined in the specification
   *
   * @param environment Enviroment properties
   * @return Context
   * @throws javax.naming.NamingException
   */
  public Context getInitialContext(Hashtable environment) throws javax.naming.NamingException {
    synchronized (monitor) {
      try {
        Hashtable env = (environment == null) ? SystemProperties.getProperties() : (Hashtable) environment.clone(); //?!?!
        //        Hashtable jndiEnv = (Hashtable) env.clone();
        //        jndiEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
        //        Context jndiContext = new InitialContextFactoryImpl().getInitialContext(jndiEnv); //security
        //
        //        if ((System.getProperty("server") != null) && (System.getProperty("memory") == null)) {
        //          return jndiContext;
        //        }
        ORB orb = null;
        Object orbObject = env.get("java.naming.corba.orb");

        if (orbObject != null && orbObject instanceof ORB) {
          orb = (ORB) orbObject;
        } else {
          String url = (env.containsKey(Context.PROVIDER_URL)) ? env.get(Context.PROVIDER_URL).toString().trim() : SystemProperties.getProperty(Context.PROVIDER_URL);
          if (url == null) {
            url = (InetAddress.getLocalHost()).getHostAddress();
          }
          // System.setProperty(Context.PROVIDER_URL, url);
          if (url.startsWith(IIOP_PREFIX)) {
            url = url.substring(url.indexOf(IIOP_PREFIX) + IIOP_PREFIX.length());
          }
          String host = url;
          String port = DEFAULT_INIT_ORB_PORT;
          //check if it looks like ipv6 url
          if (host.charAt(0) == '[') {
            //seems like ipv6 url e.g. [fec0::1]:50007
            int ipv6end = host.indexOf(']');
            if (ipv6end == -1) {
              throw new NamingException("Exception during getInitialContext operation. Wrong URL: cannot establish connection.");
            }
            //check if there is a :port at the end
            if ((host.length() > ipv6end + 2) && (host.charAt(ipv6end + 1) == ':')) {
              port = host.substring(ipv6end + 2);
            }
            host = host.substring(1, ipv6end);
          } else {
            // ipv4 or a hostname
            int colonIndex = host.indexOf(':');
            if (colonIndex != -1) {
              port = host.substring(colonIndex + 1);
              host = host.substring(0, colonIndex);
            }
          }

          //        String user = (String) env.get(Context.SECURITY_PRINCIPAL);
          //        String pass = (String) env.get(Context.SECURITY_CREDENTIALS);
          //
          //        if (user == null)  {
          //          user = "Administrator";
          //          pass = "";
          //        }
          Properties props = new Properties();
          props.putAll(environment);
          props.put("org.omg.CORBA.ORBInitialPort", port);
          props.put("org.omg.CORBA.ORBInitialHost", host);
          orb = ORB.init(new String[0], props);
        }

        org.omg.CORBA.Object obj = orb.resolve_initial_references("NameService");
        org.omg.CosNaming.NamingContext cosNaming = org.omg.CosNaming.NamingContextExtHelper.narrow(obj);
        CosNamingContext result = new CosNamingContext(cosNaming, env);

        //        String appClientName = getAppClientName(env);
        //        if(appClientName != null) result = (CosNamingContext) result.lookup("appclients/" + appClientName);
        if ("true".equals(env.get("global"))) {
          //           ThreadContext.setGlobalSessionId(ThreadContext.getSessionId());
        }

        return result;
      } catch (Exception e) {
        NamingException ne = new NamingException("Exception while trying to get InitialContext.");
        ne.setRootCause(e);
        throw ne;
      }
    }
  }

  //
  //  private String getAppClientName(Hashtable env) {
  //    String appClientName = null;
  //    if (env.get("appclient") == null) {
  //      VirtualPermission permission = new VirtualPermission(null);
  //      try {
  //        AccessController.checkPermission(permission);
  //      } catch (Exception e) {
  //      }
  //      appClientName = permission.getComponentName();
  //
  //      if (appClientName != null) {
  //        if (appClientName.startsWith("@a@p@p")) {
  //          appClientName = appClientName.substring(6);
  //          // additional
  //        } else if (appClientName.equals("@e@j@b")) {
  //          try {
  //            AccessController.checkPermission(new HomeHandlePermission());
  //          } catch (Exception ex) {
  //            appClientName = null; //here i dont know what to do.Somethimg like default client context??
  //          }
  //        } else {
  //          appClientName = null;
  //        }
  //      }
  //    }
  //    return appClientName;
  //  }

}

