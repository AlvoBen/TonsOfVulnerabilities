package com.sap.engine.lib.xml.signature.encryption;

import java.security.GeneralSecurityException;

import org.w3c.dom.*;

import com.sap.engine.lib.xml.dom.BinaryTextImpl;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.transform.Transformation;
import com.sap.engine.lib.xml.util.BASE64Encoder;

public class XMLEncryptor extends XMLCryptor {

  public static int MAX_STRING_SIZE = 1000000;

  public XMLEncryptor() {
  }

  public static Node createEncryptedKey(String recipient, String carriedKeyName, Transformation[] transforms, String uri, boolean dataReference) throws SignatureException {
    EncryptedKey ek = new EncryptedKey();
    if (recipient != null) {
      ek.setAttribute("Recipient", recipient);
    }

    if (carriedKeyName != null) {
      GenericElement ckn = new GenericElement(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX + "CarriedKeyName", ek);
      ckn.appendTextChild(carriedKeyName);
    }

    if (transforms != null) {
      GenericElement refList = new GenericElement(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX + "ReferenceList", ek);
      EncReference cr = new EncReference(uri, true);
      cr.setTransforms(transforms);
      cr.construct(refList);
    }

    return ek.getOwner();
  }

  public Node encrypt(Element el, String $algorithmURI, boolean contentOnly) throws SignatureException {
    return replaceWithEncryptedData(el, el, $algorithmURI, contentOnly);
  }

  public void encryptIndirectly(Element whereToPutData, Element parent, Element el, String $algorithmURI, boolean contentOnly, String uri, Transformation[] transforms) throws SignatureException {
    try {
      byte[] base64Encrypted = getBase64EncryptedData(el, $algorithmURI, contentOnly);
      String type = contentOnly ? Constants.CONTENT_ENCRYPTION : Constants.ELEMENT_ENCRYPTION;
      deleteContent(whereToPutData);
      Document doc = whereToPutData.getOwnerDocument();
      Node n;
      if (base64Encrypted.length>MAX_STRING_SIZE){
        BinaryTextImpl bin = new BinaryTextImpl(doc);
        bin.setBinaryData(base64Encrypted);
        n = bin;
      } else {
        n = doc.createTextNode(new String(base64Encrypted)); //$JL-I18N$ 
      }
      whereToPutData.appendChild(n);
      EncryptedData encData = new EncryptedData(parent);
      encData.setType(type);
      encData.construct();
      EncryptionMethod m = new EncryptionMethod(encData);
      m.setAlgorithmURI($algorithmURI);
      m.construct();
      CipherData cd = new CipherData(encData);
      CipherReference cr = new CipherReference(uri);
      cr.setTransforms(transforms);
      cr.construct(cd);
    } catch (GeneralSecurityException e) {
      throw new SignatureException("Error in indirect encryption",new Object[]{whereToPutData, parent, el, $algorithmURI},e);
    }
  }

  private Node replaceWithEncryptedData(Element toReplace, Element el, String $algorithmURI, boolean contentOnly) throws SignatureException {
    try {
      byte[] base64Encrypted = getBase64EncryptedData(el, $algorithmURI, contentOnly);
      String type = contentOnly ? Constants.CONTENT_ENCRYPTION : Constants.ELEMENT_ENCRYPTION;
      Node parent = null; 
      int index = 0;
      if (contentOnly) {
      	parent = toReplace;
      	NodeList childList = parent.getChildNodes();
      	for (int i=childList.getLength(); i>0 ; i--) {
      		parent.removeChild(childList.item(i-1));
      	}
      } else {
      	parent = toReplace.getParentNode();
      	index = getChildIndex(toReplace);
      	parent.removeChild(toReplace);
      }
      EncryptedData encData = new EncryptedData(parent, index);
      encData.setType(type);
      encData.construct();
      EncryptionMethod eM = new EncryptionMethod($algorithmURI, null, encData);
      eM.construct();
      CipherData cD = new CipherData(encData);
      if (base64Encrypted.length>MAX_STRING_SIZE){
        cD.setValue(base64Encrypted);
      } else {
        cD.setCipherValue(new String(base64Encrypted)); //$JL-I18N$
      }
      cD.construct();
      return encData.getDomRepresentation();
    } catch (GeneralSecurityException e) {
      throw new SignatureException("Error while replacing with encrypted data",new Object[]{toReplace, el, $algorithmURI},e);
    }
  }

  public Node getEncrypted(Element el, String $algorithmURI, boolean contentOnly) throws SignatureException {
    try {
      byte[] base64Encrypted = getBase64EncryptedData(el, $algorithmURI, contentOnly);
      String type = contentOnly ? Constants.CONTENT_ENCRYPTION : Constants.ELEMENT_ENCRYPTION;
      EncryptedData encData = new EncryptedData(el);
      encData.setType(type);
      encData.construct();
      EncryptionMethod eM = new EncryptionMethod($algorithmURI, null, encData);
      eM.construct();
      CipherData cD = new CipherData(encData);
      if (base64Encrypted.length > MAX_STRING_SIZE){
        cD.setValue(base64Encrypted);        
      } else {
        cD.setCipherValue(new String(base64Encrypted)); //$JL-I18N$
      }
      cD.construct();
      Node eData = encData.getDomRepresentation();
      el.removeChild(eData);
      return encData.getDomRepresentation();
    } catch (GeneralSecurityException e) {
      throw new SignatureException("Error while encrypting", new Object[]{el, $algorithmURI},e);
    }
  }

  public byte[] encryptRaw(byte[] input, String $algorithmURI) throws SignatureException {
    try {
      algorithmURI = $algorithmURI;
      byte[] encryptedData = encrypt0(input);
      return encryptedData;
    } catch (Exception e) {
      throw new SignatureException("Error while encrypting raw: algorithm:"+$algorithmURI, new Object[]{input, $algorithmURI},e);
    } 
  }

  private byte[] getBase64EncryptedData(Element el, String $algorithmURI, boolean contentOnly) throws SignatureException, GeneralSecurityException {
    try {
      algorithmURI = $algorithmURI;
      byte[] encryptedData = encrypt0(el, contentOnly);
      byte[] base64Encrypted = BASE64Encoder.encode(encryptedData);
      return base64Encrypted;
    } catch (SignatureException e) {
      throw e;
    } catch (Exception e) {
      throw new SignatureException("Error in geting base64 encrypted data",new Object[]{el, $algorithmURI},e);
    } 
  }

  private static int getChildIndex(Element el) {
    Node parent = el.getParentNode();
    if (parent == null) {
      return -1;
    }

    int index = 0;
    Node previous = el;
    while ((previous = previous.getPreviousSibling()) != null) {
      index = index + 1;
    }

    return index;
  }

  /**
   * Encrypts these octets and returns node representing the encrypted data, which is appended to this parent node.
   * @param octetsToEncrypt octets to be encrypted
   * @param parentNode parent node to which the resulted encrypted data node will be appended
   * @return
   * @throws SignatureException
   */
  
  public Node encrypt(byte[] octetsToEncrypt, Node parentNode, String $algorithmURI) throws SignatureException {
    try {
      byte[] base64Encrypted;
      try {
        algorithmURI = $algorithmURI;
        toEncrypt = octetsToEncrypt;
        encrypt0();
        base64Encrypted = BASE64Encoder.encode(toEncrypt);
        toEncrypt = null;
      } catch (Exception e) {
        throw new SignatureException("Error in geting base64 encrypted data", new Object[]{$algorithmURI}, e);
      }
      String type = Constants.CONTENT_ENCRYPTION;
      EncryptedData encData = new EncryptedData(parentNode, 0);
      encData.setType(type);
      encData.construct();
      EncryptionMethod eM = new EncryptionMethod($algorithmURI, null, encData);
      eM.construct();
      CipherData cD = new CipherData(encData);
      if (base64Encrypted.length > MAX_STRING_SIZE) {
        cD.setValue(base64Encrypted);
      } else {
        cD.setCipherValue(new String(base64Encrypted)); //$JL-I18N$
      }
      cD.construct();
      return encData.getDomRepresentation();
    } catch (SignatureException e1) {
      throw e1;
    } catch (Exception e) {
      throw new SignatureException("Error while replacing with encrypted data", new Object[]{$algorithmURI}, e);
    }
  }
}

