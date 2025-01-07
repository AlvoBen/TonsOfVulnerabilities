/*
 * Created on 2004-5-18
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.sap.engine.lib.xml.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author vladimir-s
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * @deprecated Use org.w3c.dom.* official API.
 */
@Deprecated
public class BinaryTextImpl extends TextImpl implements Text {
  public static final short TYPE = 3411;
  
  private byte[] binaryData;
  
  public BinaryTextImpl() {
  }

  public BinaryTextImpl(Document owner) {
    setOwnerDocument(owner);
    
  }



  /**
   * @return
   */
  public byte[] getBinaryData() {
    return binaryData;
  }

  /**
   * @param bs
   */
  public void setBinaryData(byte[] bs) {
    binaryData = bs;
  }

  /* (non-Javadoc)
   * @see org.w3c.dom.Node#getNodeType()
   */
  public short getNodeType() {
    // TODO Auto-generated method stub
    return TYPE;
  }

}
