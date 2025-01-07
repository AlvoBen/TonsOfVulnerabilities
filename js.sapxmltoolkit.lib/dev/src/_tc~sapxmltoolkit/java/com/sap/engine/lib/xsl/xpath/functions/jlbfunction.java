package com.sap.engine.lib.xsl.xpath.functions;

import com.sap.engine.lib.xml.dom.NodeListImpl;
import com.sap.engine.lib.xsl.xpath.DTM;
import com.sap.engine.lib.xsl.xpath.XFunction;
import com.sap.engine.lib.xsl.xpath.XPathContext;
import com.sap.engine.lib.xsl.xpath.XPathException;
import com.sap.engine.lib.xsl.xpath.xobjects.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;

/**
 * Implements java language binding. Allows calls for all methods
 * of a single class that have the same name.
 * The class must be already loaded.
 *
 * @author Bogomil Kovachev, bogosmith@yahoo.com
 * @version September 2001
 */
public final class JLBFunction implements XFunction {

  private XPathContext currentContext = null;
  private Class className = null;
  private String functionName = null;
  private Class theClass = null;
  private Object theObject = null;
  private boolean checked = false;
  private Method[] methods = null;
  private Constructor[] constructors = null;
  private Method theMethod;
  private Constructor theConstructor;
  private boolean isConstructor;
  private DTM dtm;
  private static final String[] numericTypes = {"double", "float", "long", "int", "short", "byte", "java.lang.Double", "java.lang.Long", "java.lang.Integer", "java.lang.Short", "java.lang.Byte"};

  //  public JLBFunction(QName qname) throws XPathException {
  //   try {
  //     className    = qname.uri.toString();
  //    functionName = qname.localname.toString();
  //    init();
  //    }catch(ClassNotFoundException e) {
  //     throw new XPathException(e.toString(), e);
  //   }
  //
  //  }
  /**
   * Constucts a new instance of JLBFuncion using the supplied Class instance
   * and method name.
   *
   * @param <code>className</code> the name of the Java class.
   * @param <code>functionName </code> the name of the Java method.
   * @throws XPathException if <code>className</code> contains no method, named <code>functionName</code>
   */
  public JLBFunction(Class className, String functionName) throws XPathException {
    try {
      this.className = className;
      this.functionName = hyphenTrimmer(functionName);
      init();
    } catch (ClassNotFoundException e) {
      throw new XPathException(e.toString(), e);
    }
  }

  /**
   * Checks if the class contains a method with the appropriate types of arguments.
   * <p>
   * The best <code>method</code> is chosen. If there is no
   * appropriate method, or if two or more methods are
   * best, returns false.
   * </p>
   *
   * @param <code>a</code> an array of XObjects used as arguments to the java function
   * @return <code>true</code> if exactly one best method is found in the class
   * or <code>false</code> otherwise.
   */
  public boolean confirmArgumentTypes(XObject[] a) {
    int length = isConstructor ? constructors.length : methods.length;
    double[] efforts = new double[length];
    double minEffort = Double.POSITIVE_INFINITY;
    int position = -1;

    for (int i = 0; i < length; i++) {
      if (!isConstructor) {
        efforts[i] = evaluateEffort(a, methods[i]);
      } else {
        efforts[i] = evaluateEffort(a, constructors[i]);
      }
    } 

    for (int i = 0; i < efforts.length; i++) {
      if (efforts[i] < minEffort) {
        minEffort = efforts[i];
        position = i;
      }
    } 

    if (minEffort == Double.POSITIVE_INFINITY) {
      return false;
    }

    for (int i = 0; i < efforts.length; i++) {
      if ((efforts[i] == minEffort) && i != position) {
        return false;
      }
    } 

    if (!isConstructor) {
      theMethod = methods[position];
    } else {
      theConstructor = constructors[position];
    }

    return true;
  }

  /**
   *  This is the actual invocation of the function.
   *  <p>
   *  The second argument is  solely responsible for
   *  compliance with the XFunction interface.
   *  If we are targeting a static method, the first argument contains
   *  all the arguments
   *  <p>
   *
   *  @param <code>a</code> an XObject array containing the arguments
   *  for the method- or constructor call.
   *  @param <code>context</code> used for interface compatibility.
   *  @return an XObject, encapsulating the return form the Java function
   *  @throws <code>XPathException</code>
   */
  public XObject execute(XObject[] a, XPathContext context) throws XPathException {
    currentContext = context;

    if (confirmArgumentTypes(a)) {
      try {
        Object result = null;

        if (!isConstructor) {
          if (Modifier.isStatic(theMethod.getModifiers())) {
            result = theMethod.invoke(null, prepareArguments(a, theMethod));
            /* invocation */
          } else {
            XObject[] arguments = new XObject[a.length - 1]; // If we are targeting an instance method

            for (int i = 0; i < arguments.length; i++) { // the first argument is the object  
              arguments[i] = a[i + 1]; // upon which we execute it. 
            } 

            result = theMethod.invoke(((XJavaObject) a[0]).getObject(), prepareArguments(arguments, theMethod));
            /* invocation */
          }
        } else {
          try {
            result = theConstructor.newInstance(prepareArguments(a, theConstructor));
            /* invocation */
          } catch (InstantiationException i) {
            throw new XPathException(i.toString(), i);
          }
        }

        if (result instanceof java.lang.Double) {
          return context.getXFactCurrent().getXNumber(((Double) result).doubleValue());
        }

        if (result instanceof java.lang.Float) {
          return context.getXFactCurrent().getXNumber(((Float) result).floatValue());
        }

        if (result instanceof java.lang.Integer) {
          return context.getXFactCurrent().getXNumber(((Integer) result).intValue());
        }

        if (result instanceof java.lang.Short) {
          return context.getXFactCurrent().getXNumber(((Short) result).shortValue());
        }

        if (result instanceof java.lang.Byte) {
          return context.getXFactCurrent().getXNumber(((Byte) result).byteValue());
        }

        if (result instanceof java.lang.Long) {
          return context.getXFactCurrent().getXNumber(((Long) result).longValue());
        }

        if (result instanceof java.lang.Character) {
          return context.getXFactCurrent().getXNumber(((Character) result).charValue());
        }

        if (result == null) {
          return context.getXFactCurrent().getXJavaObject(null);
        }

        if (result instanceof Void) {
          return context.getXFactCurrent().getXNodeSet(context.dtm);
        }

        if (result instanceof String) {
          return context.getXFactCurrent().getXString((String) result);
        }

        if (result instanceof Boolean) {
          return context.getXFactCurrent().getXBoolean(((Boolean) result).booleanValue());
        }

        XObjectFactory xof = context.getXFactCurrent();
        return xof.getXJavaObject(result);
      } catch (java.lang.IllegalAccessException i) {
        //$JL-EXC$
        i.printStackTrace();
        return null;
      } catch (java.lang.reflect.InvocationTargetException i) {
        //$J00L-EXC$
        //i.printStackTrace();
        throw new XPathException("Could not execute operation on Java Object.", i);
        //return null;
      }
    } else {
      throw new XPathException("confirmArgumentTypes failed!!");
    }
  }

  /**
   * Represents the java method name.
   *
   * @return the name of the java method
   */
  public String getFunctionName() {
    return functionName;
  }

  /**
   *  Executed once per instantiation of the class.
   */
  private void init() throws ClassNotFoundException {
    theClass = className;
    Vector v = new Vector();

    if (functionName.equals("new")) {
      isConstructor = true;
      Constructor[] all = theClass.getConstructors();

      for (int i = 0; i < all.length; i++) {
        checked = true;
        v.add(all[i]);
      } 

      if (v.size() < 1) {
        throw new ClassNotFoundException("Class doesn't have a public constructor!");
      }

      constructors = new Constructor[v.size()];

      for (int i = 0; i < constructors.length; i++) {
        constructors[i] = (Constructor) v.get(i);
      } // now all constructors[] contains all the constructors of the Class
    } else {
      isConstructor = false;
      Method[] all = theClass.getMethods();

      for (int i = 0; i < all.length; i++) {
        if (all[i].getName().equals(functionName)) {
          checked = true;
          v.add(all[i]);
        }
      } 

      if (v.size() < 1) {
        throw new ClassNotFoundException("Class doesn't have appropriate method!");
      }

      methods = new Method[v.size()];

      for (int i = 0; i < methods.length; i++) {
        methods[i] = (Method) v.get(i);
      } // now all methods[] are named functionName 
    }
  }

  /**
   *  Checks if the first argument has the same number of elements as
   *  the second's argument list.
   */
  private boolean areMatching(XObject[] a, Method m) {
    Class[] types = m.getParameterTypes();

    if (Modifier.isStatic(m.getModifiers())) {
      return (a.length != types.length) ? false : true;
    } else {
      return (a.length != types.length + 1) ? false : true;
    }
  }

  /**
   *  Checks if the first argument has the right number of elements.
   *  the second's argument list.
   */
  private boolean areMatching(XObject[] a, Constructor c) {
    Class[] types = c.getParameterTypes();
    return (a.length != types.length) ? false : true;
  }

  /**
   * As the name implies, prepares arguments for execution.
   */
  private Object[] prepareArguments(XObject[] a, AccessibleObject m) throws XPathException {
    Class[] parameters = null;
    Object[] result = new Object[a.length];

    if (m instanceof Method) {
      parameters = ((Method) m).getParameterTypes();
    } else {
      parameters = ((Constructor) m).getParameterTypes();
    }

    for (int i = 0; i < parameters.length; i++) {
      result[i] = convert(a[i], parameters[i]);
    } 

    return result;
  }

  /**
   * Converts an XObject to an object of type Class, so that it can be
   * passed as an argument for the method call.
   */
  private Object convert(XObject from, Class to) throws XPathException {
    int xtype = from.getType();
    String type = to.getName();

    switch ((short) xtype) {
      case XString.TYPE: {
        /* recieved an XString */
        String s = from.toString();

        if ((type.equals("char") || type.equals("java.lang.Character")) && s.length() == 1) {
          return new Character(s.charAt(0));
        }

        if (contains(numericTypes, type)) {
          return convert(from.toXNumber(), to);
        }

        if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
          return convert(from.toXBoolean(), to);
        }

        if (type.equals("java.lang.String") || type.equals("java.lang.Object")) {
          return s;
        }

        throw new XPathException("Incompatible types !@#!");
      }
      case XNumber.TYPE: {
        /* recieved an XNumber*/
        XNumber xnumb = (XNumber) from;
        double d = xnumb.getValue();

        if (type.equals("double") || type.equals("java.lang.Double") || type.equals("java.lang.Object")) {
          return new Double(d);
        }

        if (type.equals("float") || type.equals("java.lang.Float")) {
          return new Float((float) d);
        }

        if (type.equals("long") || type.equals("java.lang.Long")) {
          return new Long((long) d);
        }

        if (type.equals("int") || type.equals("java.lang.Integer")) {
          return new Integer((int) d);
        }

        if (type.equals("short") || type.equals("java.lang.Short")) {
          return new Short((short) d);
        }

        if (type.equals("char") || type.equals("java.lang.Character")) {
          return new Character((char) d);
        }

        if (type.equals("byte") || type.equals("java.lang.Byte")) {
          return new Byte((byte) d);
        }

        if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
          return convert(from.toXBoolean(), to);
        }

        if (type.equals("java.lang.String")) {
          return convert(from.toXString(), to);
        }

        throw new XPathException("Incompatible types !@#!");
      }
      case XBoolean.TYPE: {
        XBoolean xbool = (XBoolean) from;
        boolean b = xbool.getValue();

        if (type.equals("boolean") || type.equals("java.lang.Boolean") || type.equals("java.lang.Object")) {
          return new Boolean(b);
        }

        if (type.equals("java.lang.String")) {
          return convert(from.toXString(), to);
        }

        if (contains(numericTypes, type)) {
          return convert(from.toXNumber(), to);
        }

        throw new XPathException("Incompatible types !@#!");
      }
      case XJavaObject.TYPE: {
        XJavaObject xobj = (XJavaObject) from;
        Object o = xobj.getObject();

        if (to.isInstance(o)) {
          return o;
        }

        if (type.equals("java.lang.String")) {
          return o.toString();
        }

        if (type.equals("char") || type.equals("java.lang.Character")) {
          return convert(from.toXString(), to);
        }

        if (contains(numericTypes, type)) {
          return convert(from.toXNumber(), to);
        }

        throw new XPathException("Trying to convert form XNodeSet to : " + type);
      }
      case XNodeSet.TYPE: {
        XNodeSet xn = (XNodeSet) from;
        dtm = currentContext.dtm;
        dtm.initializeDOM();

        if (xn.size() == 1) {
          int node = xn.getKth(1) ;
          if (dtm.nodeType[node] == DTM.DOCUMENT_NODE) {
            int child = dtm.firstChild[node];
            xn = new XNodeSet();
            while (child != DTM.NONE) {
              xn.add(child);
              child = dtm.nextSibling[child];
            }
          }
        }

        if (type.equals("org.w3c.dom.NodeList")) {
          NodeList nl = new NodeListImpl();
          IntArrayIterator in = xn.iterator();

          while (in.hasNext()) {
            int i = in.next();
            Node nod = dtm.domtree[i];
            //            LogWriter.getSystemLogWriter().println("JLBFunction.convert(): check for NodeList dtm.domtree[" + i + "] = " + dtm.domtree[i].getClass() + "\n" + dtm.domtree[i]);
            ((NodeListImpl) nl).add(nod);
          }

          return nl;
        }

        //NodeList nl = new NodeListImpl();
        IntArrayIterator in = xn.iterator();
        int i = in.next();
        Node n = dtm.domtree[i];

        if (to.isAssignableFrom(n.getClass())) {
          return n;
        }
      }
      default: {
        throw new XPathException("Trying to convert form XNodeSet to : " + type);
      }
    }
  }

  /**
   * Used to determine the best method in a class.
   */
  private double evaluateEffort(XObject[] a, Method m) {
    boolean isStatic = Modifier.isStatic(m.getModifiers());

    if (!areMatching(a, m)) {
      return Double.POSITIVE_INFINITY;
    }

    double sum = 0;
    Class[] types = m.getParameterTypes();

    if (!isStatic) {
      if (a[0].getType() == XJavaObject.TYPE) {
        XJavaObject xobj = (XJavaObject) a[0];
        Object o = xobj.getObject();

        if (theClass.isInstance(o)) {
          for (int i = 0; i < a.length - 1; i++) {
            sum = sum + effort(a[i + 1], types[i]);
          } 

          return sum;
        } else {
          return Double.POSITIVE_INFINITY;
        }
      } else {
        return Double.POSITIVE_INFINITY;
      }
    } else {
      for (int i = 0; i < a.length; i++) {
        sum = sum + effort(a[i], types[i]);
      } 

      return sum;
    }
  }

  /**
   * Used to determine the best constructor in a class.
   */
  private double evaluateEffort(XObject[] a, Constructor c) {
    if (!areMatching(a, c)) {
      return Double.POSITIVE_INFINITY;
    }

    /* else...*/
    double sum = 0;
    Class[] types = c.getParameterTypes();

    for (int i = 0; i < a.length; i++) {
      sum = sum + effort(a[i], types[i]);
    } 

    return sum;
  }

  /**
   * The actual implementation of the effort table.
   */
  private double effort(XObject from, Class to) {
    int xtype = from.getType();
    String type = to.getName();

    switch ((short) xtype) {
      case XBoolean.TYPE: {
        if (type.equals("boolean")) {
          return 0;
        }

        if (type.equals("java.lang.Boolean")) {
          return 0;
        }

        if (type.equals("byte")) {
          return 3;
        }

        if (type.equals("java.lang.Byte")) {
          return 4;
        }

        if (type.equals("char")) {
          return Double.POSITIVE_INFINITY;
        }

        if (type.equals("java.lang.Character")) {
          return Double.POSITIVE_INFINITY;
        }

        if (type.equals("double")) {
          return 3;
        }

        if (type.equals("java.lang.Double")) {
          return 4;
        }

        if (type.equals("float")) {
          return 3;
        }

        if (type.equals("java.lang.Float")) {
          return 4;
        }

        if (type.equals("int")) {
          return 3;
        }

        if (type.equals("java.lang.Integer")) {
          return 4;
        }

        if (type.equals("long")) {
          return 3;
        }

        if (type.equals("java.lang.Long")) {
          return 4;
        }

        if (type.equals("short")) {
          return 3;
        }

        if (type.equals("java.lang.Short")) {
          return 4;
        }

        if (type.equals("java.lang.String")) {
          return 2;
        }

        return 1;
        /*any other object*/
      }
      case XNumber.TYPE: {
        if (type.equals("boolean")) {
          return 14;
        }

        if (type.equals("java.lang.Boolean")) {
          return 15;
        }

        if (type.equals("byte")) {
          return 12;
        }

        if (type.equals("java.lang.Byte")) {
          return 13;
        }

        if (type.equals("char")) {
          return 10;
        }

        if (type.equals("java.lang.Character")) {
          return 11;
        }

        if (type.equals("double")) {
          return 0;
        }

        if (type.equals("java.lang.Double")) {
          return 1;
        }

        if (type.equals("float")) {
          return 2;
        }

        if (type.equals("java.lang.Float")) {
          return 3;
        }

        if (type.equals("int")) {
          return 6;
        }

        if (type.equals("java.lang.Integer")) {
          return 7;
        }

        if (type.equals("long")) {
          return 4;
        }

        if (type.equals("java.lang.Long")) {
          return 5;
        }

        if (type.equals("short")) {
          return 8;
        }

        if (type.equals("java.lang.Short")) {
          return 9;
        }

        if (type.equals("java.lang.String")) {
          return 16;
        }

        return 17;
        /*any other object*/
      }
      case XString.TYPE: {
        if (type.equals("boolean")) {
          return 6;
        }

        if (type.equals("java.lang.Boolean")) {
          return 7;
        }

        if (type.equals("byte")) {
          return 4;
        }

        if (type.equals("java.lang.Byte")) {
          return 5;
        }

        if (type.equals("char")) {
          return 2;
        }

        if (type.equals("java.lang.Character")) {
          return 3;
        }

        if (type.equals("double")) {
          return 4;
        }

        if (type.equals("java.lang.Double")) {
          return 5;
        }

        if (type.equals("float")) {
          return 4;
        }

        if (type.equals("java.lang.Float")) {
          return 5;
        }

        if (type.equals("int")) {
          return 4;
        }

        if (type.equals("java.lang.Integer")) {
          return 5;
        }

        if (type.equals("long")) {
          return 4;
        }

        if (type.equals("java.lang.Long")) {
          return 3;
        }

        if (type.equals("short")) {
          return 4;
        }

        if (type.equals("java.lang.Short")) {
          return 5;
        }

        if (type.equals("java.lang.String")) {
          return 0;
        }

        return 1; //any other object	
      }
      case XJavaObject.TYPE: {
        if (type.equals("boolean")) {
          return Double.POSITIVE_INFINITY;
        }

        if (type.equals("java.lang.Boolean")) {
          return Double.POSITIVE_INFINITY;
        }

        if (type.equals("byte")) {
          return 4;
        }

        if (type.equals("java.lang.Byte")) {
          return 5;
        }

        if (type.equals("char")) {
          return 2;
        }

        if (type.equals("java.lang.Character")) {
          return 3;
        }

        if (type.equals("double")) {
          return 4;
        }

        if (type.equals("java.lang.Double")) {
          return 5;
        }

        if (type.equals("float")) {
          return 4;
        }

        if (type.equals("java.lang.Float")) {
          return 5;
        }

        if (type.equals("int")) {
          return 4;
        }

        if (type.equals("java.lang.Integer")) {
          return 5;
        }

        if (type.equals("long")) {
          return 4;
        }

        if (type.equals("java.lang.Long")) {
          return 3;
        }

        if (type.equals("short")) {
          return 4;
        }

        if (type.equals("java.lang.Short")) {
          return 5;
        }

        if (type.equals("java.lang.String")) {
          return 1;
        }

        if (type.equals("java.lang.Object")) {
          return 1;
        }

        XJavaObject xobj = (XJavaObject) from;
        Object o = xobj.getObject();
        return to.isInstance(o) ? 0 : Double.POSITIVE_INFINITY;
      }
      case XNodeSet.TYPE: {
        if (type.equals("org.w3c.dom.NodeList")) {
          return 0;
        }

        if (type.equals("org.w3c.dom.Node")) {
          return 1;
        }

        return Double.POSITIVE_INFINITY;
      }
      default: {
        return Double.POSITIVE_INFINITY;
      }
    }
  }

  /**
   * Trims hyphens in xslt names, capitalizing the immediate post-hyphen letters.
   */
  private String hyphenTrimmer(String source) {
    StringBuffer result = new StringBuffer();

    for (int i = 0; i < source.length(); i++) {
      if (source.charAt(i) != '-') {
        result.append(source.charAt(i));
      } else {
        if (i == source.length() - 1) {
          break;
        }

        if (source.charAt(i + 1) == '-') {
          continue;
        } else {
          result.append(Character.toUpperCase(source.charAt(i + 1)));
          i = i + 1;
        }
      }
    } 

    return result.toString();
  }

  private boolean contains(String[] arr, String what) {
    for (int i = 0; i < arr.length; i++) {
      if (arr[i].equals(what)) {
        return true;
      }
    } 

    return false;
  }

}

