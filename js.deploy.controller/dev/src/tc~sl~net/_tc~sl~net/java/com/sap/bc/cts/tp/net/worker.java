package com.sap.bc.cts.tp.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

import com.sap.bc.cts.tp.log.Trace;


final class Worker implements Runnable {
  private final static Trace trace = Trace.getTrace(Worker.class);
  
	private Manager manager = null;
	private int number = 0;
	private boolean work = true;

	Worker(Manager _manager, int _number) {
		this.manager = _manager;
		this.number = _number;
	}

	public void run() {
		Connection connection = null;
		Service s = null;
		Socket c = null;
		while (work) {
			connection = this.manager.getNextConnection();
			if (null != connection) {
				// one thread only at a time should be using connection
				if (connection.startUsing()) {
					c = connection.getClient();
					s = connection.getService();
					try {
						if ((connection.getNetComm() != null)
							&& (s instanceof ExtendedServiceIF)) {
							((ExtendedServiceIF) s).serve(connection.getNetComm());
  						trace.debug(
								"Worker #"
									+ this.number
									+ " worked on connection #"
									+ s.getNumber());
						} else {
							InputStream in = c.getInputStream();
							OutputStream out = c.getOutputStream();
							if (null != in && null != out) {
								s.serve(in, out);
								trace.debug(
									"Worker #"
										+ this.number
										+ " worked on connection #"
										+ s.getNumber());
							}
						}
						connection.finishedUsing();
					} catch (InterruptedIOException iioe) {
						// that is OK this connection is simply not sending anything right now
						trace.debug(
							"Worker #"
								+ this.number
								+ " connection #"
								+ s.getNumber()
								+ " kept silent");
						connection.finishedUsing();
					} catch (IOException ioe) {
					  trace.debug(
							"Worker #"
								+ this.number
								+ " connection #"
								+ s.getNumber()
								+ " is broken."
								+ ioe.getMessage());
						StringWriter stringWriter = new StringWriter();
						PrintWriter printWriter = new PrintWriter(stringWriter);
						String backTrace = null;
						if (null != stringWriter) {
							ioe.printStackTrace(printWriter);
							printWriter.close();
							backTrace = stringWriter.toString();
						}
            trace.debug("StackTrace: " + backTrace);
						connection.setClosing();
						connection.finishedUsing();
						this.manager.endConnection();
						// found nothing to do, sleep to reduce CPU-load
						try {
							Thread.sleep(this.manager.getWorkerTimeout());
						} catch (InterruptedException ie) {//$JL-EXC$
              //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
						}
					}

				} else {
    			trace.debug(
    				"Worker #"
    					+ this.number
    					+ " got connection #"
    					+ connection.getService().getNumber()
    					+ " that it in use already");
					// found nothing to do, sleep to reduce CPU-load
					try {
						Thread.sleep(this.manager.getWorkerTimeout());
					} catch (InterruptedException ie) {//$JL-EXC$
            //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
					}
				} // cannot be used
			} else { // if connection
 				trace.debug(
 					"Worker #" + this.number + " got no (null) connection from manager");
				// found nothing to do, sleep to reduce CPU-load
				try {
					Thread.sleep(this.manager.getWorkerTimeout());
				} catch (InterruptedException ie) {//$JL-EXC$
          //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
				}
			}
		} // while
	} // run

	void pleaseStop() {
		trace.debug("Worker #" + this.number + " was asked to stop.");
		this.work = false;
	} // pleaseStop

	public String toString() {
		return new String("number " + this.number);
	}
} // class Worker
