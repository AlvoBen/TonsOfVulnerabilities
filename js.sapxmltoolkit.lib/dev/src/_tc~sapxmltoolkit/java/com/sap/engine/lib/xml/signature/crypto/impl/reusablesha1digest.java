/*
 * Created on 2005-4-19
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.crypto.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.crypto.ReusableConfigurator;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class ReusableSHA1Digest  extends Reusable{
  public final static String SHA1_DIGEST = Constants.DIGEST_SHA1;
  
  public static ReusablePool pool = new ReusablePool(ReusablePool.MAX_SIZE);
  
  protected MessageDigest internalRandom = null;

  private ReusableSHA1Digest() throws NoSuchAlgorithmException, NoSuchProviderException {
    internalRandom = MessageDigest.getInstance("SHA-1", ReusableConfigurator.getProvider());
  }

  public Object getInternal(){
    return internalRandom;
  }
  
  public static Reusable newInstance() throws Exception {
    Reusable ret = pool.newInstance();
    if (ret == null){
      ret = new ReusableSHA1Digest();
    }
    return ret;
  }

  public void release() {
    pool.release(this);
  }
}