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

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

/**
 * TODO: JAVA DOC
 *
 * @author: Iliyan Nenov, ilian.nenov@sap.com
 * @version: SAP J2EE Engine 6.30
 */
public class LRUSet extends Set { //$JL-CLONE$
  
  static final long serialVersionUID = -1325998530097046225L;

  /**
   * TODO: JAVA DOC
   */
  private class SetItem {
    public SetItem prev;
    public SetItem next;
    public Object data;

    public boolean equals(Object obj) {
      return data.equals(obj);
    }

    public int hashCode() {
      return data.hashCode();
    }

  }

  /**
   * TODO: JAVA DOC
   */
  private class LRUQueue {

    /* Default queue minimal size */
    protected final static int MIN_SIZE_DEFAULT = 10;
    /* Default queue maximal size */
    protected final static int MAX_SIZE_DEFAULT = 100;
    /* Maximal size of the queue */
    protected int maxSize;
    /* Minimal size of the queue */
    protected int minSize;
    /* Current size of the queue */
    protected int size;
    /* First node of the queue */
    protected SetItem first;
    /* Last node of the queue */
    protected SetItem last;

    /* Constructor */
    public LRUQueue() {
      this(MIN_SIZE_DEFAULT, MAX_SIZE_DEFAULT);
    }

    /**
     * Constructor
     *
     * @param   maxSize Maximal queue size
     */
    public LRUQueue(int maxSize) {
      this(MIN_SIZE_DEFAULT, maxSize);
    }

    /**
     * Constructor
     *
     * @param   minSize Minimal queue size
     * @param   maxSize Maximal queue size
     */
    public LRUQueue(int minSize, int maxSize) {
      this.minSize = minSize;
      this.maxSize = maxSize;
      size = 0;
      first = null;
      last = null;
    }

    /**
     * Update the access frequency of an item and move it up if necessary
     *
     * @param node The node to be updated
     */
    public void update(SetItem node) {
      if (node != first) {
        if (last == node) {
          first = last;
          last = last.prev;
        } else {
          node.prev.next = node.next;
          node.next.prev = node.prev;
          node.next = first;
          node.prev = last;
          first.prev = node;
          last.next = node;
          first = node;
        }
      }
    }

    /**
     * Add a new item into the queue
     * Replace last if Queue Size exceeded
     *
     * @param node The node to be added
     */
    public SetItem add(SetItem node) {
      SetItem removed = null;

      if (first == null) {
        size = 1;
        node.prev = node;
        node.next = node;
        first = node;
        last = node;
        return null;
      } else {
        if (size < maxSize) {
          size++;
          node.next = first;
          node.prev = last;
          last.next = node;
          first.prev = node;
          first = node;
        } else {
          //adding before first
          node.next = first;
          node.prev = last.prev;
          last.prev.next = node;
          first.prev = node;
          first = node;
          //and deleting last
          removed = last;
          last = last.prev;
        }

        return removed;
      }
    }

    /**
     *
     * @return whether this queue is full or not
     */
    public boolean isFull() {
      return (size >= maxSize);
    }

    /**
     * Removes an item in the queue
     *
     * @param node The node to be removed
     */
    public SetItem remove(SetItem node) {
      SetItem snode = node;
      size--;

      if (size > 0) {
        if (first == node) {
          first = node.next;
          last.next = first;
          node.prev.next = node.next;
          node.next.prev = node.prev;
        } else if (last == node) {
          last = node.prev;
          first.prev = last;
          node.prev.next = node.next;
          node.next.prev = node.prev;
        } else {
          node.prev.next = node.next;
          node.next.prev = node.prev;
        }

        node.next = null;
        node.prev = null;
      } else {
        first = null;
        last = null;
      }

      return snode;
    }

    /**
     * Removes the last item of the queue
     */
    public SetItem removeLast() {
      return remove(last);
    }

    /**
     * Prints out to the screen the contents of the queue in first->last order
     */
    public void print() {
      SetItem temp;
//      System.out.print("QUEUE ");
//      System.out.print("(size:" + size + ")");

      if (first != null) {
        temp = first.next;

        while (temp != last) {
          temp = temp.next;
        }
      } else {
//        System.out.println("Queue empty.");
      }
    }

    /**
     * Get max Size of queue
     *
     * return maxSize
     */
    public int getMaxSize() {
      return maxSize;
    }

    /**
     * Get min Size of queue
     *
     * return minSize
     */
    public int getMinSize() {
      return minSize;
    }

    /**
     * Get size of queue
     *
     * return size
     */
    public int getSize() {
      return size;
    }

    /**
     * Empties this queue
     */
    public void clear() {
      size = 0;
      first = null;
      last = null;
    }

  }

  //////////////////////////////////////////////////////////////////////////////////////////

  private LRUQueue queue = null;

  /**
   * TODO: JAVA DOC
   */
  public LRUSet(int maxSize) {
    super();
    this.queue = new LRUQueue(maxSize);
  }

  /**
   * TODO: JAVA DOC
   */
  public LRUSet(int initialCapacity, int maxSize) {
    super(initialCapacity);
    this.queue = new LRUQueue(maxSize);
  }

  /**
   * TODO: JAVA DOC
   */
  public boolean add(Object obj) {
    SetItem si = new SetItem();
    si.data = obj;
    SetItem removed = queue.add(si);
    if (removed != null) {
      super.remove(removed.data);
    }
    return super.add(si);
  }

  /**
   * TODO: JAVA DOC
   */
  public boolean contains(Object obj) {
    SetItem getted = (SetItem) get(obj);
    if (getted != null) {
      queue.update(getted);
      return true;
    }

    return false;
  }

  /**
   * TODO: JAVA DOC
   */
  public boolean remove(Object obj) {
    SetItem getted = (SetItem) get(obj);
    if (getted != null) {
      return super.remove(getted);
    }

    return false;
  }

  /**
   * Used for adding extra features ...
   */
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

//  // Added for test purposes
//  public static void main(String[] args) {
//    int x = 100;
//    LRUSet myLRUSet = new LRUSet(x);
//    Random rand = new Random();
//    System.out.println("============================================================================================");
//    for(int i = 0; i < x*10; i++) {
//      int r = Math.abs(rand.nextInt() % x);
//      System.out.println("Add for: " + r + " result: " + myLRUSet.add(new String("Object: #" + r)));
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
//  }
  
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