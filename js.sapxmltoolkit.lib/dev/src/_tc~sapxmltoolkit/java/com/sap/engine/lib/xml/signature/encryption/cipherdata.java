package com.sap.engine.lib.xml.signature.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.transform.Transformation;
import com.sap.engine.lib.xml.util.BASE64Decoder;

public class CipherData extends GenericElement {

  //  private CipherReference reference = null;
  private String cipherValue = null;
//  private Transformation[] transforms = null;
  protected static final String localName = "CipherData";

  //  public CipherData(Node parent) throws com.sap.engine.lib.xml.signature.SignatureException {
  //    super(parent.getOwnerDocument(), Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX + CipherData.localName, null);
  //    parent.appendChild(this.domRepresentation);
  //  }
  public CipherData(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(parent.getOwner(), namespaceUri, qualifiedName, parent);
  }

  public CipherData(GenericElement parent) throws SignatureException {
    super(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX.concat("CipherData"), parent);
  }
  
  public CipherData(Element domRepr, GenericElement parent) throws SignatureException{
    super(domRepr, parent);
  }

  public void setCipherValue(String cipherValue) {
    this.cipherValue = cipherValue;
  }

  public void setReference(String reference, Transformation[] transforms) {
    if (transforms == null) {
      transforms = new Transformation[0];
    }

//    this.transforms = transforms;
  }

  public void construct() throws SignatureException {
    if (cipherValue != null) {
      GenericElement cVal = new GenericElement(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX.concat("CipherValue"), this);
      cVal.appendTextChild(cipherValue);
    } else if (value!=null){
      GenericElement cVal = new GenericElement(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX.concat("CipherValue"), this);
      cVal.appendBinaryTextChild(value);      
    }
  }
  
  private byte[] value = null;
  
  private int len = 0;
   int getLen(){
     return len;
   }
  
  boolean isEncoded = true;
  byte[] getCipherValue() throws SignatureException{
    if (value!=null){
      if (isEncoded){
//        value = BASE64Decoder.decode(value);
        base64Decode(value);
        isEncoded = false;
      }
      return value;
    }
    if (cipherValue!=null){
      value=BASE64Decoder.decode(cipherValue.getBytes()); //$JL-I18N$
      len = value.length;
      isEncoded = false;
      return value;
    }
    NodeList n = domRepresentation.getElementsByTagNameNS(Constants.ENCRYPTION_SPEC_NS, "CipherValue");
    if (n!=null && n.getLength()!=0){
      Element vEl = (Element) n.item(0);
      value = XMLCryptor.gatherBytes(vEl);//XMLCryptor.gatherText(vEl);
      //value = BASE64Decoder.decode(value);
      base64Decode(value);
      
    } else {
      CipherReference cr = (CipherReference) getDescendant(Constants.ENCRYPTION_SPEC_NS, "CipherReference");
      value = cr.getReferenced();
      len = value.length;
    }
    isEncoded = false;
    return value;
  }

  void base64Decode(byte[] v) throws SignatureException{
    try {
      if (v.length == 0) {
        len = 0;
        return;
      }
      InputStream is = new ByteArrayInputStream(v);
      BASE64Decoder dec = new BASE64Decoder(is);
      int temp;
      len = 0;
      while ((temp = dec.read(v, len, v.length)) != -1) {
        len += temp;
      }
      dec.close();
    } catch (IOException ex){
      throw new SignatureException(ex);
    }
  }
  
  
  /**
   * @return
   */
  public byte[] getValue() {
    return value;
  }

  /**
   * @param bs
   */
  public void setValue(byte[] bs) {
    isEncoded = true;
    value = bs;
  }

}

