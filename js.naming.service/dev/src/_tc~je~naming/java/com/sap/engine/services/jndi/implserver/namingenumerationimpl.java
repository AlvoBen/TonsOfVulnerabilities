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
package com.sap.engine.services.jndi.implserver;

import java.util.*;
import javax.naming.*;
import java.util.NoSuchElementException;

import com.sap.engine.lib.util.ConcurrentArrayObject;

//import com.inqmy.frame.container.log.LogContext;
/**
 * Implements the enumeration of the naming
 *
 * @author Petio Petev
 * @version 4.00
 */
public class NamingEnumerationImpl implements NamingEnumeration {

  /**
   * Elements of the enumeration
   */
  private ConcurrentArrayObject elements;
  /**
   * Used for counting the number of elements
   */
  private int counter = 0;
  /**
   * Stores enumeration
   */
  private Enumeration enumeration;
  /**
   * Flags if enumeration passed
   */
  private boolean isEnum;

  /**
   * Constructor - creates empty Enumeration
   */
  public NamingEnumerationImpl() {
    this.elements = new ConcurrentArrayObject();
    isEnum = false;
  }

  /**
   * Constructor - creates Enumeration from Vector
   *
   * @param vector ConcurrentArrayObject to use
   */
  public NamingEnumerationImpl(ConcurrentArrayObject vector) {
    this.elements = vector;
    isEnum = false;
  }

  /**
   * Constructor - creates Enumeration from Enumeration
   *
   * @param enumeration Enumeraton to use
   */
  public NamingEnumerationImpl(Enumeration enumeration) {
    this.enumeration = enumeration;
    isEnum = true;
  }

  /**
   * Returns the next element in the enumeration
   *
   * @return The next element
   * @throws NamingException Thrown if a problem occures
   */
  public Object next() throws NamingException {
    return nextElement();
  }

  /**
   * Determines if the enumeration has more elements
   *
   * @return "true" if there is more elements
   * @throws NamingException Thrown if a problem occures
   */
  public boolean hasMore() throws NamingException {
    return hasMoreElements();
  }

  /**
   * Returns the next element in the enumeration
   *
   * @return The next element
   */
  public Object nextElement() {
    if (isEnum) {
      if (enumeration != null) {
        return enumeration.nextElement();
      }
    } else if (counter < elements.size()) {
      return elements.elementAt(counter++);
    }
    throw new NoSuchElementException("There are no more elements in the naming enumeration.");
  }

  /**
   * Determines if the enumeration has more elements
   *
   * @return "true" if there is more elements
   */
  public boolean hasMoreElements() {
    return (isEnum) ? (enumeration != null && enumeration.hasMoreElements()) : counter < elements.size();
  }

  /**
   * Closes the enumeration
   */
  public void close() {
    if (isEnum) {
      enumeration = null;
    } else {
      elements.removeAllElements();
      counter = 0;
    }
  }

}

