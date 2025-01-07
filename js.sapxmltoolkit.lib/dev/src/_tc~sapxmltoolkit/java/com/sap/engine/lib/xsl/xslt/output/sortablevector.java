/*
 * Created on 2004-2-2
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xsl.xslt.output;

import java.util.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.Vector;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class SortableVector extends Vector {

  /**
   * 
   */
  public SortableVector() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @param initialCapacity
   */
  public SortableVector(int initialCapacity) {
    super(initialCapacity);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param initialCapacity
   * @param capacityIncrement
   */
  public SortableVector(int initialCapacity, int capacityIncrement) {
    super(initialCapacity, capacityIncrement);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param c
   */
  public SortableVector(Collection c) {
    super(c);
    // TODO Auto-generated constructor stub
  }
  
  public void sort(Comparator comp){
    Arrays.sort(elementData,0,elementCount,comp);
  }

}
