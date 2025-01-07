package com.sap.engine.lib.xsl.xpath.xobjects;

import com.sap.engine.lib.log.LogWriter;

/**
 *   Helps to conveniently iterate over <tt>XNodeSet</tt>, either in
 * increasing or in decreasing order.
 *   Not thread safe.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class IntArrayIterator {

  private int[] a;
  private int index; // points to a cell in a
  private int end; // the index which should be processed last
  private int step; // either -1 or 1
  private int start;

  public IntArrayIterator() {

  }

  /**
   * Allows reusage of the same iterator.
   */
  protected void init(int[] a, int start, int end, int step) {
    this.a = a;
    this.step = step;
    this.start = start;
    this.index = start;
    this.end = end;
    index = start;
  }

  /**
   * Returns the current integer pointed by the iterator and moves one step further.
   */
  public int next() {
    int r = a[index];
    //LogWriter.getSystemLogWriter().println("DUMP a[" + index + "]");
    index += step;
    return r;
  }

  /**
   * Checks if iteration process has finished.
   */
  public boolean hasNext() {
    return (index != end);
  }

  /**
   * When the iterator will no longer be used, this method should be called.
   * Thus the reference to the <tt>int[]</tt> being iterated will be release
   * and the garbage collector will be able to get it.
   */
  public void close() {
    a = null; // leave it for the garbage collector
  }

  public static void main(String[] args) throws Exception {
    int[] a = new int[10];

    for (int i = 0; i < a.length; i++) {
      a[i] = (i * 10) % 7;
    } 

    //IntArrayIterator it = new IntArrayIterator(a, 0, 10, 1);
    IntArrayIterator it = new IntArrayIterator();
    it.init(a, 9, -1, -1);

//    while (it.hasNext()) {
////      int x = it.next();
//      //LogWriter.getSystemLogWriter().println(" x = " + x);
//    }
  }

  /**
   * Returns the position of the last <tt>int</tt> returned by the
   * <tt>next()</tt> method.
   */
  public int getCurrentPosition() {
    return (index - step);
  }

  protected void dump() {
    LogWriter.getSystemLogWriter().println("DUMP <IntArrayIterator>"); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("DUMP      a.length = " + a.length); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("DUMP      start    = " + start); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("DUMP      end      = " + end); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("DUMP      index    = " + index); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("DUMP      step     = " + step); //$JL-SYS_OUT_ERR$
    LogWriter.getSystemLogWriter().println("DUMP </IntArrayIterator>"); //$JL-SYS_OUT_ERR$
  }

}

