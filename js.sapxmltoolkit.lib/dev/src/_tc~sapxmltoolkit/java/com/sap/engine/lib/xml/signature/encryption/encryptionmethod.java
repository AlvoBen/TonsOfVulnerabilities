package com.sap.engine.lib.xml.signature.encryption;

import java.math.BigInteger;

import org.w3c.dom.Element;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;

public class EncryptionMethod extends GenericElement {

  public String algorithmURI = null;
  public BigInteger size = null;

  //  public EncryptionMethod(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
  //    super(parent.getOwner(), namespaceUri, qualifiedName, parent);  
  //  } 
  public EncryptionMethod(GenericElement parent) throws SignatureException {
    super(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX.concat("EncryptionMethod"), parent);
  }

  public EncryptionMethod(String algorithmURI, BigInteger size, GenericElement parent) throws SignatureException {
    super(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX.concat("EncryptionMethod"), parent);
    this.algorithmURI = algorithmURI;
    this.size = size;
  }
  
  public EncryptionMethod(Element domRepr, GenericElement parent) throws SignatureException{
    super(domRepr, parent);
  }

  public void setAlgorithmURI(String algorithmURI) {
    this.algorithmURI = algorithmURI;
  }
  
  public String getAlgorithmURI() throws SignatureException{
    if(algorithmURI==null){
      algorithmURI = domRepresentation.getAttribute("Algorithm");
      if (algorithmURI==null){
        throw new SignatureException("Algorithm attribute is required",new Object[]{domRepresentation});
      }
    }
    return algorithmURI;
  }

  public BigInteger getKeySize() throws SignatureException{
    if (size==null){
      String sizeRepr = domRepresentation.getAttribute("Algorithm");
      try {
        if (sizeRepr!=null){
          size = new BigInteger(sizeRepr);
        }
      } catch (Exception ex){
        throw new SignatureException("Unable to parse key size",new Object[]{domRepresentation},ex);
      }
    }
    return size;
  }
  public void construct() throws SignatureException {
    setAttribute("Algorithm", algorithmURI);

    if (size != null) {
      GenericElement keySize = new GenericElement(null, "KeySize", this);
      keySize.appendTextChild(size.toString());
    }
  }

}

