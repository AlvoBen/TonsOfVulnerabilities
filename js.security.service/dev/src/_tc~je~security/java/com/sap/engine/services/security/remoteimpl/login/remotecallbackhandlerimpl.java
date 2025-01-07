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

import com.sap.engine.services.security.remote.login.*;
import com.sap.engine.services.security.remote.login.SerializableNameCallback;
import com.sap.engine.services.security.remote.login.SerializableGetterCallback;
import com.sap.engine.services.rmi_p4.P4RemoteObject;
import com.sap.engine.lib.security.PasswordChangeCallback;
import com.sap.engine.lib.security.http.HttpGetterCallback;
import com.sap.engine.lib.security.http.HttpSetterCallback;

import javax.security.auth.callback.*;
import java.rmi.RemoteException;
import java.io.IOException;
import java.io.Serializable;

/**
 *  
 *
 * @author Stephan Zlatarev
 * @version 6.30  
 */
public class RemoteCallbackHandlerImpl extends P4RemoteObject implements RemoteCallbackHandler {

  CallbackHandler handler = null;

  public RemoteCallbackHandlerImpl(CallbackHandler handler) throws RemoteException {
    this.handler = handler;
  }

  public Object handle(Object acallback) throws IOException, UnsupportedCallbackException {
    Callback converted = convert(acallback);

    try {
      handler.handle(new Callback[] { converted });
    } catch (UnsupportedCallbackException uce) {
      throw new UnsupportedCallbackException(null, "Handler " + handler + " does not support callback " + acallback);
    }

    fillResponse(converted, acallback);
    return acallback;
  }

  public Object[] handle(Object[] callbacks) throws IOException, UnsupportedCallbackException, RemoteException {
    Callback[] converted = new Callback[callbacks.length];
    for (int i = 0; i < converted.length; i++) {
      converted[i] = convert(callbacks[i]);
    }

    try {
      handler.handle(converted);
    } catch (UnsupportedCallbackException uce) {
      throw new UnsupportedCallbackException(null, "Handler " + handler + " does not support a callback. " + uce.getMessage());
    }

    for (int i = 0; i < converted.length; i++) {
      fillResponse(converted[i], callbacks[i]);
    }

    return callbacks;
  }

  private Callback convert(Object acallback) {
    Callback result = null;

    if (acallback instanceof Callback) {
      result = (Callback) acallback;
    } else if (acallback instanceof SerializableNameCallback) {
      SerializableNameCallback serializable = (SerializableNameCallback) acallback;
      if (serializable.getDefaultName() != null) {
        result = new NameCallback(serializable.getPrompt(), serializable.getDefaultName());
      } else {
        result = new NameCallback(serializable.getPrompt());
      }
    } else if (acallback instanceof SerializablePasswordChangeCallback) {
      SerializablePasswordChangeCallback serializable = (SerializablePasswordChangeCallback) acallback;
      result = new PasswordChangeCallback(serializable.getPrompt(), serializable.isEchoOn());
    } else if (acallback instanceof SerializablePasswordCallback) {
      SerializablePasswordCallback serializable = (SerializablePasswordCallback) acallback;
      result = new PasswordCallback(serializable.getPrompt(), serializable.isEchoOn());
    } else if (acallback instanceof SerializableGetterCallback) {
      SerializableGetterCallback serializable = (SerializableGetterCallback) acallback;

      result = new HttpGetterCallback();
      ((HttpGetterCallback) result).setType(serializable.getType());
      ((HttpGetterCallback) result).setName(serializable.getName());
    } else if (acallback instanceof SerializableSetterCallback) {
      SerializableSetterCallback serializable = (SerializableSetterCallback) acallback;

      result = new HttpSetterCallback();
      ((HttpSetterCallback) result).setType(serializable.getType());
      ((HttpSetterCallback) result).setName(serializable.getName());
      ((HttpSetterCallback) result).setValue(serializable.getValue());
    }

    return result;
  }

  private void fillResponse(Callback callback, Object transport) throws UnsupportedCallbackException {
    if (transport instanceof Callback) {
      // response is already there
    } else if (transport instanceof SerializableNameCallback) {
      SerializableNameCallback serializable = (SerializableNameCallback) transport;
      serializable.setName(((NameCallback) callback).getName());
    } else if (transport instanceof SerializablePasswordChangeCallback) {
      SerializablePasswordChangeCallback serializable = (SerializablePasswordChangeCallback) transport;
      serializable.setPassword(((PasswordChangeCallback) callback).getPassword());
    } else if (transport instanceof SerializablePasswordCallback) {
      SerializablePasswordCallback serializable = (SerializablePasswordCallback) transport;
      serializable.setPassword(((PasswordCallback) callback).getPassword());
    } else if (transport instanceof SerializableGetterCallback) {
      SerializableGetterCallback serializable = (SerializableGetterCallback) transport;
      Object value = ((HttpGetterCallback) callback).getValue();

      if (value instanceof Serializable) {
        serializable.setValue((Serializable) value);
      } else if (value != null) {
        throw new UnsupportedCallbackException(callback, "Value " + value.getClass().getName() + " is not serializable.");
      }
    } else if (transport instanceof SerializableSetterCallback) {
      // setter callback has no response
    }
  }
}