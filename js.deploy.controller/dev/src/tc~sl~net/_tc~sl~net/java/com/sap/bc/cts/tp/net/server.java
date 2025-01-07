package com.sap.bc.cts.tp.net;

import java.io.PrintWriter;
import java.io.IOException;

import com.sap.bc.cts.tp.log.Logger;
import com.sap.bc.cts.tp.log.Trace;
/**
 * Title:        SemaphoreServer
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      SAP AG
 * @author Thomas Brodkorb
 * @version 1.0
 */

public class Server {
  private final static Trace trace = Trace.getTrace(Server.class);
  
	String serverName;
	int workerTimeout;
	int maxCon;
	int maxSer;
	int IadPort;  // Admin port
	int IssPort;  // Serverport
	ManagerInfo mi;
	ServiceFactory sf;

	public Server(String serverName,
	String logonLog,
	String managerLog,
	String connectionLog,
	String servantLog,
	int workerTimeout,
	int maxCon,
	int maxSer,
	int IadPort,
	int IssPort,
	ManagerInfo mi,
	ServiceFactory sf){
    trace.debug("Parameters logonLog, managerLog, connectionLog, servantLog will not be utilized anymore");
		
    this.serverName=serverName;
		this.workerTimeout=workerTimeout;
		this.maxCon=maxCon;
		this.maxSer=maxSer;
		this.IadPort=IadPort;
		this.IssPort=IssPort;
		this.mi=mi;
		this.sf=sf;
	}


	public void start(){
		ThreadGroup tg = new ThreadGroup(serverName);

		Manager m = new Manager(tg,this.maxCon,this.maxSer,this.IadPort,mi);

		m.setWorkerTimeout(this.workerTimeout);

		trace.debug("Starting "+ serverName +" listening on port " + this.IssPort );

		Listener l = new Listener(m,this.IssPort,sf);
		Thread tl = new Thread(tg,l);
		tl.start();
	}


}