/*
 * Created on 2004-2-2
 * 
 * @author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.transform.algorithms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *  
 */
public class PooledByteArrayOutputStream extends ByteArrayOutputStream{

  protected ByteArrayOutputStreamPool pool = null;
  
  protected PooledByteArrayOutputStream(ByteArrayOutputStreamPool pool){
    super(10000);
    this.pool=pool;
  }
  
  /* (non-Javadoc)
   * @see java.io.ByteArrayOutputStream#toByteArray()
   */
  public synchronized byte[] toByteArray() {
    byte[] ret = super.toByteArray();
    reset();
    pool.release(this);
    return ret;
  }

  /* (non-Javadoc)
   * @see java.io.ByteArrayOutputStream#writeTo(java.io.OutputStream)
   */
  public synchronized void writeTo(OutputStream out) throws IOException {
    super.writeTo(out);
    reset();
    pool.release(this);
  }

  public long getLength(){
    return buf.length;
  }
  
  public void setPool(ByteArrayOutputStreamPool pool){
    this.pool = pool;
  }
  
}
