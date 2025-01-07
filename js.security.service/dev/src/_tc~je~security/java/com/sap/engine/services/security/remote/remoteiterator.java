package com.sap.engine.services.security.remote;

import com.sap.engine.interfaces.security.userstore.context.SearchResult;

public interface RemoteIterator
  extends java.util.Iterator, java.rmi.Remote, SearchResult {

}

