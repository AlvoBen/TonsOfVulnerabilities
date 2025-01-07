package com.sap.engine.lib.util;

import java.util.Comparator;

/**
 * This class is used to compare FastLongPriorityQueue element wrappers which are of type FastLongPQElem
 * and support an int priority. It has a package access, because it has to be hidden for the user.
 * It's intended to be used only by the FastIntPriorityQueue internally
 *
 * @author Stefan Dimov
 * @version 1.0
 */
public class FastLongPQElemComp implements Comparator {

  public int compare(Object e1, Object e2) {
    FastLongPQElem elem1 = (FastLongPQElem)e1;
    FastLongPQElem elem2 = (FastLongPQElem)e2;
    long diff = elem1.getPriority() - elem2.getPriority();
    if (diff == 0) return 0;
    return (diff > 0)?1:-1;
  }
  
}
