package com.sap.engine.lib.xsl.xpath.functions;

//import com.ibm.bsf.BSFEngine;
//import com.ibm.bsf.BSFException;
import java.lang.reflect.*;
import com.sap.engine.lib.xsl.xpath.*;
import com.sap.engine.lib.xsl.xpath.xobjects.*;

public class BSFFunction implements XFunction {

  //private BSFEngine eng = null;
  private static String bsfEngine = "com.ibm.bsf.BSFEngine";
  private static Class bsfEngineClass = null;
  private Object bsfEngineObject = null;
  private String functionName = null;

  static {
    try {
      bsfEngineClass = Class.forName(bsfEngine);
    } catch (ClassNotFoundException e) {
      //$JL-EXC$
      //It is handled below
      e.printStackTrace();
    }
  }

  //  public BSFFunction(BSFEngine eng, String name) {
  //    this.eng = eng;
  //    this.functionName = name;
  //  }
  public BSFFunction(Object eng, String name) {
    this.bsfEngineObject = eng;
    this.functionName = name;
  }

  public boolean confirmArgumentTypes(XObject[] o) {
    return true;
  }

  public String getFunctionName() {
    return functionName;
  }

  public XObject execute(XObject[] arr, XPathContext ctx) throws XPathException {
    if (arr.length < 1) {
      throw new XPathException("At least one argument required!");
    }

    try {
      Object[] obs = prepareArguments(arr);
      XObjectFactory fact = ctx.getXFactCurrent();
      Object[] args = new Object[obs.length - 1];

      if (obs.length > 1) {
        System.arraycopy(obs, 1, args, 0, obs.length - 1);
      }

      Object toReturn = null;
      if (bsfEngineClass == null) {
        throw new XPathException("Could not initialize BSF Class. Perhaps the BSF Library is not in classpath");
      }
      Method callMethod = bsfEngineClass.getMethod("call", new Class[] {Class.forName("java.lang.Object"), Class.forName("java.lang.String"), (new Object[0]).getClass()});
      toReturn = callMethod.invoke(bsfEngineObject, new Object[] {obs[0], functionName, args});
      //     return  fact.getXJavaObject("alabala");
      return fact.getXJavaObject(toReturn);
      //return fact.getXJavaObject(eng.call(obs[0], functionName, args));
    } catch (Exception e) {
      throw new XPathException(e);
    }
  }

  private Object[] prepareArguments(XObject[] arr) throws XPathException {
    int length = arr.length;
    Object[] result = new Object[length];

    for (int i = 0; i < length; i++) {
      if (null == arr[i]) {
        result[i] = null;
        continue;
      }

      switch (arr[i].getType()) {
        case XBoolean.TYPE: {
          result[i] = new Boolean(((XBoolean) arr[i]).getValue());
          break;
        }
        case XString.TYPE: {
          result[i] = ((XString) arr[i]).getValue();
          break;
        }
        case XNumber.TYPE: {
          result[i] = new Double(((XNumber) arr[i]).getValue());
          break;
        }
        case XJavaObject.TYPE: {
          result[i] = ((XJavaObject) arr[i]).getObject();
          break;
        }
        default: {
          throw new XPathException("Argument type not supported!");
        }
      }
    } 

    return result;
  }

}

