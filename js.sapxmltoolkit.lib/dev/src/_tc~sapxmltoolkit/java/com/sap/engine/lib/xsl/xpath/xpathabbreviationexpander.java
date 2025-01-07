package com.sap.engine.lib.xsl.xpath;

import java.util.*;

/**
 *   Encapsulates a method to expand the abbreviations in
 * a <tt>DoubleVector</tt> of tokens.
 *
 * @see ETBuilder
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @author Vladimir Savtchenko, vladimir.savchenko@sap.com
 *
 * @version July 2001
 */
public final class XPathAbbreviationExpander {

  private static final String SELF = "self";
  private static final String dvetochki = "::";
  private static final String NODE = "node";
  private static final String skobkaotv = "(";
  private static final String skobkazatv = ")";
  private static final String PAR = "parent";
  private static final String ATT = "attribute";
  private static final String DESC = "descendant-or-self";
  private static final String SLASH = "/";

  public static void process(Vector vs, IntVector vt, IntVector pos) {
    for (int i = 0; i < vs.size(); i++) {
      int t = vt.elementAt(i);

      if (t == T.DOT) {
        vs.remove(i);
        vt.remove(i);
        vs.add(i, SELF);
        vt.add(i, T.QNAME);
        int p = pos.elementAt(i);
        vs.add(i + 1, dvetochki);
        vt.add(i + 1, T.DOUBLE_COLON);
        pos.add(i, p);
        vs.add(i + 2, NODE);
        vt.add(i + 2, T.QNAME);
        pos.add(i, p);
        vs.add(i + 3, skobkaotv);
        vt.add(i + 3, T.OPENING_BRACKET);
        pos.add(i, p);
        vs.add(i + 4, skobkazatv);
        vt.add(i + 4, T.CLOSING_BRACKET);
        pos.add(i, p);
        i += 4;
      } else if (t == T.DOUBLE_DOT) {
        vs.remove(i);
        vt.remove(i);
        vs.add(i, PAR);
        vt.add(i, T.QNAME);
        int p = pos.elementAt(i);
        vs.add(i + 1, dvetochki);
        vt.add(i + 1, T.DOUBLE_COLON);
        pos.add(i, p);
        vs.add(i + 2, NODE);
        vt.add(i + 2, T.QNAME);
        pos.add(i, p);
        vs.add(i + 3, skobkaotv);
        vt.add(i + 3, T.OPENING_BRACKET);
        pos.add(i, p);
        vs.add(i + 4, skobkazatv);
        vt.add(i + 4, T.CLOSING_BRACKET);
        pos.add(i, p);
        i += 4;
      } else if (t == T.AT) {
        vs.remove(i);
        vt.remove(i);
        vs.add(i, ATT);
        vt.add(i, T.QNAME);
        int p = pos.elementAt(i);
        vs.add(i + 1, dvetochki);
        vt.add(i + 1, T.DOUBLE_COLON);
        pos.add(i, p);
        i += 1;
      } else if (t == T.DOUBLE_SLASH) {
        vs.remove(i);
        vt.remove(i);
        vs.add(i, SLASH);
        vt.add(i, T.SLASH);
        int p = pos.elementAt(i);
        vs.add(i + 1, DESC);
        vt.add(i + 1, T.QNAME);
        pos.add(i, p);
        vs.add(i + 2, dvetochki);
        vt.add(i + 2, T.DOUBLE_COLON);
        pos.add(i, p);
        vs.add(i + 3, NODE);
        vt.add(i + 3, T.QNAME);
        pos.add(i, p);
        vs.add(i + 4, skobkaotv);
        vt.add(i + 4, T.OPENING_BRACKET);
        pos.add(i, p);
        vs.add(i + 5, skobkazatv);
        vt.add(i + 5, T.CLOSING_BRACKET);
        pos.add(i, p);
        vs.add(i + 6, SLASH);
        vt.add(i + 6, T.SLASH);
        pos.add(i, p);
        i += 6;
      }
    } 
  }

}

