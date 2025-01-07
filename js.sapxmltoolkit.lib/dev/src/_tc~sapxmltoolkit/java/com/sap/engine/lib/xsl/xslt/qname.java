package com.sap.engine.lib.xsl.xslt;

import com.sap.engine.lib.xml.parser.helpers.CharArray;

public final class QName {

  public CharArray rawname = new CharArray();
  public CharArray localname = new CharArray();
  public CharArray prefix = new CharArray();
  public CharArray uri = null;
  public int uriId = -1;

  
  public QName reuse(QName q) {
    rawname.set(q.rawname);
    localname.set(q.localname);
    prefix.set(q.prefix);
    uri = q.uri;
    uriId = q.uriId;
    return this;
    
  }
  public QName reuse(CharArray ch, CharArray uri, int uriId) {
    reuse(ch);
    this.uri = uri;
    this.uriId = uriId;
    return this;
  }

  public void initFromRawname() {
    localname.clear();
    prefix.clear();
    int sep = rawname.indexOfColon();

    if (sep == -1) {
      sep = 0;
    }

    prefix.substring(rawname, 0, sep);

    if (sep > 0) {
      sep++;
    }

    localname.substring(rawname, sep);
  }
  
  public QName reuse(CharArray ch) {
    rawname.set(ch);
    initFromRawname();
    return this;
  }

  public QName reuse(String ch, CharArray uri, int uriId) {
    reuse(ch);
    this.uri = uri;
    this.uriId = uriId;
    return this;
  }

  public QName reuse(String ch) {
    rawname.set(ch);
    initFromRawname();
    return this;
  }

  public void setURI(String u, NamespaceManager nsmanager) {
    uriId = nsmanager.put(u);
    uri = nsmanager.get(uriId);
  }

  public void setURI(CharArray u, NamespaceManager nsmanager) {
    uriId = nsmanager.put(u);
    uri = nsmanager.get(uriId);
  }

  public CharArray getRawName() {
    return rawname;
  }

  public CharArray getlocalname() {
    return localname;
  }

  public CharArray getPrefix() {
    return prefix;
  }

  public String toString() {
    return rawname.getString();
  }

  public String print() {
    return "rawname=" + rawname + " prefix=" + prefix + " localname=" + localname + " uri=" + uri;
  }

  public boolean equals(Object o) {
    if (o instanceof QName) {
      QName q = (QName)o;
      if (q.localname == localname && q.prefix == q.prefix && q.rawname == rawname && q.uri == uri) {
        return true;
      } else {
        if (q.localname.equals(localname) && q.uri.equals(uri)) {
          return true;
        }
      }
    }

    return false;
  }

  public int hashCode() {
    int result = rawname.hashCode()+1;
    result *= localname.hashCode()+1;
    result *= prefix.hashCode()+1;
    return result; 
  }
}

