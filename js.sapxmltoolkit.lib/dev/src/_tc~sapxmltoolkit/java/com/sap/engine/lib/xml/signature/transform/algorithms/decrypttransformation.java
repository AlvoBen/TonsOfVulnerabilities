/*
 * Created on 2004-4-7
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.transform.algorithms;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.Data;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.encryption.EncryptedData;
import com.sap.engine.lib.xml.signature.transform.Transformation;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *  
 */
public class DecryptTransformation extends Transformation {

  public DecryptTransformation(Object[] args) {
    super(args);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sap.engine.lib.xml.signature.transform.Transformation#transform(com.sap.engine.lib.xml.signature.Data)
   */
  public void transform(Data data) throws SignatureException {
    // additionalArgs - except elements
    Node n = data.getNode();
    Element el = (n instanceof Document)?((Document) n).getDocumentElement():(Element) n;
    boolean getNodeList = true;
    while ( getNodeList){
      NodeList list = el.getElementsByTagNameNS(Constants.ENCRYPTION_SPEC_NS, "EncryptedData");
      getNodeList = false;
      try {
        for (int i = 0; i < list.getLength(); i++) {
          // TODO: check if it is to be skipped!!!
          // binary - raplace!!! no nodes etc ...
          EncryptedData encData = new EncryptedData((Element) list.item(i), true);
          if (!skipDecryption(encData)){
            encData.initializeDescendants();
            encData.replaceContent();
            getNodeList = true;
          }
        }
        data.setNode(n);
      } catch (SignatureException ex1) {
        throw ex1;
      } catch (Exception ex) {
        throw new SignatureException(ex);
      }
    }

  }

  public Transformation defineFrom(GenericElement el, HashMap $dataHashmap) throws SignatureException {
    throw new SignatureException("defineFrom not implemented for standard transformation: XSLTTransformation!", new java.lang.Object[]{el, $dataHashmap});
  }
  
  
  protected boolean skipDecryption(EncryptedData data) throws Exception{

    //TODO: support Xpointer!
    Element domRepr = (Element) data.getDomRepresentation();
    
    if (additionalArgs!=null){
      String elementUri= domRepr.getAttribute("Id");
      if ((elementUri!=null)&&(elementUri.length()>0)){
        elementUri="#".concat(elementUri);
        for(int j=0;j<additionalArgs.length;j++){
         if (elementUri.equals(additionalArgs[j])){
           return true;
         }
        }
      }
    }
    return false;
  }

}
