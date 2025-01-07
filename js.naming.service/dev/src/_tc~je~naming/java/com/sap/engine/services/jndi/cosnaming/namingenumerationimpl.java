package com.sap.engine.services.jndi.cosnaming;

import java.util.*;
import javax.naming.*;

import org.omg.CosNaming.*;

public class NamingEnumerationImpl implements NamingEnumeration {

  private static final NoSuchElementException NO_MORE_ELEMENTS = new NoSuchElementException();
  private BindingIterator iterator;
  private BindingHolder holder;
  private boolean hasMore;
  private NamingContext context;
  private boolean bindings; // true if enumerate javax.naming.Binding, false - javax.naming.NameClassPair

  NamingEnumerationImpl(BindingIterator i, NamingContext ctx, boolean binds) {
    iterator = i;
    holder = new BindingHolder();
    hasMore = true;
    bindings = binds;
    context = ctx;
  }

  public Object next() throws NamingException {
    if (!hasMore) {
      close();
      throw NO_MORE_ELEMENTS;
    }

    try {
      hasMore = iterator.next_one(holder);
      org.omg.CosNaming.Binding bind = holder.value;
      String bindingName = NamingContextImpl.toString(bind.binding_name[bind.binding_name.length - 1]);
      Object obj = null;
      try {
        obj = context.resolve(bind.binding_name);
      } catch (org.omg.CosNaming.NamingContextPackage.NotFound nf) {
        // Excluding this catch block from JLIN $JL-EXC$ since there is no need of logging here.
        // Please do not remove this comment!
        obj = null;
      }
      String className = obj == null ? "" : obj.getClass().getName();
      return bindings ? new javax.naming.Binding(bindingName, className, obj, true) : new javax.naming.NameClassPair(bindingName, className, true);
    } catch (Exception e) {  //$JL-EXC$
      throw CosNamingContext.handleException(e);
    }
  }

  public boolean hasMore() throws NamingException {
    return hasMore;
  }

  public Object nextElement() {
    Object result = null;
    try {
      result = next();
    } catch (Exception e) {//$JL-EXC$
      throw NO_MORE_ELEMENTS;
    }
    return result;
  }

  public boolean hasMoreElements() {
    return hasMore;
  }

  public void close() throws NamingException {
    if (iterator != null) {
      iterator.destroy();
    }
    iterator = null;
    holder = null;
    context = null;
  }

}

