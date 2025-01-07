package com.sap.engine.lib.xsl.xpath.xobjects;

import java.util.Arrays;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sap.engine.lib.xml.parser.helpers.CharArray;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.ETItem;
import com.sap.engine.lib.xsl.xpath.Symbols;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xslt.QName;

/**
 *   Represents XPath's <strong>node-set</strong> type.
 *   Wraps an <tt>int[]</tt> which contains the indices of the elements in
 * the set, kept in document order. Resized whenever necessary.
 *
 * @author Nick Nickolov, nick_nickolov@abv.bg
 * @version June 2001
 */
public  class XNodeSet extends XObject implements ETItem {

  /**
   * The <tt>TYPE</tt> field for this <tt>XObject</tt>.
   * @see XObject#getType()
   */
  public static final int TYPE = 4;
  private static final int INITIAL_SIZE = 16;
  public static final String AXIS_ANCESTOR = "ancestor";
  public static final String AXIS_ANCESTOR_OR_SELF = "ancestor-or-self";
  public static final String AXIS_ATTRIBUTE = "attribute";
  public static final String AXIS_CHILD = "child";
  public static final String AXIS_DESCEDANT = "descendant";
  public static final String AXIS_DESCEDANT_OR_SELF = "descendant-or-self";
  public static final String AXIS_FOLLOWING = "following";
  public static final String AXIS_FOLLOWING_SIBLING = "following-sibling";
  public static final String AXIS_NAMESPACE = "namespace";
  public static final String AXIS_PARENT = "parent";
  public static final String AXIS_PRECEDING = "preceding";
  public static final String AXIS_PRECEDING_SIBLING = "preceding-sibling";
  public static final String AXIS_SELF = "self";
  public static final CharArray NT_NODE = new CharArray("node");
  public static final CharArray NT_ELEMENT = new CharArray("element");
  public static final CharArray NT_ATTRIBUTE = new CharArray("attribute");
  public static final CharArray NT_TEXT = new CharArray("text");
  public static final CharArray NT_PROCESSING_INSTRUCTION = new CharArray("processing-instruction");
  public static final CharArray NT_COMMENT = new CharArray("comment");
  public static final CharArray NT_ALL = new CharArray("*");
  private static final Hashtable HASHTABLE = new Hashtable();

  static {
    HASHTABLE.put(new CharArray(NT_NODE), new Integer(DTM.WHATEVER_NODE));
    HASHTABLE.put(new CharArray(NT_ELEMENT), new Integer(DTM.ELEMENT_NODE));
    HASHTABLE.put(new CharArray(NT_ATTRIBUTE), new Integer(DTM.ATTRIBUTE_NODE));
    HASHTABLE.put(new CharArray(NT_PROCESSING_INSTRUCTION), new Integer(DTM.PROCESSING_INSTRUCTION_NODE));
    HASHTABLE.put(new CharArray(NT_TEXT), new Integer(DTM.TEXT_NODE));
    HASHTABLE.put(new CharArray(NT_COMMENT), new Integer(DTM.COMMENT_NODE));
  }

  public DTM dtm;
  private XPathContext xcont = null;
  private int RESIZE_STEP = INITIAL_SIZE * 2;
  private int[] value = new int[INITIAL_SIZE];
  private int n = 0; // the number of elements in the node-set
  /**
   * Every node-set knows its direction, either forward, or backward.
   * This field is checked by the sensitiveIterator() method, so
   * it returns iterator() if true, or backIterator() if false.
   */
  private boolean forward;
  /**
   * The iterator over the node-set.
   * An instance of it is created only if requested.
   */
  private IntArrayIterator it = null;

  public XNodeSet() {

  }

  /**
   * @see XObject#getType()
   */
  public int getType() {
    return TYPE;
  }

  /*
   public SegmentedIntSet getValue() {
   return value;
   }
   */
  protected XNodeSet reuse(DTM dtm) {
    this.dtm = dtm;
    forward = true;
    n = 0;
    return this;
  }

  protected XNodeSet reuse(DTM dtm, int x) {
    this.dtm = dtm;
    forward = true;
    value[0] = x;
    n = 1;
    return this;
  }

  protected XNodeSet reuse(XNodeSet xns) {
    this.dtm = xns.dtm;
    this.n = xns.n;
    this.forward = xns.forward;
    ensureCapacity(xns.n);
    System.arraycopy(xns.value, 0, this.value, 0, n);
    return this;
  }

  /**
   * Constructs an XNodeSet, which is the result of performing the location step <axis, nodeTest>,
   * taking x as a basis.
   */
  public XNodeSet reuse(XPathContext xcont, String axis, QName nodeTest) throws XPathException {
    DTM dtm = xcont.dtm;
    int x = xcont.node;
    this.xcont = xcont;
    this.dtm = dtm;
    int t = DTM.ELEMENT_NODE; // the node test (for type), or the principal node type
    String piTarget = null; // the parameter for the 'processing-instruction(...)' node-test
    n = 0;

    if (nodeTest.localname.charAt(nodeTest.localname.length() - 1) == ')') {
      int indexOfOpeningBracket = 0;
      factory.chXNodeSetInst.clear();

      for (int i = 0; (i < nodeTest.localname.length()) && (nodeTest.localname.charAt(i) != '('); i++) {
        indexOfOpeningBracket++;
        factory.chXNodeSetInst.append(nodeTest.localname.charAt(i));
      } 

      Integer tmp = (Integer) HASHTABLE.get(factory.chXNodeSetInst);

      if (tmp == null) {
        throw new XPathException("Node-test not recognized, '" + nodeTest + "'.");
      }

      t = tmp.intValue();

      if ((t == DTM.PROCESSING_INSTRUCTION_NODE) && (indexOfOpeningBracket < nodeTest.localname.length() - 3)) {
        piTarget = nodeTest.localname.getString().substring(indexOfOpeningBracket + 2, nodeTest.localname.length() - 2);
      }

      nodeTest = null;
    }

    /*
     if ((nodeTest != null) && NT_ALL.equals(nodeTest.localname)) {
     nodeTest = null; // no name checking
     }
     */
    //
    if (axis.equals(AXIS_CHILD)) {
      for (int c = dtm.firstChild[x]; c != DTM.NONE; c = dtm.nextSibling[c]) {
        if (checkNodeTypeAndNameOf(c, t, nodeTest, piTarget)) {
          add(c);
        }
      } 

      forward = true;
    } else if (axis.equals(AXIS_SELF)) {
      if (checkNodeTypeAndNameOf(x, t, nodeTest, piTarget)) {
        add(x);
      }

      forward = true;
    } else if (axis.equals(AXIS_PRECEDING)) {
      if (x > 1) {
        for (int y = dtm.getDocumentElement(x); y < x; y++) {
          if (checkNodeTypeAndNameOf(y, t, nodeTest, piTarget)) {
            add(y);
          }
        } 

        markAncestors(x);
        markNamespaceAndAttributeNodes();
        compact();
        /*
         removeAncestors(x);
         removeNamespaceAndAttributeNodes();
         */
      }

      forward = false;
      revert();
    } else if (axis.equals(AXIS_FOLLOWING)) {
      if (x < dtm.size) {
        for (int y = x + 1; y < dtm.size; y++) {
          if (checkNodeTypeAndNameOf(y, t, nodeTest, piTarget)) {
            add(y);
          }
        } 

        markDescendantsAndSelf(x);
        markNamespaceAndAttributeNodes();
        compact();
        /*
         removeAncestors(x);
         removeNamespaceAndAttributeNodes();
         */
      }

      forward = true;
    } else if (axis.equals(AXIS_PARENT)) {
      int p = dtm.parent[x];

      if (p != DTM.NONE) {
        if (checkNodeTypeAndNameOf(p, t, nodeTest, piTarget)) {
          add(p);
        }
      }

      forward = false;
      revert();
    } else if (axis.equals(AXIS_DESCEDANT_OR_SELF)) {
      //      System.out.println("XNodeSet. DOS: before addDEscendantAnd Self");
      addDescendantsAndSelf(x, t, nodeTest, piTarget);
      //      System.out.println("XNodeSet. DOS: AFTERRRRRRRRRRRR addDEscendantAnd Self");
      forward = true;
    } else if (axis.equals(AXIS_DESCEDANT)) {
      addDescendantsAndSelf(x, t, nodeTest, piTarget);
      remove(x);
      forward = true;
    } else if (axis.equals(AXIS_ATTRIBUTE)) {
      t = DTM.ATTRIBUTE_NODE;

      if (dtm.nodeType[x] == DTM.ELEMENT_NODE) {
        //int a = x + 1;
        int a = dtm.firstAttr[x];

        //if (dtm.name[a] != null && dtm.name[a].equals("xft-xliff:id")) {
        //  System.out.println("XNodeSet a=" + a + " name = " + dtm.name[a] + " nodeTest=" + nodeTest);
        //}
        while (a > -1 && (a < dtm.size) && ((dtm.nodeType[a] == DTM.ATTRIBUTE_NODE) || (dtm.nodeType[a] == DTM.NAMESPACE_NODE))) {
          if (checkNodeTypeAndNameOf(a, t, nodeTest, piTarget)) {
            add(a);
          }

          //a++
          if (a > -1) {
            a = dtm.nextSibling[a];
          }
        }
      } else if (dtm.nodeType[x] == DTM.ATTRIBUTE_NODE) {
        int a = x;
        if (checkNodeTypeAndNameOf(a, t, nodeTest, piTarget)) {
          add(a);
        }
        
      }

      forward = true;
    } else if (axis.equals(AXIS_ANCESTOR)) {
      for (int a = dtm.parent[x]; a != DTM.NONE; a = dtm.parent[a]) {
        if (checkNodeTypeAndNameOf(a, t, nodeTest, piTarget)) {
          add(a);
        }
      } 

      forward = false;
      revert();
    } else if (axis.equals(AXIS_ANCESTOR_OR_SELF)) {
      for (int a = x; a != DTM.NONE; a = dtm.parent[a]) {
        if (checkNodeTypeAndNameOf(a, t, nodeTest, piTarget)) {
          add(a);
        }
      } 

      forward = false;
      revert();
    } else if (axis.equals(AXIS_PRECEDING_SIBLING)) {
      for (int s = dtm.previousSibling[x]; s != DTM.NONE; s = dtm.previousSibling[s]) {
        if (checkNodeTypeAndNameOf(s, t, nodeTest, piTarget)) {
          add(s);
        }
      } 

      forward = false;
      revert();
    } else if (axis.equals(AXIS_FOLLOWING_SIBLING)) {
      for (int s = dtm.nextSibling[x]; s != DTM.NONE; s = dtm.nextSibling[s]) {
        if (checkNodeTypeAndNameOf(s, t, nodeTest, piTarget)) {
          add(s);
        }
      } 

      forward = true;
    } else if (axis.equals(AXIS_NAMESPACE)) {
      addNamespaces(x, nodeTest, piTarget);
//      t = DTM.NAMESPACE_NODE;
//
//      if (dtm.nodeType[x] == DTM.ELEMENT_NODE) {
//        int a = x + 1;
//
//        while ((a < dtm.size) && ((dtm.nodeType[a] == DTM.ATTRIBUTE_NODE) || (dtm.nodeType[a] == DTM.NAMESPACE_NODE))) {
//          if (checkNodeTypeAndNameOf(a, t, nodeTest, piTarget)) {
//            add(a);
//          }
//
//          a++;
//        }
//      }
    } else {
      throw new XPathException("Unknown axis, '" + axis + "::'.");
    }

    //checkOrder();
    return this;
  }
  
  private void addNamespaces(int element, QName nodeTest, String piTarget) {
    if (dtm.nodeType[element] == DTM.ELEMENT_NODE) {
      int a = element + 1;

      while ((a < dtm.size) && ((dtm.nodeType[a] == DTM.ATTRIBUTE_NODE) || (dtm.nodeType[a] == DTM.NAMESPACE_NODE))) {
        if (checkNodeTypeAndNameOf(a, DTM.NAMESPACE_NODE, nodeTest, piTarget)) {
          add(a);
        }

        a++;
      }
      int parentElement = dtm.parent[element];
      if (parentElement >= 0) {
        addNamespaces(parentElement, nodeTest, piTarget);
      }
    }
  }

  private boolean checkNodeTypeAndNameOf(int x, int t, QName name, String piTarget) {
    if (t == 3) {
      if (xcont == null) {
        xcont = dtm.getInitialContext();
      }

      if (xcont.owner != null && xcont.owner.doStripWhiteSpaceNode(xcont, x)) {
        return false;
      }
    }

    if (t != DTM.WHATEVER_NODE) {
      if (dtm.nodeType[x] != t) {
        return false;
      }

      if (t == DTM.PROCESSING_INSTRUCTION_NODE) {
        if (piTarget != null) {
          if (!dtm.name[x].localname.equals(piTarget)) {
            return false;
          }
        }
      }
    }

    if (name == null) {
      return true;
    }

    //if (name.localname.equals(NT_ALL)) {
    if (name.localname.charAt(0) == '*') {
      return (name.prefix.equals("") || dtm.name[x].uri.equals(name.uri));
    } else {
      return (dtm.name[x].localname.equals(name.localname) && dtm.name[x].uri.equals(name.uri));
    }
  }

  private int findLastDescendant(int p) {
    int x = p;

    while (dtm.firstChild[p] != DTM.NONE) {
      for (p = dtm.firstChild[p]; p != DTM.NONE; p = dtm.nextSibling[p]) {
        x = p;
      } 

      p = x;
    }

    return p;
  }

  private void addDescendantsAndSelf(int x, int t, QName name, String piTarget) {
    int lastDescendant = findLastDescendant(x);

    //    System.out.println("XNS: addDAndSelf: x=" + x + ", last = " + lastDescendant + ", qname= " + name + ", t=" + t);;
    if (t == 100 && name == null) {
      ensureCapacity(lastDescendant - x + 100);
    }

    for (; x <= lastDescendant; x++) {
      if (checkNodeTypeAndNameOf(x, t, name, piTarget)) {
        add(x);
      }
    } 

    //    for (int c = dtm.firstChild[x]; c != DTM.NONE;
    //    for (int c = dtm.firstChild[x]; c != DTM.NONE; c = dtm.nextSibling[c]) {
    //      addDescendantsAndSelf(c, t, name, piTarget);
    //    }
  }

  private void markDescendantsAndSelf(int x) {
    mark(x);

    for (int c = dtm.firstChild[x]; c != DTM.NONE; c = dtm.nextSibling[c]) {
      markDescendantsAndSelf(c);
    } 
  }

//  private void removeDescendants(int x) {
//    remove(x);
//
//    for (int c = dtm.firstChild[x]; c != DTM.NONE; c = dtm.nextSibling[c]) {
//      removeDescendants(c);
//    } 
//  }

  private void markAncestors(int x) {
    int q = n - 1;

    for (int a = dtm.parent[x]; a != DTM.NONE; a = dtm.parent[a]) {
      while (true) {
        if (q < 0) {
          return;
        }

        if (value[q] <= a) {
          if (value[q] == a) {
            value[q] = -1;
          }

          break;
        }

        q--;
      }

      mark(a);
    } 

    /*
     int q = n - 1;
     for (int a = dtm.parent[x]; a != DTM.NONE; a = dtm.parent[a]) {
     while (true) {
     if (q < 0) {
     return;
     }
     if (value[q] <= a) {
     if (value[q] == a) {
     value[q] = -1;
     }
     break;
     }
     q--;
     }
     //mark(a);
     }
     */
  }

//  private void removeAncestors(int x) {
//    for (int a = dtm.parent[x]; a != DTM.NONE; a = dtm.parent[a]) {
//      remove(a);
//    } 
//  }

//  private void removeNamespaceAndAttributeNodes() {
//    int j = 0;
//
//    for (int i = 0; i < n; i++) {
//      int t = dtm.nodeType[value[i]];
//
//      if ((t != DTM.NAMESPACE_NODE) && (t != DTM.ATTRIBUTE_NODE)) {
//        value[j] = value[i];
//        j++;
//      }
//    } 
//
//    n = j;
//  }

  private void markNamespaceAndAttributeNodes() {
    for (int i = 0; i < n; i++) {
      if (value[i] != -1) {
        int t = dtm.nodeType[value[i]];

        if ((t == DTM.NAMESPACE_NODE) || (t == DTM.ATTRIBUTE_NODE)) {
          value[i] = -1;
        }
      }
    } 
  }

  //public void uniteWith(XNodeSet xns) {
    /*
     int[] valueNew = new int[this.n + xns.n];
     int nNew = 0;
     int i = 0;
     int j = 0;
     while ((i < this.n) && (j < xns.n)) {
     int x = this.value[i];
     int y = xns.value[j];
     if (x == y) {
     valueNew[nNew] = x;
     nNew++;
     i++;
     j++;
     } else if (x < y) {
     valueNew[nNew] = x;
     nNew++;
     i++;
     } else {
     valueNew[nNew] = y;
     nNew++;
     j++;
     }
     }
     if (i >= this.n) {
     System.arraycopy(xns.value, j, valueNew, nNew, xns.n - j);
     nNew += xns.n - j;
     } else if (j >= xns.n) {
     System.arraycopy(this.value, i, valueNew, nNew, this.n - i);
     nNew += this.n - i;
     }
     value = valueNew;
     n = nNew;
     */
    // Low-level optimized version
/*    int i = 0;
    int j = 0;

    // if the new node-set has no elements - there is no need to unite them, this could lead to a new INT Array and, thus OutOfMemory Exception
    if (xns.n == 0) {
      return;
    }

    int iLimit = n;
    int jLimit = xns.n;
    int[] xnsValue = xns.value;
    //    System.out.println("XNodeSet UniteWith: " + iLimit + ", " + jLimit);
    int[] valueNew = new int[iLimit + jLimit];
    //    System.out.println("XNodeSet UniteWith: new ready");
    int nNew = 0;

    if ((i < iLimit) && (j < jLimit)) {
      while (true) {
        int x = value[i];
        int y = xnsValue[j];

        if (x < y) {
          valueNew[nNew++] = x;

          if (++i >= iLimit) {
            break;
          }
        } else if (x > y) {
          valueNew[nNew++] = y;

          if (++j >= jLimit) {
            break;
          }
        } else {
          valueNew[nNew++] = x;
          // changed because in the previous impl, when there were duplicated types - they were not handled properly
          ++i;
          ++j;

          if (i >= iLimit || j >= jLimit) {
            break;
          }
        }
      }
    }

    if (i >= iLimit) {
      System.arraycopy(xnsValue, j, valueNew, nNew, jLimit - j);
      nNew += jLimit - j;
    } else if (j >= jLimit) {
      System.arraycopy(value, i, valueNew, nNew, n - i);
      nNew += n - i;
    }

    value = valueNew;
    RESIZE_STEP = value.length;
    n = nNew;
  }*/
  
  public void uniteWith(XNodeSet xns) {
    if (xns.n == 0) {
      return;
    }
    int size = n;
    int xnsSize = xns.n;

    int[] xnsValue = xns.value;
    if (value.length < size + xnsSize) {
      int[] newValue = new int[size + xnsSize];
      System.arraycopy(value, 0, newValue, 0, size);
      value = newValue;
    }
    this.n = size;
    for (int i = 0; i < xnsSize; i++) {
      int v = xnsValue[i];
      boolean found = false;
      for (int j = 0; j < size; j++) { //check if already added
        if (value[j] == v) {
          found = true;
          break;
        }
      }
      if (!found) {
        value[this.n++] = v;
      }
    }
    Arrays.sort(value, 0, this.n);
  }  
  

  public void add(int x) {
    ensureCapacity(n + 1);
    value[n] = x;
    n++;
  }

  public void addInterval(int x, int y) {
    ensureCapacity(n + y - x + 1);

    for (int i = x; i <= y; i++) {
      value[n] = i;
      n++;
    } 
  }

  public void mark(int x) {
    for (int i = 0; i < n; i++) {
      if (value[i] == x) {
        value[i] = -1;
        return;
      }
    } 
  }

  public void remove(int x) {
    for (int i = 0; i < n; i++) {
      if (value[i] == x) {
        n--;

        for (int j = i; j < n; j++) {
          value[j] = value[j + 1];
        } 

        /*
         System.arraycopy(value, i + 1, value, i, n - i); // surprisingly, this is slower
         */
        return;
      }
    } 
  }

  /* // not used anywhere
   public void removeInterval(int x, int y) {
   for (int i = 0; i < n; i++) {
   if ((value[i] >= x) && (value[i] <= y)) {
   int j = i + 1;
   while ((j < n) && (value[i] >= x) && (value[i] <= y)) {
   j++;
   }
   int diff = j - i + 1;
   n -= diff;
   for (int k = i; k < n; k++) {
   value[k] = value[k + diff];
   }
   }
   }
   }
   */
  public void clear() {
    n = 0;
  }

  public IntArrayIterator sensitiveIterator() {
    return (forward) ? iterator() : backIterator();
  }

  public IntArrayIterator iterator() {
    ensureIterator();
    it.init(value, 0, n, 1);
    return it;
  }

  public IntArrayIterator backIterator() {
    ensureIterator();
    it.init(value, n - 1, -1, -1);
    return it;
  }

  public boolean isEmpty() {
    return (n == 0);
  }

  public boolean contains(int x) {
    for (int i = 0; i < n; i++) {
      if (x == value[i]) {
        return true;
      }
    } 

    return false;
  }

  public int count() {
    return n;
  }

  public String toString() {
    StringBuffer r = new StringBuffer();
    r.append("[ ");

    for (int i = 0; i < n; i++) {
      r.append(value[i]).append(' ');
    } 

    r.append("]");
    return r.toString();
  }

  public void print(int indent) {
    Symbols.printSpace(indent);
//    System.out.println("XNodeSet(" + this + ")");
  }

  public XObject evaluate(XPathContext context) {
    return factory.getXNodeSet(this);
  }

  public int firstInDocumentOrder() {
    return (n == 0) ? DTM.NONE : value[0];
  }

  public int size() {
    return n;
  }

  /**
   *   Returns the k-th element in the set in ascending order, or -1 if no such element exists.
   *   Counting starts from 1.
   *   Order is sensitive, i.e. if the node-set is not forward, counting starts from the end backwards.
   */
  public int getKth(int k) {
    /*
     if ((k < 1) || (k > n)) {
     throw new Error();
     }
     */
    //checkOrdering();
    return (forward) ? value[k - 1] : value[n - k];
  }

  public int[] getNodes(int[] nodes, int start) {
    if (nodes.length < start + n + 1) {
      int[] nodesOld = nodes;
      nodes = new int[start + n + 1];
      System.arraycopy(nodesOld, 0, nodes, 0, nodesOld.length);
    }

    System.arraycopy(value, 0, nodes, start, n);
    nodes[start + n] = -1;
    return nodes;
  }

  /*
   public int[] getNodes() {
   int[] nodes = new int[n];
   System.arraycopy(value, 0, nodes, 0, n);
   return nodes;
   }
   */ 

  
  private CharArray fxres = null;
   
  public CharArray fullStringValue() {
    if (n == 0) {
      return CharArray.EMPTY;
    }
    if (fxres == null) {
      fxres = new CharArray();
    } else {
      fxres.clear();
    }
    for (int i = 0; i < n; i ++) {
      fxres.append(dtm.getStringValue(value[i]));
    }

    return fxres;

  }
   
  public CharArray stringValue() {
    //System.out.println("XNodeSet.stringValur() ");
    if (n == 0) {
      return CharArray.EMPTY;
    }

    //System.out.println("XNodeSet.stringValur() start:" + value[0]);
    //System.out.println("XNodeSet.stringValur() dtm=" +dtm);
    return dtm.getStringValue(value[0]);
  }

  public void stringValue(CharArray ca) {
    //System.out.println("XNodeSet.stringValur() ");
    if (n == 0) {
      ca.set(CharArray.EMPTY);
      return;
    }

    //System.out.println("XNodeSet.stringValur() start:" + value[0]);
    //System.out.println("XNodeSet.stringValur() dtm=" +dtm);
//    dtm.getStringValue(value[0], ca);
    ca.clear();
    dtm.appendStringValue(value[0], ca);
  }

  public boolean match(XPathContext c) throws XPathException {
    throw new XPathException(this.getClass() + " cannot be matched.");
  }

  public XNumber toXNumber() throws XPathException {
    return factory.getXNumber(this);
  }

  public XString toXString() throws XPathException {
    return factory.getXString(this);
  }
  
  public XString toFullXString() throws XPathException {
    return factory.getFullXString(this);
  }
  
  public XBoolean toXBoolean() throws XPathException {
    return factory.getXBoolean(this);
  }
  
  public XNodeSet toXNodeSet() throws XPathException {
    return this;
  }

  public boolean isForward() {
    return forward;
  }

  public void setForward(boolean b) {
    forward = b;
  }

  private void ensureCapacity(int x) {
    //    System.out.println("XNS: Ensure Capacity: " + x + ", value.length=" + value.length);
    if (value.length < x) {
      if (x > RESIZE_STEP) {
        RESIZE_STEP = x + 50;
      }

      //      System.out.println("XNS: Ensure Capacity: " + x + ", " + RESIZE_STEP);
      int[] valueOld = value;
      value = new int[RESIZE_STEP];
      System.arraycopy(valueOld, 0, value, 0, valueOld.length);
      RESIZE_STEP *= 2;
    }
  }

  private void ensureIterator() {
    if (it == null) {
      it = new IntArrayIterator();
    }
  }

  private void revert() {
    int i = 0;
    int j = n - 1;
    int h;

    while (i < j) {
      h = value[i];
      value[i] = value[j];
      value[j] = h;
      i++;
      j--;
    }
  }

  public void compact() {
    int j = 0;

    for (int i = 0; i < n; i++) {
      if (value[i] != -1) {
        value[j++] = value[i];
      }
    } 

    n = j;
  }

  /*
   public void checkOrder() {
   for (int i = 1; i < n; i++) {
   if (value[i - 1] >= value[i]) {
   throw new Error();
   }
   }
   }
   public void sort() {
   Arrays.sort(value, 0, n);
   }
   */
  public void mark(IntArrayIterator i) {
    value[i.getCurrentPosition()] = -1;
  }

  public void sort() {
    Arrays.sort(value, 0, n);
  }
  
    
  public static int[] getAncestry(DTM dtm, int i) {
    if (dtm.parent[i] < 0) {
      return new int[]{0};
    } 
    int count = 0;
    int temp = i;
    while(dtm.previousSibling[temp] >= 0) {
      temp = dtm.previousSibling[temp];
      count = count + 1;
    }
    
    int[] upperBranch = getAncestry(dtm, dtm.parent[i]);
    int[] result = new int[upperBranch.length + 1];
    System.arraycopy(upperBranch, 0, result, 0, upperBranch.length);
    result[result.length - 1] = count;
    return result;   
  }
  
  
  public static int[] getAncestry(Node n) {
    Node parent = n.getParentNode();
    if (parent == null) {
      return new int[]{0};
    }
    int count = 0;
    Node temp = n; 
    while (temp.getPreviousSibling() != null ) {
      temp = temp.getPreviousSibling();
      count = count + 1;
    }
    
    int[] upperBranch = getAncestry(parent);
    int[] result = new int[upperBranch.length + 1];
    System.arraycopy(upperBranch, 0, result, 0, upperBranch.length);
    result[result.length - 1] = count;
    return result;   
  }
  
  public static int getNodeFromAncestry(DTM dtm, int[] ancestry) {
    int rootNode = 0;
    int depth = ancestry.length;
    int next = rootNode;
    //System.out.println("XPathExpressionImpl: depth is " + depth);
    
    
    for (int i = 0; i < depth - 1; i ++ ) {
      //System.out.println("XPathExpressionImp: At depth " + i + " the offset is " + ancestry[i]);
      for (int j = 0; j < ancestry[i]; j ++) {
        next = dtm.nextSibling[next];
      }
      next = dtm.firstChild[next];
    }
    //System.out.println("XPathExpressionImp: At depth " + (ancestry.length - 1) + " the offset is " + ancestry[ancestry.length - 1]);
    if (ancestry.length > 0) {
      for (int i = 0; i < ancestry[ancestry.length - 1]; i ++) {
        next = dtm.nextSibling[next];
      }
    }
    
    return next;
  }

  public static Node getNodeFromAncestry(Document doc, int[] ancestry ) {
    Node rootNode = doc;
    int depth = ancestry.length;
    Node next = rootNode;
    
    for (int i = 0; i < depth - 1; i ++) {
      for (int j = 0; j < ancestry[i]; j ++) {
        next = next.getNextSibling();
      }
      next = next.getFirstChild();
    }
    
    if (ancestry.length > 0) {
      for (int i = 0; i < ancestry[ancestry.length - 1]; i ++) {
        next = next.getNextSibling();
      }
    }
    
    return next;

    
  }



}

