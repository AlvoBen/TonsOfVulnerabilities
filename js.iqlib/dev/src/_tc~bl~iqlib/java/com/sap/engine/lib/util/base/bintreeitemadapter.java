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
 * Standard implementation of the BinTreeItem interface.<p>
 *
 * @author Miroslav Petrov
 * @version 1.0.3
 */
public class BinTreeItemAdapter extends ItemAdapter implements BinTreeItem { //$JL-CLONE$
  static final long serialVersionUID = 5539110433706052368L;
  /**
   * Left successor of the item.<p>
   */
  protected BinTreeItem left = null;
  /**
   * Right successor of the item.<p>
   */
  protected BinTreeItem right = null;
  /**
   * Search key.<p>
   */
  protected Comparable key = null;

  /**
   * Gets search key.<p>
   *
   * @return  the search key.
   */
  public Comparable getKey() {
    return key;
  }

  /**
   * Set search key.<p>
   *
   * @param   key the search key.
   */
  public void setKey(Comparable key) {
    this.key = key;
  }

  /**
   * Sets the left successor of the item.<p>
   *
   * @param   item the left successor of the item.
   */
  public void setLeft(BinTreeItem item) {
    left = item;
  }

  /**
   * Retrieves the left successor of the item.<p>
   *
   * @return  the left successor of the item.
   */
  public BinTreeItem getLeft() {
    return left;
  }

  /**
   * Sets the right successor of the item.<p>
   *
   * @param   item the left successor of the item.
   */
  public void setRight(BinTreeItem item) {
    right = item;
  }

  /**
   * Retrieves the right successor of the item.<p>
   *
   * @return  the right successor of the item.
   */
  public BinTreeItem getRight() {
    return right;
  }

  /**
   * Prepare item to be pooled.<p>
   *
   */
  public void clearItem() {
    left = null;
    right = null;
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

