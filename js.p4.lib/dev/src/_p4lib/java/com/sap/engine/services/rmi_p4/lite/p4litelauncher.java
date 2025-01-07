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

package com.sap.engine.services.rmi_p4.lite;

import com.sap.engine.services.rmi_p4.P4ObjectBroker;
import com.sap.engine.services.rmi_p4.classload.DynamicClassLoader;
import com.sap.engine.services.rmi_p4.classload.ClassLoaderContext;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import com.sap.engine.interfaces.cross.CrossObjectBroker;
import com.sap.engine.interfaces.cross.Destination;

import java.util.Properties;
import java.util.Vector;
import java.util.List;
import java.io.File;
import java.lang.reflect.Method;

/**
 * User: Mladen Droshev
 * Date: 2005-8-25
 */
public class P4LiteLauncher {

  private static boolean debug = false;

  static{
    if(System.getProperty("debug") != null){
      debug = ((String) System.getProperty("debug")).equalsIgnoreCase("true");
    }
  }

  public static final String J2EE_URL = "j2ee-url";
  public static final String P4_LITE_NAME_OBJECT = "P4LiteNameObject";
  public static final String CLIENT_TO_LAUNCH = "LaunchClientClass";
  public static final String CLIENT_CLASSPATH = "clientClasspath";
  public static final String CLIENT_ARGS = "Client Args";
  public static final String APP_CLASSPATH = "appClasspath";
  public static final String PERM_CACHE = "permCache";
  public static String defaultP4Name = "_P4Lite_";
  public static String defaultClientClasspaht = ".";
  public static String[] defaultArgs = new String[0];
  static P4ObjectBroker broker = P4ObjectBroker.init();
  public static String className = null;
  String[] methodArgs = null;

  static Properties props = new Properties();
  P4Lite p4LiteRefs = null;
  Object info = null;
  boolean enablePermCache = false;
  PermanentCacheOrganizer pco = null;

  public static void main(String[] args) {
    if (P4Logger.getLocation().beInfo()) {
      P4Logger.getLocation().infoT("P4LiteLauncher.main(String[])", "LiteLauncher is started...");
    }
    
    if (P4Logger.getLocation().beDebug()){
      StringBuffer mes = new StringBuffer ("\r\n  > P4 Launcher <  \r\n    DEBUG ON \r\n ");
      mes.append("\r\nSystem Properties \r\n[");
      mes.append(System.getProperties());
      mes.append("]\r\n");
      P4Logger.getLocation().debugT("P4LiteLauncher.main(String[])", mes.toString());
    }

    parseArgs(args);

    P4LiteLauncher client = new P4LiteLauncher();
    try {
      client.doIt(args);
    } catch (Throwable t) {
      if (P4Logger.getLocation().bePath()) {
        StringBuffer mes = new StringBuffer(" Exception catched: ");
        mes.append(t.getMessage());
        if (P4Logger.getLocation().beDebug()){
          mes.append("\r\n");
          mes.append(P4Logger.exceptionTrace(t));
          P4Logger.getLocation().debugT("P4LiteLauncher.doIt(String[])", mes.toString());
        }else{
          P4Logger.getLocation().pathT("P4LiteLauncher.doIt(String[])", mes.toString());
        }
      }
    }

  }

  public void doIt(String[] args) {
    ClassLoader p4Loader = null;
    try {
      //System.setSecurityManager(null);
      try {
        Destination destination = CrossObjectBroker.getDestination((String) props.get(J2EE_URL), null);
        com.sap.engine.interfaces.cross.RemoteBroker remoteBroker = destination.getRemoteBroker();
        p4LiteRefs = (P4Lite) remoteBroker.resolveInitialReference((String) props.get(P4_LITE_NAME_OBJECT), P4Lite.class);
        p4Loader = p4LiteRefs.getClass().getClassLoader();
      } catch (Exception e) {
        System.out.println("\r\nWARNING: There is no configured engine, so the remote loader will use only local resources\r\n");
        if(debug){
          e.printStackTrace();
        }
        // Local Loader
        p4Loader = ClassLoaderContext.getDynamicLoader(-1); 
        ((DynamicClassLoader)p4Loader).setAsLocalLoader(true);
      }

      if (p4Loader instanceof DynamicClassLoader) {
        if (debug){
          ((DynamicClassLoader) p4Loader).keepStatistic(true);
        }

        if (props.getProperty(APP_CLASSPATH) == null){
          ((DynamicClassLoader) p4Loader).setClasspath(props.getProperty(CLIENT_CLASSPATH));
        }else{
          ((DynamicClassLoader) p4Loader).setClasspath(props.getProperty(CLIENT_CLASSPATH) + File.pathSeparatorChar + props.getProperty(APP_CLASSPATH));
        }
        
        if (props.getProperty(PERM_CACHE) != null) {
          enablePermCache=true;
          pco = new PermanentCacheOrganizer(props.getProperty(PERM_CACHE));
          ((DynamicClassLoader) p4Loader).enablePermCache(pco);
        }
      }
      
      Thread.currentThread().setContextClassLoader(p4Loader);

/*    
      if (P4Logger.getLocation().beDebug()) {
        StringBuffer mes = new StringBuffer("\r\nSystem Property \"java.class.path\": \r\n");
        mes.append(System.getProperty("java.class.path"));
        mes.append("\r\n \r\nArgument Property \"clientClasspath\": \r\n");
        mes.append(props.getProperty(CLIENT_CLASSPATH));
        P4Logger.getLocation().debugT("P4LiteLauncher.doIt(String[])", mes.toString());
      }
*/
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("doIt(String[])", "Launching: " + props.get(CLIENT_TO_LAUNCH));
      }
      
      Class mainClass = Class.forName((String) props.get(CLIENT_TO_LAUNCH), true, p4Loader);
      Method mainMethod = mainClass.getDeclaredMethod("main", new Class[]{props.get(CLIENT_ARGS).getClass()});
      mainMethod.invoke(null, new Object[]{props.get(CLIENT_ARGS)});

      if (props.getProperty(PERM_CACHE) != null && p4Loader instanceof DynamicClassLoader) {
        ((DynamicClassLoader) p4Loader).stopPermCache();
      }

      if (debug && P4ObjectBroker.getBytesStatistics() > 0) {
        System.out.println("------------------------------------------------------------------------");
        System.out.println("                    Remote Class Loading Statistics                     ");
        System.out.println("  Total Download Bytes: " +  P4ObjectBroker.getBytesStatistics()         );
        System.out.println("------------------------------------------------------------------------");
        List l =  P4ObjectBroker.getClassStatistics();
        for (Object o: l) {
          System.out.println("|-" + o);
        }
        System.out.println("------------------------------------------------------------------------");
      }
    } catch (Throwable e) {
      if (P4Logger.getLocation().bePath()) {
        StringBuffer mes = new StringBuffer("Exception catched: ");
        mes.append(e.toString());
        if (P4Logger.getLocation().beDebug()){
          mes.append("\r\n");
          mes.append(P4Logger.exceptionTrace(e));
          P4Logger.getLocation().debugT("P4LiteLauncher.doIt(String[])", mes.toString());
        }else{
          P4Logger.getLocation().pathT("P4LiteLauncher.doIt(String[])", mes.toString());
        }
      }
      //In case of exception from Main method of executed test or application
      if (props.getProperty(PERM_CACHE) != null && p4Loader instanceof DynamicClassLoader) {
        ((DynamicClassLoader) p4Loader).stopPermCache();
      }
    } 
/*    
    finally{
      try{//for logging purposes
      Class c = Class.forName("com.sap.cts.porting.impl.CTSLoginContext");
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4LiteLauncher.doIt(String[])", " 1>> Classloader during Class.forName for com.sap.cts.porting.impl.CTSLoginContext - " + c.getClassLoader());
      }
      } catch(Throwable t){ }
      try{
      Class c = Thread.currentThread().getContextClassLoader().loadClass("com.sap.cts.porting.impl.CTSLoginContext");
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("P4LiteLauncher.doIt(String[])", " 2>> Classloader during classload for com.sap.cts.porting.impl.CTSLoginContext - " + c.getClassLoader()); 
      }
      } catch(Throwable t){}
    }
*/
  }

  public static void parseArgs(String[] args) {
    /* what to resolve */
    props.put(P4_LITE_NAME_OBJECT, defaultP4Name);

    /* default class path value */
    props.put(CLIENT_CLASSPATH, System.getProperty("java.class.path"));

    /* arguments for main class */
    props.put(CLIENT_ARGS, defaultArgs);
    
    Vector<String> clientArgs = new Vector<String>();
    if (args != null && args.length > 1) {

      /* print all client arguments */
      if (P4Logger.getLocation().beDebug()){                 
        StringBuffer mes = new StringBuffer ("Client Arguments: { \r\n");
        for(int i = 0; i < args.length; i++){
          mes.append("arg[" + i + "] = " + args[i] + "\r\n");
        }
        mes.append("}\r\n");
        P4Logger.getLocation().debugT("P4LiteLauncher.parseArgs(String[])", mes.toString());
      }

      props.put(CLIENT_TO_LAUNCH, args[0]);
      for (int i = 1; i < args.length; i++) {

        if (args[i].equals(J2EE_URL)) {
          props.put(J2EE_URL, args[++i]);
        } else if (args[i].equals(P4_LITE_NAME_OBJECT)) {
          props.put(P4_LITE_NAME_OBJECT, args[++i]);
        } else if (args[i].equals(CLIENT_CLASSPATH)) {
          props.put(CLIENT_CLASSPATH, args[++i]);
        } else if (args[i].equals(APP_CLASSPATH)) {
          props.put(APP_CLASSPATH, args[++i]);
        } else if (args[i].equals(PERM_CACHE)) {
          props.put(PERM_CACHE, args[++i]);
        } else {
          clientArgs.addElement(args[i]);
        }
      }

    }
    if (clientArgs.size() > 0) {
      props.put(CLIENT_ARGS, clientArgs.toArray(new String[0]));
    }
  }

}
