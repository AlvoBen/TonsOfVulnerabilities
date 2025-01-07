package com.sap.engine.services.jndi.cosnaming;

import java.util.*;
import java.rmi.Remote;
import javax.naming.*;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.ORB;

import javax.naming.directory.InvalidAttributesException;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;

import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.engine.services.jndi.NamingManager;
import com.sap.engine.services.jndi.implclient.ClientContext;
import com.sap.engine.services.jndi.implclient.IIOPReferenceFactory;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class NamingContextImpl extends NamingContextExtBaseImpl {

	private final static Location LOG_LOCATION = Location.getLocation(NamingContextImpl.class);

  private static final AlreadyBound ALREADY_BOUND = new AlreadyBound();
  static final InvalidName INVALID_NAME = new InvalidName();
  private static final NotEmpty NOT_EMPTY = new NotEmpty();
  private static final NotFound NOT_FOUND = new NotFound(NotFoundReason.missing_node, new NameComponent[0]);
  private static final CannotProceed CANNOT_PROCEED = new CannotProceed(null, new NameComponent[0]);
  private static Context jndiContext = null;
  private static Properties connectProperties = null;
  private static ORB orb = ORB.init();
  private String root;
  /**
   * serial version UID
   */
  static final long serialVersionUID = 4494267120473619684L;

  public NamingContextImpl() {
    this("");
  }

  private static final char[] escapeExceptions = {';', '/', ':', '?', '@', '&', '=', '+', '$', ',', '-', '_', '.', '!', '~', '*', '\u0092', '(', ')'};
  String[] ids = {"IDL:omg.org/CosNaming/NamingContextExt:1.0", "IDL:omg.org/CosNaming/NamingContext:1.0"};

  public NamingContextImpl(String root) {
    setRoot(root);

    if (connectProperties == null) {
      connectProperties = new Properties();
      connectProperties.put("server", "true");
      connectProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
    }

    if (jndiContext == null) {
      try {
        jndiContext = new ClientContext(connectProperties, NamingManager.getNamingManager().getProxy().getNewServerContext(false, false), false, new IIOPReferenceFactory(), null);
      } catch (Exception e) {
        SimpleLogger.traceThrowable(Severity.ERROR,LOG_LOCATION,e, "ASJ.jndi.000018", "NamingContextImpl: cannot get an InitialContext");
      }
    }
  }

  public void bind(NameComponent nameComponent[], org.omg.CORBA.Object obj) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    try {
      String name = createName(nameComponent);
      NamingContext objContext = null;
      try {
        objContext = NamingContextExtHelper.narrow(obj);
      } catch (Exception e) {
        // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here.
        // Please do not remove this comment!
        objContext = null;
      }

      if (objContext == null) {
        jndiContext.bind(name, orb.object_to_string(obj));
      } else {
        bind_context(nameComponent, objContext);
      }
    } catch (Exception ne) {
      if (ne instanceof NameAlreadyBoundException) {
        throw ALREADY_BOUND;
      }
      if (ne instanceof InvalidNameException) {
        throw INVALID_NAME;
      }
      throw CANNOT_PROCEED;
    }
  }

  private static void copyContext(NamingContext source, NamingContext destination) {
    BindingIteratorHolder bih = new BindingIteratorHolder();
    source.list(0, new BindingListHolder(), bih);
    BindingIterator bi = bih.value;
    if (bi == null) {
      return;
    }
    BindingHolder bh = new BindingHolder();
    boolean bool;

    do {
      bool = bi.next_one(bh);
      org.omg.CosNaming.Binding bind = bh.value;
      try {
        org.omg.CORBA.Object obj = source.resolve(bind.binding_name);

        if (bind.binding_type.value() == BindingType._nobject) {
          destination.bind(bind.binding_name, obj);
        } else {
          destination.bind_context(bind.binding_name, NamingContextExtHelper.narrow(obj));
        }
      } catch (Exception e) {
                LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
      }
    } while (bool);
  }

  public void bind_context(NameComponent nameComponent[], NamingContext namingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
    try {
      NamingContext newContext = bind_new_context(nameComponent);
      copyContext(namingContext, newContext);
    } catch (Exception ne) {
      if (ne instanceof NameAlreadyBoundException) {
        throw ALREADY_BOUND;
      }
      if (ne instanceof InvalidAttributesException) {
        throw INVALID_NAME;
      }
      throw CANNOT_PROCEED;
    }
  }

  public void rebind(NameComponent nameComponent[], org.omg.CORBA.Object obj) throws NotFound, CannotProceed, InvalidName {
    try {
      String name = createName(nameComponent);
      NamingContext objContext = null;
      try {
        objContext = NamingContextExtHelper.narrow(obj);
      } catch (Exception e) {
        // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here.
        // Please do not remove this comment!
        objContext = null;
      }

      if (objContext == null) {
        jndiContext.rebind(name, orb.object_to_string(obj));
      } else {
        rebind_context(nameComponent, objContext);
      }
    } catch (Exception ne) {
      if (ne instanceof NameAlreadyBoundException) {
        return;
      }
      if (ne instanceof InvalidNameException) {
        throw INVALID_NAME;
      }
      throw CANNOT_PROCEED;
    }
  }

  public void rebind_context(NameComponent nameComponent[], NamingContext namingContext) throws NotFound, CannotProceed, InvalidName {
    try {
      unbind(createName(nameComponent));
      NamingContext newContext = bind_new_context(nameComponent);
      copyContext(namingContext, newContext);
    } catch (Exception ne) {
      if (ne instanceof NameAlreadyBoundException) {
        return;
      }
      if (ne instanceof InvalidAttributesException) {
        throw INVALID_NAME;
      }
      throw CANNOT_PROCEED;
    }
  }

  public org.omg.CORBA.Object resolve(NameComponent nameComponent[]) throws NotFound, CannotProceed, InvalidName {
    try {
      String name = createName(nameComponent);
      Object result = null;

      try {   //some ejbeans may be Remote but must be lookuped from ejbCosNaming subcontext
        result = jndiContext.lookup("ejbCosNaming" + CosNamingContext.DELIM + name);
      } catch (Exception ex) {
        if (JNDIFrame.location.beDebug()) {
          JNDIFrame.location.logT(JNDIFrame.location.getEffectiveSeverity(), "Cosnaming exception during lookup of " + name + " . Exception : " + ex.toString());
        }
        result = jndiContext.lookup(name);
      }

//      if (result instanceof UnsatisfiedReference) {
//        result = jndiContext.lookup("ejbCosNaming" + CosNamingContext.DELIM + name);
//      }

      if (result instanceof org.omg.CORBA.Object) {
        return (org.omg.CORBA.Object) result;
      }

      if (result instanceof String) {
        return orb.string_to_object((String) result);
      }

      if (result instanceof Context) {
        return this instanceof NamingContextExt ? new NamingContextExtImpl(name) : new NamingContextImpl(name);
      }

      if (result instanceof Remote) {
        Tie t = Util.getTie((Remote) result);
        if (t.orb() == null) {
          org.omg.CORBA.ORB orb1 = ORB.init(new String[0], null);
          orb1.connect((org.omg.CORBA.Object) t);
        }
        return t.thisObject();
      }

    } catch (Exception e) {
      if (e instanceof NameNotFoundException) {
        throw NOT_FOUND;
      }
      if (e instanceof InvalidNameException) {
        throw INVALID_NAME;
      }
      throw CANNOT_PROCEED;
    }
    throw CANNOT_PROCEED; // Not an org.omg.CORBA.Object
  }

  private void unbind(String contextName) throws Exception {
    NamingEnumeration ne = jndiContext.list(contextName);
    String temp = contextName + CosNamingContext.DELIM;

    while (ne.hasMore()) {
      NameClassPair ncp = (NameClassPair) ne.next();
      String jndiNameStr = temp + ncp.getName();
      String className = ncp.getClassName();

      if ("javax.naming.Context".equals(className)) {
        unbind(jndiNameStr);
      } else {
        jndiContext.unbind(jndiNameStr);
      }
    }

    jndiContext.destroySubcontext(contextName);
  }

  public void unbind(NameComponent nameComponent[]) throws NotFound, CannotProceed, InvalidName {
    try {
      String name = createName(nameComponent);
      Object result = jndiContext.lookup(name);

      if (result instanceof Context) {
        unbind(name);
      } else {
        jndiContext.unbind(name);
      }
    } catch (Exception ne) {
      if (ne instanceof NameNotFoundException) {
        throw NOT_FOUND;
      }
      if (ne instanceof InvalidNameException) {
        throw INVALID_NAME;
      }
      throw CANNOT_PROCEED;
    }
  }

  public void list(int how_many, BindingListHolder bindingListHolder, BindingIteratorHolder bindingIteratorHolder) {
    if (how_many < 0) {
      how_many = 0;
    }
    try {
      NamingEnumeration ne = jndiContext.list(getRoot());
      List bindingList = new ArrayList();

      while (ne.hasMore()) {
        NameClassPair ncp = (NameClassPair) ne.next();
        String jndiNameStr = ncp.getName();
        String className = ncp.getClassName();
        Name jndiName = null;
        try {
          jndiName = CosNamingContext.nameParser.parse(jndiNameStr);
        } catch (Exception e) {
                    LOG_LOCATION.traceThrowableT(Severity.PATH, "", e);
          jndiName = null;
        }
        if (jndiName == null) {
          continue;
        }
        NameComponent[] cosName = CosNamingContext.toCosName(jndiName);
        BindingType type = "javax.naming.Context".equals(className) ? BindingType.ncontext : BindingType.nobject;
        bindingList.add(new org.omg.CosNaming.Binding(cosName, type));
      }

      int fullSize = bindingList.size();
      int size = how_many < fullSize ? how_many < 0 ? 0 : how_many : fullSize;
      bindingListHolder.value = (org.omg.CosNaming.Binding[]) bindingList.subList(0, size).toArray(new org.omg.CosNaming.Binding[size]);
      bindingIteratorHolder.value = size < fullSize ? new BindingIteratorImpl(bindingList.subList(size, fullSize)) : null;
    } catch (Exception ne) {
            LOG_LOCATION.traceThrowableT(Severity.PATH, "", ne);
    }
  }

  public NamingContext new_context() {
    return null;
  }

  public NamingContext bind_new_context(NameComponent nameComponent[]) throws NotFound, AlreadyBound, CannotProceed, InvalidName {
    try {
      String name = createName(nameComponent);
      jndiContext.createSubcontext(name);
      return this instanceof NamingContextExt ? new NamingContextExtImpl(name) : new NamingContextImpl(name);
    } catch (Exception ne) {
      if (ne instanceof NameAlreadyBoundException) {
        throw ALREADY_BOUND;
      }
      if (ne instanceof InvalidAttributesException) {
        throw INVALID_NAME;
      }
      throw CANNOT_PROCEED;
    }
  }

  public void destroy() throws NotEmpty {
    try {
      jndiContext.destroySubcontext(getRoot());
    } catch (Exception ne) {
      throw NOT_EMPTY;
    }
  }

  private static Name toJNDIName(NameComponent nameComponent[]) throws NamingException {
    try {
      return new CompositeName(toString(nameComponent));
    } catch (InvalidName in) {
      InvalidNameException ine = new InvalidNameException();
      ine.setRootCause(in);
      throw ine;
    }
  }

  void setRoot(String newRoot) {
    root = newRoot == null ? "" : newRoot;
  }

  public String getRoot() {
    return root;
  }

  public static void setContext(Context newContext) {
    jndiContext = newContext;
  }

  String createName(NameComponent[] nameComponent) throws NamingException {
    String result = getRoot();
    String name = null;
    try {
      name = toString(nameComponent);
    } catch (InvalidName in) {
      InvalidNameException ine = new InvalidNameException();
      ine.setRootCause(in);
      throw ine;
    }

    if (result.length() != 0 && !result.endsWith(CosNamingContext.DELIM) && !name.startsWith(CosNamingContext.DELIM)) {
      result += CosNamingContext.DELIM;
    }

    return result + name;
  }

  static String toString(NameComponent[] name) throws InvalidName {
    StringBuffer sb = new StringBuffer();

    for (int c = 0; c < name.length; c++) {
      sb.append(toString(name[c]) + CosNamingContext.DELIM);
    }

    if (sb.length() > 0) {
      sb.setLength(sb.length() - CosNamingContext.DELIM.length());
    }
    return sb.toString();
  }

  static String toString(NameComponent name) {
    String id = escapeName(name.id);
    String kind = escapeName(name.kind);
    return (id.length() > 0 && kind.length() == 0) ? id : id + "." + kind;
  }

  private static String escapeName(String s) {
    if (s == null) {
      return "";
    }
    StringTokenizer st = new StringTokenizer(s, "./\\", true);
    StringBuffer sb = new StringBuffer();

    while (st.hasMoreTokens()) {
      String token = st.nextToken();

      if (token.equals(".") || token.equals("/") || token.equals("\\")) {
        token = "\\" + token;
      }

      sb.append(token);
    }

    return sb.toString();
  }

  static NameComponent[] toNameComponent(String sn) throws InvalidName {
    List tempList = new ArrayList();

    while (sn != null) {
      NameComponent name = new NameComponent(null, null);
      sn = nameParse(sn, name);
      if (name.id == null) {
        break;
      }
      tempList.add(name);
    }

    NameComponent[] result = new NameComponent[tempList.size()];
    int c = 0;

    for (Iterator i = tempList.iterator(); i.hasNext(); c++) {
      result[c] = (NameComponent) i.next();
    }

    return result;
  }

  private static String nameParse(String sn, NameComponent name) throws InvalidName {
    //System.out.println("parsing -"+sn+"-");
    int size = sn.length();
    if (size == 0) {
      throw INVALID_NAME;
    }
    int id = idParse(sn);
    int kind = kindParse(sn, id);
    name.id = sn.substring(0, id);
    name.kind = id >= kind ? "" : sn.substring(id + 1, kind);
    if (kind >= size) {
      return null;
    }
    return sn.substring(kind + 1);
  }

  private static int idParse(String sn) throws InvalidName {
    if (sn.startsWith("/")) {
      throw INVALID_NAME;
    }
    int size = sn.length();
    int c = 0;

    for (; c < size; c++) {
      char ch = sn.charAt(c);

      if (ch == '\\') {
        if (c == size) {
          throw INVALID_NAME;
        }
        char nextChar = sn.charAt(++c);
        if (nextChar != '.' && nextChar != '/' && nextChar != '\\') {
          throw INVALID_NAME;
        }
      } else if (ch == '.' || ch == '/') {
        break;
      }
    }

    return c;
  }

  private static int kindParse(String sn, int id) throws InvalidName {
    //System.out.println("kinding -"+sn+"-"+id);
    int size = sn.length();
    if (id >= size || sn.charAt(id) != '.') {
      return id;
    }
    int c = 0;

    for (; c < size; c++) {
      char ch = sn.charAt(c);

      if (ch == '\\') {
        if (c == size - 1) {
          throw INVALID_NAME;
        }
        char nextChar = sn.charAt(++c);
        if (nextChar != '.' && nextChar != '/' && nextChar != '\\') {
          throw INVALID_NAME;
        }
      } else if (ch == '/') {
        break;
      }
    }

    return c;
  }

  public org.omg.CORBA.Object resolve_str(String sn) throws NotFound, CannotProceed, InvalidName {
    return this.resolve(to_name(sn));
  }

  public NameComponent[] to_name(String sn) throws InvalidName {
    return NamingContextImpl.toNameComponent(sn);
  }

  public String to_string(NameComponent[] n) throws InvalidName {
    return NamingContextImpl.toString(n);
  }

  public String to_url(String addr, String sn) throws InvalidAddress, InvalidName {
    if (addr == null || addr.length() == 0) {
      throw new InvalidAddress();
    }
    if (sn == null) {
      sn = "";
    }
    addr = escapeURL(addr);
    sn = escapeURL(sn);
    return "corbaname://" + addr + "#" + sn;
  }

  private static String escapeURL(String s) {
    StringBuffer sb = new StringBuffer();
    int size = s.length();

    for (int c = 0; c < size; c++) {
      char ch = s.charAt(c);

      if (isEscapable(ch)) {
        sb.append(escape(ch));
      } else {
        sb.append(ch);
      }
    }

    return sb.toString();
  }

  private static boolean isEscapable(char ch) {
    if (Character.isLetterOrDigit(ch)) {
      return false;
    }

    for (int c = 0; c < escapeExceptions.length; c++) {
      if (ch == escapeExceptions[c]) {
        return false;
      }
    }

    return true;
  }

  private static String escape(char ch) {
    String hex = "0123456789abcdef";
    int charAsInt = ((int) ch) & 0x00FF;
    return "%" + hex.charAt(charAsInt >> 4) + hex.charAt(charAsInt & 0x000F);
  }

}

