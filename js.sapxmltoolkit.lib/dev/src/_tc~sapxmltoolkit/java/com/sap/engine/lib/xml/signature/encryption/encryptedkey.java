package com.sap.engine.lib.xml.signature.encryption;

import java.security.Key;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.signature.Configurator;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.encryption.keytrans.KeyTransporter;
import com.sap.engine.lib.xml.signature.transform.Transformation;

public class EncryptedKey extends EncryptedType {

  private String carriedKeyName = null;
  private String recipient = null;
  private Transformation[] references = null;

  public EncryptedKey() throws SignatureException {
    super(null, "EncryptedKey");
    type = Constants.KEY_ENCRYPTION;
  }

  public EncryptedKey(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(namespaceUri, qualifiedName, parent);
    type = Constants.KEY_ENCRYPTION;
  }

  public EncryptedKey(Node parent) throws com.sap.engine.lib.xml.signature.SignatureException {
    super(parent, "EncryptedKey");
    type = Constants.KEY_ENCRYPTION;
  }

  public EncryptedKey(Element n, boolean unused) throws SignatureException {
    super(n, unused);
    type = Constants.KEY_ENCRYPTION;
  }

  public EncryptedKey(Element n, GenericElement parent) throws SignatureException{
    super(n,parent);
  }
  
  public void setCarriedKeyName(String carriedKeyName) throws SignatureException {
    this.carriedKeyName = carriedKeyName;
    GenericElement name = new GenericElement(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX + "carriedKeyName", this);
    name.appendTextChild(carriedKeyName);
  }

  public String getCarriedKey() {
    return carriedKeyName;
  }

  public void setRecipient(String recipient) throws SignatureException {
    this.recipient = recipient;
    GenericElement name = new GenericElement(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX + "recipient", this);
    name.appendTextChild(recipient);
  }

  public String getRecipient() {
    return recipient;
  }

  public void setReferences(Transformation[] references) {
    this.references = references;
  }

  public Transformation[] getReferences(Transformation[] $references) {
    return $references;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public String getType() {
    return type;
  }

  protected Key key = null;

  /**
   * @return
   */
  public Key getWrappedKey() throws SignatureException{
    if (key==null){
      byte[] dec  = getWrappedKeyEncoded();
      GenericElement el = getParent();
      while ((el!=null)&&(!(el instanceof EncryptedType))){
        el = el.getParent();
      }
      String uri =type;
      if (el!=null) {
        EncryptedType et= (EncryptedType) el;
        uri = et.getEncryptionMethod().getAlgorithmURI();
      }
      key = buildKey(dec, uri);
    }
    return key;
    
  }

  protected Key buildKey(byte[] dec, String uri) throws SignatureException{
  // change for tdes-kw,aes etc!
      Reusable reusable = null;
      try {
        reusable = Reusable.getInstance(uri.concat("_gen"));
        SecretKeyFactory keyFactory = (SecretKeyFactory) reusable.getInternal();
        String algorithm = Configurator.getCipherAlgorithm(uri);
        SecretKeySpec spec = new SecretKeySpec(dec, algorithm);
        return key = keyFactory.generateSecret(spec);
      } catch (Exception ex) {
        throw new SignatureException("Unable to init key",new Object[]{dec,uri},ex);
      } finally {
        if (reusable != null) {
          reusable.release();
        }
      }
  }
  /**
   * @return
   */
  public byte[] getWrappedKeyEncoded() throws SignatureException {
    KeyTransporter tr = KeyTransporter.getInstance(getEncryptionMethod().getAlgorithmURI(), this);
    CipherData cd = getCipherData(); 
    byte[] tv = cd.getCipherValue();
    int len = cd.getLen();
    byte[] value;
    if (len == tv.length) {
      value = tv;
    } else {
      value = new byte[len];
      System.arraycopy(tv, 0, value, 0, len);
    }
    return tr.decrypt(getKey(), value);
  }

  protected Key encodingKey;
  public void setWrappingKey(Key wrappingKey){
    encodingKey = wrappingKey;
  }
    
}

