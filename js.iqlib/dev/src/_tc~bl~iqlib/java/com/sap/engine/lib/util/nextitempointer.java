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
package com.sap.engine.lib.util;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sap.engine.lib.util.base.NextItemAdapter;
import com.sap.engine.lib.util.base.Pointer;

/**
 * Implementation of Pointer to NextItem.<p>
 *
 * @author Andrei Gatev
 * @version 1.0
 */
public class NextItemPointer extends NextItemAdapter implements Pointer { //$JL-CLONE$

  static final long serialVersionUID = 8627849927633052239L;

  protected Object value;

  public NextItemPointer() {

  }

  public NextItemPointer(Object value) {
    this.value = value;
  }

  public String toString() {
    return value.toString();
  }

  public boolean equals(Object object) {
    if (!(object instanceof NextItemPointer)) {
      return false;
    }

    return value.equals(((NextItemPointer) object).value);
  }
  
  public int hashCode() {
    if (value == null) {
      return 0;
    }
    return value.hashCode();
  }

  public Object getElement() {
    return value;
  }

  public Object setElement(Object newValue) {
    if (newValue == null) {
      throw new NullPointerException();
    }

    Object oldValue = value;
    value = newValue;
    return oldValue;
  }
  
  private void writeObject(ObjectOutputStream oos) throws NotSerializableException {
	  try {
	    oos.defaultWriteObject();
	  } catch (IOException ioex) {
	    throw new NotSerializableException("Cannot serialize class " + this.getClass().getName() + ". Error is " + ioex.toString());
	  }
	}
	
	private void readObject(ObjectInputStream oos) throws NotSerializableException {
	    try {
	    oos.defaultReadObject();
	  } catch (IOException ioex) {
	    throw new NotSerializableException("Cannot deserialize class " + this.getClass().getName() + ". Error is " + ioex.toString());
	  } catch (ClassNotFoundException cnfe) {
	    throw new NotSerializableException("Cannot deserialize class " + this.getClass().getName() + ". Error is " + cnfe.toString());
	  }
	    
	}

}

