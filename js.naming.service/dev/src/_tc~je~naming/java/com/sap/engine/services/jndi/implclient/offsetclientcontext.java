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

import java.util.Hashtable;
import javax.naming.directory.*;
import javax.naming.spi.NamingManager;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NameParser;
import javax.naming.NameNotFoundException;

import com.sap.engine.services.jndi.Constants;
import com.sap.engine.services.jndi.JNDIFrame;

import javax.naming.OperationNotSupportedException;
import javax.naming.NamingException;

import com.sap.engine.services.jndi.persistent.JNDILogConstants;
import com.sap.engine.services.jndi.persistent.JNDIResourceAccessor;
import com.sap.engine.services.jndi.implserver.ServerContextInface;
import com.sap.engine.frame.ProcessEnvironment;
import com.sap.localization.LocalizableTextFormatter;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

//import com.inqmy.frame.container.log.LogContext;
/**
 * Offset Context implementation. Extends normal Context implementation.
 * Purpose of this class is to offer dublicate root when operation with specific name prefix
 * is invoked. For example, in J2EE specification each EJBean lookups its properties and
 * other information from "java:comp/env", so all operations beginning with "java:comp" are
 * specific for the bean (and are executed in Context EJBeans/<BEAN_NAME>/java:comp/env). If
 * operation is without this prefix - the default naming Root Context is used.
 * This context also comes with Name-Object caching that is much more effective than the
 * server side Name-byte[] caching due to not done deserialization. Note: By this way
 * on different clients is returned the same instance, but this is not a problem for jsps and
 * beans
 *
 * @author Panayot Dobrikov
 * @author Petio Petev
 * @version 4.00
 */
public class OffsetClientContext extends ClientContext {
	
	private final static Location LOG_LOCATION = Location.getLocation(OffsetClientContext.class);
	
  /**
   * serial version UID
   */
  static final long serialVersionUID = 3791215843613427260L;
  /**
   * Prefix of the name
   */
  private String prefix;
  private String applicationName;
  private String appLoaderName;
  private ClassLoader additionalLoader = null; //$JL-SER$

  //stored for creation of new instances in lookup(name)
  private boolean runOnServer;
  private boolean usePrefix;
  // keeps the root context for this name space
  private String root;
 

  /**
   * Constructor
   *
   * @param environment enviroment to use
   * @param remoteContext Server context to use
   * @param runOnServer
   * @param refrenceFactory Remote object reference factory to use
   * @param prefix Prefix to add
   * @param usePrefix whether to use prefix
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public OffsetClientContext(Hashtable environment, ServerContextInface remoteContext, boolean runOnServer, RemoteObjectReferenceFactory refrenceFactory, String prefix, boolean usePrefix, LoginHelper loginCtx) throws javax.naming.NamingException {
    super(environment, remoteContext, runOnServer, refrenceFactory, loginCtx);
    this.runOnServer = runOnServer;
    this.usePrefix = usePrefix;
    int lindex = prefix.lastIndexOf(':');

    if (lindex != -1) {
      this.prefix = prefix.substring(0, lindex);
      this.applicationName = prefix.substring(lindex + 1);
      this.appLoaderName = this.applicationName;
      applicationName += ":";
      //System.out.println("mana: " + applicationName + " | " + this.prefix + " | " + classLoader);
    } else {
      this.prefix = prefix;
      this.appLoaderName = "service:naming";
      this.applicationName = "common:";
    }
    root = this.prefix;
    if (!usePrefix) {
      this.prefix = "";
    }
    try {
      //used in ServerCPOInputStream
      Object temp = environment.get("SampleLoader");

      if (temp != null) {
        this.additionalLoader = (ClassLoader) temp;
      } else {
        temp = environment.get("SampleClass");

        if (temp != null) {
          this.additionalLoader = temp.getClass().getClassLoader();
        }
      }
    } catch (java.lang.ClassCastException e) {
            LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      this.additionalLoader = null;
    }
    this.fullNameInSpace = new CompositeName();

        if (LOG_LOCATION.bePath()) {
      String loaderName = "n/a";
      String parentName = "n/a";
      String contextClassLoader = "n/a";
      ClassLoader classLoader = JNDIFrame.getClassLoader(this.appLoaderName);
      try {
        if (classLoader != null) {
          loaderName = classLoader.toString();
          parentName = classLoader.getParent().toString();
        } else {
          loaderName = "null";
        }
        contextClassLoader = Thread.currentThread().getContextClassLoader().toString();
      } catch (ThreadDeath td) {
        //$JL-EXC$
        throw td;
      } catch (OutOfMemoryError o) {
        ProcessEnvironment.handleOOM(o);
      } catch (Throwable t) {
                LOG_LOCATION.traceThrowableT(Severity.PATH, "", t);
      }

            LOG_LOCATION.pathT("OffsetClientContext is created for application " + applicationName + " with characteristics: prefix: " + this.prefix + ", usePrfix: " + usePrefix + ", classLoader: " + classLoader + ", loader name: " + loaderName + ", parent loader: " + parentName + ", context class loader: " + contextClassLoader + ", cacheEnabled: " + false + ".");
    }
  }

  public ClassLoader getApplicationClassLoader() {
    //    System.out.println("RETURNING LOADER: " + classLoader);
    return JNDIFrame.getClassLoader(this.appLoaderName);
  }

  public ClassLoader getAdditionalClassLoader() {
    //    System.out.println("RETURNING LOADER: " + additionalLoader);
    return this.additionalLoader;
  }

  /**
   * Context lookup
   *
   * @param name Name to lookup
   * @return Object requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Object lookup(Name name) throws javax.naming.NamingException {
    String originalName = name.toString();
    name = this.cleanName(name);
    Object obj = null;
    try {
      obj = super.lookup(name);
      //if context will return an OffsetClientContext
      if (obj instanceof ClientContext) {
        String originalPrefix = name.toString();
        ClientContext returnedContext = (ClientContext) obj;
        if ((applicationName != null) && !("common:".equals(this.applicationName))) {
          originalPrefix = originalPrefix + ":" + this.applicationName.substring(0, this.applicationName.length() - 1);
        }

        //when usePrefix == false it must take the clientcontext's remoteContext
        //because prefix == "" and in lookup it cannot get the object with right name !

        if (this.usePrefix) {
          obj = new OffsetClientContext(returnedContext.getEnvironment(), this.remoteContext, //here is the difference
              this.runOnServer, returnedContext.referenceFactory, originalPrefix, this.usePrefix, returnedContext.loginContext);
        } else {
          obj = new OffsetClientContext(returnedContext.getEnvironment(), returnedContext.remoteContext, //here is the difference
              this.runOnServer, returnedContext.referenceFactory, originalPrefix, this.usePrefix, returnedContext.loginContext);
        }
        if (!usePrefix) {
          ((OffsetClientContext) obj).fullNameInSpace = new CompositeName(returnedContext.getNameInNamespace());
        } else {
          ((OffsetClientContext) obj).root = this.root;
        }
      } else {
        //if ClientContext was unable to create it with the default classloader
        //will try with application's classloader
        if (obj instanceof Reference) {
          try {
            Thread thisThread = Thread.currentThread();
            ClassLoader oldClassLoader = thisThread.getContextClassLoader();
            ClassLoader newClassLoader = getApplicationClassLoader();
            thisThread.setContextClassLoader(newClassLoader);
            obj = NamingManager.getObjectInstance(obj, name, this, getEnvironment());
            thisThread.setContextClassLoader(oldClassLoader);
          } catch (Exception e) {
            NamingException ne = new NamingException("Exception during lookup operation of object with name " + name + ", cannot resolve object reference.");
            ne.setRootCause(e);
            throw ne;
          }
        }
      }
    } catch (NameNotFoundException e) {
      //v nikakav slu4aj da ne se vika s super.lookup(String) !!!
      //4e stava golqmo zaciklqne s virtualnite metodi na VM !!!!
      if (Constants.APPLICATION_ROOT_LOOKUP_ENABLED) {
        obj = super.lookup(parse(originalName));
        if (LOG_LOCATION.bePath()) {
          LOG_LOCATION.pathT("Unspecified lookup from the root of the naming system, name of the object is: " + name + ", name of the application is: " + applicationName + ".");                
        }
      } else {
        throw e;
      }
    }
    return obj;
  }

  /**
   * Context lookup
   *
   * @param name Name to lookup
   * @return Object requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Object lookup(String name) throws javax.naming.NamingException {
    return this.lookup(parse(name));
  }

  /**
   * DirContext bind
   *
   * @param name Name to bind
   * @param obj Object to bind
   * @param attrs Attributes to bind with
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void bind(String name, Object obj, Attributes attrs) throws javax.naming.NamingException {
    this.bind(parse(name), obj, attrs);
  }

  /**
   * DirContext bind
   *
   * @param name Name to bind
   * @param obj Object to bind
   * @param attrs Attributes to bind with
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void bind(Name name, Object obj, Attributes attrs) throws javax.naming.NamingException {
    name = this.cleanName(name);
    if (runOnServer && usePrefix) { //if not force_remote and not domain = true
      if (LOG_LOCATION.beWarning()) {
        String formater = "Application [" + applicationName + "] with prefix [" + this.prefix + "] [" + this.usePrefix + "] performs a BIND operation, thus violating the J2EE Specification v.1.3/1.4, section 5.2.4. Change operations, performed in the application namespace are not allowed!";
        SimpleLogger.trace(Severity.WARNING,LOG_LOCATION, "ASJ.jndi.000036", "{0}",  new Object[] { formater});
        if (LOG_LOCATION.beDebug()) {
          formater = "The caller stack trace for the BIND operation follows.";
          LOG_LOCATION.traceThrowableT(Severity.DEBUG, formater, new Exception("Application [" + applicationName + "] performs a forbidden BIND operation."));
        }
      }
    }
    super.bind(name, obj, attrs);
  }

  /**
   * DirContext bind
   *
   * @param name Name to bind
   * @param obj Object to bind
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void bind(String name, Object obj) throws javax.naming.NamingException {
    this.bind(parse(name), obj, null);
  }

  /**
   * DirContext bind
   *
   * @param name Name to bind
   * @param obj Object to bind
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void bind(Name name, Object obj) throws javax.naming.NamingException {
    this.bind(name, obj, null);
  }

  /**
   * Context rebind
   *
   * @param name Name to rebind
   * @param obj Object to rebind
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void rebind(Name name, Object obj) throws javax.naming.NamingException {
    this.rebind(name, obj, null);
  }

  /**
   * Context rebind
   *
   * @param name Name to rebind
   * @param obj Object to rebind
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void rebind(String name, Object obj) throws javax.naming.NamingException {
    this.rebind(parse(name), obj, null);
  }

  /**
   * DirContext rebind
   *
   * @param name Name to rebind
   * @param obj Object to rebind
   * @param attrs Attributes to be rebound with
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void rebind(Name name, Object obj, Attributes attrs) throws javax.naming.NamingException {
    name = this.cleanName(name);
    if (runOnServer && usePrefix) { //if not force_remote and not domain = true
      if (LOG_LOCATION.beWarning()) {
        String formater = "Application [" + applicationName + "] with prefix [" + this.prefix + "] [" + this.usePrefix + "] performs a REBIND operation, thus violating the J2EE Specification v.1.3/1.4, section 5.2.4. Change operations, performed in the application namespace are not allowed!";
        SimpleLogger.trace(Severity.WARNING,LOG_LOCATION, "ASJ.jndi.000040", "{0}",  new Object[] { formater});
        if (LOG_LOCATION.beDebug()) {
          formater = "The caller stack trace for the REBIND operation follows.";
          LOG_LOCATION.traceThrowableT(Severity.DEBUG, formater, new Exception("Application [" + applicationName + "] performs a forbidden REBIND operation."));
        }
      }
    }
    super.rebind(name, obj, attrs);
  }

  /**
   * DirContext rebind
   *
   * @param name Name to rebind
   * @param obj Object to rebind
   * @param attrs Attributes to be rebound with
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void rebind(String name, Object obj, Attributes attrs) throws javax.naming.NamingException {
    this.rebind(parse(name), obj, attrs);
  }

  /**
   * Context unbind
   *
   * @param name Name to unbind
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void unbind(Name name) throws javax.naming.NamingException {
    name = this.cleanName(name);
    if (runOnServer && usePrefix) { //if not force_remote and not domain = true
      if (LOG_LOCATION.beWarning()) {
        String formater = "Application [" + applicationName + "] with prefix [" + this.prefix + "] [" + this.usePrefix + "] performs a UNBIND operation, thus violating the J2EE Specification v.1.3/1.4, section 5.2.4. Change operations, performed in the application namespace are not allowed!";
        SimpleLogger.trace(Severity.WARNING,LOG_LOCATION, "ASJ.jndi.000042", "{0}",  new Object[] { formater});
        if (LOG_LOCATION.beDebug()) {
          formater = "The caller stack trace for the UNBIND operation follows.";
          LOG_LOCATION.traceThrowableT(Severity.DEBUG, formater, new Exception("Application [" + applicationName + "] performs a forbidden UNBIND operation."));
        }
      }
    }
    super.unbind(name);
  }

  /**
   * Context unbind
   *
   * @param name Name to unbind
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void unbind(String name) throws javax.naming.NamingException {
    this.unbind(parse(name));
  }

  /**
   * Context rename
   *
   * @param oldname Old name
   * @param newname New name
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void rename(Name oldname, Name newname) throws javax.naming.NamingException {
    oldname = this.cleanName(oldname);
    newname = this.cleanName(newname);
    if (runOnServer && usePrefix) { //if not force_remote and not domain = true
      if (LOG_LOCATION.beWarning()) {
        String formater = "Application [" + applicationName + "] with prefix [" + this.prefix + "] [" + this.usePrefix + "] performs a RENAME operation, thus violating the J2EE Specification v.1.3/1.4, section 5.2.4. Change operations, performed in the application namespace are not allowed!";
        SimpleLogger.trace(Severity.WARNING,LOG_LOCATION, "ASJ.jndi.000041", "{0}",  new Object[] { formater});
        if (LOG_LOCATION.beDebug()) {
          formater = "The caller stack trace for the RENAME operation follows.";
          LOG_LOCATION.traceThrowableT(Severity.DEBUG, formater, new Exception("Application [" + applicationName + "] performs a forbidden RENAME operation."));
        }
      }
    }
    super.rename(oldname, newname);
  }

  /**
   * Context rename
   *
   * @param oldname Old name
   * @param newname New name
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void rename(String oldname, String newname) throws javax.naming.NamingException {
    this.rename(parse(oldname), parse(newname));
  }

  /**
   * Context list
   *
   * @param name Name to list for
   * @return Enumeration requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration list(Name name) throws javax.naming.NamingException {
    name = this.cleanName(name);
    return super.list(name);
  }

  /**
   * Context list
   *
   * @param name Name to list for
   * @return Enumeration of bindings requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration list(String name) throws javax.naming.NamingException {
    return this.list(parse(name));
  }

  /**
   * Context listBindings
   *
   * @param name Name to list for
   * @return Enumeration of bindings requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration listBindings(Name name) throws javax.naming.NamingException {
    name = this.cleanName(name);
    return super.listBindings(name);
  }

  /**
   * Context listBindings
   *
   * @param name Name to list for
   * @return Enumeration of bindings requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration listBindings(String name) throws javax.naming.NamingException {
    return this.listBindings(parse(name));
  }

  /**
   * Context destroySubcontext
   *
   * @param name Name to destroy
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void destroySubcontext(Name name) throws javax.naming.NamingException {
    name = this.cleanName(name);
    if (runOnServer && usePrefix) { //if not force_remote and not domain = true
      if (LOG_LOCATION.beWarning()) {
        String formater = "Application [" + applicationName + "] with prefix [" + this.prefix + "] [" + this.usePrefix + "] performs a DESTROY_SUBCONTEXT operation, thus violating the J2EE Specification v.1.3/1.4, section 5.2.4. Change operations, performed in the application namespace are not allowed!";
        SimpleLogger.trace(Severity.WARNING,LOG_LOCATION, "ASJ.jndi.000038", "{0}",  new Object[] { formater});
        if (LOG_LOCATION.beDebug()) {
          formater = "The caller stack trace for the DESTROY_SUBCONTEXT operation follows.";
          LOG_LOCATION.traceThrowableT(Severity.DEBUG, formater, new Exception("Application [" + applicationName + "] performs a forbidden DESTROY_SUBCONTEXT operation."));
        }
      }
    }
    super.destroySubcontext(name);
  }

  /**
   * Context destroySubcontext
   *
   * @param name Name to destroy
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void destroySubcontext(String name) throws javax.naming.NamingException {
    this.destroySubcontext(parse(name));
  }

  /**
   * If the name is something like "/a/b/c/" change it to prefix/a/b/c
   *
   * @param name The name to be cleaned
   * @return prefix/clean_name if prefix is set to be used
   */
  public Name cleanName(Name name) throws javax.naming.NamingException {
    //removes last slashes if any (XXX///)
    transformName(name);
    //removes first slash if any and verifies for empty subname (/XX//X)
    verifyName(name);
    //add the prefix to the name
    if (!prefix.equals("")) {
      String[] parts = prefix.split("/");
      for (int i = parts.length - 1; i >= 0; i--) {
        name.add(0, parts[i]);
      }
    }
    return name;
  }

  /**
   * DirContext createSubcontext
   *
   * @param name Name to create
   * @param newattr Attributes to be rebound with
   * @return DirContext created
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public DirContext createSubcontext(Name name, Attributes newattr) throws javax.naming.NamingException {
    name = this.cleanName(name);
    if (runOnServer && usePrefix) { //if not force_remote and not domain = true
      if (LOG_LOCATION.beWarning()) {
        String formater = "Application [" + applicationName + "] with prefix [" + this.prefix + "] [" + this.usePrefix + "] performs a CREATE_SUBCONTEXT operation, thus violating the J2EE Specification v.1.3/1.4, section 5.2.4. Change operations, performed in the application namespace are not allowed!";
        SimpleLogger.trace(Severity.WARNING,LOG_LOCATION, "ASJ.jndi.000037", "{0}",  new Object[] { formater});
        if (LOG_LOCATION.beDebug()) {
          formater = "The caller stack trace for the CREATE_SUBCONTEXT operation follows.";
          LOG_LOCATION.traceThrowableT(Severity.DEBUG, formater, new Exception("Application [" + applicationName + "] performs a forbidden CREATE_SUBCONTEXT operation."));
        }
      }
    }
    ClientContext returnedContext = (ClientContext) super.createSubcontext(name, newattr);
    String originalPrefix = name.toString();
    DirContext result = null;
    if ((applicationName != null) && !("common:".equals(this.applicationName))) {
      originalPrefix = originalPrefix + ":" + this.applicationName.substring(0, this.applicationName.length() - 1);
    }

    //when usePrefix == false it must take the clientcontext's remoteContext
    //because prefix == "" and in lookup it cannot get the object with right name !

    if (this.usePrefix) {
      result = new OffsetClientContext(returnedContext.getEnvironment(), this.remoteContext, //here is the difference
          this.runOnServer, returnedContext.referenceFactory, originalPrefix, this.usePrefix, returnedContext.loginContext);
    } else {
      result = new OffsetClientContext(returnedContext.getEnvironment(), returnedContext.remoteContext, //here is the difference
          this.runOnServer, returnedContext.referenceFactory, originalPrefix, this.usePrefix, returnedContext.loginContext);
    }

    if (!usePrefix) {
      ((OffsetClientContext) result).fullNameInSpace = new CompositeName(returnedContext.getNameInNamespace());
    } else {
//      String fullName = returnedContext.getNameInNamespace();
//      fullName = fullName.substring((fullName.indexOf(this.prefix) + prefix.length() + 1));
      ((OffsetClientContext) result).root = this.root;
    }
    return result;
  }

  /**
   * DirContext createSubcontext
   *
   * @param name Name to create
   * @param newattr Attributes to be rebound with
   * @return DirContext created
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public DirContext createSubcontext(String name, Attributes newattr) throws javax.naming.NamingException {
    return createSubcontext(parse(name), newattr);
  }

  /**
   * Context createSubcontext
   *
   * @param name Name to create
   * @return Context created
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Context createSubcontext(Name name) throws javax.naming.NamingException {
    return createSubcontext(name, null);
  }

  /**
   * Context createSubcontext
   *
   * @param name Name to create
   * @return Context created
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Context createSubcontext(String name) throws javax.naming.NamingException {
    return createSubcontext(parse(name), null);
  }

  /**
   * Context lookupLink
   *
   * @param name Name to lookup link for
   * @return Link requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Object lookupLink(String name) throws javax.naming.NamingException {
    return this.lookup(parse(name));
  }

  /**
   * Context lookupLink
   *
   * @param name Name to lookup link for
   * @return Link requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Object lookupLink(Name name) throws javax.naming.NamingException {
    name = this.cleanName(name);
    return lookup(name);
  }

  /**
   * DirContext getAttributes
   *
   * @param name Name to get attributes for
   * @return Attributes requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Attributes getAttributes(Name name) throws javax.naming.NamingException {
    name = this.cleanName(name);
    return super.getAttributes(name);
  }

  /**
   * DirContext getAttributes
   *
   * @param name Name to get attributes for
   * @param attrIDs Attributes to return
   * @return Attributes requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Attributes getAttributes(Name name, String[] attrIDs) throws javax.naming.NamingException {
    name = this.cleanName(name);
    return super.getAttributes(name, attrIDs);
  }

  /**
   * DirContext getAttributes
   *
   * @param name Name to get attributes for
   * @return Attributes requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Attributes getAttributes(String name) throws javax.naming.NamingException {
    return this.getAttributes(parse(name));
  }

  /**
   * DirContext getAttributes
   *
   * @param name Name to get attributes for
   * @param attrIDs Attributes to return
   * @return Attributes requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Attributes getAttributes(String name, String[] attrIDs) throws javax.naming.NamingException {
    if (attrIDs == null) {
      return getAttributes(parse(name));
    }

    return this.getAttributes(parse(name), attrIDs);
  }

  /**
   * DirContext modifyAttributes
   *
   * @param name Name to modify attributes for
   * @param mod_op Code of the action to be taken
   * @param attrs Attributes to modify
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void modifyAttributes(Name name, int mod_op, Attributes attrs) throws javax.naming.NamingException {
    name = this.cleanName(name);
    if (runOnServer && usePrefix) { //if not force_remote and not domain = true
      if (LOG_LOCATION.beWarning()) {
        String formater = "Application [" + applicationName + "] with prefix [" + this.prefix + "] [" + this.usePrefix + "] performs a MODIFY_ATTRIBUTES operation, thus violating the J2EE Specification v.1.3/1.4, section 5.2.4. Change operations, performed in the application namespace are not allowed!";
        SimpleLogger.trace(Severity.WARNING,LOG_LOCATION, "ASJ.jndi.000039", "{0}",  new Object[] { formater});
        if (LOG_LOCATION.beDebug()) {
          formater = "The caller stack trace for the MODIFY_ATTRIBUTES operation follows.";
          LOG_LOCATION.traceThrowableT(Severity.DEBUG, formater, new Exception("Application [" + applicationName + "] performs a forbidden MODIFY_ATTRIBUTES operation."));
        }
      }
    }
    super.modifyAttributes(name, mod_op, attrs);
  }

  /**
   * DirContext modifyAttributes
   *
   * @param name Name to modify attributes for
   * @param mods Includes code of the operation and attributes to be changed
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void modifyAttributes(Name name, ModificationItem mods[]) throws javax.naming.NamingException {
    name = this.cleanName(name);
    super.modifyAttributes(name, mods);
  }

  /**
   * DirContext modifyAttributes
   *
   * @param name Name to modify attributes for
   * @param mod_op Code of the action to be taken
   * @param attrs Attributes to modify
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void modifyAttributes(String name, int mod_op, Attributes attrs) throws javax.naming.NamingException {
    this.modifyAttributes(parse(name), mod_op, attrs);
  }

  /**
   * DirContext modifyAttributes
   *
   * @param name Name to modify attributes for
   * @param mods Includes code of the operation and attributes to be changed
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public void modifyAttributes(String name, ModificationItem mods[]) throws javax.naming.NamingException {
    this.modifyAttributes(parse(name), mods);
  }

  /**
   * DirContext search
   *
   * @param name Name to begin search from
   * @param matchingAttributes Attributes to check match for
   * @return Enumeration containing the found items
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration search(Name name, Attributes matchingAttributes) throws javax.naming.NamingException {
    name = this.cleanName(name);
    return search(name, matchingAttributes, null);
  }

  /**
   * DirContext search
   *
   * @param name Name to begin search from
   * @param matchingAttributes Attributes to check match for
   * @return Enumeration containing the found items
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration search(String name, Attributes matchingAttributes) throws javax.naming.NamingException {
    return this.search(parse(name), matchingAttributes, null);
  }

  /**
   * DirContext search
   *
   * @param name Name to begin search from
   * @param matchingAttributes Attributes to check match for
   * @param attributesToReturn Attributes to be returned
   * @return Enumeration containing the found items
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws javax.naming.NamingException {
    name = this.cleanName(name);
    return super.search(name, matchingAttributes, attributesToReturn);
  }

  /**
   * DirContext search
   *
   * @param name Name to begin search from
   * @param matchingAttributes Attributes to check match for
   * @param attributesToReturn Attributes to be returned
   * @return Enumeration containing the found items
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration search(String name, Attributes matchingAttributes, String attributesToReturn[]) throws javax.naming.NamingException {
    return this.search(parse(name), matchingAttributes, attributesToReturn);
  }

  /**
   * DirContext search (rfc 2254 filter)
   *
   * @param name Name to begin search from
   * @param filterExpr Expression containing filter
   * @param filterArgs Arguments of the filter expression
   * @param cons Search controls to use
   * @return Enumeration containing the found items
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration search(String name, String filterExpr, Object filterArgs[], SearchControls cons) throws javax.naming.NamingException {
    return this.search(parse(name), filterExpr, filterArgs, cons);
  }

  /**
   * DirContext search (rfc 2254 filter)
   *
   * @param name Name to begin search from
   * @param filter Expression containing filter
   * @param cons Search controls to use
   * @return Enumeration containing the found items
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration search(String name, String filter, SearchControls cons) throws javax.naming.NamingException {
    return this.search(parse(name), filter, cons);
  }

  /**
   * DirContext search (rfc 2254 filter)
   *
   * @param name Name to begin search from
   * @param filterExpr Expression containing filter
   * @param filterArgs Arguments of the filter expression
   * @param cons Search controls to use
   * @return Enumeration containing the found items
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration search(Name name, String filterExpr, Object filterArgs[], SearchControls cons) throws javax.naming.NamingException {
    name = this.cleanName(name);
    return super.search(name, filterExpr, filterArgs, cons);
  }

  /**
   * DirContext search (rfc 2254 filter)
   *
   * @param name Name to begin search from
   * @param filter Expression containing filter
   * @param cons Search controls to use
   * @return Enumeration containing the found items
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NamingEnumeration search(Name name, String filter, SearchControls cons) throws javax.naming.NamingException {
    name = this.cleanName(name);
    return super.search(name, filter, cons);
  }

  /**
   * DirContext getSchemaClassDefinition (not implemented)
   *
   * @param name Name to use
   * @return DirContext
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public DirContext getSchemaClassDefinition(Name name) throws javax.naming.NamingException {
    throw new OperationNotSupportedException("Schemas addToEnvironment not supported in this version.");
  }

  /**
   * DirContext getSchemaClassDefinition (not implemented)
   *
   * @param name Name to use
   * @return DirContext
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public DirContext getSchemaClassDefinition(String name) throws javax.naming.NamingException {
    throw new OperationNotSupportedException("Schemas addToEnvironment not supported in this version.");
  }

  /**
   * DirContext getSchema (not implemented)
   *
   * @param name Name to use
   * @return DirContext
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public DirContext getSchema(Name name) throws javax.naming.NamingException {
    throw new OperationNotSupportedException("Schemas addToEnvironment not supported in this version.");
  }

  /**
   * DirContext getSchema (not implemented)
   *
   * @param name Name to use
   * @return DirContext
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public DirContext getSchema(String name) throws javax.naming.NamingException {
    throw new OperationNotSupportedException("Schemas addToEnvironment not supported in this version.");
  }

  /**
   * Context getNameParser
   *
   * @param name Name to use
   * @return NameParser requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NameParser getNameParser(String name) throws javax.naming.NamingException {
    return this.getNameParser(parse(name));
  }

  /**
   * Context getNameParser
   *
   * @param name Name to use
   * @return NameParser requested
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public NameParser getNameParser(Name name) throws javax.naming.NamingException {
    name = this.cleanName(name);
    return super.getNameParser(name);
  }

  /**
   * Context composeName
   *
   * @param name Name to use
   * @return Requested name
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public String composeName(String name, String prefix) throws javax.naming.NamingException {
    return this.composeName(new CompositeName(name), new CompositeName(prefix)).toString();
  }

  /**
   * Context composeName
   *
   * @param name Name to use
   * @return Requested name
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Name composeName(Name name, Name prefix) throws javax.naming.NamingException {
    this.transformName(name);
    this.verifyName(name);
    return cleanName(prefix).addAll(name);
  }

  /**
   * Context addToEnvironment
   *
   * @param propName Name of the property
   * @param propVal Value or the property
   * @return The resulting environment
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Object addToEnvironment(String propName, Object propVal) throws javax.naming.NamingException {
    return super.addToEnvironment(propName, propVal);
  }

  /**
   * Context removeFromEnvironment
   *
   * @param propName Name of the property
   * @return The resulting environment
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Object removeFromEnvironment(String propName) throws javax.naming.NamingException {
    return super.removeFromEnvironment(propName);
  }

  /**
   * Context getEnvironment
   *
   * @return The environment
   * @throws javax.naming.NamingException Thrown if a problem occurs.
   */
  public Hashtable getEnvironment() throws javax.naming.NamingException {
    return super.getEnvironment();
  }

  /**
   * Closes RMI Contexts
   */
  public void close() throws javax.naming.NamingException {
    super.close();
  }

  /**
   * Context getNameInNameSpace
   *
   * @return Requested name
   */
  public String getNameInNamespace() throws javax.naming.NamingException {
    String name = "";
    if (usePrefix) {
      if (!root.equals(prefix)) {
        name = prefix.substring(root.length() + 1);
      }
    } else {
      name = super.getNameInNamespace();
    }
    return name;
  }

  /**
   * Gets the client
   *
   * @return Requested client
   */
  public ClientContext getClient() {
    return this;
  }

}