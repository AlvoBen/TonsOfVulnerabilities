package com.sap.engine.cache.communication;

import com.sap.engine.cache.util.Serializator;
import com.sap.engine.cache.util.dump.LogUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.LinkedList;

/**
 * @author Petev, Petio, i024139
 */
public class NotificationMessage implements Serializable {

  static final long serialVersionUID = 8769874620522949140L;
  
  public LinkedList queue = null;

  public byte type = -1;
  public String key = null;
  public Map attributes = null;	// this is needed for semantical invalidation/by attribute
  public Serializable transported = null;
  public int regiondId = -1;

  public static byte[] write(NotificationMessage message) {
    try {
      return Serializator.toByteArray(message);
    } catch (IOException e) {
      LogUtil.logT(e);
      return null; // however - seems impossible to be here. TODO LOG
    }
  }

  public static NotificationMessage read(byte[] message) {
    try {
      return (NotificationMessage) Serializator.toObject(message);
    } catch (IOException e) {
      LogUtil.logT(e);
      return null; // however - seems impossible to be here. TODO LOG
    } catch (ClassNotFoundException e) {
      LogUtil.logT(e);
      return null; // however - seems impossible to be here. TODO LOG
    }
  }

}
