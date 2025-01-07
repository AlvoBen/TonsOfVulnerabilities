package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xsl.xpath.*;
import com.sap.engine.lib.xsl.xpath.xobjects.*;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

/**
 * <p>
 * This class contains the common functionality shared by all comparison
 * operators in XPath, namely
 * <ul>
 *   <li>=</li>
 *   <li>&gt;</li>
 *   <li>&lt;</li>
 *   <li>&gt;=</li>
 *   <li>&lt;=</li>
 * </ul>
 * </p>
 *
 * <p>
 * Extending classes should only provide a comparing method by overriding <tt>R(int)</tt>
 * where the parameter is -1, 0, or 1, depending on an appropriate comparison
 * of the parameters submitted to the function.
 * </p>
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version May 2001
 */
public abstract class ComparisonOperator implements XFunction 
/*, Relation*/
{

  /**
   * <p>
   * Describes the comparison.
   * The argument is one of { -1, 0, 1 }, depending on an appropriate comparison
   * of the parameters submitted to the function.
   * </p>
   * <p>
   * Extending classes should return true if the respective operator is satisfied.
   * E.g. operator <b>&gt;=</b> would return <tt>true</tt> if <tt>x</tt> is
   * either 0 or 1, and <tt>false</tt> otherwise.
   * </p>
   */
  public final boolean R(int x) throws XPathException {
    return false;
  }

  /**
   * Used by the <tt>execute</tt> method to learn if the operator requires
   * pre-conversion of its arguments to XPath <b>number</b>s.
   * Only operator <b>=</b> does not require such an action.
   */
  public abstract boolean requiresNumbers() throws XPathException;

  public abstract String getFunctionName();

  protected abstract boolean rStrings(CharArray a, CharArray b);

  protected abstract boolean rDoubles(double a, double b);

  protected abstract boolean rBooleans(boolean a, boolean b);

  /**
   * Executes the function by first normalizing the arguments as described in the
   * XPath spec, and then using the abstract <tt>R(int)</tt> method.
   *
   * @see #R(int)
   */
  public final XObject execute(XObject[] a, XPathContext context) throws XPathException {
    XObject a0 = a[0];
    XObject a1 = a[1];

    //LogWriter.getSystemLogWriter().println("ComparisonOperator.execute: a[0] = " + a[0] + ", a[1]=" + a[1] +"." + a[0].toXString() +".");
    if ((a0.getType() == XNodeSet.TYPE) && (a1.getType() == XNodeSet.TYPE)) {
      StaticDouble staticDouble = context.owner.getStaticDouble();
      if (staticDouble == null) {
        staticDouble = new StaticDouble();
      }
      
      /*
       Standart excerpt:
       If both objects to be compared are node-sets, then the comparison will be true if and only
       if there is a node in the first node-set and a node in the second node-set such that the result
       of performing the comparison on the string-values of the two nodes is true.
       */
      IntArrayIterator i0 = ((XNodeSet) a0).iterator();
      while (i0.hasNext()) {
        CharArray s0 = context.dtm.getStringValue(i0.next());

        for (IntArrayIterator i1 = ((XNodeSet) a1).iterator(); i1.hasNext();) {
          CharArray s1 = context.dtm.getStringValue(i1.next());
          if (isDouble(s0) && isDouble(s1)) {
            try {
              double d1 = staticDouble.stringToDouble(s0);
              double d2 = staticDouble.stringToDouble(s1);

              //double d1 = Double.parseDouble(s0), d2 = Double.parseDouble(s1);
              if (rDoubles(d1, d2)) {
                i0.close();
                i1.close();
                return context.getXFactCurrent().getXBoolean(true);
              }
            } catch (NumberFormatException e) {
              //$JL-EXC$
              if (rStrings(s0, s1)) {
                i0.close();
                i1.close();
                return context.getXFactCurrent().getXBoolean(true);
              }
            }
          } else {
            if (rStrings(s0, s1)) {
              i0.close();
              i1.close();
              return context.getXFactCurrent().getXBoolean(true);
            }
          }
        } 

      } 
      i0.close();

      return context.getXFactCurrent().getXBoolean(false);
    }

    boolean swapped = false;

    if (a1.getType() == XNodeSet.TYPE) {
      XObject h = a0;
      a0 = a1;
      a1 = h;
      swapped = true;
    }

    if (a0.getType() == XNodeSet.TYPE) {
      if (a1.getType() == XNumber.TYPE) {
        double d1 = ((XNumber) a1).getValue();

        for (IntArrayIterator i = ((XNodeSet) a0).iterator(); i.hasNext();) {
          XString xs = context.getXFactCurrent().getXString(context.dtm.getStringValue(i.next()));
          XNumber xn = context.getXFactCurrent().getXNumber(xs);
          double d0 = xn.getValue();
          xs.close();
          xn.close();

          if (swapped ? rDoubles(d1, d0) : rDoubles(d0, d1)) {
            i.close();
            return context.getXFactCurrent().getXBoolean(true);
          }
        } 

        return context.getXFactCurrent().getXBoolean(false);
      }

      if (a1.getType() == XString.TYPE) {
        CharArray s1 = ((XString) a1).getValue();

        for (IntArrayIterator i = ((XNodeSet) a0).iterator(); i.hasNext();) {
          int ii = i.next();
          //LogWriter.getSystemLogWriter().println("ComparisonOperator.execute ii=" + ii + ", a[0]="+a[0].toXString());
          //CharArray s0 = context.dtm.getStringValue(i.next());
          CharArray s0 = context.dtm.getStringValue(ii);

          if (s0 == null) {
            s0 = CharArray.EMPTY;
          }

          //LogWriter.getSystemLogWriter().println("ComparisonOperator.execute s0=" + s0 +", s1=" + s1);
          if (swapped ? rStrings(s1, s0) : rStrings(s0, s1)) {
            i.close();
            return context.getXFactCurrent().getXBoolean(true);
          }
        } 

        return context.getXFactCurrent().getXBoolean(false);
      }

      if (a1.getType() == XBoolean.TYPE) {
        boolean b0 = (context.getXFactCurrent().getXBoolean((XNodeSet) a0)).getValue();
        boolean b1 = ((XBoolean) a1).getValue();
        return context.getXFactCurrent().getXBoolean(swapped ? rBooleans(b1, b0) : rBooleans(b0, b1));
      }
    }

    if (requiresNumbers()) {
      // For operators >, >=, <, <=
      //      double d0 = a0.toXNumber().getValue();
      //      double d1 = a1.toXNumber().getValue();
      XNumber a0xn = a0.toXNumber();
      XNumber a1xn = a1.toXNumber();
      double d0 = a0xn.getValue();
      double d1 = a1xn.getValue();
      if (a0xn != a0) {
        a0xn.close();
      }
      if (a1xn != a1) {
        a1xn.close();
      }
      return context.getXFactCurrent().getXBoolean(rDoubles(d0, d1));
    } else {
      // For operator =
      if ((a0.getType() == XBoolean.TYPE) || (a0.getType() == XBoolean.TYPE)) {
        boolean b0 = a0.toXBoolean().getValue();
        boolean b1 = a1.toXBoolean().getValue();
        return context.getXFactCurrent().getXBoolean(rBooleans(b0, b1));
      }

      if ((a0.getType() == XNumber.TYPE) || (a1.getType() == XNumber.TYPE)) {
        XNumber a0xn = a0.toXNumber();
        XNumber a1xn = a1.toXNumber();
        double d0 = a0xn.getValue();
        double d1 = a1xn.getValue();
        if (a0xn != a0) {
          a0xn.close();
        }
        if (a1xn != a1) {
          a1xn.close();
        }
        //        double d0 = a0.toXNumber().getValue();
        //        double d1 = a1.toXNumber().getValue();
        return context.getXFactCurrent().getXBoolean(rDoubles(d0, d1));
      }

      XString a0xs = a0.toXString();
      XString a1xs = a1.toXString();
      CharArray s0 = a0xs.getValue();
      CharArray s1 = a1xs.getValue();
      //      if (a0xn != a0) a0xn.close();
      //      if (a1xn != a1) a1xn.close();
      //      CharArray s0 = a0.toXString().getValue();
      //      CharArray s1 = a1.toXString().getValue();
      try {
        return context.getXFactCurrent().getXBoolean(rStrings(s0, s1));
      } finally {
        if (a0xs != a0) {
          a0xs.close();
        }
        if (a1xs != a1) {
          a1xs.close();
        }
      }
    }
  }

  public final boolean confirmArgumentTypes(XObject[] a) {
    return (a.length == 2);
  }
  
  private static boolean isDouble(CharArray ch) {
    if (ch == null || ch.getSize() == 0) {
      return false;
    }
    char c = ch.charAt(0);
    if (c != '+' && c != '-' && c != '.' && !Character.isDigit(c)) {
      return false;
    }
    boolean hasDot = c == '.';
    if (!Character.isDigit(c) && ch.getSize() == 1) {
      return false;
    }
    for (int i = 1; i < ch.getSize(); i++) {
      c = ch.charAt(i);
      if (c == '.') {
        if (hasDot) {
          return false;
        }
        hasDot = true;
      } else if (!Character.isDigit(c)) {
        return false;
      }
    }
    return true;
  }
}

