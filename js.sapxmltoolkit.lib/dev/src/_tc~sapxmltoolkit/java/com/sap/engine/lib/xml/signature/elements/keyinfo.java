package com.sap.engine.lib.xml.signature.elements;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.xml.signature.Configurator;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.encryption.EncryptedKey;
import com.sap.engine.lib.xml.signature.encryption.EncryptedType;
import com.sap.engine.lib.xml.signature.encryption.XMLCryptor;
import com.sap.engine.lib.xml.util.BASE64Decoder;
import com.sap.engine.lib.xml.util.BASE64Encoder;

public class KeyInfo extends GenericElement {
  
  private String keyName = null;
  private Key privateKey = null;
  private PublicKey publicKey = null;
  private Certificate[] certificates = null;
  private String[] certTypes = null;
  
  public KeyInfo(Node parent) throws SignatureException {
    super(((parent != null) ? parent.getOwnerDocument() : null), Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "KeyInfo", null);
    if (parent != null) {
      Node kiNode = this.getDomRepresentation();
      Node enKey = parent;
      NodeList method = ((Element) enKey).getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod");
      if (method.getLength() > 0) {
        enKey.insertBefore(kiNode, method.item(method.getLength() - 1).getNextSibling());
      } else {
        enKey.insertBefore(kiNode, enKey.getFirstChild());
      }
      
    } else {
      getOwner().appendChild(domRepresentation);
    }
  } 
    
  public KeyInfo() {
    super(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "KeyInfo");
  }

  public KeyInfo(GenericElement parent) throws SignatureException {
    super(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "KeyInfo", parent);
  }
  
  public KeyInfo(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(namespaceUri, qualifiedName, parent);
  }
  
  public KeyInfo(Element domRepresentation, GenericElement parent) throws SignatureException{
    super(domRepresentation, parent);
  }

  public String getKeyName() {
    try {
    GenericElement el = getDirectChild(Constants.SIGNATURE_SPEC_NS, "KeyName");
    if (el!=null){
      return (keyName = XMLCryptor.gatherText(el.getDomRepresentation()));
    }    
    } catch (Exception ex){
      new SignatureException(ex);
    }
    return keyName;
  }

  public void setKeyName(String keyName) {
    this.keyName = keyName;
  }

  
  public Certificate[] getCerificates() {
    return certificates;
  }

  public void setCertificates(Certificate[] certificates) {
    this.certificates = certificates;
    this.certTypes = SignatureContext.getTypes(certificates);
  }

  public void setPublicKey(PublicKey publicKey) {
    this.publicKey = publicKey;
  }

  public void setPrivateKey(Key privateKey) {
    this.privateKey = privateKey;
  }

  public Key getPrivateKey() {
    return privateKey;
  }

  public void addKeyName() throws SignatureException {
    GenericElement name = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "KeyName", this);
    name.appendTextChild(keyName);
  }
  
  public void addCertificateInfo() throws CertificateEncodingException, SignatureException {
    String[] uniques = SignatureContext.removeDuplicates(certTypes);
    for (int i = 0; i < uniques.length; i ++) {
      String whatData = uniques[i] + "Data";
      String whatCertificate = uniques[i] + "Certificate";
      GenericElement data = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + whatData, this);
      for (int j = 0; j < certTypes.length; j ++) {
        if (certTypes[j].equals(uniques[i])) {
          String encCert = new String(BASE64Encoder.encode(certificates[j].getEncoded())); //$JL-I18N$
          GenericElement certData = new GenericElement(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + whatCertificate, data);
          certData.appendTextChild(encCert);
          appendChild(data);
        }
      }
    }
  }

  public void addKeyValue(String keyType) throws SignatureException {
    KeyValue dKey;

    if (keyType.equalsIgnoreCase("dsa")) {
      dKey = new DSAKeyValue(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "KeyValue", this);
    } else if (keyType.equalsIgnoreCase("rsa")) {
      dKey = new RSAKeyValue(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "KeyValue", this);
    } else {
      throw new SignatureException("Unrecognized signature type: " + keyType, new java.lang.Object[]{keyType});
    }

    if (certificates != null && certificates[0] != null) {
      dKey.setCertificate(certificates[0]);
    }
    dKey.setPublicKey(publicKey);
    dKey.construct();
    appendChild(dKey);
  } 

  public byte[] getEncodedKey() throws SignatureException{
    if (encodedKey!=null){
      return encodedKey;
    }
    if (getKey()!=null){
      encodedKey = key.getEncoded();
      return encodedKey;
    }
    return encodedKey;
  }
  
  protected Key key = null;
  protected byte[] encodedKey = null;
  
  public Key getKey() throws SignatureException{
    if (key!=null){
      return key;
    }
    if (getKeyName()!=null){
      return key=SignatureContext.getKeyAliasResolver().getKey(keyName);
    }
    GenericElement el = getDirectChild(Constants.ENCRYPTION_SPEC_NS, "EncryptedKey");
    if (el!=null){
      EncryptedKey ek = (EncryptedKey) el;
      key = ek.getWrappedKey();
      if (key==null) {
        encodedKey = ek.getWrappedKeyEncoded();
        
        //TODO: see what this key is! - see references!!!
        
      } 
      return key;
    }
    el = getDirectChild(Constants.SIGNATURE_SPEC_NS, "RetrievalMethod");
    if (el!=null){
      RetrievalMethod met = (RetrievalMethod)el;
      met.init();
      Element key1 = met.getElementByID(met.uri.substring(1));
      EncryptedKey enc = new EncryptedKey(key1, true);
      enc.initializeDescendants();
      enc.setType(((EncryptedType) parent).getEncryptionMethod().getAlgorithmURI()); 
      return key = enc.getWrappedKey();
      //TODO: see what this key is! - see references!!!
    }
    el = getDirectChild(Constants.SIGNATURE_SPEC_NS, "X509Data");
    if (el != null){
      el = el.getDirectChild(Constants.SIGNATURE_SPEC_NS, "X509Certificate");
      if (el != null){
        try {
          String base64encodedCertificate = XMLCryptor.gatherText(el.getDomRepresentation());
          CertificateFactory certFact = CertificateFactory.getInstance("X509", Configurator.getProviderName());
          Certificate cert = certFact.generateCertificate(new ByteArrayInputStream(BASE64Decoder.decode(base64encodedCertificate.getBytes()))); //$JL-I18N$
          return key=SignatureContext.getKeyAliasResolver().getKey(cert);
        } catch (Exception ex){
          throw new SignatureException("Unable to read certificate",new java.lang.Object[]{domRepresentation},ex);
        }
      }
    }    
    //TODO: see if there is any sense to implement others
    return null;
  }


}


