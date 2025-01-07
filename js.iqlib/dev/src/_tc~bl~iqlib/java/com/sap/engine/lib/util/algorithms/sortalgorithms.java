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
package com.sap.engine.lib.util.algorithms;

import com.sap.engine.lib.util.iterators.ChangeableIterator;
import com.sap.engine.lib.util.iterators.ForwardIterator;
import com.sap.engine.lib.util.iterators.RandomAccessIterator;

/**
 * Sort objects pointed by given iterator.<p>
 *
 * @author Meglena Atanasova
 * @version 1.0
 */
public class SortAlgorithms extends MutableAlgorithm {

  /**
   * BinaryIntFunction for comparing two objects.
   * Returns an int as a result.
   */
  BinaryIntFunction intFunction;
  /**
   * Flag that sets order for sorting elements. <p>
   * If flag == true the objects pointed by the iterator will be sorted in
   * ascending order. If flag == false respectively in descending.
   */
  boolean flag;

  /**
   * Sets the  function that will be used for comparing objects.<p>
   *
   * @param _intFunction a particular BinaryIntFunction.
   */
  public void setCompareFunction(BinaryIntFunction _intFunction) {
    intFunction = _intFunction;
  }

  /**
   * Sets the flag that define the order for sorting.<p>
   *
   * @param _flag a boolean value . If flag == true the objects pointed by
   *		 the iterator will be sorted in ascending order. If flag == false
   *		 respectively in descending.
   */
  public void setAscending(boolean _flag) {
    flag = _flag;
  }

  public void qsort(ChangeableIterator iterator) {

  }

  public void qsort(ForwardIterator iterator) {

  }

  /**
   * Sorts the objects pointed by definite RandomAccessIterator.<p>
   *
   * @param iterator RandomAccessIterator that points to the objects .
   *		 that will be sorted.
   */
  public void qsort(RandomAccessIterator iterator) {
    int size = iterator.size();
    int start = iterator.currentPosition();
    int end = size;

    if (flag == true) {
      internalQsortAsc(iterator, start, end);
    } else {
      internalQsortDesc(iterator, start, end);
    }
  }

  /**
   * Sorts the objects pointed by definite RandomAccessIterator in
   * ascending order.<p>
   *
   * @param iterator RandomAccessIterator that points to the objects .
   *		 that will be sorted.
   * @param start the position to start from.
   * @param size the number of objects to sort.
   */
  private void internalQsortAsc(RandomAccessIterator iterator, int start, int size) {
    if (size < 7) {
      for (int i = start; i < size + start; i++) {
        for (int j = i; j > start && (intFunction.intFunction(iterator.jumpTo(j - 1), iterator.jumpTo(j)) > 0); j--) {
          swap(iterator, j, j - 1); 
        } 
      }
      return;
    }

    int m = start + size / 2;

    if (size > 7) {
      int l = start;
      int n = start + size - 1;

      if (size > 40) {
        int s = size / 8;
        l = median(iterator, l, l + s, l + 2 * s);
        m = median(iterator, m - s, m, m + s);
        n = median(iterator, n - 2 * s, n - s, n);
      }

      m = median(iterator, l, m, n);
    }

    Object medium = iterator.jumpTo(m);
    int a = start, b = a, c = start + size - 1, d = c;

    while (true) {
      while (b <= c && intFunction.intFunction(iterator.jumpTo(b), medium) <= 0) {
        if ((intFunction.intFunction(iterator.jumpTo(b), medium) == 0)) {
          swap(iterator, a++, b);
        }
        b++;
      }

      while (c >= b && intFunction.intFunction(iterator.jumpTo(c), medium) >= 0) {
        if ((intFunction.intFunction(iterator.jumpTo(b), medium) == 0)) {
          swap(iterator, c, d--);
        }
        c--;
      }

      if (b > c) {
        break;
      }
      swap(iterator, b++, c--);
    }

    int s, n = start + size;
    s = Math.min(a - start, b - a);
    vecswap(iterator, start, b - s, s);
    s = Math.min(d - c, n - d - 1);
    vecswap(iterator, b, n - s, s);
    if ((s = b - a) > 1) {
      internalQsortAsc(iterator, start, s);
    }
    if ((s = d - c) > 1) {
      internalQsortAsc(iterator, n - s, s);
    }
  }

  /**
   * Sorts the objects pointed by definite RandomAccessIterator in
   * descending order.<p>
   *
   * @param iterator RandomAccessIterator that points to the objects .
   *		 that will be sorted.
   * @param start the position to start from.
   * @param size the number of objects to sort.
   */
  private void internalQsortDesc(RandomAccessIterator iterator, int start, int size) {
    if (size < 7) {
      for (int i = start; i < size + start; i++) {
        for (int j = i; j > start && (intFunction.intFunction(iterator.jumpTo(j - 1), iterator.jumpTo(j)) < 0); j--) {
          swap(iterator, j, j - 1); 
        } 
      }
      return;
    }

    int m = start + size / 2;

    if (size > 7) {
      int l = start;
      int n = start + size - 1;

      if (size > 40) {
        int s = size / 8;
        l = median(iterator, l, l + s, l + 2 * s);
        m = median(iterator, m - s, m, m + s);
        n = median(iterator, n - 2 * s, n - s, n);
      }

      m = median(iterator, l, m, n);
    }

    Object medium = iterator.jumpTo(m);
    int a = start, b = a, c = start + size - 1, d = c;

    while (true) {
      while (b <= c && intFunction.intFunction(iterator.jumpTo(b), medium) >= 0) {
        if ((intFunction.intFunction(iterator.jumpTo(b), medium) == 0)) {
          swap(iterator, a++, b);
        }
        b++;
      }

      while (c >= b && intFunction.intFunction(iterator.jumpTo(c), medium) <= 0) {
        if ((intFunction.intFunction(iterator.jumpTo(b), medium) == 0)) {
          swap(iterator, c, d--);
        }
        c--;
      }

      if (b > c) {
        break;
      }
      swap(iterator, b++, c--);
    }

    int s, n = start + size;
    s = Math.min(a - start, b - a);
    vecswap(iterator, start, b - s, s);
    s = Math.min(d - c, n - d - 1);
    vecswap(iterator, b, n - s, s);
    if ((s = b - a) > 1) {
      internalQsortDesc(iterator, start, s);
    }
    if ((s = d - c) > 1) {
      internalQsortDesc(iterator, n - s, s);
    }
  }

  /**
   * Swaps object on position a with object on position b.<p>
   *
   * @param iterator RandomAccessIterator that points to the objects .
   *		 that will be sorted.
   * @param a the first object position.
   * @param b the second object index position.
   */
  private void swap(RandomAccessIterator iterator, int a, int b) {
    Object work = iterator.jumpTo(a);
    Object work1 = iterator.jumpTo(b);
    iterator.jumpTo(b);
    iterator.change(work);
    iterator.jumpTo(a);
    iterator.change(work1);
  }

  /**
   * Swaps objects on positions  (a .. (a+n-1)) with objects on positions
   * (b .. (b+n-1)).
   *
   * @param iterator RandomAccessIterator that points to the objects .
   *		 that will be sorted.
   * @param a the first object position.
   * @param b the second object index position.
   * @param n the number of objects to sort.
   */
  private void vecswap(RandomAccessIterator iterator, int a, int b, int n) {
    for (int i = 0; i < n; i++, a++, b++) {
      swap(iterator, a, b); 
    }
  }

  /**
   * Returns the position of the median.
   *
   * @param iterator a RandomAccessIterator that points to the objects
   *		 that will be sorted.
   * @param a the first object position.
   * @param b the second object index position.
   * @param c the third object index position.
   * @return the position of the median.
   */
  private int median(RandomAccessIterator iterator, int a, int b, int c) {
    return ((intFunction.intFunction(iterator.jumpTo(a), iterator.jumpTo(b)) < 0) ? ((intFunction.intFunction(iterator.jumpTo(b), iterator.jumpTo(c)) < 0) ? b : (intFunction.intFunction(iterator.jumpTo(a), iterator.jumpTo(c)) < 0) ? c : a) : ((intFunction.intFunction(iterator.jumpTo(b), iterator.jumpTo(c)) > 0) ? b : (intFunction.intFunction(iterator.jumpTo(a), iterator.jumpTo(c)) > 0) ? c : a));
  }

}

