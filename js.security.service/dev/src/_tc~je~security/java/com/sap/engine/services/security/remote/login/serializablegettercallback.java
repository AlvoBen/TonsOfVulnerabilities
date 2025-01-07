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
package com.sap.engine.services.security.remote.login;

import com.sap.engine.lib.security.http.HttpGetterCallback;

import java.io.Serializable;

/**
 * A serializable version of HttpGetterCallback.
 *
 * @see com.sap.engine.lib.security.http.HttpGetterCallback
 *
 * @author Svetlana Stancheva
 * @version 6.30
 */
public class SerializableGetterCallback implements Serializable {

  static final long serialVersionUID = -6009898390662396101L;

  private byte type;
  private String name;
  private Serializable value;

  /**
   *  Construct a serializable version of an existing HttpGetterCallback.
   *
   * @param callback  the original callback
   */
  public SerializableGetterCallback(HttpGetterCallback callback) {
    this.type = callback.getType();
    this.name = callback.getName();
  }

  /**
   *  Returns the type of the credential.
   *
   * @return  the type of the credential.
   */
  public byte getType() {
    return type;
  }

  /**
   * Returns the name of the credential provided by the callback handler.
   *
   * @return a string containing the name of the credential.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the value of the credential provided by the callback handler.
   *
   * @return an object containing value of the credential.
   */
  public Serializable getValue() {
    return value;
  }

  /**
   *  Callback handlers use this method to set the type of the credential.
   *
   * @param type  the type of the credential.
   */
  public void setType(byte type) {
    this.type = type;
  }

  /**
   *  Callback handlers use this method to set the name of the credential.
   *
   * @param name  the name of the credential.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   *  Callback handlers use this method to set the value of the credential.
   *
   * @param value  the value of the credential. It must be serializable.
   */
  public void setValue(Serializable value) {
    this.value = value;
  }

}
