package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * <p>
 * Represents variable references.
 * </p>
 * <p>
 * <b>$</b><i>variable_name</i>
 * </p>
 *
 * @see ETItem
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class ETVariableReference implements ETItem {

  protected String name;

  protected ETVariableReference(String name) { // Must supply a name
    this.name = name;
  }

  public void print(int indent) {
    Symbols.printSpace(indent);
//    System.out.println("ETVariableReference($" + name + ")"); //$JL-SYS_OUT_ERR$
  }

  public XObject evaluate(XPathContext context) throws XPathException {
    XObject r = null;
    //System.out.println("ETVariableReference: evaluating for var:" + name);
    try {
      r = (XObject) context.variableBindings.get(name);
      if (r == null) {
        r = (XObject) context.owner.getVariable(name).evaluate(context);
      }
      //System.out.println("ETVariableReference: evaluated for var:" + name + " = "+ r.toXString() + "---");
    } catch (Exception e) {
      throw new XPathException("Variable '$" + name + "' has not been bound to an XObject, but to a " + r.getClass());
    }

    if (r == null) {
      throw new XPathException("Variable '$" + name + "' has not been bound to a value");
    }

    //Debug.check(r);
    return r;
  }

  public boolean match(XPathContext c) throws XPathException {
    throw new XPathException("Cannot match a variable reference in a Template Match Pattern");
  }
  
  public boolean equals() {
    return false;
  }

}

