package com.sap.engine.lib.util.concurrent;

/**
 * A standard linked list node used in various queue classes
 */
public class LinkedNode {

  public Object value;
  public LinkedNode next;

  public LinkedNode() {

  }

  public LinkedNode(Object x) {
    value = x;
  }

  public LinkedNode(Object x, LinkedNode n) {
    value = x;
    next = n;
  }

}

