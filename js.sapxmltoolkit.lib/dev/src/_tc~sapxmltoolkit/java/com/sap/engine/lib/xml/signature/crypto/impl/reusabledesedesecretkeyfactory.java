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
public class ReusableDESedeSecretKeyFactory extends Reusable{
  public final static String DESEDE_KEYGEN_URI = Constants.ALG_ENC_TRIPLEDES+"_gen";
  
  public static ReusablePool pool = new ReusablePool(ReusablePool.MAX_SIZE);
  
  protected SecretKeyFactory internalSecretKeyFactory = null;

  private ReusableDESedeSecretKeyFactory() throws NoSuchAlgorithmException, NoSuchProviderException {
    internalSecretKeyFactory = SecretKeyFactory.getInstance("DESede", ReusableConfigurator.getProvider());
  }

  public Object getInternal(){
    return internalSecretKeyFactory;
  }
  
  public static Reusable newInstance() throws Exception {
    Reusable ret = pool.newInstance();
    if (ret == null){
      ret = new ReusableDESedeSecretKeyFactory();
    }
    return ret;
  }

  public void release() {
    pool.release(this);
  }
}
