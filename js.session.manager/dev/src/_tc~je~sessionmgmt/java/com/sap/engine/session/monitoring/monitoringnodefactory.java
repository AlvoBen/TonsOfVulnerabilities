package com.sap.engine.session.monitoring;

import com.sap.engine.core.Names;
import com.sap.engine.session.SessionDomain;
import com.sap.engine.session.SessionContextFactory;
import com.sap.engine.session.SessionContext;
import com.sap.engine.session.exec.ClientContextImpl;
import com.sap.engine.session.monitoring.impl.DatashareMonitoringNode;
import com.sap.engine.session.monitoring.impl.AbstractMonitoringNode;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * User: Pavel Bonev, Mladen Droshev
 * Date: 2006-8-11
 */
public class MonitoringNodeFactory {
  public static final String HTTP_CONTEXT_ID = "HTTP_Session_Context";
  public static final String EJB_CONTEXT =  "/Service/EJB";
  public static final String USER_CONTEXT = "User_Context";

  private static Location loc = Location.getLocation(MonitoringNodeFactory.class.getName(), Names.KERNEL_DC_NAME, Names.SESSION_MANAGER_CSN_COMPONENT);

  /**
   * This method creates MonitoringNode instance corresponding to
   * the HTTP Session Domain with the given name
   *
   * @return a MonitoringNode instance or null if a domain with that name doesn't exist
   * @throws WrongPathException if the input param is wrong
   */
  public static MonitoringNode createHTTPContextNode() throws WrongPathException {
    return createMonitoringNodeForPath(HTTP_CONTEXT_ID);
  }

  public static MonitoringNode createEJBContextNode() throws WrongPathException {
    return createMonitoringNodeForPath(EJB_CONTEXT);
  }

  /**
   * This method creates MonitoringNode instance corresponding to
   * the HTTP Session Domain with the given name
   *
   * @param domainName - the name of the HTTP session domain
   *
   * @return a MonitoringNode instance or null if a domain with that name doesn't exist
   * @throws WrongPathException if the input param is wrong
   */
  public static MonitoringNode createMonitoringNode(String domainName) throws WrongPathException {
    return createMonitoringNodeForPath(HTTP_CONTEXT_ID + SessionDomain.SEPARATOR + domainName);
  }

  public static MonitoringNode[] getUserContextMonitoringNodes() {
    Collection c = ClientContextImpl.clientContexts();
    ArrayList<MonitoringNode> result = new ArrayList<MonitoringNode>();
    for (Object o : c) {
      if(o instanceof MonitoredObject){
        result.add(((MonitoredObject)o).getMonitoredObject());
      }
    }
    return result.toArray(new MonitoringNode[0]);
  }

  public static MonitoringNode[] createDatashareMonitoringNodes() {
    MonitoringNode[] datas = new DatashareMonitoringNode[2];
    datas[0] = new DatashareMonitoringNode(DatashareMonitoringNode.HASHTABLE_TYPE);
    datas[1] = new DatashareMonitoringNode(DatashareMonitoringNode.QUEUE_TYPE);
    return datas;
  }

  public static MonitoringNode[] getMonitoringNodes(String path)throws WrongPathException{
    if(path == null || path.length() == 0){
      MonitoringNode[] nodes = new MonitoringNode[3];
      nodes[0] = createHTTPContextNode();
      nodes[1] = createEJBContextNode();
      nodes[2] = new AbstractMonitoringNode(path){


        public Map<String, MonitoringNode> getChildNodes() {
          Map<String, MonitoringNode> result = new HashMap<String, MonitoringNode>();
          MonitoringNode[] r = getUserContextMonitoringNodes();
          if(r != null && r.length > 0){
            for(MonitoringNode node: r){
              result.put(node.toString(), node);
            }
          } else {
            return null;
          }
          return result;
        }

        public String getID(){
          return USER_CONTEXT;
        }

        public MonitoringNode getChildNode(String id) {
          return null;
        }

        public String toString(){
          return "RootUserContext";
        }
      };
      return nodes;
    } else if(path.startsWith(USER_CONTEXT)){
      if(path.equalsIgnoreCase(USER_CONTEXT)){
        return getUserContextMonitoringNodes();
      }
      return null;
    } else {
      MonitoringNode parent = createMonitoringNodeForPath(path);
      if(parent != null){
        Collection<MonitoringNode> n = parent.getChildNodes().values();
        ArrayList<MonitoringNode> nodes = new ArrayList<MonitoringNode>();
        for(MonitoringNode node: n){
          nodes.add(node);
        }
        return nodes.toArray(new MonitoringNode[0]);
      }
      return null;
    }
  }

  /**
   * This method creates a MonitoringNode for a given path to the session context,
   * session domain or session.
   * If empty string or null is provided as parameter it returns
   * MonitoringNode instance corresponding to the HTTP Session context
   *
   * @param path - path to the desired node. Path delimiter is SessionDomain.SEPARATOR symbol
   *
   * @return a monitorin node instance or null if path does not lead to a node
   * @throws WrongPathException if the input param is wrong
   */
  public static MonitoringNode createMonitoringNodeForPath(String path) throws WrongPathException {
    String contextName;
    String domainPath = null;

    if (loc.beInfo()) {
      String msg = "createMonitoringNodeForPath() is called with param = '" + path + "'";
      loc.logT(Severity.INFO, msg);
    }

    int i = path.indexOf(SessionDomain.SEPARATOR);

    if (i > -1) {
      contextName = path.substring(0, i);
      domainPath = path.substring(i + 1);
    } else {
      contextName = path;
    }

    if (loc.beInfo()) {
      String msg = "createMonitoringNodeForPath(): context = '" + contextName + "'; domain path='" + domainPath + "'";
      loc.logT(Severity.INFO, msg);
    }

    SessionContextFactory sessionContextFactory = SessionContextFactory.getInstance();
    SessionContext context = sessionContextFactory.getSessionContext(contextName, false);

    if (context == null) { // wrong context name
      throw new WrongPathException("Path '" + path + "' has a context name '" + contextName + "' that doesn't exist!");
    }

    if (domainPath == null || domainPath.equals("")) { // context case
      return ((MonitoredObject)context).getMonitoredObject();
    }

    SessionDomain domain = context.findSessionDomain(domainPath);
    if (domain == null) { // options: domain-session case or wrong domain name case
      i = domainPath.lastIndexOf(SessionDomain.SEPARATOR);

      if (i > -1) {
        String sessionName = domainPath.substring(i + 1);
        domainPath = domainPath.substring(0, i);

        if (loc.beInfo()) {
          String msg = "createMonitoringNodeForPath(): domainPath = '" + domainPath + "'; session ID ='" + sessionName + "'";
          loc.logT(Severity.INFO, msg);
        }

        domain = context.findSessionDomain(domainPath);
        if (domain != null) {  // try domain-session case

          if (domain.containsSession(sessionName)) { // domain-session case
            if (sessionName.length() > 0 && sessionName.charAt(sessionName.length() - 1) == SessionDomain.SEPARATOR) {
              sessionName = sessionName.substring(0, sessionName.length() - 1);
            }
            return domain.getMonitoredObject().getChildNode(sessionName);


          } else { // wrong session ID
            throw new WrongPathException("Path '" + path + "' has a session ID '" + sessionName + "' that doesn't exists!");
          }

        } else { // wrong domain-session path
          throw new WrongPathException("Path '" + path + "' has a domain path '" + domainPath + "' with a sessionID '" + sessionName + "' that doesn't exists!");
        }

      } else { // wrong domain path
        throw new WrongPathException("Path '" + path + "' has a domain path '" + domainPath + "' that doesn't exists!");
      }
    } else {// domain case
      if (domainPath.length() > 0 && domainPath.charAt(domainPath.length() - 1) == SessionDomain.SEPARATOR) {
        domainPath = domainPath.substring(0, domainPath.length() - 1);
      }
      return domain.getMonitoredObject();
    }
  }

}
