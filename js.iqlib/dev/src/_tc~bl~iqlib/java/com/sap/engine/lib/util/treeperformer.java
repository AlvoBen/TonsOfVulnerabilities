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

/**
 * Interface for performing certain operations over tree nodes.<p>
 *
 * Example:
 * <p><blockquote><pre>
 *    BinarySearchTree tree = new BinarySearchTree();
 *
 *    tree.perform( new TreePerformer() {
 *    public boolean perform(Object item) {
 *     // sum is global variable for outer clas
 *     sum += item.hashCode();
 *     System.out.println(item);
 *     return false;
 *    }}, true);
 *
 *    System.out.println(sum);
 * </pre></blockquote><p>
 *
 * @author Nikola Arnaudov
 * @version 1.0.3
 */
public interface TreePerformer {

  /**
   * Perform some operations over tree node.<p>
   *
   * @param   item a tree node.<p>
   * @return  true if one wants to stop enumeration, false otherwise.
   */
  public boolean perform(Object item);

}

