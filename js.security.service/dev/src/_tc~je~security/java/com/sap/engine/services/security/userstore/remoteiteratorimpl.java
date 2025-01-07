package com.sap.engine.services.security.userstore;

import java.util.Iterator;
import com.sap.engine.services.security.remote.RemoteIterator;
import com.sap.engine.interfaces.security.userstore.context.SearchResult;

public class RemoteIteratorImpl extends javax.rmi.PortableRemoteObject implements RemoteIterator {

  private Iterator iterator = null;

  public RemoteIteratorImpl(Iterator iterator) throws java.rmi.RemoteException {
    this.iterator = iterator;
  }

  public boolean hasNext() {
    return iterator.hasNext();
  }

  public Object next() {
    return iterator.next();
  }

  public void remove() {
    iterator.remove();
  }

  public int getState() {
    if (iterator instanceof SearchResult) {
      return ((SearchResult) iterator).getState();
    }
    return SearchResult.SEARCH_RESULT_UNDEFINED;
  }

}

