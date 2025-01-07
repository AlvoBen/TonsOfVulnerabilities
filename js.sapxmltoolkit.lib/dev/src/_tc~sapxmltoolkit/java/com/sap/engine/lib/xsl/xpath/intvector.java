package com.sap.engine.lib.xsl.xpath;

public class IntVector {

  private int DEFAULT_CAPACITY = 16;
  private int RESIZE_STEP = 32;
  int[] a;
  int n;

  public IntVector() {
    a = new int[DEFAULT_CAPACITY];
    n = 0;
  }

  public IntVector(int initialSize, int resizeStep) {
    RESIZE_STEP = resizeStep;
    a = new int[initialSize];
    n = 0;
  }

  public void resize(int capacity1) {
    int[] a1 = new int[capacity1];
    System.arraycopy(a, 0, a1, 0, a.length);
    a = a1;
    RESIZE_STEP *= 2;
  }

  public void resize() {
    resize(a.length + RESIZE_STEP);
  }

  public void add(int x) {
    if (n == a.length) {
      resize();
    }

    a[n] = x;
    n++;
  }

  public void add(int p, int x) {
    if (n == a.length) {
      resize();
    }

    for (int i = n; i > p; i--) {
      a[i] = a[i - 1];
    } 

    a[p] = x;
    n++;
  }

  public int elementAt(int index) {
    return a[index];
  }

  public void setElement(int index, int value) {
    a[index] = value;
  }

  public void clear() {
    n = 0;
  }

  public void remove(int p) {
    n--;

    for (int i = p; i < n; i++) {
      a[i] = a[i + 1];
    } 
  }

  public int indexOf(int v) {
    for (int i = 0; i < n; i++) {
      if (a[i] == v) {
        return i;
      }
    } 

    return -1;
  }

  public boolean contains(int v) {
    return indexOf(v) == -1 ? false : true;
  }

  public void removeElement(int v) {
    int x = indexOf(v);
    if (x > -1) {
      remove(x);
    }
  }

  public String toString() {
    StringBuffer b = new StringBuffer("[ ");

    for (int i = 0; i < n; i++) {
      b.append(a[i]).append(' ');
    } 

    return b.append("]").toString();
  }

  public int getCapacity() {
    return a.length;
  }

  public int size() {
    return n;
  }

  public int get(int i) {
    return elementAt(i);
  }

}

