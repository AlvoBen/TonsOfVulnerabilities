package com.sap.engine.lib.util;

import java.util.Comparator;

/**
 * This class is used to compare FastObjPriorityQueue element wrappers which are of type FastObjPQElem
 * and support a generic priority of type Object. It has a package access, because it has to be hidden
 * for the user. It's intended to be used only by the FastIntPriorityQueue internally. However, while
 * constructing a new object of type FastObjPriorityQueue, the user has to provide a comparator, which
 * is supposed to compare priority objects and is used by this comparator. Actually, this comparator
 * will compare FastObjPQElem wrappers, by comparing their priority objects using the comparator provided
 * by the user
 *
 * @author Stefan Dimov
 * @version 1.0
 */
public class FastObjPQElemComp implements Comparator {

  private Comparator prioComp;

  FastObjPQElemComp(Comparator prioComp) {
    if (prioComp == null) throw new IllegalArgumentException("The wrapped comparator must NOT be null");
    this.prioComp = prioComp;
  }

  public int compare(Object e1, Object e2) {
    FastObjPQElem elem1 = (FastObjPQElem)e1;
    FastObjPQElem elem2 = (FastObjPQElem)e2;
    return prioComp.compare(elem1.getPriority(), elem2.getPriority());
  }
}
