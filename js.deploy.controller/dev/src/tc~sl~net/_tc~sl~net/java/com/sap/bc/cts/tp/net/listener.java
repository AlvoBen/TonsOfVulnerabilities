package com.sap.bc.cts.tp.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.sap.bc.cts.tp.log.Logger;
import com.sap.bc.cts.tp.log.Trace;

public class Listener implements Runnable
{
  private final static Trace trace = Trace.getTrace(Listener.class);
  
  private final ServerSocketFactory seSoFactory;
  private final Manager manager;
  private final int port;
  private final ServiceFactory serviceFactory;
  private ServerSocket listenSocket  = null;

  /**
   * Creates a <code>Listener</code> listening on a 
   * <code>java.net.ServerSocket</code> provided by the specified 
   * <code>ServerSocketFactory</code>.
   */
  public Listener(
    Manager manager, 
    int port, 
    ServiceFactory serviceFactory, 
    ServerSocketFactory seSoFactory) {

    this.seSoFactory = seSoFactory;
    this.manager = manager;
    this.port = port;
    this.serviceFactory = serviceFactory;
    try {
      listenSocket = createServerSocket(this.port);
      listenSocket.setSoTimeout(manager.getListenerTimeout());
    } catch (IOException ioe) {
      trace.debug("Could not create ServerSocket", ioe);
    }
  }
  
  /**
   * Creates a <code>Listener</code> listening on the default 
   * <code>java.net.ServerSocket</code> (that is, one created by 
   * <code>new java.net.ServerSocket()</code>.
   */
  public Listener(
    Manager manager,
    int port,
    ServiceFactory serviceFactory) {
    this(manager, port, serviceFactory, null);
  }

  private ServerSocket createServerSocket(int port) throws IOException {
    if (seSoFactory != null) {
      return seSoFactory.create(port);
    } else {
      return new ServerSocket(port);
    }
  }

  public ServerSocket getServerSocket () {
	return this.listenSocket;
  }

  public void run () {
    while (this.manager.getStillRunning()) {
      try {
        Socket client = this.listenSocket.accept();
        client.setSoTimeout(this.manager.getWorkerTimeout());
        
        if (this.serviceFactory instanceof TimeoutControlledServiceFactory) {
          SocketTimeoutViewIF socketTimeoutView = 
            SocketTimeoutView.createSocketTimeoutView(client);
          this.manager.addConnection(
            client,
            ((TimeoutControlledServiceFactory)this.serviceFactory).
              makeService(this.manager, socketTimeoutView) );
        } else {
          this.manager.addConnection(client,this.serviceFactory.makeService(this.manager));
        }
      }
      catch (InterruptedIOException iioe) {//$JL-EXC$
        //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
      }
      catch (IOException ioe) {//$JL-EXC$
        //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
      }
    } // while

    // shutdown
      try {
            listenSocket.close();
      }
      catch (InterruptedIOException iioe) {//$JL-EXC$
        //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
      }
      catch (IOException ioe) {//$JL-EXC$
        //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
      }

    listenSocket= null;
  } // run
} // class Listener
