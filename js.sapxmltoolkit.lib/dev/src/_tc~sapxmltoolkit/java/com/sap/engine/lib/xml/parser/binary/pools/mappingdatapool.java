/*
 * Copyright (c) 2006 by SAP Labs Bulgaria.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.lib.xml.parser.binary.pools;

import com.sap.engine.lib.xml.parser.binary.common.MappingData;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * <p>
 * A pool is a public class used to make the process of creating a large number
 * of objects faster. Methods <tt>getObject()</tt> and <tt>releaseObject(Object)</tt>
 * are used to operate with objects created before, which are stored in an array called
 * <tt>poolArray</tt>.
 * </p>
 *
 * @author Vladimir Savtchenko, Vladimir Videlov
 * @version 7.10
 */
public final class MappingDataPool {

  /**
   * length of poolArray in initial state
   */
  private int initialSize;

  /**
   * count of objects which are added in an empty poolArray
   */
  private int resizeStep = 100;

  /**
   * current position
   */
  public int pos;

  /**
   * the array of elements
   */
  private MappingData arrayOfObjects[];

  public int getPos() {
    return pos;
  }

  public void releasePool() {
    pos = arrayOfObjects.length - 1;
  }

  /**
   * Constructs Pool MappingData using initSize and step parameters. Creates a
   * some objects which are stored in a poolArray.
   *
   * @param   initSize initial size of poolArray
   * @param   step resize step if poolArray is empty
   * @exception   IllegalArgumentException if negative value of initSize or step
   * parameter is set
   */
  public MappingDataPool(int initSize, int step) {
    if ((step < 1) || (initSize < 1)) {
      throw new IllegalArgumentException("Wrong value of initialSize or resizeStep parameter");
    }

    initialSize = initSize;
    resizeStep = step;
    arrayOfObjects = new MappingData[initialSize];
    /*
     //???
     for (int i = 0; i < initialSize; i++) {
     //change this in your own pool
     //arrayOfObjects[i] = new MappingData();
     }
     */
    pos = initialSize - 1;
  }

  /**
   * Returns a free MappingData. PoolArray will be resized in case it's empty.
   *
   * @param prefix Prefix of the mapping
   * @param uri URI of the mapping
   * @return current free MappingData
   */
  public MappingData getObject(CharArray prefix, CharArray uri) {
    if (pos < 0) {
      this.autoResize();
    }

    if (arrayOfObjects[pos] == null) {
      arrayOfObjects[pos] = new MappingData();
    }

    arrayOfObjects[pos].prefix = prefix;
    arrayOfObjects[pos].uri = uri;

    return arrayOfObjects[pos--];
  }

  /**
   * Released MappingData is set as free in the poolArray.
   * @param obj Object to release
   */
  public void releaseObject(MappingData obj) {
    if (pos < (arrayOfObjects.length - 1)) {
      pos++;
      arrayOfObjects[pos] = obj;
    }
  }

  /**
   * Changes value of resizeStep field.
   *
   * @param   step value of resizeStep field to be set
   * @exception   IllegalArgumentException if negative value is set
   */
  public void setResizeStep(int step) {
    if (step < 1) {
      throw new IllegalArgumentException("Wrong value of resizeStep parameter");
    }

    resizeStep = step;
  }

  /**
   * Get current value of resizeStep field.
   *
   * @return value of resizeStep field
   */
  public int getResizeStep() {
    return resizeStep;
  }

  /**
   * Changes value of initSize field.
   *
   * @param   initSize value of initSize field to be set
   */
  public void setInitialSize(int initSize) {
    if (initSize < 1) {
      throw new IllegalArgumentException("Wrong value of initSize parameter");
    }

    initialSize = initSize;
  }

  /**
   * Gets current value of initialSize field.
   *
   * @return value of initialSize field
   */
  public int getInitialSize() {
    return initialSize;
  }

  /**
   * Makes all "free" pointers in the array null
   * ... so that garbageCollector can get the objects, and release memory
   */
  public void release() {
    for (int i = 0; i < pos; i++) {
      arrayOfObjects[i] = null;
    }
  }

  public void releaseAllObjects() {
    pos = arrayOfObjects.length - 1;
  }

  /**
   * If a large number of free objects are created without real usage the
   * poolArray can be shorthened using this method. New poolArray contains
   * max(initialSize, currentUsedObjects) number of objects.
   */
  public void forceShrink() {
    int usedElements = arrayOfObjects.length - pos - 1;

    if (usedElements >= initialSize) {
      if (pos >= 0) {
        arrayOfObjects = new MappingData[usedElements];
        pos = -1;
      }
    } else if (arrayOfObjects.length != initialSize) {
      MappingData[] tempArr = new MappingData[initialSize];
      int newPos = initialSize - usedElements - 1;

      if (newPos <= pos) {
        System.arraycopy(arrayOfObjects, 0, tempArr, 0, newPos + 1);
      } else {
        if (pos >= 0) {
          System.arraycopy(arrayOfObjects, 0, tempArr, 0, pos + 1);
        }

        for (int i = pos + 1; i < newPos; i++) {
          tempArr[i] = new MappingData();
        }
      }

      arrayOfObjects = tempArr;
      pos = newPos;
    }
  }

  /**
   * This method is called in a case of resizing poolArray.
   */
  private void autoResize() {
    MappingData tempArr[] = new MappingData[arrayOfObjects.length + resizeStep];

    System.arraycopy(arrayOfObjects, 0, tempArr, resizeStep, arrayOfObjects.length);

    arrayOfObjects = tempArr;
    pos = resizeStep - 1;
    resizeStep *= 2;
  }

  public void check() throws Exception {
    for (int i = 0; i < arrayOfObjects.length; i++) {
      for (int j = i + 1; j < arrayOfObjects.length; j++) {
        if (arrayOfObjects[i] == arrayOfObjects[j]) {
          throw new Exception("they are equal:" + i + "," + j + " " + arrayOfObjects[i] + ", " + arrayOfObjects[j]);
        }
      }
    }
  }

  public void forceResize(int newsize) {
    MappingData tempArr[] = new MappingData[newsize];
    int count = arrayOfObjects.length - pos - 1;
    int tindex = newsize - count;

    System.arraycopy(arrayOfObjects, 0, tempArr, tindex, count);

    arrayOfObjects = tempArr;
    pos = newsize - count - 1;
  }

  public int getSize() {
    return arrayOfObjects.length;
  }
}