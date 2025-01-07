/**
 * Date: 2004-6-8
 * @author Nikolai Angelov
 **/
package com.sap.engine.mejb;

import com.sap.engine.mejb.notification.ListenerConnectionServer;
import com.sap.engine.mejb.notification.ListenerConnectionServerImpl;
import com.sap.engine.mejb.notification.ListenerRegistrationImpl;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.management.*;
import javax.management.j2ee.ListenerRegistration;
import javax.naming.NamingException;

import java.rmi.RemoteException;
import java.util.Set;

/**
 * @ejbHome <{javax.management.j2ee.ManagementHome}>
 * @ejbRemote <{javax.management.j2ee.Management}>
 * @stateless 
 */
public class MEJBBean implements SessionBean {

  private transient SessionContext ctx;
  protected transient ManagementProxy managementProxy;
  private transient ListenerConnectionServer listenerConnector = null;
  //	private Location location = Location.getLocation(MEJBBean.class);

  public MEJBBean() {
    managementProxy = null;
  }

  public void ejbActivate() {
  }

  public void ejbCreate() throws CreateException {
    try {
      managementProxy = ManagementProxy.getManagementProxy();
    } catch (NamingException e) {
      throw new CreateException(e.toString());
    }
  }

  public void ejbPassivate() {
  }

  public void ejbRemove() {
  }

  public void setSessionContext(SessionContext context) {
    ctx = context;
  }

  public String getDefaultDomain() throws RemoteException {
    return managementProxy.getDefaultDomain();
  }

  public Integer getMBeanCount() throws RemoteException {
    return managementProxy.getMBeanCount();
  }

  public boolean isRegistered(ObjectName name) throws RemoteException {
    return managementProxy.isRegistered(name);
  }

  public Set queryNames(ObjectName name, QueryExp query)
    throws RemoteException {
    return managementProxy.queryNames(name, query);
  }

  public ListenerRegistration getListenerRegistry() throws RemoteException {
    ListenerConnectionServer remoteListenerConnector = getListenerConnector();
    return new ListenerRegistrationImpl(remoteListenerConnector);
  }

  public MBeanInfo getMBeanInfo(ObjectName name)
    throws
      InstanceNotFoundException,
      IntrospectionException,
      ReflectionException,
      RemoteException {
    return managementProxy.getMBeanInfo(name);
  }

  public void setAttribute(ObjectName name, Attribute attribute)
    throws
      InstanceNotFoundException,
      AttributeNotFoundException,
      InvalidAttributeValueException,
      MBeanException,
      ReflectionException,
      RemoteException {
    managementProxy.setAttribute(name, attribute);
  }

  public Object getAttribute(ObjectName name, String attribute)
    throws
      MBeanException,
      AttributeNotFoundException,
      InstanceNotFoundException,
      ReflectionException,
      RemoteException {
    return managementProxy.getAttribute(name, attribute);
  }

  public AttributeList getAttributes(ObjectName name, String attributes[])
    throws InstanceNotFoundException, ReflectionException, RemoteException {
    return managementProxy.getAttributes(name, attributes);
  }

  public AttributeList setAttributes(ObjectName name, AttributeList attributes)
    throws InstanceNotFoundException, ReflectionException, RemoteException {
    return managementProxy.setAttributes(name, attributes);
  }

  public Object invoke(
    ObjectName name,
    String operationName,
    Object params[],
    String signature[])
    throws
      InstanceNotFoundException,
      MBeanException,
      ReflectionException,
      RemoteException {
    return managementProxy.invoke(name, operationName, params, signature);
  }

  protected ListenerConnectionServer getListenerConnector() {
    //		synchronized (managementProxy) { //todo:synchronized?
    if (listenerConnector == null) {
      listenerConnector = new ListenerConnectionServerImpl(managementProxy);
    }
    //		}
    return listenerConnector;
  }

}
