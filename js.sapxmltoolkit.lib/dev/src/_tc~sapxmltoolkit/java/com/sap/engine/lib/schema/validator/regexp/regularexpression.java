package com.sap.engine.lib.schema.validator.regexp;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.sap.engine.lib.schema.exception.SchemaRuntimeException;

public class RegularExpression {

  private static final String SUN_REG_EXP_CLASS_NAME = "com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression";
  private static final String IBM_REG_EXP_CLASS_NAME = "org.apache.xerces.internal.impl.xpath.regex.RegularExpression";
  private static final String REG_EXP_MATCHES_METHOD_NAME = "matches";
  private static final Class[] REG_EXP_MATCHES_METHOD_PARAM_CLASSES = new Class[]{String.class};
  private static final Class[] REG_EXP_CONSTRUCTOR_PARAM_CLASSES = new Class[]{String.class, String.class}; 
  
  private Object regExp;
  private Method regExpMatchesMethod;
  
  public RegularExpression(String regExpString) {
    try {
      Class regExpClass = loadRegularExpressionClass();
      regExp = createRegularExpression(regExpClass, regExpString);
      regExpMatchesMethod = regExpClass.getDeclaredMethod(REG_EXP_MATCHES_METHOD_NAME, REG_EXP_MATCHES_METHOD_PARAM_CLASSES);
    } catch(Exception exc) {
      throw new SchemaRuntimeException(exc);
    }
  }
  
  private final Class loadRegularExpressionClass() throws ClassNotFoundException {
    try {
      return(Class.forName(SUN_REG_EXP_CLASS_NAME));
    } catch(ClassNotFoundException classNFExc) {
      return(Class.forName(IBM_REG_EXP_CLASS_NAME));
    }
  }
  
  private final Object createRegularExpression(Class _class, String regExpString) throws Exception {
    Constructor regExpConstructor = _class.getDeclaredConstructor(REG_EXP_CONSTRUCTOR_PARAM_CLASSES);
    return(regExpConstructor.newInstance(new String[]{regExpString, "X"}));
  }
  
  public final boolean matches(String value) {
    try {
      Boolean matchesResult = (Boolean)(regExpMatchesMethod.invoke(regExp, new String[]{value}));
      return(matchesResult.booleanValue());
    } catch(Exception exc) {
      throw new SchemaRuntimeException(exc);
    }
  }
}
