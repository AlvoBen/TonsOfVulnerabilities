package com.sap.engine.lib.xsl.xslt.output;

/**
 * @author       Nick Nickolov, nick_nickolov@abv.bg
 * @version      September 2001
 */
public final class CABuffer {

  private char[] data = new char[100];
  private int length = 0;

  public CABuffer() {

  }

  public void clear() {
    length = 0;
  }

  public void append(char[] ch, int p, int l) {
    if (length + l >= data.length) {
      char[] old = data;
      data = new char[length + l + 10];
      System.arraycopy(old, 0, data, 0, length);
    }

    System.arraycopy(ch, p, data, length, l);
    length += l;
  }

  public char[] getData() {
    return data;
  }

  public int getLength() {
    return length;
  }

  public boolean isEmpty() {
    return (length == 0);
  }

}

