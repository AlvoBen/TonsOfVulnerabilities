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

import com.sap.engine.lib.util.base.LinearItemAdapter;
import com.sap.engine.lib.util.base.Pointer;

/**
 * Implementation of Pointer to LinearItem.
 *
 * @author Andrei Gatev
 * @version 1.0
 */
public class LinearItemPointer extends LinearItemAdapter implements Pointer { //$JL-CLONE$
  
  static final long serialVersionUID = -622495233584702976L;
  protected Object value;

  public LinearItemPointer() {

  }

  public LinearItemPointer(Object value) {
    this.value = value;
  }

  public String toString() {
    return value.toString();
  }

  public boolean equals(Object o) {
    if (!(o instanceof LinearItemPointer)) {
      return false;
    }

    return value.equals(((LinearItemPointer) o).value);
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

