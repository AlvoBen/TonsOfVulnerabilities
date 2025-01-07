/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.session.usr;

import com.sap.engine.session.callback.Callback;
import com.sap.engine.session.callback.CallbackHandler;
import com.sap.engine.session.callback.DuplicateNameException;

import com.sap.engine.session.data.share.HashtableImpl;
import com.sap.engine.session.data.share.QueueImpl;
import com.sap.engine.session.scope.Scope;
import java.util.WeakHashMap;

/**
 * Author: georgi-s, Nikolai Neichev
 * Date: 2005-3-21
 */
public abstract class UserContext extends SessionUserContext {
  private WeakHashMap<Object, QueueImpl> queuesMap;
  private WeakHashMap<Object, HashtableImpl> hashtablesMap;

  private transient String rootContextID;
  
  
  
  private WeakHashMap<Object, HashtableImpl> getHashtablesMap() {
    if (hashtablesMap == null) {
    	synchronized (this) {
    		if (hashtablesMap == null) {
    			hashtablesMap = new WeakHashMap<Object, HashtableImpl>();
    		}
    	}
    }
    return hashtablesMap;
  }

  private WeakHashMap<Object, QueueImpl> getQueuesMap() {
    if (queuesMap == null) {
    	synchronized (this) {
    		if (queuesMap == null) {
    			queuesMap = new WeakHashMap<Object, QueueImpl>();
    		}
    	}
    }
    return queuesMap;
  }

  protected synchronized static void setDelegate(UserContextDelegate impl) {
    if (SessionUserContext.delegate == DummyUserContextDelegate.dummyImpl) {
      SessionUserContext.delegate = impl;
    }
  }

  public QueueImpl getQueue(Object key) {
    return getQueuesMap().get(key);
  }

  public void putQueue(Object key, QueueImpl impl) {
    getQueuesMap().put(key, impl);
  }

  public QueueImpl removeQueue(Object key) {
    return getQueuesMap().remove(key);
  }

  public HashtableImpl getHashtable(Object key) {
    return getHashtablesMap().get(key);
  }

  public void putHashtable(Object key, HashtableImpl impl) {
    getHashtablesMap().put(key, impl);
  }

  public HashtableImpl removeHashtable(Object key) {
    return getHashtablesMap().remove(key);
  }

  public WeakHashMap<Object, HashtableImpl> getHashtables() {
    return getHashtablesMap();
  }

  public WeakHashMap<Object, QueueImpl> getQueues() {
    return getQueuesMap();
  }

  public abstract LoginSession getLoginSession();

  public LoginSessionInterface loginSession() {
    return null;
  }

  public String getUser() {
    return (loginSession().getPrincipal() == null) ? "null" : loginSession().getPrincipal().getName();
  }


  public abstract void addCallbackLocal(Callback callback) throws UserContextException;

  public abstract void addLocalAttribute(String key, Object value) throws UserContextException;

  protected abstract void destroy() throws UserContextException;


  public static UserContext getCurrentUserContext() {
    return (UserContext) SessionUserContext.getSessionUserContext();
  }

  public static void addCallbackHandler(CallbackHandler handler) throws DuplicateNameException {
    synchronized (handlers) {
      if (handlers.containsKey(handler.handlerName())) {
        throw new DuplicateNameException("The CallbackHandler " + handler.handlerName() + " already exist.");
      }
      handlers.put(handler.handlerName(), handler);
    }
  }

  public static CallbackHandler getCallbackHandler(String name) {
    return handlers.get(name);
  }

  public static void removeCallbackHandler(CallbackHandler handler) {
    synchronized (handlers) {
      handlers.remove(handler.handlerName());
    }
  }


  public static UserContextAccessor getAccessor() throws SecurityException {
    if (delegate != null) {
      return delegate.getUserContextAccessor();
    } else {
      return null;
//      throw new AssertionError("UserContextAccessor object is not available outside of the server.");
    }
  }

  public Scope getCurrentScope() {
    return null;
  }

  public String getRootContextID() {
    return rootContextID;
  }

  public void setRootContextID(String rootContextID) {
    this.rootContextID = rootContextID;
  }


}