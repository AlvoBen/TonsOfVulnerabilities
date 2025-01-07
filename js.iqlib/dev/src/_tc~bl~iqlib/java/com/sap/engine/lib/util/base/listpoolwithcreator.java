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
package com.sap.engine.lib.util.base;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Implemetation of abstract class ListPool.<p>
 *
 * @author Nikola Arnaudov
 * @version 1.00
 */
public class ListPoolWithCreator extends ListPool { //$JL-CLONE$
  static final long serialVersionUID = 2817422856450644069L;
  protected ListPoolInstanceCreator creator;

  /**
   * Constructs empty pool without size limit.<p>
   *
   * @param   creator for new instances.
   */
  public ListPoolWithCreator(ListPoolInstanceCreator creator) {
    this.creator = creator;
  }

  /**
   * Constructs pool without size limit and specified initial size.<p>
   *
   * @param   creator for new instances.
   * @param  initialSize initial size of the pool.
   */
  public ListPoolWithCreator(ListPoolInstanceCreator creator, int initialSize) {
    this(creator, initialSize, 0);
  }

  /**
   * Constructs pool with size limit and specified initial size.<p>
   *
   * @param   creator for new instances.
   * @param   limit max size of pool. If value is not positive then there is no limit.
   * @param  initialSize initial size of the pool.
   */
  public ListPoolWithCreator(ListPoolInstanceCreator creator, int initialSize, int limit) {
    super(0, limit);
    this.creator = creator;

    for (int i = 0; i < initialSize; i++) {
      releaseObject(newInstance());
    } 
  }

  /**
   * Creates a new object.<p>
   *
   * @return  this new object.
   */
  public NextItem newInstance() {
    return creator.newInstance();
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

