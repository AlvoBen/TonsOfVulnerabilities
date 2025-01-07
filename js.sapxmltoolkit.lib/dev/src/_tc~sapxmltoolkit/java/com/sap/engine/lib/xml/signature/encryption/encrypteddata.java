package com.sap.engine.lib.xml.signature.encryption;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.lib.xml.dom.DOM;
import com.sap.engine.lib.xml.signature.SignatureContext;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.elements.GenericElement;

public class EncryptedData extends EncryptedType {

  public static final String localName = "EncryptedData";

  private boolean            replaced  = false;

  //  private EncryptionMethod encMethod = null;
  //private CipherData cipherData = null;

  //  private EncryptionProperty[] encProps = null;
  public EncryptedData(String namespaceUri, String qualifiedName, GenericElement parent) throws SignatureException {
    super(namespaceUri, qualifiedName, parent);
  }

  public EncryptedData(Node parent) throws com.sap.engine.lib.xml.signature.SignatureException {
    super(parent, "EncryptedData");
  }

  public EncryptedData(Node parent, int childIndex) throws com.sap.engine.lib.xml.signature.SignatureException {
    super(parent, "EncryptedData", childIndex);
  }

  public EncryptedData(Element n, boolean unused) throws SignatureException {
    super(n, unused);
  }

  public void replaceContent() throws SignatureException {
    if (replaced)
      return;
    byte[] decryptedOctets = getDecrypted();
    try {
      Node domParent = getDomRepresentation().getParentNode();
      String encryptionType = ((Element) getDomRepresentation()).getAttribute("Type");
      //TODO: MimeType!!!!
      Node toAdd = null;
      Hashtable mappings = DOM.getNamespaceMappingsInScope(domParent);
      if (encryptionType == null || (encryptionType.length()==0)||encryptionType.equals(Constants.CONTENT_ENCRYPTION)) {
        byte[] temp1 = new byte[decryptedOctets.length + 7];
        temp1[0] = '<';
        temp1[1] = 'a';
        temp1[2] = '>';
        System.arraycopy(decryptedOctets, 0, temp1, 3, decryptedOctets.length);
        int index = temp1.length - 4;
        temp1[index++] = '<';
        temp1[index++] = '/';
        temp1[index++] = 'a';
        temp1[index++] = '>';
//TODO: get parent namespaces!!!!!        
        Node d =  SignatureContext.parse(new ByteArrayInputStream(temp1), mappings).getDocumentElement();
        Document doc = getDomRepresentation().getOwnerDocument();
        DocumentFragment fr = doc.createDocumentFragment();
        NodeList nl = d.getChildNodes();
        int len = nl.getLength();
        for (int i = 0; i < len; i++) {
          fr.appendChild(doc.importNode(nl.item(i).cloneNode(true), true));
        }
        toAdd = fr;
      } else if (encryptionType.equals(Constants.ELEMENT_ENCRYPTION)) {
        Document temp = SignatureContext.parse(new ByteArrayInputStream(decryptedOctets), mappings);
        toAdd = temp.getDocumentElement();
      }

      if (domParent instanceof Document) {
        toAdd = ((Document) domParent).importNode(toAdd, true);
      } else {
        toAdd = domParent.getOwnerDocument().importNode(toAdd, true);
      }

      Node sibling = getDomRepresentation().getNextSibling();
      domParent.removeChild(getDomRepresentation());
      if (sibling != null) {
        domParent.insertBefore(toAdd, sibling);
      } else {
        domParent.appendChild(toAdd);
      }
      replaced = true;
    } catch (Exception e) {
      throw new SignatureException(e);
    }

  }

}
