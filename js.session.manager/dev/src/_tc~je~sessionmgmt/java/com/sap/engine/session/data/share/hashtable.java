package com.sap.engine.session.data.share;

import com.sap.engine.core.Names;
import com.sap.engine.session.data.share.exceptions.*;
import com.sap.engine.session.usr.UserContext;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.Set;
import java.util.Collection;


public class Hashtable {

  private static Location loc = Location.getLocation(Hashtable.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  private String className = null;
  private int threshold = -1;
  private long timeout = -1;
  private Object ref = null;
  private String loaderName = null;

  public Hashtable(String className, String loaderName, int threshold) {
    this(className, loaderName, threshold, -1);
  }

  public Hashtable(String className, String loaderName, int threshold, long timeout) {
    this.className = className;
    this.threshold = threshold;
    this.timeout = timeout;
    this.ref = new Object();
    this.loaderName = loaderName;
  }

  private HashtableImpl getImpl() throws NullUserContextException {
    HashtableImpl impl;

    UserContext userContext = UserContext.getCurrentUserContext();

    if(loc.beInfo()){
      String msg = "Current User Context : " + userContext;
      loc.logT(Severity.INFO, msg);
    }

    if (userContext != null) {
      impl = userContext.getHashtable(this.ref);

      if(loc.beInfo()){
        String msg;
        if(impl == null){
          msg = "For the current UserContext there is no Hashtable for className<" + className + ">";
        } else {
          msg = "For the current UserContext there is a Hashtable <" + impl + "> for className<" + className + ">";
        }
        loc.logT(Severity.INFO, msg);
      }

      if (impl == null) {
        impl = new HashtableImpl(className, loaderName, threshold, timeout);

        if(loc.beInfo()){
          String msg = "Currently there is no hashtable and now is registered<" + impl + "> for className<" + className + ">";
          loc.logT(Severity.INFO, msg);
        }

        userContext.putHashtable(this.ref, impl);
      }
    } else {
      throw new NullUserContextException("The current user context is null!");
    }

    return impl;
  }

  protected void removeImpl() {
    ref = null;
  }

  /**
   * Add new element in the hashtable. If the number of elements becomes greater than the threshold
   * the TooManyElementsException is thrown.
   * If the element class doesn't matches to the Hashtable class type ClassNotAcceptable is thrown
   * (it could happen also if the  element's class is loaded by the different classloader than the classloader
   * of the Class given as parameter to createHashtable(Class) HashtableFactory method)
   *
   * @param key the object key
   * @param value the value
   * @throws TooManyElementsException if the elements limit count is reached
   * @throws ClassNotAcceptableException if the class is not acceptable
   * @throws NullUserContextException if the user context is null
   */
  public synchronized void put(String key, Object value) throws NullUserContextException, TooManyElementsException, ClassNotAcceptableException {
    
    if (key.equals("DSR_ROOT_ID")) {
      UserContext userContext = UserContext.getCurrentUserContext();
  
      if(loc.beInfo()){
        String msg = "Current User Context : " + userContext;
        loc.logT(Severity.INFO, msg);
      }
  
      if (userContext != null) {
        userContext.setRootContextID((String)value);
      }
    }
    getImpl().put(key, value);
  }

  /**
   * Returns the element associated with this name or returns null if there is no such key.
   *
   * @param key the object key
   * @return value the value
   * @throws NullUserContextException if the user context is null
   */
  public synchronized Object get(String key) throws NullUserContextException {
    return getImpl().get(key);
  }

  /**
   * Removes the element associated with this name
   *
   * @param key the object key
   * @throws NullUserContextException if the user context is null
   * @return the removed object
   */
  public synchronized Object remove(String key) throws NullUserContextException {
    return getImpl().remove(key);
  }


  /**
   * Returns the number of the elements
   * @throws NullUserContextException if the user context is null
   * @return the size of the table
   */
  public synchronized int size() throws NullUserContextException {
    return getImpl().size();
  }

  /**
   * Returns the set of keys
   *
   * @return value
   * @throws NullUserContextException if the user context is null
   */
  public synchronized Set keys() throws NullUserContextException {
    return getImpl().keys();
  }

  /**
   * Returns the set of values
   *
   * @return value
   * @throws NullUserContextException if the user context is null
   */
  public synchronized Collection values() throws NullUserContextException {
    return getImpl().values();
  }


}
