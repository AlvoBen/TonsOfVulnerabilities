/*
 * Created on 2005-4-14
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.crypto.impl;

import com.sap.engine.lib.xml.signature.crypto.Reusable;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
// for SecretKeyFactory
// for SecureRandom
public class ReusablePool {
  public static int MAX_SIZE = 5;
  Reusable[] pool;
  int start = 0;
  int end = 0;

  public ReusablePool(int size) {
    pool = new Reusable[size];
  }

  protected Reusable newInstance() throws Exception {
    Reusable ret;
    synchronized (pool) {
      ret = pool[start];
      if (ret != null){
        pool[start] = null;
        start++;
        start%=pool.length;
      }
    }
    return ret;
  }

  protected void release(Reusable signature) {
    synchronized (pool) {
      if (pool[end] == null){
        pool[end] = signature;
        end++;
        end%=pool.length;
      }
    }
  }

  public void resize(int size){
    end = 0;
    start = 0;
    pool = new Reusable[size];
  }
  
  public int getSize(){
    int size = 0;
    for (int i = 0;i<pool.length;i++){
      if (pool[i] != null) size++;
    }
    return size;
    //return end - start;
  }
  
  public int getLength(){
    return pool.length;
  }
  
  
}


