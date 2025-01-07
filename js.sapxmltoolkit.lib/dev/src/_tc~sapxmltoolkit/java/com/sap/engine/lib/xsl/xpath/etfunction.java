package com.sap.engine.lib.xsl.xpath;

import java.util.Vector;

import com.sap.engine.lib.xsl.xpath.xobjects.XBoolean;
import com.sap.engine.lib.xsl.xpath.xobjects.XJavaObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xslt.QName;

/**
 * Represents an XPath function call, i.e. the <b>()</b> operator.
 * Note that this is different from XFunction which is rather the
 * function itself, the mechanism of its execution, and not part of the
 * expression tree.
 *
 * @see ETItem
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class ETFunction implements ETItem {

  protected QName name;
  protected ETItem[] arguments;
  protected XObject[] argumentsToSend;
  protected XFunction xf = null;
  protected boolean isExtensionFunction = false;
//  private static final XObject[] EMPTY_XOBJECT_ARRAY = new XObject[0];
  private static final ETItem[] EMPTY_ETITEM_ARRAY = new ETItem[0];

  protected ETFunction(QName name) { // Must supply a name
    this.name = name;
    checkForExtensionFunction();
    arguments = new ETItem[0];
    argumentsToSend = new XObject[0];
  }

  protected ETFunction(QName name, ETItem a1) {
    this.name = name;
    checkForExtensionFunction();
    arguments = new ETItem[1];
    argumentsToSend = new XObject[1];
    arguments[0] = a1;
  }

  protected ETFunction(QName name, ETItem a1, ETItem a2) {
    this.name = name;
    checkForExtensionFunction();
    arguments = new ETItem[2];
    argumentsToSend = new XObject[2];
    arguments[0] = a1;
    arguments[1] = a2;
  }

  protected ETFunction(QName name, Vector argumentsVector) {
    this.name = name;
    checkForExtensionFunction();
    this.arguments = (ETItem[]) argumentsVector.toArray(EMPTY_ETITEM_ARRAY);
    this.argumentsToSend = new XObject[this.arguments.length];
  }

  public String toString() {
    return "ETFunction('" + name + "' with " + arguments.length + " arguments)";
  }

  public void print(int indent) {
    Symbols.printSpace(indent);
 //   System.out.println(toString());

    for (int i = 0; i < arguments.length; i++) {
      ((ETItem) arguments[i]).print(indent + 1);
    } 
  }

  public XObject evaluate(XPathContext context) throws XPathException {
    //    if (isExtensionFunction) {
    //      return context.xfactCurrent.getXStringEmpty();
    //    }
    //    System.out.println("-- " + name + " --  ETFucntion  EVALUATING --");
    for (int i = 0; i < arguments.length; i++) {
      //      if (arguments[i] instanceof ETVariableReference) {
      //        System.out.println("-- " + name + " --  ETFucntion ARGS[" + i + "]  hash=" + arguments[i].hashCode() + "  with name:" + ((ETVariableReference)arguments[i]).name);
      //      }
      argumentsToSend[i] = arguments[i].evaluate(context);
      if (argumentsToSend[i].getType() == XJavaObject.TYPE) {
        ((XJavaObject) argumentsToSend[i]).setContext(context);
      }
      //      System.out.println("Argument: "+ i + ", type=" + arguments[i].getClass() + ", toSend=" + argumentsToSend[i].getClass());
      //      System.out.println("-- " + name + " --  ETFucntion ARGSTOSEND[" + i + "]  hash=" + argumentsToSend[i].hashCode() + "  length= " + argumentsToSend[i].toXString().getValue().length() + " value= " + argumentsToSend[i].toXString());
      //      if (argumentsToSend[i].toXString().getValue().equals("//URI//urn:sap-com:ai:proto2:WorldTypes//PREFIX//com_sap_ns5//END//URI//urn:sap-com:ai:proto2:WorldTypes//PREFIX//com_sap_ns6//END")) {
      //        System.out.println("MY BABY !");
      //      }
    } 

    //    System.out.println("ETFucniton: name: " + name);
    //System.out.println("ETFunction: Context library is: " + context.library);
    if (xf == null) {
      
      xf = (XFunction) context.library.getFunction(name);
      if (xf == null) {
        throw new XPathException("Function with name '" + name + "' not found in context library.");
      }
    }

    //    System.out.println("ETFunction.evaluate. before confirmArgumetTypes:" + name);
    if (!xf.confirmArgumentTypes(argumentsToSend)) {
      throw new XPathException("Illegal number of arguments or types of arguments in a call of function '" + name + "'.");
    }

    //    System.out.println("ETFunction.evaluate. before evaluate");
    XObject xx = xf.execute(argumentsToSend, context);
    //System.out.println("ETFunction Evaluated to " + xx.getClass().getName());

    /*
     * CSN: E-0000728071 2006 Reason: fix Desc: sapxmltoolkit - When in ET Function XJavaObjects are sent to function-available, the evaluation step just returns the same object. And it is later reused

       Optional
       Add'tl description: In this case - when a second time the evaluation of function-available, is executed, the object is already null, and the check fails with a NPE

       Customer related info: Problems when wrapping an Extension function execution with a function-available tag. If the ext function returns "null". The next time the fragment is executed, the execution fails with NullPointerException at function-available.

       Responsibles: Vladimir Savchenko
     */
    for (int i = 0; i < arguments.length; i++) {
      if (xx != argumentsToSend[i] && argumentsToSend[i] != arguments[i]) {
        context.getXFactCurrent().releaseXObject(argumentsToSend[i]);
      }
    } 

    //    System.out.println("ETFunction.evaluate. after evaluate");
    //Debug.check(xx);
    //    System.out.println("ETFucntion rezil hash: " + xx.hashCode());
    //    System.out.println("-- " + name + " --  ETFucntion RESULT:  hash=" + xx.hashCode() + "  length= " + xx.toXString().getValue().length() +   "   value = " + xx.toXString());
    //System.out.println("ETFunction (" + name + ") rezil to string:" + xx.toXString());
    return xx;
  }

//  private static final String stroke = "|";

  //this method tries to match a function for some specific setups
  //e.g @name="asdsa" - this is easy to match, while
  // position() = 3 is not so easy, as it requires that the parent context is also assumed
  // e.g/ /segment[position()=3] requres that the posistion is evaluated
  /*
  public int fastMatch(XPathContext context) throws XPathException {
    //this IF will check only situations of @xxx = 'asda' for ext csn 1015865 2006
    
    if ((name.localname.equals("=") || name.localname.equals("!="))  && arguments.length == 2
           && arguments[0] instanceof ETLocationStep 
           && ( 
                 ((ETLocationStep)arguments[0]).axisType == ETLocationStep.AXIS_ATTRIBUTE
               ||((ETLocationStep)arguments[0]).axisType == ETLocationStep.AXIS_CHILD
               )
           && arguments[1] instanceof XObject) {
      XObject x = evaluate(context);
      return ((XBoolean)x).getValue() ? ETItem.RES_TRUE : ETItem.RES_FALSE;
    }
    
    return ETItem.RES_UNDEFINED;
    //    if (xf instanceof XFOperatorStroke) {
    //      ETItem[] a = argumentsToSend;
    //      for (int i = 0; i < arguments.length; i++) {
    //         switch (((ETItem)arguments[i]).match(context)) {
    //           case ETItem.RES_TRUE:
    //             return RES_TRUE;
    //           case ETItem.RES_UNDEFINED: return RES_UNDEFINED;
    //           case ETItem.RES_FALSE: break;
    //           default: break;
    //         }
    //      }
    //    }
    //    return RES_UNDEFINED;
    
//    XObject x = evaluate(context);
//    return ((XBoolean)x).getValue();
    //return false;
    
  }
  */
  
  public boolean match(XPathContext context) throws XPathException {
    throw new XPathException("A Function cannot be matched in a Template Match Pattern.");
    //    if (xf instanceof XFOperatorStroke) {
    //      ETItem[] a = argumentsToSend;
    //      for (int i = 0; i < arguments.length; i++) {
    //         switch (((ETItem)arguments[i]).match(context)) {
    //           case ETItem.RES_TRUE:
    //             return RES_TRUE;
    //           case ETItem.RES_UNDEFINED: return RES_UNDEFINED;
    //           case ETItem.RES_FALSE: break;
    //           default: break;
    //         }
    //      }
    //    }
    //    return RES_UNDEFINED;
  }

  public XFunction getFunction() {
    return xf;
  }

  public QName getName() {
    return name;
  }

  public ETItem getArg(int idx) {
    return arguments[idx];
  }

  private void checkForExtensionFunction() {
    isExtensionFunction = (name.uri.length() > 0);
  }
  
  public boolean equals(Object o) {
    if (! (o instanceof ETFunction)) {
      return false;
    }
    
    ETFunction e = (ETFunction) o;
    if (e.name.equals(name) && e.arguments.length == arguments.length) {
      for (int i=0; i<arguments.length; i++) {
        if (!e.arguments[i].equals(arguments[i])) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public int hashCode(){
    throw new UnsupportedOperationException("Not implemented for usage in hash-based collections");
  }
}

