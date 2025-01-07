package com.sap.engine.lib.xsl.xslt;

import java.util.Hashtable;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xslt.pool.ObjectPool;

public final class NamespaceManager {

  private Hashtable hashIDtoCharArray = new Hashtable();
  private Hashtable hashCharArraytoID = new Hashtable();
  private ObjectPool intHolderPool = new ObjectPool(new IntHolder().getClass(), 20, 20);
  private IntHolder tempHolder = new IntHolder();
  private int currentID = 0;

  public NamespaceManager() {

  }

  public NamespaceManager reuse() {
    hashIDtoCharArray.clear();
    hashCharArraytoID.clear();
    intHolderPool.releaseAllObjects();
    currentID = 0;
    return this;
  }

  public CharArray get(int idx) {
    tempHolder.set(idx);
    return (CharArray) hashIDtoCharArray.get(tempHolder);
  }

  public CharArray get(CharArray uri) {
    IntHolder h = (IntHolder) hashCharArraytoID.get(uri);

    if (h == null) {
      int id = put(uri);
      h = tempHolder;
      h.set(id);
    }

    return get(h.key());
  }

  public int put(CharArray uri) {
    return put2(uri);
  }

  public int put(String uri) {
    return put2(new CharArray(uri));
  }

  public int put2(CharArray uri) {
    IntHolder ih = (IntHolder) hashCharArraytoID.get(uri);

    if (ih == null) {
      ih = ((IntHolder) intHolderPool.getObject()).reuse(currentID++);
      CharArray u = uri.copy();
      hashIDtoCharArray.put(ih, u);
      hashCharArraytoID.put(u, ih);
    }

    return ih.key();
  }

}

