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
package com.sap.engine.lib.xml.parser.pool;

import com.sap.engine.lib.xml.parser.helpers.Reference;

/**
 * Class description -
 *
 * @author Vladimir Savtchenko
 * @version 1.00
 */
/**
 * Pool is a public class used to fast the process of creating a large number
 * of objects. Methods getObject() and releaseObject(Object obj) are used to
 * operate with objects, created before, which are stored in an array called
 * poolArray.
 *
 * @version 4.0
 */
//import com.sap.engine.services.jndi.cache.*;
//import com.sap.engine.services.jndi.Constants;
public final class ReferencePool {
  private static final int MAX_SIZE = 1000;

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
  private int pos;
  /**
   * the array of elements
   */
  private Reference arrayOfObjects[];
  private Reference temp;

//  public int getPos() {
//    return pos;
//  }

//  public void releasePool() {
//    pos = arrayOfObjects.length - 1;
//  }

  /**
   * Constructs Pool Reference using initSize and step parameters. Creates a
   * some objects which are stored in a poolArray.
   *
   * @param   initSize initial size of poolArray
   * @param   step resize step if poolArray is empty
   * @exception   IllegalArgumentException if negative value of initSize or step
   * parameter is set
   */
  public ReferencePool(int initSize, int step) {
    if ((step < 1) || (initSize < 1)) {
      throw new IllegalArgumentException("Wrong value of initialSize or resizeStep parameter");
    }

    initialSize = initSize;
    resizeStep = step;
    arrayOfObjects = new Reference[initialSize];

    for (int i = 0; i < initialSize; i++) {
      //change this in your own pool
      arrayOfObjects[i] = new Reference();
    } 

    pos = initialSize - 1;
  }

  /**
   * Returns a free Reference. PoolArray will be resized in case it's empty.
   *
   * @return current free Reference
   */
  //public  Reference getObject(){
  public Reference getObject() {
    if (pos < 0) {
      if (arrayOfObjects.length >= MAX_SIZE) {
        return new Reference();
      }
      
      //LogWriter.getSystemLogWriter().println("Reference pool: autoresizing:" + pos);
      this.autoResize();
    }

    //LogWriter.getSystemLogWriter().println("Reference pool:: " + pos + " " + arrayOfObjects[pos]);
    return arrayOfObjects[pos--];
  }

  /**
   * Released Reference is set as free in the poolArray.
   */
  public void releaseObject(Reference obj) {
    if (pos < (arrayOfObjects.length - 1)) {
      pos++;
      arrayOfObjects[pos] = obj;
    }

    ;
  }

//  /**
//   * Changes value of resizeStep field.
//   *
//   * @param   step value of resizeStep field to be set
//   * @exception   IllegalArgumentException if negative value is set
//   */
//  public void setResizeStep(int step) {
//    if (step < 1) {
//      throw new IllegalArgumentException("Wrong value of resizeStep parameter");
//    }
//
//    resizeStep = step;
//  }

//  /**
//   * Get current value of resizeStep field.
//   *
//   * @return value of resizeStep field
//   */
//  public int getResizeStep() {
//    return resizeStep;
//  }

//  /**
//   * Changes value of initSize field.
//   *
//   * @param   initSize value of initSize field to be set
//   * @param   toReconstruct true sets value reconstructing Pool Reference
//   *          false sets value without reconstructing current Pool Reference
//   * @exception   PoolException if negative value is set
//   */
//  public void setInitialSize(int initSize) {
//    if (initSize < 1) {
//      throw new IllegalArgumentException("Wrong value of initSize parameter");
//    }
//
//    initialSize = initSize;
//  }

  /**
   * Gets current value of initialSize field.
   *
   * @return value of initialSize field
   */
//  public int getInitialSize() {
//    return initialSize;
//  }

  /**
   * Makes all "free" pointers in the array null
   * ... so that garbageCollector can get the objects , and release memory
   */
//  public void release() {
//    for (int i = 0; i < pos; i++) {
//      arrayOfObjects[i] = null;
//    } 
//  }

//  /**
//   * If a large number of free objects are created without real usage the
//   * poolArray can be shorthened using this method. New poolArray contains
//   * max(initialSize, currentUsedObjects) number of objects.
//   */
//  public void forceShrink() {
//    //LogWriter.getSystemLogWriter().println("resizing! ");
//    //count used Reference at the moment
//    int usedElements = arrayOfObjects.length - pos - 1;
//
//    if (usedElements >= initialSize) {
//      if (pos >= 0) {
//        //stay place only for used at the moment objects
//        arrayOfObjects = new Reference[usedElements];
//        pos = -1;
//      }
//    } else if (arrayOfObjects.length != initialSize) {
//      Reference[] tempArr = new Reference[initialSize];
//      int newPos = initialSize - usedElements - 1;
//
//      if (newPos <= pos) {
//        System.arraycopy(arrayOfObjects, 0, tempArr, 0, newPos + 1);
//      } else {
//        if (pos >= 0) {
//          System.arraycopy(arrayOfObjects, 0, tempArr, 0, pos + 1);
//        }
//
//        for (int i = pos + 1; i < newPos; i++) {
//          tempArr[i] = new Reference();
//        } 
//      }
//
//      arrayOfObjects = tempArr;
//      pos = newPos;
//    }
//  }

  /**
   * This method is called in a case of resizing poolArray.
   */
  private void autoResize() {
    Reference tempArr[] = new Reference[arrayOfObjects.length + resizeStep];

    for (int i = 0; i < resizeStep; i++) {
      tempArr[i] = new Reference();
    } 

    for (int i = 0; i < arrayOfObjects.length; i++) {
      tempArr[i + resizeStep] = arrayOfObjects[i];
    } 

    arrayOfObjects = tempArr;
    pos = resizeStep - 1;
  }

//  public void check() throws Exception {
//    for (int i = 0; i < arrayOfObjects.length; i++) {
//      for (int j = i + 1; j < arrayOfObjects.length; j++) {
//        if (arrayOfObjects[i] == arrayOfObjects[j]) {
//          throw new Exception("they are equal:" + i + "," + j + " " + arrayOfObjects[i] + ", " + arrayOfObjects[j]);
//        }
//      } 
//    } 
//  }

}

