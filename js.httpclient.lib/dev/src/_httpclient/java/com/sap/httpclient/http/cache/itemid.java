package com.sap.httpclient.http.cache;

/**
 * @author: Mladen Droshev
 */

public class ItemID  implements java.io.Serializable  {

  public static final String NULL_ID = "NULL_ID";
  public static final ItemID NULL_ITEM_ID = new ItemID(NULL_ID);

  protected String id;

  public ItemID(String id) {
    if (id == null) {
      this.id = NULL_ID;
    } else {
      this.id = id;
    }
  }

  public int hashCode() {
    return id.hashCode();
  }

  public boolean equals(Object obj) {
    return (obj instanceof ItemID && id.equals(((ItemID) obj).id));
  }

  public String toString() {
    return id;
  }
  
}
