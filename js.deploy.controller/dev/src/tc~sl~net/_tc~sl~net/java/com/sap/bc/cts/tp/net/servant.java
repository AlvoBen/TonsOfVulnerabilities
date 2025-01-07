package com.sap.bc.cts.tp.net;

public class Servant
{
	private Thread thread = null;
	private Worker worker = null;
	
	public Servant (Thread _thread,Worker _worker) {
		this.thread = _thread;
		this.worker = _worker;
	} // Servant
	
	public Thread getThread () {
		return this.thread;
	} // getThread
	
	public Worker getWorker () {
		return this.worker;
	} // getWorker
	
	public void stop() {
		this.worker.pleaseStop();
		try {
		Thread.sleep(5000);
		}
		catch (InterruptedException ie) {//$JL-EXC$
      //Author of the class initially left them empty. This is just a cleanup of prio 1 JLin messages. Feel free to fix them properly when possible. 
		}
		if (this.thread.isAlive()) 
			this.thread.stop();
	}
	
	public String toString() {
		return new String(this.thread.toString() + " / " + this.worker.toString());	
	} //toString
	
} // class Servant
