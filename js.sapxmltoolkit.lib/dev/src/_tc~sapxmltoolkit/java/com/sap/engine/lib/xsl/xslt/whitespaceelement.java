package com.sap.engine.lib.xsl.xslt;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

public final class WhiteSpaceElement {

  private QName element = new QName();
  private boolean preserve = true;

  public WhiteSpaceElement(CharArray name, boolean preserve) {
    reuse(name, preserve);
  }

  public WhiteSpaceElement(String name, boolean preserve) {
    reuse(name, preserve);
  }

  public WhiteSpaceElement reuse(CharArray name, boolean preserve) {
    this.element.reuse(name);
    this.preserve = preserve;
    return this;
  }

  public WhiteSpaceElement reuse(String name, boolean preserve) {
    this.element.reuse(name);
    this.preserve = preserve;
    return this;
  }

  public boolean isPreserve() {
    return preserve;
  }

  public QName getElement() {
    return element;
  }

  public boolean match(CharArray name) {
    if (element.getRawName().charAt(0) == '*') {
      return true;
    }

    return element.getRawName().equals(name);
  }

}

