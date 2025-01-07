/**
 * QueueConnectionFactory.java
 *
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2002.
 * All rights reserved.
 */
package com.sap.jms.client.connection;

import javax.jms.JMSException;
import javax.jms.QueueConnection;

import com.sap.jms.client.connection.Connection.ConnectionType;

/**
 * @author Margarit Kirov
 * @version 1.0
 */
public class QueueConnectionFactory extends ConnectionFactory implements javax.jms.QueueConnectionFactory {


	static final long serialVersionUID = -5706810782152253529L;

  /**
   * Constructor QueueConnectionFactory.
   * @param hosts names of the hosts of the dispatchers
   * @param ports the dispatcher ports listening for JMS requests
   * @param userName user name for authentication purposes
   * @param password password password for authentication purposes
   * @param initialPoolSize property specifying the initial size of the client's thread pool
   * @param maxPoolSize property specifying the maximum size of the client's thread pool
   */
  public QueueConnectionFactory(String[] hosts, int[] ports, String serverInstance, String userName, String password, int initialPoolSize, int maxPoolSize, String systemID, String hardwareID) {
    super(hosts, ports, serverInstance, userName, password, initialPoolSize, maxPoolSize, systemID, hardwareID);
  }
  
  /**
   * Constructor QueueConnectionFactory.
   * @param hosts names of the hosts of the dispatchers
   * @param ports the dispatcher ports listening for JMS requests
   * @param userName user name for authentication purposes
   * @param password password password for authentication purposes
   */
  public QueueConnectionFactory(String[] hosts, int[] ports, String userName, String password, String systemID, String hardwareID) {
    super(hosts, ports, userName, password, systemID, hardwareID);
  }

  /**
   * Constructor QueueConnectionFactory.
   * @param hosts names of the hosts of the dispatchers
   * @param ports the dispatcher ports listening for JMS requests
   * @param userName user name for authentication purposes
   * @param password password password for authentication purposes
   */
  public QueueConnectionFactory(String[] hosts, int[] ports, String serverInstance, String userName, String password, String systemID, String hardwareID) {
    super(hosts, ports, serverInstance, userName, password, systemID, hardwareID);
  }

  /**
   * Constructor QueueConnectionFactory.
   * @param hosts names of the hosts of the dispatchers
   * @param ports the dispatcher ports listening for JMS requests
   */
  public QueueConnectionFactory(String[] hosts, int[] ports, String systemID, String hardwareID) {
    super(hosts, ports, systemID, hardwareID);
  } 
  
  /* (non-Javadoc)
   * @see javax.jms.QueueConnectionFactory#createQueueConnection()
   */
  public QueueConnection createQueueConnection() throws JMSException {
    return (QueueConnection) createConnection(ConnectionType.QUEUE_CONNECTION);
  }

  /* (non-Javadoc)
   * @see javax.jms.QueueConnectionFactory#createQueueConnection(String, String)
   */
  public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
      setIsPasswordFieldBringsPassword(true);
      return (QueueConnection) createConnection(userName, password, ConnectionType.QUEUE_CONNECTION);
  }

}
