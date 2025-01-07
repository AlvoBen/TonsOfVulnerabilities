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

import javax.security.auth.callback.NameCallback;
import java.io.Serializable;

/**
 * A serializable version of JAAS NameCallback.
 *
 * @see javax.security.auth.callback.NameCallback
 *
 * @author Stephan Zlatarev
 * @version 6.30  
 */
public class SerializableNameCallback implements Serializable {

  static final long serialVersionUID = -2218830809302412602L;

  String defaultName;
  String name;
  String prompt;

  /**
   *  Construct a serializable version of an existing NameCallback.
   *
   * @param callback  the original callback
   */
  public SerializableNameCallback(NameCallback callback) {
    defaultName = callback.getDefaultName();
    name = callback.getName();
    prompt = callback.getPrompt();
  }

  /**
   *  Returns the prompting message for the name to be entered.
   *
   * @return  a printable string to be displayed to the user.
   */
  public String getPrompt() {
    return prompt;
  }

  /**
   * Returns the default name for this callback.
   *
   * @return a printable string containing the default name.
   */
  public String getDefaultName() {
    return defaultName;
  }

  /**
   * Returns the name provided by the callback handler.
   *
   * @return a printable string containing the name to be authenticated.
   */
  public String getName() {
    return name;
  }

  /**
   *  Callback handlers use this method to set the name to be authenticated.
   *
   * @param name a printable string.
   */
  public void setName(String name) {
    this.name = name;
  }

}