package com.sap.engine.lib.xml.dom;

import org.w3c.dom.*;
@Deprecated
public class TextImpl extends CharacterDataImpl implements Text {

  public TextImpl() {

  }

  protected TextImpl(Document owner) {
    setOwnerDocument(owner);
  }

  public Node cloneNode(boolean deep) {
    TextImpl result = new TextImpl(getOwnerDocument());
    result.init(data, null);
    result.setOwnerDocument(getOwnerDocument());
    return result;
  }

  public String getNodeName() {
    return "#text";
  }

  public short getNodeType() {
    return TEXT_NODE;
  }

  public Text splitText(int offset) {
    if ((offset < 0) || (offset > this.data.length())) {
      throw new DOMException(DOMException.INDEX_SIZE_ERR, "Offset given to split text is out of bounds.");
    }

    TextImpl other = new TextImpl();
    other.init(this.data.substring(offset), null);
    this.data = this.data.substring(0, offset);

    if (parent != null) {
      parent.insertBefore(other, this.getNextSibling());
    }

    return other;
  }

  static public boolean isWhiteSpaceChar(char ch) {
    if (ch == 0x20 || ch == 0xD || ch == 0xA || ch == 0x9) {
      return true;
    } else {
      return false;
    }
  }

  public void trimWhiteSpace() {
    int b, e;
    //LogWriter.getSystemLogWriter().println("data----" + data + "----" + data.length());
    if (data == null) {
      return;
    }
    for (b = 0; b < data.length() && isWhiteSpaceChar(data.charAt(b)); b++) {
      ; 
    }
    //LogWriter.getSystemLogWriter().println("b---- " + b + " ----");
    for (e = data.length() - 1; e > 0 && e > b && isWhiteSpaceChar(data.charAt(e)); e--) {
      ; 
    }
    //LogWriter.getSystemLogWriter().println("e---- " + e + " ----");
    data = data.substring(b, e + 1);
  }

  static public boolean isWhiteSpace(String str) {
    if (str == null) {
      return false;
    } else {
      for (int i = 0; i < str.length(); i++) {
        if (!isWhiteSpaceChar(str.charAt(i))) {
          return false;
        }
      } 
    }

    return true;
  }

  public boolean isWhiteSpace() {
    if (data == null) {
      return false;
    } else {
      for (int i = 0; i < data.length(); i++) {
        if (!isWhiteSpaceChar(data.charAt(i))) {
          return false;
        }
      } 
    }

    return true;
  }

  public String toString() {
    return getNodeValue();
  }
  
  public boolean isElementContentWhitespace(){
  	throw new NullPointerException("Not implemented!");
  }

  
  public String getWholeText(){
  	throw new NullPointerException("Not implemented!");
  }

  public Text replaceWholeText(String content) throws DOMException{
  	throw new NullPointerException("Not implemented!");
  }
  
  
  
  

}

