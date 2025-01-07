package com.sap.engine.services.jndi.cosnaming;

import java.util.*;

import org.omg.CosNaming.*;

public class BindingIteratorImpl extends _BindingIteratorImplBase implements BindingIteratorOperations {

  private static final Binding EMPTY_BINDING = new Binding(new NameComponent[0], BindingType.nobject);
  private Iterator iterator; //$JL-SER$
  private int index;
  private int fullSize;
  static final long serialVersionUID = 5368952935822420948L;

  BindingIteratorImpl(List bindings) {
    iterator = bindings.iterator();
    index = 0;
    fullSize = bindings.size();
  }

  public boolean next_one(BindingHolder bindingHolder) {
    bindingHolder.value = iterator == null ? EMPTY_BINDING : get();
    return iterator != null;
  }

  public boolean next_n(int how_many, BindingListHolder bindingListHolder) {
    int size = Math.min(how_many, fullSize - index);
    Binding[] bindings = new Binding[size];

    for (int c = 0; c < size; c++) {
      bindings[c] = get();
    }

    bindingListHolder.value = bindings;
    return iterator != null;
  }

  private Binding get() {
    Binding binding = (Binding) iterator.next();
    if (++index >= fullSize) {
      iterator = null;
    }
    return binding;
  }

  public void destroy() {
    iterator = null;
  }

}

