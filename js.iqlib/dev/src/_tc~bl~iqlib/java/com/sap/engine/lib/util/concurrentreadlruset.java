/*
 * Copyright (c) 2002 by SAP Labs Bulgaria AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.lib.util;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sap.engine.lib.util.base.LinearItemAdapter;
import com.sap.engine.lib.util.base.ListPool;
import com.sap.engine.lib.util.base.NextItem;

/**
 * LRU Set, with concurrent and totally unsynchronized read operations.
 *
 * @author Krasimir Semerdzhiev (krasimir.semerdzhiev@sap.com)
 * @version 6.30
 */

 
public class ConcurrentReadLRUSet extends Set { //$JL-CLONE$

  static final long serialVersionUID = -5952349183210217005L;
  
  private class Wrapper extends LinearItemAdapter { //$JL-CLONE$
    static final long serialVersionUID = 1124327626889363525L;
    public Object data;
    public long lastAccess = 0;

    public boolean equals(Object obj) {
      if (obj instanceof Wrapper) {
        return data.equals(((Wrapper)obj).data);
      } else if (obj != null) {
        return obj.equals(data);
      }
      return false;
    }

    public int hashCode() {
      return data.hashCode();
    }

    public void setLastAccess() {
      lastAccess = System.currentTimeMillis();
    }

    public void clearItem() {
      data = null;
      lastAccess = 0;
      super.clearItem();
    }
    
    private void writeObject(ObjectOutputStream oos) throws NotSerializableException {
	    try {
	      oos.defaultWriteObject();
	    } catch (IOException ioex) {
	      throw new NotSerializableException("Cannot serialize class " + this.getClass().getName() + ". Error is " + ioex.toString());
	    }
	  }
	  
	  private void readObject(ObjectInputStream oos) throws NotSerializableException {
	    try {
	      oos.defaultReadObject();
	    } catch (IOException ioex) {
	      throw new NotSerializableException("Cannot deserialize class " + this.getClass().getName() + ". Error is " + ioex.toString());
	    } catch (ClassNotFoundException cnfe) {
	      throw new NotSerializableException("Cannot deserialize class " + this.getClass().getName() + ". Error is " + cnfe.toString());
	    }
	      
	  }
  }

  private class WrapperPool extends ListPool {

    static final long serialVersionUID = -7165509360313906522L;    

    public WrapperPool(int initialSize) {
      super(initialSize);
    }

    public WrapperPool(int initialSize, int limit) {
      super(initialSize, limit);
    }

    public synchronized Wrapper getFreeObject() {
      return (Wrapper)(getObject());
    }

    public NextItem newInstance() {
      return new Wrapper();
    }

    public synchronized void releaseObject(NextItem item) {
      item.clearItem();
      addFirstItem(item);
    }
  }

  private WrapperPool wrapperPool = null;
  private Wrapper[] LRUArray = null;
  private int LRUindex = 0;

  public ConcurrentReadLRUSet(int initialCapacity, int maxSize) {
    super(initialCapacity);
    this.wrapperPool = new WrapperPool(initialCapacity, maxSize);
    LRUArray = new Wrapper[maxSize];
    LRUindex = 0;
  }

  private Wrapper replaceMinimalWrapper(Wrapper newOne) {
    Wrapper min = LRUArray[0];
    int minIndex = 0;
    for(int i = 1; i < LRUindex; i++) {
      if (LRUArray[i] != null && LRUArray[i].lastAccess < min.lastAccess) {
        min = LRUArray[i];
        minIndex = i;
      }
    }

    LRUArray[minIndex] = LRUArray[0];
    LRUArray[0] = newOne;
    return min;
  }

  public synchronized boolean add(Object obj) {
    Wrapper work = wrapperPool.getFreeObject();
    work.data = obj;
    work.setLastAccess();
      if (LRUindex < LRUArray.length) {
        LRUArray[LRUindex++] = work;
      } else {
        Wrapper victim = replaceMinimalWrapper(work);
        super.remove(victim);
//        if (wrapperPool.size() < LRUArray.length) {
        wrapperPool.releaseObject(victim);
//        }
      }
    return super.add(work);
  }

  public boolean contains(Object obj) {
    Wrapper work = (Wrapper)this.get(obj);
    if (work != null) {
      work.setLastAccess();
      return true;
    }
    return false;
  }

  private Object get(Object obj) {
    int pos = nextPtr[hasher.hash(obj.hashCode()) % capacity];
    while (pos != LAST) {
      if (keys[pos - capacity].equals(obj)) {
        return keys[pos - capacity];
      }
      pos = nextPtr[pos];
    }
    return null;
  }

//  public static void main(String[] args) {
//    long k = Long.MIN_VALUE+1;
//    System.out.println("1negative: " + k);
//    System.out.println("hex long: " + Long.toHexString(k));
//    int y = (int)(k);
//    System.out.println("1(int)negative: " + y);
//    System.out.println("hex int: " + Integer.toHexString(y));
//    k = Long.MAX_VALUE-1;
//    System.out.println("2negative: " + k);
//    System.out.println("hex long: " + Long.toHexString(k));
//
//    y = (int)(k);
//    System.out.println("2(int)negative: " + y);
//    System.out.println("hex int: " + Integer.toHexString(y));
//    k = 0;
//    System.out.println("3negative: " + k);
//
//    System.out.println("3(int)negative: " + ((int)k));

//       int x = 100;
//    ConcurrentReadLRUSet myLRUSet = new ConcurrentReadLRUSet(x, x*5);
////    Set myLRUSet = new Set(x);
//
//    Random rand = new Random();
//    System.out.println("============================================================================================");
//    for(int i = 0; i < x*10; i++) {
//      int r = Math.abs(rand.nextInt() % x);
//      System.out.println("["+i+"] contains for: " + r + " result: " + myLRUSet.contains(new String("Object: #" + r)));
//      System.out.println("["+i+"] Add for: " + r + " result: " + myLRUSet.add(new String("Object: #" + r)));
//      System.out.println("["+i+"] contains for: " + r + " result: " + myLRUSet.contains(new String("Object: #" + r)));
//
//    }
//    System.out.println("============================================================================================");
//    Object[] in = new Object[x*10];
//    int count = 0;
//    for (int i=0; i< x*10; i++) {
//      int r = Math.abs(rand.nextInt() % x);
//      boolean res = myLRUSet.contains(new String("Object: #" + r));
//      if (res) {
//        in[count++] = new String("Object: #" + r);
//        System.out.println("Contains for: " + r + " result: \nTRUE\n ");
//      } else {
//        System.out.println("Contains for: " + r + " result: false ");
//      }
//    }
//    System.out.println("============================================================================================");
//    for (int i=0; i < count; i++) {
//      System.out.println("Remove for: " + in[i] + " result: \nTRUE\n ");
//    }
//
//
//  }

}
