package com.sap.sdm.is.cs.cmd.client;

/**
 * This handler's method is invoked when error in communication between SDM
 * client and server occurs.
 * 
 * @author Lalo Ivanov
 */
public interface CmdCommErrorHandler {

	/**
	 * This method is called initially from the Communication handler. Listener
	 * can take different actions to notify the user of the Communication
	 * handler of the broken connection.
	 */
	public void onError();

}
