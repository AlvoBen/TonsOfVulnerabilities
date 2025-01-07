/*
 * Created on 2005-4-14
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.crypto.impl;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.SecretKeyFactory;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.crypto.ReusableConfigurator;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class ReusableAESSecretKeyFactory extends Reusable{
  public final static String AES_KEYGEN128_URI = Constants.ALG_ENC_AES128+"_gen";
  public final static String AES_KEYGEN192_URI = Constants.ALG_ENC_AES192+"_gen";
  public final static String AES_KEYGEN256_URI = Constants.ALG_ENC_AES256+"_gen";
  
  public static ReusablePool pool = new ReusablePool(ReusablePool.MAX_SIZE);
  
  protected SecretKeyFactory internalSecretKeyFactory = null;

  private ReusableAESSecretKeyFactory() throws NoSuchAlgorithmException, NoSuchProviderException {
    internalSecretKeyFactory = SecretKeyFactory.getInstance("AES", ReusableConfigurator.getProvider());
  }

  public Object getInternal(){
    return internalSecretKeyFactory;
  }

  public static Reusable newInstance() throws Exception {
    Reusable ret = pool.newInstance();
    if (ret == null){
      ret = new ReusableAESSecretKeyFactory();
    }
    return ret;
  }

  public void release() {
    pool.release(this);
  }
}

