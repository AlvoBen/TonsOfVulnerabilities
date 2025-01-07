package com.sap.engine.lib.xml.signature.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.Hashtable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import org.w3c.dom.*;

import com.sap.engine.lib.xml.dom.BinaryTextImpl;
import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.crypto.CustomCipher;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
import com.sap.engine.lib.xml.signature.elements.GenericElement;

public class XMLDecryptor extends XMLCryptor {

  protected boolean parseDecrypted = true;

  /**
   * @return Returns the parseDecrypted.
   */
  public boolean isParseDecrypted() {
    return parseDecrypted;
  }

  /**
   * @param parseDecrypted The parseDecrypted to set.
   */
  public void setParseDecrypted(boolean parseDecrypted) {
    this.parseDecrypted = parseDecrypted;
  }
  public XMLDecryptor() {
  }

  private static String deduceAlgorithm(EncryptedData encData) throws SignatureException {
    GenericElement encMethod = encData.getEncryptionMethod();

    if (encMethod == null || encMethod.getAttribute("Algorithm", null, null) == null) {
      return null;
    }

    return encMethod.getAttribute("Algorithm", null, null);
  }

  public String decryptEncData(Element el) throws SignatureException {
    return decryptEncData(el, null);
  }
  
  public byte[] decryptToOctets(Element el, String $algorithmURI) throws SignatureException {
    EncryptedData encData = new EncryptedData(el, true);
    encData.initializeDescendants();
    if (key!=null){
      encData.setKey(key);
    }
    try {
      byte[] ret= encData.getDecrypted();
      if (ret!=null){
        return ret;
      }
    } catch (Exception ex){
      // $JL-EXC$
      // do nothing - continue
    }
    if ($algorithmURI == null) {
      $algorithmURI = deduceAlgorithm(encData);
    }

    if ($algorithmURI == null) {
      throw new SignatureException("Unable to determine algorithm.", new Object[]{el, $algorithmURI});
    }

    this.algorithmURI = $algorithmURI;
    GenericElement cipherData = encData.getCipherData();
    GenericElement cipherValue = cipherData.getDescendant(Constants.ENCRYPTION_SPEC_NS, "CipherValue");
    byte[] data = null;
    if (cipherValue != null) {
      data = gatherBytes(cipherValue.getDomRepresentation());//gatherText(cipherValue.getDomRepresentation()).getBytes();
    } else {
      CipherReference cipherReference = (CipherReference) cipherData.getDescendant(Constants.ENCRYPTION_SPEC_NS, "CipherReference");
      cipherReference.setTransformationFactory(trFact);
      data = cipherReference.getReferenced();
    }
    
    try {
      return decrypt1(data);
    } catch (SignatureException ex){
      throw ex; 
    } catch (Exception e) {
      throw new SignatureException("Unable to decrypt to octets:  algorithm: " +$algorithmURI+ " element: "+el, new Object[]{el, $algorithmURI}, e);
    } 
  }

  public String decryptEncData(Node n, String $algorithmURI) throws SignatureException {
    if (n instanceof Element) {
      return decryptEncData((Element)n, $algorithmURI);
    }
    if (n instanceof Document) {
      return decryptEncData(((Document) n).getDocumentElement(), $algorithmURI);
    }

    throw new SignatureException("Can decrypt Document or Element only!",new Object[]{n, $algorithmURI});
  }

  public String decryptEncData(Element el, String $algorithmURI) throws SignatureException {
    EncryptedData encData = new EncryptedData(el, true);
    encData.initializeDescendants();
    
    if (key!=null){
      encData.setKey(key);
    }
    try {
      byte[] ret= encData.getDecrypted();
      if (ret!=null){
        return new String(ret); //$JL-I18N$
      }
    } catch (Exception ex){
//    $JL-EXC$
      // do nothing - continue
    }
    
    if ($algorithmURI == null) {
      $algorithmURI = deduceAlgorithm(encData);
    }

    if ($algorithmURI == null) {
      throw new SignatureException("Unable to determine algorithm.",new Object[]{el, $algorithmURI});
    }

    this.algorithmURI = $algorithmURI;
    GenericElement cipherData = encData.getCipherData();
    GenericElement cipherValue = cipherData.getDescendant(Constants.ENCRYPTION_SPEC_NS, "CipherValue");

    if (cipherValue != null) {
      return decryptLiteral(cipherValue, $algorithmURI);
    } 
    CipherReference cipherReference = (CipherReference) cipherData.getDescendant(Constants.ENCRYPTION_SPEC_NS, "CipherReference");
    cipherReference.setTransformationFactory(trFact);
    return decryptReference(cipherReference, $algorithmURI);
  }

  private String decryptLiteral(GenericElement cipherValue, String $algorithmURI) throws SignatureException {
    return decrypt((Element) cipherValue.getDomRepresentation(), $algorithmURI);
  }

  private String decryptReference(CipherReference cipherReference, String $algorithmURI) throws SignatureException {
    try {
      byte[] data = cipherReference.getReferenced();
      return decrypt0(data);
    } catch (SignatureException e) {
      throw e;
    } catch (Exception e) {
      throw new SignatureException("Error while decrypting reference: algorithm:"+$algorithmURI, new Object[]{cipherReference, $algorithmURI},e);
    }
  }

  public String decrypt(Element parent, String $algorithmURI) throws SignatureException {
    try {
      this.algorithmURI = $algorithmURI;
      return decrypt0(gatherBytes(parent));
    } catch (SignatureException e) {
      throw e;
    } catch (Exception e) {
      throw new SignatureException("Error while decrypting: algorithm: "+ $algorithmURI,new Object[]{parent, $algorithmURI}, e);
    } 
  }

  public void restoreOriginalContent(Element encData, String $algorithmURI) throws SignatureException {
    Node parent = encData.getParentNode();
    byte[] decrypted = decryptToOctets(encData, $algorithmURI);
    String type = encData.getAttribute("Type");
    Node toAdd = null;
    if (parseDecrypted) {
      Hashtable mappings = DOM.getNamespaceMappingsInScope(parent);
      if (type == null || type.equals(Constants.CONTENT_ENCRYPTION)) {
        byte[] temp1 = new byte[decrypted.length + 7];
        temp1[0] = '<';
        temp1[1] = 'a';
        temp1[2] = '>';
        System.arraycopy(decrypted, 0, temp1, 3, decrypted.length);
        int index = temp1.length - 4;
        temp1[index++] = '<';
        temp1[index++] = '/';
        temp1[index++] = 'a';
        temp1[index++] = '>';
        Node d = SignatureContext.parse(new ByteArrayInputStream(temp1), mappings).getDocumentElement();//readStreamAsDOM(new
                                                                                                        // ByteArrayInputStream(temp1));
        Document doc = encData.getOwnerDocument();
        DocumentFragment fr = doc.createDocumentFragment();
        NodeList nl = d.getChildNodes();
        int len = nl.getLength();
        for (int i = 0; i < len; i++) {
          fr.appendChild(doc.importNode(nl.item(i).cloneNode(true), true));
        }
        toAdd = fr;
      } else if (type.equals(Constants.ELEMENT_ENCRYPTION)) {
        Document temp = SignatureContext.parse(new ByteArrayInputStream(decrypted), mappings);
        toAdd = temp.getDocumentElement();
      }
      if (parent instanceof Document) {
        toAdd = ((Document) parent).importNode(toAdd, true);
      } else {
        toAdd = parent.getOwnerDocument().importNode(toAdd, true);
      }
    } else {
      toAdd = new BinaryTextImpl((Document) (parent instanceof Document ? parent : parent.getOwnerDocument()));
      ((BinaryTextImpl) toAdd).setBinaryData(decrypted);
    }
    
    Node oldNode = getChildElementByName(parent, "EncryptedData", Constants.ENCRYPTION_SPEC_NS);
    
    Node sibling = oldNode.getNextSibling();
    parent.removeChild(oldNode);
    if (sibling != null) {
      parent.insertBefore(toAdd, sibling);
    } else {
      parent.appendChild(toAdd);
    }
  }

  public void restoreOriginalContent(Element encData) throws SignatureException {
    restoreOriginalContent(encData, null);
  }

  public static Node readStreamAsDOM(InputStream in) throws SignatureException {
    try {
      Document document = SignatureContext.getDocumentBuilderFT().parse(in);
      return document.getDocumentElement();
    } catch (Exception e) {
      throw new SignatureException("Error reading stream as DOM",new Object[]{in},e);
    }
  }

  public static Element getChildElementByName(Node n, String localName, String uri) {
    NodeList nl = n.getChildNodes();

    for (int i = 0; i < nl.getLength(); i++) {
      Node tempNode = nl.item(i);

      if (tempNode.getNodeType() == Node.ELEMENT_NODE && tempNode.getLocalName().equals(localName) && tempNode.getNamespaceURI().equals(uri)) {
        return (Element) tempNode;
      }
    } 

    return null;
  }
  
  public byte[] decryptRaw(byte[] content, String $algorithmURI) throws IOException, GeneralSecurityException, SignatureException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    algorithmURI = $algorithmURI;
    Reusable reusable = null;
    try {
      reusable = Reusable.getInstance(algorithmURI);
      CustomCipher ciph = (CustomCipher) reusable;
      int start = ciph.getIVLength();
      IV = new byte[start];
      System.arraycopy(content, 0, IV, 0, start);
      IvParameterSpec spec = new IvParameterSpec(IV);
      ciph.init(Cipher.DECRYPT_MODE, key, spec);
      return ciph.doFinal(content, start, content.length - start);
    } finally {
      if (reusable != null) {
        reusable.release();
      }
    }    
  }
}

