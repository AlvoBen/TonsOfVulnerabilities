package com.sap.bc.cts.tp.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

import com.sap.bc.cts.tp.log.Logger;
import com.sap.bc.cts.tp.log.Trace;

public class Manager implements Runnable
{
  private final static Logger log = Logger.getLogger();
  private final static Trace trace = Trace.getTrace(Manager.class);
  
  private Admin  admin             = null;
  private int    admin_port        = 0;
  private Thread admin_engine      = null;
  private final String admin_port_s     = new String("AdminPort");
  private boolean stillRunning = true; // stop the Manger
  private boolean listen= true;

  private int maxConnections  = 10;
  private int maxServants     = 10;
  private final String maxConnections_s = new String("MaxConnections");
  private final String maxServants_s    = new String("MaxServants");

  private ManagerInfo managerInfo = null;

  private ThreadGroup group  = null;
  private Thread      engine = null;

  private Vector connections = null;
  private Vector servants    = null;

  private int currentConnection = 0;
  private int currentServant    = 0;

  // this number should increase with every thread that needs to be created
  // if a thread dies and a replacement is created the number of active threads remains constant
  // but this number should still be increased
  private int worker = 0;

  // this number should increase with every connection that needs to be created
  // This number should uniquely identify the connection
  private int connection = 0;

  private int numberOfServants = 0;


  private final String workerTimeout_s = new String("workerTimeout");
  private int workerTimeout = 1000;
  private int listenerTimeout = 30000;


  // The constructor is private, so nobody can call it
  public Manager (ThreadGroup _group,
                  int _maxConnections,
                  int _maxServants,
                  int _admin_port,
                  ManagerInfo _managerInfo) {
    this.maxConnections = _maxConnections;
    this.connections = new Vector(this.maxConnections,this.maxConnections);

    this.maxServants = _maxServants;
    this.servants    = new Vector(this.maxServants);

    this.group = _group;

    this.managerInfo = _managerInfo;

    this.admin_port = _admin_port;
    this.startAdmin();


    this.engine = new Thread(this.group,this,"Manager");
    this.engine.setDaemon(true);

    this.engine.start();

  } // Manager


    /**
     * The basic/central method that runs all the time in a separate thread
     * It basically sleeps (waits) until it is interrupted Then it check all its
     * Connections and Servants
     */
  public void run() {
    tellProperty(this.maxConnections_s,new String(""+this.maxConnections));
    tellProperty(this.maxServants_s,new String(""+this.maxServants));
    tellProperty(this.workerTimeout_s,new String(""+this.workerTimeout));

    while (stillRunning) { // infinite loop

      this.check();

      this.printServants();
      this.printConnections();

      try {
        synchronized(this) { this.wait(); }
      }
      catch(InterruptedException e) {//$JL-EXC$
        //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
      }
    } // while

/*    try {
        this.engine.sleep(getListenerTimeout()); //wait for Listener
    }
    catch(InterruptedException e) {}
for future use */
    stopConnections();
    stopServants();
    this.admin.stopRunning();
    //Commented for JLin reasons
    //System.exit(-1);
    //--
  } //run

  public void stopRunning(){
    stillRunning = false;
    this.group.interrupt();
  }

  public boolean getStillRunning(){
    return(stillRunning);
  }

    /**
     * Starts the simple and very basic Administrator service in a separate Thread
     */
  private void startAdmin () {
    trace.debug("Starting Admin Server listening on port " + this.admin_port);

    ThreadGroup tg = new ThreadGroup("Admin");
    this.admin = new Admin(this,this.admin_port);

    this.admin_engine = new Thread(tg,this.admin,"Admin");
    this.admin_engine.setDaemon(true);

    this.admin_engine.start();
    /*
     * Allow the admin Thread to really start.
     */
    Thread.yield();
  } // startAdmin

    /**
     * Start a new Servant (i.e. a Thread with a Worker)
     */
  private Servant startServant () {
    this.worker++;
    Worker wr = new Worker(this,this.worker);
    Thread tn = new Thread(this.group,wr,"Worker #" + worker);

    tn.start();
    return new Servant(tn,wr);
  } // startServant

    /**
     * Check all the Connections and Servants
     * Restart a new Servant in case less than the allowed number of Servants is working
     */
  public synchronized void check() {

    for (int i=this.connections.size()-1 ; i>=0 ;i--) {
      Connection c = (Connection)this.connections.elementAt(i);
      // check Connection c
      // is there anything I can check on the socket?
      if (null != c) {
        // one thread only at a time should be using connection
      ////  if (c.startUsing()) { check also Connections in use
           Socket s = c.getClient();
           if (null==s || c.isClosed()) {
              this.removeConnection(c);
           }
          else {
          // Update Tables
             tellNewConnection(c);
          }
       //   c.finishedUsing();
       // }
      } // connectio is null, ignore
    } // for connections
    for (int i = 0 ; i < this.servants.size(); i++) {
      Servant sv = (Servant)this.servants.elementAt(i);
      Thread t = sv.getThread();
      if (!t.isAlive()) {
        // the thread died, create a new one
        Servant nsv = this.startServant();
        this.servants.setElementAt(nsv,i);
        tellDelServant(sv);
        tellNewServant(nsv);
      } else {
        tellNewServant(sv);
      }
    } // for threads

    if (!this.admin_engine.isAlive())
      this.startAdmin();

    if (null != this.managerInfo)
      this.managerInfo.check();

  } // check


    /**
     * store a new Connection or refuse it when alread the maximum number of connections is reached
     */
   synchronized void addConnection (Socket _socket,Service _service) {
    if(null == _socket){
      trace.debug("Connection was nor created because socket was 'null' ");
    }
    else{
      try{
        InetAddress inetAddress = _socket.getInetAddress();
        int remotePort = _socket.getPort();
        String hostName;
        try {
          hostName = _socket.getInetAddress().getHostName();
        } catch (SecurityException e) {
          // not sure how likely it is that this exception will be thrown;
          // however, do not disturb the networking because of the failed lookup
          trace.debug(
            "Unable to lookup host name for address " + inetAddress, e);
          hostName = "<unable to lookup due to security restrictions>";
        }
        
        log.info("Opened client connection to " + hostName + 
          " (IP address " + inetAddress + ", remote port " + remotePort + ")");

        Connection l_conn = null;
        if(_service instanceof ExtendedServiceIF){
          SocketTimeoutViewIF socketTimeoutView = 
            SocketTimeoutView.createSocketTimeoutView(_socket);
          NetComm nc = new NetComm(
            _socket.getInputStream(),
            _socket.getOutputStream(),
            socketTimeoutView);
          l_conn = new Connection(_socket,_service,nc);
        }else{
          l_conn = new Connection(_socket,_service);
        }

        this.connection++;
        _service.setNumber(this.connection);

        trace.debug("Connection #"+this.connection+" created and added to administrative structures");
       //add connection as late as possibel to the vector, first do all inits
        connections.addElement(l_conn);
        tellNewConnection(l_conn);

        // check if a new thread needs to be started
        if (this.numberOfServants < this.maxServants) {
          Servant sv = this.startServant();
          this.numberOfServants++;
          this.servants.addElement(sv);
          tellNewServant(sv);
        } // if numberOfThreads
      /* } */
      } catch (IOException ioEx ){
        trace.debug("Connection was nor created because IO-Error occurred", ioEx);
      }
    }
    this.notify();

  } // addConnection

    /**
     * Remove a Connection from the list of stored connections
     * But kindly ask the connection to end itself before closing
     * the socket and removing the stored information
     */
  synchronized void removeConnection (Connection _connection) {
    trace.debug("Removing Connection " + _connection.toString());
    _connection.startUsing(); // notify that nobody should work on this connection any more
    Socket s = _connection.getClient();
    Service service = _connection.getService();
    service.endIt(null,null);
    tellDelConnection(_connection);
    this.connections.removeElement(_connection);
    trace.debug("Connection " + _connection.toString() + " removed.");

		// do this dangerous close effort as last thing
    try {
    	s.getInputStream().close();
    	s.getOutputStream().close();
    } catch (IOException ioe) {//$JL-EXC$
      //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
    }
    try {
      log.info("Close client connection to " + s.getInetAddress() 
        + ", remote port " + s.getPort()); 
      s.close();
    } catch (IOException ioe) {//$JL-EXC$
      //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
    }
  } // removeConnection


    /**
     * This method doesn't really actively end a Connection
     * It rather wakes up the "run" method to check all its Connections
     */
  public synchronized void endConnection() {
    this.notify();
  } // endConnection


    /**
     * returns the Connection with the given number
     * returns null if the number is out of bounds
     */
  public synchronized Connection getConnection(int num) {
    if (0 <= num && num < this.connections.size())
      return (Connection)this.connections.elementAt(num);
    else
      return null;
  } // getConnection

    /**
     * this can be used by the Worker Threads to get the next work item (a connection)
     * it always returns a Connection except when no Connection is available at all (stored)
     */
  public synchronized Connection getNextConnection() {
    Connection rc = null;
    if ( 0 == connections.size()) {
      rc = null;
      currentConnection  = 0;
      return rc;
      // log("getNextConnection: null");
    } 
    
    if (currentConnection >= connections.size()) {
       currentConnection  = 0;
    } 

    rc =(Connection)connections.elementAt(currentConnection);
    currentConnection++;

    return rc;
  } // getNextConnection

    /**
     * Print information about all stored Connections to a PrintWriter
     */
  public synchronized void printConnections() {
    Connection t_conn = null;
    Socket c = null;
    Service s = null;

    for (int i=0; i<this.connections.size(); i++) {
      t_conn = (Connection)this.connections.elementAt(i);
      c = t_conn.getClient();
      s = t_conn.getService();
      trace.debug(Timestamp.get() + ": " + "Connection #" + i + "  Client: " + c.toString());
      trace.debug(Timestamp.get() + ": " + "Connection #" + i + " Service: " + s.toString());
    } // for

  } // printConnections

    /**
     * Print information about all stored Servants to a PrintWriter
     */
  public synchronized void printServants() {
    Thread tn = null;
    Worker wr = null;
    Servant sv = null;
    for (int i=0; i< this.servants.size(); i++) {
      sv = (Servant)this.servants.elementAt(i);
      tn = sv.getThread();
      wr = sv.getWorker();
      trace.debug(Timestamp.get() + ": " + "Thread #" + i + ":" + tn.toString());
      trace.debug(Timestamp.get() + ": " + "Worker #" + i + ":" + wr.toString());
    }
  } // printServants


  /* tells the outside world that there is a new connection
  * or that a connection has been updated
  */
  private void tellNewConnection(Connection _connection) {
    String res = null;
    if (null != this.managerInfo) {
      trace.debug("tellNewConnection: " + _connection.toString());
      res = this.managerInfo.setConnection(_connection.getClient().getInetAddress().getHostName(),
                                         Integer.toString(_connection.getClient().getPort()),
                                         Integer.toString(_connection.getClient().getLocalPort()),
                                         (Integer.toString(_connection.getService().getNumber()) +
                                         " " + _connection.getService().toString()));
        
      trace.debug("tellNewConnection: " + res);
    } else {
      trace.debug("tellNewConnectio: null == this.managerInfo");
    }
  } // tellNewConnection



  private void tellDelConnection(Connection _connection) {
    if (null != this.managerInfo) {

      trace.debug("tellDelConnection: " + _connection.toString());
      trace.debug("tellDelConnection: " +
          this.managerInfo.delConnection(_connection.getClient().getInetAddress().getHostName(),
                                         Integer.toString(_connection.getClient().getPort())));
    } else {
      trace.debug("tellDelConnection: null == this.managerInfo");
    }
  } // tellDelConnection


  /* tells the outside world that there is a new servant
  * or that a servant has been updated
  */
  private void tellNewServant(Servant _servant) {
    if (null != this.managerInfo) {
      int number = this.servants.indexOf(_servant);
      trace.debug("tellNewServant: " + number + " " + _servant.toString ());
      trace.debug("tellNewServant: " +this.managerInfo.setServant(_servant.toString()));
    } else {
      trace.debug("tellNewServant: null == this.managerInfo");
    }
  } // tellNewServant



  private void tellDelServant(Servant _servant) {
    if (null != this.managerInfo) {
      trace.debug("tellDelServant: " + _servant.toString());
      trace.debug("tellDelServant: " + this.managerInfo.delServant(_servant.toString()));
    } else {
      trace.debug("tellDelServant: null == this.managerInfo");
    }
  } // tellDelServant



  private void tellProperty(String _name,String _value) {
    if (null != this.managerInfo) {
      String value = null;
      if (null == _value)
        value = "null";
      else
        value = _value;
      trace.debug("tellProperty: " + _name + " " + value);
      trace.debug("tellProperty: " + this.managerInfo.setProperty(_name,value));
    } else {
      trace.debug("tellProperty: null == this.managerInfo");
    }
  } // tellProperty


  public int getMaxConnections () {
    return this.maxConnections;
  } // getMaxConnections

  public int getMaxServants () {
    return this.maxServants;
  } // getMaxServants

  public synchronized Servant getNextServant() {
    if ( 0 == this.servants.size())
      return null;
    // wrap around "currentConnection"
    this.currentServant++;
    if (this.currentServant >= this.servants.size())
      this.currentServant = 0;

    return (Servant)this.servants.elementAt(this.currentServant);
  }

  public int getNumberOfServants () {
    return this.servants.size();
  } // getNumberOfThreads

  public int getNumberOfConnections () {
    return this.connections.size();
  } // getNumberOfConnections

  public int getListenerTimeout(){
    return listenerTimeout;
  }

  public int getWorkerTimeout () {
    return this.workerTimeout;
  } // getWorkerTimeout

  public void setMaxConnections ( int _maxConnections) {
    this.maxConnections = _maxConnections;
    tellProperty(this.maxConnections_s,new String(""+this.maxConnections));
  } // setMaxConnections

  public void setMaxServants (int _maxServants) {
    Servant sv = null;
    while (_maxServants < this.numberOfServants) {
      sv = this.getNextServant();
      if (null != sv) {
        sv.stop();
        tellDelServant(sv);
        this.servants.removeElement(sv);
        this.numberOfServants--;
      } else {
        break;
      }
    } // while
    this.maxServants = _maxServants;
    tellProperty(this.maxServants_s,new String(""+this.maxServants));
  } // setMaxServants

  public void setWorkerTimeout ( int _workerTimeout) {
    this.workerTimeout = _workerTimeout;
    this.tellProperty(this.workerTimeout_s,new String(""+this.workerTimeout));
  } // setWorkerTimeout

  public synchronized boolean tooManyConnections () {
    if (this.connections.size() > this.maxConnections) {
      return true;
    } else {
      return false;
    }
  } // tooManyConnections

  public void stopServants () {
    Servant sv = null;
    for (int i=this.servants.size()-1 ; i>= 0; i--) {
      sv = (Servant)this.servants.elementAt(i);
      sv.stop();
      this.servants.removeElement(sv);
    }
  } // stopServant

  public synchronized void stopConnections () {
    // shutdown hard all the connections, ignore other threads working on them
    Connection con = null;
    long startTime = System.currentTimeMillis();
    long runTime   = 0;
    while ((connections.size() > 0) && (runTime < 25000)) {
      for (int i=this.connections.size()-1 ; i>= 0; i--) {
        con = (Connection)this.connections.elementAt(i);
        if (null != con) {
          if (con.startUsing())
            removeConnection(con);
        }
      }
      runTime = System.currentTimeMillis() - startTime;
    }
  } // stopServant

} // class Manager
