package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xml.util.FileClassLoader;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.functions.JLBFunction;
import java.io.File;
import java.util.StringTokenizer;

public class JLBLibrary implements FunctionLibrary {

  private CharArray classNameOrg = new CharArray();
  private CharArray classNameStrip = new CharArray();
  private String classPath = null;
  private FileClassLoader fileClassLoader = null;
  private Class userClass = null;

  public JLBLibrary(String className) throws ClassNotFoundException {
    this(className, null);
  }

  public JLBLibrary(String className, ClassLoader extClassLoader) throws ClassNotFoundException {
    this.classNameOrg.set(className);
    this.classNameStrip.set(checkClass(className));
    try {
      userClass = getClass().forName(checkClass(className));
    } catch (ClassNotFoundException e) {
      //$JL-EXC$ In case the first invocation fails, try using the external ClassLoader
      try {
        userClass = Thread.currentThread().getContextClassLoader().loadClass(checkClass(className));
      } catch (ClassNotFoundException e1) {
        //$JL-EXC$ In case the first invocation fails, try using the external ClassLoader
        if (extClassLoader != null) {
          userClass = extClassLoader.loadClass(checkClass(className));
        }
      }
    }
  }

  public JLBLibrary(String name, String className, String classPath) throws ClassNotFoundException {
    this(name, className, classPath, null);
  }

  public JLBLibrary(String name, String className, String classPath, ClassLoader extClassLoader) throws ClassNotFoundException {
    if (name == null) {
      this.classNameOrg.set(className);
      this.classNameStrip.set(checkClass(className));
    } else {
      this.classNameOrg.set(name);
      this.classNameStrip.set(checkClass(name));
    }

    //    LogWriter.getSystemLogWriter().println("JLBLibrary.<init>: classNameOrg=" + classNameOrg + ", classNameStrip=" + classNameStrip);
    this.classPath = classPath;
    fileClassLoader = new FileClassLoader(getClass().getClassLoader(), "");

    if (classPath.length() > 0) {
      fileClassLoader.addMixed(prepareFiles(classPath));
      try {
        userClass = fileClassLoader.loadClass(checkClass(className));
      } catch (ClassNotFoundException e) {
        //$JL-EXC$ In case the first invocation fails, try using the external ClassLoader
        try {
          userClass = Thread.currentThread().getContextClassLoader().loadClass(checkClass(className));
        } catch (ClassNotFoundException e1) {
          //$JL-EXC$ In case the first invocation fails, try using the external ClassLoader
          if (extClassLoader != null) {
            userClass = extClassLoader.loadClass(checkClass(className));
          }
        }
      }
    } else {
      try {
        userClass = getClass().forName(checkClass(className));
      } catch (ClassNotFoundException e) {
        //$JL-EXC$ In case the first invocation fails, try using the external ClassLoader
        try {
          userClass = Thread.currentThread().getContextClassLoader().loadClass(checkClass(className));
        } catch (ClassNotFoundException e1) {
          //$JL-EXC$ In case the first invocation fails, try using the external ClassLoader
          if (extClassLoader != null) {
            userClass = extClassLoader.loadClass(checkClass(className));
          }
        }
      }
    }
  }

  public String checkClass(String cl) {
    if (cl.startsWith("java:")) {
      cl = cl.substring(5);
    } else if (cl.startsWith("xalan:")) {
      cl = cl.substring(6);
    }

    while (cl.length() > 0 && cl.charAt(0) == '/') {
      cl = cl.substring(1);
    }

    return cl;
  }

  public XFunction getFunction(CharArray method) throws XPathException {
    if (userClass == null) {
      throw new XPathException("Could not load class: " + classNameOrg + " required for execution of '" + method + "'");
    }

    return new JLBFunction(userClass, method.toString());
  }

  public void init(String className) {
    this.classNameOrg.set(className);
  }

  public CharArray getName() {
    return classNameOrg;
  }

  public CharArray getClassName() {
    return classNameStrip;
  }

  public void setUserClass(Class cl) {
    userClass = cl;
  }

  private File[] prepareFiles(String path) {
    StringTokenizer st = new StringTokenizer(path, "\n\t \r,;");
    File[] result = new File[st.countTokens()];
    File f = null;

    for (int i = 0; i < st.countTokens(); i++) {
      f = new File((String) st.nextElement());
      result[i] = f;
    } 

    return result;
  }

}

