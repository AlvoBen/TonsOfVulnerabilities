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

import com.sap.engine.lib.util.base.Pointer;
import com.sap.engine.lib.util.iterators.RootIterator;
import com.sap.engine.lib.util.iterators.SnapShotEnumeration;

import java.io.Serializable;

/**
 * The root interface for all data structures, available in
 * com.sap.engine.lib.util library.<p>
 *
 * @author Vasil Popovski
 * @version 4.0
 */
public interface RootDataStructure
  extends Cloneable, DeepCloneable, Serializable {

  static final long serialVersionUID = 3928771800110038899L;
  
  /**
   * Retrieves the count of the elements in the structure.<p>
   *
   * @return   the count of the elements in the structure
   */
  public int size();


  /**
   * Clears the structure so that it contains no keys and no elements.<p>
   */
  public void clear();


  /**
   * Checks if the structure is empty.<p>
   *
   * @return true if the structure has no elements
   * and false otherwise
   */
  public boolean isEmpty();


  /**
   * Creates and returns a copy of this structure object.<p>
   *
   * @return  a clone of this instance.
   */
  public Object clone();


  /**
   * Returns a snapshot enumeraiton, containing the elements of the datastructure.
   *
   * @return   an enumeraiton, containing the elements of the datastructure
   */
  public SnapShotEnumeration elementsEnumeration();


  /**
   * Returns iterator of the components of this datastructure.
   *
   * @return     iterator of the components of this datastructure
   */
  public RootIterator elementsIterator();


  /**
   * Returns an array, containing the wrappers of the datastructure.
   *
   * @return   an array, containing the wrappers of the datastructure
   */
  public Pointer[] toPointerArray();


  /**
   * Returns an array, containing the elements of the datastructure.
   *
   * @return   an array, containing the elements of the datastructure
   */
  public Object[] toArray();


  /*  public Pointer add(Object item);
   public void remove(Pointer item);
   public void removeElement(Object item); // searching to find pointer*/

}

