package com.sap.engine.lib.xsl.xpath;

import com.sap.engine.lib.xsl.xpath.xobjects.IntArrayIterator;
import com.sap.engine.lib.xsl.xpath.xobjects.XJavaObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XString;

/**
 * Represents the <b>/</b> operator.
 *
 * @see ETItem
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class ETSlash implements ETItem {

  public boolean isUnary;
  public ETItem left;
  public ETItem right;
  public ETLocationStep rightMostLS = null;
  protected XPathContext c = new XPathContext();

  public ETSlash(ETItem et) {
    isUnary = true;
    left = null;
    right = et;
    initRightMost(right);
  }

  public ETSlash(ETItem etLeft, ETItem etRight) {
    isUnary = false;
    left = etLeft;
    right = etRight;
    initRightMost(right);
  }

  public void initRightMost(ETItem r) {
    if (!(r instanceof ETPredicate) && !(r instanceof ETLocationStep))  {
      return; //otherwise an endless loop will occur
    }

    while (!(r instanceof ETLocationStep)) {
      if (r instanceof ETPredicate) {
        r = ((ETPredicate) r).expression;
      }
    }

    rightMostLS = (ETLocationStep) r;
  }

  public void print(int indent) {
    Symbols.printSpace(indent);
/*
    if (!isUnary) {
      System.out.println("ETSlash(binary)"); //$JL-SYS_OUT_ERR$
      left.print(indent + 1);
    } else {
      System.out.println("ETSlash(unary)"); //$JL-SYS_OUT_ERR$
    }
*/
    right.print(indent + 1);
  }

  public XObject evaluate(XPathContext context) throws XPathException {
    if (context.owner != null) {
      context.owner.freeMem();
    }

    if (isUnary) {
      //      System.out.println("ETSlash: evaluating: unary:"  );
      //      right.print(1);
      XPathContext ctx = new XPathContext(context);
      
      int docElement = ctx.dtm.getDocumentElement(ctx.node);
      ctx = ctx.dtm.getInitialContext();
      ctx.node = docElement;

      XObject r1 = right.evaluate(ctx);
      return r1;
    }

    XObject xo = left.evaluate(context);

    if (xo.getType() == XJavaObject.TYPE) {
      ((XJavaObject) xo).setContext(context);
      xo = xo.toXNodeSet();
      //r.dtm = ((XNodeSet) xo).dtm;
    }
      
    if (xo.getType() != XNodeSet.TYPE) {
      XNodeSet r = context.getXFactCurrent().getXNodeSet(context.dtm);
      r.close();
      return r;

     // throw new XPathException("Left operand of '/' must evaluate to a node-set. Left operand is: " + left);
    }

    XNodeSet r = context.getXFactCurrent().getXNodeSet(context.dtm);

    r.dtm = ((XNodeSet) xo).dtm;
    context.dtm = r.dtm;

    XNodeSet xns = (XNodeSet) xo;
    if (xns.count() == 0) {
      if (xns != r) {
        r.close();
      }
      return xns;
    }
    c.reuse(context);
    c.size = xns.count();
    int pos = 0;
    r.setForward(true); // The resulting node-set is forward <=> at lest one

    // of the left-hand node-sets is forward.
    //    System.out.println("ETSlash: Iterating to evaluate Right"  );
    //    right.print(1);
    for (IntArrayIterator i = xns.iterator(); i.hasNext();) {
      pos++;
      c.position = pos;
      c.node = i.next();
      XNodeSet rhxns;
      XObject evalled = right.evaluate(c);
      try {
        if (evalled.getType() == XJavaObject.TYPE) {
          evalled = evalled.toXNodeSet();
        } 
        else if (evalled.getType() == XString.TYPE) {
          return evalled;
        }
        rhxns = (XNodeSet) evalled;
      } catch (ClassCastException e) {
        throw new XPathException("Right operand of '/' must evaluate to a node-set.");
      }

      if (!rhxns.isForward()) {
        r.setForward(false);
      }

      r.uniteWith(rhxns);
      rhxns.close();
    } 

    xo.close(); // xxxx this should be returned since it is an instance var
    return r;
  }

  public boolean match(XPathContext context) throws XPathException {
    ETLocationStep rr = rightMostLS;
    if (isUnary) {
      if (rr.axisType == AXIS_DOS) {
        return true;
      } else {
        if (context.dtm.parent[context.node] != 0) {
          return false;
        }

        return right.match(context);
      }
    }

    c.reuse(context);

    if (rr.axisType == AXIS_DOS) {
      for (int p = c.node; p >= 0; p = c.dtm.parent[p]) {
        c.node = p;

        if (left.match(c)) {
          return true;
        }

        if (p == 0) {
          return false;
        }
      } 
    } else {
      if (rr.match(c) == false) {
        return false;
      }

      if (!right.match(c)) {
        return false;
      }

      c.node = c.dtm.parent[c.node];
      return left.match(c);
    }

    return false;
  }
  
  public boolean equals() {
    return false;
  }

}

