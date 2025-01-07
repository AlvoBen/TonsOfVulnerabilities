/**
 * Copyright (c) 2002 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.engine.services.security.remoteimpl.login;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.rmi.PortableRemoteObject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import com.sap.engine.interfaces.security.SecurityContext;
import com.sap.engine.interfaces.security.SecuritySession;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.engine.lib.security.PasswordChangeCallback;
import com.sap.engine.lib.security.ReusableLoginContext;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.lib.security.http.HttpSetterCallback;
import com.sap.engine.services.security.remote.login.RemoteCallbackHandler;
import com.sap.engine.services.security.remote.login.RemoteLoginContextHelper;
import com.sap.engine.services.security.remote.login.SerializableGetterCallback;
import com.sap.engine.services.security.remote.login.SerializableNameCallback;
import com.sap.engine.services.security.remote.login.SerializablePasswordCallback;
import com.sap.engine.services.security.remote.login.SerializablePasswordChangeCallback;
import com.sap.engine.services.security.remote.login.SerializableSetterCallback;
import com.sap.engine.services.security.SecurityServerFrame;

/**
 *
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class RemoteLoginContextHelperImpl extends PortableRemoteObject implements CallbackHandler, RemoteLoginContextHelper {
  
  private SecurityContext security = null;

  LoginContext login = null;
  RemoteCallbackHandler handler = null;
  SecuritySession session = null;
  Object[] prepared = null;

  public RemoteLoginContextHelperImpl(SecurityContext security) throws RemoteException {
    this(security, null);
  }

  public RemoteLoginContextHelperImpl(SecurityContext security, Object[] preparedCredentials) throws RemoteException {
    this.security = security;
    this.prepared = preparedCredentials;
  }

  public void login(RemoteCallbackHandler calbbackHandler) throws LoginException, RemoteException {
    this.handler = calbbackHandler;
    this.login = security.getAuthenticationContext().getLoginContext(null, this);
    ThreadContext currentThreadContext = SecurityServerFrame.threadContext.getThreadContext();
    
    if (currentThreadContext != null) {
      com.sap.engine.services.security.login.SecurityContext object = (com.sap.engine.services.security.login.SecurityContext) 
        currentThreadContext.getContextObject(com.sap.engine.services.security.login.SecurityContext.NAME);
      
      if (object != null && !object.isClientAnonymous()) {
        object.emptySessionContextObjects();
      }
    }

    login.login();
  }

  public void logout() throws LoginException, RemoteException {
    login.logout();

    if (login instanceof ReusableLoginContext) {
      ((ReusableLoginContext) login).close();
    }
  }

  public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    if ((handler == null) && (prepared == null)) {
      throw new IllegalStateException("Use newLoginContext() before login().");
    }

    Object[] handled = getPreparedOrHandle(callbacks);

    for (int i = 0; i < callbacks.length; i++) {
      if (callbacks[i] instanceof NameCallback) {
        ((NameCallback) callbacks[i]).setName(((SerializableNameCallback) handled[i]).getName());
      } else if (callbacks[i] instanceof PasswordChangeCallback) {
        ((PasswordChangeCallback) callbacks[i]).setPassword(((SerializablePasswordChangeCallback) handled[i]).getPassword());
      } else if (callbacks[i] instanceof PasswordCallback) {
        ((PasswordCallback) callbacks[i]).setPassword(((SerializablePasswordCallback) handled[i]).getPassword());
      } else if (callbacks[i] instanceof HttpGetterCallback) {
        ((HttpGetterCallback) callbacks[i]).setValue(((SerializableGetterCallback) handled[i]).getValue());
      }
    }
    
  }

  private final Object[] getPreparedOrHandle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    Object[] result = new Object[callbacks.length];
    int notPrepared = 0;

    for (int i = 0; i < callbacks.length; i++) {
      result[i] = getPreparedCallback(callbacks[i]);

      if (result[i] == null) {
        // there is no prepared callback
        notPrepared++;
      }
    }

    if (notPrepared > 0) {
      if (handler == null) {
        for (int i = 0; i < result.length; i++) {
          if (result[i] == null) {
            throw new UnsupportedCallbackException(callbacks[i], "Unsupported type of callback.");
          }
        }
      }

      Object[] transport = new Object[notPrepared];

      for (int i = 0, t = 0; i < result.length; i++) {
        if (result[i] == null) {
          transport[t++] = getSerializedVersion(callbacks[i]);
        }
      }

      try {
        transport = handler.handle(transport);
      } catch (RuntimeException e) {
        // backward compatibility to 6.40. client jar from 6.40 can call later versions
        for (int i = 0; i < transport.length; i++) {
          transport[i] = handler.handle(transport[i]);
        }
      }

      for (int i = 0, t = 0; i < result.length; i++) {
        if (result[i] == null) {
          result[i] = transport[t++];
        }
        if (result[i] == null) {
          throw new UnsupportedCallbackException(callbacks[i], "Unsupported type of callback.");
        }
      }
    }

    for (int i = 0; i < result.length; i++) {
      if (result[i] == null) {
        throw new UnsupportedCallbackException(callbacks[i], "Unsupported type of callback.");
      }
    }

    return result;
  }

  private final Object getPreparedCallback(Callback callback) throws IOException, UnsupportedCallbackException {
    if (prepared != null) {
      Class searchClass = callback.getClass();

      if (callback instanceof NameCallback) {
        searchClass = SerializableNameCallback.class;
      } else if (callback instanceof PasswordChangeCallback) {
        searchClass = SerializablePasswordChangeCallback.class;
      } else if (callback instanceof PasswordCallback) {
        searchClass = SerializablePasswordCallback.class;
      } else if (callback instanceof HttpGetterCallback) {
        searchClass = HttpGetterCallback.class;
        // todo: match for the criteria
      } else if (callback instanceof HttpSetterCallback) {
        searchClass = HttpSetterCallback.class;
        // todo: match for the criteria
      }

      if (searchClass != null) {
        for (int i = 0; i < prepared.length; i++) {
          if (searchClass.isInstance(prepared[i])) {
            return prepared[i];
          }
        }
      }
    }

    return null;
  }

  private final Object getSerializedVersion(Callback callback) throws UnsupportedCallbackException {
    if (callback instanceof Remote) {
      return callback;
    } else if (callback instanceof NameCallback) {
      return new SerializableNameCallback((NameCallback) callback);
    } else if (callback instanceof PasswordChangeCallback) {
      return new SerializablePasswordChangeCallback((PasswordChangeCallback) callback);
    } else if (callback instanceof PasswordCallback) {
      return new SerializablePasswordCallback((PasswordCallback) callback);
    } else if (callback instanceof HttpGetterCallback) {
      return new SerializableGetterCallback((HttpGetterCallback) callback);
    } else if (callback instanceof HttpSetterCallback) {
      HttpSetterCallback setterCallback = (HttpSetterCallback) callback;

      if (setterCallback.getValue() instanceof Serializable) {
        return new SerializableSetterCallback(setterCallback);
      } else {
        throw new UnsupportedCallbackException(callback, "Value " + setterCallback.getValue() + " is not serializable.");
      }
    } else {
      throw new UnsupportedCallbackException(callback, "Unsupported type of callback.");
    }
  }

}
