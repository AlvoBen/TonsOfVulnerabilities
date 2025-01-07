/*
 * Created on 2005-4-14
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.crypto.impl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import com.sap.engine.lib.xml.signature.crypto.Reusable;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class ReusableSecureRandom extends Reusable{
  public final static String SHA1_PRNG_URI = "http://www.acme.com/secran#sha1-prng";
  
  public static ReusablePool pool = new ReusablePool(ReusablePool.MAX_SIZE);
  
  protected SecureRandom internalRandom = null;

  private ReusableSecureRandom() throws NoSuchAlgorithmException, NoSuchProviderException {
    internalRandom = SecureRandom.getInstance("SHA1PRNG"/*, Configurator.getProviderName()*/);
  }

  public Object getInternal(){
    return internalRandom;
  }
  
  public static Reusable newInstance() throws Exception {
    Reusable ret = pool.newInstance();
    if (ret == null){
      ret = new ReusableSecureRandom();
    }
    return ret;
  }

  public void release() {
    pool.release(this);
  }
}
