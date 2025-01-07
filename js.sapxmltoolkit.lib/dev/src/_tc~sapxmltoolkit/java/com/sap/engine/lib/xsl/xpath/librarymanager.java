// Decompiled by Jad v1.5.5.3. Copyright 1997-98 Pavel Kouznetsov.
// Jad home page:      http://web.unicom.com.cy/~kpd/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   LibraryManager.java
package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xslt.QName;
import com.sap.engine.lib.xsl.xslt.XSLStylesheet;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;

// Referenced classes of package com.sap.engine.lib.xsl.xpath:
//            CoreFunctionLibrary, XercesStyleFunctionLibrary, FunctionLibrary, JLBLibrary, 
//            XPathException, XFunction
public class LibraryManager {

  private Hashtable libraries;
  private CharArray crtmp;
  private XSLStylesheet parent;
  private static CharArray crJAVA = new CharArray("java:").setStatic();
  private static CharArray crXALAN = new CharArray("xalan:").setStatic();

  public LibraryManager() {
    this(null);
  }

  public LibraryManager(XSLStylesheet parent) {
    libraries = new Hashtable();
    crtmp = new CharArray();
    this.parent = parent;
    registerLibrary(new CoreFunctionLibrary());
    registerLibrary(new XercesStyleFunctionLibrary(parent));
    this.parent = parent;
  }

  public void reuse() {
    libraries.clear();
    registerLibrary(new CoreFunctionLibrary());
    registerLibrary(new XercesStyleFunctionLibrary(parent));
  }

  public void registerLibrary(FunctionLibrary lib) {
    libraries.put(lib.getName(), lib);
  }

  public FunctionLibrary getLibrary(CharArray name) {
    if (name == null || name.length() == 0) {
      name = CoreFunctionLibrary.NAME;
    }
    return (FunctionLibrary) libraries.get(name);
  }

  public XFunction getFunction(QName name) throws XPathException {
    FunctionLibrary lib = getLibrary(name.uri);
    if (lib == null) {
      lib = getLibrary(checkClass(name.uri));
    }

    if (lib == null) {
      try {
        lib = new JLBLibrary(name.uri.toString(), parent.getExtClassLoader());
        registerLibrary(lib);
      } catch (ClassNotFoundException e) {
        throw new XPathException("Could not load class: " + name.uri + " required for extension library.", e);
      }
    }

    return lib.getFunction(name.localname);
  }

  public JLBLibrary getByClassName(String className) {
    for (Enumeration enum1 = libraries.elements(); enum1.hasMoreElements();) {
      Object o = enum1.nextElement();
      if ((o instanceof JLBLibrary) && ((JLBLibrary) o).getClassName().equals(className)) {
        return (JLBLibrary) o;
      }
    } 

    try {
      ClassLoader cl = null;
      if (parent != null) {
        cl = parent.getExtClassLoader();
      }
      JLBLibrary lib = new JLBLibrary(className, cl);
      registerLibrary(lib);
      return lib;
    } catch (ClassNotFoundException classnotfoundexception) {
      //$JL-EXC$
      return null;
    }
  }

  public CharArray checkClass(CharArray cl) {
    crtmp.set(cl);
    if (cl.startsWith(crJAVA)) {
      crtmp.substring(cl, 5);
    } else if (cl.startsWith(crXALAN)) {
      crtmp.substring(cl, 6);
    } else {
      crtmp.set(cl);
    }
    for (; crtmp.length() > 0 && crtmp.charAt(0) == '/'; crtmp.substring(crtmp, 1)) {
      ; 
    }
    return crtmp;
  }

}

