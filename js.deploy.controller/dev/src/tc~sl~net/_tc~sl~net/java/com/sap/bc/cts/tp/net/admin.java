package com.sap.bc.cts.tp.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.sap.bc.cts.tp.log.Trace;

final class Admin implements Runnable
{
  private final static Trace trace = Trace.getTrace(Admin.class);
  
	private int port;
	private Manager manager = null;
	private boolean stillRunning = true;
	private ServerSocket listenSocket = null;

	Admin (Manager _manager,int _port) {
		this.port = _port;
		this.manager = _manager;
		try {
			listenSocket = new ServerSocket(this.port);

		}
    catch (IOException ioe) {
      trace.debug("Could not create ServerSocket on port "+this.port+" for Server Administration", ioe);
    }

	} // Admin

	public void run () {
		

 		if (null == this.listenSocket) {
      trace.debug("Server Socket for Administration was not created. Don't run Admin Thread.");
 			return;
 		}
		while (stillRunning) {
			boolean OK = true;
			Socket client = null;
			try {
				client = listenSocket.accept();
			}
			catch (IOException ioe) {
        trace.debug("Error during accept of a new client", ioe);
				continue;
			}
			Service service = new AdminLogik(this.manager);
			InputStream in = null;
			OutputStream out = null;
			try {
				in = client.getInputStream();
				out = client.getOutputStream();
			}
			catch (IOException ioe) {
        trace.debug("Client Socket could not create IO Streams, give it a new try.", ioe);
				OK = false;
			}
			while (OK) {
				try {
					service.serve(in,out);
				}
				catch (InterruptedIOException iioe) {
					OK = false;
				}
				catch (IOException ioe) {
					service.endIt(in,out);
					OK = false;
					break;
				}
				if(false == stillRunning){
					service.endIt(in,out);
					OK = false;
				}
			} // while
			try {
					client.close();

			}
			catch (IOException ioe){
        trace.debug("Error during closing the client socket", ioe);
			}
			client=null;
		} // while

			try {
				listenSocket.close();
			}
			catch (IOException iose){
        trace.debug("Error during closing the ServerSocket", iose);
			}
			listenSocket=null;

	} // run

	public void stopRunning(){
		stillRunning=false;
	}
} // class Admin
