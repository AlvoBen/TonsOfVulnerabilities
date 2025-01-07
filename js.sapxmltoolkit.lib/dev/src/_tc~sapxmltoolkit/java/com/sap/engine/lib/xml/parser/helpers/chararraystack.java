package com.sap.engine.lib.xml.parser.helpers;

public class CharArrayStack {

  private CharArray storage = new CharArray(100, 100);
  private int i1, i2, i3, o2;
  private char[] tmp1;
  private char[] tmp2;

  public boolean isEmpty() {
    return storage.getSize() == 0;
  }

  public boolean isFull() {
    return storage.getSize() != 0;
  }

  public char[] getTop() {
    tmp1 = storage.getData();
//    o1 = storage.getOffset();
    i1 = storage.getSize();
    i2 = ((int) tmp1[i1 - 1] << 16) + tmp1[i1 - 2];
    //i3 = item.getSize();
    char[] ttt = new char[i2];
    System.arraycopy(tmp1, i1 - i2 - 2, ttt, 0, i2);
    return ttt;
  }
  
  private CharArray tmpca = new CharArray();
  public CharArray getTopCharArray() {
    tmp1 = storage.getData();
//    o1 = storage.getOffset();
    i1 = storage.getSize();
    i2 = ((int) tmp1[i1 - 1] << 16) + tmp1[i1 - 2];
    //i3 = item.getSize();
    //char[] ttt = new char[i2];
    int offset = i1 - i2 - 2;
    tmpca.substring(storage, offset, offset + i2);
    storage.setSize(i1 - i2 - 2);
    return tmpca;
    //System.arraycopy(tmp1, o1 + i1 - i2 - 2, ttt, 0, i2);
    //return ttt;
  }

  public void put(CharArray item) {
    storage.append(item);
    i1 = item.getSize();
    storage.append((char) (i1 & 0xFFFF));
    storage.append(((char) ((i1 & 0xFFFF0000) >> 16)));
  }
  
  public void appendSize(int size) {
    storage.append((char) (size & 0xFFFF));
    storage.append(((char) ((size & 0xFFFF0000) >> 16)));
  }
  
  public CharArray getStorage()  {
    return storage; 
  }
  

  public boolean matchTop(CharArray item) {
    tmp1 = storage.getData();
//    o1 = storage.getOffset();
    tmp2 = item.getData();
    o2 = item.getOffset();
    i1 = storage.getSize();
    i2 = ((int) tmp1[i1 - 1] << 16) + tmp1[i1 - 2];
    i3 = item.getSize();
    if (i2 != i3) {
      return false;
    }
    i3--;
    i1 -= 3;

    while (i3 > -1) {
      if (tmp2[i3] != tmp1[i1]) {
        return false;
      }
      i1--;
      i3--;
    }

    storage.setSize(i1 + 1);
    return true;
  }

  public void reuse() {
    storage.clear();
  }
  
  public void substringTop(CharArray dest)  {
    tmp1 = storage.getData();
//    o1 = storage.getOffset();
    i1 = storage.getSize();
    i2 = ((int) tmp1[i1 - 1] << 16) + tmp1[i1 - 2];
    //LogWriter.getSystemLogWriter().println("CharArrayStack: i1=" + i1 + ", i2=" + i2 );
    dest.substring(storage, i1 - i2 - 2, i1 - 2);
    //LogWriter.getSystemLogWriter().println("CharArrayStack: dest=" + dest);
  }
  
  public void consumeTop(int topLength)  {
//    LogWriter.getSystemLogWriter().println("CharArrayStack: storagesize before= " + storage.length());
//    LogWriter.getSystemLogWriter().println("CharArrayStack: storagesize before= " + (storage.getSize() - topLength - 2));
    storage.setSize(storage.getSize() - topLength - 2);
//    LogWriter.getSystemLogWriter().println("CharArrayStack: storagesize= " + storage.length());
  }

}

