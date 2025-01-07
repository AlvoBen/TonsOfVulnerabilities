package com.sap.sdm.api.remote;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * An entry point for a client of the SDM remote client API.
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management Tools</a> - Martin Stahl
 * @version 1.0
 * @deprecated The SDM API is deprecated. From now on the <code>Deploy Controller API</code>
 * has to be used. The current type is replaced by <code>com.sap.engine.services.dc.api.ClientFactory</code>.
 */
public class ClientSessionFactory {

  /* class name of implementation of APIClientSessionFactory. */
  final private static String IMPLCLASSNAME =
    "com.sap.sdm.apiimpl.remote.client.APIClientSessionFactoryImpl";
  final private static String IMPLMETHODNAME = 
    "createAPIClientSession";
  final private static String IMPLMETHODNAMEIDE = 
    "createAPIClientSessionIDE";
  final private static Class[] IMPLPARAMARRNEW = { int.class, 
                                                   String.class, 
                                                  int.class, 
                                                  String.class,
                                                  String.class, 
                                                  int.class };
  
  final private static Class[] IMPL_PARAM_ARR = { String.class,
																							    int.class, 
																							    String.class,
																							    int.class,
																							    String.class,
																							    String.class, 
																							    String.class,
																							    int.class };  

  private static final String DEFAULT_CLIENT_HOST        = "0.0.0.0";
  private static final int    DEFAULT_CLIENT_PORT        = 0;    
  
  final private static int API_CLIENT_VERSION = 13;

  private static int loginTimeout = 30000; 
  
  /* IM           : Version 11 introduces ServerSideError */
  /* LI 16.02.2006: Version 12 introduces the following enhancement - user is
   *                able now to specify the SDM Client host to be used in
   *                socket connections to the SDM server side
   * IM 23.10.2006: Version 13 introduces a new vertioning strategy: 
   *                UPDATE_LOWER_OR_CHANGED_VERSIONS_ONLY                
   */                
  private static Class clientSessionFactoryInstance;
  
  private  static synchronized Class getClientSessionFactoryInstance() {
    if (clientSessionFactoryInstance == null) {
      clientSessionFactoryInstance = createFactory();
    }
    return clientSessionFactoryInstance;
  }
  
  private static Class createFactory() {
    try {
      final Class classFactory = Class.forName(IMPLCLASSNAME);
      return classFactory.newInstance().getClass();
    } catch (Exception e) {
      final String errMsg = "An error occurred while creating an instance of "
          + "class ClientSessionFactory! \r\n" + e.getMessage();
      throw new RuntimeException(errMsg);
    }
  }

  public synchronized static void registerImpl(Class clientFactory) {
    clientSessionFactoryInstance = clientFactory;
  } 
    
  /**
   * Creates a client session on the specified SDM server.
   * This method is deprecated with API client version 3 because there is a new
   * method with an additional third parameter for the password.
   * To make sure you are able to connect to the SDM server you have to use 
   * the new method since this method does work only as long as the password
   * of the SDM server was not changed after installation.
   * 
   * @param port the port on which the SDM server is listening
   * @param host the host name of the SDM server
   * @return a <code>ClientSession</code> representing the client session
   * @throws RemoteException if no session on the specified SDM server
   *          can be established
   * @throws AuthenticationException 
   * @throws UnsupportedProtocolException 
   * @throws RuntimeException if the SDM remote client lib is not properly 
   *          installed
   * @deprecated
   */
  public static ClientSession createRemoteClientSession(int port, String host) 
    throws RemoteException, UnsupportedProtocolException, AuthenticationException
  {
    return createRemoteClientSession(DEFAULT_CLIENT_PORT, DEFAULT_CLIENT_HOST, port, host, null);
  }
  
  /**
   * Creates a client session on the specified SDM server. 
   * Replaces the old deprecated version of this method without the password
   * parameter. Due to security issues only this new method should be used to
   * create a client session. However the old method still works as long as 
   * the password for the SDM server was not changed after installation.
   * In contrast to
   * {@link com.sap.sdm.api.remote.ClientSessionFactory#createRemoteClientSessionIDE(int, String, String)}
   * this method throws a {@link com.sap.sdm.api.remote.RemoteException}
   * when the login failes due to a wrong password.
   * 
   * This method is new with API client version 3.
   * 
   * @param port the port on which the SDM server is listening
   * @param host the host name of the SDM server
   * @param password the password of the SDM server
   * @return a <code>ClientSession</code> representing the client session
   * @throws RemoteException if no session on the specified SDM server
   *          can be established
   * @throws AuthenticationException 
   * @throws UnsupportedProtocolException 
   * @throws RuntimeException if the SDM remote client lib is not properly 
   *          installed
   */
  static public ClientSession createRemoteClientSession(
      int port, 
      String host,
      String password ) 
      throws RemoteException, UnsupportedProtocolException, AuthenticationException
  {
    return createRemoteClientSession(DEFAULT_CLIENT_PORT, DEFAULT_CLIENT_HOST, port, host, password);
    } 
  
  /**
   * Creates a client session on the specified SDM server. 
   * Replaces the old deprecated version of this method without the password
   * parameter. Due to security issues only this new method should be used to
   * create a client session. However the old method still works as long as 
   * the password for the SDM server was not changed after installation.
   * In contrast to
   * {@link com.sap.sdm.api.remote.ClientSessionFactory#createRemoteClientSessionIDE(int, int, String, String)}
   * this method throws a {@link com.sap.sdm.api.remote.RemoteException}
   * when the login failes due to a wrong password.
   * 
   * This method is new with API client version 10.
   * 
   * @param localPort the local port(port on client side)
   * @param port the port on which the SDM server is listening
   * @param host the host name of the SDM server
   * @param password the password of the SDM server
   * @return a <code>ClientSession</code> representing the client session
   * @throws RemoteException if no session on the specified SDM server
   *          can be established
   * @throws AuthenticationException 
   * @throws UnsupportedProtocolException 
   * @throws RuntimeException if the SDM remote client lib is not properly 
   *          installed
   */
  public static ClientSession createRemoteClientSession(
      int localPort,
      int port, 
      String host,
      String password ) 
      throws RemoteException, UnsupportedProtocolException, AuthenticationException {
    return createRemoteClientSession(localPort, DEFAULT_CLIENT_HOST, port, host, password);  
  }

  public static ClientSession createRemoteClientSession(
    int localPort,
    String localHost,      
    int port, 
    String host,
    String password) 
    throws UnsupportedProtocolException, 
           AuthenticationException, 
           RemoteException {

     ClientSession result = null;
     //Class factoryClass = null;
     Class factoryClass = getClientSessionFactoryInstance();
  
    checkHost(localHost);
    checkHost(host);
    checkPort(localPort);
    checkPort(port);
       
//     try {
//       factoryClass = Class.forName(IMPLCLASSNAME);
//     } catch (ClassNotFoundException e1) {
//       throw new RuntimeException("Cannot load Class " + IMPLCLASSNAME + 
//                                  " - " + e1.getMessage());
//     }
     Method factoryMethod = null;
     try {
       factoryMethod = factoryClass.getMethod(IMPLMETHODNAME, IMPLPARAMARRNEW);
     } catch (NoSuchMethodException e2) {
       throw new RuntimeException("Cannot find Method " + IMPLMETHODNAME + 
                                  " of Class " + IMPLCLASSNAME + " - " + 
                                  e2.getMessage());
     }
     try {
       result = (ClientSession) factoryMethod.invoke( null, 
           new Object[] { 
              new Integer(localPort),
              localHost,
               new Integer(port), 
               host, 
               password, 
               new Integer(loginTimeout) } );
     } catch (IllegalAccessException e3) {
       throw new RuntimeException("Exception occurred while calling " + 
                                  IMPLMETHODNAME + " of Class " + IMPLCLASSNAME +
                                  " - " + e3.getMessage());
        
     } catch (InvocationTargetException e4) {
       if (e4.getTargetException() instanceof RemoteException) {
         throw (RemoteException)e4.getTargetException();
       } else if (e4.getTargetException() instanceof UnsupportedProtocolException) {
         throw (UnsupportedProtocolException) e4.getTargetException();
       } else if (e4.getTargetException() instanceof AuthenticationException) {
         throw (AuthenticationException) e4.getTargetException();
       } else {
         if (e4.getTargetException() != null) {
           throw new RuntimeException("Exception occurred while calling " + 
             IMPLMETHODNAME + " of Class " + 
             IMPLCLASSNAME + " - " + 
             e4.getTargetException().getClass().getName() + 
             ": " + e4.getTargetException().getMessage() );
         } else {
           throw new RuntimeException("Exception occurred while calling " + 
               IMPLMETHODNAME + " of Class " + IMPLCLASSNAME +
               " - " + e4.getMessage());
         }
       }
     }
     return result;
 
  }
  
  public static ClientSession createRemoteClientSession(
      String protocol,
      int port, 
      String host,
      String user,
      String password) 
      throws UnsupportedProtocolException, 
             AuthenticationException, 
             RemoteException {
  	return createRemoteClientSession(protocol, DEFAULT_CLIENT_PORT, DEFAULT_CLIENT_HOST, port, host, user, password);
  }
  
  public static ClientSession createRemoteClientSession(
  		String protocol, 
  		int localPort, 
  		int port, 
  		String host, 
  		String user, 
  		String password)
  throws UnsupportedProtocolException, AuthenticationException, RemoteException {  	
  	return createRemoteClientSession(protocol, localPort, DEFAULT_CLIENT_HOST, port, host, user, password);
  }
  
  public static ClientSession createRemoteClientSession(
    String protocol,
    int localPort,
    String localHost,      
    int port, 
    String host,
    String user,
    String password) 
    throws UnsupportedProtocolException, 
           AuthenticationException, 
           RemoteException {

     ClientSession result = null;
     //Class factoryClass = null;
     Class factoryClass = getClientSessionFactoryInstance();
  
    checkHost(localHost);
    checkHost(host);
    checkPort(localPort);
    checkPort(port);
       
//     try {
//       factoryClass = Class.forName(IMPLCLASSNAME);
//     } catch (ClassNotFoundException e1) {
//       throw new RuntimeException("Cannot load Class " + IMPLCLASSNAME + 
//                                  " - " + e1.getMessage());
//     }
     Method factoryMethod = null;
     try {
       factoryMethod = factoryClass.getMethod(IMPLMETHODNAME, IMPL_PARAM_ARR);
     } catch (NoSuchMethodException e2) {
       throw new RuntimeException("Cannot find Method " + IMPLMETHODNAME + 
                                  " of Class " + IMPLCLASSNAME + " - " + 
                                  e2.getMessage());
     }
     try {
       result = (ClientSession) factoryMethod.invoke( null, 
           new Object[] { 
              protocol,
              new Integer(localPort),
              localHost,
               new Integer(port), 
               host, 
               user, 
               password, 
               new Integer(loginTimeout) } );
     } catch (IllegalAccessException e3) {
       throw new RuntimeException("Exception occurred while calling " + 
                                  IMPLMETHODNAME + " of Class " + IMPLCLASSNAME +
                                  " - " + e3.getMessage());
        
     } catch (InvocationTargetException e4) {
       if (e4.getTargetException() instanceof RemoteException) {
         throw (RemoteException)e4.getTargetException();
       } else if (e4.getTargetException() instanceof UnsupportedProtocolException) {
         throw (UnsupportedProtocolException) e4.getTargetException();
       } else if (e4.getTargetException() instanceof AuthenticationException) {
         throw (AuthenticationException) e4.getTargetException();
       } else {
         if (e4.getTargetException() != null) {
           throw new RuntimeException("Exception occurred while calling " + 
             IMPLMETHODNAME + " of Class " + 
             IMPLCLASSNAME + " - " + 
             e4.getTargetException().getClass().getName() + 
             ": " + e4.getTargetException().getMessage() );
         } else {
           throw new RuntimeException("Exception occurred while calling " + 
               IMPLMETHODNAME + " of Class " + IMPLCLASSNAME +
               " - " + e4.getMessage());
         }
       }
     }
     return result;
 
  }
 
  /**
   * Creates a client session on the specified SDM server. 
   * Replaces the old deprecated version of this method without the password
   * parameter. Due to security issues only this new method should be used to
   * create a client session. However the old method still works as long as 
   * the password for the SDM server was not changed after installation.
   * In contrast to
   * {@link com.sap.sdm.api.remote.ClientSessionFactory#createRemoteClientSession(int, String, String)}
   * this method throws a {@link com.sap.sdm.api.remote.WrongPasswordException}
   * when the login failes due to a wrong password.
   * With that exception it should be easier to distinguish between 
   * communication problems and password problems in case of failing login.
   * 
   * This method is new with API client version 3.
   * 
   * @param port the port on which the SDM server is listening
   * @param host the host name of the SDM server
   * @param password the password of the SDM server
   * @return a <code>ClientSession</code> representing the client session
   * @throws RemoteException if no session on the specified SDM server
   *          can be established
   * @throws WrongPasswordException if the wrong password was supplied
   * @throws RuntimeException if the SDM remote client lib is not properly 
   *          installed
   */
  static public ClientSession createRemoteClientSessionIDE(
	  int port, 
	  String host,
	  String password ) 
	  throws RemoteException, WrongPasswordException
  {
    return createRemoteClientSessionIDE(DEFAULT_CLIENT_PORT, DEFAULT_CLIENT_HOST, port, host, password);		
  }  

  /**
   * Creates a client session on the specified SDM server. 
   * Replaces the old deprecated version of this method without the password
   * parameter. Due to security issues only this new method should be used to
   * create a client session. However the old method still works as long as 
   * the password for the SDM server was not changed after installation.
   * In contrast to
   * {@link com.sap.sdm.api.remote.ClientSessionFactory#createRemoteClientSession(int, String, String)}
   * this method throws a {@link com.sap.sdm.api.remote.WrongPasswordException}
   * when the login failes due to a wrong password.
   * With that exception it should be easier to distinguish between 
   * communication problems and password problems in case of failing login.
   * 
   * This method is new with API client version 10.
   * 
   * @param localPort the local port(port on client side) 
   * @param port the port on which the SDM server is listening
   * @param host the host name of the SDM server
   * @param password the password of the SDM server
   * @return a <code>ClientSession</code> representing the client session
   * @throws RemoteException if no session on the specified SDM server
   *          can be established
   * @throws WrongPasswordException if the wrong password was supplied
   * @throws RuntimeException if the SDM remote client lib is not properly 
   *          installed
   */
  static public ClientSession createRemoteClientSessionIDE(
      int localPort,
      int port, 
      String host,
      String password ) 
      throws RemoteException, WrongPasswordException
  {
    return createRemoteClientSessionIDE(localPort, DEFAULT_CLIENT_HOST, port, host, password);
  }  
  /**
   * Creates a client session on the specified SDM server. 
   * Replaces the old deprecated version of this method without the password
   * parameter. Due to security issues only this new method should be used to
   * create a client session. However the old method still works as long as 
   * the password for the SDM server was not changed after installation.
   * In contrast to
   * {@link com.sap.sdm.api.remote.ClientSessionFactory#createRemoteClientSession(int, String, String)}
   * this method throws a {@link com.sap.sdm.api.remote.WrongPasswordException}
   * when the login failes due to a wrong password.
   * With that exception it should be easier to distinguish between 
   * communication problems and password problems in case of failing login.
   * 
   * This method is new with API client version 12.
   * 
   * @param localPort the local port(port on client side)
   * @param localHost the local host to be used(host of the client side).
   *                  Could be either hostname or IP string.
   * @param port the port on which the SDM server is listening
   * @param host the host name of the SDM server
   * @param password the password of the SDM server
   * @return a <code>ClientSession</code> representing the client session
   * @throws RemoteException if no session on the specified SDM server
   *          can be established
   * @throws WrongPasswordException if the wrong password was supplied
   * @throws RuntimeException if the SDM remote client lib is not properly 
   *          installed
   */
   public static ClientSession createRemoteClientSessionIDE(
  	  int localPort,
      String localHost,
      int port, 
      String host,
      String password ) 
      throws RemoteException, WrongPasswordException
  {
    ClientSession result = null;
//    Class factoryClass = null;
    Class factoryClass = getClientSessionFactoryInstance();
    
    checkHost(localHost);
    checkHost(host);
               
//    try {
//      factoryClass = Class.forName(IMPLCLASSNAME);
//    } catch (ClassNotFoundException e1) {
//      throw new RuntimeException("Cannot load Class " + IMPLCLASSNAME + 
//                                 " - " + e1.getMessage());
//    }
    Method factoryMethod = null;
    try {
      factoryMethod = factoryClass.getMethod(IMPLMETHODNAMEIDE, IMPLPARAMARRNEW);
    } catch (NoSuchMethodException e2) {
      throw new RuntimeException("Cannot find Method " + IMPLMETHODNAMEIDE + 
                                 " of Class " + IMPLCLASSNAME + " - " + 
                                 e2.getMessage());
    }
    try {
      result = (ClientSession) factoryMethod.invoke( null, 
          new Object[] { 
              new Integer(localPort),
              localHost,
              new Integer(port), 
              host, 
              password, 
              new Integer(loginTimeout) } );
    } catch (IllegalAccessException e3) {
      throw new RuntimeException("Exception occurred while calling " + 
                                 IMPLMETHODNAME + " of Class " + IMPLCLASSNAME +
                                 " - " + e3.getMessage());
                
    } catch (InvocationTargetException e4) {
      if (e4.getTargetException() instanceof RemoteException) {
        throw (RemoteException)e4.getTargetException();
      } else if (e4.getTargetException() instanceof WrongPasswordException) {
        throw (WrongPasswordException)e4.getTargetException();
      } else {
        if (e4.getTargetException() != null) {
          throw new RuntimeException("Exception occurred while calling " + 
            IMPLMETHODNAME + " of Class " + 
            IMPLCLASSNAME + " - " + 
            e4.getTargetException().getClass().getName() + 
            ": " + e4.getTargetException().getMessage() );
        } else {
          throw new RuntimeException("Exception occurred while calling " + 
              IMPLMETHODNAME + " of Class " + IMPLCLASSNAME +
              " - " + e4.getMessage());
        }
      }
    }
    return result;
  }

  /**
   * Returns the version information for the SDMClient.jar in use.
   * You should compare this version number with the one returned
   * by {@link com.sap.sdm.api.remote.Client#getAPIServerVersion}
   * to check whether this client is of the same version as the server.
   * 
   * @return an <code>int</code> containing the version number
   */
  static public int getAPIClientVersion() {
    return API_CLIENT_VERSION;
  }

  
  /**
   * Returns the timeout in ms which is used for the initial 
   * login to the SDM server.
   * Any additional calls of methods of the Client API are
   * processed without a timeout.
   * 
   * @return an <code>int</code> containing the timeout in ms
   */
  static public int getLoginTimeout() {
    return loginTimeout;
  }

  
  /**
   * Sets the timeout in ms which is used for the initial login
   * to the SDM server.
   * The default timeout is 30000 ms.
   * This method has to be called prior to 
   * {@link com.sap.sdm.api.remote.ClientSessionFactory#createRemoteClientSession(int, String, String)}
   * to change the timeout used for the initial login.
   * if <code>newTimeout</code> &lt; 0 then the timeout is set to zero i.e.
   * no timeout.
   *  
   * @param newTimeout the timeout in ms
   */ 
  static public void setLoginTimeout(int newTimeout) {
    
    if (newTimeout < 0) {
      newTimeout = 0;
    }
    
    loginTimeout = newTimeout;
    
  }
    
  
  
  private static void checkHost(String host) {
    if (host == null) {
      throw new NullPointerException("Cannot create a remote ClientSession with host \"null\"");
    }
  }

  private static void checkPort(int port) {
    if ((port < 0) || (port > 65536)) {
      throw new IllegalArgumentException("Cannot create a remote ClientSession with port \"" + port + "\""); 
    }    
  }
      
}
