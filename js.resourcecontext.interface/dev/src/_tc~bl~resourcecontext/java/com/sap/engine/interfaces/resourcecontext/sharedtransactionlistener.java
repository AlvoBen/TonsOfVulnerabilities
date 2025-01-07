package com.sap.engine.interfaces.resourcecontext;


public interface SharedTransactionListener {

	public void transactionStarted();
	
	public void close();
}
