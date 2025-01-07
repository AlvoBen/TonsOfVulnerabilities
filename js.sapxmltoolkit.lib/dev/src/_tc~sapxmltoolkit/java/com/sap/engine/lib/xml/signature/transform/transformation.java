package com.sap.engine.lib.xml.signature.transform;

import java.util.HashMap;
import java.util.Hashtable;

import com.sap.engine.lib.xml.signature.*;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.transform.algorithms.*;

public abstract class Transformation implements Cloneable {
  
  public static boolean allowXSLT = Boolean.getBoolean("com.sap.xml.allowxslt");
  
  public String uri = null;
  public Object[] additionalArgs = null;
  public HashMap dataHashmap = null;

  public String toString(){
    return uri.concat(additionalArgs());
  }
  
  String additionalArgs(){
    if (additionalArgs==null){
      return "";
    }
    StringBuffer buf = new StringBuffer();
    buf.append(' ');
    for(int i=0;i<additionalArgs.length;i++){
      Object arg = additionalArgs[i];
      if (arg instanceof String[]){
        String[] arr = (String[]) arg;
        for(int j=0;j<arr.length;j++){
          buf.append(arr[j]).append(' ');
        }
      } else {
        buf.append(arg);
      }
      buf.append('|');
    }
    return buf.toString();
  }
  protected Transformation(Object[] additionalArgs) {
    this.additionalArgs = additionalArgs;
  }
    
  protected Transformation(Object[] additionalArgs, HashMap dataHashmap) {
    this.additionalArgs = additionalArgs;
    this.dataHashmap = dataHashmap;
  }

  public abstract void transform(Data data) throws SignatureException;

  public static Transformation getInstance(String uri, Object[] additionalArgs) throws SignatureException {
    if (uri.equals(Constants.TR_C14N_EXCL_OMIT_COMMENTS)) {
      ExclusiveCanonicalization xc = new ExclusiveCanonicalization(additionalArgs, false);
      xc.uri = uri;
      return xc;
    }
    
    if (uri.equals(Constants.TR_C14N_EXCL_WITH_COMMENTS)) {
      ExclusiveCanonicalization xc = new ExclusiveCanonicalization(additionalArgs, true);
      xc.uri = uri;
      return xc;
    }


    if (uri.equals(Constants.TR_C14N_OMIT_COMMENTS)) {
      Canonicalization c = new Canonicalization(additionalArgs, false);
      c.uri = uri;
      return c;
    }

    if (uri.equals(Constants.TR_C14N_WITH_COMMENTS)) {
      Canonicalization c = new Canonicalization(additionalArgs, true);
      c.uri = uri;
      return c;
    }

    if (uri.equals(Constants.TR_XPATH)) {
      XPathTransformation xp = new XPathTransformation(additionalArgs);
      xp.uri = uri;
      return xp;
    }

    if (uri.equals(Constants.TR_ENVELOPED_SIGNATURE)) {
//      Hashtable mapping = new Hashtable();
//      mapping.put("dsig", "http://www.w3.org/2000/09/xmldsig#");
//      XPathTransformation xp = new XPathTransformation(new Object[] {"count(ancestor-or-self::dsig:Signature | here()/ancestor::dsig:Signature[1]) > count(ancestor-or-self::dsig:Signature)", mapping});
//      xp.uri = uri;
      EvelopedSignatureTransformation est =  new EvelopedSignatureTransformation(additionalArgs);
      est.uri = uri;
      return est;
    }

    if (uri.equals(Constants.TR_BASE64_DECODE)) {
      Base64 b64 = new Base64(additionalArgs);
      return b64;
    }

    if (uri.equals(Constants.TR_XSLT) && allowXSLT) {
      XSLTTransformation xslt = new XSLTTransformation(additionalArgs);
      xslt.uri = uri;
      return xslt;
    }
    
    if (uri.equals(Constants.TR_DECRYPT_XML)){
      DecryptTransformation decrypt = new DecryptTransformation(additionalArgs);
      decrypt.uri = uri;
      return decrypt;
    }
    
    if (uri.equals(Constants.TR_DECRYPT_BINARY)){
      BinaryDecryptTransformation decrypt = new BinaryDecryptTransformation(additionalArgs);
      decrypt.uri = uri;
      return decrypt;
    }

    if (uri.equals(Constants.TR_DECRYPT_OLD)){
      DecryptTransformation decrypt = new DecryptTransformation(additionalArgs);
      decrypt.uri = uri;
      return decrypt;
    }
    throw new SignatureException("Unrecognized transformation uri: " + uri, new java.lang.Object[]{uri, additionalArgs});
  }
  
  public abstract Transformation defineFrom(GenericElement el, HashMap $dataHashmap) throws SignatureException;
  
  public Transformation defineFrom(Object[] additionalArguments, HashMap $dataHashmap) throws SignatureException {    
    this.additionalArgs = additionalArguments;
    this.dataHashmap = $dataHashmap;
    return this;
  }
}

