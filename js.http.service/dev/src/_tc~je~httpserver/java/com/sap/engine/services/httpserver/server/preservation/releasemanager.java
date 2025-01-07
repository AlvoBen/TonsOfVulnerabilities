package com.sap.engine.services.httpserver.server.preservation;

import com.sap.engine.lib.lang.ObjectPool;
import com.sap.engine.services.httpserver.server.Client;

public class ReleaseManager{

	//client pool
	ObjectPool pool;
	
	public ReleaseManager(ObjectPool clientsPool) {
		this.pool = clientsPool;
	}

	public void returnOldInPool(Client client) {
		pool.returnInPool(client);
	}
	
	public void returnNewInPool(){
		pool.returnInPool(new Client());
	}

}
