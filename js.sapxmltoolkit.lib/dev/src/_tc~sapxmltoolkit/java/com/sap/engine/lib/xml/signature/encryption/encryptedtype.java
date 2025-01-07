package com.sap.engine.lib.xml.signature.encryption;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import org.w3c.dom.*;

import com.sap.engine.lib.xml.dom.DocumentImpl;
import com.sap.engine.lib.xml.signature.*;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.crypto.CustomCipher;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.elements.KeyInfo;

public abstract class EncryptedType extends GenericElement {

  protected String type = Constants.ELEMENT_ENCRYPTION;

  public EncryptedType(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(parent.getOwner(), namespaceUri, qualifiedName, parent);
  }

  public EncryptedType(Node parent, String localName, int childIndex) throws com.sap.engine.lib.xml.signature.SignatureException {
    super(getOwner(parent), Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX.concat(localName), null);    if (parent != null) {
      insertAtPosition(parent, this.domRepresentation, childIndex);
    } else {
      insertAtPosition(getOwner(), this.domRepresentation, childIndex);
    }
  }
  
  public EncryptedType(Element domRepr, GenericElement parent) throws SignatureException{
    super(domRepr, parent);
  }
  
  private static Document getOwner(Node parent) {
    return       
      (parent != null ? 
        (Document) ((parent instanceof Document) ? (parent) : (parent.getOwnerDocument())) : 
        new DocumentImpl());

  }
  
  public EncryptedType(Node parent, String localName) throws com.sap.engine.lib.xml.signature.SignatureException {
    this(parent, localName, -1);
  }
 
  public EncryptedType(Element n, boolean unused) throws SignatureException {
    super(n, null);
  }

  public CipherData getCipherData() throws SignatureException {
    return (CipherData) getDescendant(Constants.ENCRYPTION_SPEC_NS, "CipherData");
  }

  public EncryptionMethod getEncryptionMethod() throws SignatureException {
    return (EncryptionMethod) getDescendant(Constants.ENCRYPTION_SPEC_NS, "EncryptionMethod");
  }
  
  public KeyInfo getKeyInfo() throws SignatureException{
    return (KeyInfo) getDescendant(Constants.SIGNATURE_SPEC_NS, "KeyInfo");
  }

  public void setType(String type) {
    this.type = type;
  }

  public void construct() {
    if (type != null) {
      setAttribute("Type", type);
    }
  }
  
  public void construct(int $childIndex) {
    setAttribute("Type", type);
  }

  protected static void insertAtPosition(Node parent, Node child, int position) throws com.sap.engine.lib.xml.signature.SignatureException {      
    NodeList childNodes = parent.getChildNodes();
    if (position > childNodes.getLength()) {
      throw new com.sap.engine.lib.xml.signature.SignatureException("Cannot insert child on position" + position + " when old childs are only " +  childNodes.getLength() + ".",  new java.lang.Object[]{parent, child});
    }
    if (position < 0 || position == childNodes.getLength()) {
      parent.appendChild(child);
      return;
    }
        
    if (position == 0) {
      if (childNodes.getLength() <= 0) {
        parent.appendChild(child);
      } else {
        parent.insertBefore(child, childNodes.item(0));
      }
      return;
    }
    
    Node next = parent.getFirstChild();
    for (int i = 0; i < position; i ++) {
      next = next.getNextSibling();
    }
    parent.insertBefore(child, next);
  }
  
  protected Key key = null;
  public void setKey(Key key){
    this.key = key;
  }
  
  public Key getKey() throws SignatureException{
    if (key==null){
      key = getKeyInfo().getKey();
    }
    return key;
  }
  protected byte[] decrypted = null;
  
  public byte[] getDecrypted() throws SignatureException {
    if (decrypted == null) {
      Reusable reusable = null;
      try {
        CipherData data = getCipherData();
        String algorithmURI = getEncryptionMethod().getAlgorithmURI();
        reusable = Reusable.getInstance(algorithmURI);//SignatureContext.getCryptographicPool().generateCipherInstance(algorithmURI, "CBC", "NoPadding");
        CustomCipher cipher = (CustomCipher) reusable;
        byte[] encoded = data.getCipherValue();
        byte[] IV = new byte[Configurator.getIVLength(algorithmURI)];
        System.arraycopy(encoded, 0, IV, 0, IV.length);
        IvParameterSpec pSpec = new IvParameterSpec(IV);
        Key k = getKey();
        if (k==null){
         throw new SignatureException("No encryption key found", new Object[]{domRepresentation});
        }
        cipher.init(Cipher.DECRYPT_MODE, k, pSpec);
        decrypted = cipher.doFinal(encoded, IV.length, data.getLen() /*encoded.length*/ - IV.length );
      } catch (SignatureException sig) {
        throw sig;
      } catch (Exception ex) {
        throw new SignatureException("Unable to decrypt", ex);
      } finally {
        if (reusable!=null){
          reusable.release();
        }
      }

    }
    return decrypted;
  }
}

