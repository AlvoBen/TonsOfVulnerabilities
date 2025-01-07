package com.sap.engine.lib.xml.parser.helpers;

import java.io.*;
import java.net.*;
import com.sap.engine.lib.xml.parser.URLLoaderBase;

/**
 * Class description -
 *
 * @author Vladimir Savtchenko
 * @version 1.00
 */
public final class Entity {

  private CharArray name = null;
  private CharArray publicId = null;
  private CharArray systemId = null;
  private CharArray value = null;
  private boolean pe;
  private CharArray note = null;
  //private URL url = null;
  private String urlString = null;
  private URLLoaderBase urlLoader = null;

  //private final static CharArray caEmpty = new CharArray();
  public Entity(CharArray name, CharArray value, boolean pe, URLLoaderBase urlLoader) {
    this.name = name.copy();
    this.value = value.copy();
    this.pe = pe;
    this.urlLoader = urlLoader;
  }

  public Entity(CharArray name, CharArray entvalue, boolean pe, CharArray publicId, CharArray systemId, CharArray note, URL baseURL, URLLoaderBase urlLoader) throws FileNotFoundException, IOException {
    this.name = name.copy();
    this.value = entvalue.copy();
    this.pe = pe;
    this.systemId = systemId.copy();
    this.publicId = publicId.copy();
    this.note = note.copy();
    this.urlString = isInternal() ? null : systemId.toString();
    this.urlLoader = urlLoader;
  }

  public CharArray getName() {
    return name;
  }

  public CharArray getPub() {
    return (publicId == null) ? CharArray.EMPTY : publicId;
  }

  public CharArray getSys() {
    return (systemId == null) ? CharArray.EMPTY : systemId;
  }

  public CharArray getValue() throws Exception {
    if (isInternal()) {
      if (urlLoader != null) {
        urlLoader.pushTheSame();
      }

      return value;
    }

    //urlLoader.printStack();
    InputStream in = urlLoader.loadAndPush(urlString).openStream();
    int a;
    value.clear();

    while (true) {
      a = in.read();

      if (a == -1) {
        break;
      }

      value.append((char) a);
    }

    in.close();
    return value;
  }

  public CharArray getNote() {
    return note;
  }

  public boolean isUnparsed() {
    //LogWriter.getSystemLogWriter().println("Entitiy.isUnparsed: note = " + note);
    return (note == null || note.length() == 0) ? false : true;
  }

  public boolean isPE() {
    return pe;
  }

  public boolean isInternal() {
    return (getSys().getSize() == 0) ? true : false;
  }

  public boolean isPredefined() {
    if (value.getSize() != 1) {
      return false;
    }
    if (value.charAt(0) == '>' || value.charAt(0) == '<' || value.charAt(0) == '\'' || value.charAt(0) == '\"' || value.charAt(0) == '&') {
      return true;
    }
    return false;
  }

  //  public String resolve() {
  //    return "";
  //  }

}

