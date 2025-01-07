/*
 * Created on 2004-3-25
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.transform.algorithms;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class ByteArrayOutputStreamPool {

  protected Vector freePool = new Vector(100, 100);
  // TODO: read these from XML
  // TODO: some tests!
  protected int MAX_COUNT = 400;
  protected int MAX_POOLED_SIZE = 7000000;
  protected long MAX_POOL_SIZE = 10000000;
  
  protected long currentPoolSize = 0;

  public void release(PooledByteArrayOutputStream enc) {
  
    synchronized (freePool) {
      if ((MAX_POOL_SIZE!=0)&&(currentPoolSize>MAX_POOL_SIZE)){
        releaseAll();
      }
      if ((freePool.size() < MAX_COUNT)&&((MAX_POOLED_SIZE==0)||(enc.getLength()<MAX_POOLED_SIZE))) {
        freePool.addElement(enc);
        currentPoolSize += enc.getLength(); 
      } 
    }
  }

  public int getMaxCount() {
    return MAX_COUNT;
  }

  public void setMaxCount(int value) {
    if (value < 0) {
      throw new IllegalArgumentException("No negative pool size allowed!:"+value);
    } else {
      int k;
      synchronized (freePool) {
        while (value < (k = freePool.size())) {
          freePool.removeElementAt(k - 1);
          
        }
      }
    }
    MAX_COUNT = value;
    recalculateSize();
  }

  protected void recalculateSize(){
    synchronized (freePool){
      currentPoolSize = 0;
      for(int i=0;i<freePool.size();i++){
        currentPoolSize += ((PooledByteArrayOutputStream) freePool.elementAt(i)).getLength();
      }
    }
  }

  public void releaseAll() {
    synchronized (freePool) {
      freePool.clear();
      currentPoolSize = 0;
    }
  }

  /**
   * @return Returns the maxPooledSize.
   */
  public int getMaxPooledSize() {
    return MAX_POOLED_SIZE;
  }

  /**
   * @param maxPooledSize The maxPooledSize to set.
   */
  public void setMaxPooledSize(int maxPooledSize) {
    if (MAX_POOL_SIZE < 0){
      throw new IllegalArgumentException("Max Pooled Element Size cannot be less than 0:"+maxPooledSize);
    }    
    synchronized (freePool){
      MAX_POOLED_SIZE = maxPooledSize;
      Iterator iter = freePool.iterator();
      while (iter.hasNext()){
        PooledByteArrayOutputStream temp = (PooledByteArrayOutputStream) iter.next();
        if ((maxPooledSize!=0)&&(temp.getLength()>maxPooledSize)){
          iter.remove();
          currentPoolSize -= temp.getLength(); 
        }
      }
    }
  }

  /**
   * @return Returns the maxPoolSize.
   */
  public long getMaxPoolSize() {
    return MAX_POOL_SIZE;
  }

  /**
   * @param maxPoolSize The maxPoolSize to set.
   */
  public void setMaxPoolSize(long maxPoolSize) {
    if (maxPoolSize < 0){
      throw new IllegalArgumentException("Max Pool Size cannot be less than 0:"+maxPoolSize);
    }
    if ((maxPoolSize!=0)&&(maxPoolSize < MAX_POOL_SIZE)){
      releaseAll();
    }
    MAX_POOL_SIZE = maxPoolSize;
    
  }

  /**
   * @return Returns the currentPoolSize.
   */
  public long getCurrentPoolSize() {
    return currentPoolSize;
  }

  public ByteArrayOutputStream getInstance() {
    PooledByteArrayOutputStream stream; // = new Encoder();
  
    synchronized (freePool) {
      int size = freePool.size();
      if (size > 0) {
  
        stream = (PooledByteArrayOutputStream) freePool.remove(size - 1);
        currentPoolSize -= stream.getLength();
      } else {
        stream = new PooledByteArrayOutputStream(this);
      }
    }
    return stream;
  }

}
