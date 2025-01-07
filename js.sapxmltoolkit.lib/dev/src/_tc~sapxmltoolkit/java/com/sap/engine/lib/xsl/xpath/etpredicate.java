package com.sap.engine.lib.xsl.xpath;

import java.util.Vector;

import com.sap.engine.lib.log.LogWriter;
import com.sap.engine.lib.xsl.xpath.xobjects.IntArrayIterator;
import com.sap.engine.lib.xsl.xpath.xobjects.XJavaObject;
import com.sap.engine.lib.xsl.xpath.xobjects.XNodeSet;
import com.sap.engine.lib.xsl.xpath.xobjects.XNumber;
import com.sap.engine.lib.xsl.xpath.xobjects.XObject;

/**
 * Represents the <b>[]</b> operator.
 *
 * @see ETItem
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class ETPredicate implements ETItem {

  public ETItem expression;
  public ETItem filter;
  protected XPathContext c = new XPathContext();
  protected int[] iPerfData = null;
  private IntVector nodelist = new IntVector(50, 50);
  private IntVector nodelistnew = new IntVector(50, 50);
  private IntVector nodelisttmp = null;

  ;
  private Vector predlist = null;
  private IntHashtable intHash = null;
  private long linkedContextId = -1;
  private ETLocationStep nodetest = null;

  protected ETPredicate(ETItem expression, ETItem filter) {
    this.expression = expression;
    this.filter = filter;
  }

  public void print(int indent) {
    Symbols.printSpace(indent);
    LogWriter.getSystemLogWriter().println("ETPredicate"); //$JL-SYS_OUT_ERR$
    expression.print(indent + 1);
    filter.print(indent + 1);
  }

  public XObject evaluate(XPathContext context) throws XPathException {
    //    LogWriter.getSystemLogWriter().println("ETPredicate.evaluate expr: node=" + context.node);
    //    expression.print(1);
    if (context.owner != null) {
      context.owner.freeMem();
    }

    XObject xo = expression.evaluate(context);

    //    LogWriter.getSystemLogWriter().println("ETPredicate.evaluate expr  EVALATED:");
    //DTM newDTM = null;
    if (xo.getType() == XJavaObject.TYPE) {
      xo = xo.toXNodeSet();
      //newDTM = ((XNodeSet) xo).dtm;
    }
    if (xo.getType() != XNodeSet.TYPE) {
      throw new XPathException("Filtered expression must evaluate to a node-set.");
    }

    context.dtm = ((XNodeSet) xo).dtm;
    if (((XNodeSet) xo).size() == 0) {
      return xo;
    }

    XNodeSet r = context.getXFactCurrent().getXNodeSet((XNodeSet) xo); // !!!
    //if (newDTM != null) {
    //  r.dtm = newDTM;
    // }
    xo.close(); // xxxx XO must not be used any more.. it is copied to 'r'

    //    LogWriter.getSystemLogWriter().println("ETPredicate. r.size=" + r.size());
    if (filter instanceof XNumber) {
      double pDouble = ((XNumber) filter).getValue();
      int p = (int) pDouble;

      if ((p == pDouble) && (p > 0) && (p <= r.count())) {
        int x = r.getKth(p);
        XObject r1 = context.getXFactCurrent().getXNodeSet(r.dtm, x);
        r.close();
        return r1;
      } else {
        XObject r1 = context.getXFactCurrent().getXNodeSet(r.dtm);
        r.close();
        return r1;
      }
    } else {
      //        LogWriter.getSystemLogWriter().println("ETPredicate.evaluate filter 1:");
      //        filter.print(1);
      c.reuse(context);
      int cnt = r.count();
      c.size = cnt;
      int pos = 0;
      XNodeSet iterNS = context.getXFactCurrent().getXNodeSet(r);
      IntArrayIterator i = iterNS.sensitiveIterator();
      XObject preeval = null;

      if (!isExpressionContextSensitive(filter)) {
        preeval = filter.evaluate(context);
        //          LogWriter.getSystemLogWriter().println("ETPredicate using preevaluated filter:" + preeval);
      } else {
        //          LogWriter.getSystemLogWriter().println("ETPredicate expr IS Context sens= " + filter);
      }

      for (; i.hasNext();) {
        pos++;
        c.position = pos;
        int x = i.next();
        c.node = x;
        XObject p = null;

        if (preeval != null) {
          p = preeval;
        } else {
          p = filter.evaluate(c);
        }

        //            LogWriter.getSystemLogWriter().println("ETPredicate.evaluate filter Evaluated: p.hash=" + p.hashCode());
        if (p.getType() == XNumber.TYPE) {
          if (pos != (int) ((XNumber) p).getValue()) {
            r.mark(i);
          }
        } else {
          if (!p.toXBoolean().getValue()) {
            r.mark(i);
          }
        }

        if (preeval == null) {
          p.close(); //p.must not be used any more !!! it must be returned for further reuse
        }
      } 

      if (preeval != null) {
        preeval.close();
      }

      iterNS.close();
      i.close();
      r.setForward(true);
      r.compact();
      return r;
    }
  }

  private boolean evalFilter(XPathContext context, XObject p, int pos) throws XPathException {
    context.getXFactCurrent().releaseXObject(p);

    if (p.getType() == XNumber.TYPE) {
      if (c.position == (int) ((XNumber) p).getValue()) {
        return true;
      }
    } else if (p.toXBoolean().getValue()) {
      return true;
    } else {
      return false;
    }

    return false;
  }

  public boolean match(XPathContext context) throws XPathException {
    // ??? za kakwo e tova tuka ??
    boolean res = expression.match(context);

    //boolean res = true;
    if (res) {
      if (linkedContextId != context.contextId) {
        intHash = null;
      }

      nodelist.clear();

      if (intHash == null) {
        linkedContextId = context.contextId;
        intHash = new IntHashtable(101);
        predlist = new Vector();
        predlist.clear();
        ETItem curex = expression;

        //Compute the last LocationStep of the expression and all the Predicates before the current one
        while (!(curex instanceof ETLocationStep)) {
          if (curex instanceof ETPredicate) {
            predlist.add(((ETPredicate) curex).filter);
            curex = ((ETPredicate) curex).expression;
          } else if (curex instanceof ETSlash) {
            curex = ((ETSlash) curex).right;
          } else {
            throw new XPathException("Could not match expression '" + expression + "' because of unsuppoted pattern types");
          }
        }

        nodetest = (ETLocationStep) curex;
        predlist.add(filter);
      }

      int rrr = intHash.get(context.node);

      if (rrr > -1) {
        //        LogWriter.getSystemLogWriter().println("Using old result");
        return rrr == 0 ? false : true;
      }

      int matchNode = context.node;
      int parentNode = context.dtm.parent[matchNode];
      int contextSize = 0;
      int contextPos = -1;

      //create a nodelist of all matching nodes, according to the NodeTest of the LocationStep
      //if (parentNode > 0) {  //????                                   
      for (int n = context.dtm.firstChild[parentNode]; n != DTM.NONE; n = context.dtm.nextSibling[n]) {
        c.reuse(context);
        c.node = n;

        if (nodetest.match(c)) {
          nodelist.add(n);
        }
      } 

      //      } else {
      //        return false;
      //      }
      //
      //      for (int i=0; i<nodelist.size(); i++) {
      //        intHash.put(nodelist.get(i), 0);
      //      }
      c.reuse(context);

      //filter the resulting nodelist if there are more than one filters
      for (int ip = 0; ip < predlist.size(); ip++) {
        ETItem f = (ETItem) predlist.get(ip);
        c.size = nodelist.size();

        for (int i = 0; i < c.size; i++) {
          c.position = i + 1;
          c.node = nodelist.get(i);

          //          int rrr = intHash.get(context.node);
          //          if (rrr > -1) {
          //            //        LogWriter.getSystemLogWriter().println("Using old result");
          //            return rrr==0?false:true;
          //          }
          //          if (rrr == 1 || (rrr == -1 && evalFilter(context, f.evaluate(c), c.position))) {
          if (evalFilter(context, f.evaluate(c), c.position)) {
            nodelistnew.add(c.node);
            intHash.put(c.node, 1);
          } else {
            intHash.put(c.node, 0);
          }
        } 

        if (nodelistnew.indexOf(matchNode) == -1) {
          return false;
        }

        nodelisttmp = nodelist;
        nodelist = nodelistnew;
        nodelistnew = nodelisttmp;
        nodelistnew.clear();
      } 

      //evaluate the last filter, this of the current ETPredicate and directly return the result
      //      for (int i=0; i<nodelist.size(); i++) {
      //        intHash.put(nodelist.get(i), 1);
      //      }
      c.position = nodelist.indexOf(matchNode) + 1;
      if (c.position == 0) {
        return false;
      } else {
        return true;
      }
      //c.size = nodelist.size();
      //c.node = matchNode;
      //return evalFilter(context, filter.evaluate(c), c.position);
    }

    return false;
  }

  public boolean isExpressionContextSensitive(ETItem et) throws XPathException {
    //    LogWriter.getSystemLogWriter().println("ETPredicate.isexpr..: " + et);
    if (et instanceof ETSlash) {
      ETSlash ets = (ETSlash) et;

      if (ets.isUnary) {
        return false;
      } else if (ets.right instanceof ETLocationStep) {
        return isExpressionContextSensitive(ets.left);
      } else {
        return true;
      }

      //return isExpressionContextSensitive(((ETSlash)et).left, ((ETSlash)et).right);
    } else if (et instanceof ETLocationStep) {
      return true;
      //ETLocationStep els = (ETLocationStep)et;
    } else if (et instanceof ETPredicate) {
      return true;
    } else if (et instanceof ETFunction) {
      ETFunction etf = (ETFunction) et;

      if (etf.getName().getRawName().equals("current")) {
        return false;
      } else if (etf.getName().getRawName().equals("key")) {
        return isExpressionContextSensitive(etf.arguments[1]);
      } else {
        return true;
      }
    } else {
      return true;
    }
  }

  public boolean equals(Object o) {
    if (! (o instanceof ETPredicate)) {
      return false;
    }
    
    ETPredicate e = (ETPredicate) o;
    return e.expression.equals(expression) && e.filter.equals(filter);
  }

  public int hashCode(){
    throw new UnsupportedOperationException("Not implemented for usage in hash-based collections");
  }
  

}

