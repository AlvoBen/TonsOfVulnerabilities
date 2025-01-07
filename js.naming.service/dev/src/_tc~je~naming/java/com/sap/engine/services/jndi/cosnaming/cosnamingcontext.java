package com.sap.engine.services.jndi.cosnaming;

import java.util.*;
import javax.naming.*;
import javax.rmi.CORBA.Util;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import com.sap.engine.services.jndi.implclient.*;

public class CosNamingContext implements Context {

  static final String DELIM = "/";
  static final String EMPTY_NAME = "<empty>";
  private static final NamingException CLASS_CAST_EXCEPTION = new NamingException("Object must implement org.omg.CORBA.Object");
  private static final NamingException OPERATION_NOT_SUPPORTED_EXCEPTION = new OperationNotSupportedException("This operation is not supported in this version");
  private static final NamingException NOT_CONTEXT_EXCEPTION = new NotContextException();
  static ClientNameParser nameParser = new ClientNameParser();
  private NamingContext ctx;
  private Hashtable env;

  public CosNamingContext(NamingContext namingContext, Hashtable environment) {
    ctx = namingContext;
    env = environment == null ? new Hashtable() : (Hashtable) environment.clone();
  }

  public Object lookup(Name name) throws NamingException {
    try {
      org.omg.CORBA.Object result = ctx.resolve(toCosName(name));
      NamingContext resultContext = null;
      //      try {
      //        resultContext = NamingContextExtHelper.narrow(result);
      //      } catch(Exception e) {
      //      }
      return resultContext == null ? (Object) result : new CosNamingContext(resultContext, env);
    } catch (Exception e) {  //$JL-EXC$
      throw handleException(e);
    }
  }

  public Object lookup(String name) throws NamingException {
    return lookup(getNameParser(name).parse(name));
  }

  public void bind(Name name, Object obj) throws NamingException {
    try {
      if (obj instanceof org.omg.CORBA.Object) {
        ctx.bind(toCosName(name), (org.omg.CORBA.Object) obj);
      } else if (obj instanceof java.rmi.Remote) {
        ctx.bind(toCosName(name), (org.omg.CORBA.Object) Util.getTie( (java.rmi.Remote) obj));
      }
    } catch (Exception e) {//$JL-EXC$
      throw handleException(e);
    }
  }

  public void bind(String name, Object obj) throws NamingException {
    bind(getNameParser(name).parse(name), obj);
  }

  public void rebind(Name name, Object obj) throws NamingException {
    try {
      if (obj instanceof org.omg.CORBA.Object) {
        ctx.rebind(toCosName(name), (org.omg.CORBA.Object) obj);
      } else if (obj instanceof java.rmi.Remote) {
        ctx.rebind(toCosName(name), (org.omg.CORBA.Object) Util.getTie( (java.rmi.Remote) obj));
      }
    } catch (Exception e) {//$JL-EXC$
      throw handleException(e);
    }
  }

  public void rebind(String name, Object obj) throws NamingException {
    rebind(getNameParser(name).parse(name), obj);
  }

  public void unbind(Name name) throws NamingException {
    try {
      ctx.unbind(toCosName(name));
    } catch (Exception e) {//$JL-EXC$
      throw handleException(e);
    }
  }

  public void unbind(String name) throws NamingException {
    unbind(getNameParser(name).parse(name));
  }

  public void rename(Name oldName, Name newName) throws NamingException {
    throw OPERATION_NOT_SUPPORTED_EXCEPTION;
  }

  public void rename(String oldName, String newName) throws NamingException {
    rename(getNameParser(oldName).parse(oldName), getNameParser(newName).parse(newName));
  }

  public NamingEnumeration list(Name name) throws NamingException {
    return list(name, false);
  }

  public NamingEnumeration list(String name) throws NamingException {
    return list(getNameParser(name).parse(name));
  }

  public NamingEnumeration listBindings(Name name) throws NamingException {
    return list(name, true);
  }

  private NamingEnumeration list(Name name, boolean binds) throws NamingException {
    try {
      org.omg.CORBA.Object result = ctx.resolve(toCosName(name));
      NamingContext subContext = null;
      try {
        subContext = NamingContextExtHelper.narrow(result);
      } catch (Exception e) {//$JL-EXC$
        throw NOT_CONTEXT_EXCEPTION;
      }
      BindingListHolder blh = new BindingListHolder();
      BindingIteratorHolder bih = new BindingIteratorHolder();
      subContext.list(0, blh, bih);
      return new NamingEnumerationImpl(bih.value, subContext, binds);
    } catch (Exception e) {//$JL-EXC$
      throw handleException(e);
    }
  }

  public NamingEnumeration listBindings(String name) throws NamingException {
    return listBindings(getNameParser(name).parse(name));
  }

  public void destroySubcontext(Name name) throws NamingException {
    try {
      org.omg.CORBA.Object result = ctx.resolve(toCosName(name));
      NamingContext cosContext = null;
      try {
        cosContext = NamingContextExtHelper.narrow(result);
      } catch (Exception e) {//$JL-EXC$
        throw NOT_CONTEXT_EXCEPTION;
      }
      cosContext.destroy();
    } catch (Exception e) {   //$JL-EXC$
      throw handleException(e);
    }
  }

  public void destroySubcontext(String name) throws NamingException {
    destroySubcontext(getNameParser(name).parse(name));
  }

  public Context createSubcontext(Name name) throws NamingException {
    try {
      return new CosNamingContext(ctx.bind_new_context(toCosName(name)), env);
    } catch (Exception e) {      //$JL-EXC$
      throw handleException(e);
    }
  }

  public Context createSubcontext(String name) throws NamingException {
    return createSubcontext(getNameParser(name).parse(name));
  }

  public Object lookupLink(Name name) throws NamingException {
    return lookup(name);
  }

  public Object lookupLink(String name) throws NamingException {
    return lookupLink(getNameParser(name).parse(name));
  }

  public NameParser getNameParser(Name name) throws NamingException {
    return nameParser;
  }

  public NameParser getNameParser(String name) throws NamingException {
    return nameParser;
  }

  public Name composeName(Name name, Name prefix) throws NamingException {
    return ((Name) prefix.clone()).addAll(name);
  }

  public String composeName(String name, String prefix) throws NamingException {
    return composeName(getNameParser(name).parse(name), getNameParser(prefix).parse(prefix)).toString();
  }

  public Object addToEnvironment(String propName, Object propVal) throws NamingException {
    return env.put(propName, propVal);
  }

  public Object removeFromEnvironment(String propName) throws NamingException {
    return env.remove(propName);
  }

  public Hashtable getEnvironment() throws NamingException {
    return env;
  }

  public void close() throws NamingException {
    env.clear();
  }

  public String getNameInNamespace() throws NamingException {
    throw OPERATION_NOT_SUPPORTED_EXCEPTION;
  }

  static NameComponent[] toCosName(Name name) {
    Enumeration en = name.getAll();
    List<NameComponent> nameComponentList = new ArrayList<NameComponent>();

    while (en.hasMoreElements()) {
      String n = (String) en.nextElement();
      int dotIndex = n.indexOf(".");
      String kind = "";

      if (dotIndex != -1) {
        kind = n.substring(dotIndex + 1);
        n = n.substring(0, dotIndex);
      }

      if (n.equals(EMPTY_NAME)) {
        n = "";
      }
      nameComponentList.add(new NameComponent(n, kind));
    }

    NameComponent[] result = new NameComponent[nameComponentList.size()];
    int c = 0;

    for (Iterator<NameComponent> i = nameComponentList.iterator(); i.hasNext(); c++) {
      result[c] = i.next();
    } 

    return result;
  }

  static String toString(Name name) {
    Enumeration en = name.getAll();
    StringBuffer sb = new StringBuffer();

    while (en.hasMoreElements()) {
      String n = (String) en.nextElement();
      sb.append(n).append(DELIM);
    }

    if (sb.length() > 0) {
      sb.setLength(sb.length() - DELIM.length());
    }
    return sb.toString();
  }

  static NamingException handleException(Exception e) {
    if (e instanceof NamingException) {
      return (NamingException) e;
    }

    NamingException nex = null;
    if (e instanceof NotFound) {
      nex = new NameNotFoundException(e.getLocalizedMessage());
    } else if (e instanceof CannotProceed) {
      nex = new CannotProceedException(e.getLocalizedMessage());
    } else if (e instanceof InvalidName) {
      nex = new InvalidNameException(e.getLocalizedMessage());
    } else if (e instanceof AlreadyBound) {
      nex = new NameAlreadyBoundException(e.getLocalizedMessage());
    } else if (e instanceof ClassCastException) {
      nex = CLASS_CAST_EXCEPTION;
    } else {
      nex = new NamingException(e.getLocalizedMessage());
    }

    nex.setRootCause(e);
    return nex;
  }

}

