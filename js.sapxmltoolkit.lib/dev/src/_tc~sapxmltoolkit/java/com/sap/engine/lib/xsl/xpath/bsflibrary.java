package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xml.util.NestedIOException;
import com.sap.engine.lib.xsl.xpath.functions.BSFFunction;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//import com.ibm.bsf.BSFManager;
//import com.ibm.bsf.BSFException;
//import com.ibm.bsf.BSFEngine;
public class BSFLibrary implements FunctionLibrary {

  private CharArray classNameOrg = new CharArray();
  private static String bsfManager = "com.ibm.bsf.BSFManager";
  private static String bsfException = "com.ibm.bsf.BSFException";
  private static String bsfEngine = "com.ibm.bsf.BSFEngine";
  private static Class bsfManagerClass = null;
  private static Class bsfExceptionClass = null;
  private static Class bsfEngineClass = null;

  static {
    try {
      bsfManagerClass = Class.forName(bsfManager);
      bsfExceptionClass = Class.forName(bsfException);
      bsfEngineClass = Class.forName(bsfEngine);
    } catch (ClassNotFoundException e) {
      //$JL-EXC$
      LogWriter.getSystemLogWriter().println("At least one class for BSF not found!");
      e.printStackTrace();
    }
  }

  private String language = null;
  private String script = null;
  //  private BSFManager bsfman;
  //  private BSFEngine eng;
  private Object bsfManagerObject = null;
  private Object bsfEngineObject = null;

  public BSFLibrary(String name, String lang, String scriptSource) throws XPathException {
    if (name != null) {
      this.classNameOrg.set(name);
    } else {
      this.classNameOrg.set(lang);
    }

    getReady(lang, scriptSource);
  }

  public BSFLibrary(String name, String language, File f) throws XPathException, ClassNotFoundException, IOException {
    if (name != null) {
      this.classNameOrg.set(name);
    } else {
      this.classNameOrg.set(language);
    }

    try {
      if (language == null) {
        Method m = bsfManagerClass.getMethod("getLangFromFilename", new Class[] {Class.forName("java.lang.String")});
        this.language = (String) m.invoke(null, new Object[] {new String(f.getName())});
        //this.language = BSFManager.getLangFromFilename(f.getName());
      } else {
        this.language = language;
      }

      StringBuffer result = new StringBuffer();
      FileReader f1 = new FileReader(f);
      char c;

      while ((c = (char) f1.read()) != (char) -1) {
        result.append(c);
      }

      f1.close();
      getReady(this.language, result.toString());
    } catch (IOException e) {
      throw e;
    } catch (NoSuchMethodException e) {
      throw new NestedIOException(e);
    } catch (IllegalAccessException e) {
      throw new NestedIOException(e);
    } catch (InvocationTargetException e) {
      throw new NestedIOException(e);
    }
  }

  private void getReady(String lang, String scriptSource) throws XPathException {
    this.language = language;
    this.script = script;
    try {
      bsfManagerObject = bsfManagerClass.newInstance();
      //    bsfman = new BSFManager();
      Boolean isLanguageRegistered = (Boolean) bsfManagerClass.getMethod("isLanguageRegistered", new Class[] {Class.forName("java.lang.String")}).invoke(null, new Object[] {new String(lang)});

      //        if(!bsfman.isLanguageRegistered(lang))
      //          throw new BSFException("Language " + lang + " is not registered!");
      if (!isLanguageRegistered.booleanValue()) {
        throw new XPathException("Language " + lang + " is not registered!");
      }

      bsfEngineObject = bsfManagerClass.getMethod("loadScriptingEngine", new Class[] {Class.forName("java.lang.String")}).invoke(bsfManagerObject, new Object[] {new String(lang)});
      //        eng = bsfman.loadScriptingEngine(lang);
      compileSource(scriptSource);
    } catch (XPathException e) {
      throw new XPathException(e);
    } catch (InvocationTargetException e) {
      throw new XPathException(e);
    } catch (Exception e) {
      throw new XPathException(e);
    }
  }

  public void init(String str) {
    return;
  }

  private void compileSource(String ScriptSource) throws XPathException {
    try {
      bsfEngineClass.getMethod("exec", new Class[] {Class.forName("java.lang.String"), int.class, int.class, Class.forName("java.lang.Object")}).invoke(bsfEngineObject, new Object[] {"", new Integer("-1"), new Integer("-1"), ScriptSource});
      //eng.exec("", -1, -1, ScriptSource);
    } catch (Exception e) {
      throw new XPathException(e);
    }
  }

  public XFunction getFunction(CharArray method) throws XPathException {
    //return new BSFFunction(eng, method.toString());
    return new BSFFunction(bsfEngineObject, method.toString());
  }

  public CharArray getName() {
    return classNameOrg;
  }

}

