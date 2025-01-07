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
package com.sap.engine.services.jndi.providerimpl;

import com.sap.engine.system.naming.provider.IResolver;

import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.StateFactory;
import javax.naming.Context;
import java.util.Hashtable;

/*
 * 
 * @author Elitsa Pancheva
 * @version 6.30
 */

public class DefaultResolver implements IResolver {


  private static Hashtable initialContextFactories = new Hashtable();
  private static Hashtable urlContextFactories = new Hashtable();
  private static Hashtable objectFactories = new Hashtable();
  private static Hashtable stateFactories = new Hashtable();

  public static void addInitialContextFactoryImpl(InitialContextFactory initialContextFactory) {
//    System.out.println("DefaultResolver.addInitialContextFactoryImpl : "+ initialContextFactory);
    String className = initialContextFactory.getClass().getName();

    if (initialContextFactories.get(className) == null) {
      initialContextFactories.put(className, initialContextFactory);
    }
  }

  public static void addURLContextFactoryImpl(ObjectFactory urlContextFactory) {
//    System.out.println("DefaultResolver.addURLContextFactoryImpl : " + urlContextFactory);
    String className = urlContextFactory.getClass().getName();

    if (urlContextFactories.get(className) == null) {
      urlContextFactories.put(className, urlContextFactory);
    }
  }

  public static void addObjectFactoryImpl(ObjectFactory objectFactory) {
//    System.out.println("DefaultResolver.addObjectFactoryImpl : " + objectFactory);
    String className = objectFactory.getClass().getName();

    if (objectFactories.get(className) == null) {
      objectFactories.put(className, objectFactory);
    }
  }

  public static void addStateFactoryImpl(StateFactory stateFactory) {
//    System.out.println("DefaultResolver.addStateFactoryImpl : " + stateFactory);
    String className = stateFactory.getClass().getName();

    if (stateFactories.get(className) == null) {
      stateFactories.put(className, stateFactory);
    }
  }


  public InitialContextFactory getInitialContextFactory(Hashtable environment) throws javax.naming.NamingException {
    //    System.out.println("DefaultResolver.getInitialContextFactory : " + environment);
    String factoryName = environment != null ? ((String) environment.get(Context.INITIAL_CONTEXT_FACTORY)) : null;
    InitialContextFactory initialFactory = null;

    if (factoryName != null) {
      synchronized (this.initialContextFactories) {
        initialFactory = (InitialContextFactory) initialContextFactories.get(factoryName);
      }
    }
//    System.out.println("DefaultResolver.getInitialContextFactory  name: " + factoryName + " | will return -> " + initialFactory);
    return initialFactory;
  }

  public ObjectFactory getURLContextFactory(String scheme, Hashtable env) throws javax.naming.NamingException {
    //    System.out.println("DefaultResolver.getURLContextFactory : " + scheme);
    ObjectFactory factory = null;
    synchronized (this.urlContextFactories) {
      factory = (ObjectFactory) this.urlContextFactories.get(scheme);
	  }
    //    System.out.println("DefaultResolver.getURLContextFactory with scheme: " + scheme + " | will return " + factory);
    return factory;
  }

  public ObjectFactory getObjectFactory(String name) throws javax.naming.NamingException {
    //    System.out.println("DefaultResolver.getObjectFactory name = " + name);
    ObjectFactory factory = null;
    synchronized (this.objectFactories) {
      factory = (ObjectFactory) this.objectFactories.get(name);
    }
    //    System.out.println("provider.getObjectFactory("+name+") = " + factory);
    return factory;
  }

  public StateFactory getStateFactory(String name) throws javax.naming.NamingException {
//    System.out.println("DefaultResolver.getStateFactory name = " + name);
    StateFactory factory = null;
	  synchronized (this.stateFactories) {
      factory = (StateFactory) this.stateFactories.get(name);
      //    System.out.println("DefaultResolver.getStateFactory  factory name = " + name + " | factory = " + factory);
    }
	  return factory;
  }

//  public Properties getProperties() {
//    Properties properties = new Properties();
//    // add ICF
//    String icf = null;
//    Enumeration keys = initialContextFactories.keys();
//    while (keys.hasMoreElements()) {
//      icf = icf + "," + keys.nextElement();
//    }
//    if (icf != null) {
//     properties.put(Manager.INITIAL_CONTEXT_FACTORIES, icf.substring(1));
//    }
//    // add OF
//    String of = null;
//    keys = objectFactories.keys();
//    while (keys.hasMoreElements()) {
//      of = of + "," + keys.nextElement();
//    }
//    if (of != null) {
//     properties.put(Manager.OBJECT_FACTORIES, of.substring(1));
//    }
//    //add SF
//    String sf = null;
//    keys = stateFactories.keys();
//    while (keys.hasMoreElements()) {
//      sf = sf + "," + keys.nextElement();
//    }
//    if (sf != null) {
//     properties.put(Manager.STATE_FACTORIES, sf.substring(1));
//    }
//    //add scheme
//    String scheme = null;
//    keys = urlContextFactories.keys();
//    String temp =null;
//    while (keys.hasMoreElements()) {
//      // URLContextFactory syntaxis - package_prefix . scheme . schemeURLContextFactory
//      temp = ((String) keys.nextElement());
//      temp = temp.substring(0, temp.lastIndexOf("."));
//      temp = temp.substring(temp.lastIndexOf("."));
//      scheme = scheme + "," + temp;
//    }
//    if (scheme != null) {
//     properties.put(Manager.SCHEMAS, scheme.substring(1));
//    }
//    System.out.println("DefaultResolver.getProperties properties = " + properties);
//    return properties;
//  }


}
