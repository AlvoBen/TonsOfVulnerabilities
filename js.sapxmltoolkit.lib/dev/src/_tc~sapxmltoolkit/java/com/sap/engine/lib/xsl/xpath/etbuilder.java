package com.sap.engine.lib.xsl.xpath;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import com.sap.engine.lib.xml.parser.handlers.NamespaceHandler;
import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.xobjects.XObjectFactory;
import com.sap.engine.lib.xsl.xpath.xobjects.XString;
import com.sap.engine.lib.xsl.xslt.NamespaceManager;
import com.sap.engine.lib.xsl.xslt.QName;
import com.sap.engine.lib.xsl.xslt.XSLStylesheet;

/**
 * <p>
 * Builds the expression tree out of the query <tt>String</tt> or
 * <tt>CharArray</tt>.
 * </p>
 * <p>
 * Implements the Reverse Polish Notation algorithm using a pair
 * of stacks and not building the reverse notation explicitly.
 * </p>
 * <p>
 * Before building the expression tree the query is tokenized and
 * abbreviation-expanded.
 * </p>
 *
 * @see XPathTokenizer
 * @see XPathAbbreviationExpander
 * @see ETItem
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public final class ETBuilder {

  /**
   *   The library in whose context the expression tree will be built.
   */
  //  private LibraryManager library;
  private StaticDouble staticDouble = new StaticDouble();
  private XObjectFactory xobjectsPool = new XObjectFactory(500);
  private NamespaceManager nsmanager = null;
  private NamespaceHandler namespaceHandler = null;
  private XPathTokenizer tokenizerx = new XPathTokenizer();
  private XPathAbbreviationExpander expanderx = new XPathAbbreviationExpander();
  private Vector vsx = new Vector();
  private IntVector vtx = new IntVector();
  private IntVector posx = new IntVector();
  //  private LibraryManager defaultLibraryManager = new LibraryManager();
  /**
   *   The tokenized expression.
   */
  private Vector vs;
  private IntVector vt;
  private int vIndex;
  /**
   *   Contains operatrions and brackets, and on the other hand their types
   * Types can be: T.OPENING_BRACKET, T.OPENING_SQUARE_BRACKET, T.COMMA,
   * T.UNARY_OPERATOR, T.BINARY_OPERATOR, T.FUNCTION, T.UNARY_SLASH,
   * T.BINARY_SLASH.
   */
  private Stack stackOpS = new Stack();
  private IntStack stackOpT = new IntStack();
  /**
   *   Elements are implementations of interface ETItem.
   */
  private Stack stackVal = new Stack();
  /**
   *   Maps binary operators to their precedences.
   */
  private static final Hashtable binaryPrecedence = new Hashtable();
  
  private XSLStylesheet parent = null;

  static {
    binaryPrecedence.put("|", new Integer(1)); // Lowest precedence
    binaryPrecedence.put("or", new Integer(2));
    binaryPrecedence.put("and", new Integer(3));
    binaryPrecedence.put("=", new Integer(4));
    binaryPrecedence.put("!=", new Integer(4));
    binaryPrecedence.put("<", new Integer(5));
    binaryPrecedence.put("<=", new Integer(5));
    binaryPrecedence.put(">", new Integer(5));
    binaryPrecedence.put(">=", new Integer(5));
    binaryPrecedence.put("+", new Integer(6));
    binaryPrecedence.put("-", new Integer(6));
    binaryPrecedence.put("*", new Integer(7));
    binaryPrecedence.put("div", new Integer(7));
    binaryPrecedence.put("mod", new Integer(7)); // Highest precedence
  }

  /**
   *   Contains binary operators that are calculated right-to-left.
   */
  private static final HashSet binaryFromRightToLeft = new HashSet();

  static {
    //   No binary operators are calculated from right to left in XPath
    // binaryFromRightToLeft.add("+"); // For testing
  }

  /**
   *   Contains operators that can be unary.
   */
  private static final HashSet unary = new HashSet();

  static {
    unary.add("-");
    unary.add("+");
  }

  /**
   *   Contains 'functional' node-tests.
   */
  private static final HashSet specialNodeTests = new HashSet();

  static {
    specialNodeTests.add("processing-instruction");
    specialNodeTests.add("node");
    specialNodeTests.add("text");
    specialNodeTests.add("comment");
    // The following are not required by the specification
    /*
     specialNodeTests.add("attribute");
     specialNodeTests.add("element");
     */
  }

  /**
   *   Performs an operation from stackOp and returns the result in stackVal.
   */
  private void perform() throws XPathException {
    String os = (String) stackOpS.pop();
    QName qname = getQName(os);
    int t = stackOpT.pop();
    ETItem operand1;
    ETItem operand2;

    switch (t) {
      case T.UNARY_OPERATOR: {
        operand1 = (ETItem) stackVal.pop();
        stackVal.push(new ETFunction(qname, operand1));
        break;
      }
      case T.BINARY_OPERATOR: {
        operand2 = (ETItem) stackVal.pop();
        operand1 = (ETItem) stackVal.pop();
        stackVal.push(new ETFunction(qname, operand1, operand2));
        break;
      }
      case T.BINARY_SLASH: {
        operand2 = (ETItem) stackVal.pop();
        operand1 = (ETItem) stackVal.pop();
        stackVal.push(new ETSlash(operand1, operand2));
        break;
      }
      case T.UNARY_SLASH: {
        operand1 = (ETItem) stackVal.pop();
        stackVal.push(new ETSlash(operand1));
        break;
      }
      case T.OPENING_BRACKET: {
        throw new XPathException("Attempt to perform() a T.OPENING_BRACKET.");
      }
      case T.OPENING_SQUARE_BRACKET: {
        throw new XPathException("Attempt to perform() a T.OPENING_SQUARE_BRACKET.");
      }
      case T.COMMA: {
        throw new XPathException("Attempt to perform() a T.COMMA.");
      }
      case T.FUNCTION: {
        throw new XPathException("Attempt to perform() a T.FUNCTION.");
      }
      default: {
        throw new XPathException("Illegal type of token in stackOp.");
      }
    }
  }

  /**
   *   Reads a 'functional' node-test.
   */
  private String readSpecialNodeTest() throws XPathException {
    String nt = (String) vs.elementAt(vIndex);

    if (!specialNodeTests.contains(nt)) {
      throw new XPathException("Unrecongnized special node-test, '" + nt + "'.");
    }

    // processing-instruction might have a literal parameter
    String s2 = (String) vs.elementAt(vIndex + 2);

    if ((nt.equals("processing-instruction") && (!")".equals(s2)))) {
      if (!")".equals(vs.elementAt(vIndex + 3))) {
        throw new XPathException("')' expected after special node-test '" + nt + "'.");
      }

      char ch = s2.charAt(0);

      if ((ch != '\'') && (ch != '\"')) {
        throw new XPathException("Parameter of special node-test 'processing-instruction' must be a literal.");
      }

      vIndex += 3;
      return nt + "(" + s2 + ")";
    }

    if (!")".equals(s2)) {
      throw new XPathException("')' expected after special node-test '" + nt + "'");
    }

    vIndex += 2;
    return nt + "()";
  }

  public ETObject process(CharArray query) throws XPathException {
    return process(query.getString());
  }

  public ETObject process(String query) throws XPathException {
    try {
      if (query == null || query.length() == 0) {
        return new ETObject("", null);
      }
      vsx.clear();
      vtx.clear();
      posx.clear();
      tokenizerx.process(query, vsx, vtx, posx);
      expanderx.process(vsx, vtx, posx);
      return new ETObject(query, process(vsx, vtx, posx));
    } catch (XPathException _) {
      throw _;
    } catch (Exception e) {
      throw new XPathException("Could not process query \'" + query + "\'", e);
    }
  }

  /**
   *   Parses the expression using the default, core function library.
   *   Returns the expression tree obtained from the argument.
   */
  private ETItem process(Vector vs, IntVector vt, IntVector pos) throws XPathException {
    return process(vs, vt, pos, null);
  }

  private ETItem process(Vector vs, IntVector vt, IntVector pos, LibraryManager library) throws XPathException {
    return process(vs, vt, library);
  }

  /**
   *   Parses the expression using an arbitrary function library.
   *   Returns the expression tree obtained from the argument.
   */
  private ETItem process(Vector vs0, IntVector vt0, LibraryManager library0) throws XPathException {
    vs = vs0;
    vt = vt0;
    //    library = library0;
    vs.add(0, "(");
    vt.add(0, T.OPENING_BRACKET);
    int lastButOne = vs.size() - 1;
    vs.add(lastButOne, ")");
    vt.add(lastButOne, T.CLOSING_BRACKET);
    boolean oe = false;
    // true when a binary operator or ')' is expected
    // false when a variable or '(' is expected
    stackOpS.clear();
    stackOpT.clear();
    stackVal.clear();
    try {
      int end = vs.size() - 1;

      for (vIndex = 0; vIndex < end; vIndex++) {
        int type = vt.elementAt(vIndex);
        int typeNext = vt.elementAt(vIndex + 1);
        String token = (String) vs.elementAt(vIndex);
        String tokenNext = (String) vs.elementAt(vIndex + 1);

        if (type == T.OPENING_BRACKET) {
          if (oe) {
            throw new XPathException("Unexpected '('.");
          }

          if (typeNext == T.CLOSING_BRACKET) {
            throw new XPathException("Useless '()' or empty query string.");
          }

          stackOpS.push("(");
          stackOpT.push(T.OPENING_BRACKET);
        } else if (type == T.CLOSING_BRACKET) {
//          boolean hasCommas = false;

          LABEL: while (true) {
            switch (stackOpT.peek()) {
              case T.OPENING_BRACKET: {
                stackOpS.pop();
                stackOpT.pop();
                break  LABEL;
              }
              case T.OPENING_SQUARE_BRACKET: {
                throw new XPathException("Attempt to close '[' with ')'.");
              }
              case T.UNARY_OPERATOR: // falls through
              case T.UNARY_SLASH: {
                perform();
                continue  LABEL;
              }
              case T.BINARY_OPERATOR:
              case T.BINARY_SLASH: // falls through
              {
                perform();
                continue  LABEL;
              }
              case T.COMMA: { //$JL-SWITCH$
                Vector arguments = new Vector();
                arguments.addElement(stackVal.pop());

                LABEL2: while (true) {
                  switch (stackOpT.peek()) {
                    case T.FUNCTION: {
                      QName qname = getQName((String) stackOpS.peek());
                      stackVal.push(new ETFunction(qname, arguments));
                      stackOpS.pop();
                      stackOpT.pop();
                      break  LABEL;
                    }
                    case T.COMMA: {
                      arguments.insertElementAt(stackVal.pop(), 0);
                      stackOpS.pop();
                      stackOpT.pop();
                      continue  LABEL2;
                    }
                    default: {
                      throw new XPathException("Invalid usage of ','.");
                    }
                  }
                }


              }
              //break; // Statement not reached.
              case T.FUNCTION: // This will only be called for f(x), not for f()
              {
                Vector argument = new Vector();
                argument.addElement(stackVal.pop());
                String fname = (String) stackOpS.peek();

                if (fname.equals("function-available")) {
                  if (argument.get(0) instanceof XString) {
                    CharArray value = ((XString) argument.get(0)).getValue();
                    argument.set(0, xobjectsPool.getXJavaObject(getQName(value)));
                  }
                }

                stackVal.push(new ETFunction(getQName((String) stackOpS.peek()), argument));
                stackOpS.pop();
                stackOpT.pop();
                break  LABEL;
              }
              default: {
                throw new XPathException("Illegal type in stackOp.");
              }
            }
          }
        } else if (type == T.OPENING_SQUARE_BRACKET) {
          if (!oe) {
            throw new XPathException("Unexpected '['.");
          }

          /*
           while (stackOpT.peek() == T.BINARY_SLASH) {
           perform();
           }
           */
          stackOpS.push("[");
          stackOpT.push(T.OPENING_SQUARE_BRACKET);
          oe = false;
        } else if (type == T.CLOSING_SQUARE_BRACKET) {
          // ! Copied from CLOSING BRACKET HANDLING and modified
//          boolean hasCommas = false;

          LABEL: while (true) {
            switch (stackOpT.peek()) {
              case T.OPENING_SQUARE_BRACKET: {
                stackOpS.pop();
                stackOpT.pop();
                ETItem a2 = (ETItem) stackVal.pop();
                /*
                 while (stackOpT.peek() == T.BINARY_SLASH) {
                 perform();
                 }
                 */
                ETItem a1 = (ETItem) stackVal.pop();
                stackVal.push(new ETPredicate(a1, a2));
                break  LABEL;
              }
              case T.OPENING_BRACKET: {
                throw new XPathException("Attempt to close '(' with ']'.");
              }
              case T.UNARY_OPERATOR: // falls through
              case T.UNARY_SLASH: // falls through
              case T.BINARY_OPERATOR: // falls through
              case T.BINARY_SLASH: {
                perform();
                continue  LABEL;
              }
              case T.COMMA:
              case T.FUNCTION: {
                throw new XPathException("Attempt to close a function's argument list with ']'.");
              }
              default: {
                throw new XPathException("Illegal type in stackOp.");
              }
            }
          }
        } else if (type == T.COMMA) {
          if (!oe) {
            throw new XPathException("Unexpected ','.");
          }

          LABEL: while (true) {
            switch (stackOpT.peek()) {
              case T.OPENING_BRACKET: // falls through
              case T.FUNCTION: // falls through
              case T.COMMA: {
                break  LABEL;
              }
              case T.OPENING_SQUARE_BRACKET: {
                throw new XPathException("Invalid usage of ','.");
              }
              case T.UNARY_OPERATOR: // falls through
              case T.UNARY_SLASH: // falls through
              case T.BINARY_OPERATOR: // falls through
              case T.BINARY_SLASH: // falls through
              {
                perform();
                continue  LABEL;
              }
              default: {
                throw new XPathException("Illegal type in stackOp.");
              }
            }
          }

          stackOpS.push(",");
          stackOpT.push(T.COMMA);
          oe = false;
        } else if (type == T.DOLLAR) {
          if (oe) {
            throw new XPathException("Operator or ')' expected.");
          }

          if (typeNext != T.QNAME) {
            throw new XPathException("A variable name must match the QName production.");
          }

          stackVal.push(new ETVariableReference(tokenNext));
          vIndex++; // Skip the name of the variable
          oe = true;
        } else if (type == T.NUMBER) {
          if (oe) {
            throw new XPathException("Operator or ')' expected.");
          }

          stackVal.push(xobjectsPool.getXNumber(staticDouble.stringToDouble(token)).setConstant());
          //stackVal.push(xobjectsPool.getXNumber(Double.parseDouble(token)));
          oe = true;
        } else if (type == T.LITERAL) {
          if (oe) {
            throw new XPathException("Operator or ')' expected.");
          }

          stackVal.push(xobjectsPool.getXString(token.substring(1, token.length() - 1)).setConstant());
          oe = true;
        } else if ((type == T.QNAME) && (typeNext == T.OPENING_BRACKET) && !oe) {
          //   It is essential for this 'if' to be before the node test and axis handling
          // because of the '*', which may also appear there.
          if (specialNodeTests.contains(token)) {
            String test = readSpecialNodeTest();
            QName qname = getQName(test); //new QName().reuse(test);
            //                    String uri = "";
            //                    //skip default namespaces
            //                    if (qname.prefix.length() > 0) {
            //                      uri = namespaceHandler.get(qname.prefix.getString());
            //                    }
            //                    qname.setURI(uri, nsmanager);
            stackVal.push(new ETLocationStep("child", qname));
            oe = true;
          } else {
            if (vt.elementAt(vIndex + 2) == T.CLOSING_BRACKET) {
              stackVal.push(new ETFunction(getQName(token), new Vector()));
              vIndex += 2;
              oe = !oe;
            } else {
              stackOpS.push(token);
              stackOpT.push(T.FUNCTION);
              vIndex++;
            }
          }
        } else if (oe && binaryPrecedence.containsKey(token)) {
          int p = ((Integer) binaryPrecedence.get(token)).intValue();

          LABEL: while (true) {
            switch (stackOpT.peek()) {
              case T.OPENING_BRACKET: // falls through
              case T.OPENING_SQUARE_BRACKET: {
                break  LABEL;
              }
              case T.UNARY_OPERATOR: // falls through
              case T.UNARY_SLASH: // falls through
              case T.BINARY_SLASH: {
                perform();
                continue  LABEL;
              }
              case T.BINARY_OPERATOR: {
                int q = ((Integer) binaryPrecedence.get(stackOpS.peek())).intValue();

                if ((p > q) || ((p == q) && (binaryFromRightToLeft.contains(stackOpS.peek())))) {
                  break  LABEL;
                }

                perform();
                continue  LABEL;
              }
              case T.COMMA: {
                break  LABEL;
              }
              case T.FUNCTION: {
                break  LABEL;
              }
              default: {
                throw new XPathException("Illegal type in stackOp.");
              }
            }
          }

          stackOpS.push(token);
          stackOpT.push(T.BINARY_OPERATOR);
          oe = false;
        } else if (oe && (type == T.SLASH)) {
          // Binary '/'
          while ((stackOpT.peek() == T.BINARY_SLASH) || (stackOpT.peek() == T.UNARY_SLASH)) {
            perform();
          }

          stackOpS.push(token);
          stackOpT.push(T.BINARY_SLASH);
          oe = false;
        } else if (!oe && unary.contains(token)) {
          stackOpS.push(token);
          stackOpT.push(T.UNARY_OPERATOR);
        } else if (!oe && (type == T.SLASH)) {
          if ((binaryPrecedence.containsKey(tokenNext) && !tokenNext.equals("*")) || (typeNext == T.COMMA) || (typeNext == T.CLOSING_BRACKET) || (typeNext == T.CLOSING_SQUARE_BRACKET)) {
            // This is a '/' that means 'root'
            stackVal.push(new ETDocumentRoot());
            oe = true;
          } else {
            // Unary '/'
            while ((stackOpT.peek() == T.BINARY_SLASH) || (stackOpT.peek() == T.UNARY_SLASH)) {
              perform();
            }

            stackOpS.push(token);
            stackOpT.push(T.UNARY_SLASH);
          }
        } else if ("*".equals(token) || (type == T.QNAME)) {
          if (oe) {
            throw new XPathException("Operator or ')' expected.");
          }

          String axis = "child";
          String test = token;

          if ("::".equals(tokenNext) && !"*".equals(token)) {
            axis = token;
            vIndex += 2;
            test = (String) vs.elementAt(vIndex);

            if (!"*".equals(test) && !Symbols.isInitialCharForQName(test.charAt(0))) {
              throw new XPathException("Node test expected after axis '" + token + "'.");
            }

            if (vt.elementAt(vIndex + 1) == T.OPENING_BRACKET) {
              test = readSpecialNodeTest();
            }
          }

          QName qname = getQName(test); //new QName().reuse(test);
          //                //skip default namespaces
          //                String uri = "";
          //                if (qname.prefix.length() > 0) {
          //                  uri = namespaceHandler.get(qname.prefix.getString());
          //                }
          //                qname.setURI(uri, nsmanager);
          stackVal.push(new ETLocationStep(axis, qname));
          oe = true;
        } else {
          throw new XPathException("Unexpected token or token not recognized, '" + token + "'.");
        }
      } 
    } catch (EmptyStackException e) {
      throw new XPathException("Error parsing query.", e);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new XPathException("Error parsing query.", e);
    }

    if (!stackOpS.isEmpty() || (stackVal.size() != 1)) {
      throw new XPathException("Unexpected end of query.");
    }

    return (ETItem) stackVal.pop();
  }

  public void setNamespaceStuff(NamespaceManager nsmanager, NamespaceHandler nshandler) {
    this.nsmanager = nsmanager;
    this.namespaceHandler = nshandler;
  }
  
  public void setParentStylesheet(XSLStylesheet p) {
    parent = p;
  }
  
  private QName tempQName = new QName();

  private QName getQName(CharArray s) throws XPathException  {
    return getQName_int(tempQName.reuse(s));
  }

  private QName getQName(String s) throws XPathException  {
    //QName qname = new QName().reuse(s);
    return getQName_int(tempQName.reuse(s));
  }

  private QName getQName_int(QName qname) throws XPathException {
    CharArray uri = CharArray.EMPTY;

    //skip default namespaces
    if (qname.prefix.length() > 0) {
      try {
        uri = namespaceHandler.isMapped(qname.prefix);
      } catch (Exception e) {
        throw new XPathException("Prefix not mapped: " + qname.prefix, e);
      }
    }

    qname.setURI(uri, nsmanager);
    if (parent == null) {
      return new QName().reuse(qname);
    } else {
      if (!parent.qnameTable.contains(qname)) {
        QName q = new QName().reuse(qname);
        parent.qnameTable.put(q, q);
      }
      return (QName)parent.qnameTable.get(qname);
    }
//    return qname;
      
  }

  //  public void addNamespaceMapping(String prefix, String uri) {
  //    namespaceHandler.

}

