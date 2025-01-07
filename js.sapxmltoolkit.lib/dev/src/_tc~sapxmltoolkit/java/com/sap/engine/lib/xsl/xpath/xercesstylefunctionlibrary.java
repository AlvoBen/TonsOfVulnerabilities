package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xslt.XSLStylesheet;
import java.util.Hashtable;

public class XercesStyleFunctionLibrary implements FunctionLibrary {

  private Hashtable cachedClasses1 = null;
  private static final CharArray libName = new CharArray("http://xml.apache.org/xslt/java");
  private CharArray chClass1 = null;
  private ClassLoader extClassLoader = null;

  public void init(String pack) {

  }
  
  private Hashtable getCachedClasses() {
    if (cachedClasses1 == null) {
      cachedClasses1 = new Hashtable();
    }
    return cachedClasses1;
  }
  
  private CharArray getChClass() {
    if (chClass1 == null) {
      chClass1 = new CharArray();
    }
    return chClass1;
  }

  public XercesStyleFunctionLibrary(XSLStylesheet parent) {
    if (parent != null) {
      this.extClassLoader = parent.getExtClassLoader();
    }
  }

  public XFunction getFunction(CharArray cname) throws XPathException {
    String name = cname.toString();
    String className = name.substring(0, name.lastIndexOf('.'));
    String methodName = name.substring(name.lastIndexOf('.') + 1);
    getChClass().set(className);
    FunctionLibrary lib = null;

    if ((lib = (FunctionLibrary) getCachedClasses().get(getChClass())) == null) {
      try {
        lib = new JLBLibrary(className, extClassLoader);
        getCachedClasses().put(lib.getName(), lib);
      } catch (ClassNotFoundException e) {
        throw new XPathException("Could not load Extension Function Library.", e);
      }
    }

    getChClass().set(methodName);
    return lib.getFunction(getChClass());
  }

  public CharArray getName() {
    return libName;
  }

}

