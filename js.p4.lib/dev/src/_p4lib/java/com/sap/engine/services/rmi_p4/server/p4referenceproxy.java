/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rmi_p4.server;

import java.lang.ref.WeakReference;

/**
 * Public class P4ReferenceProxy is the used for handling
 * object references for P4 remote protocol
 *
 * @author Georgy Stanev
 * @version 7.0
 */
public class P4ReferenceProxy {

  /**
   * A WeakReference instance to be registered
   */
  private WeakReference reference;
  private int hash = 0;

  /**
   * Constructor for the P4ReferenceProxy
   *
   * @param referent - the referent to which field reference is mapped
   */
  public P4ReferenceProxy(Object referent) {
    reference = new WeakReference(referent);
  }

  /**
   * This method ovrrides method java.lang.Object.equals(java.lang.Object)
   *
   * @param object - the object to be checked for equality with the referent
   * @return true if the referent is not null and equals the parameter and false otherwise
   */
  public boolean equals(Object object) {
    boolean return_value;
    Object referent = reference.get();

    if (referent != null) {
      return_value = referent.equals(object);
    } else {
      return_value = super.equals(object);
    }

    return return_value;
  }

  /**
   * This method ovrrides method java.lang.Object.hashCode()
   *
   * @return the hash code of the referent if it is not null and -1 otherwise
   */
  public int hashCode() {
    Object referent = reference.get();

    if (hash == 0) {
      hash = referent.hashCode();
    }

    return hash;
  }

  public Object get() {
    return reference.get();
  }

}

