package com.sap.engine.lib.xml.parser.pool;

import java.io.Serializable;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * <p>
 * A pool is a public class used to make the process of creating a large number
 * of objects faster. Methods <tt>getObject()</tt> and <tt>releaseObject(Object)</tt>
 * are used to operate with objects created before, which are stored in an array called
 * <tt>poolArray</tt>.
 * </p>
 *
 * @author Vladimir Savtchenko
 * @version 1.00
 */
public final class CharArrayPool implements Serializable {
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
  public int pos;
  /**
   * the array of elements
   */
  private CharArray arrayOfObjects[];
  private CharArray temp;

  public int getPos() {
    return pos;
  }

  public void releasePool() {
    pos = arrayOfObjects.length - 1;
  }

  /**
   * Constructs Pool CharArray using initSize and step parameters. Creates a
   * some objects which are stored in a poolArray.
   *
   * @param   initSize initial size of poolArray
   * @param   step resize step if poolArray is empty
   * @exception   IllegalArgumentException if negative value of initSize or step
   * parameter is set
   */
  public CharArrayPool(int initSize, int step) {
    if (initSize > MAX_SIZE) {
      initSize = MAX_SIZE;
    }
    
    if ((step < 1) || (initSize < 1)) {
      throw new IllegalArgumentException("Wrong value of initialSize or resizeStep parameter");
    }

    initialSize = initSize;
    resizeStep = step;
    arrayOfObjects = new CharArray[initialSize];
    /*
     //???
     for (int i = 0; i < initialSize; i++) {
     //change this in your own pool
     //arrayOfObjects[i] = new CharArray();
     }
     */
    pos = initialSize - 1;
  }

  /**
   * Returns a free CharArray. PoolArray will be resized in case it's empty.
   *
   * @return current free CharArray
   */
  public CharArray getObject() {
    if (pos < 0) {
      if (arrayOfObjects.length >= MAX_SIZE) {
        return new CharArray();
      }
      
      this.autoResize();
    }

    if (arrayOfObjects[pos] == null) {
      arrayOfObjects[pos] = new CharArray();
    }

    return arrayOfObjects[pos--];
  }

  /**
   * Released CharArray is set as free in the poolArray.
   */
  public void releaseObject(CharArray obj) {
    if (pos < (arrayOfObjects.length - 1)) {
      pos++;
      arrayOfObjects[pos] = obj;
    }

    ;
  }
//
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

  /**
   * Get current value of resizeStep field.
   *
   * @return value of resizeStep field
   */
//  public int getResizeStep() {
//    return resizeStep;
//  }

//  /**
//   * Changes value of initSize field.
//   *
//   * @param   initSize value of initSize field to be set
//   * @param   toReconstruct true sets value reconstructing Pool CharArray
//   *          false sets value without reconstructing current Pool CharArray
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

//  /**
//   * If a large number of free objects are created without real usage the
//   * poolArray can be shorthened using this method. New poolArray contains
//   * max(initialSize, currentUsedObjects) number of objects.
//   */
//  public void forceShrink() {
//    int usedElements = arrayOfObjects.length - pos - 1;
//
//    if (usedElements >= initialSize) {
//      if (pos >= 0) {
//        arrayOfObjects = new CharArray[usedElements];
//        pos = -1;
//      }
//    } else if (arrayOfObjects.length != initialSize) {
//      CharArray[] tempArr = new CharArray[initialSize];
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
//          tempArr[i] = new CharArray();
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
    CharArray tempArr[] = new CharArray[arrayOfObjects.length + resizeStep];

    for (int i = 0; i < arrayOfObjects.length; i++) {
      tempArr[i + resizeStep] = arrayOfObjects[i];
    } 

    arrayOfObjects = tempArr;
    pos = resizeStep - 1;
    resizeStep *= 2;
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

//  public void forceResize(int newsize) {
//    CharArray tempArr[] = new CharArray[newsize];
//    int count = arrayOfObjects.length - pos - 1;
//    int tindex = newsize - count;
//
//    for (int i = 0; i < count; i++) {
//      tempArr[tindex + i] = arrayOfObjects[i];
//    } 
//
//    arrayOfObjects = tempArr;
//    pos = newsize - count - 1;
//  }

//  public int getSize() {
//    return arrayOfObjects.length;
//  }

}

