package com.sap.engine.lib.util;

import java.util.Comparator;

/**
 * This class is used to compare FastIntPriorityQueue element wrappers which are of type FastIntPQElem
 * and support an int priority. It has a package access, because it has to be hidden for the user.
 * It's intended to be used only by the FastIntPriorityQueue internally
 *
 * @author Stefan Dimov
 * @version 1.0
 */
final class FastIntPQElemComp implements Comparator {

  public int compare(Object e1, Object e2) {
    FastIntPQElem elem1 = (FastIntPQElem)e1;
    FastIntPQElem elem2 = (FastIntPQElem)e2;
    int diff = elem1.getPriority() - elem2.getPriority();
    if (diff == 0) return 0;
    return (diff > 0)?1:-1;
  }
  
}
