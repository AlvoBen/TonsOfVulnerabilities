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

import java.io.Serializable;

import com.sap.engine.lib.security.PasswordChangeCallback;

/**
 * A serializable version of JAAS PasswordChangeCallback.
 *
 * @see com.sap.engine.lib.security.PasswordChangeCallback
 *
 * @author Stephan Zlatarev
 * @version 6.30
 */
public class SerializablePasswordChangeCallback implements Serializable {

  static final long serialVersionUID = 6888493003580770289L;

  boolean isEchoOn;
  char[] password;
  String prompt;

  /**
   *  Construct a serializable version of an existing PasswordChangeCallback.
   *
   * @param callback  the original callback
   */
  public SerializablePasswordChangeCallback(PasswordChangeCallback callback) {
    isEchoOn = callback.isEchoOn();
    prompt = callback.getPrompt();
    setPassword(callback.getPassword());
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
   *  Returns echo flag for the callback.
   *
   * @return echo flag.
   */
  public boolean isEchoOn() {
    return isEchoOn;
  }

  /**
   *  Callback handlers use this method to set the password of the user to be authenticated.
   *
   * @param chars a character array containing the password.
   */
  public void setPassword(char[] chars) {
    password = (chars != null) ? mask(chars) : null;
  }

  /**
   *  Returns the password provided by the callback handler.
   *
   * @return  a character array containing the password.
   */
  public char[] getPassword() {
    return (password != null) ? unmask(password) : null;
  }

  /**
   *  Clears the field containing the password in the callback.
   */
  public void clearPassword() {
    password = null;
  }

  private final static char[] mask(char[] data) {
    char mask = (char) 0xAAAA;
    char check = (char) 0x5555;
    char[] result = new char[data.length + 1];

    for (int i = 0; i < data.length; i++) {
      mask ^= data[i];
      result[i] = mask;
    }
    result[data.length] = (char) (mask ^ check);

    return result;
  }

  private final static char[] unmask(char[] data) {
    if (data.length == 0) {
      return data;
    }

    char mask = (char) 0xAAAA;
    char check = (char) 0x5555;
    char[] result = new char[data.length - 1];

    for (int i = 0; i < result.length; i++) {
      result[i] = (char) (mask ^ data[i]);
      mask = data[i];
    }

    if (data[result.length] == (char) (mask ^ check)) {
      return result;
    } else {
      return data;
    }
  }

}
