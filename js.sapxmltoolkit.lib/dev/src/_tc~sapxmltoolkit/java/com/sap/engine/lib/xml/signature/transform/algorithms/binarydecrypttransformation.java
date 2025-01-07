/*
 * Created on 2004-4-15
 *
 *@author Alexander Alexandrov, e-mail:aleksandar.aleksandrov@sap.com
 */
package com.sap.engine.lib.xml.signature.transform.algorithms;

import java.io.ByteArrayOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.Data;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.encryption.EncryptedData;

/**
 * @author Alexander Alexandrov, e-mail: aleksandar.aleksandrov@sap.com
 *
 */
public class BinaryDecryptTransformation extends DecryptTransformation {

  /**
   * @param args
   */
  public BinaryDecryptTransformation(Object[] args) {
    super(args);
    // TODO Auto-generated constructor stub
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
    NodeList list = el.getElementsByTagNameNS(Constants.ENCRYPTION_SPEC_NS, "EncryptedData");
    ByteArrayOutputStream bos = SignatureContext.getByteArrayOutputStreamPool().getInstance();
    try {
      for (int i = 0; i < list.getLength(); i++) {
        // TODO: check if it is to be skipped!!!
        // binary - raplace!!! no nodes etc ...
        EncryptedData encData = new EncryptedData((Element) list.item(i), true);
        if (!skipDecryption(encData)){
          encData.initializeDescendants();
          bos.write(encData.getDecrypted());
        }
      }
      data.setOctets(bos.toByteArray());
    } catch (SignatureException ex1) {
      throw ex1;
    } catch (Exception ex) {
      throw new SignatureException(ex);
    }

  }


}
