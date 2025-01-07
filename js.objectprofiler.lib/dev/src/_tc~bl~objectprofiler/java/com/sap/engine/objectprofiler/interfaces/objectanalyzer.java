package com.sap.engine.objectprofiler.interfaces;

import com.sap.engine.objectprofiler.graph.Graph;
import com.sap.engine.objectprofiler.graph.GraphReport;
import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;

import java.rmi.RemoteException;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * <p/>
 * User: Pavel Bonev
 * Date: 2005-5-3
 * Time: 11:20:33
 */
public interface ObjectAnalyzer {
  public static final String OBJECT_ANALYZER_JNDI_NAME = "ObjectAnalyzer";

  public SessionProperties[] listSessionContexts() throws RemoteException;
  public SessionProperties[] listDomains(String contextName) throws RemoteException;
  public SessionProperties[] listSubdomains(String path) throws RemoteException;
  public SessionProperties[] listSessions(String path) throws RemoteException;
  public Graph getSessionGraph(String path) throws RemoteException;
  public Graph getSessionGraph(String path, int level) throws RemoteException;
  public Graph getSessionGraph(String path, int level, ClassesFilter filter) throws RemoteException;
  public Graph getSessionGraph(String path, int level, ClassesFilter filter, boolean includeTransients, boolean onlyNonshareable) throws RemoteException;

  public GraphReport getReport(String path) throws RemoteException;
}