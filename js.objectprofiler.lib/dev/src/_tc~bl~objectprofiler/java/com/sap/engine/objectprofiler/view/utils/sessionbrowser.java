package com.sap.engine.objectprofiler.view.utils;

import com.sap.engine.objectprofiler.interfaces.ObjectAnalyzer;
import com.sap.engine.objectprofiler.interfaces.SessionProperties;
import com.sap.engine.objectprofiler.graph.Graph;
import com.sap.engine.objectprofiler.graph.GraphReport;
import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;

import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Date;
import java.text.SimpleDateFormat;

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
 * Date: 2005-4-28
 * Time: 17:41:46
 */
public class SessionBrowser implements ObjectAnalyzer {

  private ObjectAnalyzer analyzer = null;
  private String[] connectionProps = null;

  public SessionBrowser(String[] args) throws NamingException {
    setConnectionProps(args);
    connect();
  }

  public void setConnectionProps(String[] props) {
    connectionProps = props;
  }

  public void connect() throws NamingException {
    analyzer = null;
    if (connectionProps != null && connectionProps.length > 0) {
      Hashtable env = new Hashtable();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
      if (connectionProps.length == 4) {
        env.put(Context.PROVIDER_URL, connectionProps[0] + ":" + connectionProps[1]);
        env.put(Context.SECURITY_PRINCIPAL, connectionProps[2]);
        env.put(Context.SECURITY_CREDENTIALS, connectionProps[3]);
      } else if (connectionProps.length == 5) {
        if (connectionProps[2] == null || connectionProps[2].trim().equals("")) {
          env.put(Context.PROVIDER_URL, connectionProps[0] + ":" + connectionProps[1]);
        } else {
          env.put(Context.PROVIDER_URL, connectionProps[0] + ":" + connectionProps[1] + "#" + connectionProps[2]);
        }
        env.put(Context.SECURITY_PRINCIPAL, connectionProps[3]);
        env.put(Context.SECURITY_CREDENTIALS, connectionProps[4]);
      }

      InitialContext ctx = new InitialContext(env);
      Object obj = ctx.lookup(OBJECT_ANALYZER_JNDI_NAME);
      //System.out.println(" REMOTE = "+obj.getClass());
      analyzer = (ObjectAnalyzer)obj;
    } else {
      throw new NamingException("Please, correct your connection settings!");
    }
  }

  public GraphReport getReport(String path) throws RemoteException {
    GraphReport res = analyzer.getReport(path);

    return res;
  }

  public SessionProperties[] listSessionContexts() throws RemoteException {
    SessionProperties[] res = analyzer.listSessionContexts();

    return res;
  }

  public SessionProperties[] listDomains(String path) throws RemoteException {
    SessionProperties[] res = analyzer.listDomains(path);

    return res;
  }

  public SessionProperties[] listSubdomains(String path) throws RemoteException {
    SessionProperties[] res = analyzer.listSubdomains(path);

    return res;
  }

  public SessionProperties[] listSessions(String path) throws RemoteException {
    SessionProperties[] res = analyzer.listSessions(path);

    return res;
  }

  public SessionProperties[] listSessionsAndTimestamps(String path) throws RemoteException {
    SessionProperties[] res = analyzer.listSessions(path);

    return res;
  }

  public Graph getSessionGraph(String path) throws RemoteException {
    return analyzer.getSessionGraph(path, -1);
  }

  public Graph getSessionGraph(String path, int level) throws RemoteException {
    return analyzer.getSessionGraph(path, level, null);
  }

  public Graph getSessionGraph(String path, int level, ClassesFilter filter) throws RemoteException {
    return analyzer.getSessionGraph(path, level, null, false, false);
  }

  public Graph getSessionGraph(String path, int level, ClassesFilter filter, boolean includeTransients, boolean onlyNonshareable) throws RemoteException {
    Graph graph = analyzer.getSessionGraph(path, level, filter, includeTransients, onlyNonshareable);

    return graph;
  }

  public DefaultTreeModel buildTreeModel() throws Exception {
    SessionProperties rootInfo = new SessionProperties("J2EE", null, -1);
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootInfo);

    DefaultTreeModel treeModel = new DefaultTreeModel(root);

    SessionProperties[] sessionContexts = listSessionContexts();
    for (int i=0;i<sessionContexts.length;i++) {
      DefaultMutableTreeNode contextNode = new DefaultMutableTreeNode(sessionContexts[i]);
      root.add(contextNode);

      SessionProperties[] domains = listDomains(sessionContexts[i].getPath());
      for (int j=0;j<domains.length;j++) {
        DefaultMutableTreeNode domainNode = new DefaultMutableTreeNode(domains[j]);
        contextNode.add(domainNode);

        traverseSubdomains(domainNode, domains[j]);
      }
    }

    return treeModel;
  }

  private void traverseSubdomains(DefaultMutableTreeNode domainNode, SessionProperties domain) throws Exception {
    SessionProperties[] sessions = listSessionsAndTimestamps(domain.getPath());
    for (int i=0;i<sessions.length;i++) {
      String timeStamp = timestampToString(sessions[i].getTimestamp());
      sessions[i].setDateTime(timeStamp);
      domainNode.add(new DefaultMutableTreeNode(sessions[i]));
    }

    SessionProperties[] subdomains = listSubdomains(domain.getPath());
    for (int i=0;i<subdomains.length;i++) {
      DefaultMutableTreeNode subdomainNode = new DefaultMutableTreeNode(subdomains[i]);
      domainNode.add(subdomainNode);
      traverseSubdomains(subdomainNode, subdomains[i]);
    }
  }


  private String timestampToString(long time) {
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

    return format.format(new Date(time));
  }
}
